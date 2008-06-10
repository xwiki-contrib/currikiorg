#!/bin/sh

DIR=`dirname $0`
cd $DIR

COMPRESSOR="./compressor/yuicompressor-2.3.5.jar"

COMPRESS_JS="java -jar $COMPRESSOR --type js "
COMPRESS_CSS="java -jar $COMPRESSOR --type css "

##COMPRESS_JS="cat"
##COMPRESS_CSS="cat"


I18N="\
	curriki/i18n.js \
	"

cat $I18N | $COMPRESS_JS > ../webapp/js/i18n.js

CURRIKI="\
	ext/DDView.js \
	ext/Multiselect.js \
	curriki/curriki-base.js \
	curriki/data/curriki-data-user.js \
	curriki/data/curriki-data-metadata.js \
	curriki/assets/curriki-assets.js \
	curriki/ui/curriki-ui.js \
	"

cat $CURRIKI | $COMPRESS_JS > ../webapp/js/curriki-main.js


CURRIKICSS="\
	ext/Multiselect.css \
	"
cat $CURRIKICSS | $COMPRESS_CSS > ../webapp/js/curriki-js.css



ADDPATH="\
	curriki/addpath/addpath-base.js \
	"
##	curriki/addpath/addpath-run.js \

cat $ADDPATH | $COMPRESS_JS > ../webapp/js/curriki-module-addpath.js
