#!/bin/bash
# Script to Create a ssl certificate with CertBot
# "$1" = path of the file to move
# "$2" = path of the dir you'll be moving to
# "$3" = name of the file
fileToRemove="$2/$3"
rm "$fileToRemove"
mv "$1" "$2"
