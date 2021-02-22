#!/usr/bin/python
#
# Copyright 2014 Google Inc. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the 'License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
from googleapiclient.http import MediaFileUpload

"""Uploads apk to alpha track and updates its listing properties."""

import argparse
import sys
from apiclient import sample_tools
from oauth2client import client

DEOBFUSCATION_MIME_TYPE = "application/octet-stream"
    
# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument('package_name',
                       help='The package name.')
argparser.add_argument('--apk_version_code','-n',
                       action='append',
                       type=int,
                       help='The apk base version code.')
argparser.add_argument('deobfuscation_files',
                       nargs='+',
                       help='The path to the deobfuscation files to upload.')

def test(argv):
    # Authenticate and construct service.
    flags = argparser.parse_args()

    # Process flags and read their values.
    package_name = flags.package_name
    deobfuscation_files = flags.deobfuscation_files
    apk_version_codes = flags.apk_version_code
    print 'PACKAGE %s' % package_name
    print 'CODES %s' % apk_version_codes
    print 'FILES %s' % deobfuscation_files

    for deobfuscation_file in deobfuscation_files:
        print 'Uploading %s' % deobfuscation_file
        for code in apk_version_codes: 
            print "packageName=%s apkVersionCode=%s deobfuscationFile=%s" % (package_name, code, deobfuscation_file)

def main(argv):
    # Authenticate and construct service.
    service, flags = sample_tools.init(
        argv,
        'androidpublisher',
        'v3',
        __doc__,
        __file__, parents=[argparser],
        scope='https://www.googleapis.com/auth/androidpublisher')

    # Process flags and read their values.
    package_name = flags.package_name
    deobfuscation_files = flags.deobfuscation_files
    apk_version_codes = flags.apk_version_code
    print 'PACKAGE %s' % package_name

    try:
        # get edit id
        edit_request = service.edits().insert(body={}, packageName=package_name)
        result = edit_request.execute()
        edit_id = result['id']

        # upload
        for deobfuscation_file in deobfuscation_files:
            media = MediaFileUpload(deobfuscation_file, mimetype=DEOBFUSCATION_MIME_TYPE)
            for code in apk_version_codes: 
                print 'Uploading %s with code %d' % (deobfuscation_file, code)
                deob_response = service.edits().deobfuscationfiles().upload(
                    editId=edit_id,
                    packageName=package_name,
                    apkVersionCode=code,
                    deobfuscationFileType='proguard',
                    media_body=media).execute()
                print 'DeobfuscationFile %s has been uploaded' % deob_response['deobfuscationFile']

        # commit
        commit_request = service.edits().commit(
            editId=edit_id, packageName=package_name).execute()
        print 'Edit "%s" has been committed' % (commit_request['id'])
 
    except client.AccessTokenRefreshError:
        print ('The credentials have been revoked or expired, please re-run the application to re-authorize')

if __name__ == '__main__':
    main(sys.argv)
