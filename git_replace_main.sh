#!/bin/bash

main=main
old=appcompat
dev=material3searchview

# Rename local main to old, create main from dev, and push everything
git branch -m $main $old && \
git checkout -b $main $dev && \
git push origin $main $old && \
git push origin --delete $main

# Set upstream for the new main branch
git branch --set-upstream-to=origin/$main $main

# Clean up old remote tracking references
git remote prune origin


## Rename the old main branch to old (locally and remotely)
#git branch -m $main $old          # Rename main to old locally
#git push -u origin $old           # Push old to remote
#
## Push the dev branch as the new main
#git push -u origin $dev:$main     # Push dev to remote as main
#
## Update local repository
#git fetch origin                  # Fetch all branches
#git branch -u origin/$main $main  # Set upstream for local main
#
## (Optional) Delete the old remote main branch
#git push origin --delete $main    # Delete old main from remote
#
## all collaborators should run:
#git fetch --all --prune
#git checkout $main

