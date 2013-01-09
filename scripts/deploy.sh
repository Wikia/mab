#!/bin/bash
lein2 deploy file:///tmp/mab
rsync -a -e 'ssh -p 880' /tmp/mab/* ops002.huddler.com:/var/www/jars/
