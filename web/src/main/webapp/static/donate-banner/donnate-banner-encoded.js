var htmlHeadInsert = "<style type=\"text/css\">" +"" +
"#donateBanner {" +"" +
"    font-family: Arial, sans-serif; font-size: 11pt;" +"" +
"    text-align: center;" +"" +
"    border: 2px solid orange;" +"" +
"    border-top: 0;" +"" +
"    border-bottom-right-radius: 10px;" +"" +
"    border-bottom-left-radius: 10px;" +"" +
"    display:none;" +"" +
"    }" +"" +
"#donateBanner table {" +"" +
"    background: #FFCC00;" +"" +
"    width: 100%;" +"" +
"    }" +"" +
"#donateBanner table tr td {" +"" +
"    padding: 1em;" +"" +
"    font-weight: bold;" +"" +
"    vertical-align: top;" +"" +
"    text-align: justify;" +"" +
"    }" +"" +
"#donateBanner #closeBox {" +"" +
"    text-align:right;" +"" +
"    padding: 0.5em;" +"" +
"    }" +"" +
"#donateBanner a {" +"" +
"    background: transparent;" +"" +
"    border:0;" +"" +
"    }" +"" +
"#donateBanner a img {" +"" +
"    width: 1em;" +"" +
"    height: 1em;" +"" +
"    }" +"" +
"#pleaseHelpCtn {" +"" +
"    text-align:center;" +"" +
"    width:100%;" +"" +
"    }" +"" +
"#pleaseHelpBtn {" +"" +
"    background:#FFCC00;" +"" +
"    margin-top: -2pt;" +"" +
"    border: 2px solid orange;" +"" +
"    border-top: 0;" +"" +
"    border-bottom-right-radius: 10px;" +"" +
"    border-bottom-left-radius: 10px;" +"" +
"    padding: 5px 10px 5px 10px;" +"" +
"" +
"    font-family: Arial, sans-serif; font-size: 11pt; font-weight: bold;" +"" +
"    }" +"" +
"#pleaseHelpBtn:hover {" +"" +
"    background: #FFC000;" +"" +
"    }" +"" +
"#pleaseHelpBtn img {" +"" +
"    vertical-align: -30%;" +"" +
"    }" +"" +
"</style>";

var htmlBodyInsert = "<div id=\"donateBanner\">" +"" +
"    <table ><tr>" +"" +
"        <td></td>" +"" +
"        <td colspan=\"2\"" +"" +
"        >Dear Curriki members: We are the tiny non-profit that brings you a wealth of free digital teaching" +"" +
"        and learning content. Other sites offer a \"freemium service\" where you get a \"lite\" version of" +"" +
"        the product for free, but you have to pay for the best stuff." +"" +
"        </td>" +"" +
"        <td colspan=\"2\"" +"" +
"        >Curriki has only 4 staff members and serves over 2 million visitors per year and it" +"" +
"        always has been and always will be FREE. If Curriki has served you well, please consider donating" +"" +
"        $10, $25, $50 or whatever you can so we can keep Curriki free. Thank you" +"" +
"        </td>" +"" +
"        <td id=\"closeBox\"><a href=\"javascript:switchDonateBanner();\"><img" +"" +
"        onmouseover=\"this.src='fa-times-circle-checked.png';\"" +"" +
"        onmouseout=\"this.src='fa-times-circle.png';\"" +"" +
"        src=\"fa-times-circle.png\" alt=\"close donate banner\"/></a></td>" +"" +
"    </tr>" +"" +
"    </table>" +"" +
"    </div>" +"" +
"" +
"<div id=\"pleaseHelpCtn\">" +"" +
"    <button id=\"pleaseHelpBtn\"" +"" +
"    onclick=\"switchDonateBanner();\"" +"" +
"    >Please help&nbsp;&nbsp;<img id=\"pleaseHelpBtnArrow\" src=\"icon-arrow-circle-down.png\" width=\"10%\"/></button>" +"" +
"</div>" +"";
