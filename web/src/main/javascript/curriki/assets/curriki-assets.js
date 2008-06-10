// vim: ts=4:sw=4
/*global Curriki */
/*global _ */

Ext.ns('Curriki.assets');
Curriki.assets = {
	 json_prefix:'/xwiki/curriki/assets'
	,CreateAsset:function(parentPage, callback){
		Ext.Ajax.request({
			 url: this.json_prefix
			,method:'POST'
			,jsonData: {'parent':parentPage||''}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with
				//   assetPage, assetType, and fullAssetType items
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot create resource');
					throw {message: "CreateAsset: Json object not found"};
				}
				callback(o);
			}
			,failure:function(options){
				console.error('Cannot create resource', options);
				throw {message: "Server Error: Cannot create resource."};
			}
		});
	}
	,SetMetadata:function(assetPage, metadata, callback){
		// TODO: ExtJS + Prototype 1.6 can't do native PUT
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/metadata?_method=PUT'
			,method:'POST'
			,jsonData: metadata
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with metadata
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot set resource metadata');
					throw {message: "CreateAsset: Json object not found"};
				}
				callback(o);
			}
			,failure:function(options){
				console.error('Cannot set resource metadata', options);
				throw {message: "Server Error: Cannot set resource metadata."};
			}
		});
	}
	,CreateExternal:function(assetPage, linkUrl, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/externals'
			,method:'POST'
			,jsonData: {'link':linkUrl}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with new ref info
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot create external link');
					throw {message: "CreateAsset: Json object not found"};
				}
				callback(o);
			}
			,failure:function(options){
				console.error('Cannot create external link', options);
				throw {message: "Server Error: Cannot create external link."};
			}
		});
	}
	,Publish:function(assetPage, space, callback){
		// TODO: ExtJS + Prototype 1.6 can't do native PUT
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/published?_method=PUT'
			,method:'POST'
			,jsonData: {space: space}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with the new asset URL
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot publish resource');
					throw {message: "CreateAsset: Json object not found"};
				}
				callback(o);
			}
			,failure:function(options){
				console.error('Cannot publish resource', options);
				throw {message: "Server Error: Cannot publish resource."};
			}
		});
	}
}
