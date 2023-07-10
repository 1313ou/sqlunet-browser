#!/usr/bin/python2
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

# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument('--verbose', action='store_const', const=True, help='Verbose.')
argparser.add_argument('package_name', help='The package name.')

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
    verbose = flags.verbose
    package_name = flags.package_name
    print ('PACKAGE %s' % package_name)

    try:
        
        # get edit id
        edit_request = service.edits().insert(body={}, packageName=package_name)
        result = edit_request.execute()
        edit_id = result['id']

        if verbose:
            # get apks
            try:
                apks_result = service.edits().apks().list(
                    editId=edit_id,
                    packageName=package_name
                ).execute()

                # iterate on apks
                print ('APKs')
                for apk in apks_result['apks']:
                    #print apk
                    print ('versionCode: %s, binary.sha1: %s' % (
                        apk['versionCode'], 
                        apk['binary']['sha1']
                    ))
            except:
                pass

            # get bundles
            bundles_result = service.edits().bundles().list(
                editId=edit_id,
                packageName=package_name
            ).execute()

            # iterate on bundles
            print ('BUNDLES')
            for bundle in bundles_result['bundles']:
                #print bundle
                print ( 'versionCode: %s, sha1: %s' % (
                    bundle['versionCode'],
                    bundle['sha1'],
                ))

        # get tracks
        tracks_result = service.edits().tracks().list(
            editId=edit_id,
            packageName=package_name
        ).execute()

        # iterate on tracks
        print ('TRACKS')
        for track in tracks_result['tracks']:
            #print ( track)
            print( '%12s' % (
                track['track'])
            ),
            # iterate on track releases
            for release in track['releases']:
                print ( ' %8s, %10s, versionCodes: %s' % (
                    release['name'], 
                    release['status'], 
                    map(str,release['versionCodes'])
              ))

    except client.AccessTokenRefreshError:
        print ('The credentials have been revoked or expired, please re-run the application to re-authorize')
    
if __name__ == '__main__':
    main(sys.argv)
