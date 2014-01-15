#!/bin/sh
/appserv/build/mvn.sh clean install -Pmysql
rm web/target/curriki-web-1.0-SNAPSHOT/WEB-INF/lib/plexus-container-default-1.0-alpha-30.jar
rm wiki/src/main/resources/package.xml
cd wiki
/appserv/build/mvn.sh clean install
cd ..
