package org.curriki.plugin.activitystream.impl;


public class TestTeasify {

    public static void main(String[] args) throws Throwable {
        System.out.println("============== testing testTeasifyLongBig ============");
        try {
            new TestTeasify().testTeasifyLongBig();
            System.out.println("Success");
        } catch(Throwable t) {
            t.printStackTrace();
        }
        System.out.println("============== testing testTeasifyHtmlBit ============");
        try {
            new TestTeasify().testTeasifyHtmlBit();
            System.out.println("Success");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        System.out.println("============== testing testTeasifyShortBit ============");
        try {
            new TestTeasify().testTeasifyShortBit();
            System.out.println("Success");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void testTeasifyLongBig() throws Throwable {
        // create a text of length 2000
        StringBuilder b = new StringBuilder (2100);
        String baseString = "123456789";
        for(int i=0; i<200; i++) b.append("123456789").append(' ');
        String result = teasify(b.toString());

        System.out.println("Teasify result: " + result);
        if(result.length()>200) throw new IllegalStateException("Length is " + result.length() + " and not smaller as 200 as it should.");
        if(!result.endsWith(baseString + "…")) throw new IllegalStateException("The text got cut in the middle of a word!");
    }

    public void testTeasifyShortBit() throws Throwable {
        String shortBit = "short-bit in\n\ttwo lines";
        String result = teasify(shortBit);
        if(result.endsWith("…")) throw new IllegalStateException("It added … at the end of the thing!");
        if(!"short-bit in two lines".equals(result)) throw new IllegalStateException("Not exepected");

        result = teasify("A little bit of myself.");
        if(result.endsWith("…")) throw new IllegalStateException("It added … at the end of the thing!");
        if(!"A little bit of myself.".equals(result)) throw new IllegalStateException("Not exepected");
    }

    public void testTeasifyHtmlBit() throws Throwable {

        String result = teasify(htmlSource);

        String shouldBe = "testgroup2 Home Discussions Messages Curriculum Members Documentation asdad Edit | Delete Jul-10-2014 - 10:32 PM GMT+01:00, by Paul Libbrecht (admin) asdad asdasd asdasd Comment(s) Paul Libbrecht…";
        System.out.println("Obtained:\n" + result);
        if(result.contains("<") || result.contains(">")) throw new IllegalStateException("Result contains markup!");
        if(!result.equals(shouldBe)) {
            System.out.println(shouldBe);
            throw new IllegalStateException("The result is not as expected.");
        }
    }



    static String teasify(String text) {
        if(text==null) text = "";
        // put a space before block-separating elements (see http://de.selfhtml.org/html/referenz/elemente.htm)
        text = text.replaceAll("</?(address|blockquote|center|del|dir|div|dl|fieldset|form|h[0-6]|hr|ins|isindex|menu|noframes|noscript|ol|p|pre|table|ul)"," <x");
        text = text.replaceAll("<[^>]+>","");
        text = text.replaceAll("\\{\\{/?html[^}]*\\}\\}", "");
        text = text.replaceAll("[\\s]+", " ");
        text = text.trim();
        if(text.length()<200) return text;
        int p =0, max = Math.min(200, text.length());
        for(int i=0; i<max; i++) {
            if(!Character.isLetterOrDigit(text.charAt(i))) p = i;
        }
        if(p<150 && text.length()>=200) p = 150;
        if(p<text.length()) {
            text = text.substring(0, p);
            text = text.trim();
            text = text + "…";
        }
        return text;
    }



    private static String htmlSource = "\n" +
            "       <h1 class=\"groupsTitle\">testgroup2</h1>\n" +
            "<div id=\"groups-messages\" class=\"groups groups-messages\">\n" +
            " <ul id=\"groups-tabs\" class=\"tabs\">\n" +
            "  <li>\n" +
            "  <a href=\"/xwiki/bin/view/Group_testgroup2/WebHome\">Home</a>\n" +
            " </li>\n" +
            "  <li>\n" +
            "  <a href=\"/xwiki/bin/view/Group_testgroup2/Discussions\">Discussions</a>\n" +
            " </li>\n" +
            "  <li class=\"current\">\n" +
            "  <a href=\"/xwiki/bin/view/Group_testgroup2/Messages\">Messages</a>\n" +
            " </li>\n" +
            "  <li>\n" +
            "  <a href=\"/xwiki/bin/view/Group_testgroup2/Curriculum\">Curriculum</a>\n" +
            " </li>\n" +
            "  <li>\n" +
            "  <a href=\"/xwiki/bin/view/Group_testgroup2/Members\">Members</a>\n" +
            " </li>\n" +
            "  <li>\n" +
            "  <a href=\"/xwiki/bin/view/Group_testgroup2/Documentation\">Documentation</a>\n" +
            " </li>\n" +
            " </ul>\n" +
            "<div id=\"groups-messages-main\" class=\"form-wrp tab-container\">\n" +
            "                <script src=\"/xwiki/skins/curriki8/Pork.Iframe.js\" type=\"text/javascript\"></script> \n" +
            "      <script type=\"text/javascript\" src=\"/xwiki/bin/skin/skins/curriki8/groups/groups.js\"></script>\n" +
            "          <script type=\"text/javascript\" src=\"/xwiki/bin/skin/skins/curriki8/groups/groupsmessages.js\"></script>\n" +
            "                <div class=\"frame\" id=\"section_1\">\n" +
            "    <div class=\"titleheader titleheader-blue\"><h3 class=\"titleheader-left\">asdad</h3>\n" +
            "      <div class=\"titleheader-right\">\n" +
            "      <span id=\"editLinkContainer\" style=\"padding:0px;\">\n" +
            "      <a href=\"javascript:editMessage('Messages_Group_testgroup2','asdad','groups-message-edit')\" title=\"Edit\">Edit</a>\n" +
            "      <span class=\"separator\"> | </span>\n" +
            "      </span>\n" +
            "            <a href=\"javascript:deleteMessage('Messages_Group_testgroup2','asdad', '%2Fxwiki%2Fbin%2Fview%2FGroup_testgroup2%2FMessages')\" title=\"Delete\">Delete</a>\n" +
            "      </div>\n" +
            "    </div>\n" +
            "    <div class=\"frame-content\" id=\"section1content\">\n" +
            "    <div id=\"groups-message-edit\">\n" +
            "                    <p><span class=\"date\">Jul-10-2014 - 10:32 PM GMT+01:00</span>, by <a><span class=\"wikilink\"></span></a><a href=\"/xwiki/bin/view/XWiki/adminPolx\">Paul Libbrecht (admin)</a></p>\n" +
            "    <p class=\"frame-title\">asdad</p>\n" +
            "    <p>\n" +
            "        </p><p>asdasd</p><p><strong>asdasd</strong></p>\n" +
            "    <p></p>\n" +
            "      </div>\n" +
            "     <div class=\"clearfloats\"></div>\n" +
            "     </div>\n" +
            "  </div>\n" +
            "  <a name=\"comments\"></a>                                  <div class=\"frame\" id=\"section_2\">\n" +
            "            <h3 class=\"titleheader titleheader-blue\">Comment(s)</h3>\n" +
            "       <div class=\"frame-content\" id=\"section2content\">\n" +
            "                  <div id=\"comment0\" class=\"blog-comment\">\n" +
            "      <div class=\"blog-comment-avatar\">\n" +
            "           \n" +
            " \n" +
            "         <a href=\"/xwiki/bin/view/XWiki/adminPolx\" target=\"_self\" title=\"Paul Libbrecht (admin)\" class=\"photo\">\n" +
            "               <img src=\"/xwiki/bin/download/XWiki/adminPolx/MoiAutoroute.jpeg?width=96\" class=\"photo\" width=\"48\">          </a>\n" +
            "       </div>\n" +
            "      <div class=\"blog-comment-line\">\n" +
            "      <p><span class=\"date\">\n" +
            "        <span class=\"wikilink\"><a href=\"/xwiki/bin/view/XWiki/adminPolx\">Paul Libbrecht (admin)</a></span> | <span class=\"blog-comment-date\">Jul-10-2014</span>\n" +
            "          <a class=\"right\" href=\"/xwiki/bin/view/Groups/MessageDeleteCommentService?docname=Messages_Group_testgroup2.asdad&amp;date=1405011810000\" title=\"Delete\" onclick=\"return updateHref(this, confirm('Delete comment?'));\">Delete</a>           </span></p>\n" +
            "      </div>\n" +
            "      test oh test.    </div>\n" +
            "              <hr class=\"my-curriki-hr\">\n" +
            "                <div id=\"comment1\" class=\"blog-comment odd-row\">\n" +
            "      <div class=\"blog-comment-avatar\">\n" +
            "           \n" +
            " \n" +
            "         <a href=\"/xwiki/bin/view/XWiki/adminPolx\" target=\"_self\" title=\"Paul Libbrecht (admin)\" class=\"photo\">\n" +
            "               <img src=\"/xwiki/bin/download/XWiki/adminPolx/MoiAutoroute.jpeg?width=96\" class=\"photo\" width=\"48\">          </a>\n" +
            "       </div>\n" +
            "      <div class=\"blog-comment-line\">\n" +
            "      <p><span class=\"date\">\n" +
            "        <span class=\"wikilink\"><a href=\"/xwiki/bin/view/XWiki/adminPolx\">Paul Libbrecht (admin)</a></span> | <span class=\"blog-comment-date\">Jul-10-2014</span>\n" +
            "          <a class=\"right\" href=\"/xwiki/bin/view/Groups/MessageDeleteCommentService?docname=Messages_Group_testgroup2.asdad&amp;date=1405012288000\" title=\"Delete\" onclick=\"return updateHref(this, confirm('Delete comment?'));\">Delete</a>           </span></p>\n" +
            "      </div>\n" +
            "      asdasdas    </div>\n" +
            "              <hr class=\"my-curriki-hr\">\n" +
            "                <div id=\"comment0\" class=\"blog-comment\">\n" +
            "      <div class=\"blog-comment-avatar\">\n" +
            "           \n" +
            " \n" +
            "         <a href=\"/xwiki/bin/view/XWiki/adminPolx\" target=\"_self\" title=\"Paul Libbrecht (admin)\" class=\"photo\">\n" +
            "               <img src=\"/xwiki/bin/download/XWiki/adminPolx/MoiAutoroute.jpeg?width=96\" class=\"photo\" width=\"48\">          </a>\n" +
            "       </div>\n" +
            "      <div class=\"blog-comment-line\">\n" +
            "      <p><span class=\"date\">\n" +
            "        <span class=\"wikilink\"><a href=\"/xwiki/bin/view/XWiki/adminPolx\">Paul Libbrecht (admin)</a></span> | <span class=\"blog-comment-date\">Jul-10-2014</span>\n" +
            "          <a class=\"right\" href=\"/xwiki/bin/view/Groups/MessageDeleteCommentService?docname=Messages_Group_testgroup2.asdad&amp;date=1405012310000\" title=\"Delete\" onclick=\"return updateHref(this, confirm('Delete comment?'));\">Delete</a>           </span></p>\n" +
            "      </div>\n" +
            "      asdasd    </div>\n" +
            "              <hr class=\"my-curriki-hr\">\n" +
            "                <div id=\"comment1\" class=\"blog-comment odd-row\">\n" +
            "      <div class=\"blog-comment-avatar\">\n" +
            "           \n" +
            " \n" +
            "         <a href=\"/xwiki/bin/view/XWiki/adminPolx\" target=\"_self\" title=\"Paul Libbrecht (admin)\" class=\"photo\">\n" +
            "               <img src=\"/xwiki/bin/download/XWiki/adminPolx/MoiAutoroute.jpeg?width=96\" class=\"photo\" width=\"48\">          </a>\n" +
            "       </div>\n" +
            "      <div class=\"blog-comment-line\">\n" +
            "      <p><span class=\"date\">\n" +
            "        <span class=\"wikilink\"><a href=\"/xwiki/bin/view/XWiki/adminPolx\">Paul Libbrecht (admin)</a></span> | <span class=\"blog-comment-date\">Jul-10-2014</span>\n" +
            "          <a class=\"right\" href=\"/xwiki/bin/view/Groups/MessageDeleteCommentService?docname=Messages_Group_testgroup2.asdad&amp;date=1405012346000\" title=\"Delete\" onclick=\"return updateHref(this, confirm('Delete comment?'));\">Delete</a>           </span></p>\n" +
            "      </div>\n" +
            "      asdas    </div>\n" +
            "              <hr class=\"my-curriki-hr\">\n" +
            "                <div id=\"comment0\" class=\"blog-comment\">\n" +
            "      <div class=\"blog-comment-avatar\">\n" +
            "           \n" +
            " \n" +
            "         <a href=\"/xwiki/bin/view/XWiki/adminPolx\" target=\"_self\" title=\"Paul Libbrecht (admin)\" class=\"photo\">\n" +
            "               <img src=\"/xwiki/bin/download/XWiki/adminPolx/MoiAutoroute.jpeg?width=96\" class=\"photo\" width=\"48\">          </a>\n" +
            "       </div>\n" +
            "      <div class=\"blog-comment-line\">\n" +
            "      <p><span class=\"date\">\n" +
            "        <span class=\"wikilink\"><a href=\"/xwiki/bin/view/XWiki/adminPolx\">Paul Libbrecht (admin)</a></span> | <span class=\"blog-comment-date\">Jul-10-2014</span>\n" +
            "          <a class=\"right\" href=\"/xwiki/bin/view/Groups/MessageDeleteCommentService?docname=Messages_Group_testgroup2.asdad&amp;date=1405013900000\" title=\"Delete\" onclick=\"return updateHref(this, confirm('Delete comment?'));\">Delete</a>           </span></p>\n" +
            "      </div>\n" +
            "      asdas    </div>\n" +
            "              <hr class=\"my-curriki-hr\">\n" +
            "                <div id=\"comment1\" class=\"blog-comment odd-row\">\n" +
            "      <div class=\"blog-comment-avatar\">\n" +
            "           \n" +
            " \n" +
            "         <a href=\"/xwiki/bin/view/XWiki/adminPolx\" target=\"_self\" title=\"Paul Libbrecht (admin)\" class=\"photo\">\n" +
            "               <img src=\"/xwiki/bin/download/XWiki/adminPolx/MoiAutoroute.jpeg?width=96\" class=\"photo\" width=\"48\">          </a>\n" +
            "       </div>\n" +
            "      <div class=\"blog-comment-line\">\n" +
            "      <p><span class=\"date\">\n" +
            "        <span class=\"wikilink\"><a href=\"/xwiki/bin/view/XWiki/adminPolx\">Paul Libbrecht (admin)</a></span> | <span class=\"blog-comment-date\">Jul-10-2014</span>\n" +
            "          <a class=\"right\" href=\"/xwiki/bin/view/Groups/MessageDeleteCommentService?docname=Messages_Group_testgroup2.asdad&amp;date=1405014137000\" title=\"Delete\" onclick=\"return updateHref(this, confirm('Delete comment?'));\">Delete</a>           </span></p>\n" +
            "      </div>\n" +
            "      asdas    </div>\n" +
            "              <hr class=\"my-curriki-hr\">\n" +
            "                <div id=\"comment0\" class=\"blog-comment\">\n" +
            "      <div class=\"blog-comment-avatar\">\n" +
            "           \n" +
            " \n" +
            "         <a href=\"/xwiki/bin/view/XWiki/adminPolx\" target=\"_self\" title=\"Paul Libbrecht (admin)\" class=\"photo\">\n" +
            "               <img src=\"/xwiki/bin/download/XWiki/adminPolx/MoiAutoroute.jpeg?width=96\" class=\"photo\" width=\"48\">          </a>\n" +
            "       </div>\n" +
            "      <div class=\"blog-comment-line\">\n" +
            "      <p><span class=\"date\">\n" +
            "        <span class=\"wikilink\"><a href=\"/xwiki/bin/view/XWiki/adminPolx\">Paul Libbrecht (admin)</a></span> | <span class=\"blog-comment-date\"></span>\n" +
            "          <a class=\"right\" href=\"/xwiki/bin/view/Groups/MessageDeleteCommentService?docname=Messages_Group_testgroup2.asdad&amp;date=$%7Bdoc.getValue('date', $comment).time%7D\" title=\"Delete\" onclick=\"return updateHref(this, confirm('Delete comment?'));\">Delete</a>           </span></p>\n" +
            "      </div>\n" +
            "          </div>\n" +
            "              <hr class=\"my-curriki-hr\">\n" +
            "                <div id=\"comment1\" class=\"blog-comment odd-row\">\n" +
            "      <div class=\"blog-comment-avatar\">\n" +
            "           \n" +
            " \n" +
            "         <a href=\"/xwiki/bin/view/XWiki/adminPolx\" target=\"_self\" title=\"Paul Libbrecht (admin)\" class=\"photo\">\n" +
            "               <img src=\"/xwiki/bin/download/XWiki/adminPolx/MoiAutoroute.jpeg?width=96\" class=\"photo\" width=\"48\">          </a>\n" +
            "       </div>\n" +
            "      <div class=\"blog-comment-line\">\n" +
            "      <p><span class=\"date\">\n" +
            "        <span class=\"wikilink\"><a href=\"/xwiki/bin/view/XWiki/adminPolx\">Paul Libbrecht (admin)</a></span> | <span class=\"blog-comment-date\">Jul-10-2014</span>\n" +
            "          <a class=\"right\" href=\"/xwiki/bin/view/Groups/MessageDeleteCommentService?docname=Messages_Group_testgroup2.asdad&amp;date=1405025669000\" title=\"Delete\" onclick=\"return updateHref(this, confirm('Delete comment?'));\">Delete</a>           </span></p>\n" +
            "      </div>\n" +
            "      adasd    </div>\n" +
            "              <hr class=\"my-curriki-hr\">\n" +
            "                <div id=\"comment0\" class=\"blog-comment\">\n" +
            "      <div class=\"blog-comment-avatar\">\n" +
            "           \n" +
            " \n" +
            "         <a href=\"/xwiki/bin/view/XWiki/adminPolx\" target=\"_self\" title=\"Paul Libbrecht (admin)\" class=\"photo\">\n" +
            "               <img src=\"/xwiki/bin/download/XWiki/adminPolx/MoiAutoroute.jpeg?width=96\" class=\"photo\" width=\"48\">          </a>\n" +
            "       </div>\n" +
            "      <div class=\"blog-comment-line\">\n" +
            "      <p><span class=\"date\">\n" +
            "        <span class=\"wikilink\"><a href=\"/xwiki/bin/view/XWiki/adminPolx\">Paul Libbrecht (admin)</a></span> | <span class=\"blog-comment-date\">Jul-10-2014</span>\n" +
            "          <a class=\"right\" href=\"/xwiki/bin/view/Groups/MessageDeleteCommentService?docname=Messages_Group_testgroup2.asdad&amp;date=1405027973000\" title=\"Delete\" onclick=\"return updateHref(this, confirm('Delete comment?'));\">Delete</a>           </span></p>\n" +
            "      </div>\n" +
            "      asdasd    </div>\n" +
            "           <div id=\"commentscontent\" class=\"xwikiintracontent\">\n" +
            "    <div class=\"button-right\"><a class=\"button-orange\" onclick=\"CurrikiApp.formToggle(document.commentAddForm); return false;\" href=\"#\" title=\"Add a Comment\">Add a Comment</a></div>\n" +
            "        <form name=\"commentAddForm\" id=\"commentAddForm\" action=\"/xwiki/bin/commentadd/Messages_Group_testgroup2/asdad\" method=\"post\">\n" +
            "    <hr class=\"my-curriki-hr\">\n" +
            "    <fieldset id=\"commentform\">\n" +
            "      <input name=\"xredirect\" value=\"/xwiki/bin/view/Messages_Group_testgroup2/asdad\" type=\"hidden\">\n" +
            "      <input name=\"XWiki.XWikiComments_author\" value=\"XWiki.adminPolx\" type=\"hidden\">\n" +
            "      <input name=\"XWiki.XWikiComments_date\" value=\"\" type=\"hidden\">\n" +
            "      <div><div class=\"fullScreenEditLinkContainer\"><a title=\"Maximize\" class=\"fullScreenEditLink\">Maximize »</a></div><textarea id=\"XWiki.XWikiComments_comment\" rows=\"5\" name=\"XWiki.XWikiComments_comment\"></textarea></div>\n" +
            "      <div>\n" +
            "        <button class=\"button-orange\" type=\"submit\" onclick=\"return updateXRedirect('commentAddForm', true);\">Save Comment</button>\n" +
            "        <button class=\"button-grey\" type=\"reset\" onclick=\"CurrikiApp.formHide(this.form);\">Cancel</button>\n" +
            "      </div>\n" +
            "    </fieldset>\n" +
            "    </form>\n" +
            "  </div>\n" +
            "      <div class=\"clearfloats\"></div>\n" +
            "     </div>\n" +
            "  </div>\n" +
            "  <a name=\"attachments\"></a>                                  <div class=\"frame\" id=\"section_3\">\n" +
            "            <h3 class=\"titleheader titleheader-blue\">Attachment(s)</h3>\n" +
            "       <div class=\"frame-content\" id=\"section3content\">\n" +
            "      <div id=\"attachmentscontent\" class=\"xwikiintracontent\">\n" +
            "    <div class=\"button-right\"><a class=\"button-orange\" onclick=\"CurrikiApp.formToggle(document.attachmentAddForm, 'filepath')\">Add an Attachment</a></div>\n" +
            "    <form name=\"attachmentAddForm\" id=\"attachmentAddForm\" action=\"/xwiki/bin/upload/Messages_Group_testgroup2/asdad\" enctype=\"multipart/form-data\" method=\"post\">\n" +
            "      <input name=\"form_token\" value=\"hiC5gsLc4n4mvktQI3ZfOg\" type=\"hidden\">\n" +
            "              \n" +
            "      <input name=\"xredirect\" value=\"http://node1.hoplahup.net/xwiki/bin/view/Messages_Group_testgroup2/asdad\" type=\"hidden\">\n" +
            "        \n" +
            "  \n" +
            "      <fieldset id=\"attachform\">\n" +
            "        <div><input id=\"xwikiuploadname\" name=\"filename\" value=\"\" size=\"40\" type=\"hidden\"></div>\n" +
            "        <div><label id=\"xwikiuploadfilelabel\" for=\"xwikiuploadfile\">Choose file to upload:</label></div>\n" +
            "        <div><input id=\"xwikiuploadfile\" name=\"filepath\" value=\"\" size=\"40\" type=\"file\"></div>\n" +
            "        <div>\n" +
            "          <button class=\"button-orange\" type=\"submit\" onclick=\"return updateXRedirect('attachmentAddForm', updateAttachName(this.form, 'Do you want to replace the filename with'))\">Attach This File</button>\n" +
            "          <button class=\"button-grey\" type=\"reset\" onclick=\"CurrikiApp.formHide(this.form);\">Cancel</button>\n" +
            "        </div>\n" +
            "      </fieldset>\n" +
            "    </form>\n" +
            "  </div>\n" +
            "     <div class=\"clearfloats\"></div>\n" +
            "     </div>\n" +
            "  </div>\n" +
            "          \n" +
            "  <script type=\"text/javascript\">\n" +
            "    function isEditMode(){\n" +
            "        var child = document.getElementById('groups-message-edit').firstChild;\n" +
            "        while(child){\n" +
            "            if (child.tagName && child.tagName.toLowerCase()=='form'){\n" +
            "                return true;\n" +
            "            }\n" +
            "            child = child.nextSibling;\n" +
            "        }\n" +
            "        return false;\n" +
            "    }\n" +
            "    function updateXRedirect(formName, returnValue){\n" +
            "        var xredirect = document.forms[formName].xredirect;\n" +
            "        if (isEditMode()){\n" +
            "            xredirect.value = '/xwiki/bin/view/Messages_Group_testgroup2/asdad?edit=1';\n" +
            "        }else{\n" +
            "            xredirect.value = '/xwiki/bin/view/Messages_Group_testgroup2/asdad';\n" +
            "        }\n" +
            "        return returnValue;\n" +
            "    }\n" +
            "    function updateHref(anchor, returnValue){\n" +
            "        if (isEditMode()){\n" +
            "            anchor.href += '&edit=1';\n" +
            "        }\n" +
            "        anchor.href += '&confirm=1';\n" +
            "        return returnValue;\n" +
            "    }\n" +
            "  </script>\n" +
            "    \n" +
            "  \n" +
            "   </div>\n" +
            "</div>\n";
}
