#!/bin/bash
REPO_SERVER=$1
TMP=/tmp/mab.$$
/bin/rm -rf $TMP

lein deploy file://$TMP
ssh -p 880 $REPO_SERVER 'sudo chmod -R g+w,g+s /var/www/jars/'
rsync -a -e 'ssh -p 880' $TMP/ $REPO_SERVER:/var/www/jars/

/bin/rm -rf $TMP
