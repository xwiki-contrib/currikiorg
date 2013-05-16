//
Ajax.XWikiRequest = Class.create(Ajax.Request, {
  initialize: function($super, space, docName, options, action) {
    this.transport = Ajax.getTransport();
    if (action)
      this.action = action;
    else
      this.action = "view";
    this.baseUrl = "/xwiki/bin/" + this.action;

    options = Object.clone(options);
    var onComplete = options.onComplete || Prototype.emptyFunction;
    options.onComplete = (function() {
       this.returnValue(onComplete);
       //onComplete(this.transport);
    }).bind(this);

    $super(this.generateUrl(space, docName), options);
  },
  generateUrl: function(space, docName){
    return this.baseUrl + "/" + space + "/" + docName;
  },
  returnValue: function(callBack) {
    if (callBack) {
      callBack(this);
    } else {
      alert("error, callback");
    }
  }
});
//
function refreshPage(ajaxreq) {
  alert(ajaxreq.transport.responseText);
  window.location.href = window.location.href;
}

function editWelcomeBlock(spaceName,pageName,divid) {
  // The user can click on the edit link even when the editor is loaded so it's better to release the existing editor.
  releaseWysiwygEditor('welcomeWysiwyg');
  var pars = "space=" + spaceName + "&page=" + pageName + "&divid=" + divid + "&xpage=plain";
  $(divid).innerHTML = "<p>${msg.groups_loadinginprogress}</p>";
  // call url to get the edit html to edit the profile
  var myAjax = new Ajax.XWikiRequest( "Groups", "EditWelcomeBlockService", {method: 'get', parameters: pars, onComplete: editWelcomeBlockCallback, divid: divid });
}
function editWelcomeBlockCallback(ajaxreq) {
  var divid = ajaxreq.options.divid;
  //$(divid).innerHTML = ajaxreq.transport.responseText;
  $(divid).update(ajaxreq.transport.responseText);
  //if (typeof tinyMCE != 'undefined') {
  //tinyMCE.addMCEControl(document.getElementById("XWiki.CurrikiWelcomeBlockClass_0_content"), "XWiki.CurrikiWelcomeBlockClass_0_content");
  //} else {
  //createWysiwygEditor('welcomeWysiwyg', welcomeWysiwygConfig);
  //}
  //new WysiwygEditor({hookId:'XWiki.CurrikiWelcomeBlockClass_0_content'});
}
function cancelEditWelcomeBlock(spaceName,pageName,divid) {
 if (confirm(i18nDict['groups_welcomeblock_confirmcancel'])) {
   if(Prototype.Browser.IE) {
    history.go(0); 
   } else {
    releaseWysiwygEditor('welcomeWysiwyg');
    // We give the WYSIWYG editor a chance to execute its deferred updates before removing it from the document. This
    // timeout shouldn't be necessary and will be removed in the future.
    setTimeout(function() {
     var pars = "space=" + spaceName + "&page=" + pageName + "&divid=" + divid + "&xpage=plain";
     $(divid).innerHTML = "<p>"+i18nDict['groups_loadinginprogress']+"</p>";
     // call url to get the edit html to edit the profile
     var myAjax = new Ajax.XWikiRequest( "Groups", "ViewWelcomeBlockService", {method: 'get', parameters: pars, onComplete: cancelEditWelcomeBlockCallback, divid: divid });
    }, 10);
  }
 }
}
function cancelEditWelcomeBlockCallback(ajaxreq) {
 var divid = ajaxreq.options.divid;
 $(divid).innerHTML = ajaxreq.transport.responseText;
}
function beforeSubmitEditWelcomeBlock() {
 if (typeof tinyMCE != 'undefined') {
  document.forms['wblockform']['XWiki.CurrikiWelcomeBlockClass_0_content'].value = tinyMCE.getContent();
 } else {
  // We have to release the editor before submitting the form. The editor will be replaced by the response.
  releaseWysiwygEditor('welcomeWysiwyg');
 }
}
// With the new WYSIWYG editor it is possible to experience conversion exceptions (from HTML to wiki syntax) in which 
// case we must reopen the editor to prevent the user from loosing unsaved changes.
function afterSubmitEditWelcomeBlock(transport, divid) {
 $(divid).innerHTML = transport.responseText;
 // Check if the save failed. This test is not bullet proof: the user can add the specified form in the welcome block.
 if (document.forms['wblockform']) {
  createWysiwygEditor('welcomeWysiwyg', welcomeWysiwygConfig);
 }
}
// Creates a new WYSIWYG editor based on the given configuration and stores it in a window property with the specified name.
function createWysiwygEditor(name, config) {
 if (typeof Wysiwyg != 'undefined') {
  Wysiwyg.onModuleLoad(function() {
   // We have to update the input URL because the user can edit/save multiple times without reloading the page.
   // The input URL is taken from the edit form.
   config.inputURL = document.getElementById('welcomeInputURL').value;
   window[name] = new WysiwygEditor(config);
  });
 }
}
// Release the JavaScript objects and the event listeners used by the WYSIWYG editor to prevent memory leaks.
function releaseWysiwygEditor(name) {
 if (window[name]) {
  window[name].release();
  window[name] = undefined;
 }
}
function checkFileExtension() {
  var isValid = true;
  var ext = getFileExtension();
  if (ext) {
    if (ext != "ok" && ext != "ai" && ext != "gif" && ext != "jpg" && ext != "tif" && ext != "bmp" && ext != "jpe" && ext != "psd" && ext != "png") {
      isValid = false;
    }
  } else {
    isValid = false;
  }
  if (!isValid) {
    alert(i18nDict['mycurriki.profile.needPicture']);
  }
  return isValid;
}
function getFileExtension() {
  var fileName = document.getElementById("xwikiuploadfile").value;
  if (fileName.length){
   fileName = fileName.toLowerCase();
   var pos = fileName.lastIndexOf(".");
   if (pos > 0){
     return fileName.substring(pos + 1);
   }
  }
  return "ok";
}
function editGroupInfo(spaceName,divid) {
  var pars = "space=" + spaceName + "&divid=" + divid + "&xpage=plain";
  $(divid).innerHTML = "<p>${msg.groups_loadinginprogress}</p>";
  // call url to get the edit html to edit the profile
  var myAjax = new Ajax.XWikiRequest( "Groups", "EditGroupInfoService", {method: 'get', parameters: pars, onComplete: editGroupInfoCallback, divid: divid });
}
function editGroupInfoCallback(ajaxreq) {
 var divid = ajaxreq.options.divid;
 $(divid).innerHTML = ajaxreq.transport.responseText;
}
function cancelEditGroupInfo(spaceName,divid) {
  if (confirm(i18nDict['groups_welcomeblock_confirmcancel'])) {
   var pars = "space=" + spaceName + "&divid=" + divid + "&xpage=plain";
   $(divid).innerHTML = "<p>"+i18nDict['groups_loadinginprogress']+"</p>";
   // call url to get the edit html to edit the profile
   var myAjax = new Ajax.XWikiRequest( "Groups", "ViewGroupInfoService", {method: 'get', parameters: pars, onComplete: cancelEditGroupInfoCallback, divid: divid });
  }
}
function cancelEditGroupInfoCallback(ajaxreq) {
 var divid = ajaxreq.options.divid;
 $(divid).innerHTML = ajaxreq.transport.responseText;
}
function editProfile(memberName, groupName, divid) {
  var pars = "user=" + memberName + "&group=" + groupName + "&divid=" + divid + "&xpage=plain";
  $(divid).innerHTML = "<p>"+i18nDict['msg.groups_members_editsettings_loadinginprogress']+"</p>";
  // call url to get the edit html to edit the profile
  var myAjax = new Ajax.XWikiRequest( "Groups", "EditUserProfileService", {method: 'get', parameters: pars, onComplete: editProfileCallback, divid: divid });
  return false;
}
function editProfileCallback(ajaxreq) {
 var divid = ajaxreq.options.divid;
 $(divid).innerHTML = ajaxreq.transport.responseText;
 $(divid).parentNode.className = "groups-members-member group-members-edited";
}
function cancelEditProfile(memberName, groupName, divid) {
  var pars = "user=" + memberName + "&group=" + groupName + "&divid=" + divid + "&xpage=plain";
  $(divid).innerHTML = "<p>"+i18nDict['msg.groups_members_editsettings_loadinginprogress']+"</p>";
  // call url to get the edit html to edit the profile
  var myAjax = new Ajax.XWikiRequest( "Groups", "ViewUserProfileService", {method: 'get', parameters: pars, onComplete: cancelEditProfileCallback, divid: divid });
  return false;
}
function cancelEditProfileCallback(ajaxreq) {
 var divid = ajaxreq.options.divid;
 $(divid).innerHTML = ajaxreq.transport.responseText;
 $(divid).parentNode.className = "groups-members-member";
}
function saveProfileCallback(ajaxreq) {
 var divid = ajaxreq.options.divid;
 $(divid).parentNode.className = "groups-members-member";
}
function removeAdmin(memberName, groupName) {
  var pars = "user=" + memberName + "&group=" + groupName  + "&action=demote&xpage=plain";
  // call url to remove admin
  var myAjax = new Ajax.XWikiRequest( "Groups", "MembersService", {method: 'get', parameters: pars, onComplete: refreshPage});
  return false;
}
function addAdmin(memberName, groupName) {
  var pars = "user=" + memberName + "&group=" + groupName  + "&action=promote&xpage=plain";
  // call url to remove admin
  var myAjax = new Ajax.XWikiRequest( "Groups", "MembersService", {method: 'get', parameters: pars, onComplete: refreshPage});
  return false;
}
function removeMember(memberName, groupName) {
  var pars = "user=" + memberName + "&group=" + groupName  + "&action=remove&xpage=plain";
  // call url to remove admin
  var myAjax = new Ajax.XWikiRequest( "Groups", "MembersService", {method: 'get', parameters: pars, onComplete: refreshPage});
  return false;
}
// Groups membership request JS
function acceptRequest(divid, groupName, memberName, displayName) {
      $(divid).oldInnerHTML = $(divid).innerHTML;
      $(divid).innerHTML = "<div class=\"groups-members-request-confirm-box \"><form><p>${msg.groups_members_accept_confirm}</p><br />"
                                                 + "<div class=\"button-right\"><input type=\"button\" class=\"button button-cancel\" value=\"${msg.groups_members_cancel_btt}\""
                                                 + " onclick=\"acceptRequestCancel('" + divid + "','" + groupName + "','" + memberName + "');\" />"
                                                + "<input type=\"button\" class=\"button button-confirm\" value=\"${msg.groups_members_accept_confirm_btt}\""
                                                + " onclick=\"acceptRequestConfirm('" + divid + "','" + groupName + "','" + memberName + "');\" /></div>"
                                                + "</form></div>";
      $(divid).parentNode.className = "groups-members-request group-members-request-confirm";
}
function acceptRequestCancel(divid, groupName, memberName) {
      $(divid).innerHTML = $(divid).oldInnerHTML;
      $(divid).parentNode.className = "groups-members-request";
}
function acceptRequestConfirm(divid, groupName, memberName) {
      var pars = "user=" + memberName + "&group=" + groupName + "&xpage=plain";
      $(divid).innerHTML = "<p>${msg.groups_members_accept_inprogress}</p>";
      // call url to accept the request.. check the return is "SUCCESS" other wise show output
      var myAjax = new Ajax.XWikiRequest( "Groups", "AcceptMembershipRequestService", {method: 'get', parameters: pars, onComplete: acceptRequestCallback, divid: divid });
}
function acceptRequestCallback(ajaxreq) {
    // hide div to show success full acceptance for user
    var result = ajaxreq.transport.responseText;
    // alert(result);
    var divid = ajaxreq.options.divid;
    if (result.strip()=="SUCCESS")
     $(divid).innerHTML = "<p>${msg.groups_members_accept_done}</p>";
    else
     $(divid).innerHTML = "<p>${msg.groups_members_accept_failed}</p>" + "<p>" + result + "</p>";
     $(divid).parentNode.className = "groups-members-request";
}
function rejectRequest(divid, groupName, memberName) {
      $(divid).oldInnerHTML = $(divid).innerHTML;
      $(divid).innerHTML = "<div class=\"groups-members-request-confirm-box\"><form><p>${msg.groups_members_reject_confirm}</p><textarea id=\"" + divid + "-reason\" cols=\"60\" rows=\"7\"></textarea><br />"
                                                + "<div class=\"button-right\"><input type=\"button\" class=\"button button-cancel\" value=\"${msg.groups_members_cancel_btt}\""
                                                + " onclick=\"rejectRequestCancel('" + divid + "','" + groupName + "','"  + memberName + "');\" />"
                                                + "<input type=\"button\" class=\"button button-confirm\" value=\"${msg.groups_members_reject_confirm_btt}\""
                                                + " onclick=\"rejectRequestConfirm('" + divid + "','" + groupName + "','" + memberName + "');\" /></div>"
                                                + "</form></div>";
      $(divid).parentNode.className = "groups-members-request group-members-request-confirm";
}
function rejectRequestCancel(divid, groupName, memberName, displayName) {
      $(divid).innerHTML = $(divid).oldInnerHTML;
      $(divid).parentNode.className = "groups-members-request";
}
function rejectRequestConfirm(divid, groupName, memberName) {
      var reason = encodeURIComponent($(divid+"-reason").value);
      var pars = "user=" + memberName + "&reason=" + reason + "&group=" + groupName + "&xpage=plain";
      $(divid).innerHTML = "<p>${msg.groups_members_reject_inprogress}</p>";
      // call url to accept the request.. check the return is "SUCCESS" other wise show output
      var myAjax = new Ajax.XWikiRequest( "Groups", "RejectMembershipRequestService", {method: 'get', parameters: pars, onComplete: rejectRequestCallback, divid: divid});
}
function rejectRequestCallback(ajaxreq) {
    // hide div to show success full acceptance for user
    var result = ajaxreq.transport.responseText;
    // alert(result);
    var divid = ajaxreq.options.divid;
    if (result.strip()=="SUCCESS")
     $(divid).innerHTML = "<p>${msg.groups_members_reject_done}</p>";
    else
     $(divid).innerHTML = "<p>${msg.groups_members_reject_failed}</p>" + "<p>" + result + "</p>";
    $(divid).parentNode.className = "groups-members-request";
}
// Groups invitations
function cancelInvitation(invitee, groupName, key){
	if (!window.confirm(i18nDict['groups_members_admin_invitations_cancel_confirm'])){
		return;
	}
	var pars = "user=" + invitee + "&group=" + groupName + "&key=" + key  + "&action=cancel&xpage=plain";
  // call url to cancel invitation
  var myAjax = new Ajax.XWikiRequest( "Groups", "MembersService", {method: 'get', parameters: pars, onComplete: refreshPage});
  return false;
}
// Groups macros
function checkFileExtension() {
  var isValid = true;
  var ext = getFileExtension();
  if (ext) {
    if (ext != "ok" && ext != "ai" && ext != "gif" && ext != "jpg" && ext != "tif" && ext != "bmp" && ext != "jpe" && ext != "psd" && ext != "png") {
      isValid = false;
    }
  } else {
    isValid = false;
  }
  if (!isValid) {
    alert(i18nDict['mycurriki.profile.needPicture']);
  }
  return isValid;
}
function getFileExtension() {
  var fileName = document.getElementById("xwikiuploadfile").value;
  if (fileName.length){
   fileName = fileName.toLowerCase();
   var pos = fileName.lastIndexOf(".");
   if (pos > 0){
     return fileName.substring(pos + 1);
   }
  }
  return "ok";
}