#!/bin/sh

DIR=`dirname $0`
java -cp $DIR/:$DIR/commons-lang-2.4.jar:$DIR/commons-collections-3.2.1.jar:$DIR/oro-2.0.8.jar:$DIR/velocity-1.5.jar:$DIR/xwiki-plugin-velocidoc-0.1-SNAPSHOT.jar net.sourceforge.velocidoc.Velocidoc $*
