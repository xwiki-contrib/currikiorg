var XWiki = (function (XWiki) {
// Start XWiki augmentation.
var viewers = XWiki.viewers = XWiki.viewers || {};
/**
 * Javascript enhancements for the comments viewer.
 */
viewers.Comments = Class.create({
  xcommentSelector : ".xwikicomment",
  /** Constructor. Adds all the JS improvements of the Comments area. */
  initialize: function(convContainer) {
    if (!convContainer || typeof(convContainer) == "undefined") {
      // if we don't have a container, do nothing, this is to prevent the default behaviour of the comments enhancer
      return false;
    }
    this.conversation = convContainer;
    var conversationId = this.conversation.readAttribute('id');
    if (conversationId) {
      this.xcommentSelector = "#" + conversationId + " " + this.xcommentSelector;
    }

    if (this.conversation.down(".commentscontent")) {
      // If the comments area is already visible, enhance it.
      this.startup();
      this.addConversationHandlers();
    }
    if ($("Commentstab")) {
      this.container = $("Commentspane");
      this.generatorTemplate = "conversationscommentsinline.vm";
    } else if ($$(".main.layoutsubsection").size() > 0 && $$(".main.layoutsubsection").first().down("#commentscontent")) {
      this.container = $$(".main.layoutsubsection").first();
      this.generatorTemplate = "conversationscomments.vm";
    }

    // don't know yet what is the container used for
    this.container = this.conversation.down('.commentscontent');
    this.generatorTemplate = 'conversations.vm';

    // We wait for a notification for the AJAX loading of the Comments metadata tab.
    this.addTabLoadListener();
  },
  /** Enhance the Comments UI with JS behaviors. */
  startup : function() {
    if (this.conversation.down(".commentform")) {
      this.form = this.conversation.down(".commentform").up('form');
    } else {
      this.form = undefined;
    }
    this.loadIDs();
    this.addDeleteListener();
    this.addReplyListener();
    this.addPermalinkListener();
    this.addSubmitListener(this.form);
    this.addCancelListener();
    this.addEditListener();
    this.addPreview(this.form);
  },
  addConversationHandlers : function() {
    this.addConversationDeleteListener();
    this.addConversationEditListener();
    this.addConversationHideListener();
    this.addConversationLikeListener();
    this.addConversationPermalinkListener();
  },
  /**
   * Parse the IDs of the comments to obtain the xobject number.
   */
  loadIDs : function() {
    $$(this.xcommentSelector).each(function(item) {
      var elementId = item.id;
      item._x_number = elementId.substring(elementId.lastIndexOf("_") + 1) - 0;
    });
  },
  /**
   * Ajax comment deletion.
   * For all delete buttons, listen to "click", and make ajax request to remove the comment. Remove the corresponding
   * HTML element on succes (replace it with a small notification message). Display error message (alert) on failure.
   */
  addDeleteListener : function() {
    $$(this.xcommentSelector).each(function(item) {
      // Prototype bug in Opera: $$(".comment a.delete") returns only the first result.
      // Quick fix until Prototype 1.6.1 is integrated.
      item = item.down('a.delete');
      if (!item) {
        return;
      }
      item.observe('click', function(event) {
        item.blur();
        event.stop();
        if (item.disabled) {
          // Do nothing if the button was already clicked and it's waiting for a response from the server.
          return;
        } else {
          new XWiki.widgets.ConfirmedAjaxRequest(
            /* Ajax request URL */
            item.readAttribute('href') + (Prototype.Browser.Opera ? "" : "&ajax=1"),
            /* Ajax request parameters */
            {
              onCreate : function() {
                // Disable the button, to avoid a cascade of clicks from impatient users
                item.disabled = true;
              },
              onSuccess : function() {
                // Remove the corresponding HTML element from the UI and update the comment count
                var comment = item.up(this.xcommentSelector);
                // If the form is inside this comment's reply thread, move it back to the bottom.
                if (this.form && this.form.descendantOf(comment.next('.commentthread'))) {
                  this.resetForm();
                }
                // Replace the comment with a "deleted comment" placeholder
                comment.replace(this.createNotification("$msg.get('core.viewers.comments.commentDeleted')"));
                this.updateCount();
              }.bind(this),
              onComplete : function() {
                // In the end: re-enable the button
                item.disabled = false;
              }
            },
            /* Interaction parameters */
            {
               confirmationText: "$msg.get('core.viewers.comments.delete.confirm')",
               progressMessageText : "$msg.get('core.viewers.comments.delete.inProgress')",
               successMessageText : "$msg.get('core.viewers.comments.delete.done')",
               failureMessageText : "$msg.get('core.viewers.comments.delete.failed')"
            }
          );
        }
      }.bindAsEventListener(this));
    }.bind(this));
  },
  /**
   * Ajax comment editing.
   * For all edit buttons, listen to "click", and make ajax request to retrieve the form and save the comment.
   */
  addEditListener : function() {
    $$(this.xcommentSelector).each(function(item) {
      // Prototype bug in Opera: $$(".comment a.delete") returns only the first result.
      // Quick fix until Prototype 1.6.1 is integrated.
      item = item.down('a.edit');
      if (!item) {
        return;
      }
      item.observe('click', function(event) {
        item.blur();
        event.stop();
        if (item.disabled) {
          // Do nothing if the button was already clicked and it's waiting for a response from the server.
          return;
        } else if (item._x_editForm){
          // If the form was already fetched, but hidden after cancel, just show it again
          // without making a new request
          var comment = item.up(this.xcommentSelector);
          comment.hide();
          item._x_editForm.show();
        } else {
          new Ajax.Request(
            /* Ajax request URL */
            item.readAttribute('href').replace('viewer=conversationscomments', 'xpage=xpart&vm=conversationscommentsinline.vm'),
            /* Ajax request parameters */
            {
              onCreate : function() {
                // Disable the button, to avoid a cascade of clicks from impatient users
                item.disabled = true;
                item._x_notification = new XWiki.widgets.Notification("$msg.get('core.viewers.comments.editForm.fetch.inProgress')", "inprogress");
              },
              onSuccess : function(response) {
                // Hide other comment editing forms (allow only one comment to be edited at a time)
                if (this.editing) {
                  this.cancelEdit(false, this.editing);
                }
                // Replace the comment text with a form for editing it
                var comment = item.up(this.xcommentSelector);
                comment.insert({before: response.responseText});
                item._x_editForm = comment.previous();
                this.addSubmitListener(item._x_editForm);
                this.addPreview(item._x_editForm);
                item._x_editForm.down('a.cancel').observe('click', this.cancelEdit.bindAsEventListener(this, item));
                comment.hide();
                item._x_notification.hide();
                // Currently editing: this comment
                this.editing = item;
              }.bind(this),
              onFailure : function (response) {
                var failureReason = response.statusText;
                if (response.statusText == '' /* No response */ || response.status == 12031 /* In IE */) {
                  failureReason = 'Server not responding';
                }
                item._x_notification.replace(new XWiki.widgets.Notification("$msg.get('core.viewers.comments.editForm.fetch.failed')" + failureReason, "error"));
              }.bind(this),
              on0 : function (response) {
                response.request.options.onFailure(response);
              },
              onComplete : function() {
                // In the end: re-enable the button
                item.disabled = false;
              }
            }
          );
        }
      }.bindAsEventListener(this));
    }.bind(this));
  },
  /**
   * Cancel edit
   */
  cancelEdit : function (event, editActivator) {
    if (event) {
      event.stop();
    }
    var comment = editActivator.up(this.xcommentSelector);
    editActivator._x_editForm.hide();
    comment.show();
    this.cancelPreview(editActivator._x_editForm);
    this.editing = false;
  },
  /**
   * Inline reply: Move the form under the replied comment and update the hidden "replyto" field.
   */
  addReplyListener : function() {
    if (this.form) {
      $$(this.xcommentSelector).each(function(item) {
        // Prototype bug in Opera: $$(".comment a.commentreply") returns only the first result.
        // Quick fix until Prototype 1.6.1 is integrated.
        item = item.down('a.commentreply');
        if (!item) {
          return;
        }
        item.observe('click', function(event) {
          item.blur();
          event.stop();
          // If the form was already displayed as a reply, re-enable the Reply button for the old location
          if (this.form.up('.commentthread')) {
            this.form.up(".commentthread").previous(this.xcommentSelector).down('a.commentreply').show();
          }

         // Before moving the editor we need to unload the wysiwyg editor
         var tarea = this.form["XWiki.XWikiComments_comment"];
         tarea.wysiwyg.release()
         tarea.previous().remove()

          // Insert the form on top of that comment's discussion
         item.up(this.xcommentSelector).next('.commentthread').insert({'top' : this.form});

         // now we can reload the editor
         tarea.wysiwyg = new WysiwygEditor(WysiwygConfig[tarea.id]);
         XWiki.widgets.fs.addBehavior(this.form.down(".xRichTextEditor"));

          // Set the replyto field to the replied comment's number
          this.form["XWiki.XWikiComments_replyto"].value = item.up(this.xcommentSelector)._x_number;
          // Clear the contents and focus the textarea
          this.form["XWiki.XWikiComments_comment"].value = "";
          // this.form["XWiki.XWikiComments_comment"].focus();
          // Hide the reply button
          item.hide();
        }.bindAsEventListener(this));
      }.bind(this));
    } else {
      // If, for some reason, the form is missing, hide the reply functionality from the user
      $$(this.xcommentSelector + ' a.commentreply').each(function(item) {
        item.hide();
      });
    }
  },
  /**
   * Permalink: Display a modal popup providing the permalink.
   */
  addPermalinkListener : function() {
    $$(this.xcommentSelector + ' a.permalink').each(function(item) {
      item.observe('click', function(event) {
        item.blur();
        event.stop();
        var permalinkBox = new XWiki.widgets.ConfirmationBox(
        {
          onYes : function () {
            window.location = item.href;
          }
        },
        /* Interaction parameters */
        {
          confirmationText: "$msg.get('core.viewers.comments.permalink'): <input type='text' class='full' value='" + item.href + "'/>",
          yesButtonText: "$msg.get('core.viewers.comments.permalink.goto')",
          noButtonText : "$msg.get('core.viewers.comments.permalink.hide')"
        });
        permalinkBox.dialog.addClassName('permalinkBox')
        permalinkBox.dialog.down('input[type="text"]').select();
      });
    });
  },
  /**
   * When pressing Submit, check that the comment is not empty. Submit the form with ajax and update the whole comments
   * zone on success.
   *
   * We customize this function only to handle the redirect for the comment edit form, which is wrongly being overwritten with a URL to the current document
   */
  addSubmitListener : function(form) {
    if (form) {
      // Add listener for submit
      form.down("input[type='submit']").observe('click', function(event) {
        event.stop();
        if (form.down('textarea').value != "") {
          var formData = new Hash(form.serialize(true));
          // only overwrite the xredirect if it's not already set. In any case, add comment and add reply don't use this redirect, they use the xpage and vm under
          if(!formData.get('xredirect')) {
            formData.set('xredirect', window.docgeturl + '?xpage=xpart&vm=' + this.generatorTemplate);
          }
          // Allows CommentAddAction to parse a template which will return a message telling if the captcha was wrong.
          formData.set('xpage', 'xpart');
          formData.set('vm', this.generatorTemplate);
          // Strip whatever query string is supplied by the form so it doesn't override the formData.
          var url = form.action.replace(/\?.*/, '');
          formData.unset('action_cancel');
          // Create a notification message to display to the user when the submit is being sent
          form._x_notification = new XWiki.widgets.Notification("$msg.get('core.viewers.comments.add.inProgress')", "inprogress");
          form.disable();
          this.restartNeeded = false;
          new Ajax.Request(url, {
            method : 'post',
            evalJS : false,
            parameters : formData,
            onSuccess : function () {
              this.restartNeeded = true;
              this.editing = false;
              form._x_notification.replace(new XWiki.widgets.Notification("$msg.get('core.viewers.comments.add.done')", "done"));
            }.bind(this),
            onFailure : function (response) {
              var failureReason = response.statusText;
              if (response.statusText == '' /* No response */ || response.status == 12031 /* In IE */) {
                failureReason = 'Server not responding';
              }
              form._x_notification.replace(new XWiki.widgets.Notification("$msg.get('core.viewers.comments.add.failed')" + failureReason, "error"));
            }.bind(this),
            on0 : function (response) {
              response.request.options.onFailure(response);
            },
            onComplete : function (response) {
              if (this.restartNeeded) {
                // force reload
                location.reload();
                /*
                this.container.innerHTML = response.responseText;
                document.fire("xwiki:docextra:loaded", {
                  "id" : "Comments",
                  "element": this.container
                });
                this.updateCount()
                */
              } else {
                form.enable();
              } 
            }.bind(this)
          });
        }
      }.bindAsEventListener(this));
    }
  },
  addCancelListener : function() {
    if (this.form) {
      // I have no idea what this initial location is used for, but we leave it here and correct it to use .commentscontent instead of #_comments
      this.initialLocation = new Element("span", {className : "hidden"});
      this.conversation.down('.commentscontent').insert(this.initialLocation);
      // If the form is inside a thread, as a reply form, move it back to the bottom.
      var that = this;
      this.form.down('a.cancel').observe('click', this.resetForm.bindAsEventListener(this));
    }
  },
   /**
   * Add a preview button that generates the rendered comment,
   */
  addPreview : function(form) {
    if (!form || !XWiki.hasEdit) {
      return;
    }
    var previewURL = "$xwiki.getURL('__space__.__page__', 'preview')".replace("__space__", encodeURIComponent($$("meta[name=space]")[0].content)).replace("__page__", encodeURIComponent($$("meta[name=page]")[0].content));
    form.commentElt = form.down('textarea');
    var buttons = form.down('input[type=submit]').up('div');
    form.previewButton = new Element('span', {'class' : 'buttonwrapper'}).update(new Element('input', {'type' : 'button', 'class' : 'button', 'value' : "$msg.get('core.viewers.comments.preview.button.preview')"}));
    form.previewButton._x_modePreview = false;
    form.previewContent = new Element('div', {'class' : 'commentcontent commentPreview'});
    form.commentElt.insert({'before' : form.previewContent});
    form.previewContent.hide();
    buttons.insert({'top' : form.previewButton});
    form.previewButton.observe('click', function() {
      if (!form.previewButton._x_modePreview && !form.previewButton.disabled) {
         form.previewButton.disabled = true;
         var notification = new XWiki.widgets.Notification("$msg.get('core.viewers.comments.preview.inProgress')", "inprogress");
         new Ajax.Request(previewURL, {
            method : 'post',
            parameters : {'xpage' : 'plain', 'sheet' : '', 'content' : form.commentElt.value},
            onSuccess : function (response) {
              this.doPreview(response.responseText, form);
              notification.hide();
            }.bind(this),
            /* If the content is empty or does not generate anything, we have the "This template does not exist" response,
               with a 400 status code. */
            on400 : function(response) {
              this.doPreview('&nbsp;', form);
              notification.hide();
            }.bind(this),
            onFailure : function (response) {
              var failureReason = response.statusText;
              if (response.statusText == '' /* No response */ || response.status == 12031 /* In IE */) {
                failureReason = 'Server not responding';
              }
              notification.replace(new XWiki.widgets.Notification("$msg.get('core.viewers.comments.preview.failed')" + failureReason, "error"));
            },
            on0 : function (response) {
              response.request.options.onFailure(response);
            },
            onComplete : function (response) {
              form.previewButton.disabled = false;
            }.bind(this)
        });
      } else {
        this.cancelPreview(form);
      }
    }.bindAsEventListener(this));
  },
  /**
   * Display the comment preview instead of the comment textarea.
   * 
   * @param content the rendered comment, as HTML text
   * @param form the form for which the preview is done
   */
  doPreview : function(content, form) {
    form.previewButton._x_modePreview = true;
    form.previewContent.update(content);
    form.previewContent.show();
    form.commentElt.hide();
    form.previewButton.down('input').value = "$msg.get('core.viewers.comments.preview.button.back')";
  },
  /**
   * Display the comment textarea instead of the comment preview.
   *
   * @param form the form for which the preview is canceled
   */
  cancelPreview : function(form) {
    form.previewButton._x_modePreview = false;
    form.previewContent.hide();
    form.previewContent.update('');
    form.commentElt.show();
    form.previewButton.down('input').value = "$msg.get('core.viewers.comments.preview.button.preview')";
  },
  resetForm : function (event) {
    if (event) {
      event.stop();
    }
    if (this.form.up('.commentthread')) {
      // Show the comment's reply button
      this.form.up(".commentthread").previous(this.xcommentSelector).down('a.commentreply').show();

      // Before moving the editor we need to unload the wysiwyg editor
      var tarea = this.form["XWiki.XWikiComments_comment"];
      tarea.wysiwyg.release()
      tarea.previous().remove()

      // Put the form back to its initial location and clear the contents
      this.initialLocation.insert({after: this.form});

      // now we can reload the editor
      tarea.wysiwyg = new WysiwygEditor(WysiwygConfig[tarea.id]);
      XWiki.widgets.fs.addBehavior(this.form.down(".xRichTextEditor"));
    }
    this.form["XWiki.XWikiComments_replyto"].value = "";
    this.form["XWiki.XWikiComments_comment"].value = "";
    this.cancelPreview(this.form);
  },
  /**
   * Customized to take into account the new display of the conversation count.
   */
  updateCount : function() {
    if (this.conversation.down('.conversation-count')) {
      this.conversation.down('.conversation-count').update($$(this.xcommentSelector).size());
    }
  },
  /**
   * Registers a listener that watches for the insertion of the Comments tab and triggers the enhancements.
   * After that, the listener removes itself, since it is no longer needed.
   * We overwrite this in order to listen to only the reload of this conversation, not to all conversations.
   */
  addTabLoadListener : function(event) {
    var listener = function(event) {
      if (event.memo.id == 'Comments' && event.memo.element == this.container) {
        this.startup();
      }
    }.bindAsEventListener(this);
    document.observe("xwiki:docextra:loaded", listener);
  },
  /**
   * Just a simple message box that is displayed at various events: comment deleted, sending comment...
   */
  createNotification : function(message) {
    var msg = new Element('div', {"class" : "notification" });
    msg.update(message);
    return msg;
  },

  /**
   * Ajax conversation deletion.
   */
  addConversationDeleteListener : function() {
    var conversationDelete = this.conversation.down('.conversation-delete a');
    if (!conversationDelete) {
      return;
    }
    conversationDelete.observe('click', function(event) {
      var commentsCount = $$(this.xcommentSelector).size();
      conversationDelete.blur();
      event.stop();
      if (conversationDelete.disabled) {
        // Do nothing if the button was already clicked and it's waiting for a response from the server.
        return;
      } else {
        new XWiki.widgets.ConfirmedAjaxRequest(
          /* Ajax request URL */
          conversationDelete.readAttribute('href') + (Prototype.Browser.Opera ? "" : "&ajax=1"),
          /* Ajax request parameters */
          {
            onCreate : function() {
              // Disable the button, to avoid a cascade of clicks from impatient users
              conversationDelete.disabled = true;
            },
            onSuccess : function() {
              // Remove the corresponding HTML element from the UI
              var conversation = conversationDelete.up('.conversation');
              // Replace the comment with a "deleted conversation" placeholder
              conversation.replace(this.createNotification("$msg.get('conversation.delete.success')"));
            }.bind(this),
            onComplete : function() {
              // In the end: re-enable the button
              conversationDelete.disabled = false;
            }
          },
          /* Interaction parameters */
          {
             confirmationText: commentsCount > 0 ? "$escapetool.javascript($msg.get('conversation.delete.confirm.withReplies', ['__number__']))".replace('__number__', commentsCount) : "$escapetool.javascript($msg.get('conversation.delete.confirm'))",
             progressMessageText : "$msg.get('conversation.delete.inProgress')",
             successMessageText : "$msg.get('conversation.delete.done')",
             failureMessageText : "$msg.get('conversation.delete.failed')"
          }
        );
      }
    }.bindAsEventListener(this));
  },

  addConversationEditListener : function() {
    var conversationEdit = this.conversation.down('.conversation-edit a');
    if (!conversationEdit) {
      return;
    }
    conversationEdit.observe('click', function(event){
      event.stop();
      // check if a form exists already and if it has something in it
      var existingForm = this.conversation.down('.conversation-editformcontainer form');
      if (existingForm && existingForm.getElements().size() > 0) {
        return;
      }
      var conversationEditFormContainer = new Element('div', {'class' : 'loading conversation-editformcontainer'});
      var conversationInfo = this.conversation.down('.conversation-info');
      conversationInfo.insert({after : conversationEditFormContainer});
      var url = event.findElement().readAttribute('href');
      // make up a save URL from the edit URL
      var saveUrl = url.replace("\/edit\/", "/save/");
      var editForm = new Element('form', {'method' : 'post', 'action' : saveUrl})
      conversationEditFormContainer.insert(editForm);
      // create the buttons for the future form, but don't insert them just yet
      var saveButton = new Element('input', {'type' : 'submit', 'value' : '$escapetool.javascript($msg.get("save"))', 'class' : 'button'});
      var cancelButton = new Element('a', {'href' : window.location.href, 'class' : 'button'}).insert('$msg.get("cancel")');
      cancelButton.observe('click', function(event) {
        var conversationFormContainer = event.findElement('.conversation-editformcontainer');
        if (conversationFormContainer) {
          event.stop();
          conversationFormContainer.remove();
        }
      });
      var buttonsContainer = new Element('div', {'class' : 'buttonwrapper'}).insert(saveButton).insert(' ').insert(cancelButton);
      new Ajax.Request(url, {
        method : 'get',
        parameters : {'xpage' : 'plain'},
        onSuccess : function (response) {
          // put the form data in the form
          editForm.insert(response.responseText);
          // put the redirect in the form and wait for the prey...
          editForm.insert(new Element('input', {'type' : 'hidden', 'name' : 'xredirect', 'value' : window.location.href}));
          editForm.insert(buttonsContainer);
        }.bind(this),
        onFailure : function (response) {
          var failureReason = response.statusText;
          if (response.statusText == '' /* No response */ || response.status == 12031 /* In IE */) {
            failureReason = 'Server not responding';
          }
          editForm._x_notification = new XWiki.widgets.Notification("$msg.get('conversation.edit.failed')" + failureReason, "error");
          conversationEditFormContainer.remove();
        }.bind(this),
        on0 : function (response) {
          response.request.options.onFailure(response);
        },
        onComplete : function (response) {
          conversationEditFormContainer.removeClassName('loading');
        }.bind(this)
      });
    }.bindAsEventListener(this));
  },
  toggleConversationContent : function() {
    var commentsContent = this.conversation.down('.commentscontent');
    if (commentsContent) {
      commentsContent.toggleClassName('hidden');
    }
    var conversationTitle = this.conversation.down('.conversation-titlebar');
    if (conversationTitle) {
      conversationTitle.toggleClassName('conversation-showhandler');
    }
  },
    /**
      * Hide the conversation if it's not focused and add a handler to toggle it.
      *
      */
    addConversationHideListener : function() {
      var isVisible = false;
      // we cannot use .xwikicomment:target here since we're not sure it;s already loaded (e.g. on chrome) so we read the anchor manually
      var anchor = window.location.hash;
      if (anchor && anchor != "" && (this.conversation.down(anchor) || this.conversation.match(anchor))) {
        isVisible = true;
      }
      // force display:
      isVisible = true;
      var conversationTitle = this.conversation.down('.conversation-titlebar');
      if (conversationTitle) {
        if (!isVisible) {
          this.toggleConversationContent();
        }
        // add the listener
        conversationTitle.observe('click', function(event){
          this.toggleConversationContent();
        }.bindAsEventListener(this));
        // and a class name
        conversationTitle.addClassName('conversation-togglehandler');
      }
    },

    addConversationLikeListener : function() {
      var conversationLike = this.conversation.down('.conversation-like img.canVote');
      // if there is no clickable button, return, don't do anything
      if (!conversationLike) {
        return;
      }

      // if we have an active like button, add a listener to it
      conversationLike.observe('click', function(event) {
        event.stop();
        var conversationLikeBlock = event.findElement('.conversation-like');
        if (conversationLikeBlock.votingInProgress) {
          // there is already a voting in progress, don't start again
          return;
        }
        // find the conversation document name to vote for
        var conversationDocName;
        if (conversationLikeBlock) {
          var conversationNameInput = conversationLikeBlock.down('input[name=documenttolike]');
          if (conversationNameInput) {
            conversationDocName = conversationNameInput.value;
          }
        }
        if (!conversationDocName || typeof(conversationDocName) == "undefined") {
          // we don't have the name of the document to like, return
          return;
        }

        var likeUrl = '$escapetool.javascript($xwiki.getURL("XWiki.Ratings"))';
        conversationLikeBlock.votingInProgress = false;

        new Ajax.Request(likeUrl, {
          method : 'post',
          parameters : {'xpage' : 'plain', 'doc' : conversationDocName, 'vote' : '1'},
          onCreate : function () {
            conversationLikeBlock.votingInProgress = true;
            conversationLikeBlock._x_notification = new XWiki.widgets.Notification("$escapetool.javascript($msg.get('conversation.like.loading'))", "inprogress");
          }.bind(this),
          onSuccess : function (response) {
            conversationLikeBlock._x_notification.replace(new XWiki.widgets.Notification("$escapetool.javascript($msg.get('conversation.like.done'))", "done"));
            // get the conversation score which is the sibling of the like block
            var scoreDisplayer = conversationLikeBlock.next('.conversation-score');
            if (scoreDisplayer) {
              scoreDisplayer.update(response.responseJSON.totalvotes);
            }
            // and now remove this listener from the like button, since the current user shouldn't be able to vote again ...
            var conversationLikeButton = conversationLikeBlock.down('img');
            if (conversationLikeButton) {
              conversationLikeButton.stopObserving('click');
              // ... and put inactive class to change the style
              conversationLikeButton.removeClassName('canVote');
            }
          }.bind(this),
          onFailure : function (response) {
            var failureReason = response.responseText;
            if (!response.responseText || response.responseText == '' ) {
              failureReason = response.statusText;
            }
            if (response.statusText == '' /* No response */ || response.status == 12031 /* In IE */) {
              failureReason = 'Server not responding';
            }
            conversationLikeBlock._x_notification.replace(new XWiki.widgets.Notification("$escapetool.javascript($msg.get('conversation.like.failed'))" + failureReason, "error"));
          }.bind(this),
          on0 : function (response) {
            response.request.options.onFailure(response);
          },
          onComplete : function (response) {
            conversationLikeBlock.votingInProgress = false;
          }.bind(this)
        });
      }.bindAsEventListener(this));
    },

    addConversationPermalinkListener : function() {
      var conversationPermalink = this.conversation.down('.conversation-permalink a');
      if (!conversationPermalink) {
        return;
      }
      conversationPermalink.observe('click', function(event) {
        conversationPermalink.blur();
        event.stop();
        var permalinkBox = new XWiki.widgets.ConfirmationBox(
          {
            onYes : function () {
              window.location = conversationPermalink.href;
            }
          },
          /* Interaction parameters */
          {
            confirmationText: "$msg.get('core.viewers.comments.permalink'): <input type='text' class='full' value='" + conversationPermalink.href + "'/>",
            yesButtonText: "$msg.get('core.viewers.comments.permalink.goto')",
            noButtonText : "$msg.get('core.viewers.comments.permalink.hide')"
          }
        );
        permalinkBox.dialog.addClassName('permalinkBox')
        permalinkBox.dialog.down('input[type="text"]').select();
      });
    }
  });

function init() {
  $$('.conversation').each(function(conv) {
    new XWiki.viewers.Comments(conv);
  });

  $$('.AddComment').each(function(el) {
    // el.hide();
  });

  // also make the conversation add activator to show the add form when clicked
  $$(".addconversation-activator").each(function(item) {
    item.observe('click', function(event) {
      // get the button that was clicked
      var activator = event.findElement();
      // get its form, which is the sibling form .addconversation
      var form = activator.next('form.addconversation');
      // if we have a form, do all sorts of stuff, otherwise just let the link go
      if (form) {
        event.stop();
        form.removeClassName('hidden');
        activator.addClassName('hidden');

        // find the cancel button of this form and make it display the button back and hide the form
        var cancelButton = form.down('a.cancel');
        cancelButton.observe('click', function(event){
          event.stop();
          activator.removeClassName('hidden');
          form.addClassName('hidden');
        });
      }
    });
    
    // add vote click handler for the topic
    var topicDiv = $('conversation-topic');
    var topicLike = topicDiv.down('.conversation-like img.canVote');
      
    // if there is no clickable button, return, don't do anything
    if (topicLike) {
      // if we have an active like button, add a listener to it
      topicLike.observe('click', function(event) {
        event.stop();
        var topicLikeBlock = event.findElement('.conversation-like');
        if (topicLikeBlock.votingInProgress) {
          // there is already a voting in progress, don't start again
          return;
        }
        // find the conversation document name to vote for
        var topicDocName;
        if (topicLikeBlock) {
          var topicNameInput = topicLikeBlock.down('input[name=documenttolike]');
          if (topicNameInput) {
            topicDocName = topicNameInput.value;
          }
        }
        if (!topicDocName || typeof(topicDocName) == "undefined") {
          // we don't have the name of the document to like, return
          return;
        }

        console.log("Liking " + topicDocName);
        var likeUrl = '$escapetool.javascript($xwiki.getURL("XWiki.Ratings"))';
        topicLikeBlock.votingInProgress = false;

        new Ajax.Request(likeUrl, {
          method : 'post',
          parameters : {'xpage' : 'plain', 'doc' : topicDocName, 'vote' : '1'},
          onCreate : function () {
            topicLikeBlock.votingInProgress = true;
            topicLikeBlock._x_notification = new XWiki.widgets.Notification("$escapetool.javascript($msg.get('conversation.like.loading'))", "inprogress");
          }.bind(this),
          onSuccess : function (response) {
            topicLikeBlock._x_notification.replace(new XWiki.widgets.Notification("$escapetool.javascript($msg.get('conversation.like.done'))", "done"));
            // get the conversation score which is the sibling of the like block
            var scoreDisplayer = topicLikeBlock.next('.conversation-score');
            if (scoreDisplayer) {
              scoreDisplayer.update(response.responseJSON.totalvotes);
            }
            // and now remove this listener from the like button, since the current user shouldn't be able to vote again ...
            var topicLikeButton = topicLikeBlock.down('img');
            if (topicLikeButton) {
              topicLikeButton.stopObserving('click');
              // ... and put inactive class to change the style
              topicLikeButton.removeClassName('canVote');
            }
          }.bind(this),
          onFailure : function (response) {
            var failureReason = response.responseText;
            if (!response.responseText || response.responseText == '' ) {
              failureReason = response.statusText;
            }
            if (response.statusText == '' /* No response */ || response.status == 12031 /* In IE */) {
              failureReason = 'Server not responding';
            }
            topicLikeBlock._x_notification.replace(new XWiki.widgets.Notification("$escapetool.javascript($msg.get('conversation.like.failed'))" + failureReason, "error"));
          }.bind(this),
          on0 : function (response) {
            response.request.options.onFailure(response);
          },
          onComplete : function (response) {
            topicLikeBlock.votingInProgress = false;
          }.bind(this)
        });
      }.bindAsEventListener(this));
    }
    // end topicLike listener

    
    
  });

  // Activate full screen:
  if (!XWiki.widgets.fs)
     XWiki.widgets.fs = new XWiki.widgets.FullScreen();

}

// When the document is loaded, trigger the Comments form enhancements.
// Modification to work with curriki. This means js needs to be loaded non defered
document.observe("dom:loaded", init);

// End XWiki augmentation.
return XWiki;
}(XWiki || {}));
