#!/bin/bash
# Script to renew a domain's SSL Certificate useing certbot
# $1 is the domain you want to update
# $2 is the path to the GET dir of the specified domain
sudo certbot certonly --force-renewal -d $1 -a webroot -w $2

