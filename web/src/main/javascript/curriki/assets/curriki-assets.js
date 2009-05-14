// vim: ts=4:sw=4
/*global Curriki */
/*global _ */

Ext.ns('Curriki.assets');
Curriki.assets = {
	 json_prefix:'/xwiki/curriki/assets'
	,CreateAsset:function(parentPage, publishSpace, callback){
		Ext.Ajax.request({
			 url: this.json_prefix
			,method:'POST'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {
				'parent':parentPage||''
				,'publishSpace':publishSpace||''
			}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with
				//   assetPage, assetType, and fullAssetType items
				var o = json.evalJSON(true);
				if(!o || !o.assetPage) {
					console.warn('Cannot create resource', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot create resource', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,CopyAsset:function(copyOf, publishSpace, callback){
		Ext.Ajax.request({
			 url: this.json_prefix
			,method:'POST'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {
				'copyOf':copyOf||''
				,'publishSpace':publishSpace||''
			}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with
				//   assetPage, assetType, and fullAssetType items
				var o = json.evalJSON(true);
				if(!o || !o.assetPage) {
					console.warn('Cannot copy resource', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot copy resource', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,GetAssetInfo:function(assetPage, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage
			,method:'GET'
			,disableCaching:true
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with metadata
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot get resource metadata', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot get resource metadata', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,GetMetadata:function(assetPage, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/metadata'
			,method:'GET'
			,disableCaching:true
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with metadata
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot get resource metadata', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					if ('string' === typeof o.rightsList) {
						o.rightsList = o.rightsList.evalJSON(true);
					}
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot get resource metadata', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,SetMetadata:function(assetPage, metadata, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/metadata'
			,method:'PUT'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: metadata
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with metadata
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot set resource metadata', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot set resource metadata', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,CreateExternal:function(assetPage, linkUrl, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/externals'
			,method:'POST'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {'link':linkUrl}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with new ref info
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot create external link', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot create external link', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,CreateSubasset:function(assetPage, subassetPage, order, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/subassets'
			,method:'POST'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {page:subassetPage, order:order}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with new ref info
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot add subasset', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					// We need to refresh the collections for the user
					Curriki.data.user.GetCollections(function(){if ('function' === typeof callback) {callback(o);}});
				}
			}
			,failure:function(response, options){
				console.error('Cannot add subasset', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,CreateFolder:function(assetPage, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/subassets'
			,method:'POST'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {collectionType:'folder'}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object for the current asset
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot create folder', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot create folder', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,CreateCollection:function(assetPage, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/subassets'
			,method:'POST'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {collectionType:'collection'}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object for the current asset
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot create collection', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot create collection', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,CreateVIDITalk:function(assetPage, videoId, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/viditalks'
			,method:'POST'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {page:assetPage, videoId:videoId}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with new ref info
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot add video', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot add video', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,Publish:function(assetPage, space, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/published'
			,method:'PUT'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {space: space}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with the new asset URL
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot publish resource', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot publish resource', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,ReorderRootCollection:function(place, which, original, wanted, callback){
		Ext.Ajax.request({
			url: '/xwiki/curriki/'+place+'/'+which+'/collections'
			,method:'PUT'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData:{original:original, wanted:wanted}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot reorder', response.responseText, options);
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot reorder', response, options);
				if (response.status == 412){
					if (response.responseText.search(/ 107 [^ ]+ 101:/) !== -1){
						var msgPfx = 'mycurriki.collections.reorder.';
						if (place === 'groups'){
							msgPfx = 'groups_curriculum_collections_reorder.';
						}
						alert(_(msgPfx+'error'));
					} else {
						alert(_('add.servertimedout.message.text'));
					}
				} else {
					alert(_('add.servertimedout.message.text'));
				}
			}
		});
	}
	,SetSubassets:function(assetPage, revision, wanted, logMsg, callback){
		var jsData = {wanted:wanted};
		if (!Ext.isEmpty(revision)) {
			jsData.previousRevision = revision;
		} else {
			jsData.ignorePreviousRevision = true;
		}
		if (!Ext.isEmpty(logMsg)) {
			jsData.logMessage = logMsg;
		}

		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/subassets'
			,method:'PUT'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData:jsData
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object for the current asset
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot save subassets', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot save subassets', response, options);
				if (response.status == 412){
					if (response.responseText.search(/ 107 [^ ]+ 101:/) !== -1){
						alert(_('error: Collision while saving -- only some changes saved'));
					} else {
						alert(_('add.servertimedout.message.text'));
					}
				} else {
					alert(_('add.servertimedout.message.text'));
				}
			}
		});
	}
	,UnnominateAsset:function(assetPage, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/unnominate'
			,method:'PUT'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with the new asset URL
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot unnominate resource', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot unnominate resource', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,NominateAsset:function(assetPage, comments, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/nominate'
			,method:'PUT'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {comments:comments}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with the new asset URL
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot nominate resource', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot nominate resource', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,PartnerAsset : function(assetPage, callback) {
		Ext.Ajax.request({
			url : this.json_prefix + '/' + assetPage + '/partner',
			method : 'PUT',
			headers : {
				'Accept' : 'application/json',
				'Content-type' : 'application/json'
			},
			jsonData : {},
			scope : this,
			success : function(response, options) {
				var json = response.responseText;
				var o = json.evalJSON(true);
				if (!o) {
					console.warn('Cannot set as Partner resource',
							response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			},
			failure : function(response, options) {
				console.error('Cannot set as Partner resource', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,SetAsterixReview : function(assetPage, callback, asterixReviewValue) {
		Ext.Ajax.request({
			url : this.json_prefix + "/" + assetPage + "/assetManager",
			method : 'PUT',
			headers : {
				'Accept' : 'application/json',
				'Content-type' : 'application/json'
			},
			jsonData : {
				action : 'setAsterixReview',
				asterixReviewValue : asterixReviewValue
				},
			scope : this,
			success : function(response, options) {
				var json = response.responseText;
				var o = json.evalJSON(true);
				if (!o) {
					console.warn('Cannot set as Partner resource', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			},
			failure : function(response, options) {
				console.error('Cannot execute the action', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,RemoveAsterixReview : function(assetPage, callback) {
		Ext.Ajax.request({
			url : this.json_prefix + "/" + assetPage + "/assetManager",
			method : 'PUT',
			headers : {
				'Accept' : 'application/json',
				'Content-type' : 'application/json'
			},
			jsonData : {
				action : 'removeAsterixReview'
				},
			scope : this,
			success : function(response, options) {
				var json = response.responseText;
				var o = json.evalJSON(true);
				if (!o) {
					console.warn('Cannot set as Partner resource', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			},
			failure : function(response, options) {
				console.error('Cannot execute the action', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,Flag : function(assetPage, reason, altReason, callback) {
		Ext.Ajax.request({
			url: '/xwiki/bin/view/FileCheck/Flag?xpage=plain&page='+(assetPage||'')+'&reason='+(reason||'')+'&altreason='+(altReason||'')+'&_dc='+(new Date().getTime())
			,method: 'POST'
			,headers: {
				'Accept' : 'application/json',
				'Content-type' : 'application/json'
			}
			,params: {
				page: assetPage||''
				,reason: reason||''
				,altreason: altReason||''
			}
			,scope: this
			,success: function(response, options) {
				var json = response.responseText;
				var o = {};
				try {
					o = json.evalJSON(true);
				} catch (e) {
					o = null;
				}
				if (!o) {
					console.warn('Could not flag resource', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					if (o.success) {
						callback(o);
					} else {
						console.warn('Could not flag resource', response.responseText, options);
						alert('Flagging Failed');
					}
				}
			}
			,failure: function(response, options) {
				console.error('Cannot execute the action', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
}
