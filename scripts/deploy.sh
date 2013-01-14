#!/bin/bash
TMP=/tmp/mab.$$
/bin/rm -rf $TMP

lein2 deploy file://$TMP
ssh -p 880 ops002.huddler.com 'sudo chmod -R g+w,g+s /var/www/jars/'
rsync -a -e 'ssh -p 880' $TMP/ ops002.huddler.com:/var/www/jars/

/bin/rm -rf $TMP
