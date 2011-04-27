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

cat $I18N | $UNCOMPRESS_JS > ../webapp/js/i18n-debug.js
cat $I18N | $COMPRESS_JS > ../webapp/js/i18n.js

CURRIKI="\
	ext/DDView.js \
	ext/Multiselect.js \
	ext/pPageSize.js \
	ext/Ext.grid.RowExpander.js \
	ext/ExtJsOverride.js \
	\
	curriki/curriki-base.js \
	curriki/data/curriki-data-user.js \
	curriki/data/curriki-data-code.js \
	curriki/assets/curriki-assets.js \
	curriki/ui/curriki-ui.js \
	curriki/ui/rating.js \
	"

cat $CURRIKI | $UNCOMPRESS_JS > ../webapp/js/curriki-main-debug.js
cat $CURRIKI | $COMPRESS_JS > ../webapp/js/curriki-main.js



CURRIKICSS="\
	ext/Multiselect.css \
	"
cat $CURRIKICSS | $UNCOMPRESS_CSS > ../webapp/js/curriki-js-debug.css
cat $CURRIKICSS | $COMPRESS_CSS > ../webapp/js/curriki-js.css



ADDPATH="\
	curriki/addpath/addpath-base.js \
	"

cat $ADDPATH | $UNCOMPRESS_JS > ../webapp/js/curriki-module-addpath-debug.js
cat $ADDPATH | $COMPRESS_JS > ../webapp/js/curriki-module-addpath.js



NOMINATE="\
	curriki/crs/nominate-base.js \
	"

cat $NOMINATE | $UNCOMPRESS_JS > ../webapp/js/curriki-module-nominate-debug.js
cat $NOMINATE | $COMPRESS_JS > ../webapp/js/curriki-module-nominate.js

REVIEW="\
	curriki/crs/review-base.js \
	"

cat $REVIEW | $UNCOMPRESS_JS > ../webapp/js/curriki-module-review-debug.js
cat $REVIEW | $COMPRESS_JS > ../webapp/js/curriki-module-review.js

UNNOMINATE="\
	curriki/crs/unnominate-base.js \
	"

cat $UNNOMINATE | $UNCOMPRESS_JS > ../webapp/js/curriki-module-unnominate-debug.js
cat $UNNOMINATE | $COMPRESS_JS > ../webapp/js/curriki-module-unnominate.js

PARTNER="\
	curriki/crs/partner-base.js \
	"

cat $PARTNER | $UNCOMPRESS_JS > ../webapp/js/curriki-module-partner-debug.js
cat $PARTNER | $COMPRESS_JS > ../webapp/js/curriki-module-partner.js

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

cat $SEARCH | $UNCOMPRESS_JS > ../webapp/js/curriki-module-search-debug.js
cat $SEARCH | $COMPRESS_JS > ../webapp/js/curriki-module-search.js

ASTERIXREVIEW="\
	curriki/crs/asterixReview-base.js \
	"

cat $ASTERIXREVIEW | $UNCOMPRESS_JS > ../webapp/js/curriki-module-asterixReview-debug.js
cat $ASTERIXREVIEW | $COMPRESS_JS > ../webapp/js/curriki-module-asterixReview.js

REORDER="\
	curriki/reorder/base.js \
	"
cat $REORDER | $UNCOMPRESS_JS > ../webapp/js/curriki-module-reorder-debug.js
cat $REORDER | $COMPRESS_JS > ../webapp/js/curriki-module-reorder.js

TOC="\
	curriki/toc/base.js \
	"
cat $TOC | $UNCOMPRESS_JS > ../webapp/js/curriki-module-toc-debug.js
cat $TOC | $COMPRESS_JS > ../webapp/js/curriki-module-toc.js

ORGANIZE="\
	curriki/organize/base.js \
	"
cat $ORGANIZE | $UNCOMPRESS_JS > ../webapp/js/curriki-module-organize-debug.js
cat $ORGANIZE | $COMPRESS_JS > ../webapp/js/curriki-module-organize.js

FLAG="\
	curriki/flag/base.js \
	"
cat $FLAG | $UNCOMPRESS_JS > ../webapp/js/curriki-module-flag-debug.js
cat $FLAG | $COMPRESS_JS > ../webapp/js/curriki-module-flag.js
