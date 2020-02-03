#!/bin/bash
echo "starting script"
# Shell Script to Convert .pem Certificates to .ks
#
#
if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ]; then
     echo "Not Enough Args"
else
    echo "Args Given:\n1. \"$1\"\n2. \"$2\"\n3. "$3""
    echo "executing"
    cat /etc/letsencrypt/live/"$1"/*.pem > /etc/letsencrypt/live/"$1"/fullcert.pem
    openssl pkcs12 -export -out /etc/letsencrypt/live/"$1"/fullcert.pkcs12 -in /etc/letsencrypt/live/"$1"/fullcert.pem -passout pass:"$3"
    keytool -v -importkeystore -srckeystore /etc/letsencrypt/live/"$1"/fullcert.pkcs12 -srcstorepass "$3" -destkeystore /etc/letsencrypt/live/"$1"/"$1".ks -deststoretype JKS -deststorepass "$3"
    today=$(date +"%m-%d-%y")
    echo "$today"
    mkdir "$2"/OldCertificates/"$today"
    cp "$2"/Certificates/"$1".ks "$2"/OldCertificates/"$today"
    rm "$2"/Certificates/"$1".ks
    cp /etc/letsencrypt/live/"$1"/"$1".ks "$2"/Certificatesfiecho "done"
fi