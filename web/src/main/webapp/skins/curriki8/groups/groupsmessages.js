//
function checkForm(formName) {
  var isValid = true;
  var title = document.forms[formName]['XWiki.ArticleClass_0_title'].value;
  if (title.length <= 1) {
    alert(_("groups_messages_create_mandatory"));
    isValid = false;
  }
  if (isValid && !saveToList()){
    alert(_("groups_messages_create_to_mandatory"));
    isValid = false;
  }
  return isValid;
}
//
function displayAddLink(selectObj, linkId){
  var linkObj = document.getElementById(linkId);
  if (selectObj.value!=''){
    linkObj.style.display='';
  } else {
    linkObj.style.display='none';
  }
}
//
function addToList(addLink, selectId, divId){
  var selectObj = document.getElementById(selectId);
  if (selectObj.selectedIndex < 0) {
    return;
  }
  var option = selectObj.options[selectObj.selectedIndex];
  if (option.value.length == 0) {
    return;
  }
  var getClass=document.getElementsByClassName(divId);
  var menu=getClass[0].getElementsByTagName('td');
  var tr = document.createElement('tr');
  tr.className=divId;
  var td1 = document.createElement('td');
  var td2 = document.createElement('td');
  var td3 = document.createElement('td');
  getClass[0].parentNode.insertBefore(tr, getClass[0]);
  tr.appendChild(td1);
  moveChildNodes(menu[0], td1);
  tr.appendChild(td2);
  tr.appendChild(td3);

  var deleteLink = document.createElement('a');
  deleteLink.appendChild(document.createTextNode('Delete'));
  deleteLink.href = 'javascript:void()';
  if (selectId == 'rolesList') {
    deleteLink.title = '${msg.get('groups_messages_create_to_delete_role_tooltip')}';
  } else if (selectId == 'membersList') {
    deleteLink.title = '${msg.get('groups_messages_create_to_delete_member_tooltip')}';
  } else {
    deleteLink.title = '${msg.get('groups_messages_create_to_delete_tooltip')}';
  }
  deleteLink.onclick = function(){
    if (!selectObj.disabled){
      var parentTr=this.parentNode.parentNode;
      var getClass=document.getElementsByClassName(parentTr.className);
      if(parentTr==getClass[0]){
        moveChildNodes(parentTr.getElementsByTagName('td')[0],getClass[1].getElementsByTagName('td')[0]);
      }
      this.parentNode.parentNode.parentNode.removeChild(this.parentNode.parentNode);
      option.style.display = '';
      option.disabled = false;
    }
    return false;
  };
  td2.appendChild(document.createTextNode(option.text));
  td3.appendChild(deleteLink);
  option.style.display = 'none';
  option.disabled = true;
  selectObj.selectedIndex = 0;
  selectObj.onchange();
}
//
function getInputs(parent, type){
  var inputs = parent.getElementsByTagName('input');
  var result = [];
  for(var i=0; i<inputs.length; i++){
    if (inputs[i].type == type){
      result.push(inputs[i]);
    }
  }
  return result;
}
//
function moveChildNodes(fromParent, toParent){
  var checkboxes = getInputs(fromParent, 'checkbox');
  var checked = new Array(checkboxes.length);
  for(var i=0; i<checkboxes.length; i++){
    checked[i] = checkboxes[i].checked;
  }
  var child = fromParent.firstChild;
  while(child){
    var nextSibling = child.nextSibling;
    toParent.appendChild(child);
    child = nextSibling;
  }
  checkboxes = getInputs(toParent, 'checkbox');
  for(var i=0; i<checkboxes.length; i++){
    checkboxes[i].checked = checked[i];
  }
}
//
function enableSelect(checkbox, selectId){
  var selectObj = document.getElementById(selectId);
  selectObj.selectedIndex = 0;
  selectObj.disabled = !checkbox.checked;
  selectObj.onchange();
}
//
function getSelectedOptions(select){
  var selectedOptions = [];
  for(var i=0; i<select.options.length; i++){
    if (select.options[i].style.display == 'none'){
      selectedOptions.push(select.options[i].value);
    }
  }
  return selectedOptions;
}
//
function saveSelectedOptions(checkBoxId, selectId, hiddenId){
  if (document.getElementById(checkBoxId).checked){
    var select = document.getElementById(selectId);
    var selectedOptions = getSelectedOptions(select);
    if (selectedOptions.length == 0){
      return false;
    }
    document.getElementById(hiddenId).value = selectedOptions.join(',');
    return true;
  }
  return false;
}
//
function saveToList(){
  var rolesSaved = saveSelectedOptions('toRole', 'rolesList', 'selectedRolesList');
  var membersSaved = saveSelectedOptions('toMember', 'membersList', 'selectedMembersList');
  var toGroup = document.getElementById('toGroup').checked;
  return toGroup || rolesSaved || membersSaved;
}
//
function preFillList(addLinkId, selectId, divId, items){
  var select = document.getElementById(selectId);
  var addLink = document.getElementById(addLinkId);
  for(var i=0; i<select.options.length; i++){
    var option = select.options[i];
    if (inArray(option.value, items)){
      select.selectedIndex = option.index;
      addToList(addLink, selectId, divId);
    }
  }
}
//
function inArray(item, items){
  for(var i=0; i<items.length; i++){
    if (items[i] == item){
      return true;
    }
  }
  return false;
}
//
function editMessage(spaceName,pageName,divid) {
  var pars = "space=" + spaceName + "&page=" + pageName + "&divid=" + divid + "&xpage=plain";
  $(divid).innerHTML = '<p>'+_('groups_loadinginprogress')+'</p>';
  // call url to get the edit html to edit the profile
  var myAjax = new Ajax.XWikiRequest( "Groups", "EditMessageService", {method: 'get', parameters: pars, onComplete: editMessageCallback, divid: divid});
  //var myAjax = new Ajax.Updater($(divid), "$xwiki.getURL('Groups.EditMessageService', 'view')", {
  //  method: 'GET',
  //  parameters: pars,
  //  evalScripts: true
  //  });
}
//
function editMessageCallback(ajaxreq) {
  //var divid = ajaxreq.options.divid;
  //$(divid).innerHTML = ajaxreq.transport.responseText;
  //var scripts = $(divid).getElementsByTagName('script');
  //for(var i=0; i<scripts.length; i++){
  //  var script = scripts[i];
  //  try{
  //    eval(script.innerHTML);
  //  }catch(e){
  //    // ignore
  //  }
  //}
  //tinyMCE.addMCEControl(document.getElementById("XWiki.ArticleClass_0_content"), "XWiki.ArticleClass_0_content");
  $(ajaxreq.options.divid).update(ajaxreq.transport.responseText);
  setViewMode(false);
  // activate wysiwyg
  new XWiki.widgets.FullScreen(); 
}
//
function cancelEditMessage(spaceName,pageName,divid) {
  var pars = "space=" + spaceName + "&page=" + pageName + "&divid=" + divid + "&xpage=plain";
  $(divid).innerHTML = "<p>${msg.groups_loadinginprogress}</p>";
  // call url to get the edit html to edit the profile
  var myAjax = new Ajax.XWikiRequest( "Groups", "ViewMessageService", {method: 'get', parameters: pars, onComplete: cancelEditMessageCallback, divid: divid });
}
//
function cancelEditMessageCallback(ajaxreq) {
  var divid = ajaxreq.options.divid;
  $(divid).innerHTML = ajaxreq.transport.responseText;
  setViewMode(true);
}
//
function deleteMessage(spaceName,pageName,xredirect) {
  if (confirm(_('groups_messages_view_message_delete_confirm'))) {
    var pars = "confirm=1&ajax=1&xredirect=" + xredirect;
    // call url to get the edit html to edit the profile
    var myAjax = new Ajax.XWikiRequest( spaceName, pageName, {method: 'get', parameters: pars, onComplete: deleteMessageCallback }, "delete");
  }
}
//
function deleteMessageCallback(ajaxreq) {
  alert(_('groups_messages_view_message_delete_done'));
  location= ajaxreq.parameters.xredirect;
}
//
function setViewMode(viewMode){
  var display = 'none';
  if (viewMode){
    display = 'block';
  }
  // show / hide comments
  document.getElementById('section_2').style.display = display;
  // show / hide attachments
  document.getElementById('section_3').style.display = display;
  // show / hide edit link
  document.getElementById('editLinkContainer').style.display = viewMode ? 'inline':'none';
}
//
function onAfterSave(ajaxreq){
  setViewMode(true);
  updateSectionTitle(ajaxreq);
}
function updateSectionTitle(ajaxreq){
  var h3s = document.getElementById('section_1').getElementsByTagName('h3');
  if (h3s.length == 0){
    return;
  }
  var sectionTitle = h3s[0];
  var tmpDiv = document.createElement('div');
  tmpDiv.innerHTML = ajaxreq.responseText;
  var ps = tmpDiv.getElementsByTagName('p');
  for(var i=0; i<ps.length; i++){
    if (ps[i].className == 'frame-title'){
      sectionTitle.innerHTML = ps[i].innerHTML;
    }
  }
}
