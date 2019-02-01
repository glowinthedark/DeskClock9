#!/usr/bin/env python3
import dropbox
import sys
import glob
import os
from dropbox.exceptions import ApiError
from dropbox.files import WriteMode


class DropboxUploader:
    def __init__(self):
        self.dbx = dropbox.Dropbox("i3dIC6phgQcAAAAAAAAH05be1WNRmPJUlbxSUQl4d3tx6fNmgMq41JRaspK2vG9j")
        print('linked account: ', self.dbx.users_get_current_account())

    def upload_file(self, local_file, remote_dir):
        with open(local_file, 'rb') as f:
            # We use WriteMode=overwrite to make sure that the settings in the file
            # are changed on upload
            remote_path = os.path.join(remote_dir, os.path.basename(local_file))
            print("Uploading " + local_file + " to Dropbox as " + remote_path + "...")
            try:
                response_metadata = self.dbx.files_upload(f.read(), remote_path, mode=WriteMode('overwrite'))
                print(response_metadata)
            except ApiError as err:
                # This checks for the specific error where a user doesn't have
                # enough Dropbox space quota to upload this file
                if (err.error.is_path() and
                        err.error.get_path().reason.is_insufficient_space()):
                    sys.exit("ERROR: Cannot back up; insufficient space.")
                elif err.user_message_text:
                    print(err.user_message_text)
                    sys.exit()
                else:
                    print(err)
                    sys.exit()


if __name__ == '__main__':
    if len(sys.argv) == 1:
        print("Usage {} <local_file> <remote_dir>".format(os.path.basename(sys.argv[0])))
        exit(-1)
    src = sys.argv[1]
    dst = sys.argv[2]
    client = DropboxUploader()
    for fname in glob.glob(src):
        client.upload_file(fname, dst)

# f = open('working-draft.txt', 'rb')
# response = client.put_file('/magnum-opus.txt', f)
# print('uploaded: ', response)
