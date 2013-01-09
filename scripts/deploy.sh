#!/bin/bash
TMP=/tmp/mab.$$
/bin/rm -rf $TMP

lein2 deploy file://$TMP
rsync -a -e 'ssh -p 880' /tmp/mab/* ops002.huddler.com:/var/www/jars/

/bin/rm -rf $TMP
