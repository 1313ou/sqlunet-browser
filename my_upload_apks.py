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

"""Uploads apk to alpha track and updates its listing properties."""

import argparse
import sys
from apiclient import sample_tools
from oauth2client import client

TRACK = 'alpha'  # Can be 'alpha', beta', 'production' or 'rollout'
RECENT_CHANGES=u'Fixes and enhancements'

# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument('package_name',
                       help='The package name.')
argparser.add_argument('release_name',
                       help='The release name.')
argparser.add_argument('recent_changes',
                       help='The recent changes.')
argparser.add_argument('apk_files',
                       nargs='*',
                       help='The path to the APK files to upload.')

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
    apk_files = flags.apk_files
    release_name=flags.release_name
    recent_changes=flags.recent_changes

    try:
        edit_request = service.edits().insert(body={}, packageName=package_name)
        result = edit_request.execute()
        edit_id = result['id']
    
        versionCodes=[]
        for apk_file in apk_files:
            print 'Uploading %s' % apk_file
            apk_response = service.edits().apks().upload(
                editId=edit_id,
                packageName=package_name,
                media_body=apk_file).execute()
            versionCodes.append(apk_response['versionCode'])
            print 'Version code %d has been uploaded' % apk_response['versionCode']
    
        track_response = service.edits().tracks().update(
            editId=edit_id,
            track=TRACK,
            packageName=package_name,
            body={u'releases': [{
                u'name': release_name,
                u'versionCodes': versionCodes,
                u'releaseNotes': [
                #    {u'recentChanges': recent_changes},
                ],
                u'status': u'completed',
            }]}).execute()
    
        print 'Track %s is set with releases: %s' % (
            track_response['track'], str(track_response['releases']))
    
        commit_request = service.edits().commit(
            editId=edit_id, packageName=package_name).execute()
    
        print 'Edit "%s" has been committed' % (commit_request['id'])

    except client.AccessTokenRefreshError:
        print ('The credentials have been revoked or expired, please re-run the '
           'application to re-authorize')
    
if __name__ == '__main__':
    main(sys.argv)
