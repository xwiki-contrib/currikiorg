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
	ext/pPageSize.js \
	curriki/curriki-base.js \
	curriki/data/curriki-data-user.js \
	curriki/data/curriki-data-metadata.js \
	curriki/assets/curriki-assets.js \
	curriki/ui/curriki-ui.js \
	"
#	ext/ExtJsOverride.js \

cat $CURRIKI | $COMPRESS_JS > ../webapp/js/curriki-main.js


CURRIKICSS="\
	ext/Multiselect.css \
	"
cat $CURRIKICSS | $COMPRESS_CSS > ../webapp/js/curriki-js.css



ADDPATH="\
	curriki/addpath/addpath-base.js \
	"

cat $ADDPATH | $COMPRESS_JS > ../webapp/js/curriki-module-addpath.js

NOMINATE="\
	curriki/crs/nominate-base.js \
	"

cat $NOMINATE | $COMPRESS_JS > ../webapp/js/curriki-module-nominate.js

REVIEW="\
	curriki/crs/review-base.js \
	"

cat $REVIEW | $COMPRESS_JS > ../webapp/js/curriki-module-review.js

UNNOMINATE="\
	curriki/crs/unnominate-base.js \
	"

cat $UNNOMINATE | $COMPRESS_JS > ../webapp/js/curriki-module-unnominate.js

PARTNER="\
	curriki/crs/partner-base.js \
	"

cat $PARTNER | $COMPRESS_JS > ../webapp/js/curriki-module-partner.js
