if(typeof JSON!=="object"){JSON={}}(function(){function f(n){return n<10?"0"+n:n}if(typeof Date.prototype.toJSON!=="function"){Date.prototype.toJSON=function(key){return isFinite(this.valueOf())?this.getUTCFullYear()+"-"+f(this.getUTCMonth()+1)+"-"+f(this.getUTCDate())+"T"+f(this.getUTCHours())+":"+f(this.getUTCMinutes())+":"+f(this.getUTCSeconds())+"Z":null
};String.prototype.toJSON=Number.prototype.toJSON=Boolean.prototype.toJSON=function(key){return this.valueOf()
}}var cx=/[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,escapable=/[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,gap,indent,meta={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"},rep;
function quote(string){escapable.lastIndex=0;return escapable.test(string)?'"'+string.replace(escapable,function(a){var c=meta[a];
return typeof c==="string"?c:"\\u"+("0000"+a.charCodeAt(0).toString(16)).slice(-4)
})+'"':'"'+string+'"'}function str(key,holder){var i,k,v,length,mind=gap,partial,value=holder[key];
if(value&&typeof value==="object"&&typeof value.toJSON==="function"){value=value.toJSON(key)
}if(typeof rep==="function"){value=rep.call(holder,key,value)}switch(typeof value){case"string":return quote(value);
case"number":return isFinite(value)?String(value):"null";case"boolean":case"null":return String(value);
case"object":if(!value){return"null"}gap+=indent;partial=[];if(Object.prototype.toString.apply(value)==="[object Array]"){length=value.length;
for(i=0;i<length;i+=1){partial[i]=str(i,value)||"null"}v=partial.length===0?"[]":gap?"[\n"+gap+partial.join(",\n"+gap)+"\n"+mind+"]":"["+partial.join(",")+"]";
gap=mind;return v}if(rep&&typeof rep==="object"){length=rep.length;for(i=0;i<length;
i+=1){if(typeof rep[i]==="string"){k=rep[i];v=str(k,value);if(v){partial.push(quote(k)+(gap?": ":":")+v)
}}}}else{for(k in value){if(Object.prototype.hasOwnProperty.call(value,k)){v=str(k,value);
if(v){partial.push(quote(k)+(gap?": ":":")+v)}}}}v=partial.length===0?"{}":gap?"{\n"+gap+partial.join(",\n"+gap)+"\n"+mind+"}":"{"+partial.join(",")+"}";
gap=mind;return v}}if(typeof JSON.stringify!=="function"){JSON.stringify=function(value,replacer,space){var i;
gap="";indent="";if(typeof space==="number"){for(i=0;i<space;i+=1){indent+=" "}}else{if(typeof space==="string"){indent=space
}}rep=replacer;if(replacer&&typeof replacer!=="function"&&(typeof replacer!=="object"||typeof replacer.length!=="number")){throw new Error("JSON.stringify")
}return str("",{"":value})}}if(typeof JSON.parse!=="function"){JSON.parse=function(text,reviver){var j;
function walk(holder,key){var k,v,value=holder[key];if(value&&typeof value==="object"){for(k in value){if(Object.prototype.hasOwnProperty.call(value,k)){v=walk(value,k);
if(v!==undefined){value[k]=v}else{delete value[k]}}}}return reviver.call(holder,key,value)
}text=String(text);cx.lastIndex=0;if(cx.test(text)){text=text.replace(cx,function(a){return"\\u"+("0000"+a.charCodeAt(0).toString(16)).slice(-4)
})}if(/^[\],:{}\s]*$/.test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,"@").replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,"]").replace(/(?:^|:|,)(?:\s*\[)+/g,""))){j=eval("("+text+")");
return typeof reviver==="function"?walk({"":j},""):j}throw new SyntaxError("JSON.parse")
}}}());(function(b,c){var a=b.History=b.History||{};if(typeof a.Adapter!=="undefined"){throw new Error("History.js Adapter has already been loaded...")
}a.Adapter={handlers:{},_uid:1,uid:function(d){return d._uid||(d._uid=a.Adapter._uid++)
},bind:function(f,d,g){var e=a.Adapter.uid(f);a.Adapter.handlers[e]=a.Adapter.handlers[e]||{};
a.Adapter.handlers[e][d]=a.Adapter.handlers[e][d]||[];a.Adapter.handlers[e][d].push(g);
f["on"+d]=(function(i,h){return function(j){a.Adapter.trigger(i,h,j)}})(f,d)},trigger:function(g,d,h){h=h||{};
var f=a.Adapter.uid(g),e,j;a.Adapter.handlers[f]=a.Adapter.handlers[f]||{};a.Adapter.handlers[f][d]=a.Adapter.handlers[f][d]||[];
for(e=0,j=a.Adapter.handlers[f][d].length;e<j;++e){a.Adapter.handlers[f][d][e].apply(this,[h])
}},extractEventData:function(e,f){var d=(f&&f[e])||c;return d},onDomLoad:function(e){var d=b.setTimeout(function(){e()
},2000);b.onload=function(){clearTimeout(d);e()}}};if(typeof a.init!=="undefined"){a.init()
}})(window);(function(d,g){var a=d.document,e=d.setTimeout||e,f=d.clearTimeout||f,b=d.setInterval||b,c=d.History=d.History||{};
if(typeof c.initHtml4!=="undefined"){throw new Error("History.js HTML4 Support has already been loaded...")
}c.initHtml4=function(){if(typeof c.initHtml4.initialized!=="undefined"){return false
}else{c.initHtml4.initialized=true}c.enabled=true;c.savedHashes=[];c.isLastHash=function(h){var j=c.getHashByIndex(),i;
i=h===j;return i};c.isHashEqual=function(h,i){h=encodeURIComponent(h).replace(/%25/g,"%");
i=encodeURIComponent(i).replace(/%25/g,"%");return h===i};c.saveHash=function(h){if(c.isLastHash(h)){return false
}c.savedHashes.push(h);return true};c.getHashByIndex=function(h){var i=null;if(typeof h==="undefined"){i=c.savedHashes[c.savedHashes.length-1]
}else{if(h<0){i=c.savedHashes[c.savedHashes.length+h]}else{i=c.savedHashes[h]}}return i
};c.discardedHashes={};c.discardedStates={};c.discardState=function(l,h,k){var i=c.getHashByState(l),j;
j={discardedState:l,backState:k,forwardState:h};c.discardedStates[i]=j;return true
};c.discardHash=function(i,h,k){var j={discardedHash:i,backState:k,forwardState:h};
c.discardedHashes[i]=j;return true};c.discardedState=function(h){var j=c.getHashByState(h),i;
i=c.discardedStates[j]||false;return i};c.discardedHash=function(i){var h=c.discardedHashes[i]||false;
return h};c.recycleState=function(h){var i=c.getHashByState(h);if(c.discardedState(h)){delete c.discardedStates[i]
}return true};if(c.emulated.hashChange){c.hashChangeInit=function(){c.checkerFunction=null;
var h="",m,i,k,l,j=Boolean(c.getHash());if(c.isInternetExplorer()){m="historyjs-iframe";
i=a.createElement("iframe");i.setAttribute("id",m);i.setAttribute("src","#");i.style.display="none";
a.body.appendChild(i);i.contentWindow.document.open();i.contentWindow.document.close();
k="";l=false;c.checkerFunction=function(){if(l){return false}l=true;var o=c.getHash(),n=c.getHash(i.contentWindow.document);
if(o!==h){h=o;if(n!==o){k=n=o;i.contentWindow.document.open();i.contentWindow.document.close();
i.contentWindow.document.location.hash=c.escapeHash(o)}c.Adapter.trigger(d,"hashchange")
}else{if(n!==k){k=n;if(j&&n===""){c.back()}else{c.setHash(n,false)}}}l=false;return true
}}else{c.checkerFunction=function(){var n=c.getHash()||"";if(n!==h){h=n;c.Adapter.trigger(d,"hashchange")
}return true}}c.intervalList.push(b(c.checkerFunction,c.options.hashChangeInterval));
return true};c.Adapter.onDomLoad(c.hashChangeInit)}if(c.emulated.pushState){c.onHashChange=function(l){var m=((l&&l.newURL)||c.getLocationHref()),k=c.getHashByUrl(m),j=null,h=null,n=null,i;
if(c.isLastHash(k)){c.busy(false);return false}c.doubleCheckComplete();c.saveHash(k);
if(k&&c.isTraditionalAnchor(k)){c.Adapter.trigger(d,"anchorchange");c.busy(false);
return false}j=c.extractState(c.getFullUrl(k||c.getLocationHref()),true);if(c.isLastSavedState(j)){c.busy(false);
return false}h=c.getHashByState(j);i=c.discardedState(j);if(i){if(c.getHashByIndex(-2)===c.getHashByState(i.forwardState)){c.back(false)
}else{c.forward(false)}return false}c.pushState(j.data,j.title,encodeURI(j.url),false);
return true};c.Adapter.bind(d,"hashchange",c.onHashChange);c.pushState=function(l,q,h,n){h=encodeURI(h).replace(/%25/g,"%");
if(c.getHashByUrl(h)){throw new Error("History.js does not support states with fragment-identifiers (hashes/anchors).")
}if(n!==false&&c.busy()){c.pushQueue({scope:c,callback:c.pushState,args:arguments,queue:n});
return false}c.busy(true);var k=c.createStateObject(l,q,h),i=c.getHashByState(k),j=c.getState(false),m=c.getHashByState(j),p=c.getHash(),o=c.expectedStateId==k.id;
c.storeState(k);c.expectedStateId=k.id;c.recycleState(k);c.setTitle(k);if(i===m){c.busy(false);
return false}c.saveState(k);if(!o){c.Adapter.trigger(d,"statechange")}if(!c.isHashEqual(i,p)&&!c.isHashEqual(i,c.getShortUrl(c.getLocationHref()))){c.setHash(i,false)
}c.busy(false);return true};c.replaceState=function(l,p,h,n){h=encodeURI(h).replace(/%25/g,"%");
if(c.getHashByUrl(h)){throw new Error("History.js does not support states with fragment-identifiers (hashes/anchors).")
}if(n!==false&&c.busy()){c.pushQueue({scope:c,callback:c.replaceState,args:arguments,queue:n});
return false}c.busy(true);var k=c.createStateObject(l,p,h),i=c.getHashByState(k),j=c.getState(false),m=c.getHashByState(j),o=c.getStateByIndex(-2);
c.discardState(j,k,o);if(i===m){c.storeState(k);c.expectedStateId=k.id;c.recycleState(k);
c.setTitle(k);c.saveState(k);c.Adapter.trigger(d,"statechange");c.busy(false)}else{c.pushState(k.data,k.title,k.url,false)
}return true}}if(c.emulated.pushState){if(c.getHash()&&!c.emulated.hashChange){c.Adapter.onDomLoad(function(){c.Adapter.trigger(d,"hashchange")
})}}};if(typeof c.init!=="undefined"){c.init()}})(window);(function(i,a){var d=i.console||a,k=i.document,n=i.navigator,m=i.sessionStorage||false,c=i.setTimeout,l=i.clearTimeout,f=i.setInterval,p=i.clearInterval,o=i.JSON,j=i.alert,b=i.History=i.History||{},h=i.history;
try{m.setItem("TEST","1");m.removeItem("TEST")}catch(g){m=false}o.stringify=o.stringify||o.encode;
o.parse=o.parse||o.decode;if(typeof b.init!=="undefined"){throw new Error("History.js Core has already been loaded...")
}b.init=function(e){if(typeof b.Adapter==="undefined"){return false}if(typeof b.initCore!=="undefined"){b.initCore()
}if(typeof b.initHtml4!=="undefined"){b.initHtml4()}return true};b.initCore=function(e){if(typeof b.initCore.initialized!=="undefined"){return false
}else{b.initCore.initialized=true}b.options=b.options||{};b.options.hashChangeInterval=b.options.hashChangeInterval||100;
b.options.safariPollInterval=b.options.safariPollInterval||500;b.options.doubleCheckInterval=b.options.doubleCheckInterval||500;
b.options.disableSuid=b.options.disableSuid||false;b.options.storeInterval=b.options.storeInterval||1000;
b.options.busyDelay=b.options.busyDelay||250;b.options.debug=b.options.debug||false;
b.options.initialTitle=b.options.initialTitle||k.title;b.options.html4Mode=b.options.html4Mode||false;
b.options.delayInit=b.options.delayInit||false;b.intervalList=[];b.clearAllIntervals=function(){var t,s=b.intervalList;
if(typeof s!=="undefined"&&s!==null){for(t=0;t<s.length;t++){p(s[t])}b.intervalList=null
}};b.debug=function(){if((b.options.debug||false)){b.log.apply(b,arguments)}};b.log=function(){var y=!(typeof d==="undefined"||typeof d.log==="undefined"||typeof d.log.apply==="undefined"),t=k.getElementById("log"),x,w,z,u,s;
if(y){u=Array.prototype.slice.call(arguments);x=u.shift();if(typeof d.debug!=="undefined"){d.debug.apply(d,[x,u])
}else{d.log.apply(d,[x,u])}}else{x=("\n"+arguments[0]+"\n")}for(w=1,z=arguments.length;
w<z;++w){s=arguments[w];if(typeof s==="object"&&typeof o!=="undefined"){try{s=o.stringify(s)
}catch(v){}}x+="\n"+s+"\n"}if(t){t.value+=x+"\n-----\n";t.scrollTop=t.scrollHeight-t.clientHeight
}else{if(!y){j(x)}}return true};b.getInternetExplorerMajorVersion=function(){var s=b.getInternetExplorerMajorVersion.cached=(typeof b.getInternetExplorerMajorVersion.cached!=="undefined")?b.getInternetExplorerMajorVersion.cached:(function(){var t=3,w=k.createElement("div"),u=w.getElementsByTagName("i");
while((w.innerHTML="<!--[if gt IE "+(++t)+"]><i></i><![endif]-->")&&u[0]){}return(t>4)?t:false
})();return s};b.isInternetExplorer=function(){var s=b.isInternetExplorer.cached=(typeof b.isInternetExplorer.cached!=="undefined")?b.isInternetExplorer.cached:Boolean(b.getInternetExplorerMajorVersion());
return s};if(b.options.html4Mode){b.emulated={pushState:true,hashChange:true}}else{b.emulated={pushState:!Boolean(i.history&&i.history.pushState&&i.history.replaceState&&!((/ Mobile\/([1-7][a-z]|(8([abcde]|f(1[0-8]))))/i).test(n.userAgent)||(/AppleWebKit\/5([0-2]|3[0-2])/i).test(n.userAgent))),hashChange:Boolean(!(("onhashchange" in i)||("onhashchange" in k))||(b.isInternetExplorer()&&b.getInternetExplorerMajorVersion()<8))}
}b.enabled=!b.emulated.pushState;b.bugs={setHash:Boolean(!b.emulated.pushState&&n.vendor==="Apple Computer, Inc."&&/AppleWebKit\/5([0-2]|3[0-3])/.test(n.userAgent)),safariPoll:Boolean(!b.emulated.pushState&&n.vendor==="Apple Computer, Inc."&&/AppleWebKit\/5([0-2]|3[0-3])/.test(n.userAgent)),ieDoubleCheck:Boolean(b.isInternetExplorer()&&b.getInternetExplorerMajorVersion()<8),hashEscape:Boolean(b.isInternetExplorer()&&b.getInternetExplorerMajorVersion()<7)};
b.isEmptyObject=function(t){for(var s in t){if(t.hasOwnProperty(s)){return false}}return true
};b.cloneObject=function(u){var t,s;if(u){t=o.stringify(u);s=o.parse(t)}else{s={}
}return s};b.getRootUrl=function(){var s=k.location.protocol+"//"+(k.location.hostname||k.location.host);
if(k.location.port||false){s+=":"+k.location.port}s+="/";return s};b.getBaseHref=function(){var s=k.getElementsByTagName("base"),u=null,t="";
if(s.length===1){u=s[0];t=u.href.replace(/[^\/]+$/,"")}t=t.replace(/\/+$/,"");if(t){t+="/"
}return t};b.getBaseUrl=function(){var s=b.getBaseHref()||b.getBasePageUrl()||b.getRootUrl();
return s};b.getPageUrl=function(){var s=b.getState(false,false),u=(s||{}).url||b.getLocationHref(),t;
t=u.replace(/\/+$/,"").replace(/[^\/]+$/,function(x,w,v){return(/\./).test(x)?x:x+"/"
});return t};b.getBasePageUrl=function(){var s=(b.getLocationHref()).replace(/[#\?].*/,"").replace(/[^\/]+$/,function(v,u,t){return(/[^\/]$/).test(v)?"":v
}).replace(/\/+$/,"")+"/";return s};b.getFullUrl=function(t,v){var s=t,u=t.substring(0,1);
v=(typeof v==="undefined")?true:v;if(/[a-z]+\:\/\//.test(t)){}else{if(u==="/"){s=b.getRootUrl()+t.replace(/^\/+/,"")
}else{if(u==="#"){s=b.getPageUrl().replace(/#.*/,"")+t}else{if(u==="?"){s=b.getPageUrl().replace(/[\?#].*/,"")+t
}else{if(v){s=b.getBaseUrl()+t.replace(/^(\.\/)+/,"")}else{s=b.getBasePageUrl()+t.replace(/^(\.\/)+/,"")
}}}}}return s.replace(/\#$/,"")};b.getShortUrl=function(u){var t=u,v=b.getBaseUrl(),s=b.getRootUrl();
if(b.emulated.pushState){t=t.replace(v,"")}t=t.replace(s,"/");if(b.isTraditionalAnchor(t)){t="./"+t
}t=t.replace(/^(\.\/)+/g,"./").replace(/\#$/,"");return t};b.getLocationHref=function(s){s=s||k;
if(s.URL===s.location.href){return s.location.href}if(s.location.href===decodeURIComponent(s.URL)){return s.URL
}if(s.location.hash&&decodeURIComponent(s.location.href.replace(/^[^#]+/,""))===s.location.hash){return s.location.href
}if(s.URL.indexOf("#")==-1&&s.location.href.indexOf("#")!=-1){return s.location.href
}return s.URL||s.location.href};b.store={};b.idToState=b.idToState||{};b.stateToId=b.stateToId||{};
b.urlToId=b.urlToId||{};b.storedStates=b.storedStates||[];b.savedStates=b.savedStates||[];
b.normalizeStore=function(){b.store.idToState=b.store.idToState||{};b.store.urlToId=b.store.urlToId||{};
b.store.stateToId=b.store.stateToId||{}};b.getState=function(u,t){if(typeof u==="undefined"){u=true
}if(typeof t==="undefined"){t=true}var s=b.getLastSavedState();if(!s&&t){s=b.createStateObject()
}if(u){s=b.cloneObject(s);s.url=s.cleanUrl||s.url}return s};b.getIdByState=function(s){var u=b.extractId(s.url),t;
if(!u){t=b.getStateString(s);if(typeof b.stateToId[t]!=="undefined"){u=b.stateToId[t]
}else{if(typeof b.store.stateToId[t]!=="undefined"){u=b.store.stateToId[t]}else{while(true){u=(new Date()).getTime()+String(Math.random()).replace(/\D/g,"");
if(typeof b.idToState[u]==="undefined"&&typeof b.store.idToState[u]==="undefined"){break
}}b.stateToId[t]=u;b.idToState[u]=s}}}return u};b.normalizeState=function(t){var u,s;
if(!t||(typeof t!=="object")){t={}}if(typeof t.normalized!=="undefined"){return t
}if(!t.data||(typeof t.data!=="object")){t.data={}}u={};u.normalized=true;u.title=t.title||"";
u.url=b.getFullUrl(t.url?t.url:(b.getLocationHref()));u.hash=b.getShortUrl(u.url);
u.data=b.cloneObject(t.data);u.id=b.getIdByState(u);u.cleanUrl=u.url.replace(/\??\&_suid.*/,"");
u.url=u.cleanUrl;s=!b.isEmptyObject(u.data);if((u.title||s)&&b.options.disableSuid!==true){u.hash=b.getShortUrl(u.url).replace(/\??\&_suid.*/,"");
if(!/\?/.test(u.hash)){u.hash+="?"}u.hash+="&_suid="+u.id}u.hashedUrl=b.getFullUrl(u.hash);
if((b.emulated.pushState||b.bugs.safariPoll)&&b.hasUrlDuplicate(u)){u.url=u.hashedUrl
}return u};b.createStateObject=function(u,v,t){var s={data:u,title:v,url:t};s=b.normalizeState(s);
return s};b.getStateById=function(t){t=String(t);var s=b.idToState[t]||b.store.idToState[t]||a;
return s};b.getStateString=function(t){var s,u,v;s=b.normalizeState(t);u={data:s.data,title:t.title,url:t.url};
v=o.stringify(u);return v};b.getStateId=function(t){var s,u;s=b.normalizeState(t);
u=s.id;return u};b.getHashByState=function(t){var s,u;s=b.normalizeState(t);u=s.hash;
return u};b.extractId=function(v){var w,u,s,t;if(v.indexOf("#")!=-1){t=v.split("#")[0]
}else{t=v}u=/(.*)\&_suid=([0-9]+)$/.exec(t);s=u?(u[1]||v):v;w=u?String(u[2]||""):"";
return w||false};b.isTraditionalAnchor=function(t){var s=!(/[\/\?\.]/.test(t));return s
};b.extractState=function(v,u){var s=null,w,t;u=u||false;w=b.extractId(v);if(w){s=b.getStateById(w)
}if(!s){t=b.getFullUrl(v);w=b.getIdByUrl(t)||false;if(w){s=b.getStateById(w)}if(!s&&u&&!b.isTraditionalAnchor(v)){s=b.createStateObject(null,null,t)
}}return s};b.getIdByUrl=function(s){var t=b.urlToId[s]||b.store.urlToId[s]||a;return t
};b.getLastSavedState=function(){return b.savedStates[b.savedStates.length-1]||a};
b.getLastStoredState=function(){return b.storedStates[b.storedStates.length-1]||a
};b.hasUrlDuplicate=function(u){var t=false,s;s=b.extractState(u.url);t=s&&s.id!==u.id;
return t};b.storeState=function(s){b.urlToId[s.url]=s.id;b.storedStates.push(b.cloneObject(s));
return s};b.isLastSavedState=function(v){var u=false,t,s,w;if(b.savedStates.length){t=v.id;
s=b.getLastSavedState();w=s.id;u=(t===w)}return u};b.saveState=function(s){if(b.isLastSavedState(s)){return false
}b.savedStates.push(b.cloneObject(s));return true};b.getStateByIndex=function(t){var s=null;
if(typeof t==="undefined"){s=b.savedStates[b.savedStates.length-1]}else{if(t<0){s=b.savedStates[b.savedStates.length+t]
}else{s=b.savedStates[t]}}return s};b.getCurrentIndex=function(){var s=null;if(b.savedStates.length<1){s=0
}else{s=b.savedStates.length-1}return s};b.getHash=function(u){var s=b.getLocationHref(u),t;
t=b.getHashByUrl(s);return t};b.unescapeHash=function(t){var s=b.normalizeHash(t);
s=decodeURIComponent(s);return s};b.normalizeHash=function(t){var s=t.replace(/[^#]*#/,"").replace(/#.*/,"");
return s};b.setHash=function(v,s){var t,u;if(s!==false&&b.busy()){b.pushQueue({scope:b,callback:b.setHash,args:arguments,queue:s});
return false}b.busy(true);t=b.extractState(v,true);if(t&&!b.emulated.pushState){b.pushState(t.data,t.title,t.url,false)
}else{if(b.getHash()!==v){if(b.bugs.setHash){u=b.getPageUrl();b.pushState(null,null,u+"#"+v,false)
}else{k.location.hash=v}}}return b};b.escapeHash=function(t){var s=b.normalizeHash(t);
s=i.encodeURIComponent(s);if(!b.bugs.hashEscape){s=s.replace(/\%21/g,"!").replace(/\%26/g,"&").replace(/\%3D/g,"=").replace(/\%3F/g,"?")
}return s};b.getHashByUrl=function(s){var t=String(s).replace(/([^#]*)#?([^#]*)#?(.*)/,"$2");
t=b.unescapeHash(t);return t};b.setTitle=function(u){var v=u.title,t;if(!v){t=b.getStateByIndex(0);
if(t&&t.url===u.url){v=t.title||b.options.initialTitle}}try{k.getElementsByTagName("title")[0].innerHTML=v.replace("<","&lt;").replace(">","&gt;").replace(" & "," &amp; ")
}catch(s){}k.title=v;return b};b.queues=[];b.busy=function(t){if(typeof t!=="undefined"){b.busy.flag=t
}else{if(typeof b.busy.flag==="undefined"){b.busy.flag=false}}if(!b.busy.flag){l(b.busy.timeout);
var s=function(){var v,u,w;if(b.busy.flag){return}for(v=b.queues.length-1;v>=0;--v){u=b.queues[v];
if(u.length===0){continue}w=u.shift();b.fireQueueItem(w);b.busy.timeout=c(s,b.options.busyDelay)
}};b.busy.timeout=c(s,b.options.busyDelay)}return b.busy.flag};b.busy.flag=false;
b.fireQueueItem=function(s){return s.callback.apply(s.scope||b,s.args||[])};b.pushQueue=function(s){b.queues[s.queue||0]=b.queues[s.queue||0]||[];
b.queues[s.queue||0].push(s);return b};b.queue=function(t,s){if(typeof t==="function"){t={callback:t}
}if(typeof s!=="undefined"){t.queue=s}if(b.busy()){b.pushQueue(t)}else{b.fireQueueItem(t)
}return b};b.clearQueue=function(){b.busy.flag=false;b.queues=[];return b};b.stateChanged=false;
b.doubleChecker=false;b.doubleCheckComplete=function(){b.stateChanged=true;b.doubleCheckClear();
return b};b.doubleCheckClear=function(){if(b.doubleChecker){l(b.doubleChecker);b.doubleChecker=false
}return b};b.doubleCheck=function(s){b.stateChanged=false;b.doubleCheckClear();if(b.bugs.ieDoubleCheck){b.doubleChecker=c(function(){b.doubleCheckClear();
if(!b.stateChanged){s()}return true},b.options.doubleCheckInterval)}return b};b.safariStatePoll=function(){var t=b.extractState(b.getLocationHref()),s;
if(!b.isLastSavedState(t)){s=t}else{return}if(!s){s=b.createStateObject()}b.Adapter.trigger(i,"popstate");
return b};b.back=function(s){if(s!==false&&b.busy()){b.pushQueue({scope:b,callback:b.back,args:arguments,queue:s});
return false}b.busy(true);b.doubleCheck(function(){b.back(false)});h.go(-1);return true
};b.forward=function(s){if(s!==false&&b.busy()){b.pushQueue({scope:b,callback:b.forward,args:arguments,queue:s});
return false}b.busy(true);b.doubleCheck(function(){b.forward(false)});h.go(1);return true
};b.go=function(t,s){var u;if(t>0){for(u=1;u<=t;++u){b.forward(s)}}else{if(t<0){for(u=-1;
u>=t;--u){b.back(s)}}else{throw new Error("History.go: History.go requires a positive or negative integer passed.")
}}return b};if(b.emulated.pushState){var r=function(){};b.pushState=b.pushState||r;
b.replaceState=b.replaceState||r}else{b.onPopState=function(v,s){var x=false,w=false,u,t;
b.doubleCheckComplete();u=b.getHash();if(u){t=b.extractState(u||b.getLocationHref(),true);
if(t){b.replaceState(t.data,t.title,t.url,false)}else{b.Adapter.trigger(i,"anchorchange");
b.busy(false)}b.expectedStateId=false;return false}x=b.Adapter.extractEventData("state",v,s)||false;
if(x){w=b.getStateById(x)}else{if(b.expectedStateId){w=b.getStateById(b.expectedStateId)
}else{w=b.extractState(b.getLocationHref())}}if(!w){w=b.createStateObject(null,null,b.getLocationHref())
}b.expectedStateId=false;if(b.isLastSavedState(w)){b.busy(false);return false}b.storeState(w);
b.saveState(w);b.setTitle(w);b.Adapter.trigger(i,"statechange");b.busy(false);return true
};b.Adapter.bind(i,"popstate",b.onPopState);b.pushState=function(u,w,t,s){if(b.getHashByUrl(t)&&b.emulated.pushState){throw new Error("History.js does not support states with fragement-identifiers (hashes/anchors).")
}if(s!==false&&b.busy()){b.pushQueue({scope:b,callback:b.pushState,args:arguments,queue:s});
return false}b.busy(true);var v=b.createStateObject(u,w,t);if(b.isLastSavedState(v)){b.busy(false)
}else{b.storeState(v);b.expectedStateId=v.id;h.pushState(v.id,v.title,v.url);b.Adapter.trigger(i,"popstate")
}return true};b.replaceState=function(u,w,t,s){if(b.getHashByUrl(t)&&b.emulated.pushState){throw new Error("History.js does not support states with fragement-identifiers (hashes/anchors).")
}if(s!==false&&b.busy()){b.pushQueue({scope:b,callback:b.replaceState,args:arguments,queue:s});
return false}b.busy(true);var v=b.createStateObject(u,w,t);if(b.isLastSavedState(v)){b.busy(false)
}else{b.storeState(v);b.expectedStateId=v.id;h.replaceState(v.id,v.title,v.url);b.Adapter.trigger(i,"popstate")
}return true}}if(m){try{b.store=o.parse(m.getItem("History.store"))||{}}catch(q){b.store={}
}b.normalizeStore()}else{b.store={};b.normalizeStore()}b.Adapter.bind(i,"unload",b.clearAllIntervals);
b.saveState(b.storeState(b.extractState(b.getLocationHref(),true)));if(m){b.onUnload=function(){var s,u,w;
try{s=o.parse(m.getItem("History.store"))||{}}catch(t){s={}}s.idToState=s.idToState||{};
s.urlToId=s.urlToId||{};s.stateToId=s.stateToId||{};for(u in b.idToState){if(!b.idToState.hasOwnProperty(u)){continue
}s.idToState[u]=b.idToState[u]}for(u in b.urlToId){if(!b.urlToId.hasOwnProperty(u)){continue
}s.urlToId[u]=b.urlToId[u]}for(u in b.stateToId){if(!b.stateToId.hasOwnProperty(u)){continue
}s.stateToId[u]=b.stateToId[u]}b.store=s;b.normalizeStore();w=o.stringify(s);try{m.setItem("History.store",w)
}catch(v){if(v.code===DOMException.QUOTA_EXCEEDED_ERR){if(m.length){m.removeItem("History.store");
m.setItem("History.store",w)}else{}}else{throw v}}};b.intervalList.push(f(b.onUnload,b.options.storeInterval));
b.Adapter.bind(i,"beforeunload",b.onUnload);b.Adapter.bind(i,"unload",b.onUnload)
}if(!b.emulated.pushState){if(b.bugs.safariPoll){b.intervalList.push(f(b.safariStatePoll,b.options.safariPollInterval))
}if(n.vendor==="Apple Computer, Inc."||(n.appCodeName||"")==="Mozilla"){b.Adapter.bind(i,"hashchange",function(){b.Adapter.trigger(i,"popstate")
});if(b.getHash()){b.Adapter.onDomLoad(function(){b.Adapter.trigger(i,"hashchange")
})}}}};if(!b.options||!b.options.delayInit){b.init()}})(window);(function(){Ext.ns("Curriki.module.search");
var a=Curriki.module.search;a.settings={gridWidth:(Ext.isIE6?620:"auto")};a.stateProvider=new Ext.state.CookieProvider({});
Ext.state.Manager.setProvider(a.stateProvider);a.sessionProvider=new Ext.state.CookieProvider({expires:null});
Curriki.module.search.outerResources={prefix:"http://www.curriki.org/xwiki/bin/view/",suffix:"?comingFrom="+location.host,target:"currikiResources",ratingsPrefix:"http://www.curriki.org/xwiki/bin/view/",ratingsSuffix:"?viewer=comments"}
})();(function(){Ext.ns("Curriki.module.search.util");var a=Curriki.module.search;
var b=a.util;b.init=function(){console.log("search util: init");b.logFilterList={outerResource:["subject","subjectparent","level","language","ictprfx","ict","review","special","other","sort","dir"],resource:["subject","subjectparent","category","level","language","ictprfx","ict","review","special","other","sort","dir"],group:["subject","level","language","policy","other","sort","dir"],member:["subject","member_type","country","other","sort","dir"],blog:["other","sort","dir"],curriki:["other","sort","dir"]};
b.registerTabTitleListener=function(c){Ext.StoreMgr.lookup("search-store-"+c).addListener("datachanged",function(i){var h=false;
var k=0;var j=i.getTotalCount();if(!Ext.isEmpty(i.reader.jsonData)&&!Ext.isEmpty(i.reader.jsonData.totalResults)){k=parseInt(i.reader.jsonData.totalResults)
}if(k>j){h=true}var e=Ext.getCmp("search-"+c+"-tab");if(!Ext.isEmpty(e)){var g=_("search.tab.title.results");
if(h&&(_("search.tab.title.resultsmax_exceeds")!=="search.tab.title.resultsmax_exceeds")){g=_("search.tab.title.resultsmax_exceeds")
}e.setTitle(String.format(g,_("search."+c+".tab.title"),j,k))}var f=Ext.getCmp("search-pager-"+c);
if(!Ext.isEmpty(f)){var d=_("search.pagination.afterpage");if(h&&(_("search.pagination.afterpage_resultsmax_exceeds")!=="search.pagination.afterpage_resultsmax_exceeds")){d=_("search.pagination.afterpage_resultsmax_exceeds")
}f.afterPageText=String.format(d,"{0}",k);var l=_("search.pagination.displaying."+c);
if(h&&(_("search.pagination.displaying."+c+"_resultsmax_exceeds")!=="search.pagination.displaying."+c+"_resultsmax_exceeds")){l=_("search.pagination.displaying."+c+"_resultsmax_exceeds")
}f.displayMsg=String.format(l,"{0}","{1}","{2}",k)}});Ext.StoreMgr.lookup("search-store-"+c).addListener("beforeload",function(f,g){var e=Ext.StoreMgr.lookup("search-store-"+c);
var d=Ext.getCmp("search-pager-"+c);e.baseParams.rows=d.pageSize;return true});Ext.StoreMgr.lookup("search-store-"+c).addListener("load",function(l,h,n){var g=n.params||{};
var e=g.module;var i=encodeURI(g.terms||"");var k=Ext.getCmp("search-advanced-"+e);
var m=(k&&!k.collapsed)?"advanced":"simple";var j="";if(g.start){if(g.start!=="0"){j="/start/"+g.start
}}if(g.rows){if(g.rows!="25"){j=j+"/rows/"+g.rows}}var d="";Ext.each(b.logFilterList[e],function(o){if(!Ext.isEmpty(g[o],false)){d+="/"+o+"/"+encodeURI(g[o])
}});var f="";if(Curriki.module.search.util.isInEmbeddedMode()){Curriki.module.search.util.sendResizeMessageToEmbeddingWindow();
f="/features/embeddedsearch/"+e+"/"+i+"/"+m+d+j}else{f="/features/search/"+e+"/"+i+"/"+m+d+j
}Curriki.logView(f);a.saveState("?state="+encodeURI(f))});Ext.StoreMgr.lookup("search-store-"+c).addListener("exception",Curriki.notifyException)
};b.doSearch=function(h,i){console.log("Doing search",h,i);var f={};var e=Ext.getCmp("search-termPanel");
if(!Ext.isEmpty(e)){var g=e.getForm();if(!Ext.isEmpty(g)){Ext.apply(f,g.getValues(false))
}}var c=h;if(c=="outerResource"){c="resource"}if(c=="curriki"){c="discussions"}Ext.apply(f,{module:c});
e=Ext.getCmp("search-filterPanel-"+h);if(!Ext.isEmpty(e)){var g=e.getForm();if(!Ext.isEmpty(g)){Ext.apply(f,g.getValues(false))
}}if(f.terms&&f.terms===_("search.text.entry.label")){f.terms=""}console.log("Applying search filters",f);
Ext.apply(Ext.StoreMgr.lookup("search-store-"+h).baseParams||{},f);var d=Ext.getCmp("search-pager-"+h);
if(!Ext.isEmpty(d)){console.log("Searching",f);d.doLoad(Ext.num(i,0))}console.log("Done util.doSearch",f)
};b.createTermPanel=function(d,c){return{xtype:"panel",labelAlign:"left",id:"search-termPanel-"+d,cls:"term-panel",border:false,items:[{layout:"column",border:false,defaults:{border:false},items:[{layout:"form",id:"search-termPanel-"+d+"-form",cls:"search-termPanel-form",items:[{xtype:"textfield",id:"search-termPanel-"+d+"-terms",cls:"search-termPanel-terms",fieldLabel:_("search.text.entry.label"),name:"terms",hideLabel:true,emptyText:_("search.text.entry.label"),listeners:{specialkey:{fn:function(g,f){if(f.getKey()===Ext.EventObject.ENTER){f.stopEvent();
if("resource"==d&&Ext.StoreMgr.lookup("search-store-resource").sortInfo){Ext.StoreMgr.lookup("search-store-resource").sortInfo.field="score";
Ext.StoreMgr.lookup("search-store-resource").sortInfo.direction="DESC"}a.doSearch(d,true)
}}}}}]},{layout:"form",id:"search-termPanel-buttonColumn-"+d,cls:"search-termPanel-buttonColumn",items:[{xtype:"button",id:"search-termPanel-button-"+d,cls:"search-termPanel-button",text:_("search.text.entry.button"),listeners:{click:{fn:function(){if("resource"==d&&Ext.StoreMgr.lookup("search-store-resource").sortInfo){Ext.StoreMgr.lookup("search-store-resource").sortInfo.field="score";
Ext.StoreMgr.lookup("search-store-resource").sortInfo.direction="DESC"}a.doSearch(d,true)
}}}}]},{xtype:"box",id:"search-termPanel-tips-"+d,cls:"search-termPanel-tips",autoEl:{html:'<a href="/xwiki/bin/view/Search/Tips?xpage=popup" target="search_tips" onclick="{var popup=window.open(this.href, \'search_tips\', \'width=725,height=400,status=no,toolbar=no,menubar=no,location=no,resizable=yes\'); popup.focus();} return false;">'+_("search.text.entry.help.button")+"</a>"}}]},{xtype:"hidden",name:"other",id:"search-termPanel-other-"+d,value:(!Ext.isEmpty(a.restrictions)?a.restrictions:"")}]}
};b.fieldsetPanelSave=function(c,d){if(Ext.isEmpty(d)){d={}}if(!c.collapsed){d.collapsed=c.collapsed
}else{d=null}console.log("fieldset Panel Save state:",d);a.sessionProvider.set(c.stateId||c.id,d)
};b.fieldsetPanelRestore=function(c,d){if(!Ext.isEmpty(d)&&!Ext.isEmpty(d.collapsed)&&!d.collapsed){c.expand(false)
}};b.isInEmbeddedMode=function(){return !(typeof Curriki.module.search.embeddingPartnerUrl==="undefined")
};b.sendResizeMessageToEmbeddingWindow=function(){var c=document.body.scrollHeight+25;
console.log("search: sending resource view height to embedding window ("+c+"px)");
var d="resize:height:"+c+"px;";window.parent.postMessage(d,"*")};b.sendResourceUrlToEmbeddingWindow=function(c){console.log("search: sending resource url to embedding window ("+c+")");
var d="resourceurl:"+c;window.parent.postMessage(d,"*")};b.registerSearchLogging=function(c){}
};Ext.onReady(function(){b.init()})})();(function(){var a=["outerResource","resource"];
for(var b=0;b<2;b++){var c=a[b];Ext.ns("Curriki.module.search.data."+c);Curriki.module.search.data[c].init=function(g){var e=Curriki.module.search.data[g];
console.log("data."+g+": init");e.filter={};var d=e.filter;d.data={};d.data.subject={mapping:Curriki.data.fw_item.fwMap["FW_masterFramework.WebHome"],list:[],data:[["",_("CurrikiCode.AssetClass_fw_items_FW_masterFramework.UNSPECIFIED")]]};
d.data.subject.mapping.each(function(f){d.data.subject.list.push(f.id)});d.data.subject.list.push("UNCATEGORIZED");
d.data.subject.list.each(function(f){d.data.subject.data.push([f,_("CurrikiCode.AssetClass_fw_items_"+f)])
});d.data.subsubject={mapping:Curriki.data.fw_item.fwMap,data:[]};d.data.subject.mapping.each(function(f){d.data.subsubject.data.push([f.id,_("CurrikiCode.AssetClass_fw_items_"+f.id+".UNSPECIFIED"),f.id]);
d.data.subsubject.mapping[f.id].each(function(h){d.data.subsubject.data.push([h.id,_("CurrikiCode.AssetClass_fw_items_"+h.id),f.id])
})});d.data.level={list:Curriki.data.el.list,data:[["",_("CurrikiCode.AssetClass_educational_level_UNSPECIFIED")]]};
d.data.level.list.each(function(f){d.data.level.data.push([f,_("CurrikiCode.AssetClass_educational_level_"+f)])
});d.data.ict={fullList:Curriki.data.ict.list,parentList:{},list:[],data:[["",_("CurrikiCode.AssetClass_instructional_component_UNSPECIFIED","   ")]]};
d.data.ict.fullList.each(function(h){var f=h.replace(/_.*/,"");d.data.ict.parentList[f]=f
});Object.keys(d.data.ict.parentList).each(function(h){var f=_("CurrikiCode.AssetClass_instructional_component_"+h);
if(h==="other"){f="zzz"}d.data.ict.data.push([h,_("CurrikiCode.AssetClass_instructional_component_"+h),f])
});d.data.subict={list:Curriki.data.ict.list,parents:{},data:[]};d.data.subict.list.each(function(i){var f=i.replace(/_.*/,"");
if(f!==i){if(Ext.isEmpty(d.data.subict.parents[f])){d.data.subict.data.push([f+"*",_("CurrikiCode.AssetClass_instructional_component_"+f+"_UNSPECIFIED"),f,"   "]);
d.data.subict.parents[f]=f}var h=_("CurrikiCode.AssetClass_instructional_component_"+i);
if(i==="other"){h="zzz"}d.data.subict.data.push([i,_("CurrikiCode.AssetClass_instructional_component_"+i),f,h])
}});d.data.language={list:Curriki.data.language.list,data:[["",_("CurrikiCode.AssetClass_language_UNSPECIFIED")]]};
d.data.language.list.each(function(f){d.data.language.data.push([f,_("CurrikiCode.AssetClass_language_"+f)])
});d.data.category={list:Curriki.data.category.list,data:[["",_("CurrikiCode.AssetClass_category_UNSPECIFIED"),"   "]]};
d.data.category.list.each(function(h){var f=_("CurrikiCode.AssetClass_category_"+h);
if(h==="unknown"){f="zzz"}if(h!=="collection"){d.data.category.data.push([h,_("CurrikiCode.AssetClass_category_"+h),f])
}});d.data.review={list:["partners","highest_rated","members.highest_rated"],data:[["",_("search.resource.review.selector.UNSPECIFIED")]]};
d.data.review.list.each(function(f){d.data.review.data.push([f,_("search.resource.review.selector."+f)])
});d.data.special={list:typeof(Curriki.userinfo.userName)=="undefined"||Curriki.userinfo.userName=="XWiki.XWikiGuest"||g=="outerResource"?["collections","updated","info-only"]:["contributions","collections","updated","info-only"],data:[["",_("search.resource.special.selector.UNSPECIFIED")]]};
d.data.special.list.each(function(f){d.data.special.data.push([f,_("search.resource.special.selector."+f)])
});d.store={subject:new Ext.data.SimpleStore({fields:["id","subject"],data:d.data.subject.data,id:0}),subsubject:new Ext.data.SimpleStore({fields:["id","subject","parentItem"],data:d.data.subsubject.data,id:0}),level:new Ext.data.SimpleStore({fields:["id","level"],data:d.data.level.data,id:0}),ict:new Ext.data.SimpleStore({fields:["id","ict","sortValue"],sortInfo:{field:"sortValue",direction:"ASC"},data:d.data.ict.data,id:0}),subict:new Ext.data.SimpleStore({fields:["id","ict","parentICT","sortValue"],sortInfo:{field:"sortValue",direction:"ASC"},data:d.data.subict.data,id:0}),language:new Ext.data.SimpleStore({fields:["id","language"],data:d.data.language.data,id:0}),category:new Ext.data.SimpleStore({fields:["id","category","sortValue"],sortInfo:{field:"sortValue",direction:"ASC"},data:d.data.category.data,id:0}),review:new Ext.data.SimpleStore({fields:["id","review"],data:d.data.review.data,id:0}),special:new Ext.data.SimpleStore({fields:["id","special"],data:d.data.special.data,id:0})};
e.store={};e.store.record=new Ext.data.Record.create([{name:"title"},{name:"assetType"},{name:"category"},{name:"subcategory"},{name:"ict"},{name:"ictText"},{name:"ictIcon"},{name:"contributor"},{name:"contributorName"},{name:"rating",mapping:"review"},{name:"memberRating",mapping:"rating"},{name:"ratingCount"},{name:"description"},{name:"fwItems"},{name:"levels"},{name:"parents"},{name:"lastUpdated"},{name:"updated"},{name:"score"}]);
e.store.results=new Ext.data.Store({storeId:"search-store-"+g,proxy:new Ext.data.HttpProxy({url:document.location.pathname.endsWith("Old")?"/xwiki/bin/view/Search/Resources":(g=="outerResource"?"/outerCurrikiExtjs":"/currikiExtjs"),method:"GET"}),baseParams:{xpage:"plain"},reader:new Ext.data.JsonReader({root:"rows",totalProperty:"resultCount",id:"page"},e.store.record),remoteSort:true});
if(Curriki.userinfo.userGroups){e.store.results.baseParams.groupsId=Curriki.userinfo.userGroups
}if(Curriki.userinfo.userName){e.store.results.baseParams.userId=Curriki.userinfo.userName
}if(Curriki.userinfo.isAdmin){e.store.results.baseParams.isAdmin=true}if(Curriki.isISO8601DateParsing()){e.store.results.baseParams.dateFormat="ISO8601"
}e.store.results.setDefaultSort("score","desc");e.renderer={title:function(s,r,j,q,u,t){console.log("render title "+s);
if(typeof(s)!="string"){title=""}var o=j.id.replace(/\./,"/");var k=Ext.util.Format.stripTags(j.data.description);
k=Ext.util.Format.ellipsis(k,256);k=Ext.util.Format.htmlEncode(k);var i=Curriki.data.fw_item.getRolloverDisplay(j.data.fwItems||[]);
var p=Curriki.data.el.getRolloverDisplay(j.data.levels||[]);var n=j.data.lastUpdated||"";
var m="{1}<br />{0}<br /><br />";if(n!==""){m=m+"{7}<br />{6}<br /><br />"}m=m+"{3}<br />{2}<br />{5}<br />{4}";
k=String.format(m,k,_("global.title.popup.description"),i,_("global.title.popup.subject"),p,_("global.title.popup.educationlevel"),n,_("global.title.popup.last_updated"));
var l=j.data.assetType;var f=j.data.category;var h=j.data.subcategory;r.css=String.format("resource-{0} category-{1} subcategory-{1}_{2}",l,f,h);
var w=_(f+"."+h);if(w===f+"."+h){w=_("unknown.unknown")}if(Curriki.module.search.util.isInEmbeddedMode()){return String.format('<img class="x-tree-node-icon assettype-icon" src="{3}" ext:qtip="{4}" /><a  target="_blank" href="'+Curriki.module.search.resourceDisplay+'?resourceurl=/xwiki/bin/view/{0}" class="asset-title" ext:qtip="{2}">{1}</a>',escape(o+"?"+Curriki.module.search.embedViewMode),Ext.util.Format.ellipsis(s,80),k,Ext.BLANK_IMAGE_URL,w)
}else{if(g=="outerResource"){var v=Curriki.module.search.outerResources;return String.format('<img class="x-tree-node-icon assettype-icon" src="{0}" ext:qtip="{1}" /><a href="{2}{3}{4}" target="{5}" class="asset-title" ext:qtip="{1}">{6}</a>',Ext.BLANK_IMAGE_URL,k,v.prefix,o,v.suffix,v.target,s)
}else{return String.format('<img class="x-tree-node-icon assettype-icon" src="{3}" ext:qtip="{4}" /><a href="/xwiki/bin/view/{0}" class="asset-title" ext:qtip="{2}">{1}</a>',o,Ext.util.Format.ellipsis(s,80),k,Ext.BLANK_IMAGE_URL,w)
}}},ict:function(n,m,j,l,p,o){var k;var h;var i=j.data.ict;console.log("render ict "+n);
if(!Ext.isEmpty(i)){var f=i.replace(/_.*/,"");k="ict-"+f;if(f!==i){k=k+" ict-"+i}h=i.replace(/_/,".")
}else{k="ict-unknown";h="unknown"}m.css=k;return String.format('<img class="ict-icon" src="{1}" /><span class="ict-title">{0}</span>',_("search.resource.ict."+h),Ext.BLANK_IMAGE_URL)
},contributor:function(m,k,f,n,i,h){var l=m.replace(/\./,"/");console.log("render contributor "+m);
if(typeof("value")!="string"){m=""}if(Curriki.module.search.util.isInEmbeddedMode()){return String.format('<a href="/xwiki/bin/view/{0}" target="_blank">{1}</a>',l,f.data.contributorName)
}else{if(g=="outerResource"){var j=Curriki.module.search.outerResources;return String.format('<a href="{0}{1}{2}" target="{3}">{4}</a>',j.prefix,l,j.suffix,j.target,f.data.contributorName)
}else{return String.format('<a href="/xwiki/bin/view/{0}">{1}</a>',l,f.data.contributorName)
}}},rating:function(m,k,f,n,i,h){console.log("render rating "+m);if(typeof(m)=="string"&&m!=""){var l=f.id.replace(/\./,"/");
k.css=String.format("crs-{0}",m);if(Curriki.module.search.util.isInEmbeddedMode()){return String.format('<a href="/xwiki/bin/view/{3}?viewer=comments" target="_blank"><img class="crs-icon" alt="" src="{2}" /><span class="crs-text">{1}</span></a>',m,_("search.resource.review."+m),Ext.BLANK_IMAGE_URL,l)
}else{if(g=="outerResource"){var j=Curriki.module.search.outerResources;return String.format('<a "{0}{1}{2}" target="{3}"><img class="crs-icon" alt="" src="{4}" /><span class="crs-text">{5}</span></a>',j.ratingsPrefix,l,j.ratingsSuffix,j.target,Ext.BLANK_IMAGE_URL,_("search.resource.review."+m))
}else{return String.format('<a href="/xwiki/bin/view/{3}?viewer=comments"><img class="crs-icon" alt="" src="{2}" /><span class="crs-text">{1}</span></a>',m,_("search.resource.review."+m),Ext.BLANK_IMAGE_URL,l)
}}}else{return String.format("")}},memberRating:function(l,k,h,j,n,m){console.log("render memberRating "+l);
if(typeof(l)=="string"&&l!=""&&l!="0"&&l!=0){var i=h.id.replace(/\./,"/");var f=h.data.ratingCount;
if(f!=""&&f!="0"&&f!=0){k.css=String.format("rating-{0}",l);if(Curriki.module.search.util.isInEmbeddedMode()){return String.format('<a href="/xwiki/bin/view/{2}?viewer=comments" target="_blank"><img class="rating-icon" src="{4}" ext:qtip="{3}" /></a><a href="/xwiki/bin/view/{2}?viewer=comments" ext:qtip="{3}" target="_blank"> ({1})</a>',l,f,i,_("search.resource.rating."+l),Ext.BLANK_IMAGE_URL)
}else{if(g=="outerResource"){var o=Curriki.module.search.outerResources;return String.format('<a href="{0}{1}{2}"><img class="rating-icon" src="{3}" ext:qtip="{4}" /></a><a href="{0}{1}{2}" ext:qtip="{4}"> ({5})</a>',o.ratingsPrefix,i,o.ratingsSuffix,Ext.BLANK_IMAGE_URL,_("search.resource.rating."+l),f)
}else{return String.format('<a href="/xwiki/bin/view/{2}?viewer=comments"><img class="rating-icon" src="{4}" ext:qtip="{3}" /></a><a href="/xwiki/bin/view/{2}?viewer=comments" ext:qtip="{3}"> ({1})</a>',l,f,i,_("search.resource.rating."+l),Ext.BLANK_IMAGE_URL)
}}}else{return String.format("")}}else{return String.format("")}},updated:function(l,j,f,m,i,h){console.log("render updated "+l);
if(typeof("value")!="string"){return""}var k=Ext.util.Format.date(l,"M-d-Y");if(typeof(k)!="string"){return""
}return String.format("{0}",k)},score:function(k,j,f,l,i,h){if(typeof(k)!="number"){k=0
}return k}};console.log("Finished initting data for "+g+".")}}})();Ext.onReady(function(){Curriki.module.search.data.outerResource.init("outerResource");
Curriki.module.search.data.resource.init("resource")});Ext.ns("Curriki.module.search");
var Search=Curriki.module.search;(function(){var a=["outerResource","resource"];for(var b=0;
b<2;b++){var c=a[b];Ext.ns("Curriki.module.search.form."+c);Search.form[c].init=function(h){var e=Search.form[h];
var g=Search.data[h];console.log("form."+h+": init");var j=140;var d=250;e.ictCombo=function(k){Ext.apply(this,k)
};var i=$("curriki-searchbox");if(!Ext.isEmpty(i)){i.setValue("...");i.setAttribute("curriki:deftxt","...");
i.disable()}var f=$("searchbtn");if(!Ext.isEmpty(f)){f.innerHTML=""}Ext.extend(e.ictCombo,Ext.util.Observable,{init:function(k){Ext.apply(k,{tpl:'<tpl for="."><div class="x-combo-list-item ict-icon-combo-item ict-{'+k.valueField+'}"><img class="ict-icon" src="'+Ext.BLANK_IMAGE_URL+'"/><span class="ict-title">{'+k.displayField+"}</span></div></tpl>",onRender:k.onRender.createSequence(function(m,l){this.wrap.applyStyles({position:"relative"});
this.el.addClass("ict-icon-combo-input");this.icon=Ext.DomHelper.append(this.el.up("div.x-form-field-wrap"),{tag:"div",style:"position:absolute",children:{tag:"div",cls:"ict-icon"}})
}),setIconCls:function(){var l=this.store.query(this.valueField,this.getValue()).itemAt(0);
if(l){this.icon.className="ict-icon-combo-icon ict-"+l.get(this.valueField)}},setValue:k.setValue.createSequence(function(l){this.setIconCls()
})})}});e.categoryCombo=function(k){Ext.apply(this,k)};Ext.extend(e.categoryCombo,Ext.util.Observable,{init:function(k){Ext.apply(k,{tpl:'<tpl for="."><div class="x-combo-list-item category-icon-combo-item category-{'+k.valueField+'}"><img class="category-icon" src="'+Ext.BLANK_IMAGE_URL+'"/><span class="category-title">{'+k.displayField+"}</span></div></tpl>",onRender:k.onRender.createSequence(function(m,l){this.wrap.applyStyles({position:"relative"});
this.el.addClass("category-icon-combo-input");this.icon=Ext.DomHelper.append(this.el.up("div.x-form-field-wrap"),{tag:"div",style:"position:absolute",children:{tag:"div",cls:"category-icon"}})
}),setIconCls:function(){var l=this.store.query(this.valueField,this.getValue()).itemAt(0);
if(l){this.icon.className="category-icon-combo-icon category-"+l.get(this.valueField)
}},setValue:k.setValue.createSequence(function(l){this.setIconCls()})})}});e.termPanel=Search.util.createTermPanel(h,e);
e.filterPanel={xtype:"form",labelAlign:"left",id:"search-filterPanel-"+h,formId:"search-filterForm-"+h,border:false,items:[e.termPanel,{xtype:"fieldset",title:"",id:"search-advanced-"+h,autoHeight:true,collapsible:false,collapsed:false,animCollapse:false,border:true,stateful:true,stateEvents:["expand","collapse"],listeners:{statesave:{fn:Search.util.fieldsetPanelSave},staterestore:{fn:Search.util.fieldsetPanelRestore},expand:{fn:function(k){Ext.getCmp("search-results-"+h).getView().refresh();
Ext.select(".x-form-field-wrap",false,"search-advanced-"+h).setWidth(j);Ext.getCmp("search-termPanel-"+h).el.repaint()
}},collapse:{fn:function(k){Ext.getCmp("search-results-"+h).getView().refresh();Ext.getCmp("search-termPanel-"+h).el.repaint()
}}},items:[{layout:"column",border:false,defaults:{border:false,hideLabel:true},items:[{columnWidth:0.33,layout:"form",defaults:{hideLabel:true},items:[{xtype:"combo",id:"combo-subject-"+h,fieldLabel:"Subject",hiddenName:"subjectparent",width:j,listWidth:d,mode:"local",store:g.filter.store.subject,displayField:"subject",valueField:"id",typeAhead:true,triggerAction:"all",emptyText:_("CurrikiCode.AssetClass_fw_items_FW_masterFramework.UNSPECIFIED"),selectOnFocus:true,forceSelection:true,listeners:{select:{fn:function(m,l){var k=Ext.getCmp("combo-subsubject-"+h);
if(m.getValue()===""){k.clearValue();k.hide()}else{if(m.getValue()==="UNCATEGORIZED"){k.show();
k.clearValue();k.store.filter("parentItem",m.getValue());k.setValue(m.getValue());
k.hide()}else{k.show();k.clearValue();k.store.filter("parentItem",m.getValue());k.setValue(m.getValue())
}}}}}},{xtype:"combo",fieldLabel:"Sub Subject",id:"combo-subsubject-"+h,hiddenName:"subject",width:j,listWidth:d,mode:"local",store:g.filter.store.subsubject,displayField:"subject",valueField:"id",typeAhead:true,triggerAction:"all",selectOnFocus:true,forceSelection:true,lastQuery:"",hidden:true,hideMode:"visibility"},{xtype:"combo",id:"combo-category-"+h,fieldLabel:"Category",hiddenName:"category",width:j,listWidth:d,mode:"local",store:g.filter.store.category,displayField:"category",valueField:"id",plugins:new e.categoryCombo(),typeAhead:true,triggerAction:"all",emptyText:_("CurrikiCode.AssetClass_category_UNSPECIFIED"),selectOnFocus:true,forceSelection:true}]},{columnWidth:0.33,layout:"form",defaults:{hideLabel:true},items:[{xtype:"combo",id:"combo-level-"+h,fieldLabel:"Level",mode:"local",width:j,listWidth:d,store:g.filter.store.level,hiddenName:"level",displayField:"level",valueField:"id",typeAhead:true,triggerAction:"all",emptyText:_("CurrikiCode.AssetClass_educational_level_UNSPECIFIED"),selectOnFocus:true,forceSelection:true},{xtype:"combo",id:"combo-language-"+h,fieldLabel:"Language",hiddenName:"language",width:j,listWidth:d,mode:"local",store:g.filter.store.language,displayField:"language",valueField:"id",typeAhead:true,triggerAction:"all",emptyText:_("CurrikiCode.AssetClass_language_UNSPECIFIED"),selectOnFocus:true,forceSelection:true},{xtype:"combo",id:"combo-review-"+h,fieldLabel:"Review",hiddenName:"review",width:j,listWidth:d,mode:"local",store:g.filter.store.review,displayField:"review",valueField:"id",typeAhead:true,triggerAction:"all",emptyText:_("search.resource.review.selector.UNSPECIFIED"),selectOnFocus:true,forceSelection:true}]},{columnWidth:0.34,layout:"form",defaults:{hideLabel:true},items:[{xtype:"combo",id:"combo-ictprfx-"+h,fieldLabel:"Instructional Type",hiddenName:"ictprfx",width:j,listWidth:d,mode:"local",store:g.filter.store.ict,displayField:"ict",valueField:"id",plugins:new e.ictCombo(),typeAhead:true,triggerAction:"all",emptyText:_("CurrikiCode.AssetClass_instructional_component_UNSPECIFIED"),selectOnFocus:true,forceSelection:true,listeners:{select:{fn:function(n,k){var m=Ext.getCmp("combo-subICT-"+h);
if(n.getValue()===""){m.clearValue();m.hide()}else{m.clearValue();m.store.filter("parentICT",n.getValue());
var l=m.store.getById(n.getValue()+"*");if(Ext.isEmpty(l)){m.setValue(n.getValue());
m.hide()}else{m.setValue(n.getValue()+"*");m.show()}}}}}},{xtype:"combo",fieldLabel:"Sub ICT",hiddenName:"ict",width:j,listWidth:d,id:"combo-subICT-"+h,mode:"local",store:g.filter.store.subict,displayField:"ict",valueField:"id",typeAhead:true,triggerAction:"all",emptyText:"Select a Sub ICT...",selectOnFocus:true,forceSelection:true,lastQuery:"",hidden:true,hideMode:"visibility"},{xtype:"combo",id:"combo-special-"+h,fieldLabel:"Special Filters",hiddenName:"special",width:j,listWidth:d,mode:"local",store:g.filter.store.special,displayField:"special",valueField:"id",typeAhead:true,triggerAction:"all",emptyText:_("search.resource.special.selector.UNSPECIFIED"),selectOnFocus:true,forceSelection:true}]}]}]}]};
e.rowExpander=new Ext.grid.RowExpander({tpl:new Ext.XTemplate(_("search.resource.resource.expanded.title"),"<ul>",'<tpl for="parents">','<li class="resource-{assetType} category-{category} subcategory-{category}_{subcategory}">','<a target="{[this.getLinkTarget(values)]}" href="{[this.getParentURL(values)]}" ext:qtip="{[this.getQtip(values)]}">',"{title}","</a>","</li>","</tpl>","</ul>",{getParentURL:function(k){var l=k.page||false;
if(l){if(Curriki.module.search.util.isInEmbeddedMode()){return Curriki.module.search.resourceDisplay+"?resourceurl=/xwiki/bin/view/"+escape(l.replace(/\./,"/")+"?"+Curriki.module.search.embedViewMode)
}else{return"/xwiki/bin/view/"+l.replace(/\./,"/")}}else{return""}},getQtip:function(k){var n=Curriki.module.search.data[h].filter;
var o=Ext.util.Format.stripTags(k.description||"");o=Ext.util.Format.ellipsis(o,256);
o=Ext.util.Format.htmlEncode(o);var m=Curriki.data.fw_item.getRolloverDisplay(k.fwItems||[]);
var l=Curriki.data.el.getRolloverDisplay(k.levels||[]);return String.format("{1}<br />{0}<br /><br />{3}<br />{2}<br />{5}<br />{4}",o,_("global.title.popup.description"),m,_("global.title.popup.subject"),l,_("global.title.popup.educationlevel"))
},getLinkTarget:function(k){if(Curriki.module.search.util.isInEmbeddedMode()){return"_blank"
}else{return"_self"}}})});e.rowExpander.renderer=function(m,n,l){var k;if(l.data.parents&&l.data.parents.size()>0){n.cellAttr='rowspan="2"';
k="x-grid3-row-expander";return String.format('<img class="{0}" src="{1}" ext:qtip="{2}" />',k,Ext.BLANK_IMAGE_URL,_("search.resource.icon.plus.rollover"))
}else{k="x-grid3-row-expander-empty";return String.format('<img class="{0}" src="{1}" />',k,Ext.BLANK_IMAGE_URL)
}};e.rowExpander.on("expand",function(p,m,l,k){var o=p.grid.view.getRow(k);var n=Ext.DomQuery.selectNode("img[class*=x-grid3-row-expander]",o);
Ext.fly(n).set({"ext:qtip":_("search.resource.icon.minus.rollover")});if(Curriki.module.search.util.isInEmbeddedMode()){Curriki.module.search.util.sendResizeMessageToEmbeddingWindow()
}});e.rowExpander.on("collapse",function(p,m,l,k){var o=p.grid.view.getRow(k);var n=Ext.DomQuery.selectNode("img[class*=x-grid3-row-expander]",o);
Ext.fly(n).set({"ext:qtip":_("search.resource.icon.plus.rollover")});if(Curriki.module.search.util.isInEmbeddedMode()){Curriki.module.search.util.sendResizeMessageToEmbeddingWindow()
}});e.columnModel=new Ext.grid.ColumnModel([Ext.apply(e.rowExpander,{id:"score",tooltip:_("search.resource.column.header.score.tooltip"),header:" ",dataIndex:"score",width:30,sortable:true}),{id:"title",header:_("search.resource.column.header.title"),width:164,dataIndex:"title",sortable:true,hideable:false,renderer:g.renderer.title},{id:"ict",width:108,header:_("search.resource.column.header.ict"),dataIndex:"ictText",sortable:true,renderer:g.renderer.ict},{id:"contributor",width:110,header:_("search.resource.column.header.contributor"),dataIndex:"contributor",sortable:true,renderer:g.renderer.contributor},{id:"rating",width:88,header:_("search.resource.column.header.rating"),dataIndex:"rating",sortable:true,renderer:g.renderer.rating},{id:"memberRating",width:105,header:_("search.resource.column.header.member.rating"),dataIndex:"memberRating",sortable:true,renderer:g.renderer.memberRating},{id:"updated",width:80,header:_("search.resource.column.header.updated"),dataIndex:"updated",hidden:true,sortable:true,renderer:g.renderer.updated}]);
e.resultsPanel={xtype:"grid",id:"search-results-"+h,border:false,autoHeight:true,width:Search.settings.gridWidth,autoExpandColumn:"title",stateful:true,frame:false,stripeRows:true,viewConfig:{forceFit:true,enableRowBody:true,showPreview:true,scrollOffset:0},columnsText:_("search.columns.menu.columns"),sortAscText:_("search.columns.menu.sort_ascending"),sortDescText:_("search.columns.menu.sort_descending"),store:g.store.results,sm:new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn}),cm:e.columnModel,loadMask:false,plugins:e.rowExpander,bbar:new Ext.PagingToolbar({id:"search-pager-"+h,plugins:new Ext.ux.Andrie.pPageSize({variations:[10,25,50],beforeText:_("search.pagination.pagesize.before"),afterText:_("search.pagination.pagesize.after"),addBefore:_("search.pagination.pagesize.addbefore"),addAfter:_("search.pagination.pagesize.addafter")}),pageSize:25,store:g.store.results,displayInfo:true,displayMsg:_("search.pagination.displaying."+h),emptyMsg:_("search.find.no.results"),beforePageText:_("search.pagination.beforepage"),afterPageText:_("search.pagination.afterpage"),firstText:_("search.pagination.first"),prevText:_("search.pagination.prev"),nextText:_("search.pagination.next"),lastText:_("search.pagination.last"),refreshText:_("search.pagination.refresh")})};
e.mainPanel={xtype:"panel",id:"search-panel-"+h,autoHeight:true,items:[e.filterPanel,e.resultsPanel]};
e.doSearch=function(){Search.util.doSearch(h)};Search.util.registerTabTitleListener(h);
console.log("Finished initting form for "+h+".");console.log("Now get: Curriki.module.search.form['otherResource']: "+Curriki.module.search.form.otherResource)
}}Ext.onReady(function(){for(var d=0;d<2;d++){var e=a[d];Search.form[e].init(e)}})
})();(function(){var b="group";Ext.ns("Curriki.module.search.data."+b);var a=Curriki.module.search.data.group;
a.init=function(){console.log("data."+b+": init");a.filter={};var c=a.filter;c.data={};
c.data.subject={mapping:Curriki.data.fw_item.fwMap["FW_masterFramework.WebHome"],list:[],data:[["",_("XWiki.CurrikiSpaceClass_topic_FW_masterFramework.WebHome.UNSPECIFIED")]]};
c.data.subject.mapping.each(function(d){c.data.subject.list.push(d.id)});c.data.subject.list.each(function(d){c.data.subject.data.push([d,_("XWiki.CurrikiSpaceClass_topic_"+d)])
});c.data.subsubject={mapping:Curriki.data.fw_item.fwMap,data:[]};c.data.subject.mapping.each(function(d){c.data.subsubject.data.push([d.id,_("XWiki.CurrikiSpaceClass_topic_"+d.id+".UNSPECIFIED"),d.id]);
c.data.subsubject.mapping[d.id].each(function(e){c.data.subsubject.data.push([e.id,_("XWiki.CurrikiSpaceClass_topic_"+e.id),d.id])
})});c.data.level={list:Curriki.data.el.list,data:[["",_("XWiki.CurrikiSpaceClass_educationLevel_UNSPECIFIED")]]};
c.data.level.list.each(function(d){c.data.level.data.push([d,_("XWiki.CurrikiSpaceClass_educationLevel_"+d)])
});c.data.policy={list:["open","closed"],data:[["",_("search.XWiki.SpaceClass_policy_UNSPECIFIED")]]};
c.data.policy.list.each(function(d){c.data.policy.data.push([d,_("search.XWiki.SpaceClass_policy_"+d)])
});c.data.language={list:Curriki.data.language.list,data:[["",_("XWiki.CurrikiSpaceClass_language_UNSPECIFIED")]]};
c.data.language.list.each(function(d){c.data.language.data.push([d,_("XWiki.CurrikiSpaceClass_language_"+d)])
});c.store={subject:new Ext.data.SimpleStore({fields:["id","subject"],data:c.data.subject.data,id:0}),subsubject:new Ext.data.SimpleStore({fields:["id","subject","parentItem"],data:c.data.subsubject.data,id:0}),level:new Ext.data.SimpleStore({fields:["id","level"],data:c.data.level.data,id:0}),policy:new Ext.data.SimpleStore({fields:["id","policy"],data:c.data.policy.data,id:0}),language:new Ext.data.SimpleStore({fields:["id","language"],data:c.data.language.data,id:0})};
a.store={};a.store.record=new Ext.data.Record.create([{name:"title"},{name:"url"},{name:"policy"},{name:"description"},{name:"updated"}]);
a.store.results=new Ext.data.Store({storeId:"search-store-"+b,proxy:new Ext.data.HttpProxy({url:document.location.pathname.endsWith("Old")?"/xwiki/bin/view/Search/Groups":"/currikiExtjs",method:"GET"}),baseParams:{xpage:"plain",_dc:(new Date().getTime())},reader:new Ext.data.JsonReader({root:"rows",totalProperty:"resultCount",id:"page"},a.store.record),remoteSort:true});
if(Curriki.isISO8601DateParsing()){a.store.results.baseParams.dateFormat="ISO8601"
}a.store.results.setDefaultSort("title","asc");a.renderer={title:function(h,g,d,i,f,e){return String.format('<a href="{0}">{1}</a>',d.data.url,h)
},policy:function(h,g,d,j,f,e){if(h!==""){g.css="policy-"+h}var i=_("search.group.icon."+h);
return String.format('<span ext:qtip="{1}">{0}</span>',i,_("search.group.icon."+h+".rollover"))
},description:function(h,g,d,j,f,e){var i=Ext.util.Format.htmlDecode(h);i=Ext.util.Format.stripScripts(h);
i=Ext.util.Format.stripTags(i);i=Ext.util.Format.ellipsis(i,128);i=Ext.util.Format.htmlEncode(i);
i=Ext.util.Format.trim(i);return String.format("{0}",i)},updated:function(i,g,d,j,f,e){if(typeof("value")!="string"){return""
}var h=Ext.util.Format.date(i,"M-d-Y");if(typeof(h)!="string"){return""}return String.format("{0}",h)
}}};Ext.onReady(function(){a.init()})})();(function(){var d="group";Ext.ns("Curriki.module.search.form."+d);
var a=Curriki.module.search;var b=a.form[d];var c=a.data[d];b.init=function(){console.log("form."+d+": init");
var f=140;var e=250;b.termPanel=a.util.createTermPanel(d,b);b.filterPanel={xtype:"form",labelAlign:"left",id:"search-filterPanel-"+d,formId:"search-filterForm-"+d,border:false,items:[b.termPanel,{xtype:"fieldset",title:_("search.advanced.search.button"),id:"search-advanced-"+d,autoHeight:true,collapsible:true,collapsed:true,animCollapse:false,border:true,stateful:true,stateEvents:["expand","collapse"],listeners:{statesave:{fn:a.util.fieldsetPanelSave},staterestore:{fn:a.util.fieldsetPanelRestore},expand:{fn:function(g){Ext.getCmp("search-results-"+d).getView().refresh();
Ext.select(".x-form-field-wrap",false,"search-advanced-"+d).setWidth(f);Ext.getCmp("search-termPanel-"+d).el.repaint()
}},collapse:{fn:function(g){Ext.getCmp("search-results-"+d).getView().refresh();Ext.getCmp("search-termPanel-"+d).el.repaint()
}}},items:[{layout:"column",border:false,defaults:{border:false,hideLabel:true},items:[{columnWidth:0.33,layout:"form",defaults:{hideLabel:true},items:[{xtype:"combo",fieldLabel:"Subject",id:"combo-subject-"+d,hiddenName:"subjectparent",width:f,listWidth:e,mode:"local",store:c.filter.store.subject,displayField:"subject",valueField:"id",typeAhead:true,triggerAction:"all",emptyText:_("XWiki.CurrikiSpaceClass_topic_FW_masterFramework.WebHome.UNSPECIFIED"),selectOnFocus:true,forceSelection:true,listeners:{select:{fn:function(i,h){var g=Ext.getCmp("combo-subsubject-"+d);
if(i.getValue()===""){g.clearValue();g.hide()}else{g.show();g.clearValue();g.store.filter("parentItem",i.getValue());
g.setValue(i.getValue())}}}}},{xtype:"combo",fieldLabel:"Sub Subject",id:"combo-subsubject-"+d,hiddenName:"subject",width:f,listWidth:e,mode:"local",store:c.filter.store.subsubject,displayField:"subject",valueField:"id",typeAhead:true,triggerAction:"all",selectOnFocus:true,forceSelection:true,lastQuery:"",hidden:true,hideMode:"visibility"}]},{columnWidth:0.33,layout:"form",defaults:{hideLabel:true},items:[{xtype:"combo",id:"combo-level-"+d,fieldLabel:"Level",mode:"local",width:f,listWidth:e,store:c.filter.store.level,hiddenName:"level",displayField:"level",valueField:"id",typeAhead:true,triggerAction:"all",emptyText:_("XWiki.CurrikiSpaceClass_educationLevel_UNSPECIFIED"),selectOnFocus:true,forceSelection:true},{xtype:"combo",id:"combo-language-"+d,fieldLabel:"Language",hiddenName:"language",mode:"local",width:f,listWidth:e,store:c.filter.store.language,displayField:"language",valueField:"id",typeAhead:true,triggerAction:"all",emptyText:_("XWiki.CurrikiSpaceClass_language_UNSPECIFIED"),selectOnFocus:true,forceSelection:true}]},{columnWidth:0.34,layout:"form",defaults:{hideLabel:true},items:[{xtype:"combo",id:"combo-policy-"+d,fieldLabel:"Membership Policy",hiddenName:"policy",mode:"local",width:f,listWidth:e,store:c.filter.store.policy,displayField:"policy",valueField:"id",typeAhead:true,triggerAction:"all",emptyText:_("search.XWiki.SpaceClass_policy_UNSPECIFIED"),selectOnFocus:true,forceSelection:true}]}]}]}]};
b.columnModel=new Ext.grid.ColumnModel([{id:"policy",header:_("search.group.column.header.policy"),width:62,dataIndex:"policy",sortable:true,renderer:c.renderer.policy},{id:"title",header:_("search.group.column.header.name"),width:213,dataIndex:"title",sortable:true,hideable:false,renderer:c.renderer.title},{id:"description",width:225,header:_("search.group.column.header.description"),dataIndex:"description",sortable:false,renderer:c.renderer.description},{id:"updated",width:96,header:_("search.group.column.header.updated"),dataIndex:"updated",sortable:true,renderer:c.renderer.updated}]);
b.resultsPanel={xtype:"grid",id:"search-results-"+d,border:false,autoHeight:true,width:a.settings.gridWidth,autoExpandColumn:"description",stateful:true,frame:false,stripeRows:true,viewConfig:{forceFit:true,enableRowBody:true,showPreview:true,scrollOffset:0},columnsText:_("search.columns.menu.columns"),sortAscText:_("search.columns.menu.sort_ascending"),sortDescText:_("search.columns.menu.sort_descending"),store:c.store.results,sm:new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn}),cm:b.columnModel,loadMask:false,plugins:b.rowExpander,bbar:new Ext.PagingToolbar({id:"search-pager-"+d,plugins:new Ext.ux.Andrie.pPageSize({variations:[10,25,50],beforeText:_("search.pagination.pagesize.before"),afterText:_("search.pagination.pagesize.after"),addBefore:_("search.pagination.pagesize.addbefore"),addAfter:_("search.pagination.pagesize.addafter")}),pageSize:25,store:c.store.results,displayInfo:true,displayMsg:_("search.pagination.displaying."+d),emptyMsg:_("search.find.no.results"),beforePageText:_("search.pagination.beforepage"),afterPageText:_("search.pagination.afterpage"),firstText:_("search.pagination.first"),prevText:_("search.pagination.prev"),nextText:_("search.pagination.next"),lastText:_("search.pagination.last"),refreshText:_("search.pagination.refresh")})};
b.mainPanel={xtype:"panel",id:"search-panel-"+d,autoHeight:true,items:[b.filterPanel,b.resultsPanel]};
b.doSearch=function(){a.util.doSearch(d)};a.util.registerTabTitleListener(d)};Ext.onReady(function(){b.init()
})})();(function(){var b="member";Ext.ns("Curriki.module.search.data."+b);var a=Curriki.module.search.data.member;
a.init=function(){console.log("data."+b+": init");a.filter={};var c=a.filter;c.data={};
c.data.subject={mapping:Curriki.data.fw_item.fwMap["FW_masterFramework.WebHome"],list:[],data:[["",_("XWiki.XWikiUsers_topics_FW_masterFramework.WebHome.UNSPECIFIED")]]};
c.data.subject.mapping.each(function(d){c.data.subject.list.push(d.id)});c.data.subject.list.each(function(d){c.data.subject.data.push([d,_("XWiki.XWikiUsers_topics_"+d)])
});c.data.subsubject={mapping:Curriki.data.fw_item.fwMap,data:[]};c.data.subject.mapping.each(function(d){c.data.subsubject.data.push([d.id,_("XWiki.XWikiUsers_topics_"+d.id+".UNSPECIFIED"),d.id]);
c.data.subsubject.mapping[d.id].each(function(e){c.data.subsubject.data.push([e.id,_("XWiki.XWikiUsers_topics_"+e.id),d.id])
})});c.data.country={list:"AD|AE|AF|AG|AI|AL|AM|AN|AO|AQ|AR|AS|AT|AU|AW|AX|AZ|BA|BB|BD|BE|BF|BG|BH|BI|BJ|BM|BN|BO|BR|BS|BT|BV|BW|BY|BZ|CA|CC|CD|CF|CG|CH|CI|CK|CL|CM|CN|CO|CR|CU|CV|CX|CY|CZ|DE|DJ|DK|DM|DO|DZ|EC|EE|EG|EH|ER|ES|ET|FI|FJ|FK|FM|FO|FR|GA|GB|GD|GE|GF|GG|GH|GI|GL|GM|GN|GP|GQ|GR|GS|GT|GU|GW|GY|HK|HM|HN|HR|HT|HU|ID|IE|IL|IM|IN|IO|IQ|IR|IS|IT|JE|JM|JO|JP|KE|KG|KH|KI|KM|KN|KP|KR|KW|KY|KZ|LA|LB|LC|LI|LK|LR|LS|LT|LU|LV|LY|MA|MC|MD|ME|MG|MH|MK|ML|MM|MN|MO|MP|MQ|MR|MS|MT|MU|MV|MW|MX|MY|MZ|NA|NC|NE|NF|NG|NI|NL|NO|NP|NR|NU|NZ|OM|PA|PE|PF|PG|PH|PK|PL|PM|PN|PR|PS|PT|PW|PY|QA|RE|RO|RS|RU|RW|SA|SB|SC|SD|SE|SG|SH|SI|SJ|SK|SL|SM|SN|SO|SR|ST|SV|SY|SZ|TC|TD|TF|TG|TH|TJ|TK|TL|TM|TN|TO|TR|TT|TV|TW|TZ|UA|UG|UM|US|UY|UZ|VA|VC|VE|VG|VI|VN|VU|WF|WS|YE|YT|ZA|ZM|ZW".split("|"),data:[["",_("XWiki.XWikiUsers_country_UNSPECIFIED")]]};
c.data.country.list.each(function(d){c.data.country.data.push([d,_("XWiki.XWikiUsers_country_"+d)])
});c.data.member_type={list:["student","teacher","parent","professional","administration","nonprofit","nonprofit_education","corporation"],data:[["",_("XWiki.XWikiUsers_member_type_UNSPECIFIED")]]};
c.data.member_type.list.each(function(d){c.data.member_type.data.push([d,_("XWiki.XWikiUsers_member_type_"+d)])
});c.store={subject:new Ext.data.SimpleStore({fields:["id","subject"],data:c.data.subject.data,id:0}),subsubject:new Ext.data.SimpleStore({fields:["id","subject","parentItem"],data:c.data.subsubject.data,id:0}),member_type:new Ext.data.SimpleStore({fields:["id","member_type"],data:c.data.member_type.data,id:0}),country:new Ext.data.SimpleStore({fields:["id","country"],data:c.data.country.data,id:0})};
a.store={};a.store.record=new Ext.data.Record.create([{name:"name1"},{name:"name2"},{name:"url"},{name:"bio"},{name:"picture"},{name:"contributions"}]);
a.store.results=new Ext.data.Store({storeId:"search-store-"+b,proxy:new Ext.data.HttpProxy({url:document.location.pathname.endsWith("Old")?"/xwiki/bin/view/Search/Members":"/currikiExtjs",method:"GET"}),baseParams:{xpage:"plain",_dc:(new Date().getTime())},reader:new Ext.data.JsonReader({root:"rows",totalProperty:"resultCount",id:"page"},a.store.record),remoteSort:true});
if(Curriki.isISO8601DateParsing()){a.store.results.baseParams.dateFormat="ISO8601"
}a.store.results.setDefaultSort("name1","asc");a.renderer={name1:function(h,g,d,i,f,e){return String.format('<a href="{1}">{0}</a>',h,d.data.url)
},name2:function(h,g,d,i,f,e){return String.format('<a href="{1}">{0}</a>',h,d.data.url)
},picture:function(h,g,d,i,f,e){return String.format('<a href="{2}"><img src="{0}" alt="{1}" class="member-picture" style="width:88px" /></a>',h,_("search.member.column.picture.alt.text"),d.data.url)
},contributions:function(h,g,d,i,f,e){return String.format("{0}",h)},bio:function(h,g,d,j,f,e){var i=Ext.util.Format.htmlDecode(h);
i=Ext.util.Format.stripScripts(h);i=Ext.util.Format.stripTags(i);i=Ext.util.Format.ellipsis(i,128);
i=Ext.util.Format.htmlEncode(i);i=Ext.util.Format.trim(i);return String.format("{0}",i)
}}};Ext.onReady(function(){a.init()})})();(function(){var d="member";Ext.ns("Curriki.module.search.form."+d);
var a=Curriki.module.search;var b=a.form[d];var c=a.data[d];b.init=function(){console.log("form."+d+": init");
var f=140;var e=250;b.termPanel=a.util.createTermPanel(d,b);b.filterPanel={xtype:"form",labelAlign:"left",id:"search-filterPanel-"+d,formId:"search-filterForm-"+d,border:false,items:[b.termPanel,{xtype:"fieldset",title:_("search.advanced.search.button"),id:"search-advanced-"+d,autoHeight:true,collapsible:true,collapsed:true,animCollapse:false,border:true,stateful:true,stateEvents:["expand","collapse"],listeners:{statesave:{fn:a.util.fieldsetPanelSave},staterestore:{fn:a.util.fieldsetPanelRestore},expand:{fn:function(g){Ext.getCmp("search-results-"+d).getView().refresh();
Ext.select(".x-form-field-wrap",false,"search-advanced-"+d).setWidth(f);Ext.getCmp("search-termPanel-"+d).el.repaint()
}},collapse:{fn:function(g){Ext.getCmp("search-results-"+d).getView().refresh();Ext.getCmp("search-termPanel-"+d).el.repaint()
}}},items:[{layout:"column",border:false,defaults:{border:false,hideLabel:true},items:[{columnWidth:0.33,layout:"form",defaults:{hideLabel:true},items:[{xtype:"combo",id:"combo-subject-"+d,fieldLabel:"Subject",hiddenName:"subjectparent",width:f,listWidth:e,mode:"local",store:c.filter.store.subject,displayField:"subject",valueField:"id",typeAhead:true,triggerAction:"all",emptyText:_("XWiki.XWikiUsers_topics_FW_masterFramework.WebHome.UNSPECIFIED"),selectOnFocus:true,forceSelection:true,listeners:{select:{fn:function(i,h){var g=Ext.getCmp("combo-subsubject-"+d);
if(i.getValue()===""){g.clearValue();g.hide()}else{g.show();g.clearValue();g.store.filter("parentItem",i.getValue());
g.setValue(i.getValue())}}}}},{xtype:"combo",fieldLabel:"Sub Subject",id:"combo-subsubject-"+d,hiddenName:"subject",width:f,listWidth:e,mode:"local",store:c.filter.store.subsubject,displayField:"subject",valueField:"id",typeAhead:true,triggerAction:"all",selectOnFocus:true,forceSelection:true,lastQuery:"",hidden:true,hideMode:"visibility"}]},{columnWidth:0.33,layout:"form",defaults:{hideLabel:true},items:[{xtype:"combo",id:"combo-member_type-"+d,fieldLabel:"Member Type",mode:"local",width:f,listWidth:e,store:c.filter.store.member_type,hiddenName:"member_type",displayField:"member_type",valueField:"id",typeAhead:true,triggerAction:"all",emptyText:_("XWiki.XWikiUsers_member_type_UNSPECIFIED"),selectOnFocus:true,forceSelection:true}]},{columnWidth:0.34,layout:"form",defaults:{hideLabel:true},items:[{xtype:"combo",id:"combo-country-"+d,fieldLabel:"Country",hiddenName:"country",width:f,listWidth:e,mode:"local",store:c.filter.store.country,displayField:"country",valueField:"id",typeAhead:true,triggerAction:"all",emptyText:_("XWiki.XWikiUsers_country_UNSPECIFIED"),selectOnFocus:true,forceSelection:true}]}]}]}]};
b.columnModelList=[{id:"picture",header:_("search.member.column.header.picture"),width:116,dataIndex:"picture",sortable:false,resizable:false,menuDisabled:true,renderer:c.renderer.picture},{id:"name1",header:_("search.member.column.header.name1"),width:120,dataIndex:"name1",sortable:true,hideable:false,renderer:c.renderer.name1},{id:"name2",width:120,header:_("search.member.column.header.name2"),dataIndex:"name2",sortable:true,hideable:false,renderer:c.renderer.name2},{id:"bio",width:120,header:_("search.member.column.header.bio"),dataIndex:"bio",sortable:false,renderer:c.renderer.bio},{id:"contributions",width:120,header:_("search.member.column.header.contributions"),dataIndex:"contributions",sortable:false,renderer:c.renderer.contributions}];
b.columnModel=new Ext.grid.ColumnModel(b.columnModelList);b.resultsPanel={xtype:"grid",id:"search-results-"+d,border:false,autoHeight:true,width:a.settings.gridWidth,autoExpandColumn:"bio",stateful:true,frame:false,stripeRows:true,viewConfig:{forceFit:true,enableRowBody:true,showPreview:true,scrollOffset:0},columnsText:_("search.columns.menu.columns"),sortAscText:_("search.columns.menu.sort_ascending"),sortDescText:_("search.columns.menu.sort_descending"),store:c.store.results,sm:new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn}),cm:b.columnModel,loadMask:false,plugins:b.rowExpander,bbar:new Ext.PagingToolbar({id:"search-pager-"+d,plugins:new Ext.ux.Andrie.pPageSize({variations:[10,25,50],beforeText:_("search.pagination.pagesize.before"),afterText:_("search.pagination.pagesize.after"),addBefore:_("search.pagination.pagesize.addbefore"),addAfter:_("search.pagination.pagesize.addafter")}),pageSize:25,store:c.store.results,displayInfo:true,displayMsg:_("search.pagination.displaying."+d),emptyMsg:_("search.find.no.results"),beforePageText:_("search.pagination.beforepage"),afterPageText:_("search.pagination.afterpage"),firstText:_("search.pagination.first"),prevText:_("search.pagination.prev"),nextText:_("search.pagination.next"),lastText:_("search.pagination.last"),refreshText:_("search.pagination.refresh")})};
b.mainPanel={xtype:"panel",id:"search-panel-"+d,autoHeight:true,items:[b.filterPanel,b.resultsPanel]};
b.doSearch=function(){a.util.doSearch(d)};a.util.registerTabTitleListener(d)};Ext.onReady(function(){b.init()
})})();(function(){var b="blog";Ext.ns("Curriki.module.search.data."+b);var a=Curriki.module.search.data.blog;
a.init=function(){console.log("data."+b+": init");a.store={};a.store.record=new Ext.data.Record.create([{name:"name"},{name:"title"},{name:"text"},{name:"comments"},{name:"updated"},{name:"memberUrl"},{name:"blogUrl"}]);
a.store.results=new Ext.data.Store({storeId:"search-store-"+b,proxy:new Ext.data.HttpProxy({url:document.location.pathname.endsWith("Old")?"/xwiki/bin/view/Search/Blogs":"/currikiExtjs",method:"GET"}),baseParams:{xpage:"plain",_dc:(new Date().getTime())},reader:new Ext.data.JsonReader({root:"rows",totalProperty:"resultCount",id:"page"},a.store.record),remoteSort:true});
if(Curriki.isISO8601DateParsing()){a.store.results.baseParams.dateFormat="ISO8601"
}a.store.results.setDefaultSort("updated","desc");a.renderer={name:function(g,f,c,h,e,d){return String.format('<a href="{1}">{0}</a>',g,c.data.memberUrl)
},text:function(g,f,c,i,e,d){var h=Ext.util.Format.htmlDecode(g);h=Ext.util.Format.stripScripts(g);
h=Ext.util.Format.stripTags(h);h=Ext.util.Format.trim(h);h=Ext.util.Format.ellipsis(h,128);
return String.format('<a href="{2}" class="search-blog-title">{1}</a><br /><br />{0}',h,c.data.title,c.data.blogUrl)
},comments:function(g,f,c,h,e,d){return String.format("{0}",g)},updated:function(h,f,c,i,e,d){if(typeof("value")!="string"){return""
}var g=Ext.util.Format.date(h,"M-d-Y");if(typeof(g)!="string"){return""}return String.format("{0}",g)
}}};Ext.onReady(function(){a.init()})})();(function(){var d="blog";Ext.ns("Curriki.module.search.form."+d);
var a=Curriki.module.search;var b=a.form[d];var c=a.data[d];b.init=function(){console.log("form."+d+": init");
b.termPanel=a.util.createTermPanel(d,b);b.filterPanel={xtype:"form",labelAlign:"left",id:"search-filterPanel-"+d,formId:"search-filterForm-"+d,border:false,items:[b.termPanel]};
b.columnModel=new Ext.grid.ColumnModel([{id:"name",header:_("search.blog.column.header.name"),width:160,dataIndex:"name",sortable:true,renderer:c.renderer.name},{id:"text",header:_("search.blog.column.header.text"),width:260,dataIndex:"text",sortable:false,renderer:c.renderer.text},{id:"comments",header:_("search.blog.column.header.comments"),width:80,dataIndex:"comments",sortable:false,renderer:c.renderer.comments},{id:"updated",width:96,header:_("search.blog.column.header.updated"),dataIndex:"updated",sortable:true,renderer:c.renderer.updated}]);
b.resultsPanel={xtype:"grid",id:"search-results-"+d,border:false,autoHeight:true,width:a.settings.gridWidth,autoExpandColumn:"text",stateful:true,frame:false,stripeRows:true,viewConfig:{forceFit:true,enableRowBody:true,showPreview:true,scrollOffset:0},columnsText:_("search.columns.menu.columns"),sortAscText:_("search.columns.menu.sort_ascending"),sortDescText:_("search.columns.menu.sort_descending"),store:c.store.results,sm:new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn}),cm:b.columnModel,loadMask:false,bbar:new Ext.PagingToolbar({id:"search-pager-"+d,plugins:new Ext.ux.Andrie.pPageSize({variations:[10,25,50],beforeText:_("search.pagination.pagesize.before"),afterText:_("search.pagination.pagesize.after"),addBefore:_("search.pagination.pagesize.addbefore"),addAfter:_("search.pagination.pagesize.addafter")}),pageSize:25,store:c.store.results,displayInfo:true,displayMsg:_("search.pagination.displaying."+d),emptyMsg:_("search.find.no.results"),beforePageText:_("search.pagination.beforepage"),afterPageText:_("search.pagination.afterpage"),firstText:_("search.pagination.first"),prevText:_("search.pagination.prev"),nextText:_("search.pagination.next"),lastText:_("search.pagination.last"),refreshText:_("search.pagination.refresh")})};
b.mainPanel={xtype:"panel",id:"search-panel-"+d,autoHeight:true,items:[b.filterPanel,b.resultsPanel]};
b.doSearch=function(){a.util.doSearch(d)};a.util.registerTabTitleListener(d)};Ext.onReady(function(){b.init()
})})();(function(){var b="curriki";Ext.ns("Curriki.module.search.data."+b);var a=Curriki.module.search.data.curriki;
a.init=function(){console.log("data."+b+": init");a.store={};a.store.record=new Ext.data.Record.create([{name:"name"},{name:"updated"},{name:"url"},{name:"score"}]);
a.store.results=new Ext.data.Store({storeId:"search-store-"+b,proxy:new Ext.data.HttpProxy({url:document.location.pathname.endsWith("Old")?"/xwiki/bin/view/Search/Curriki":"/currikiExtjs",method:"GET"}),baseParams:{xpage:"plain",_dc:(new Date().getTime())},reader:new Ext.data.JsonReader({root:"rows",totalProperty:"resultCount"},a.store.record),remoteSort:true});
if(Curriki.userinfo.userGroups){a.store.results.baseParams.groupsId=Curriki.userinfo.userGroups
}if(Curriki.userinfo.userName){a.store.results.baseParams.userId=Curriki.userinfo.userName
}if(Curriki.userinfo.isAdmin){a.store.results.baseParams.isAdmin=true}if(Curriki.isISO8601DateParsing()){a.store.results.baseParams.dateFormat="ISO8601"
}a.store.results.setDefaultSort("score","desc");a.renderer={name:function(g,f,c,h,e,d){return String.format('<a href="{1}">{0}</a>',g,c.data.url)
},updated:function(h,f,c,i,e,d){if(typeof("value")!="string"){return""}var g=Ext.util.Format.date(h,"M-d-Y");
if(typeof(g)!="string"){return""}return String.format("{0}",g)},score:function(g,f,c,h,e,d){if(typeof(g)!="number"){g=0
}return g}}};Ext.onReady(function(){a.init()})})();(function(){var d="curriki";Ext.ns("Curriki.module.search.form."+d);
var a=Curriki.module.search;var b=a.form[d];var c=a.data[d];b.init=function(){console.log("form."+d+": init");
b.termPanel=a.util.createTermPanel(d,b);b.filterPanel={xtype:"form",labelAlign:"left",id:"search-filterPanel-"+d,formId:"search-filterForm-"+d,border:false,items:[b.termPanel]};
b.columnModel=new Ext.grid.ColumnModel([{id:"name",header:_("search.curriki.column.header.name"),width:500,dataIndex:"name",sortable:true,renderer:c.renderer.name},{id:"updated",width:96,header:_("search.curriki.column.header.updated"),dataIndex:"updated",sortable:true,renderer:c.renderer.updated}]);
b.resultsPanel={xtype:"grid",id:"search-results-"+d,border:false,autoHeight:true,width:a.settings.gridWidth,autoExpandColumn:"name",stateful:true,frame:false,stripeRows:true,viewConfig:{forceFit:true,enableRowBody:true,showPreview:true,scrollOffset:0},columnsText:_("search.columns.menu.columns"),sortAscText:_("search.columns.menu.sort_ascending"),sortDescText:_("search.columns.menu.sort_descending"),store:c.store.results,sm:new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn}),cm:b.columnModel,loadMask:false,bbar:new Ext.PagingToolbar({id:"search-pager-"+d,plugins:new Ext.ux.Andrie.pPageSize({variations:[10,25,50],beforeText:_("search.pagination.pagesize.before"),afterText:_("search.pagination.pagesize.after"),addBefore:_("search.pagination.pagesize.addbefore"),addAfter:_("search.pagination.pagesize.addafter")}),pageSize:25,store:c.store.results,displayInfo:true,displayMsg:_("search.pagination.displaying."+d),emptyMsg:_("search.find.no.results"),beforePageText:_("search.pagination.beforepage"),afterPageText:_("search.pagination.afterpage"),firstText:_("search.pagination.first"),prevText:_("search.pagination.prev"),nextText:_("search.pagination.next"),lastText:_("search.pagination.last"),refreshText:_("search.pagination.refresh")})};
b.mainPanel={xtype:"panel",id:"search-panel-"+d,autoHeight:true,items:[b.filterPanel,b.resultsPanel]};
b.doSearch=function(){a.util.doSearch(d)};a.util.registerTabTitleListener(d)};Ext.onReady(function(){b.init()
})})();if(!Array.prototype.indexOf){Array.prototype.indexOf=function(c){if(this==null){throw new TypeError()
}var e,b,d=Object(this),a=d.length>>>0;if(a===0){return -1}e=0;if(arguments.length>1){e=Number(arguments[1]);
if(e!=e){e=0}else{if(e!=0&&e!=Infinity&&e!=-Infinity){e=(e>0||-1)*Math.floor(Math.abs(e))
}}}if(e>=a){return -1}for(b=e>=0?e:Math.max(a-Math.abs(e),0);b<a;b++){if(b in d&&d[b]===c){return b
}}return -1}}(function(){Ext.ns("Curriki.module.search.form");var b=Curriki.module.search;
var a=b.form;b.init=function(){console.log("search: init");if(!Ext.isEmpty(b.initialized)){alert("Search module already initialized");
return}if(Ext.isEmpty(b.tabList)){b.tabList=["resource","outerResource","group","member","curriki"]
}var d=140;b.currentStateUrl=null;b.getState=function(){var h={};if(Ext.getCmp("search-termPanel")&&Ext.getCmp("search-termPanel").getForm){h.all=Ext.getCmp("search-termPanel").getForm().getValues(false)
}var f={};var g={};Ext.each(b.tabList,function(m){var l=a[m];if(!Ext.isEmpty(l)&&!Ext.isEmpty(l.doSearch)){var k=Ext.getCmp("search-filterPanel-"+m);
if(!Ext.isEmpty(k)){var n=k.getForm();if(!Ext.isEmpty(n)){h[m]=n.getValues(false);
if("undefined"!==typeof h[m]["terms"]&&h[m]["terms"]===_("search.text.entry.label")){delete (h[m]["terms"])
}if("undefined"!==typeof h[m]["other"]&&h[m]["other"]===""){delete (h[m]["other"])
}if(Ext.StoreMgr.lookup("search-store-"+m).sortInfo){h[m]["sort"]={};h[m]["sort"].field=Ext.StoreMgr.lookup("search-store-"+m).sortInfo.field;
h[m]["sort"].dir=Ext.StoreMgr.lookup("search-store-"+m).sortInfo.direction}}}var i=Ext.getCmp("search-advanced-"+m);
if(!Ext.isEmpty(i)){if(!i.collapsed){g[m]={a:true}}}var j=Ext.getCmp("search-pager-"+m);
if(!Ext.isEmpty(j)){var o={};o.c=j.cursor;o.s=j.pageSize;f[m]=o}}});var e={};e.s="all";
e.f=h;e.p=f;if(Ext.getCmp("search-tabPanel").getActiveTab){e.t=Ext.getCmp("search-tabPanel").getActiveTab().id
}e.a=g;return e};b.saveState=function(f){var e=b.getState();if((f==null)||(f==document.location.pathname)){return
}console.info("pushState:"+f);b.currentStateUrl=f;History.pushState(e,f,f);if(console){console.info("Added Token To History")
}};b.doSearch=function(k,i,g){var f=$("search-termPanel-"+k+"-terms").getValue();
if(f==_("search.text.entry.label")){f=""}if(document.savedTitle&&f!=""){document.title=document.savedTitle
}else{if(typeof(document.savedTitle)=="undefined"){document.savedTitle=document.title
}document.title=_("search.window.title."+k,[f])}try{var h=$("curriki-searchbox");
if(typeof(h)=="object"&&typeof(h.style)=="object"){h.style.color="lightgrey";h.value=f
}}catch(j){console.log("search: curriki-searchbox not found. (Ok in embedded mode)");
console.log("EmbeddedMode: "+Curriki.module.search.util.isInEmbeddedMode())}Curriki.numSearches=0;
Ext.each(b.tabList,function(n){var e=a[n];if(!Ext.isEmpty(e)&&!Ext.isEmpty(e.doSearch)){if((("undefined"===typeof g)||(g=false))&&(Ext.isEmpty(k)||k===n)){if(Curriki.numSearches>10){return
}var m=b.getState();var l=m.p;Curriki.numSearches++;b.util.doSearch(n,(("undefined"!==typeof l[n])?l[n].c:0))
}}})};b.tabPanel={xtype:(b.tabList.size()>1?"tab":"")+"panel",id:"search-tabPanel",activeTab:0,deferredRender:false,autoHeight:true,layoutOnTabChange:true,frame:false,border:false,plain:true,defaults:{autoScroll:false,border:false},listeners:{tabchange:function(h,g){var f=g.id.replace(/(^search-|-tab$)/g,"");
Curriki.logView("/features/search/"+f);var e=Ext.getCmp("search-advanced-"+f);if(!Ext.isEmpty(e)){if(!e.collapsed){Ext.select(".x-form-field-wrap",false,"search-advanced-"+f).setWidth(d)
}}}},items:[]};Ext.each(b.tabList,function(g){var e={title:_("search."+g+".tab.title"),id:"search-"+g+"-tab",cls:"search-"+g,autoHeight:true};
var f=a[g];if(!Ext.isEmpty(f)&&!Ext.isEmpty(f.mainPanel)){e.items=[f.mainPanel];b.tabPanel.items.push(e)
}else{console.log("Dropping "+g+" (module Curriki.module.search.form["+g+"] is empty).")
}});b.mainPanel={el:"search-div",border:false,height:"600px",defaults:{border:false},cls:"search-module",items:[b.tabPanel]};
Ext.ns("Curriki.module.search.history");var c=b.history;c.historyChange=function(e){if(e){if(console){console.info("Updated from History")
}c.updateFromHistory(e)}};c.updateFromHistory=function(g){var e=g.data;console.log("Got History: "+g.url,{values:e});
if(!Ext.isEmpty(e)){var i=e.f;if(!Ext.isEmpty(i)&&i.all&&Ext.getCmp("search-termPanel")&&Ext.getCmp("search-termPanel").getForm){Ext.getCmp("search-termPanel").getForm().setValues(i.all)
}var f=e.p;var h=e.a;if(e.t){if(Ext.getCmp("search-tabPanel").setActiveTab){Ext.getCmp("search-tabPanel").setActiveTab(e.t)
}}Ext.each(b.tabList,function(k){var j=b.form[k];if(!Ext.isEmpty(j)&&!Ext.isEmpty(j.doSearch)&&!Ext.isEmpty(i)&&!Ext.isEmpty(i[k])){var p=Ext.getCmp("search-filterPanel-"+k);
if(!Ext.isEmpty(p)){var l=p.getForm();if(!Ext.isEmpty(l)){try{l.setValues(i[k]);var n=Ext.getCmp("combo-subject-"+k);
if(n){n.fireEvent("select",n,n.getValue());if(!Ext.isEmpty(i[k].subject)){if(Ext.getCmp("combo-subsubject-"+k)){Ext.getCmp("combo-subsubject-"+k).setValue(i[k].subject)
}}}n=Ext.getCmp("combo-ictprfx-"+k);if(n){n.fireEvent("select",n,n.getValue());if(!Ext.isEmpty(i[k].ict)){if(Ext.getCmp("combo-subICT-"+k)){Ext.getCmp("combo-subICT-"+k).setValue(i[k].ict)
}}}if(i[k]["sort"]){var r=i[k]["sort"];if(r.field){Ext.StoreMgr.lookup("search-store-"+k).sortInfo.field=r.field
}if(r.dir){Ext.StoreMgr.lookup("search-store-"+k).sortInfo.direction=r.dir}}}catch(m){console.log("ERROR Updating "+k,m)
}}}if(!Ext.isEmpty(h)&&!Ext.isEmpty(h[k])&&h[k].a){var o=Ext.getCmp("search-advanced-"+k);
if(!Ext.isEmpty(o)){o.expand(false)}}var q=Ext.getCmp("search-pager-"+k);if(!Ext.isEmpty(q)&&!Ext.isEmpty(f)){if(f[k]){try{if(f[k]["c"]){q.cursor=f[k]["c"]
}if(f[k]["s"]){if(q.pageSize!=f[k]["s"]){q.setPageSize(f[k]["s"])}}}catch(m){console.log("ERROR Updating "+k,m)
}}}}});if(e.s){console.info("Starting search");if(e.s==="all"){b.doSearch(e.t.split("-")[1])
}else{b.doSearch(e.s)}}}};c.parseInitialStateOld=function(g){var e=Ext.History.getToken();
if(e==null){return false}var f=new Ext.state.Provider();var h=f.decodeValue(e);g=Ext.apply(g,h,{},true);
return true};c.parseInitialStateNew=function(e,g){function f(n){n=n.split("+").join(" ");
var q={},p,o=/[?&]?([^=]+)=([^&]*)/g;while(p=o.exec(n)){q[decodeURIComponent(p[1])]=decodeURIComponent(p[2])
}return q}var m=f(document.location.search);var g=m.state;if(typeof g=="undefined"){return false
}var h=g.split("/");var k=b.util.logFilterList;var l="";var j="";var i="";while(typeof i!="undefined"){j=i;
i=h.shift();switch(i){case"":case"features":case"advanced":case"simple":l="";continue;
break;case"search":l="AwaitingForTab";continue;break;case"sort":l="AwaitingFSortField";
continue;break;case"start":l="AwaitingForStart";continue;break;case"rows":l="AwaitingForRows";
continue;break;case"dir":l="AwaitingFDirField";continue;break;default:if((b.currentTab)&&(k[b.currentTab].indexOf(i)>=0)){l="AwaitingFValue";
continue}}switch(l){case"AwaitingForTab":b.currentTab=i;e.t="search-"+i+"-tab";l="AwaitingForSearchString";
break;case"AwaitingForSearchString":e.f[b.currentTab]["terms"]=i;break;case"AwaitingFValue":e.f[b.currentTab][j]=i;
break;case"AwaitingFSortField":e.f[b.currentTab]["sort"]["field"]=i;break;case"AwaitingFDirField":e.f[b.currentTab]["sort"]["dir"]=i;
break;case"AwaitingForStart":e.p[b.currentTab]["c"]=i;e.p[b.currentTab]["s"]=25;l="";
case"AwaitingForRows":e.p[b.currentTab]["s"]=i;l=""}}return true};c.init=function(){if(Ext.isEmpty(c.initialized)){var f=b.getState();
var e="";if(!History.enabled){alert("your browser is too old to support history, sorry");
return}History.Adapter.bind(window,"statechange",function(){var g=History.getState(true,true);
History.log("statechange:",g.data,g.title,g.url);if(g.url.indexOf(b.currentStateUrl)==-1){console.log("CURRENTSTATE==>  "+b.currentStateUrl);
console.log("NEWSTATE==>  "+g.url);c.historyChange(g);b.currentStateUrl=g.url}});
c.initialized=true;if(c.parseInitialStateNew(f,e)){History.pushState(f,"?state="+e,"?state="+e)
}else{if(c.parseInitialStateOld(f)){History.pushState(f,"?state=/","?state=/")}}}};
b.initialized=true;console.log("search: init done")};b.display=function(){b.init();
var c=new Ext.Panel(b.mainPanel);c.render();if(Curriki.module.search.util.isInEmbeddedMode()){Curriki.module.search.util.sendResizeMessageToEmbeddingWindow()
}b.history.init()};b.start=function(){Ext.onReady(function(){b.display()})}})();