try {

    var htmlHeadInsert = "<style type=\"text/css\">"+
            "    #donateBanner {"+
            "        font-family: Arial, sans-serif; font-size: 11pt;"+
            "        text-align: center;"+
            "        border: 2px solid orange;"+
            "        border-top: 0;"+
            "        border-bottom-right-radius: 10px;"+
            "        border-bottom-left-radius: 10px;"+
            "        display:none;"+
            "    }"+
            "    .curriki #donateBanner {"+
            "        max-width: 850px; align:center;"+
            "    }"+
            "    #donateBanner table {"+
            "        background: #FFCC00;"+
            "        width: 100%;"+
            "    }"+
            "    #donateBanner table tr td {"+
            "        padding: 1em;"+
            "        font-weight: bold;"+
            "        vertical-align: top;"+
            "        text-align: justify;"+
            "    }"+
            "    #donateBanner #closeBox {"+
            "        text-align:right;"+
            "        padding: 0.5em;"+
            "    }"+
            "    #donateBanner a {"+
            "        background: transparent;"+
            "        border:0;"+
            "    }"+
            "    #donateBanner a img {"+
            "        width: 1em;"+
            "        height: 1em;"+
            "    }"+
            "    #pleaseHelpCtn {"+
            "        text-align:center;"+
            "        width:100%;"+
            "        margin-bottom: -0.6em;"+
            "    }"+
            "    #pleaseHelpBtn {"+
            "        background:#FFCC00;"+
            "        margin-top: -2pt;"+
            "        border: 2px solid orange;"+
            "        border-top: 0;"+
            "        border-bottom-right-radius: 10px;"+
            "        border-bottom-left-radius: 10px;"+
            "        padding: 5px 10px 5px 10px;" +
            ""+
            "        font-family: Arial, sans-serif; font-size: 11pt; font-weight: bold;"+
            "    }"+
            "    #pleaseHelpBtn:hover {"+
            "        background: #FFC000;"+
            "    }"+
            "    #pleaseHelpBtn:focus { outline:0; }"+
            "    #pleaseHelpBtn img {"+
            "        vertical-align: -30%;"+
            "    }"+
            "    #pleaseHelpBtn a, #pleaseHelpBtn a:visited {color:black; text-decoration: none}"+
            "    #pleaseHelpBtn a:hover {color:#111111; text-decoration: none; cursor: hand; cursor:pointer;}"+
            "    #pleaseHelpBtn a:active {color:#555555; text-decoration: none}"+
            "</style>";

    var htmlBodyInsert ="<div id=\"donateBanner\">"+
        "    <table ><tr>"+
        "        <td></td>"+
        "        <td colspan=\"2\""+
        "          >Dear Curriki members: We are the tiny non-profit that brings you a wealth of free digital teaching"+
        "            and learning content. Other sites offer a \"freemium service\" where you get a \"lite\" version of"+
        "            the product for free, but you have to pay for the best stuff."+
        "        </td>"+
        "        <td colspan=\"2\""+
        "          >Curriki has only 4 staff members and serves over 2 million visitors per year and it"+
        "            always has been and always will be FREE. If Curriki has served you well, please consider donating"+
        "            $10, $25, $50 or whatever you can so we can keep Curriki free. Thank you."+
        "          </td>"+
        "        <td id=\"closeBox\"><a href=\"javascript:switchDonateBanner();\"><img"+
        "                onmouseover=\"this.src='/static/donate-banner/fa-times-circle-checked.png';\""+
        "                onmouseout=\"this.src='/static/donate-banner/fa-times-circle.png';\""+
        "                src=\"/static/donate-banner/fa-times-circle.png\" alt=\"close donate banner\"/></a></td>"+
        "    </tr>"+
        "    </table>"+
        "</div>"+
        ""+
        "<div id=\"pleaseHelpCtn\">"+
        "    <span id=\"pleaseHelpBtn\""+
        "            ><a href=\"/welcome/about-curriki/donate/\">Please help.</a><a onclick=\"switchDonateBanner();\">&nbsp;&nbsp;<img id=\"pleaseHelpBtnArrow\" src=\"/static/donate-banner/icon-arrow-circle-down.png\" style=\"height:1.2em\"/>&nbsp;&nbsp;</a></span>"+
        "</div>";

    /** Compute visibility from the cookie. */
    function initDonateBannerVisible() {
        if(document.cookie.indexOf("donateBannerVisible=false")>=0)
            window.donateBannerVisible = false;
        else
            window.donateBannerVisible = true;
        if(console) console.log("visible? " + window.donateBannerVisible);
        if(window.donateBannerVisible) {
            window.donateBannerVisible = false;
            switchDonateBanner();
        }
    }

    function switchDonateBanner() {
        if(window.donateBannerVisible) {
            document.getElementById("donateBanner").style.display="none";
            if(console) console.log("Made display none.");
            document.getElementById("pleaseHelpBtnArrow").src="/static/donate-banner/icon-arrow-circle-down.png";
            document.cookie = "donateBannerVisible=false";
            window.donateBannerVisible = false;
        } else {
            document.getElementById("donateBanner").style.display="block";
            if(console) console.log("Made display block.");
            document.getElementById("pleaseHelpBtnArrow").src="/static/donate-banner/icon-arrow-circle-up.png";
            document.cookie = "donateBannerVisible=true";
            window.donateBannerVisible = true;
        }
    }


    function initDonateBanner() {

        if(document.body) {
            clearInterval(window.bannerInitter);
            if (document.body.insertAdjacentHTML) {
                document.head.insertAdjacentHTML("beforeEnd", htmlHeadInsert);
                document.getElementById("header").insertAdjacentHTML("beforeBegin", htmlBodyInsert);
                initDonateBannerVisible();
            }
        } else {
            if(console) console.log("Retrying...");
        }
    }
    window.bannerInitter = setInterval(initDonateBanner, 50);
} catch (e) {
    if(console) console.log(e);
}