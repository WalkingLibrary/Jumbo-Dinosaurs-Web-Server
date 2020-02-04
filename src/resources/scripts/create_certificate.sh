#!/bin/bash
# Script to Create a ssl certificate with CertBot
# "$1" = Path to web root/ domains root
# "$2" = Domain
# "$3" = email to register with
sudo certbot certonly --webroot -w "$1" -d "$2" --email "$3" --agree-tos