#!/bin/bash

modFiles=$(git status | grep 'modified')  #Get files that had been modified
delFiles=$(git status | grep 'deleted')   #Get files that had been deleted
echo modifed: `echo $modFiles | sed 's/modified//'` deleted: `echo $delFiles | sed 's/deleted//'`  #echo files

nFiles=$( echo $modFiles $delFiles | wc -l )  #Count number of files

if [ $nFiles -eq 0 ]
then
  echo Nothing to commit
  exit 1
fi

git add .
read -p 'Commit message: ' msg
git commit -m "$msg" && \
git push
