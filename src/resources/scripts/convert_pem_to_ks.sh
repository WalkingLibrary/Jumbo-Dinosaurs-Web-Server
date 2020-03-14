#!/bin/bash
# Shell Script to Convert LetsEncrypt .pem Certificates to .ks Certificates
#
# "$1" = Domain Name
# "$2" = Certificate Password
#
sudo rm /etc/letsencrypt/live/"$1"/"$1".ks
cat /etc/letsencrypt/live/"$1"/*.pem > /etc/letsencrypt/live/"$1"/fullcert.pem
openssl pkcs12 -export -out /etc/letsencrypt/live/"$1"/fullcert.pkcs12 -in /etc/letsencrypt/live/"$1"/fullcert.pem -passout pass:"$2"
keytool -v -importkeystore -srckeystore /etc/letsencrypt/live/"$1"/fullcert.pkcs12 -srcstorepass "$2" -destkeystore /etc/letsencrypt/live/"$1"/"$1".ks -deststoretype JKS -deststorepass "$2"
