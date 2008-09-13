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
					alert('Error creating resource: '+(response.responseText||'Unknown server error'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot create resource', response, options);
				alert('Error: '+(response.responseText||('Server error creating resource.  '+(response.statusText||''))));
			}
		});
	}
	,GetAssetInfo:function(assetPage, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage
			,method:'GET'
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
					alert('Error getting resource information: '+(response.responseText||'Unknown server error'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot get resource metadata', response, options);
				alert('Error: '+(response.responseText||('Server error getting resource information.  '+(response.statusText||''))));
			}
		});
	}
	,GetMetadata:function(assetPage, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/metadata'
			,method:'GET'
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
					alert('Error getting resource metadata: '+(response.responseText||'Unknown server error'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot get resource metadata', response, options);
				alert('Error: '+(response.responseText||('Server error getting resource metadata.  '+(response.statusText||''))));
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
					alert('Error setting resource metadata: '+(response.responseText||'Unknown server error'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot set resource metadata', response, options);
				alert('Error: '+(response.responseText||('Server error setting resource metadata.  '+(response.statusText||''))));
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
					alert('Error creating external link: '+(response.responseText||'Unknown server error'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot create external link', response, options);
				alert('Error: '+(response.responseText||('Server error creating external resource.  '+(response.statusText||''))));
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
					alert('Error adding subasset: '+(response.responseText||'Unknown server error'));
				} else {
					// We need to refresh the collections for the user
					Curriki.data.user.GetCollections(function(){callback(o);});
				}
			}
			,failure:function(response, options){
				console.error('Cannot add subasset', response, options);
				alert('Error: '+(response.responseText||('Server error adding resource to folder.  '+(response.statusText||''))));
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
					alert('Error creating folder: '+(response.responseText||'Unknown server error'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot create folder', response, options);
				alert('Error: '+(response.responseText||('Server error creating folder.  '+(response.statusText||''))));
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
					alert('Error creating collection: '+(response.responseText||'Unknown server error'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot create collection', response, options);
				alert('Error: '+(response.responseText||('Server error creating collection.  '+(response.statusText||''))));
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
					alert('Error adding video: '+(response.responseText||'Unknown server error'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot add video', response, options);
				alert('Error: '+(response.responseText||('Server error creating VIDITalk resource.  '+(response.statusText||''))));
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
					alert('Error publishing resource: '+(response.responseText||'Unknown server error'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot publish resource', response, options);
				alert('Error: '+(response.responseText||('Server error publishing resource.  '+(response.statusText||''))));
			}
		});
	},
	UnnominateAsset:function(assetPage, callback){
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
					alert('Error unnominating resource: '+(response.responseText||'Unknown server error'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot unnominate resource', response, options);
				alert('Error: '+(response.responseText||('Server error unnominating resource.  '+(response.statusText||''))));
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
					alert('Error nominating resource: '+(response.responseText||'Unknown server error'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot nominate resource', response, options);
				alert('Error: '+(response.responseText||('Server error nominating resource.  '+(response.statusText||''))));
			}
		});
	},
	PartnerAsset : function(assetPage, callback) {
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
					alert('Error set as Partner resource: '
							+ (response.responseText || 'Unknown server error'));
				} else {
					callback(o);
				}
			},
			failure : function(response, options) {
				console.error('Cannot set as Partner resource', response, options);
				alert('Error: '
						+ (response.responseText || ('Server error set as Partner resource.  ' + (response.statusText || ''))));
			}
		});
	},
	SetAsterixReview : function(assetPage, callback, asterixReviewValue) {
		Ext.Ajax.request({
			url : this.json_prefix+'/'+assetPage,
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
					console.warn('Cannot set as Partner resource',
							response.responseText, options);
					alert('Error executing the action: '
							+ (response.responseText || 'Unknown server error'));
				} else {
					callback(o);
				}
			},
			failure : function(response, options) {
				console.error('Cannot execute the action', response,
						options);
				alert('Error: '
						+ (response.responseText || ('Server error executing the action.  ' + (response.statusText || ''))));
			}
		});
	},
	RemoveAsterixReview : function(assetPage, callback) {
		Ext.Ajax.request({
			url : this.json_prefix + '/' + assetPage,
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
					console.warn('Cannot set as Partner resource',
							response.responseText, options);
					alert('Error executing the action: '
							+ (response.responseText || 'Unknown server error'));
				} else {
					callback(o);
				}
			},
			failure : function(response, options) {
				console.error('Cannot execute the action', response,
						options);
				alert('Error: '
						+ (response.responseText || ('Server error executing the action.  ' + (response.statusText || ''))));
			}
		});
	}
}
