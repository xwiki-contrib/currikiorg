#!/bin/sh

commandHere="$0"
binPath=`dirname $commandHere`
cd ${binPath}
echo "Updating phrases in directory \"`pwd`\""

HOST=http://demo:gelc@current.dev.curriki.org/
## or HOST=http://demo:gelc@current.dev.curriki.org/

languages="en";##,fr,ru,hi,es,id,si,ta,eo,pl"
## (to copy from xwiki preferences)
languages=`echo $languages | sed 's/,/ /g'`
## we could implement the following to define it automatically
##`curl -s "${HOST}/xwiki/bin/view/XWiki/SupportedLanguages?xpage=plain&len=2"`


## (to copy from xwiki preferences)
## we could also automate that
files="XWiki.GlobalClassTranslations,CreateResources.Translations,XWiki.Translations,XWiki.CurrikiGWTTranslation,CRS.Translations,Registration.Translations,FileCheck.Translations,Groups.Translations,MyCurriki.Translations,XWiki.ToTranslate,Search.Translations,CurrikiCode.EditViewTranslations,Affiliate.Translations"
# removed for now ,AdWordsJump.Translations"
files=`echo $files | sed 's|\.|/|g' | sed 's|\,| |g'`
##
baseURL="${HOST}/xwiki/bin/view/"
SOURCE=`mktemp ./TMPXXXXXX`
XSL=`mktemp ./TMPxslXXXXXX`
echo With the following languages: $languages

dumpHeader() {
cat <<- the-end
#
#
# Phrase file for Curriki for language $language
# ---- please do not edit here
# ---- please edit at ${HOST}/xwiki/bin/view/Translations/${file}?language=${language}
# 
the-end
}


cat >"$XSL" <<- the-end
<xsl:stylesheet version = '1.0'
 xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
 <xsl:output method='text'/>
 <xsl:template match='/'>
 <xsl:value-of select='//content'/>
 </xsl:template>
</xsl:stylesheet>
the-end


echo "Starting through languages..."
for file in $files ; do
echo
echo "==== $file ======= "
for language in $languages ; do
  export language
  url="${baseURL}${file}?xpage=xml&language=${language}"
  echo "${language}: Fetching source of \n  ${url} "
  XML_PAGE=`mktemp ./TMPXXXXXX`
  curl -L -s -k "$url" > $XML_PAGE
  xsltproc $XSL $XML_PAGE > $SOURCE
  mkdir -p `dirname ${file}.${language}.properties`

  ( dumpHeader && cat $SOURCE) | \
    sed 's|\(^1 .*$\)|\# \1|'  |  \
    sed 's|\(^<pre>$\)|\# \1|' | \
    sed 's|\(^</pre>$\)|\# \1|' > "${file}.${language}.properties"
  rm $XML_PAGE
done
done
rm $XSL
rm $SOURCE
echo " ============================ "
echo "... finished updating phrases."
