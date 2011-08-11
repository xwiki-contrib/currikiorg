#!/bin/sh
BASEURL="http://localhost:8080"
echo
echo
echo ---- warming up host $BASEURL -----------------
echo
echo ========================== REQUESTING HOME PAGE ===========================
echo ------------------------ `date` --------------------------
echo ===========================================================================
echo
wget -O /dev/null $BASEURL"/xwiki/bin/view/Main/WebHomeVistor"
echo
echo
echo
echo =================== REQUESTING ONE VELOCITY RENDERING =====================
echo ------------------------ `date` --------------------------
echo ===========================================================================
echo 
wget -O /dev/null $BASEURL"/xwiki/bin/view/Main/AboutConnecting"
echo 
echo
echo ================== REQUESTING ONE ATTACHMENT DOWNLOAD =====================
echo ------------------------ `date` --------------------------
echo ===========================================================================
echo 
wget -O /dev/null $BASEURL"/xwiki/bin/download/Main/WebHomeVistor/images.zip/images/video%20position%20img.png"
echo 
echo
echo =================== REQUESTING ONE SOLR INDEX ACCESS =====================
echo ------------------------ `date` --------------------------
echo ===========================================================================
echo
wget -O /dev/null $BASEULR"/currikiExtjs?start=0&limit=25&xpage=plain&groupsId=Group_InventingtheFuture%2CGroup_AppalachianWatershedEducators%2CGroup_DigitalToolsforHomeworkHelp%2CGroup_HotTopicsinEducationalTechnology%2C&userId=XWiki.adminPolx&isAdmin=true&module=resource&terms=&other=&subjectparent=&subject=&category=interactive&level=&language=&review=&ictprfx=&ict=&special=&rows=25&sort=score&dir=DESC"
echo
echo =================== REQUESTING ONE LUCENE INDEX ACCESS =====================
echo ------------------------ `date` --------------------------
echo ===========================================================================
echo 
wget -O /dev/null $BASEURL"/xwiki/bin/view/Search/Resources?start=0&limit=25&xpage=plain&_dc=1293583818710&module=resource&terms=&other=&subjectparent=FW_masterFramework.Mathematics&subject=FW_masterFramework.Mathematics&category=interactive&level=&language=&review=&ictprfx=activity&ict=activity_exercise&special=&sort=title&dir=ASC"
echo
echo
echo ============================== FINISHED WARM-UP ===========================
echo ------------------------ `date` --------------------------
echo ===========================================================================

