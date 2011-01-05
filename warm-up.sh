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
echo =================== REQUESTING ONE VELOCITY RENDERING =====================
echo ------------------------ `date` --------------------------
echo ===========================================================================
echo 
wget -O /dev/null $BASEURL"/xwiki/bin/download/Main/WebHomeVistor/images.zip/images/video%20position%20img.png"
echo 
echo
echo =================== REQUESTING ONE VELOCITY RENDERING =====================
echo ------------------------ `date` --------------------------
echo ===========================================================================
echo 
wget -O /dev/null $BASEURL"/xwiki/bin/view/Search/Resources?start=0&limit=25&xpage=plain&_dc=1293583818710&module=resource&terms=&other=&subjectparent=FW_masterFramework.Mathematics&subject=FW_masterFramework.Mathematics&category=interactive&level=&language=&review=&ictprfx=activity&ict=activity_exercise&special=&sort=title&dir=ASC"
echo
echo
echo ============================== FINISHED WARM-UP ===========================
echo ------------------------ `date` --------------------------
echo ===========================================================================

