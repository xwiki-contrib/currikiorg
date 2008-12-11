#!/bin/sh

DIR=`dirname $0`
cd $DIR

COMPRESSOR="./compressor/yuicompressor-2.3.5.jar"

COMPRESS_JS="java -jar $COMPRESSOR --type js "
COMPRESS_CSS="java -jar $COMPRESSOR --type css "

UNCOMPRESS_JS="cat"
UNCOMPRESS_CSS="cat"


I18N="\
	curriki/i18n.js \
	"

cat $I18N | $COMPRESS_JS > ../webapp/js/i18n.js
cat $I18N | $UNCOMPRESS_JS > ../webapp/js/i18n-debug.js

CURRIKI="\
	ext/DDView.js \
	ext/Multiselect.js \
	ext/pPageSize.js \
	ext/Ext.grid.RowExpander.js \
	ext/ExtJsOverride.js \
	\
	curriki/curriki-base.js \
	curriki/data/curriki-data-user.js \
	curriki/data/curriki-data-metadata.js \
	curriki/assets/curriki-assets.js \
	curriki/ui/curriki-ui.js \
	"

cat $CURRIKI | $COMPRESS_JS > ../webapp/js/curriki-main.js
cat $CURRIKI | $UNCOMPRESS_JS > ../webapp/js/curriki-main-debug.js


CURRIKICSS="\
	ext/Multiselect.css \
	"
cat $CURRIKICSS | $COMPRESS_CSS > ../webapp/js/curriki-js.css
cat $CURRIKICSS | $UNCOMPRESS_CSS > ../webapp/js/curriki-js-debug.css



ADDPATH="\
	curriki/addpath/addpath-base.js \
	"

cat $ADDPATH | $COMPRESS_JS > ../webapp/js/curriki-module-addpath.js
cat $ADDPATH | $UNCOMPRESS_JS > ../webapp/js/curriki-module-addpath-debug.js



NOMINATE="\
	curriki/crs/nominate-base.js \
	"

cat $NOMINATE | $COMPRESS_JS > ../webapp/js/curriki-module-nominate.js
cat $NOMINATE | $UNCOMPRESS_JS > ../webapp/js/curriki-module-nominate-debug.js

REVIEW="\
	curriki/crs/review-base.js \
	"

cat $REVIEW | $COMPRESS_JS > ../webapp/js/curriki-module-review.js
cat $REVIEW | $UNCOMPRESS_JS > ../webapp/js/curriki-module-review-debug.js

UNNOMINATE="\
	curriki/crs/unnominate-base.js \
	"

cat $UNNOMINATE | $COMPRESS_JS > ../webapp/js/curriki-module-unnominate.js
cat $UNNOMINATE | $UNCOMPRESS_JS > ../webapp/js/curriki-module-unnominate-debug.js

PARTNER="\
	curriki/crs/partner-base.js \
	"

cat $PARTNER | $COMPRESS_JS > ../webapp/js/curriki-module-partner.js
cat $PARTNER | $UNCOMPRESS_JS > ../webapp/js/curriki-module-partner-debug.js

SEARCH="\
	curriki/search/global.js \
	curriki/search/util.js \
	\
	curriki/search/resource-data.js \
	curriki/search/resource-form.js \
	\
	curriki/search/group-data.js \
	curriki/search/group-form.js \
	\
	curriki/search/member-data.js \
	curriki/search/member-form.js \
	\
	curriki/search/blog-data.js \
	curriki/search/blog-form.js \
	\
	curriki/search/curriki-data.js \
	curriki/search/curriki-form.js \
	\
	curriki/search/form.js \
	"

cat $SEARCH | $COMPRESS_JS > ../webapp/js/curriki-module-search.js
cat $SEARCH | $UNCOMPRESS_JS > ../webapp/js/curriki-module-search-debug.js

ASTERIXREVIEW="\
	curriki/crs/asterixReview-base.js \
	"

cat $ASTERIXREVIEW | $COMPRESS_JS > ../webapp/js/curriki-module-asterixReview.js
cat $ASTERIXREVIEW | $UNCOMPRESS_JS > ../webapp/js/curriki-module-asterixReview-debug.js

REORDER="\
	curriki/reorder/base.js \
	"
cat $REORDER | $COMPRESS_JS > ../webapp/js/curriki-module-reorder.js
cat $REORDER | $UNCOMPRESS_JS > ../webapp/js/curriki-module-reorder-debug.js

TOC="\
	curriki/toc/base.js \
	"
cat $TOC | $COMPRESS_JS > ../webapp/js/curriki-module-toc.js
cat $TOC | $UNCOMPRESS_JS > ../webapp/js/curriki-module-toc-debug.js
