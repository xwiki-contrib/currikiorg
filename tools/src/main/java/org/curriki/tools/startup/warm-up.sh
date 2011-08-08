#!/bin/sh
BASEURL="http://localhost:8080"
echo
echo
echo ---- warming up host $BASEURL -----------------
echo
echo -------------------- REQUESTING HOME PAGE ----------------
echo ------------------------ `date` --------------------------
echo ----------------------------------------------------------
echo
wget --quiet --save-headers -O - $BASEURL"/xwiki/bin/view/Main/WebHomeVistor" | head | grep "^HTTP"
echo
echo
echo
echo --------------- REQUESTING ONE VELOCITY RENDERING -------------------------
echo ------------------------ `date` --------------------------
echo ---------------------------------------------------------------------------
echo 
wget --quiet --save-headers -O - $BASEURL"/xwiki/bin/view/Main/AboutConnecting" | head | grep "^HTTP"
echo 
echo
echo ---------------- REQUESTING ONE ATTACHMENT DOWNLOAD -----------------------
echo ------------------------ `date` --------------------------
echo ---------------------------------------------------------------------------
echo 
wget --quiet --save-headers -O - $BASEURL"/xwiki/bin/download/Main/WebHomeVistor/images.zip/images/video%20position%20img.png" | head | grep "^HTTP"
echo 
echo
echo ------------------- REQUESTING ONE LUCENE INDEX ACCESS --------------------
echo ------------------------ `date` --------------------------
echo ---------------------------------------------------------------------------
echo 
wget --quiet --save-headers -O - $BASEURL"/xwiki/bin/view/Search/Members?start=0&limit=25&xpage=plain&_dc=1293583818710&module=member&terms=&other=&subjectparent=&language=&review=&ictprfx=activity&ict=activity_exercise&special=&sort=title&dir=ASC"  | head | grep "^HTTP"
echo
echo
echo --------------------------- FINISHED WARM-UP ------------------------------
echo ------------------------ `date` --------------------------
echo ---------------------------------------------------------------------------

