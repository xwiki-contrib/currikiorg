/*
    json2.js
    2012-10-08

    Public Domain.

    NO WARRANTY EXPRESSED OR IMPLIED. USE AT YOUR OWN RISK.

    See http://www.JSON.org/js.html


    This code should be minified before deployment.
    See http://javascript.crockford.com/jsmin.html

    USE YOUR OWN COPY. IT IS EXTREMELY UNWISE TO LOAD CODE FROM SERVERS YOU DO
    NOT CONTROL.


    This file creates a global JSON object containing two methods: stringify
    and parse.

        JSON.stringify(value, replacer, space)
            value       any JavaScript value, usually an object or array.

            replacer    an optional parameter that determines how object
                        values are stringified for objects. It can be a
                        function or an array of strings.

            space       an optional parameter that specifies the indentation
                        of nested structures. If it is omitted, the text will
                        be packed without extra whitespace. If it is a number,
                        it will specify the number of spaces to indent at each
                        level. If it is a string (such as '\t' or '&nbsp;'),
                        it contains the characters used to indent at each level.

            This method produces a JSON text from a JavaScript value.

            When an object value is found, if the object contains a toJSON
            method, its toJSON method will be called and the result will be
            stringified. A toJSON method does not serialize: it returns the
            value represented by the name/value pair that should be serialized,
            or undefined if nothing should be serialized. The toJSON method
            will be passed the key associated with the value, and this will be
            bound to the value

            For example, this would serialize Dates as ISO strings.

                Date.prototype.toJSON = function (key) {
                    function f(n) {
                        // Format integers to have at least two digits.
                        return n < 10 ? '0' + n : n;
                    }

                    return this.getUTCFullYear()   + '-' +
                         f(this.getUTCMonth() + 1) + '-' +
                         f(this.getUTCDate())      + 'T' +
                         f(this.getUTCHours())     + ':' +
                         f(this.getUTCMinutes())   + ':' +
                         f(this.getUTCSeconds())   + 'Z';
                };

            You can provide an optional replacer method. It will be passed the
            key and value of each member, with this bound to the containing
            object. The value that is returned from your method will be
            serialized. If your method returns undefined, then the member will
            be excluded from the serialization.

            If the replacer parameter is an array of strings, then it will be
            used to select the members to be serialized. It filters the results
            such that only members with keys listed in the replacer array are
            stringified.

            Values that do not have JSON representations, such as undefined or
            functions, will not be serialized. Such values in objects will be
            dropped; in arrays they will be replaced with null. You can use
            a replacer function to replace those with JSON values.
            JSON.stringify(undefined) returns undefined.

            The optional space parameter produces a stringification of the
            value that is filled with line breaks and indentation to make it
            easier to read.

            If the space parameter is a non-empty string, then that string will
            be used for indentation. If the space parameter is a number, then
            the indentation will be that many spaces.

            Example:

            text = JSON.stringify(['e', {pluribus: 'unum'}]);
            // text is '["e",{"pluribus":"unum"}]'


            text = JSON.stringify(['e', {pluribus: 'unum'}], null, '\t');
            // text is '[\n\t"e",\n\t{\n\t\t"pluribus": "unum"\n\t}\n]'

            text = JSON.stringify([new Date()], function (key, value) {
                return this[key] instanceof Date ?
                    'Date(' + this[key] + ')' : value;
            });
            // text is '["Date(---current time---)"]'


        JSON.parse(text, reviver)
            This method parses a JSON text to produce an object or array.
            It can throw a SyntaxError exception.

            The optional reviver parameter is a function that can filter and
            transform the results. It receives each of the keys and values,
            and its return value is used instead of the original value.
            If it returns what it received, then the structure is not modified.
            If it returns undefined then the member is deleted.

            Example:

            // Parse the text. Values that look like ISO date strings will
            // be converted to Date objects.

            myData = JSON.parse(text, function (key, value) {
                var a;
                if (typeof value === 'string') {
                    a =
/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2}(?:\.\d*)?)Z$/.exec(value);
                    if (a) {
                        return new Date(Date.UTC(+a[1], +a[2] - 1, +a[3], +a[4],
                            +a[5], +a[6]));
                    }
                }
                return value;
            });

            myData = JSON.parse('["Date(09/09/2001)"]', function (key, value) {
                var d;
                if (typeof value === 'string' &&
                        value.slice(0, 5) === 'Date(' &&
                        value.slice(-1) === ')') {
                    d = new Date(value.slice(5, -1));
                    if (d) {
                        return d;
                    }
                }
                return value;
            });


    This is a reference implementation. You are free to copy, modify, or
    redistribute.
*/

/*jslint evil: true, regexp: true */

/*members "", "\b", "\t", "\n", "\f", "\r", "\"", JSON, "\\", apply,
    call, charCodeAt, getUTCDate, getUTCFullYear, getUTCHours,
    getUTCMinutes, getUTCMonth, getUTCSeconds, hasOwnProperty, join,
    lastIndex, length, parse, prototype, push, replace, slice, stringify,
    test, toJSON, toString, valueOf
*/


// Create a JSON object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (typeof JSON !== 'object') {
    JSON = {};
}

(function () {
    'use strict';

    function f(n) {
        // Format integers to have at least two digits.
        return n < 10 ? '0' + n : n;
    }

    if (typeof Date.prototype.toJSON !== 'function') {

        Date.prototype.toJSON = function (key) {

            return isFinite(this.valueOf())
                ? this.getUTCFullYear()     + '-' +
                    f(this.getUTCMonth() + 1) + '-' +
                    f(this.getUTCDate())      + 'T' +
                    f(this.getUTCHours())     + ':' +
                    f(this.getUTCMinutes())   + ':' +
                    f(this.getUTCSeconds())   + 'Z'
                : null;
        };

        String.prototype.toJSON      =
            Number.prototype.toJSON  =
            Boolean.prototype.toJSON = function (key) {
                return this.valueOf();
            };
    }

    var cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        gap,
        indent,
        meta = {    // table of character substitutions
            '\b': '\\b',
            '\t': '\\t',
            '\n': '\\n',
            '\f': '\\f',
            '\r': '\\r',
            '"' : '\\"',
            '\\': '\\\\'
        },
        rep;


    function quote(string) {

// If the string contains no control characters, no quote characters, and no
// backslash characters, then we can safely slap some quotes around it.
// Otherwise we must also replace the offending characters with safe escape
// sequences.

        escapable.lastIndex = 0;
        return escapable.test(string) ? '"' + string.replace(escapable, function (a) {
            var c = meta[a];
            return typeof c === 'string'
                ? c
                : '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
        }) + '"' : '"' + string + '"';
    }


    function str(key, holder) {

// Produce a string from holder[key].

        var i,          // The loop counter.
            k,          // The member key.
            v,          // The member value.
            length,
            mind = gap,
            partial,
            value = holder[key];

// If the value has a toJSON method, call it to obtain a replacement value.

        if (value && typeof value === 'object' &&
                typeof value.toJSON === 'function') {
            value = value.toJSON(key);
        }

// If we were called with a replacer function, then call the replacer to
// obtain a replacement value.

        if (typeof rep === 'function') {
            value = rep.call(holder, key, value);
        }

// What happens next depends on the value's type.

        switch (typeof value) {
        case 'string':
            return quote(value);

        case 'number':

// JSON numbers must be finite. Encode non-finite numbers as null.

            return isFinite(value) ? String(value) : 'null';

        case 'boolean':
        case 'null':

// If the value is a boolean or null, convert it to a string. Note:
// typeof null does not produce 'null'. The case is included here in
// the remote chance that this gets fixed someday.

            return String(value);

// If the type is 'object', we might be dealing with an object or an array or
// null.

        case 'object':

// Due to a specification blunder in ECMAScript, typeof null is 'object',
// so watch out for that case.

            if (!value) {
                return 'null';
            }

// Make an array to hold the partial results of stringifying this object value.

            gap += indent;
            partial = [];

// Is the value an array?

            if (Object.prototype.toString.apply(value) === '[object Array]') {

// The value is an array. Stringify every element. Use null as a placeholder
// for non-JSON values.

                length = value.length;
                for (i = 0; i < length; i += 1) {
                    partial[i] = str(i, value) || 'null';
                }

// Join all of the elements together, separated with commas, and wrap them in
// brackets.

                v = partial.length === 0
                    ? '[]'
                    : gap
                    ? '[\n' + gap + partial.join(',\n' + gap) + '\n' + mind + ']'
                    : '[' + partial.join(',') + ']';
                gap = mind;
                return v;
            }

// If the replacer is an array, use it to select the members to be stringified.

            if (rep && typeof rep === 'object') {
                length = rep.length;
                for (i = 0; i < length; i += 1) {
                    if (typeof rep[i] === 'string') {
                        k = rep[i];
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            } else {

// Otherwise, iterate through all of the keys in the object.

                for (k in value) {
                    if (Object.prototype.hasOwnProperty.call(value, k)) {
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            }

// Join all of the member texts together, separated with commas,
// and wrap them in braces.

            v = partial.length === 0
                ? '{}'
                : gap
                ? '{\n' + gap + partial.join(',\n' + gap) + '\n' + mind + '}'
                : '{' + partial.join(',') + '}';
            gap = mind;
            return v;
        }
    }

// If the JSON object does not yet have a stringify method, give it one.

    if (typeof JSON.stringify !== 'function') {
        JSON.stringify = function (value, replacer, space) {

// The stringify method takes a value and an optional replacer, and an optional
// space parameter, and returns a JSON text. The replacer can be a function
// that can replace values, or an array of strings that will select the keys.
// A default replacer method can be provided. Use of the space parameter can
// produce text that is more easily readable.

            var i;
            gap = '';
            indent = '';

// If the space parameter is a number, make an indent string containing that
// many spaces.

            if (typeof space === 'number') {
                for (i = 0; i < space; i += 1) {
                    indent += ' ';
                }

// If the space parameter is a string, it will be used as the indent string.

            } else if (typeof space === 'string') {
                indent = space;
            }

// If there is a replacer, it must be a function or an array.
// Otherwise, throw an error.

            rep = replacer;
            if (replacer && typeof replacer !== 'function' &&
                    (typeof replacer !== 'object' ||
                    typeof replacer.length !== 'number')) {
                throw new Error('JSON.stringify');
            }

// Make a fake root object containing our value under the key of ''.
// Return the result of stringifying the value.

            return str('', {'': value});
        };
    }


// If the JSON object does not yet have a parse method, give it one.

    if (typeof JSON.parse !== 'function') {
        JSON.parse = function (text, reviver) {

// The parse method takes a text and an optional reviver function, and returns
// a JavaScript value if the text is a valid JSON text.

            var j;

            function walk(holder, key) {

// The walk method is used to recursively walk the resulting structure so
// that modifications can be made.

                var k, v, value = holder[key];
                if (value && typeof value === 'object') {
                    for (k in value) {
                        if (Object.prototype.hasOwnProperty.call(value, k)) {
                            v = walk(value, k);
                            if (v !== undefined) {
                                value[k] = v;
                            } else {
                                delete value[k];
                            }
                        }
                    }
                }
                return reviver.call(holder, key, value);
            }


// Parsing happens in four stages. In the first stage, we replace certain
// Unicode characters with escape sequences. JavaScript handles many characters
// incorrectly, either silently deleting them, or treating them as line endings.

            text = String(text);
            cx.lastIndex = 0;
            if (cx.test(text)) {
                text = text.replace(cx, function (a) {
                    return '\\u' +
                        ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
                });
            }

// In the second stage, we run the text against regular expressions that look
// for non-JSON patterns. We are especially concerned with '()' and 'new'
// because they can cause invocation, and '=' because it can cause mutation.
// But just to be safe, we want to reject all unexpected forms.

// We split the second stage into 4 regexp operations in order to work around
// crippling inefficiencies in IE's and Safari's regexp engines. First we
// replace the JSON backslash pairs with '@' (a non-JSON character). Second, we
// replace all simple value tokens with ']' characters. Third, we delete all
// open brackets that follow a colon or comma or that begin the text. Finally,
// we look to see that the remaining characters are only whitespace or ']' or
// ',' or ':' or '{' or '}'. If that is so, then the text is safe for eval.

            if (/^[\],:{}\s]*$/
                    .test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@')
                        .replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']')
                        .replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {

// In the third stage we use the eval function to compile the text into a
// JavaScript structure. The '{' operator is subject to a syntactic ambiguity
// in JavaScript: it can begin a block or an object literal. We wrap the text
// in parens to eliminate the ambiguity.

                j = eval('(' + text + ')');

// In the optional fourth stage, we recursively walk the new structure, passing
// each name/value pair to a reviver function for possible transformation.

                return typeof reviver === 'function'
                    ? walk({'': j}, '')
                    : j;
            }

// If the text is not JSON parseable, then a SyntaxError is thrown.

            throw new SyntaxError('JSON.parse');
        };
    }
}());/**
 * History.js Native Adapter
 * @author Benjamin Arthur Lupton <contact@balupton.com>
 * @copyright 2010-2011 Benjamin Arthur Lupton <contact@balupton.com>
 * @license New BSD License <http://creativecommons.org/licenses/BSD/>
 */

// Closure
(function(window,undefined){
	"use strict";

	// Localise Globals
	var History = window.History = window.History||{};

	// Check Existence
	if ( typeof History.Adapter !== 'undefined' ) {
		throw new Error('History.js Adapter has already been loaded...');
	}

	// Add the Adapter
	History.Adapter = {
		/**
		 * History.Adapter.handlers[uid][eventName] = Array
		 */
		handlers: {},

		/**
		 * History.Adapter._uid
		 * The current element unique identifier
		 */
		_uid: 1,

		/**
		 * History.Adapter.uid(element)
		 * @param {Element} element
		 * @return {String} uid
		 */
		uid: function(element){
			return element._uid || (element._uid = History.Adapter._uid++);
		},

		/**
		 * History.Adapter.bind(el,event,callback)
		 * @param {Element} element
		 * @param {String} eventName - custom and standard events
		 * @param {Function} callback
		 * @return
		 */
		bind: function(element,eventName,callback){
			// Prepare
			var uid = History.Adapter.uid(element);

			// Apply Listener
			History.Adapter.handlers[uid] = History.Adapter.handlers[uid] || {};
			History.Adapter.handlers[uid][eventName] = History.Adapter.handlers[uid][eventName] || [];
			History.Adapter.handlers[uid][eventName].push(callback);

			// Bind Global Listener
			element['on'+eventName] = (function(element,eventName){
				return function(event){
					History.Adapter.trigger(element,eventName,event);
				};
			})(element,eventName);
		},

		/**
		 * History.Adapter.trigger(el,event)
		 * @param {Element} element
		 * @param {String} eventName - custom and standard events
		 * @param {Object} event - a object of event data
		 * @return
		 */
		trigger: function(element,eventName,event){
			// Prepare
			event = event || {};
			var uid = History.Adapter.uid(element),
				i,n;

			// Apply Listener
			History.Adapter.handlers[uid] = History.Adapter.handlers[uid] || {};
			History.Adapter.handlers[uid][eventName] = History.Adapter.handlers[uid][eventName] || [];

			// Fire Listeners
			for ( i=0,n=History.Adapter.handlers[uid][eventName].length; i<n; ++i ) {
				History.Adapter.handlers[uid][eventName][i].apply(this,[event]);
			}
		},

		/**
		 * History.Adapter.extractEventData(key,event,extra)
		 * @param {String} key - key for the event data to extract
		 * @param {String} event - custom and standard events
		 * @return {mixed}
		 */
		extractEventData: function(key,event){
			var result = (event && event[key]) || undefined;
			return result;
		},

		/**
		 * History.Adapter.onDomLoad(callback)
		 * @param {Function} callback
		 * @return
		 */
		onDomLoad: function(callback) {
			var timeout = window.setTimeout(function(){
				callback();
			},2000);
			window.onload = function(){
				clearTimeout(timeout);
				callback();
			};
		}
	};

	// Try to Initialise History
	if ( typeof History.init !== 'undefined' ) {
		History.init();
	}

})(window);
/**
 * History.js HTML4 Support
 * Depends on the HTML5 Support
 * @author Benjamin Arthur Lupton <contact@balupton.com>
 * @copyright 2010-2011 Benjamin Arthur Lupton <contact@balupton.com>
 * @license New BSD License <http://creativecommons.org/licenses/BSD/>
 */

(function(window,undefined){
	"use strict";

	// ========================================================================
	// Initialise

	// Localise Globals
	var
		document = window.document, // Make sure we are using the correct document
		setTimeout = window.setTimeout||setTimeout,
		clearTimeout = window.clearTimeout||clearTimeout,
		setInterval = window.setInterval||setInterval,
		History = window.History = window.History||{}; // Public History Object

	// Check Existence
	if ( typeof History.initHtml4 !== 'undefined' ) {
		throw new Error('History.js HTML4 Support has already been loaded...');
	}


	// ========================================================================
	// Initialise HTML4 Support

	// Initialise HTML4 Support
	History.initHtml4 = function(){
		// Initialise
		if ( typeof History.initHtml4.initialized !== 'undefined' ) {
			// Already Loaded
			return false;
		}
		else {
			History.initHtml4.initialized = true;
		}


		// ====================================================================
		// Properties

		/**
		 * History.enabled
		 * Is History enabled?
		 */
		History.enabled = true;


		// ====================================================================
		// Hash Storage

		/**
		 * History.savedHashes
		 * Store the hashes in an array
		 */
		History.savedHashes = [];

		/**
		 * History.isLastHash(newHash)
		 * Checks if the hash is the last hash
		 * @param {string} newHash
		 * @return {boolean} true
		 */
		History.isLastHash = function(newHash){
			// Prepare
			var oldHash = History.getHashByIndex(),
				isLast;

			// Check
			isLast = newHash === oldHash;

			// Return isLast
			return isLast;
		};

		/**
		 * History.isHashEqual(newHash, oldHash)
		 * Checks to see if two hashes are functionally equal
		 * @param {string} newHash
		 * @param {string} oldHash
		 * @return {boolean} true
		 */
		History.isHashEqual = function(newHash, oldHash){
			newHash = encodeURIComponent(newHash).replace(/%25/g, "%");
			oldHash = encodeURIComponent(oldHash).replace(/%25/g, "%");
			return newHash === oldHash;
		};

		/**
		 * History.saveHash(newHash)
		 * Push a Hash
		 * @param {string} newHash
		 * @return {boolean} true
		 */
		History.saveHash = function(newHash){
			// Check Hash
			if ( History.isLastHash(newHash) ) {
				return false;
			}

			// Push the Hash
			History.savedHashes.push(newHash);

			// Return true
			return true;
		};

		/**
		 * History.getHashByIndex()
		 * Gets a hash by the index
		 * @param {integer} index
		 * @return {string}
		 */
		History.getHashByIndex = function(index){
			// Prepare
			var hash = null;

			// Handle
			if ( typeof index === 'undefined' ) {
				// Get the last inserted
				hash = History.savedHashes[History.savedHashes.length-1];
			}
			else if ( index < 0 ) {
				// Get from the end
				hash = History.savedHashes[History.savedHashes.length+index];
			}
			else {
				// Get from the beginning
				hash = History.savedHashes[index];
			}

			// Return hash
			return hash;
		};


		// ====================================================================
		// Discarded States

		/**
		 * History.discardedHashes
		 * A hashed array of discarded hashes
		 */
		History.discardedHashes = {};

		/**
		 * History.discardedStates
		 * A hashed array of discarded states
		 */
		History.discardedStates = {};

		/**
		 * History.discardState(State)
		 * Discards the state by ignoring it through History
		 * @param {object} State
		 * @return {true}
		 */
		History.discardState = function(discardedState,forwardState,backState){
			//History.debug('History.discardState', arguments);
			// Prepare
			var discardedStateHash = History.getHashByState(discardedState),
				discardObject;

			// Create Discard Object
			discardObject = {
				'discardedState': discardedState,
				'backState': backState,
				'forwardState': forwardState
			};

			// Add to DiscardedStates
			History.discardedStates[discardedStateHash] = discardObject;

			// Return true
			return true;
		};

		/**
		 * History.discardHash(hash)
		 * Discards the hash by ignoring it through History
		 * @param {string} hash
		 * @return {true}
		 */
		History.discardHash = function(discardedHash,forwardState,backState){
			//History.debug('History.discardState', arguments);
			// Create Discard Object
			var discardObject = {
				'discardedHash': discardedHash,
				'backState': backState,
				'forwardState': forwardState
			};

			// Add to discardedHash
			History.discardedHashes[discardedHash] = discardObject;

			// Return true
			return true;
		};

		/**
		 * History.discardedState(State)
		 * Checks to see if the state is discarded
		 * @param {object} State
		 * @return {bool}
		 */
		History.discardedState = function(State){
			// Prepare
			var StateHash = History.getHashByState(State),
				discarded;

			// Check
			discarded = History.discardedStates[StateHash]||false;

			// Return true
			return discarded;
		};

		/**
		 * History.discardedHash(hash)
		 * Checks to see if the state is discarded
		 * @param {string} State
		 * @return {bool}
		 */
		History.discardedHash = function(hash){
			// Check
			var discarded = History.discardedHashes[hash]||false;

			// Return true
			return discarded;
		};

		/**
		 * History.recycleState(State)
		 * Allows a discarded state to be used again
		 * @param {object} data
		 * @param {string} title
		 * @param {string} url
		 * @return {true}
		 */
		History.recycleState = function(State){
			//History.debug('History.recycleState', arguments);
			// Prepare
			var StateHash = History.getHashByState(State);

			// Remove from DiscardedStates
			if ( History.discardedState(State) ) {
				delete History.discardedStates[StateHash];
			}

			// Return true
			return true;
		};


		// ====================================================================
		// HTML4 HashChange Support

		if ( History.emulated.hashChange ) {
			/*
			 * We must emulate the HTML4 HashChange Support by manually checking for hash changes
			 */

			/**
			 * History.hashChangeInit()
			 * Init the HashChange Emulation
			 */
			History.hashChangeInit = function(){
				// Define our Checker Function
				History.checkerFunction = null;

				// Define some variables that will help in our checker function
				var lastDocumentHash = '',
					iframeId, iframe,
					lastIframeHash, checkerRunning,
					startedWithHash = Boolean(History.getHash());

				// Handle depending on the browser
				if ( History.isInternetExplorer() ) {
					// IE6 and IE7
					// We need to use an iframe to emulate the back and forward buttons

					// Create iFrame
					iframeId = 'historyjs-iframe';
					iframe = document.createElement('iframe');

					// Adjust iFarme
					// IE 6 requires iframe to have a src on HTTPS pages, otherwise it will throw a
					// "This page contains both secure and nonsecure items" warning.
					iframe.setAttribute('id', iframeId);
					iframe.setAttribute('src', '#');
					iframe.style.display = 'none';

					// Append iFrame
					document.body.appendChild(iframe);

					// Create initial history entry
					iframe.contentWindow.document.open();
					iframe.contentWindow.document.close();

					// Define some variables that will help in our checker function
					lastIframeHash = '';
					checkerRunning = false;

					// Define the checker function
					History.checkerFunction = function(){
						// Check Running
						if ( checkerRunning ) {
							return false;
						}

						// Update Running
						checkerRunning = true;

						// Fetch
						var
							documentHash = History.getHash(),
							iframeHash = History.getHash(iframe.contentWindow.document);

						// The Document Hash has changed (application caused)
						if ( documentHash !== lastDocumentHash ) {
							// Equalise
							lastDocumentHash = documentHash;

							// Create a history entry in the iframe
							if ( iframeHash !== documentHash ) {
								//History.debug('hashchange.checker: iframe hash change', 'documentHash (new):', documentHash, 'iframeHash (old):', iframeHash);

								// Equalise
								lastIframeHash = iframeHash = documentHash;

								// Create History Entry
								iframe.contentWindow.document.open();
								iframe.contentWindow.document.close();

								// Update the iframe's hash
								iframe.contentWindow.document.location.hash = History.escapeHash(documentHash);
							}

							// Trigger Hashchange Event
							History.Adapter.trigger(window,'hashchange');
						}

						// The iFrame Hash has changed (back button caused)
						else if ( iframeHash !== lastIframeHash ) {
							//History.debug('hashchange.checker: iframe hash out of sync', 'iframeHash (new):', iframeHash, 'documentHash (old):', documentHash);

							// Equalise
							lastIframeHash = iframeHash;
							
							// If there is no iframe hash that means we're at the original
							// iframe state.
							// And if there was a hash on the original request, the original
							// iframe state was replaced instantly, so skip this state and take
							// the user back to where they came from.
							if (startedWithHash && iframeHash === '') {
								History.back();
							}
							else {
								// Update the Hash
								History.setHash(iframeHash,false);
							}
						}

						// Reset Running
						checkerRunning = false;

						// Return true
						return true;
					};
				}
				else {
					// We are not IE
					// Firefox 1 or 2, Opera

					// Define the checker function
					History.checkerFunction = function(){
						// Prepare
						var documentHash = History.getHash()||'';

						// The Document Hash has changed (application caused)
						if ( documentHash !== lastDocumentHash ) {
							// Equalise
							lastDocumentHash = documentHash;

							// Trigger Hashchange Event
							History.Adapter.trigger(window,'hashchange');
						}

						// Return true
						return true;
					};
				}

				// Apply the checker function
				History.intervalList.push(setInterval(History.checkerFunction, History.options.hashChangeInterval));

				// Done
				return true;
			}; // History.hashChangeInit

			// Bind hashChangeInit
			History.Adapter.onDomLoad(History.hashChangeInit);

		} // History.emulated.hashChange


		// ====================================================================
		// HTML5 State Support

		// Non-Native pushState Implementation
		if ( History.emulated.pushState ) {
			/*
			 * We must emulate the HTML5 State Management by using HTML4 HashChange
			 */

			/**
			 * History.onHashChange(event)
			 * Trigger HTML5's window.onpopstate via HTML4 HashChange Support
			 */
			History.onHashChange = function(event){
				//History.debug('History.onHashChange', arguments);

				// Prepare
				var currentUrl = ((event && event.newURL) || History.getLocationHref()),
					currentHash = History.getHashByUrl(currentUrl),
					currentState = null,
					currentStateHash = null,
					currentStateHashExits = null,
					discardObject;

				// Check if we are the same state
				if ( History.isLastHash(currentHash) ) {
					// There has been no change (just the page's hash has finally propagated)
					//History.debug('History.onHashChange: no change');
					History.busy(false);
					return false;
				}

				// Reset the double check
				History.doubleCheckComplete();

				// Store our location for use in detecting back/forward direction
				History.saveHash(currentHash);

				// Expand Hash
				if ( currentHash && History.isTraditionalAnchor(currentHash) ) {
					//History.debug('History.onHashChange: traditional anchor', currentHash);
					// Traditional Anchor Hash
					History.Adapter.trigger(window,'anchorchange');
					History.busy(false);
					return false;
				}

				// Create State
				currentState = History.extractState(History.getFullUrl(currentHash||History.getLocationHref()),true);

				// Check if we are the same state
				if ( History.isLastSavedState(currentState) ) {
					//History.debug('History.onHashChange: no change');
					// There has been no change (just the page's hash has finally propagated)
					History.busy(false);
					return false;
				}

				// Create the state Hash
				currentStateHash = History.getHashByState(currentState);

				// Check if we are DiscardedState
				discardObject = History.discardedState(currentState);
				if ( discardObject ) {
					// Ignore this state as it has been discarded and go back to the state before it
					if ( History.getHashByIndex(-2) === History.getHashByState(discardObject.forwardState) ) {
						// We are going backwards
						//History.debug('History.onHashChange: go backwards');
						History.back(false);
					} else {
						// We are going forwards
						//History.debug('History.onHashChange: go forwards');
						History.forward(false);
					}
					return false;
				}

				// Push the new HTML5 State
				//History.debug('History.onHashChange: success hashchange');
				History.pushState(currentState.data,currentState.title,encodeURI(currentState.url),false);

				// End onHashChange closure
				return true;
			};
			History.Adapter.bind(window,'hashchange',History.onHashChange);

			/**
			 * History.pushState(data,title,url)
			 * Add a new State to the history object, become it, and trigger onpopstate
			 * We have to trigger for HTML4 compatibility
			 * @param {object} data
			 * @param {string} title
			 * @param {string} url
			 * @return {true}
			 */
			History.pushState = function(data,title,url,queue){
				//History.debug('History.pushState: called', arguments);

				// We assume that the URL passed in is URI-encoded, but this makes
				// sure that it's fully URI encoded; any '%'s that are encoded are
				// converted back into '%'s
				url = encodeURI(url).replace(/%25/g, "%");

				// Check the State
				if ( History.getHashByUrl(url) ) {
					throw new Error('History.js does not support states with fragment-identifiers (hashes/anchors).');
				}

				// Handle Queueing
				if ( queue !== false && History.busy() ) {
					// Wait + Push to Queue
					//History.debug('History.pushState: we must wait', arguments);
					History.pushQueue({
						scope: History,
						callback: History.pushState,
						args: arguments,
						queue: queue
					});
					return false;
				}

				// Make Busy
				History.busy(true);

				// Fetch the State Object
				var newState = History.createStateObject(data,title,url),
					newStateHash = History.getHashByState(newState),
					oldState = History.getState(false),
					oldStateHash = History.getHashByState(oldState),
					html4Hash = History.getHash(),
					wasExpected = History.expectedStateId == newState.id;

				// Store the newState
				History.storeState(newState);
				History.expectedStateId = newState.id;

				// Recycle the State
				History.recycleState(newState);

				// Force update of the title
				History.setTitle(newState);

				// Check if we are the same State
				if ( newStateHash === oldStateHash ) {
					//History.debug('History.pushState: no change', newStateHash);
					History.busy(false);
					return false;
				}

				// Update HTML5 State
				History.saveState(newState);

				// Fire HTML5 Event
				if(!wasExpected)
					History.Adapter.trigger(window,'statechange');

				// Update HTML4 Hash
				if ( !History.isHashEqual(newStateHash, html4Hash) && !History.isHashEqual(newStateHash, History.getShortUrl(History.getLocationHref())) ) {
					History.setHash(newStateHash,false);
				}
				
				History.busy(false);

				// End pushState closure
				return true;
			};

			/**
			 * History.replaceState(data,title,url)
			 * Replace the State and trigger onpopstate
			 * We have to trigger for HTML4 compatibility
			 * @param {object} data
			 * @param {string} title
			 * @param {string} url
			 * @return {true}
			 */
			History.replaceState = function(data,title,url,queue){
				//History.debug('History.replaceState: called', arguments);

				// We assume that the URL passed in is URI-encoded, but this makes
				// sure that it's fully URI encoded; any '%'s that are encoded are
				// converted back into '%'s
				url = encodeURI(url).replace(/%25/g, "%");

				// Check the State
				if ( History.getHashByUrl(url) ) {
					throw new Error('History.js does not support states with fragment-identifiers (hashes/anchors).');
				}

				// Handle Queueing
				if ( queue !== false && History.busy() ) {
					// Wait + Push to Queue
					//History.debug('History.replaceState: we must wait', arguments);
					History.pushQueue({
						scope: History,
						callback: History.replaceState,
						args: arguments,
						queue: queue
					});
					return false;
				}

				// Make Busy
				History.busy(true);

				// Fetch the State Objects
				var newState        = History.createStateObject(data,title,url),
					newStateHash = History.getHashByState(newState),
					oldState        = History.getState(false),
					oldStateHash = History.getHashByState(oldState),
					previousState   = History.getStateByIndex(-2);

				// Discard Old State
				History.discardState(oldState,newState,previousState);

				// If the url hasn't changed, just store and save the state
				// and fire a statechange event to be consistent with the
				// html 5 api
				if ( newStateHash === oldStateHash ) {
					// Store the newState
					History.storeState(newState);
					History.expectedStateId = newState.id;
	
					// Recycle the State
					History.recycleState(newState);
	
					// Force update of the title
					History.setTitle(newState);
					
					// Update HTML5 State
					History.saveState(newState);

					// Fire HTML5 Event
					//History.debug('History.pushState: trigger popstate');
					History.Adapter.trigger(window,'statechange');
					History.busy(false);
				}
				else {
					// Alias to PushState
					History.pushState(newState.data,newState.title,newState.url,false);
				}

				// End replaceState closure
				return true;
			};

		} // History.emulated.pushState



		// ====================================================================
		// Initialise

		// Non-Native pushState Implementation
		if ( History.emulated.pushState ) {
			/**
			 * Ensure initial state is handled correctly
			 */
			if ( History.getHash() && !History.emulated.hashChange ) {
				History.Adapter.onDomLoad(function(){
					History.Adapter.trigger(window,'hashchange');
				});
			}

		} // History.emulated.pushState

	}; // History.initHtml4

	// Try to Initialise History
	if ( typeof History.init !== 'undefined' ) {
		History.init();
	}

})(window);
/**
 * History.js Core
 * @author Benjamin Arthur Lupton <contact@balupton.com>
 * @copyright 2010-2011 Benjamin Arthur Lupton <contact@balupton.com>
 * @license New BSD License <http://creativecommons.org/licenses/BSD/>
 */

(function(window,undefined){
	"use strict";

	// ========================================================================
	// Initialise

	// Localise Globals
	var
		console = window.console||undefined, // Prevent a JSLint complain
		document = window.document, // Make sure we are using the correct document
		navigator = window.navigator, // Make sure we are using the correct navigator
		sessionStorage = window.sessionStorage||false, // sessionStorage
		setTimeout = window.setTimeout,
		clearTimeout = window.clearTimeout,
		setInterval = window.setInterval,
		clearInterval = window.clearInterval,
		JSON = window.JSON,
		alert = window.alert,
		History = window.History = window.History||{}, // Public History Object
		history = window.history; // Old History Object

	try {
		sessionStorage.setItem('TEST', '1');
		sessionStorage.removeItem('TEST');
	} catch(e) {
		sessionStorage = false;
	}

	// MooTools Compatibility
	JSON.stringify = JSON.stringify||JSON.encode;
	JSON.parse = JSON.parse||JSON.decode;

	// Check Existence
	if ( typeof History.init !== 'undefined' ) {
		throw new Error('History.js Core has already been loaded...');
	}

	// Initialise History
	History.init = function(options){
		// Check Load Status of Adapter
		if ( typeof History.Adapter === 'undefined' ) {
			return false;
		}

		// Check Load Status of Core
		if ( typeof History.initCore !== 'undefined' ) {
			History.initCore();
		}

		// Check Load Status of HTML4 Support
		if ( typeof History.initHtml4 !== 'undefined' ) {
			History.initHtml4();
		}

		// Return true
		return true;
	};


	// ========================================================================
	// Initialise Core

	// Initialise Core
	History.initCore = function(options){
		// Initialise
		if ( typeof History.initCore.initialized !== 'undefined' ) {
			// Already Loaded
			return false;
		}
		else {
			History.initCore.initialized = true;
		}


		// ====================================================================
		// Options

		/**
		 * History.options
		 * Configurable options
		 */
		History.options = History.options||{};

		/**
		 * History.options.hashChangeInterval
		 * How long should the interval be before hashchange checks
		 */
		History.options.hashChangeInterval = History.options.hashChangeInterval || 100;

		/**
		 * History.options.safariPollInterval
		 * How long should the interval be before safari poll checks
		 */
		History.options.safariPollInterval = History.options.safariPollInterval || 500;

		/**
		 * History.options.doubleCheckInterval
		 * How long should the interval be before we perform a double check
		 */
		History.options.doubleCheckInterval = History.options.doubleCheckInterval || 500;

		/**
		 * History.options.disableSuid
		 * Force History not to append suid
		 */
		History.options.disableSuid = History.options.disableSuid || false;

		/**
		 * History.options.storeInterval
		 * How long should we wait between store calls
		 */
		History.options.storeInterval = History.options.storeInterval || 1000;

		/**
		 * History.options.busyDelay
		 * How long should we wait between busy events
		 */
		History.options.busyDelay = History.options.busyDelay || 250;

		/**
		 * History.options.debug
		 * If true will enable debug messages to be logged
		 */
		History.options.debug = History.options.debug || false;

		/**
		 * History.options.initialTitle
		 * What is the title of the initial state
		 */
		History.options.initialTitle = History.options.initialTitle || document.title;

		/**
		 * History.options.html4Mode
		 * If true, will force HTMl4 mode (hashtags)
		 */
		History.options.html4Mode = History.options.html4Mode || false;

		/**
		 * History.options.delayInit
		 * Want to override default options and call init manually.
		 */
		History.options.delayInit = History.options.delayInit || false;


		// ====================================================================
		// Interval record

		/**
		 * History.intervalList
		 * List of intervals set, to be cleared when document is unloaded.
		 */
		History.intervalList = [];

		/**
		 * History.clearAllIntervals
		 * Clears all setInterval instances.
		 */
		History.clearAllIntervals = function(){
			var i, il = History.intervalList;
			if (typeof il !== "undefined" && il !== null) {
				for (i = 0; i < il.length; i++) {
					clearInterval(il[i]);
				}
				History.intervalList = null;
			}
		};


		// ====================================================================
		// Debug

		/**
		 * History.debug(message,...)
		 * Logs the passed arguments if debug enabled
		 */
		History.debug = function(){
			if ( (History.options.debug||false) ) {
				History.log.apply(History,arguments);
			}
		};

		/**
		 * History.log(message,...)
		 * Logs the passed arguments
		 */
		History.log = function(){
			// Prepare
			var
				consoleExists = !(typeof console === 'undefined' || typeof console.log === 'undefined' || typeof console.log.apply === 'undefined'),
				textarea = document.getElementById('log'),
				message,
				i,n,
				args,arg
				;

			// Write to Console
			if ( consoleExists ) {
				args = Array.prototype.slice.call(arguments);
				message = args.shift();
				if ( typeof console.debug !== 'undefined' ) {
					console.debug.apply(console,[message,args]);
				}
				else {
					console.log.apply(console,[message,args]);
				}
			}
			else {
				message = ("\n"+arguments[0]+"\n");
			}

			// Write to log
			for ( i=1,n=arguments.length; i<n; ++i ) {
				arg = arguments[i];
				if ( typeof arg === 'object' && typeof JSON !== 'undefined' ) {
					try {
						arg = JSON.stringify(arg);
					}
					catch ( Exception ) {
						// Recursive Object
					}
				}
				message += "\n"+arg+"\n";
			}

			// Textarea
			if ( textarea ) {
				textarea.value += message+"\n-----\n";
				textarea.scrollTop = textarea.scrollHeight - textarea.clientHeight;
			}
			// No Textarea, No Console
			else if ( !consoleExists ) {
				alert(message);
			}

			// Return true
			return true;
		};


		// ====================================================================
		// Emulated Status

		/**
		 * History.getInternetExplorerMajorVersion()
		 * Get's the major version of Internet Explorer
		 * @return {integer}
		 * @license Public Domain
		 * @author Benjamin Arthur Lupton <contact@balupton.com>
		 * @author James Padolsey <https://gist.github.com/527683>
		 */
		History.getInternetExplorerMajorVersion = function(){
			var result = History.getInternetExplorerMajorVersion.cached =
					(typeof History.getInternetExplorerMajorVersion.cached !== 'undefined')
				?	History.getInternetExplorerMajorVersion.cached
				:	(function(){
						var v = 3,
								div = document.createElement('div'),
								all = div.getElementsByTagName('i');
						while ( (div.innerHTML = '<!--[if gt IE ' + (++v) + ']><i></i><![endif]-->') && all[0] ) {}
						return (v > 4) ? v : false;
					})()
				;
			return result;
		};

		/**
		 * History.isInternetExplorer()
		 * Are we using Internet Explorer?
		 * @return {boolean}
		 * @license Public Domain
		 * @author Benjamin Arthur Lupton <contact@balupton.com>
		 */
		History.isInternetExplorer = function(){
			var result =
				History.isInternetExplorer.cached =
				(typeof History.isInternetExplorer.cached !== 'undefined')
					?	History.isInternetExplorer.cached
					:	Boolean(History.getInternetExplorerMajorVersion())
				;
			return result;
		};

		/**
		 * History.emulated
		 * Which features require emulating?
		 */

		if (History.options.html4Mode) {
			History.emulated = {
				pushState : true,
				hashChange: true
			};
		}

		else {

			History.emulated = {
				pushState: !Boolean(
					window.history && window.history.pushState && window.history.replaceState
					&& !(
						(/ Mobile\/([1-7][a-z]|(8([abcde]|f(1[0-8]))))/i).test(navigator.userAgent) /* disable for versions of iOS before version 4.3 (8F190) */
						|| (/AppleWebKit\/5([0-2]|3[0-2])/i).test(navigator.userAgent) /* disable for the mercury iOS browser, or at least older versions of the webkit engine */
					)
				),
				hashChange: Boolean(
					!(('onhashchange' in window) || ('onhashchange' in document))
					||
					(History.isInternetExplorer() && History.getInternetExplorerMajorVersion() < 8)
				)
			};
		}

		/**
		 * History.enabled
		 * Is History enabled?
		 */
		History.enabled = !History.emulated.pushState;

		/**
		 * History.bugs
		 * Which bugs are present
		 */
		History.bugs = {
			/**
			 * Safari 5 and Safari iOS 4 fail to return to the correct state once a hash is replaced by a `replaceState` call
			 * https://bugs.webkit.org/show_bug.cgi?id=56249
			 */
			setHash: Boolean(!History.emulated.pushState && navigator.vendor === 'Apple Computer, Inc.' && /AppleWebKit\/5([0-2]|3[0-3])/.test(navigator.userAgent)),

			/**
			 * Safari 5 and Safari iOS 4 sometimes fail to apply the state change under busy conditions
			 * https://bugs.webkit.org/show_bug.cgi?id=42940
			 */
			safariPoll: Boolean(!History.emulated.pushState && navigator.vendor === 'Apple Computer, Inc.' && /AppleWebKit\/5([0-2]|3[0-3])/.test(navigator.userAgent)),

			/**
			 * MSIE 6 and 7 sometimes do not apply a hash even it was told to (requiring a second call to the apply function)
			 */
			ieDoubleCheck: Boolean(History.isInternetExplorer() && History.getInternetExplorerMajorVersion() < 8),

			/**
			 * MSIE 6 requires the entire hash to be encoded for the hashes to trigger the onHashChange event
			 */
			hashEscape: Boolean(History.isInternetExplorer() && History.getInternetExplorerMajorVersion() < 7)
		};

		/**
		 * History.isEmptyObject(obj)
		 * Checks to see if the Object is Empty
		 * @param {Object} obj
		 * @return {boolean}
		 */
		History.isEmptyObject = function(obj) {
			for ( var name in obj ) {
				if ( obj.hasOwnProperty(name) ) {
					return false;
				}
			}
			return true;
		};

		/**
		 * History.cloneObject(obj)
		 * Clones a object and eliminate all references to the original contexts
		 * @param {Object} obj
		 * @return {Object}
		 */
		History.cloneObject = function(obj) {
			var hash,newObj;
			if ( obj ) {
				hash = JSON.stringify(obj);
				newObj = JSON.parse(hash);
			}
			else {
				newObj = {};
			}
			return newObj;
		};


		// ====================================================================
		// URL Helpers

		/**
		 * History.getRootUrl()
		 * Turns "http://mysite.com/dir/page.html?asd" into "http://mysite.com"
		 * @return {String} rootUrl
		 */
		History.getRootUrl = function(){
			// Create
			var rootUrl = document.location.protocol+'//'+(document.location.hostname||document.location.host);
			if ( document.location.port||false ) {
				rootUrl += ':'+document.location.port;
			}
			rootUrl += '/';

			// Return
			return rootUrl;
		};

		/**
		 * History.getBaseHref()
		 * Fetches the `href` attribute of the `<base href="...">` element if it exists
		 * @return {String} baseHref
		 */
		History.getBaseHref = function(){
			// Create
			var
				baseElements = document.getElementsByTagName('base'),
				baseElement = null,
				baseHref = '';

			// Test for Base Element
			if ( baseElements.length === 1 ) {
				// Prepare for Base Element
				baseElement = baseElements[0];
				baseHref = baseElement.href.replace(/[^\/]+$/,'');
			}

			// Adjust trailing slash
			baseHref = baseHref.replace(/\/+$/,'');
			if ( baseHref ) baseHref += '/';

			// Return
			return baseHref;
		};

		/**
		 * History.getBaseUrl()
		 * Fetches the baseHref or basePageUrl or rootUrl (whichever one exists first)
		 * @return {String} baseUrl
		 */
		History.getBaseUrl = function(){
			// Create
			var baseUrl = History.getBaseHref()||History.getBasePageUrl()||History.getRootUrl();

			// Return
			return baseUrl;
		};

		/**
		 * History.getPageUrl()
		 * Fetches the URL of the current page
		 * @return {String} pageUrl
		 */
		History.getPageUrl = function(){
			// Fetch
			var
				State = History.getState(false,false),
				stateUrl = (State||{}).url||History.getLocationHref(),
				pageUrl;

			// Create
			pageUrl = stateUrl.replace(/\/+$/,'').replace(/[^\/]+$/,function(part,index,string){
				return (/\./).test(part) ? part : part+'/';
			});

			// Return
			return pageUrl;
		};

		/**
		 * History.getBasePageUrl()
		 * Fetches the Url of the directory of the current page
		 * @return {String} basePageUrl
		 */
		History.getBasePageUrl = function(){
			// Create
			var basePageUrl = (History.getLocationHref()).replace(/[#\?].*/,'').replace(/[^\/]+$/,function(part,index,string){
				return (/[^\/]$/).test(part) ? '' : part;
			}).replace(/\/+$/,'')+'/';

			// Return
			return basePageUrl;
		};

		/**
		 * History.getFullUrl(url)
		 * Ensures that we have an absolute URL and not a relative URL
		 * @param {string} url
		 * @param {Boolean} allowBaseHref
		 * @return {string} fullUrl
		 */
		History.getFullUrl = function(url,allowBaseHref){
			// Prepare
			var fullUrl = url, firstChar = url.substring(0,1);
			allowBaseHref = (typeof allowBaseHref === 'undefined') ? true : allowBaseHref;

			// Check
			if ( /[a-z]+\:\/\//.test(url) ) {
				// Full URL
			}
			else if ( firstChar === '/' ) {
				// Root URL
				fullUrl = History.getRootUrl()+url.replace(/^\/+/,'');
			}
			else if ( firstChar === '#' ) {
				// Anchor URL
				fullUrl = History.getPageUrl().replace(/#.*/,'')+url;
			}
			else if ( firstChar === '?' ) {
				// Query URL
				fullUrl = History.getPageUrl().replace(/[\?#].*/,'')+url;
			}
			else {
				// Relative URL
				if ( allowBaseHref ) {
					fullUrl = History.getBaseUrl()+url.replace(/^(\.\/)+/,'');
				} else {
					fullUrl = History.getBasePageUrl()+url.replace(/^(\.\/)+/,'');
				}
				// We have an if condition above as we do not want hashes
				// which are relative to the baseHref in our URLs
				// as if the baseHref changes, then all our bookmarks
				// would now point to different locations
				// whereas the basePageUrl will always stay the same
			}

			// Return
			return fullUrl.replace(/\#$/,'');
		};

		/**
		 * History.getShortUrl(url)
		 * Ensures that we have a relative URL and not a absolute URL
		 * @param {string} url
		 * @return {string} url
		 */
		History.getShortUrl = function(url){
			// Prepare
			var shortUrl = url, baseUrl = History.getBaseUrl(), rootUrl = History.getRootUrl();

			// Trim baseUrl
			if ( History.emulated.pushState ) {
				// We are in a if statement as when pushState is not emulated
				// The actual url these short urls are relative to can change
				// So within the same session, we the url may end up somewhere different
				shortUrl = shortUrl.replace(baseUrl,'');
			}

			// Trim rootUrl
			shortUrl = shortUrl.replace(rootUrl,'/');

			// Ensure we can still detect it as a state
			if ( History.isTraditionalAnchor(shortUrl) ) {
				shortUrl = './'+shortUrl;
			}

			// Clean It
			shortUrl = shortUrl.replace(/^(\.\/)+/g,'./').replace(/\#$/,'');

			// Return
			return shortUrl;
		};

		/**
		 * History.getLocationHref(document)
		 * Returns a normalized version of document.location.href
		 * accounting for browser inconsistencies, etc.
		 *
		 * This URL will be URI-encoded and will include the hash
		 *
		 * @param {object} document
		 * @return {string} url
		 */
		History.getLocationHref = function(doc) {
			doc = doc || document;

			// most of the time, this will be true
			if (doc.URL === doc.location.href)
				return doc.location.href;

			// some versions of webkit URI-decode document.location.href
			// but they leave document.URL in an encoded state
			if (doc.location.href === decodeURIComponent(doc.URL))
				return doc.URL;

			// FF 3.6 only updates document.URL when a page is reloaded
			// document.location.href is updated correctly
			if (doc.location.hash && decodeURIComponent(doc.location.href.replace(/^[^#]+/, "")) === doc.location.hash)
				return doc.location.href;

			if (doc.URL.indexOf('#') == -1 && doc.location.href.indexOf('#') != -1)
				return doc.location.href;
			
			return doc.URL || doc.location.href;
		};


		// ====================================================================
		// State Storage

		/**
		 * History.store
		 * The store for all session specific data
		 */
		History.store = {};

		/**
		 * History.idToState
		 * 1-1: State ID to State Object
		 */
		History.idToState = History.idToState||{};

		/**
		 * History.stateToId
		 * 1-1: State String to State ID
		 */
		History.stateToId = History.stateToId||{};

		/**
		 * History.urlToId
		 * 1-1: State URL to State ID
		 */
		History.urlToId = History.urlToId||{};

		/**
		 * History.storedStates
		 * Store the states in an array
		 */
		History.storedStates = History.storedStates||[];

		/**
		 * History.savedStates
		 * Saved the states in an array
		 */
		History.savedStates = History.savedStates||[];

		/**
		 * History.noramlizeStore()
		 * Noramlize the store by adding necessary values
		 */
		History.normalizeStore = function(){
			History.store.idToState = History.store.idToState||{};
			History.store.urlToId = History.store.urlToId||{};
			History.store.stateToId = History.store.stateToId||{};
		};

		/**
		 * History.getState()
		 * Get an object containing the data, title and url of the current state
		 * @param {Boolean} friendly
		 * @param {Boolean} create
		 * @return {Object} State
		 */
		History.getState = function(friendly,create){
			// Prepare
			if ( typeof friendly === 'undefined' ) { friendly = true; }
			if ( typeof create === 'undefined' ) { create = true; }

			// Fetch
			var State = History.getLastSavedState();

			// Create
			if ( !State && create ) {
				State = History.createStateObject();
			}

			// Adjust
			if ( friendly ) {
				State = History.cloneObject(State);
				State.url = State.cleanUrl||State.url;
			}

			// Return
			return State;
		};

		/**
		 * History.getIdByState(State)
		 * Gets a ID for a State
		 * @param {State} newState
		 * @return {String} id
		 */
		History.getIdByState = function(newState){

			// Fetch ID
			var id = History.extractId(newState.url),
				str;

			if ( !id ) {
				// Find ID via State String
				str = History.getStateString(newState);
				if ( typeof History.stateToId[str] !== 'undefined' ) {
					id = History.stateToId[str];
				}
				else if ( typeof History.store.stateToId[str] !== 'undefined' ) {
					id = History.store.stateToId[str];
				}
				else {
					// Generate a new ID
					while ( true ) {
						id = (new Date()).getTime() + String(Math.random()).replace(/\D/g,'');
						if ( typeof History.idToState[id] === 'undefined' && typeof History.store.idToState[id] === 'undefined' ) {
							break;
						}
					}

					// Apply the new State to the ID
					History.stateToId[str] = id;
					History.idToState[id] = newState;
				}
			}

			// Return ID
			return id;
		};

		/**
		 * History.normalizeState(State)
		 * Expands a State Object
		 * @param {object} State
		 * @return {object}
		 */
		History.normalizeState = function(oldState){
			// Variables
			var newState, dataNotEmpty;

			// Prepare
			if ( !oldState || (typeof oldState !== 'object') ) {
				oldState = {};
			}

			// Check
			if ( typeof oldState.normalized !== 'undefined' ) {
				return oldState;
			}

			// Adjust
			if ( !oldState.data || (typeof oldState.data !== 'object') ) {
				oldState.data = {};
			}

			// ----------------------------------------------------------------

			// Create
			newState = {};
			newState.normalized = true;
			newState.title = oldState.title||'';
			newState.url = History.getFullUrl(oldState.url?oldState.url:(History.getLocationHref()));
			newState.hash = History.getShortUrl(newState.url);
			newState.data = History.cloneObject(oldState.data);

			// Fetch ID
			newState.id = History.getIdByState(newState);

			// ----------------------------------------------------------------

			// Clean the URL
			newState.cleanUrl = newState.url.replace(/\??\&_suid.*/,'');
			newState.url = newState.cleanUrl;

			// Check to see if we have more than just a url
			dataNotEmpty = !History.isEmptyObject(newState.data);

			// Apply
			if ( (newState.title || dataNotEmpty) && History.options.disableSuid !== true ) {
				// Add ID to Hash
				newState.hash = History.getShortUrl(newState.url).replace(/\??\&_suid.*/,'');
				if ( !/\?/.test(newState.hash) ) {
					newState.hash += '?';
				}
				newState.hash += '&_suid='+newState.id;
			}

			// Create the Hashed URL
			newState.hashedUrl = History.getFullUrl(newState.hash);

			// ----------------------------------------------------------------

			// Update the URL if we have a duplicate
			if ( (History.emulated.pushState || History.bugs.safariPoll) && History.hasUrlDuplicate(newState) ) {
				newState.url = newState.hashedUrl;
			}

			// ----------------------------------------------------------------

			// Return
			return newState;
		};

		/**
		 * History.createStateObject(data,title,url)
		 * Creates a object based on the data, title and url state params
		 * @param {object} data
		 * @param {string} title
		 * @param {string} url
		 * @return {object}
		 */
		History.createStateObject = function(data,title,url){
			// Hashify
			var State = {
				'data': data,
				'title': title,
				'url': url
			};

			// Expand the State
			State = History.normalizeState(State);

			// Return object
			return State;
		};

		/**
		 * History.getStateById(id)
		 * Get a state by it's UID
		 * @param {String} id
		 */
		History.getStateById = function(id){
			// Prepare
			id = String(id);

			// Retrieve
			var State = History.idToState[id] || History.store.idToState[id] || undefined;

			// Return State
			return State;
		};

		/**
		 * Get a State's String
		 * @param {State} passedState
		 */
		History.getStateString = function(passedState){
			// Prepare
			var State, cleanedState, str;

			// Fetch
			State = History.normalizeState(passedState);

			// Clean
			cleanedState = {
				data: State.data,
				title: passedState.title,
				url: passedState.url
			};

			// Fetch
			str = JSON.stringify(cleanedState);

			// Return
			return str;
		};

		/**
		 * Get a State's ID
		 * @param {State} passedState
		 * @return {String} id
		 */
		History.getStateId = function(passedState){
			// Prepare
			var State, id;

			// Fetch
			State = History.normalizeState(passedState);

			// Fetch
			id = State.id;

			// Return
			return id;
		};

		/**
		 * History.getHashByState(State)
		 * Creates a Hash for the State Object
		 * @param {State} passedState
		 * @return {String} hash
		 */
		History.getHashByState = function(passedState){
			// Prepare
			var State, hash;

			// Fetch
			State = History.normalizeState(passedState);

			// Hash
			hash = State.hash;

			// Return
			return hash;
		};

		/**
		 * History.extractId(url_or_hash)
		 * Get a State ID by it's URL or Hash
		 * @param {string} url_or_hash
		 * @return {string} id
		 */
		History.extractId = function ( url_or_hash ) {
			// Prepare
			var id,parts,url, tmp;

			// Extract
			
			// If the URL has a #, use the id from before the #
			if (url_or_hash.indexOf('#') != -1)
			{
				tmp = url_or_hash.split("#")[0];
			}
			else
			{
				tmp = url_or_hash;
			}
			
			parts = /(.*)\&_suid=([0-9]+)$/.exec(tmp);
			url = parts ? (parts[1]||url_or_hash) : url_or_hash;
			id = parts ? String(parts[2]||'') : '';

			// Return
			return id||false;
		};

		/**
		 * History.isTraditionalAnchor
		 * Checks to see if the url is a traditional anchor or not
		 * @param {String} url_or_hash
		 * @return {Boolean}
		 */
		History.isTraditionalAnchor = function(url_or_hash){
			// Check
			var isTraditional = !(/[\/\?\.]/.test(url_or_hash));

			// Return
			return isTraditional;
		};

		/**
		 * History.extractState
		 * Get a State by it's URL or Hash
		 * @param {String} url_or_hash
		 * @return {State|null}
		 */
		History.extractState = function(url_or_hash,create){
			// Prepare
			var State = null, id, url;
			create = create||false;

			// Fetch SUID
			id = History.extractId(url_or_hash);
			if ( id ) {
				State = History.getStateById(id);
			}

			// Fetch SUID returned no State
			if ( !State ) {
				// Fetch URL
				url = History.getFullUrl(url_or_hash);

				// Check URL
				id = History.getIdByUrl(url)||false;
				if ( id ) {
					State = History.getStateById(id);
				}

				// Create State
				if ( !State && create && !History.isTraditionalAnchor(url_or_hash) ) {
					State = History.createStateObject(null,null,url);
				}
			}

			// Return
			return State;
		};

		/**
		 * History.getIdByUrl()
		 * Get a State ID by a State URL
		 */
		History.getIdByUrl = function(url){
			// Fetch
			var id = History.urlToId[url] || History.store.urlToId[url] || undefined;

			// Return
			return id;
		};

		/**
		 * History.getLastSavedState()
		 * Get an object containing the data, title and url of the current state
		 * @return {Object} State
		 */
		History.getLastSavedState = function(){
			return History.savedStates[History.savedStates.length-1]||undefined;
		};

		/**
		 * History.getLastStoredState()
		 * Get an object containing the data, title and url of the current state
		 * @return {Object} State
		 */
		History.getLastStoredState = function(){
			return History.storedStates[History.storedStates.length-1]||undefined;
		};

		/**
		 * History.hasUrlDuplicate
		 * Checks if a Url will have a url conflict
		 * @param {Object} newState
		 * @return {Boolean} hasDuplicate
		 */
		History.hasUrlDuplicate = function(newState) {
			// Prepare
			var hasDuplicate = false,
				oldState;

			// Fetch
			oldState = History.extractState(newState.url);

			// Check
			hasDuplicate = oldState && oldState.id !== newState.id;

			// Return
			return hasDuplicate;
		};

		/**
		 * History.storeState
		 * Store a State
		 * @param {Object} newState
		 * @return {Object} newState
		 */
		History.storeState = function(newState){
			// Store the State
			History.urlToId[newState.url] = newState.id;

			// Push the State
			History.storedStates.push(History.cloneObject(newState));

			// Return newState
			return newState;
		};

		/**
		 * History.isLastSavedState(newState)
		 * Tests to see if the state is the last state
		 * @param {Object} newState
		 * @return {boolean} isLast
		 */
		History.isLastSavedState = function(newState){
			// Prepare
			var isLast = false,
				newId, oldState, oldId;

			// Check
			if ( History.savedStates.length ) {
				newId = newState.id;
				oldState = History.getLastSavedState();
				oldId = oldState.id;

				// Check
				isLast = (newId === oldId);
			}

			// Return
			return isLast;
		};

		/**
		 * History.saveState
		 * Push a State
		 * @param {Object} newState
		 * @return {boolean} changed
		 */
		History.saveState = function(newState){
			// Check Hash
			if ( History.isLastSavedState(newState) ) {
				return false;
			}

			// Push the State
			History.savedStates.push(History.cloneObject(newState));

			// Return true
			return true;
		};

		/**
		 * History.getStateByIndex()
		 * Gets a state by the index
		 * @param {integer} index
		 * @return {Object}
		 */
		History.getStateByIndex = function(index){
			// Prepare
			var State = null;

			// Handle
			if ( typeof index === 'undefined' ) {
				// Get the last inserted
				State = History.savedStates[History.savedStates.length-1];
			}
			else if ( index < 0 ) {
				// Get from the end
				State = History.savedStates[History.savedStates.length+index];
			}
			else {
				// Get from the beginning
				State = History.savedStates[index];
			}

			// Return State
			return State;
		};
		
		/**
		 * History.getCurrentIndex()
		 * Gets the current index
		 * @return (integer)
		*/
		History.getCurrentIndex = function(){
			// Prepare
			var index = null;
			
			// No states saved
			if(History.savedStates.length < 1) {
				index = 0;
			}
			else {
				index = History.savedStates.length-1;
			}
			return index;
		};

		// ====================================================================
		// Hash Helpers

		/**
		 * History.getHash()
		 * @param {Location=} location
		 * Gets the current document hash
		 * Note: unlike location.hash, this is guaranteed to return the escaped hash in all browsers
		 * @return {string}
		 */
		History.getHash = function(doc){
			var url = History.getLocationHref(doc),
				hash;
			hash = History.getHashByUrl(url);
			return hash;
		};

		/**
		 * History.unescapeHash()
		 * normalize and Unescape a Hash
		 * @param {String} hash
		 * @return {string}
		 */
		History.unescapeHash = function(hash){
			// Prepare
			var result = History.normalizeHash(hash);

			// Unescape hash
			result = decodeURIComponent(result);

			// Return result
			return result;
		};

		/**
		 * History.normalizeHash()
		 * normalize a hash across browsers
		 * @return {string}
		 */
		History.normalizeHash = function(hash){
			// Prepare
			var result = hash.replace(/[^#]*#/,'').replace(/#.*/, '');

			// Return result
			return result;
		};

		/**
		 * History.setHash(hash)
		 * Sets the document hash
		 * @param {string} hash
		 * @return {History}
		 */
		History.setHash = function(hash,queue){
			// Prepare
			var State, pageUrl;

			// Handle Queueing
			if ( queue !== false && History.busy() ) {
				// Wait + Push to Queue
				//History.debug('History.setHash: we must wait', arguments);
				History.pushQueue({
					scope: History,
					callback: History.setHash,
					args: arguments,
					queue: queue
				});
				return false;
			}

			// Log
			//History.debug('History.setHash: called',hash);

			// Make Busy + Continue
			History.busy(true);

			// Check if hash is a state
			State = History.extractState(hash,true);
			if ( State && !History.emulated.pushState ) {
				// Hash is a state so skip the setHash
				//History.debug('History.setHash: Hash is a state so skipping the hash set with a direct pushState call',arguments);

				// PushState
				History.pushState(State.data,State.title,State.url,false);
			}
			else if ( History.getHash() !== hash ) {
				// Hash is a proper hash, so apply it

				// Handle browser bugs
				if ( History.bugs.setHash ) {
					// Fix Safari Bug https://bugs.webkit.org/show_bug.cgi?id=56249

					// Fetch the base page
					pageUrl = History.getPageUrl();

					// Safari hash apply
					History.pushState(null,null,pageUrl+'#'+hash,false);
				}
				else {
					// Normal hash apply
					document.location.hash = hash;
				}
			}

			// Chain
			return History;
		};

		/**
		 * History.escape()
		 * normalize and Escape a Hash
		 * @return {string}
		 */
		History.escapeHash = function(hash){
			// Prepare
			var result = History.normalizeHash(hash);

			// Escape hash
			result = window.encodeURIComponent(result);

			// IE6 Escape Bug
			if ( !History.bugs.hashEscape ) {
				// Restore common parts
				result = result
					.replace(/\%21/g,'!')
					.replace(/\%26/g,'&')
					.replace(/\%3D/g,'=')
					.replace(/\%3F/g,'?');
			}

			// Return result
			return result;
		};

		/**
		 * History.getHashByUrl(url)
		 * Extracts the Hash from a URL
		 * @param {string} url
		 * @return {string} url
		 */
		History.getHashByUrl = function(url){
			// Extract the hash
			var hash = String(url)
				.replace(/([^#]*)#?([^#]*)#?(.*)/, '$2')
				;

			// Unescape hash
			hash = History.unescapeHash(hash);

			// Return hash
			return hash;
		};

		/**
		 * History.setTitle(title)
		 * Applies the title to the document
		 * @param {State} newState
		 * @return {Boolean}
		 */
		History.setTitle = function(newState){
			// Prepare
			var title = newState.title,
				firstState;

			// Initial
			if ( !title ) {
				firstState = History.getStateByIndex(0);
				if ( firstState && firstState.url === newState.url ) {
					title = firstState.title||History.options.initialTitle;
				}
			}

			// Apply
			try {
				document.getElementsByTagName('title')[0].innerHTML = title.replace('<','&lt;').replace('>','&gt;').replace(' & ',' &amp; ');
			}
			catch ( Exception ) { }
			document.title = title;

			// Chain
			return History;
		};


		// ====================================================================
		// Queueing

		/**
		 * History.queues
		 * The list of queues to use
		 * First In, First Out
		 */
		History.queues = [];

		/**
		 * History.busy(value)
		 * @param {boolean} value [optional]
		 * @return {boolean} busy
		 */
		History.busy = function(value){
			// Apply
			if ( typeof value !== 'undefined' ) {
				//History.debug('History.busy: changing ['+(History.busy.flag||false)+'] to ['+(value||false)+']', History.queues.length);
				History.busy.flag = value;
			}
			// Default
			else if ( typeof History.busy.flag === 'undefined' ) {
				History.busy.flag = false;
			}

			// Queue
			if ( !History.busy.flag ) {
				// Execute the next item in the queue
				clearTimeout(History.busy.timeout);
				var fireNext = function(){
					var i, queue, item;
					if ( History.busy.flag ) return;
					for ( i=History.queues.length-1; i >= 0; --i ) {
						queue = History.queues[i];
						if ( queue.length === 0 ) continue;
						item = queue.shift();
						History.fireQueueItem(item);
						History.busy.timeout = setTimeout(fireNext,History.options.busyDelay);
					}
				};
				History.busy.timeout = setTimeout(fireNext,History.options.busyDelay);
			}

			// Return
			return History.busy.flag;
		};

		/**
		 * History.busy.flag
		 */
		History.busy.flag = false;

		/**
		 * History.fireQueueItem(item)
		 * Fire a Queue Item
		 * @param {Object} item
		 * @return {Mixed} result
		 */
		History.fireQueueItem = function(item){
			return item.callback.apply(item.scope||History,item.args||[]);
		};

		/**
		 * History.pushQueue(callback,args)
		 * Add an item to the queue
		 * @param {Object} item [scope,callback,args,queue]
		 */
		History.pushQueue = function(item){
			// Prepare the queue
			History.queues[item.queue||0] = History.queues[item.queue||0]||[];

			// Add to the queue
			History.queues[item.queue||0].push(item);

			// Chain
			return History;
		};

		/**
		 * History.queue (item,queue), (func,queue), (func), (item)
		 * Either firs the item now if not busy, or adds it to the queue
		 */
		History.queue = function(item,queue){
			// Prepare
			if ( typeof item === 'function' ) {
				item = {
					callback: item
				};
			}
			if ( typeof queue !== 'undefined' ) {
				item.queue = queue;
			}

			// Handle
			if ( History.busy() ) {
				History.pushQueue(item);
			} else {
				History.fireQueueItem(item);
			}

			// Chain
			return History;
		};

		/**
		 * History.clearQueue()
		 * Clears the Queue
		 */
		History.clearQueue = function(){
			History.busy.flag = false;
			History.queues = [];
			return History;
		};


		// ====================================================================
		// IE Bug Fix

		/**
		 * History.stateChanged
		 * States whether or not the state has changed since the last double check was initialised
		 */
		History.stateChanged = false;

		/**
		 * History.doubleChecker
		 * Contains the timeout used for the double checks
		 */
		History.doubleChecker = false;

		/**
		 * History.doubleCheckComplete()
		 * Complete a double check
		 * @return {History}
		 */
		History.doubleCheckComplete = function(){
			// Update
			History.stateChanged = true;

			// Clear
			History.doubleCheckClear();

			// Chain
			return History;
		};

		/**
		 * History.doubleCheckClear()
		 * Clear a double check
		 * @return {History}
		 */
		History.doubleCheckClear = function(){
			// Clear
			if ( History.doubleChecker ) {
				clearTimeout(History.doubleChecker);
				History.doubleChecker = false;
			}

			// Chain
			return History;
		};

		/**
		 * History.doubleCheck()
		 * Create a double check
		 * @return {History}
		 */
		History.doubleCheck = function(tryAgain){
			// Reset
			History.stateChanged = false;
			History.doubleCheckClear();

			// Fix IE6,IE7 bug where calling history.back or history.forward does not actually change the hash (whereas doing it manually does)
			// Fix Safari 5 bug where sometimes the state does not change: https://bugs.webkit.org/show_bug.cgi?id=42940
			if ( History.bugs.ieDoubleCheck ) {
				// Apply Check
				History.doubleChecker = setTimeout(
					function(){
						History.doubleCheckClear();
						if ( !History.stateChanged ) {
							//History.debug('History.doubleCheck: State has not yet changed, trying again', arguments);
							// Re-Attempt
							tryAgain();
						}
						return true;
					},
					History.options.doubleCheckInterval
				);
			}

			// Chain
			return History;
		};


		// ====================================================================
		// Safari Bug Fix

		/**
		 * History.safariStatePoll()
		 * Poll the current state
		 * @return {History}
		 */
		History.safariStatePoll = function(){
			// Poll the URL

			// Get the Last State which has the new URL
			var
				urlState = History.extractState(History.getLocationHref()),
				newState;

			// Check for a difference
			if ( !History.isLastSavedState(urlState) ) {
				newState = urlState;
			}
			else {
				return;
			}

			// Check if we have a state with that url
			// If not create it
			if ( !newState ) {
				//History.debug('History.safariStatePoll: new');
				newState = History.createStateObject();
			}

			// Apply the New State
			//History.debug('History.safariStatePoll: trigger');
			History.Adapter.trigger(window,'popstate');

			// Chain
			return History;
		};


		// ====================================================================
		// State Aliases

		/**
		 * History.back(queue)
		 * Send the browser history back one item
		 * @param {Integer} queue [optional]
		 */
		History.back = function(queue){
			//History.debug('History.back: called', arguments);

			// Handle Queueing
			if ( queue !== false && History.busy() ) {
				// Wait + Push to Queue
				//History.debug('History.back: we must wait', arguments);
				History.pushQueue({
					scope: History,
					callback: History.back,
					args: arguments,
					queue: queue
				});
				return false;
			}

			// Make Busy + Continue
			History.busy(true);

			// Fix certain browser bugs that prevent the state from changing
			History.doubleCheck(function(){
				History.back(false);
			});

			// Go back
			history.go(-1);

			// End back closure
			return true;
		};

		/**
		 * History.forward(queue)
		 * Send the browser history forward one item
		 * @param {Integer} queue [optional]
		 */
		History.forward = function(queue){
			//History.debug('History.forward: called', arguments);

			// Handle Queueing
			if ( queue !== false && History.busy() ) {
				// Wait + Push to Queue
				//History.debug('History.forward: we must wait', arguments);
				History.pushQueue({
					scope: History,
					callback: History.forward,
					args: arguments,
					queue: queue
				});
				return false;
			}

			// Make Busy + Continue
			History.busy(true);

			// Fix certain browser bugs that prevent the state from changing
			History.doubleCheck(function(){
				History.forward(false);
			});

			// Go forward
			history.go(1);

			// End forward closure
			return true;
		};

		/**
		 * History.go(index,queue)
		 * Send the browser history back or forward index times
		 * @param {Integer} queue [optional]
		 */
		History.go = function(index,queue){
			//History.debug('History.go: called', arguments);

			// Prepare
			var i;

			// Handle
			if ( index > 0 ) {
				// Forward
				for ( i=1; i<=index; ++i ) {
					History.forward(queue);
				}
			}
			else if ( index < 0 ) {
				// Backward
				for ( i=-1; i>=index; --i ) {
					History.back(queue);
				}
			}
			else {
				throw new Error('History.go: History.go requires a positive or negative integer passed.');
			}

			// Chain
			return History;
		};


		// ====================================================================
		// HTML5 State Support

		// Non-Native pushState Implementation
		if ( History.emulated.pushState ) {
			/*
			 * Provide Skeleton for HTML4 Browsers
			 */

			// Prepare
			var emptyFunction = function(){};
			History.pushState = History.pushState||emptyFunction;
			History.replaceState = History.replaceState||emptyFunction;
		} // History.emulated.pushState

		// Native pushState Implementation
		else {
			/*
			 * Use native HTML5 History API Implementation
			 */

			/**
			 * History.onPopState(event,extra)
			 * Refresh the Current State
			 */
			History.onPopState = function(event,extra){
				// Prepare
				var stateId = false, newState = false, currentHash, currentState;

				// Reset the double check
				History.doubleCheckComplete();

				// Check for a Hash, and handle apporiatly
				currentHash = History.getHash();
				if ( currentHash ) {
					// Expand Hash
					currentState = History.extractState(currentHash||History.getLocationHref(),true);
					if ( currentState ) {
						// We were able to parse it, it must be a State!
						// Let's forward to replaceState
						//History.debug('History.onPopState: state anchor', currentHash, currentState);
						History.replaceState(currentState.data, currentState.title, currentState.url, false);
					}
					else {
						// Traditional Anchor
						//History.debug('History.onPopState: traditional anchor', currentHash);
						History.Adapter.trigger(window,'anchorchange');
						History.busy(false);
					}

					// We don't care for hashes
					History.expectedStateId = false;
					return false;
				}

				// Ensure
				stateId = History.Adapter.extractEventData('state',event,extra) || false;

				// Fetch State
				if ( stateId ) {
					// Vanilla: Back/forward button was used
					newState = History.getStateById(stateId);
				}
				else if ( History.expectedStateId ) {
					// Vanilla: A new state was pushed, and popstate was called manually
					newState = History.getStateById(History.expectedStateId);
				}
				else {
					// Initial State
					newState = History.extractState(History.getLocationHref());
				}

				// The State did not exist in our store
				if ( !newState ) {
					// Regenerate the State
					newState = History.createStateObject(null,null,History.getLocationHref());
				}

				// Clean
				History.expectedStateId = false;

				// Check if we are the same state
				if ( History.isLastSavedState(newState) ) {
					// There has been no change (just the page's hash has finally propagated)
					//History.debug('History.onPopState: no change', newState, History.savedStates);
					History.busy(false);
					return false;
				}

				// Store the State
				History.storeState(newState);
				History.saveState(newState);

				// Force update of the title
				History.setTitle(newState);

				// Fire Our Event
				History.Adapter.trigger(window,'statechange');
				History.busy(false);

				// Return true
				return true;
			};
			History.Adapter.bind(window,'popstate',History.onPopState);

			/**
			 * History.pushState(data,title,url)
			 * Add a new State to the history object, become it, and trigger onpopstate
			 * We have to trigger for HTML4 compatibility
			 * @param {object} data
			 * @param {string} title
			 * @param {string} url
			 * @return {true}
			 */
			History.pushState = function(data,title,url,queue){
				//History.debug('History.pushState: called', arguments);

				// Check the State
				if ( History.getHashByUrl(url) && History.emulated.pushState ) {
					throw new Error('History.js does not support states with fragement-identifiers (hashes/anchors).');
				}

				// Handle Queueing
				if ( queue !== false && History.busy() ) {
					// Wait + Push to Queue
					//History.debug('History.pushState: we must wait', arguments);
					History.pushQueue({
						scope: History,
						callback: History.pushState,
						args: arguments,
						queue: queue
					});
					return false;
				}

				// Make Busy + Continue
				History.busy(true);

				// Create the newState
				var newState = History.createStateObject(data,title,url);

				// Check it
				if ( History.isLastSavedState(newState) ) {
					// Won't be a change
					History.busy(false);
				}
				else {
					// Store the newState
					History.storeState(newState);
					History.expectedStateId = newState.id;

					// Push the newState
					history.pushState(newState.id,newState.title,newState.url);

					// Fire HTML5 Event
					History.Adapter.trigger(window,'popstate');
				}

				// End pushState closure
				return true;
			};

			/**
			 * History.replaceState(data,title,url)
			 * Replace the State and trigger onpopstate
			 * We have to trigger for HTML4 compatibility
			 * @param {object} data
			 * @param {string} title
			 * @param {string} url
			 * @return {true}
			 */
			History.replaceState = function(data,title,url,queue){
				//History.debug('History.replaceState: called', arguments);

				// Check the State
				if ( History.getHashByUrl(url) && History.emulated.pushState ) {
					throw new Error('History.js does not support states with fragement-identifiers (hashes/anchors).');
				}

				// Handle Queueing
				if ( queue !== false && History.busy() ) {
					// Wait + Push to Queue
					//History.debug('History.replaceState: we must wait', arguments);
					History.pushQueue({
						scope: History,
						callback: History.replaceState,
						args: arguments,
						queue: queue
					});
					return false;
				}

				// Make Busy + Continue
				History.busy(true);

				// Create the newState
				var newState = History.createStateObject(data,title,url);

				// Check it
				if ( History.isLastSavedState(newState) ) {
					// Won't be a change
					History.busy(false);
				}
				else {
					// Store the newState
					History.storeState(newState);
					History.expectedStateId = newState.id;

					// Push the newState
					history.replaceState(newState.id,newState.title,newState.url);

					// Fire HTML5 Event
					History.Adapter.trigger(window,'popstate');
				}

				// End replaceState closure
				return true;
			};

		} // !History.emulated.pushState


		// ====================================================================
		// Initialise

		/**
		 * Load the Store
		 */
		if ( sessionStorage ) {
			// Fetch
			try {
				History.store = JSON.parse(sessionStorage.getItem('History.store'))||{};
			}
			catch ( err ) {
				History.store = {};
			}

			// Normalize
			History.normalizeStore();
		}
		else {
			// Default Load
			History.store = {};
			History.normalizeStore();
		}

		/**
		 * Clear Intervals on exit to prevent memory leaks
		 */
		History.Adapter.bind(window,"unload",History.clearAllIntervals);

		/**
		 * Create the initial State
		 */
		History.saveState(History.storeState(History.extractState(History.getLocationHref(),true)));

		/**
		 * Bind for Saving Store
		 */
		if ( sessionStorage ) {
			// When the page is closed
			History.onUnload = function(){
				// Prepare
				var	currentStore, item, currentStoreString;

				// Fetch
				try {
					currentStore = JSON.parse(sessionStorage.getItem('History.store'))||{};
				}
				catch ( err ) {
					currentStore = {};
				}

				// Ensure
				currentStore.idToState = currentStore.idToState || {};
				currentStore.urlToId = currentStore.urlToId || {};
				currentStore.stateToId = currentStore.stateToId || {};

				// Sync
				for ( item in History.idToState ) {
					if ( !History.idToState.hasOwnProperty(item) ) {
						continue;
					}
					currentStore.idToState[item] = History.idToState[item];
				}
				for ( item in History.urlToId ) {
					if ( !History.urlToId.hasOwnProperty(item) ) {
						continue;
					}
					currentStore.urlToId[item] = History.urlToId[item];
				}
				for ( item in History.stateToId ) {
					if ( !History.stateToId.hasOwnProperty(item) ) {
						continue;
					}
					currentStore.stateToId[item] = History.stateToId[item];
				}

				// Update
				History.store = currentStore;
				History.normalizeStore();

				// In Safari, going into Private Browsing mode causes the
				// Session Storage object to still exist but if you try and use
				// or set any property/function of it it throws the exception
				// "QUOTA_EXCEEDED_ERR: DOM Exception 22: An attempt was made to
				// add something to storage that exceeded the quota." infinitely
				// every second.
				currentStoreString = JSON.stringify(currentStore);
				try {
					// Store
					sessionStorage.setItem('History.store', currentStoreString);
				}
				catch (e) {
					if (e.code === DOMException.QUOTA_EXCEEDED_ERR) {
						if (sessionStorage.length) {
							// Workaround for a bug seen on iPads. Sometimes the quota exceeded error comes up and simply
							// removing/resetting the storage can work.
							sessionStorage.removeItem('History.store');
							sessionStorage.setItem('History.store', currentStoreString);
						} else {
							// Otherwise, we're probably private browsing in Safari, so we'll ignore the exception.
						}
					} else {
						throw e;
					}
				}
			};

			// For Internet Explorer
			History.intervalList.push(setInterval(History.onUnload,History.options.storeInterval));

			// For Other Browsers
			History.Adapter.bind(window,'beforeunload',History.onUnload);
			History.Adapter.bind(window,'unload',History.onUnload);

			// Both are enabled for consistency
		}

		// Non-Native pushState Implementation
		if ( !History.emulated.pushState ) {
			// Be aware, the following is only for native pushState implementations
			// If you are wanting to include something for all browsers
			// Then include it above this if block

			/**
			 * Setup Safari Fix
			 */
			if ( History.bugs.safariPoll ) {
				History.intervalList.push(setInterval(History.safariStatePoll, History.options.safariPollInterval));
			}

			/**
			 * Ensure Cross Browser Compatibility
			 */
			if ( navigator.vendor === 'Apple Computer, Inc.' || (navigator.appCodeName||'') === 'Mozilla' ) {
				/**
				 * Fix Safari HashChange Issue
				 */

				// Setup Alias
				History.Adapter.bind(window,'hashchange',function(){
					History.Adapter.trigger(window,'popstate');
				});

				// Initialise Alias
				if ( History.getHash() ) {
					History.Adapter.onDomLoad(function(){
						History.Adapter.trigger(window,'hashchange');
					});
				}
			}

		} // !History.emulated.pushState


	}; // History.initCore

	// Try to Initialise History
	if (!History.options || !History.options.delayInit) {
		History.init();
	}

})(window);
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
	Ext.ns('Curriki.module.search');

	var Search = Curriki.module.search;

	Search.settings = {
		gridWidth:(Ext.isIE6?620:'auto')
	};

	Search.stateProvider = new Ext.state.CookieProvider({
	});
	Ext.state.Manager.setProvider(Search.stateProvider);

	Search.sessionProvider = new Ext.state.CookieProvider({
		expires: null // Valid until end of browser session
	});


    Curriki.module.search.outerResources = {
        prefix: "http://www.curriki.org/xwiki/bin/view/",
        suffix: "?comingFrom=" + location.host,
        target: "currikiResources",
        ratingsPrefix: "http://www.curriki.org/xwiki/bin/view/",
        ratingsSuffix : "?viewer=comments"
        };
})();
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _

MODIFIED BY SLAVKO
*/

(function(){
Ext.ns('Curriki.module.search.util');

var Search = Curriki.module.search;
var module = Search.util;

module.init = function(){
	console.log('search util: init');

	module.logFilterList = {
        'outerResource':['subject','subjectparent', 'level', 'language', 'ictprfx', 'ict', 'review', 'special', 'other', 'sort', 'dir'],
        'resource':['subject','subjectparent', 'category', 'level', 'language', 'ictprfx', 'ict', 'review', 'special', 'other', 'sort', 'dir']
		,'group':['subject', 'level', 'language', 'policy', 'other', 'sort', 'dir']
		,'member':['subject', 'member_type', 'country', 'other', 'sort', 'dir']
		,'blog':['other', 'sort', 'dir']
		,'curriki':['other', 'sort', 'dir']
	};

	// Register a listener that will update counts on the tab
	module.registerTabTitleListener = function(modName){
		// Adjust title with count
		Ext.StoreMgr.lookup('search-store-'+modName).addListener(
			'datachanged'
			,function(store) {
				var overmax = false;
				var totalCount = 0;
				var resultCount = store.getTotalCount();
				if (!Ext.isEmpty(store.reader.jsonData) && !Ext.isEmpty(store.reader.jsonData.totalResults)) {
					totalCount = parseInt(store.reader.jsonData.totalResults);
				}
				if (totalCount > resultCount) {
					overmax = true;
				}

				var tab = Ext.getCmp('search-'+modName+'-tab');
				if (!Ext.isEmpty(tab)) {
					var titleMsg = _('search.tab.title.results');
					if (overmax && (_('search.tab.title.resultsmax_exceeds') !== 'search.tab.title.resultsmax_exceeds')) {
						titleMsg = _('search.tab.title.resultsmax_exceeds');
					}

					tab.setTitle(String.format(titleMsg, _('search.'+modName+'.tab.title'), resultCount, totalCount));

				}

				var pager = Ext.getCmp('search-pager-'+modName);
				if (!Ext.isEmpty(pager)) {
					var afterPageText = _('search.pagination.afterpage');
					if (overmax && (_('search.pagination.afterpage_resultsmax_exceeds') !== 'search.pagination.afterpage_resultsmax_exceeds')) {
						afterPageText = _('search.pagination.afterpage_resultsmax_exceeds');
					}
					pager.afterPageText = String.format(afterPageText, '{0}', totalCount);

					var displayMsg = _('search.pagination.displaying.'+modName);
					if (overmax && (_('search.pagination.displaying.'+modName+'_resultsmax_exceeds') !== 'search.pagination.displaying.'+modName+'_resultsmax_exceeds')) {
						displayMsg = _('search.pagination.displaying.'+modName+'_resultsmax_exceeds');
					}
					pager.displayMsg = String.format(displayMsg, '{0}', '{1}', '{2}', totalCount);
				}
			}
		);

        Ext.StoreMgr.lookup('search-store-'+modName).addListener(
                'beforeload'
                ,function(s, o) {
                    var store = Ext.StoreMgr.lookup('search-store-'+modName);
                    var pager = Ext.getCmp('search-pager-'+modName);
                    store.baseParams.rows = pager.pageSize;
                    return true;
            }
        );
		Ext.StoreMgr.lookup('search-store-'+modName).addListener(
			'load'
			,function(store, data, options) {
				var params = options.params||{};
				var tab = params.module;
				var terms = encodeURI(params.terms||'');
				var advancedPanel = Ext.getCmp('search-advanced-'+tab);
				var advanced = (advancedPanel&&!advancedPanel.collapsed)
				               ?'advanced'
				               :'simple';
				var page = ''; // Only if not first page
				if (params.start) {
					if (params.start !== '0') {
						page = '/start/'+params.start;
					}
				};
                if (params.rows) {
                    if (params.rows != '25') {
                        page = page + '/rows/'+params.rows;
                    }
                }

				var filters = ''; // Need to construct
				Ext.each(
					module.logFilterList[tab]
					,function(filter){
						if (!Ext.isEmpty(params[filter], false)){
							filters += '/'+filter+'/'+encodeURI(params[filter]);
						}
					}
				);

                var pageurl = '';
				if(Curriki.module.search.util.isInEmbeddedMode()){
					Curriki.module.search.util.sendResizeMessageToEmbeddingWindow();
                    pageurl = '/features/embeddedsearch/'+tab+'/'+terms+'/'+advanced+filters+page;
                }else {
                    pageurl = '/features/search/'+tab+'/'+terms+'/'+advanced+filters+page;
                }

                Curriki.logView(pageurl);

				// Add to history
                Search.saveState('?state='+encodeURI(pageurl));

			}
		);

        Ext.StoreMgr.lookup('search-store-'+modName).addListener(
            'exception'
            ,Curriki.notifyException
        );

	};

	// Perform a search for a module
	module.doSearch = function(modName, start){
		console.log('Doing search', modName, start);
		var filters = {};

		// Global panel (if exists)
		var filterPanel = Ext.getCmp('search-termPanel');
		if (!Ext.isEmpty(filterPanel)) {
			var filterForm = filterPanel.getForm();
			if (!Ext.isEmpty(filterForm)) {
				Ext.apply(filters, filterForm.getValues(false));
			}
		}
        var modName2 = modName;
        if(modName2=='outerResource') modName2='resource';
        if(modName2=='curriki') modName2='discussions';
		Ext.apply(filters, {module: modName2});

		// Module panel
		filterPanel = Ext.getCmp('search-filterPanel-'+modName);
		if (!Ext.isEmpty(filterPanel)) {
			var filterForm = filterPanel.getForm();
			if (!Ext.isEmpty(filterForm)) {
				Ext.apply(filters, filterForm.getValues(false));
			}
		}

        

		// Check for emptyText value in terms field
		if (filters.terms && filters.terms === _('search.text.entry.label')){
			filters.terms = '';
		}

		console.log('Applying search filters', filters);

		Ext.apply(Ext.StoreMgr.lookup('search-store-'+modName).baseParams || {}, filters);

		var pager = Ext.getCmp('search-pager-'+modName)
		if (!Ext.isEmpty(pager)) {
			console.log('Searching', filters);
			pager.doLoad(Ext.num(start, 0)); // Reset to first page if the tab is shown
		}
		console.log('Done util.doSearch', filters);
	};

	// General term panel (terms and search button)
	module.createTermPanel = function(modName, form){
		return {
			xtype:'panel'
			,labelAlign:'left'
			,id:'search-termPanel-'+modName
			,cls:'term-panel'
			,border:false
			,items:[{
				layout:'column'
				,border:false
				,defaults:{border:false}
				,items:[{
					layout:'form'
					,id:'search-termPanel-'+modName+'-form'
					,cls:'search-termPanel-form'
					,items:[{
						xtype:'textfield'
						,id:'search-termPanel-'+modName+'-terms'
						,cls:'search-termPanel-terms'
						,fieldLabel:_('search.text.entry.label')
						,name:'terms'
						,hideLabel:true
						,emptyText:_('search.text.entry.label')
						,listeners:{
							specialkey:{
								fn:function(field, e){
									if (e.getKey() === Ext.EventObject.ENTER) {
										e.stopEvent();
                                        if('resource'==modName && Ext.StoreMgr.lookup('search-store-resource').sortInfo) {
                                            Ext.StoreMgr.lookup('search-store-resource').sortInfo.field = 'score';
                                            Ext.StoreMgr.lookup('search-store-resource').sortInfo.direction = 'DESC';
                                        }
										Search.doSearch(modName, true);
									}
								}
							}
						}
					}]
				},{
					layout:'form'
					,id:'search-termPanel-buttonColumn-'+modName
					,cls:'search-termPanel-buttonColumn'
					,items:[{
						xtype:'button'
						,id:'search-termPanel-button-'+modName
						,cls:'search-termPanel-button'
						,text:_('search.text.entry.button')
						,listeners:{
							click:{
								fn: function(){
                                    if('resource'==modName && Ext.StoreMgr.lookup('search-store-resource').sortInfo) {
                                        Ext.StoreMgr.lookup('search-store-resource').sortInfo.field = 'score';
                                        Ext.StoreMgr.lookup('search-store-resource').sortInfo.direction = 'DESC';
                                    }
									Search.doSearch(modName, true);
								}
							}
						}
					}]
				},{
					xtype:'box'
					,id:'search-termPanel-tips-'+modName
					,cls:'search-termPanel-tips'
					,autoEl:{html:'<a href="/xwiki/bin/view/Search/Tips?xpage=popup" target="search_tips" onclick="{var popup=window.open(this.href, \'search_tips\', \'width=725,height=400,status=no,toolbar=no,menubar=no,location=no,resizable=yes\'); popup.focus();} return false;">'+_('search.text.entry.help.button')+'</a>'}
				}]
			},{
				xtype:'hidden'
				,name:'other'
				,id:'search-termPanel-other-'+modName
				,value:(!Ext.isEmpty(Search.restrictions)?Search.restrictions:'')
			}]
		};
	};

/*
	// General help panel
	module.createHelpPanel = function(modName, form){
		var cookie = 'search_help_'+modName;
		return {
			xtype:'fieldset'
			,id:'search-helpPanel-'+modName
			,title:_('search.text.entry.help.button')
			,collapsible:true
			,collapsed:((Search.sessionProvider.get(cookie, 0)===0)?true:false)
			,listeners:{
				collapse:{
					fn:function(panel){
						Search.sessionProvider.clear(cookie);
					}
				}
				,expand:{
					fn:function(panel){
						Search.sessionProvider.set(cookie, 1);
					}
				}
			}
			,border:true
			,autoHeight:true
			,items:[{
				xtype:'box'
				,autoEl:{
					tag:'div'
					,html:_('search.text.entry.help.text')
					,cls:'help-text'
				}
			}]
		};
	};
*/

	module.fieldsetPanelSave = function(panel, state){
		if (Ext.isEmpty(state)) {
			state = {};
		}
		if (!panel.collapsed) {
			state.collapsed = panel.collapsed;
		} else {
			state = null;
		}
		console.log('fieldset Panel Save state:', state);
		Search.sessionProvider.set(panel.stateId || panel.id, state);
	};

	module.fieldsetPanelRestore = function(panel, state){
		if (!Ext.isEmpty(state)
		    && !Ext.isEmpty(state.collapsed)
		    && !state.collapsed) {
			panel.expand(false);
		}
	};

	module.isInEmbeddedMode = function(){
		return !(	//If this attribute is not set, we can are not in embedded mode
					typeof Curriki.module.search.embeddingPartnerUrl === "undefined");
	};

    module.sendResizeMessageToEmbeddingWindow = function() {
		var height = document.body.scrollHeight + 25;
		console.log("search: sending resource view height to embedding window (" + height + "px)");
		var data = "resize:height:"+ height + "px;"
		window.parent.postMessage(data,'*');
	};

	module.sendResourceUrlToEmbeddingWindow = function(url) {
		console.log("search: sending resource url to embedding window (" + url + ")");
		var data = "resourceurl:"+url;
		window.parent.postMessage(data,'*');
	};
	
	module.registerSearchLogging = function(tab){
	};


};

Ext.onReady(function(){
	module.init();
});
})();
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modNames = ['outerResource', 'resource'];
for(var i=0; i<2; i++) {
    var modName = modNames[i];
    Ext.ns('Curriki.module.search.data.'+modName);

Curriki.module.search.data[modName].init = function(modName){
    var data = Curriki.module.search.data[modName];
    console.log('data.'+modName+': init');

	// Set up filters
	data.filter = {};
	var f = data.filter; // Alias

	f.data = {};

	f.data.subject =  {
		mapping: Curriki.data.fw_item.fwMap['FW_masterFramework.WebHome']
		,list: []
		,data: [
			['', _('CurrikiCode.AssetClass_fw_items_FW_masterFramework.UNSPECIFIED')]
		]
	};
	f.data.subject.mapping.each(function(value){
		f.data.subject.list.push(value.id);
	});

	// CURRIKI-2872
	f.data.subject.list.push('UNCATEGORIZED');

	f.data.subject.list.each(function(value){
		f.data.subject.data.push([
			value
			,_('CurrikiCode.AssetClass_fw_items_'+value)
		]);
	});

	f.data.subsubject =  {
		mapping: Curriki.data.fw_item.fwMap
		,data: [
		]
	};
	f.data.subject.mapping.each(function(parentItem){
		f.data.subsubject.data.push([
			parentItem.id
			,_('CurrikiCode.AssetClass_fw_items_'+parentItem.id+'.UNSPECIFIED')
			,parentItem.id
		]);
		f.data.subsubject.mapping[parentItem.id].each(function(subject){
			f.data.subsubject.data.push([
				subject.id
				,_('CurrikiCode.AssetClass_fw_items_'+subject.id)
				,parentItem.id
			]);
		});
	});

	f.data.level =  {
		list: Curriki.data.el.list
		,data: [
			['', _('CurrikiCode.AssetClass_educational_level_UNSPECIFIED')]
		]
	};
	f.data.level.list.each(function(value){
		f.data.level.data.push([
			value
			,_('CurrikiCode.AssetClass_educational_level_'+value)
		]);
	});

	f.data.ict =  {
		fullList: Curriki.data.ict.list
		,parentList: {}
		,list: []
		,data: [
			['', _('CurrikiCode.AssetClass_instructional_component_UNSPECIFIED', '   ')]
		]
	};
	f.data.ict.fullList.each(function(value){
		var name = value.replace(/_.*/, '');
		f.data.ict.parentList[name] = name;
	});
	Object.keys(f.data.ict.parentList).each(function(value){
        var sort = _('CurrikiCode.AssetClass_instructional_component_'+value);
        if (value === 'other') {
                sort = 'zzz';
        }

		f.data.ict.data.push([
			value
			,_('CurrikiCode.AssetClass_instructional_component_'+value)
			,sort
		]);
	});

	f.data.subict =  {
		list: Curriki.data.ict.list
		,parents: {}
		,data: [
		]
	};
	f.data.subict.list.each(function(value){
		var parentICT = value.replace(/_.*/, '');
		if (parentICT !== value) {
			if (Ext.isEmpty(f.data.subict.parents[parentICT])) {
				f.data.subict.data.push([
					parentICT+'*'
					,_('CurrikiCode.AssetClass_instructional_component_'+parentICT+'_UNSPECIFIED')
					,parentICT
					,'   '
				]);
				f.data.subict.parents[parentICT] = parentICT;
			}

			var sort = _('CurrikiCode.AssetClass_instructional_component_'+value);
			if (value === 'other') {
					sort = 'zzz';
			}

			f.data.subict.data.push([
				value
				,_('CurrikiCode.AssetClass_instructional_component_'+value)
				,parentICT
				,sort
			]);
		}
	});

	f.data.language =  {
		list: Curriki.data.language.list
		,data: [
			['', _('CurrikiCode.AssetClass_language_UNSPECIFIED')]
		]
	};
	f.data.language.list.each(function(value){
		f.data.language.data.push([
			value
			,_('CurrikiCode.AssetClass_language_'+value)
		]);
	});

	f.data.category =  {
		list: Curriki.data.category.list
		,data: [
			['', _('CurrikiCode.AssetClass_category_UNSPECIFIED'), '   ']
		]
	};
	f.data.category.list.each(function(value){
		var sort = _('CurrikiCode.AssetClass_category_'+value);
		if (value === 'unknown') {
			sort = 'zzz';
		}
		if (value !== 'collection') { //collection should not be in the list
			f.data.category.data.push([
				value
				,_('CurrikiCode.AssetClass_category_'+value)
				,sort
			]);
		}
	});

	f.data.review = {
		list: [ 'partners', 'highest_rated', 'members.highest_rated' ]
		,data: [
			['', _('search.resource.review.selector.UNSPECIFIED')]
		]
	};
	f.data.review.list.each(function(review){
		f.data.review.data.push([
			review
			,_('search.resource.review.selector.'+review)
		]);
	});

	f.data.special = {
		list:     // not logged-in? no "own contributions" filter
        typeof(Curriki.userinfo.userName)=='undefined' ||  Curriki.userinfo.userName=="XWiki.XWikiGuest" || modName=='outerResource' ?
            [ 'collections', 'updated', 'info-only' ]
            : [ 'contributions', 'collections', 'updated', 'info-only' ]
		,data: [
			['', _('search.resource.special.selector.UNSPECIFIED')]
		]
	};
	f.data.special.list.each(function(special){
		f.data.special.data.push([
			special
			,_('search.resource.special.selector.'+special)
		]);
	});


	f.store = {
		subject: new Ext.data.SimpleStore({
			fields: ['id', 'subject']
			,data: f.data.subject.data
			,id: 0
		})

		,subsubject: new Ext.data.SimpleStore({
			fields: ['id', 'subject', 'parentItem']
			,data: f.data.subsubject.data
			,id: 0
		})

		,level: new Ext.data.SimpleStore({
			fields: ['id', 'level']
			,data: f.data.level.data
			,id: 0
		})

		,ict: new Ext.data.SimpleStore({
			fields: ['id', 'ict', 'sortValue']
			,sortInfo: {field:'sortValue', direction:'ASC'}
			,data: f.data.ict.data
			,id: 0
		})

		,subict: new Ext.data.SimpleStore({
			fields: ['id', 'ict', 'parentICT', 'sortValue']
			,sortInfo: {field:'sortValue', direction:'ASC'}
			,data: f.data.subict.data
			,id: 0
		})

		,language: new Ext.data.SimpleStore({
			fields: ['id', 'language']
			,data: f.data.language.data
			,id: 0
		})

		,category: new Ext.data.SimpleStore({
			fields: ['id', 'category', 'sortValue']
			,sortInfo: {field:'sortValue', direction:'ASC'}
			,data: f.data.category.data
			,id: 0
		})

		,review: new Ext.data.SimpleStore({
			fields: ['id', 'review']
			,data: f.data.review.data
			,id: 0
		})

		,special: new Ext.data.SimpleStore({
			fields: ['id', 'special']
			,data: f.data.special.data
			,id: 0
		})
	};



	// Set up data store
	data.store = {};

	data.store.record = new Ext.data.Record.create([
		{ name: 'title' }
		,{ name: 'assetType' }
		,{ name: 'category' }
		,{ name: 'subcategory' }
		,{ name: 'ict' }
		,{ name: 'ictText' }
		,{ name: 'ictIcon' }
		,{ name: 'contributor' }
		,{ name: 'contributorName' }
		,{ name: 'rating', mapping: 'review' }
		,{ name: 'memberRating', mapping: 'rating' }
		,{ name: 'ratingCount' }
		,{ name: 'description' }
		,{ name: 'fwItems' }
		,{ name: 'levels' }
		,{ name: 'parents' }
		,{ name: 'lastUpdated' }
		,{ name: 'updated' }
        ,{ name: 'score' }
	]);

	data.store.results = new Ext.data.Store({
		storeId: 'search-store-'+modName
		,proxy: new Ext.data.HttpProxy({
			url: document.location.pathname.endsWith("Old") ?
                    '/xwiki/bin/view/Search/Resources' : (modName=='outerResource'? '/outerCurrikiExtjs' : '/currikiExtjs')
			,method:'GET'
		})
		,baseParams: {	xpage: "plain"
                     	//"json.wrf": "Curriki.module.search.data.resource.store.results.loadData" // parameter for Solr to wrap the json result into a function call
                     	//, '_dc':(new Date().getTime())
                      }
		,reader: new Ext.data.JsonReader({
			root: 'rows'
			,totalProperty: 'resultCount'
			,id: 'page'
		}, data.store.record)

		// turn on remote sorting
		,remoteSort: true
	});
    if(Curriki.userinfo.userGroups) data.store.results.baseParams.groupsId= Curriki.userinfo.userGroups;
    if(Curriki.userinfo.userName) data.store.results.baseParams.userId = Curriki.userinfo.userName;
    if(Curriki.userinfo.isAdmin) data.store.results.baseParams.isAdmin = true;
    if(Curriki.isISO8601DateParsing() ) data.store.results.baseParams.dateFormat="ISO8601";
	data.store.results.setDefaultSort('score', 'desc');



    // Set up renderers
	data.renderer = {
		title: function(value, metadata, record, rowIndex, colIndex, store){
            console.log("render title " + value);
            if(typeof(value)!="string") title ="";
			// Title
			var page = record.id.replace(/\./, '/');

			var desc = Ext.util.Format.stripTags(record.data.description);
			desc = Ext.util.Format.ellipsis(desc, 256);
			desc = Ext.util.Format.htmlEncode(desc);

			var fw = Curriki.data.fw_item.getRolloverDisplay(record.data.fwItems||[]);
			var lvl = Curriki.data.el.getRolloverDisplay(record.data.levels||[]);
			var lastUpdated = record.data.lastUpdated||'';

			var qTipFormat = '{1}<br />{0}<br /><br />';

			// Add lastUpdated if available
			if (lastUpdated !== '') {
				qTipFormat = qTipFormat+'{7}<br />{6}<br /><br />';
			}

			// Base qTip (framework, ed levels)
			qTipFormat = qTipFormat+'{3}<br />{2}<br />{5}<br />{4}';


			desc = String.format(qTipFormat
				,desc,_('global.title.popup.description')
				,fw,_('global.title.popup.subject')
				,lvl,_('global.title.popup.educationlevel')
				,lastUpdated,_('global.title.popup.last_updated')
			);

			// Asset Type icon
			var assetType = record.data.assetType;
			var category = record.data.category;
			var subcategory = record.data.subcategory;
			metadata.css = String.format('resource-{0} category-{1} subcategory-{1}_{2}', assetType, category, subcategory); // Added to <td>

			var rollover = _(category+'.'+subcategory);
			if (rollover === category+'.'+subcategory) {
				rollover = _('unknown.unknown');
			}

			if(Curriki.module.search.util.isInEmbeddedMode()){
				return String.format('<img class="x-tree-node-icon assettype-icon" src="{3}" ext:qtip="{4}" /><a  target="_blank" href="' + Curriki.module.search.resourceDisplay + '?resourceurl=/xwiki/bin/view/{0}" class="asset-title" ext:qtip="{2}">{1}</a>', escape(page+'?'+Curriki.module.search.embedViewMode), Ext.util.Format.ellipsis(value, 80), desc, Ext.BLANK_IMAGE_URL, rollover);			
				// return String.format('<img class="x-tree-node-icon assettype-icon" src="{3}" ext:qtip="{4}" /><a onclick="Curriki.module.search.util.sendResourceUrlToEmbeddingWindow(\'/xwiki/bin/view/{0}\')" href="#" class="asset-title" ext:qtip="{2}">{1}</a>', escape(page+"?viewer=embed-teachhub"), Ext.util.Format.ellipsis(value, 80), desc, Ext.BLANK_IMAGE_URL, rollover);			
            } else if(modName=="outerResource") {
                var outer = Curriki.module.search.outerResources;
                return String.format('<img class="x-tree-node-icon assettype-icon" src="{0}" ext:qtip="{1}" /><a href="{2}{3}{4}" target="{5}" class="asset-title" ext:qtip="{1}">{6}</a>',
                    Ext.BLANK_IMAGE_URL, desc, outer.prefix, page, outer.suffix, outer.target,  value);
            }else {
				return String.format('<img class="x-tree-node-icon assettype-icon" src="{3}" ext:qtip="{4}" /><a href="/xwiki/bin/view/{0}" class="asset-title" ext:qtip="{2}">{1}</a>',
                    page, Ext.util.Format.ellipsis(value, 80), desc, Ext.BLANK_IMAGE_URL, rollover);
			}
		}

		,ict: function(value, metadata, record, rowIndex, colIndex, store){
			var css;
			var dotIct;
			var ict = record.data.ict;
            console.log("render ict " + value);
			if (!Ext.isEmpty(ict)){
				// Find CSS classes needed
				var topIct = ict.replace(/_.*/, '');
				css = 'ict-'+topIct;
				if (topIct !== ict) {
					css = css + ' ict-'+ict;
				}

				// Get value to use in lookup key
				dotIct = ict.replace(/_/, '.');
			} else {
				css = 'ict-unknown';
				dotIct = 'unknown';
			}
			metadata.css = css;
			return String.format('<img class="ict-icon" src="{1}" /><span class="ict-title">{0}</span>', _('search.resource.ict.'+dotIct), Ext.BLANK_IMAGE_URL);
		}

		,contributor: function(value, metadata, record, rowIndex, colIndex, store){
			var page = value.replace(/\./, '/');
            console.log("render contributor " + value);
            if(typeof("value")!="string") value="";
            if(Curriki.module.search.util.isInEmbeddedMode()){
				return String.format('<a href="/xwiki/bin/view/{0}" target="_blank">{1}</a>', page, record.data.contributorName);
            } else if(modName=="outerResource") {
                var outer = Curriki.module.search.outerResources;
                return String.format('<a href="{0}{1}{2}" target="{3}">{4}</a>', outer.prefix, page, outer.suffix, outer.target, record.data.contributorName);
			} else{
				return String.format('<a href="/xwiki/bin/view/{0}">{1}</a>', page, record.data.contributorName);
			}
		}

		,rating: function(value, metadata, record, rowIndex, colIndex, store){
            console.log("render rating " + value);
			if (typeof(value)=="string" && value != "") {
				var page = record.id.replace(/\./, '/');

				metadata.css = String.format('crs-{0}', value); // Added to <td>
				//metadata.attr = String.format('title="{0}"', _('curriki.crs.rating'+value)); // Added to <div> around the returned HTML
				
				
				if(Curriki.module.search.util.isInEmbeddedMode()){
					return String.format('<a href="/xwiki/bin/view/{3}?viewer=comments" target="_blank"><img class="crs-icon" alt="" src="{2}" /><span class="crs-text">{1}</span></a>', value, _('search.resource.review.'+value), Ext.BLANK_IMAGE_URL, page);
                } else if(modName=="outerResource") {
                    var outer = Curriki.module.search.outerResources;
                    return String.format('<a "{0}{1}{2}" target="{3}"><img class="crs-icon" alt="" src="{4}" /><span class="crs-text">{5}</span></a>',
                        outer.ratingsPrefix, page, outer.ratingsSuffix, outer.target, Ext.BLANK_IMAGE_URL, _('search.resource.review.'+value));
                } else {
					return String.format('<a href="/xwiki/bin/view/{3}?viewer=comments"><img class="crs-icon" alt="" src="{2}" /><span class="crs-text">{1}</span></a>', value, _('search.resource.review.'+value), Ext.BLANK_IMAGE_URL, page);
				}
			} else {
				return String.format('');
			}
		}

		,memberRating: function(value, metadata, record, rowIndex, colIndex, store){
            console.log("render memberRating " + value);
			if (typeof(value)=="string"  && value != "" && value != "0" && value != 0) {
				var page = record.id.replace(/\./, '/');
				var ratingCount = record.data.ratingCount;

				if (ratingCount != "" && ratingCount != "0" && ratingCount != 0) {
					metadata.css = String.format('rating-{0}', value);
					if(Curriki.module.search.util.isInEmbeddedMode()){
						return String.format('<a href="/xwiki/bin/view/{2}?viewer=comments" target="_blank"><img class="rating-icon" src="{4}" ext:qtip="{3}" /></a><a href="/xwiki/bin/view/{2}?viewer=comments" ext:qtip="{3}" target="_blank"> ({1})</a>', value, ratingCount, page, _('search.resource.rating.'+value), Ext.BLANK_IMAGE_URL);
                    } else if(modName=="outerResource") {
                        var outer = Curriki.module.search.outerResources;
                        return String.format('<a href="{0}{1}{2}"><img class="rating-icon" src="{3}" ext:qtip="{4}" /></a><a href="{0}{1}{2}" ext:qtip="{4}"> ({5})</a>',
                            outer.ratingsPrefix, page, outer.ratingsSuffix, Ext.BLANK_IMAGE_URL, _('search.resource.rating.'+value), ratingCount);
                    }else{
						return String.format('<a href="/xwiki/bin/view/{2}?viewer=comments"><img class="rating-icon" src="{4}" ext:qtip="{3}" /></a><a href="/xwiki/bin/view/{2}?viewer=comments" ext:qtip="{3}"> ({1})</a>', value, ratingCount, page, _('search.resource.rating.'+value), Ext.BLANK_IMAGE_URL);
					}
				} else {
					return String.format('');
				}
			} else {
				return String.format('');
			}
		}

		,updated: function(value, metadata, record, rowIndex, colIndex, store){
            console.log("render updated " + value);
            if(typeof("value")!="string") return "";
			var dt = Ext.util.Format.date(value, 'M-d-Y');
            if(typeof(dt)!="string") return "";
			return String.format('{0}', dt);
		}
        , score: function(value, metadata, record, rowIndex, colIndex, store){
            if(typeof(value)!="number") value=0;
            return value;
         }
	};
    console.log("Finished initting data for " + modName + ".");
};
}
})();

Ext.onReady(function(){
    Curriki.module.search.data.outerResource.init("outerResource");
    Curriki.module.search.data.resource.init("resource");
});
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */
Ext.ns('Curriki.module.search');
var Search = Curriki.module.search;

(function(){

    var modNames = ['outerResource', 'resource'];
    for(var i=0; i<2; i++) {
    var modName = modNames[i];

Ext.ns('Curriki.module.search.form.'+modName);




Search.form[modName].init = function(modName){
    var form = Search.form[modName];
    var data = Search.data[modName];
    console.log('form.'+modName+': init');

	var comboWidth = 140;
	var comboListWidth = 250;

	// Plugin to add icons to ICT combo box
	form.ictCombo = function(config) {
		Ext.apply(this, config);
	};

    // disable top search box, it triggers inconsistent searches, rather not use this double of function
    var searchBox = $('curriki-searchbox');
    if(!Ext.isEmpty(searchBox)) {
        searchBox.setValue("...");
        searchBox.setAttribute("curriki:deftxt","...");
        searchBox.disable();
        
    }
    var searchBoxGoBtn = $('searchbtn');
    if(!Ext.isEmpty(searchBoxGoBtn)) {
        searchBoxGoBtn.innerHTML="";
    }


	Ext.extend(form.ictCombo, Ext.util.Observable, {
		init:function(combo){
			Ext.apply(combo, {
				tpl:  '<tpl for=".">'
					+ '<div class="x-combo-list-item ict-icon-combo-item '
					+ 'ict-{' + combo.valueField + '}">'
					+ '<img class="ict-icon" src="'+Ext.BLANK_IMAGE_URL+'"/>'
					+ '<span class="ict-title">{' + combo.displayField + '}</span>'
					+ '</div></tpl>',

				onRender:combo.onRender.createSequence(function(ct, position) {
					// adjust styles
					this.wrap.applyStyles({position:'relative'});
					this.el.addClass('ict-icon-combo-input');

					// add div for icon
					this.icon = Ext.DomHelper.append(this.el.up('div.x-form-field-wrap'), {
						tag:'div'
						,style:'position:absolute'
						,children:{tag:'div', cls:'ict-icon'}
					});
				}), // end of function onRender

				setIconCls:function() {
					var rec = this.store.query(this.valueField, this.getValue()).itemAt(0);
					if(rec) {
						this.icon.className = 'ict-icon-combo-icon ict-'+rec.get(this.valueField);
					}
				}, // end of function setIconCls

				setValue:combo.setValue.createSequence(function(value) {
					this.setIconCls();
				})
			});
		}
	});

	// Plugin to add icons to Category combo box
	form.categoryCombo = function(config) {
		Ext.apply(this, config);
	};
	Ext.extend(form.categoryCombo, Ext.util.Observable, {
		init:function(combo){
			Ext.apply(combo, {
				tpl:  '<tpl for=".">'
					+ '<div class="x-combo-list-item category-icon-combo-item '
					+ 'category-{' + combo.valueField + '}">'
					+ '<img class="category-icon" src="'+Ext.BLANK_IMAGE_URL+'"/>'
					+ '<span class="category-title">{' + combo.displayField + '}</span>'
					+ '</div></tpl>',

				onRender:combo.onRender.createSequence(function(ct, position) {
					// adjust styles
					this.wrap.applyStyles({position:'relative'});
					this.el.addClass('category-icon-combo-input');

					// add div for icon
					this.icon = Ext.DomHelper.append(this.el.up('div.x-form-field-wrap'), {
						tag:'div'
						,style:'position:absolute'
						,children:{tag:'div', cls:'category-icon'}
					});
				}), // end of function onRender

				setIconCls:function() {
					var rec = this.store.query(this.valueField, this.getValue()).itemAt(0);
					if(rec) {
						this.icon.className = 'category-icon-combo-icon category-'+rec.get(this.valueField);
					}
				}, // end of function setIconCls

				setValue:combo.setValue.createSequence(function(value) {
					this.setIconCls();
				})
			});
		}
	});

	form.termPanel = Search.util.createTermPanel(modName, form);
//	form.helpPanel = Search.util.createHelpPanel(modName, form);

	form.filterPanel = {
		xtype:'form'
		,labelAlign:'left'
		,id:'search-filterPanel-'+modName
		,formId:'search-filterForm-'+modName
		,border:false
		,items:[
			form.termPanel
//			,form.helpPanel
			,{
				xtype:'fieldset'
				,title:''// _('search.advanced.search.button')
				,id:'search-advanced-'+modName
				,autoHeight:true
				,collapsible:false
				,collapsed:false
				,animCollapse:false
				,border:true
				,stateful:true
				,stateEvents:['expand','collapse']
				,listeners:{
					'statesave':{
						fn:Search.util.fieldsetPanelSave
					}
					,'staterestore':{
						fn:Search.util.fieldsetPanelRestore
					}
					,'expand':{
						fn:function(panel){
							// CURRIKI-2989
							//  - Force a refresh of the grid view, as this
							//    seems to make the advanced search fieldset
							//    visible in IE7
							Ext.getCmp('search-results-'+modName).getView().refresh();

							Ext.select('.x-form-field-wrap', false, 'search-advanced-'+modName).setWidth(comboWidth);

							// CURRIKI-2873
							// - Force a repaint of the fieldset
							Ext.getCmp('search-termPanel-'+modName).el.repaint();
						}
					}
					,'collapse':{
						fn:function(panel){
							Ext.getCmp('search-results-'+modName).getView().refresh();
							Ext.getCmp('search-termPanel-'+modName).el.repaint();
						}
					}
				}
				,items:[{
					layout:'column'
					,border:false
					,defaults:{
						border:false
						,hideLabel:true
					}
					,items:[{
						columnWidth:0.33
						,layout:'form'
						,defaults:{
							hideLabel:true
						}
						,items:[{
							xtype:'combo'
							,id:'combo-subject-'+modName
							,fieldLabel:'Subject'
							,hiddenName:'subjectparent'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.subject
							,displayField:'subject'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('CurrikiCode.AssetClass_fw_items_FW_masterFramework.UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
							,listeners:{
								select:{
									fn:function(combo, value){
										var subSubject = Ext.getCmp('combo-subsubject-'+modName);
										if (combo.getValue() === '') {
											subSubject.clearValue();
											subSubject.hide();
										// Special case - UNCATEGORIZED does not show sub-items
										} else if (combo.getValue() === 'UNCATEGORIZED') {
											subSubject.show();
											subSubject.clearValue();
											subSubject.store.filter('parentItem', combo.getValue());
											subSubject.setValue(combo.getValue());
											subSubject.hide();
										} else {
											subSubject.show();
											subSubject.clearValue();
											subSubject.store.filter('parentItem', combo.getValue());
											subSubject.setValue(combo.getValue());
										}
									}
								}
							}
						},{
							xtype:'combo'
							,fieldLabel:'Sub Subject'
							,id:'combo-subsubject-'+modName
							,hiddenName:'subject'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.subsubject
							,displayField:'subject'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
	//						,emptyText:'Select a Sub Subject...'
							,selectOnFocus:true
							,forceSelection:true
							,lastQuery:''
							,hidden:true
							,hideMode:'visibility'
						},{
							xtype:'combo'
							,id:'combo-category-'+modName
							,fieldLabel:'Category'
							,hiddenName:'category'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.category
							,displayField:'category'
							,valueField:'id'
							,plugins:new form.categoryCombo()
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('CurrikiCode.AssetClass_category_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
						}]
					},{
						columnWidth:0.33
						,layout:'form'
						,defaults:{
							hideLabel:true
						}
						,items:[{
							xtype:'combo'
							,id:'combo-level-'+modName
							,fieldLabel:'Level'
							,mode:'local'
							,width:comboWidth
							,listWidth:comboListWidth
							,store:data.filter.store.level
							,hiddenName:'level'
							,displayField:'level'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('CurrikiCode.AssetClass_educational_level_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
						},{
							xtype:'combo'
							,id:'combo-language-'+modName
							,fieldLabel:'Language'
							,hiddenName:'language'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.language
							,displayField:'language'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('CurrikiCode.AssetClass_language_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
						},{
							xtype:'combo'
							,id:'combo-review-'+modName
							,fieldLabel:'Review'
							,hiddenName:'review'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.review
							,displayField:'review'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('search.resource.review.selector.UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
						}]
					},{
						columnWidth:0.34
						,layout:'form'
						,defaults:{
							hideLabel:true
						}
						,items:[{
							xtype:'combo'
							,id:'combo-ictprfx-'+modName
							,fieldLabel:'Instructional Type'
							,hiddenName:'ictprfx'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.ict
							,displayField:'ict'
							,valueField:'id'
							,plugins:new form.ictCombo()
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('CurrikiCode.AssetClass_instructional_component_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
							,listeners:{
								select:{
									fn:function(combo, value){
										var subICT = Ext.getCmp('combo-subICT-'+modName);
										if (combo.getValue() === '') {
											subICT.clearValue();
											subICT.hide();
										} else {
											subICT.clearValue();
											subICT.store.filter('parentICT', combo.getValue());
											var p = subICT.store.getById(combo.getValue()+'*');
											if (Ext.isEmpty(p)) {
												subICT.setValue(combo.getValue());
												subICT.hide();
											} else {
												subICT.setValue(combo.getValue()+'*');
												subICT.show();
											}
										}
									}
								}
							}
						},{
							xtype:'combo'
							,fieldLabel:'Sub ICT'
							,hiddenName:'ict'
							,width:comboWidth
							,listWidth:comboListWidth
							,id:'combo-subICT-'+modName
							,mode:'local'
							,store:data.filter.store.subict
							,displayField:'ict'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:'Select a Sub ICT...'
							,selectOnFocus:true
							,forceSelection:true
							,lastQuery:''
							,hidden:true
							,hideMode:'visibility'
						},{
							xtype:'combo'
							,id:'combo-special-'+modName
							,fieldLabel:'Special Filters'
							,hiddenName:'special'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.special
							,displayField:'special'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('search.resource.special.selector.UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
						}]
					}]
				}]
			}
		]
	};

	form.rowExpander = new Ext.grid.RowExpander({


		tpl: new Ext.XTemplate(
			_('search.resource.resource.expanded.title'),
			'<ul>',
			'<tpl for="parents">',
				'<li class="resource-{assetType} category-{category} subcategory-{category}_{subcategory}">',
					'<a target="{[this.getLinkTarget(values)]}" href="{[this.getParentURL(values)]}" ext:qtip="{[this.getQtip(values)]}">',
						'{title}',
					'</a>',
				'</li>',
			'</tpl>',
			'</ul>', {
				getParentURL: function(values){
					var page = values.page||false;
					if (page) {
						if(Curriki.module.search.util.isInEmbeddedMode()){
							return Curriki.module.search.resourceDisplay + '?resourceurl=/xwiki/bin/view/'+ escape(page.replace(/\./, '/')+'?'+Curriki.module.search.embedViewMode);
						}else{
							return '/xwiki/bin/view/'+page.replace(/\./, '/');
						}
					} else {
						return '';
					}
				},
				getQtip: function(values){
					var f = Curriki.module.search.data[modName].filter;

					var desc = Ext.util.Format.stripTags(values.description||'');
					desc = Ext.util.Format.ellipsis(desc, 256);
					desc = Ext.util.Format.htmlEncode(desc);

					var fw = Curriki.data.fw_item.getRolloverDisplay(values.fwItems||[]);
					var lvl = Curriki.data.el.getRolloverDisplay(values.levels||[]);
			
					return String.format("{1}<br />{0}<br /><br />{3}<br />{2}<br />{5}<br />{4}"
						,desc,_('global.title.popup.description')
						,fw,_('global.title.popup.subject')
						,lvl,_('global.title.popup.educationlevel')
					);
				},

				getLinkTarget: function(values){
					if(Curriki.module.search.util.isInEmbeddedMode()){
							return '_blank';
						}else{
							return '_self';
						}
				}
			}
		)
	});

	form.rowExpander.renderer = function(v, p, record){
		var cls;
        if (record.data.parents && record.data.parents.size() > 0) {
			p.cellAttr = 'rowspan="2"';
			cls = 'x-grid3-row-expander';
			return String.format('<img class="{0}" src="{1}" ext:qtip="{2}" />', cls, Ext.BLANK_IMAGE_URL, _('search.resource.icon.plus.rollover'));
		} else {
			cls = 'x-grid3-row-expander-empty';
			return String.format('<img class="{0}" src="{1}" />', cls, Ext.BLANK_IMAGE_URL);
		}
	};

	form.rowExpander.on('expand', function(expander, record, body, idx){
		var row = expander.grid.view.getRow(idx);
		var iconCol = Ext.DomQuery.selectNode('img[class*=x-grid3-row-expander]', row); // TODO: here
		Ext.fly(iconCol).set({'ext:qtip':_('search.resource.icon.minus.rollover')});
		
		if(Curriki.module.search.util.isInEmbeddedMode()){
			Curriki.module.search.util.sendResizeMessageToEmbeddingWindow();
		}
	});

	form.rowExpander.on('collapse', function(expander, record, body, idx){
		var row = expander.grid.view.getRow(idx);
		var iconCol = Ext.DomQuery.selectNode('img[class*=x-grid3-row-expander]', row); // TODO: here
		Ext.fly(iconCol).set({'ext:qtip':_('search.resource.icon.plus.rollover')});

		if(Curriki.module.search.util.isInEmbeddedMode()){
			Curriki.module.search.util.sendResizeMessageToEmbeddingWindow();
		}
	});

	form.columnModel = new Ext.grid.ColumnModel([
		Ext.apply(
			form.rowExpander
			,{
                id:'score'
                ,tooltip:_('search.resource.column.header.score.tooltip')
                ,header: ' '
                ,dataIndex: 'score'
                ,width: 30
                ,sortable:true
			}
		)
        ,{
			id: 'title'
			,header: _('search.resource.column.header.title')
			,width: 164
			,dataIndex: 'title'
			,sortable:true
			,hideable:false
			,renderer: data.renderer.title
//			,tooltip:_('search.resource.column.header.title')
		},{
			id: 'ict'
			,width: 108
			,header: _('search.resource.column.header.ict')
			,dataIndex:'ictText'
			,sortable:true
			,renderer: data.renderer.ict
//			,tooltip: _('search.resource.column.header.ict')
		},{
			id: 'contributor'
			,width: 110
			,header: _('search.resource.column.header.contributor')
			,dataIndex:'contributor'
			,sortable:true
			,renderer: data.renderer.contributor
//			,tooltip: _('search.resource.column.header.contributor')
		},{
			id: 'rating'
			,width: 88
			,header: _('search.resource.column.header.rating')
			,dataIndex:'rating'
			,sortable:true
			,renderer: data.renderer.rating
//			,tooltip: _('search.resource.column.header.rating')
		},{
			id: 'memberRating'
			,width: 105
			,header: _('search.resource.column.header.member.rating')
			,dataIndex:'memberRating'
			,sortable:true
			,renderer: data.renderer.memberRating
//			,tooltip: _('search.resource.column.header.member.rating')
		},{
			id: 'updated'
			,width: 80
			,header: _('search.resource.column.header.updated')
			,dataIndex:'updated'
			,hidden:true
			,sortable:true
			,renderer: data.renderer.updated
//			,tooltip: _('search.resource.column.header.updated')
	}]);

	form.resultsPanel = {
		xtype:'grid'
		,id:'search-results-'+modName
		//,title:'Results'
		,border:false
		,autoHeight:true
		,width:Search.settings.gridWidth
		,autoExpandColumn:'title'
		,stateful:true
		,frame:false
		,stripeRows:true
		,viewConfig: {
			forceFit:true
			,enableRowBody:true
			,showPreview:true
			// Remove the blank space on right of grid (reserved for scrollbar)
			,scrollOffset:0
		}
		,columnsText:_('search.columns.menu.columns')
		,sortAscText:_('search.columns.menu.sort_ascending')
		,sortDescText:_('search.columns.menu.sort_descending')
		,store: data.store.results
		,sm: new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn})
		,cm: form.columnModel
		,loadMask: false
		,plugins: form.rowExpander
		,bbar: new Ext.PagingToolbar({
			id: 'search-pager-'+modName
			,plugins:new Ext.ux.Andrie.pPageSize({
				 variations: [10, 25, 50]
				,beforeText: _('search.pagination.pagesize.before')
				,afterText: _('search.pagination.pagesize.after')
				,addBefore: _('search.pagination.pagesize.addbefore')
				,addAfter: _('search.pagination.pagesize.addafter')
			})
			,pageSize: 25
			,store: data.store.results
			,displayInfo: true
			,displayMsg: _('search.pagination.displaying.'+modName)
			,emptyMsg: _('search.find.no.results')
			,beforePageText: _('search.pagination.beforepage')
			,afterPageText: _('search.pagination.afterpage')
			,firstText: _('search.pagination.first')
			,prevText: _('search.pagination.prev')
			,nextText: _('search.pagination.next')
			,lastText: _('search.pagination.last')
			,refreshText: _('search.pagination.refresh')
		})
	};

	form.mainPanel = {
		xtype:'panel'
		,id:'search-panel-'+modName
		,autoHeight:true
		,items:[
			form.filterPanel
			,form.resultsPanel
		]
	};

	form.doSearch = function(){
		Search.util.doSearch(modName);
	};

	// Adjust title with count
	Search.util.registerTabTitleListener(modName);
    console.log("Finished initting form for " + modName + ".");
    console.log("Now get: Curriki.module.search.form['otherResource']: " + Curriki.module.search.form['otherResource'])
};
}
    Ext.onReady(function(){
        for(var i=0; i<2; i++) {
            var modName = modNames[i];
            Search.form[modName].init(modName);
        }
    });

})();




// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'group';

Ext.ns('Curriki.module.search.data.'+modName);

var data = Curriki.module.search.data.group;

data.init = function(){
	console.log('data.'+modName+': init');


	// Set up filters
	data.filter = {};
	var f = data.filter; // Alias

	f.data = {};

	f.data.subject =  {
		mapping: Curriki.data.fw_item.fwMap['FW_masterFramework.WebHome']
		,list: []
		,data: [
			['', _('XWiki.CurrikiSpaceClass_topic_FW_masterFramework.WebHome.UNSPECIFIED')]
		]
	};
	f.data.subject.mapping.each(function(value){
		f.data.subject.list.push(value.id);
	});
	f.data.subject.list.each(function(value){
		f.data.subject.data.push([
			value
			,_('XWiki.CurrikiSpaceClass_topic_'+value)
		]);
	});

	f.data.subsubject =  {
		mapping: Curriki.data.fw_item.fwMap
		,data: [
		]
	};
	f.data.subject.mapping.each(function(parentItem){
		f.data.subsubject.data.push([
			parentItem.id
			,_('XWiki.CurrikiSpaceClass_topic_'+parentItem.id+'.UNSPECIFIED')
			,parentItem.id
		]);
		f.data.subsubject.mapping[parentItem.id].each(function(subject){
			f.data.subsubject.data.push([
				subject.id
				,_('XWiki.CurrikiSpaceClass_topic_'+subject.id)
				,parentItem.id
			]);
		});
	});

	f.data.level =  {
		list: Curriki.data.el.list
		,data: [
			['', _('XWiki.CurrikiSpaceClass_educationLevel_UNSPECIFIED')]
		]
	};
	f.data.level.list.each(function(value){
		f.data.level.data.push([
			value
			,_('XWiki.CurrikiSpaceClass_educationLevel_'+value)
		]);
	});

	f.data.policy =  {
		list: ['open', 'closed']
		,data: [
			['', _('search.XWiki.SpaceClass_policy_UNSPECIFIED')]
		]
	};
	f.data.policy.list.each(function(value){
		f.data.policy.data.push([
			value
			,_('search.XWiki.SpaceClass_policy_'+value)
		]);
	});

	f.data.language =  {
		list: Curriki.data.language.list
		,data: [
			['', _('XWiki.CurrikiSpaceClass_language_UNSPECIFIED')]
		]
	};
	f.data.language.list.each(function(value){
		f.data.language.data.push([
			value
			,_('XWiki.CurrikiSpaceClass_language_'+value)
		]);
	});

	f.store = {
		subject: new Ext.data.SimpleStore({
			fields: ['id', 'subject']
			,data: f.data.subject.data
			,id: 0
		})

		,subsubject: new Ext.data.SimpleStore({
			fields: ['id', 'subject', 'parentItem']
			,data: f.data.subsubject.data
			,id: 0
		})

		,level: new Ext.data.SimpleStore({
			fields: ['id', 'level']
			,data: f.data.level.data
			,id: 0
		})

		,policy: new Ext.data.SimpleStore({
			fields: ['id', 'policy']
			,data: f.data.policy.data
			,id: 0
		})

		,language: new Ext.data.SimpleStore({
			fields: ['id', 'language']
			,data: f.data.language.data
			,id: 0
		})
	};



	// Set up data store
	data.store = {};

	data.store.record = new Ext.data.Record.create([
		{ name: 'title' }
		,{ name: 'url' }
		,{ name: 'policy' }
		,{ name: 'description' }
		,{ name: 'updated' }
	]);

	data.store.results = new Ext.data.Store({
		storeId: 'search-store-'+modName
		,proxy: new Ext.data.HttpProxy({
            url: document.location.pathname.endsWith("Old") ?
                '/xwiki/bin/view/Search/Groups' : '/currikiExtjs'
			,method:'GET'
		})
		,baseParams: { xpage: "plain", '_dc':(new Date().getTime()) }

		,reader: new Ext.data.JsonReader({
			root: 'rows'
			,totalProperty: 'resultCount'
			,id: 'page'
		}, data.store.record)

		// turn on remote sorting
		,remoteSort: true
	});
    if(Curriki.isISO8601DateParsing() ) data.store.results.baseParams.dateFormat="ISO8601";
	data.store.results.setDefaultSort('title', 'asc');



	// Set up renderers
	data.renderer = {
		title: function(value, metadata, record, rowIndex, colIndex, store){
			return String.format('<a href="{0}">{1}</a>', record.data.url, value);
		}

		,policy: function(value, metadata, record, rowIndex, colIndex, store){
			if (value !== ''){
				metadata.css = 'policy-'+value;
			}
			var policy = _('search.group.icon.'+value);
			return String.format('<span ext:qtip="{1}">{0}</span>', policy, _('search.group.icon.'+value+'.rollover'));
		}

		,description: function(value, metadata, record, rowIndex, colIndex, store){
			var desc = Ext.util.Format.htmlDecode(value);
			desc = Ext.util.Format.stripScripts(value);
			desc = Ext.util.Format.stripTags(desc);
			desc = Ext.util.Format.ellipsis(desc, 128);
			desc = Ext.util.Format.htmlEncode(desc);
			desc = Ext.util.Format.trim(desc);
			return String.format('{0}', desc);
		}

		,updated: function(value, metadata, record, rowIndex, colIndex, store){
            if(typeof("value")!="string") return "";
            var dt = Ext.util.Format.date(value, 'M-d-Y');
            if(typeof(dt)!="string") return "";
			return String.format('{0}', dt);
		}
	};
};

Ext.onReady(function(){
	data.init();
});
})();
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'group';

Ext.ns('Curriki.module.search.form.'+modName);

var Search = Curriki.module.search;

var form = Search.form[modName];
var data = Search.data[modName];

form.init = function(){
	console.log('form.'+modName+': init');

	var comboWidth = 140;
	var comboListWidth = 250;

	form.termPanel = Search.util.createTermPanel(modName, form);
//	form.helpPanel = Search.util.createHelpPanel(modName, form);

	form.filterPanel = {
		xtype:'form'
		,labelAlign:'left'
		,id:'search-filterPanel-'+modName
		,formId:'search-filterForm-'+modName
		,border:false
		,items:[
			form.termPanel
//			,form.helpPanel
			,{
				xtype:'fieldset'
				,title:_('search.advanced.search.button')
				,id:'search-advanced-'+modName
				,autoHeight:true
				,collapsible:true
				,collapsed:true
				,animCollapse:false
				,border:true
				,stateful:true
				,stateEvents:['expand','collapse']
				,listeners:{
					'statesave':{
						fn:Search.util.fieldsetPanelSave
					}
					,'staterestore':{
						fn:Search.util.fieldsetPanelRestore
					}
					,'expand':{
						fn:function(panel){
							// CURRIKI-2989
							//  - Force a refresh of the grid view, as this
							//    seems to make the advanced search fieldset
							//    visible in IE7
							Ext.getCmp('search-results-'+modName).getView().refresh();

							Ext.select('.x-form-field-wrap', false, 'search-advanced-'+modName).setWidth(comboWidth);

							// CURRIKI-2873
							// - Force a repaint of the fieldset
							Ext.getCmp('search-termPanel-'+modName).el.repaint();
						}
					}
					,'collapse':{
						fn:function(panel){
							Ext.getCmp('search-results-'+modName).getView().refresh();
							Ext.getCmp('search-termPanel-'+modName).el.repaint();
						}
					}
				}
				,items:[{
					layout:'column'
					,border:false
					,defaults:{
						border:false
						,hideLabel:true
					}
					,items:[{
						columnWidth:0.33
						,layout:'form'
						,defaults:{
							hideLabel:true
						}
						,items:[{
							xtype:'combo'
							,fieldLabel:'Subject'
							,id:'combo-subject-'+modName
							,hiddenName:'subjectparent'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.subject
							,displayField:'subject'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('XWiki.CurrikiSpaceClass_topic_FW_masterFramework.WebHome.UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
							,listeners:{
								select:{
									fn:function(combo, value){
										var subSubject = Ext.getCmp('combo-subsubject-'+modName);
										if (combo.getValue() === '') {
											subSubject.clearValue();
											subSubject.hide();
										} else {
											subSubject.show();
											subSubject.clearValue();
											subSubject.store.filter('parentItem', combo.getValue());
											subSubject.setValue(combo.getValue());
										}
									}
								}
							}
						},{
							xtype:'combo'
							,fieldLabel:'Sub Subject'
							,id:'combo-subsubject-'+modName
							,hiddenName:'subject'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.subsubject
							,displayField:'subject'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
	//						,emptyText:'Select a Sub Subject...'
							,selectOnFocus:true
							,forceSelection:true
							,lastQuery:''
							,hidden:true
							,hideMode:'visibility'
						}]
					},{
						columnWidth:0.33
						,layout:'form'
						,defaults:{
							hideLabel:true
						}
						,items:[{
							xtype:'combo'
							,id:'combo-level-'+modName
							,fieldLabel:'Level'
							,mode:'local'
							,width:comboWidth
							,listWidth:comboListWidth
							,store:data.filter.store.level
							,hiddenName:'level'
							,displayField:'level'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('XWiki.CurrikiSpaceClass_educationLevel_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
						},{
							xtype:'combo'
							,id:'combo-language-'+modName
							,fieldLabel:'Language'
							,hiddenName:'language'
							,mode:'local'
							,width:comboWidth
							,listWidth:comboListWidth
							,store:data.filter.store.language
							,displayField:'language'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('XWiki.CurrikiSpaceClass_language_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
						}]
					},{
						columnWidth:0.34
						,layout:'form'
						,defaults:{
							hideLabel:true
						}
						,items:[{
							xtype:'combo'
							,id:'combo-policy-'+modName
							,fieldLabel:'Membership Policy'
							,hiddenName:'policy'
							,mode:'local'
							,width:comboWidth
							,listWidth:comboListWidth
							,store:data.filter.store.policy
							,displayField:'policy'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('search.XWiki.SpaceClass_policy_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
						}]
					}]
				}]
			}
		]
	}

	form.columnModel = new Ext.grid.ColumnModel([{
			id: 'policy'
			,header: _('search.group.column.header.policy')
			,width: 62
			,dataIndex: 'policy'
			,sortable:true
			,renderer: data.renderer.policy
//			,tooltip: _('search.group.column.header.policy')
		},{
			id: 'title'
			,header: _('search.group.column.header.name')
			,width: 213
			,dataIndex: 'title'
			,sortable:true
			,hideable:false
			,renderer: data.renderer.title
//			,tooltip:_('search.group.column.header.name')
		},{
			id: 'description'
			,width: 225
			,header: _('search.group.column.header.description')
			,dataIndex:'description'
			,sortable:false
			,renderer: data.renderer.description
//			,tooltip: _('search.group.column.header.description')
		},{
			id: 'updated'
			,width: 96
			,header: _('search.group.column.header.updated')
			,dataIndex:'updated'
			,sortable:true
			,renderer: data.renderer.updated
//			,tooltip: _('search.group.column.header.updated')
	}]);

	form.resultsPanel = {
		xtype:'grid'
		,id:'search-results-'+modName
		//,title:'Results'
		,border:false
		,autoHeight:true
		,width:Search.settings.gridWidth
		,autoExpandColumn:'description'
		,stateful:true
		,frame:false
		,stripeRows:true
		,viewConfig: {
			forceFit:true
			,enableRowBody:true
			,showPreview:true
			// Remove the blank space on right of grid (reserved for scrollbar)
			,scrollOffset:0
		}
		,columnsText:_('search.columns.menu.columns')
		,sortAscText:_('search.columns.menu.sort_ascending')
		,sortDescText:_('search.columns.menu.sort_descending')
		,store: data.store.results
		,sm: new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn})
		,cm: form.columnModel
		,loadMask: false
		,plugins: form.rowExpander
		,bbar: new Ext.PagingToolbar({
			id: 'search-pager-'+modName
			,plugins:new Ext.ux.Andrie.pPageSize({
				 variations: [10, 25, 50]
				,beforeText: _('search.pagination.pagesize.before')
				,afterText: _('search.pagination.pagesize.after')
				,addBefore: _('search.pagination.pagesize.addbefore')
				,addAfter: _('search.pagination.pagesize.addafter')
			})
			,pageSize: 25
			,store: data.store.results
			,displayInfo: true
			,displayMsg: _('search.pagination.displaying.'+modName)
			,emptyMsg: _('search.find.no.results')
			,beforePageText: _('search.pagination.beforepage')
			,afterPageText: _('search.pagination.afterpage')
			,firstText: _('search.pagination.first')
			,prevText: _('search.pagination.prev')
			,nextText: _('search.pagination.next')
			,lastText: _('search.pagination.last')
			,refreshText: _('search.pagination.refresh')
		})
	};

	form.mainPanel = {
		xtype:'panel'
		,id:'search-panel-'+modName
		,autoHeight:true
		,items:[
			form.filterPanel
			,form.resultsPanel
		]
	};

	form.doSearch = function(){
		Search.util.doSearch(modName);
	};

	// Adjust title with count
	Search.util.registerTabTitleListener(modName);
};

Ext.onReady(function(){
	form.init();
});


// TODO:  Register this tab somehow with the main form

})();
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'member';

Ext.ns('Curriki.module.search.data.'+modName);

var data = Curriki.module.search.data.member;

data.init = function(){
	console.log('data.'+modName+': init');

	// Set up filters
	data.filter = {};
	var f = data.filter; // Alias

	f.data = {};

	f.data.subject =  {
		mapping: Curriki.data.fw_item.fwMap['FW_masterFramework.WebHome']
		,list: []
		,data: [
			['', _('XWiki.XWikiUsers_topics_FW_masterFramework.WebHome.UNSPECIFIED')]
		]
	};
	f.data.subject.mapping.each(function(value){
		f.data.subject.list.push(value.id);
	});
	f.data.subject.list.each(function(value){
		f.data.subject.data.push([
			value
			,_('XWiki.XWikiUsers_topics_'+value)
		]);
	});

	f.data.subsubject =  {
		mapping: Curriki.data.fw_item.fwMap
		,data: [
		]
	};
	f.data.subject.mapping.each(function(parentItem){
		f.data.subsubject.data.push([
			parentItem.id
			,_('XWiki.XWikiUsers_topics_'+parentItem.id+'.UNSPECIFIED')
			,parentItem.id
		]);
		f.data.subsubject.mapping[parentItem.id].each(function(subject){
			f.data.subsubject.data.push([
				subject.id
				,_('XWiki.XWikiUsers_topics_'+subject.id)
				,parentItem.id
			]);
		});
	});

	f.data.country =  {
		list: 'AD|AE|AF|AG|AI|AL|AM|AN|AO|AQ|AR|AS|AT|AU|AW|AX|AZ|BA|BB|BD|BE|BF|BG|BH|BI|BJ|BM|BN|BO|BR|BS|BT|BV|BW|BY|BZ|CA|CC|CD|CF|CG|CH|CI|CK|CL|CM|CN|CO|CR|CU|CV|CX|CY|CZ|DE|DJ|DK|DM|DO|DZ|EC|EE|EG|EH|ER|ES|ET|FI|FJ|FK|FM|FO|FR|GA|GB|GD|GE|GF|GG|GH|GI|GL|GM|GN|GP|GQ|GR|GS|GT|GU|GW|GY|HK|HM|HN|HR|HT|HU|ID|IE|IL|IM|IN|IO|IQ|IR|IS|IT|JE|JM|JO|JP|KE|KG|KH|KI|KM|KN|KP|KR|KW|KY|KZ|LA|LB|LC|LI|LK|LR|LS|LT|LU|LV|LY|MA|MC|MD|ME|MG|MH|MK|ML|MM|MN|MO|MP|MQ|MR|MS|MT|MU|MV|MW|MX|MY|MZ|NA|NC|NE|NF|NG|NI|NL|NO|NP|NR|NU|NZ|OM|PA|PE|PF|PG|PH|PK|PL|PM|PN|PR|PS|PT|PW|PY|QA|RE|RO|RS|RU|RW|SA|SB|SC|SD|SE|SG|SH|SI|SJ|SK|SL|SM|SN|SO|SR|ST|SV|SY|SZ|TC|TD|TF|TG|TH|TJ|TK|TL|TM|TN|TO|TR|TT|TV|TW|TZ|UA|UG|UM|US|UY|UZ|VA|VC|VE|VG|VI|VN|VU|WF|WS|YE|YT|ZA|ZM|ZW'.split('|')
		,data: [
			['', _('XWiki.XWikiUsers_country_UNSPECIFIED')]
		]
	};
	f.data.country.list.each(function(value){
		f.data.country.data.push([
			value
			,_('XWiki.XWikiUsers_country_'+value)
		]);
	});

	f.data.member_type =  {
		list: ['student', 'teacher', 'parent','professional','administration','nonprofit','nonprofit_education','corporation']
		,data: [
			['', _('XWiki.XWikiUsers_member_type_UNSPECIFIED')]
		]
	};
	f.data.member_type.list.each(function(value){
		f.data.member_type.data.push([
			value
			,_('XWiki.XWikiUsers_member_type_'+value)
		]);
	});

	f.store = {
		subject: new Ext.data.SimpleStore({
			fields: ['id', 'subject']
			,data: f.data.subject.data
			,id: 0
		})

		,subsubject: new Ext.data.SimpleStore({
			fields: ['id', 'subject', 'parentItem']
			,data: f.data.subsubject.data
			,id: 0
		})

		,member_type: new Ext.data.SimpleStore({
			fields: ['id', 'member_type']
			,data: f.data.member_type.data
			,id: 0
		})

		,country: new Ext.data.SimpleStore({
			fields: ['id', 'country']
			,data: f.data.country.data
			,id: 0
		})
	};



	// Set up data store
	data.store = {};

	data.store.record = new Ext.data.Record.create([
		{ name: 'name1' }
		,{ name: 'name2' }
		,{ name: 'url' }
		,{ name: 'bio' }
		,{ name: 'picture' }
		,{ name: 'contributions' }
	]);

	data.store.results = new Ext.data.Store({
		storeId: 'search-store-'+modName
		,proxy: new Ext.data.HttpProxy({
            url: document.location.pathname.endsWith("Old") ?
                '/xwiki/bin/view/Search/Members' : '/currikiExtjs'

            ,method:'GET'
		})
		,baseParams: { xpage: "plain", '_dc':(new Date().getTime()) }

		,reader: new Ext.data.JsonReader({
			root: 'rows'
			,totalProperty: 'resultCount'
			,id: 'page'
		}, data.store.record)

		// turn on remote sorting
		,remoteSort: true
	});
    if(Curriki.isISO8601DateParsing() ) data.store.results.baseParams.dateFormat="ISO8601";
	data.store.results.setDefaultSort('name1', 'asc');



	// Set up renderers
	data.renderer = {
		name1: function(value, metadata, record, rowIndex, colIndex, store){
			return String.format('<a href="{1}">{0}</a>', value, record.data.url);
		}

		,name2: function(value, metadata, record, rowIndex, colIndex, store){
			return String.format('<a href="{1}">{0}</a>', value, record.data.url);
		}

		,picture: function(value, metadata, record, rowIndex, colIndex, store){
			//TODO: Remove specialized style
			return String.format('<a href="{2}"><img src="{0}" alt="{1}" class="member-picture" style="width:88px" /></a>', value, _('search.member.column.picture.alt.text'), record.data.url);
		}

		,contributions: function(value, metadata, record, rowIndex, colIndex, store){
			return String.format('{0}', value);
		}

		,bio: function(value, metadata, record, rowIndex, colIndex, store){
			var desc = Ext.util.Format.htmlDecode(value);
			desc = Ext.util.Format.stripScripts(value);
			desc = Ext.util.Format.stripTags(desc);
			desc = Ext.util.Format.ellipsis(desc, 128);
			desc = Ext.util.Format.htmlEncode(desc);
			desc = Ext.util.Format.trim(desc);
			return String.format('{0}', desc);
		}
	};
};

Ext.onReady(function(){
	data.init();
});
})();
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'member';
Ext.ns('Curriki.module.search.form.'+modName);

var Search = Curriki.module.search;

var form = Search.form[modName];
var data = Search.data[modName];

form.init = function(){
	console.log('form.'+modName+': init');

	var comboWidth = 140;
	var comboListWidth = 250;

	form.termPanel = Search.util.createTermPanel(modName, form);
//	form.helpPanel = Search.util.createHelpPanel(modName, form);

	form.filterPanel = {
		xtype:'form'
		,labelAlign:'left'
		,id:'search-filterPanel-'+modName
		,formId:'search-filterForm-'+modName
		,border:false
		,items:[
			form.termPanel
//			,form.helpPanel
			,{
				xtype:'fieldset'
				,title:_('search.advanced.search.button')
				,id:'search-advanced-'+modName
				,autoHeight:true
				,collapsible:true
				,collapsed:true
				,animCollapse:false
				,border:true
				,stateful:true
				,stateEvents:['expand','collapse']
				,listeners:{
					'statesave':{
						fn:Search.util.fieldsetPanelSave
					}
					,'staterestore':{
						fn:Search.util.fieldsetPanelRestore
					}
					,'expand':{
						fn:function(panel){
							// CURRIKI-2989
							//  - Force a refresh of the grid view, as this
							//    seems to make the advanced search fieldset
							//    visible in IE7
							Ext.getCmp('search-results-'+modName).getView().refresh();

							Ext.select('.x-form-field-wrap', false, 'search-advanced-'+modName).setWidth(comboWidth);

							// CURRIKI-2873
							// - Force a repaint of the fieldset
							Ext.getCmp('search-termPanel-'+modName).el.repaint();
						}
					}
					,'collapse':{
						fn:function(panel){
							Ext.getCmp('search-results-'+modName).getView().refresh();
							Ext.getCmp('search-termPanel-'+modName).el.repaint();
						}
					}
				}
				,items:[{
					layout:'column'
					,border:false
					,defaults:{
						border:false
						,hideLabel:true
					}
					,items:[{
						columnWidth:0.33
						,layout:'form'
						,defaults:{
							hideLabel:true
						}
						,items:[{
							xtype:'combo'
							,id:'combo-subject-'+modName
							,fieldLabel:'Subject'
							,hiddenName:'subjectparent'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.subject
							,displayField:'subject'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('XWiki.XWikiUsers_topics_FW_masterFramework.WebHome.UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
							,listeners:{
								select:{
									fn:function(combo, value){
										var subSubject = Ext.getCmp('combo-subsubject-'+modName);
										if (combo.getValue() === '') {
											subSubject.clearValue();
											subSubject.hide();
										} else {
											subSubject.show();
											subSubject.clearValue();
											subSubject.store.filter('parentItem', combo.getValue());
											subSubject.setValue(combo.getValue());
										}
									}
								}
							}
						},{
							xtype:'combo'
							,fieldLabel:'Sub Subject'
							,id:'combo-subsubject-'+modName
							,hiddenName:'subject'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.subsubject
							,displayField:'subject'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
	//						,emptyText:'Select a Sub Subject...'
							,selectOnFocus:true
							,forceSelection:true
							,lastQuery:''
							,hidden:true
							,hideMode:'visibility'
						}]
					},{
						columnWidth:0.33
						,layout:'form'
						,defaults:{
							hideLabel:true
						}
						,items:[{
							xtype:'combo'
							,id:'combo-member_type-'+modName
							,fieldLabel:'Member Type'
							,mode:'local'
							,width:comboWidth
							,listWidth:comboListWidth
							,store:data.filter.store.member_type
							,hiddenName:'member_type'
							,displayField:'member_type'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('XWiki.XWikiUsers_member_type_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
						}]
					},{
						columnWidth:0.34
						,layout:'form'
						,defaults:{
							hideLabel:true
						}
						,items:[{
							xtype:'combo'
							,id:'combo-country-'+modName
							,fieldLabel:'Country'
							,hiddenName:'country'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.country
							,displayField:'country'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('XWiki.XWikiUsers_country_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
						}]
					}]
				}]
			}
		]
	}

	form.columnModelList = [{
			id: 'picture'
			,header: _('search.member.column.header.picture')
			,width: 116
			,dataIndex: 'picture'
			,sortable:false
			,resizable:false
			,menuDisabled:true
			,renderer: data.renderer.picture
//			,tooltip:_('search.member.column.header.picture')
		},{
			id: 'name1'
			,header: _('search.member.column.header.name1')
			,width: 120
			,dataIndex: 'name1'
			,sortable:true
			,hideable:false
			,renderer: data.renderer.name1
//			,tooltip:_('search.member.column.header.name1')
		},{
			id: 'name2'
			,width: 120
			,header: _('search.member.column.header.name2')
			,dataIndex:'name2'
			,sortable:true
			,hideable:false
			,renderer: data.renderer.name2
//			,tooltip: _('search.member.column.header.name2')
		},{
			id: 'bio'
			,width: 120
			,header: _('search.member.column.header.bio')
			,dataIndex:'bio'
			,sortable:false
			,renderer: data.renderer.bio
//			,tooltip: _('search.member.column.header.bio')
		},{
			id: 'contributions'
			,width: 120
			,header: _('search.member.column.header.contributions')
			,dataIndex:'contributions'
			,sortable:false
			,renderer: data.renderer.contributions
//			,tooltip: _('search.member.column.header.contributions')
	}];

	form.columnModel = new Ext.grid.ColumnModel(form.columnModelList);

	form.resultsPanel = {
		xtype:'grid'
		,id:'search-results-'+modName
		//,title:'Results'
		,border:false
		,autoHeight:true
		,width:Search.settings.gridWidth
		,autoExpandColumn:'bio'
		,stateful:true
		,frame:false
		,stripeRows:true
		,viewConfig: {
			forceFit:true
			,enableRowBody:true
			,showPreview:true
			// Remove the blank space on right of grid (reserved for scrollbar)
			,scrollOffset:0
		}
		,columnsText:_('search.columns.menu.columns')
		,sortAscText:_('search.columns.menu.sort_ascending')
		,sortDescText:_('search.columns.menu.sort_descending')
		,store: data.store.results
		,sm: new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn})
		,cm: form.columnModel
		,loadMask: false
		,plugins: form.rowExpander
		,bbar: new Ext.PagingToolbar({
			id: 'search-pager-'+modName
			,plugins:new Ext.ux.Andrie.pPageSize({
				 variations: [10, 25, 50]
				,beforeText: _('search.pagination.pagesize.before')
				,afterText: _('search.pagination.pagesize.after')
				,addBefore: _('search.pagination.pagesize.addbefore')
				,addAfter: _('search.pagination.pagesize.addafter')
			})
			,pageSize: 25
			,store: data.store.results
			,displayInfo: true
			,displayMsg: _('search.pagination.displaying.'+modName)
			,emptyMsg: _('search.find.no.results')
			,beforePageText: _('search.pagination.beforepage')
			,afterPageText: _('search.pagination.afterpage')
			,firstText: _('search.pagination.first')
			,prevText: _('search.pagination.prev')
			,nextText: _('search.pagination.next')
			,lastText: _('search.pagination.last')
			,refreshText: _('search.pagination.refresh')
		})
	};

	form.mainPanel = {
		xtype:'panel'
		,id:'search-panel-'+modName
		,autoHeight:true
		,items:[
			form.filterPanel
			,form.resultsPanel
		]
	};

	form.doSearch = function(){
		Search.util.doSearch(modName);
	};

	// Adjust title with count
	Search.util.registerTabTitleListener(modName);
};

Ext.onReady(function(){
	form.init();
});


// TODO:  Register this tab somehow with the main form

})();
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'blog';

Ext.ns('Curriki.module.search.data.'+modName);

var data = Curriki.module.search.data.blog;

data.init = function(){
	console.log('data.'+modName+': init');

	// No filters for blog search

	// Set up data store
	data.store = {};

	data.store.record = new Ext.data.Record.create([
		{ name: 'name' }
		,{ name: 'title' }
		,{ name: 'text' }
		,{ name: 'comments' }
		,{ name: 'updated' }
		,{ name: 'memberUrl' }
		,{ name: 'blogUrl' }
	]);

	data.store.results = new Ext.data.Store({
		storeId: 'search-store-'+modName
		,proxy: new Ext.data.HttpProxy({
            url: document.location.pathname.endsWith("Old") ?
                '/xwiki/bin/view/Search/Blogs' : '/currikiExtjs'
            ,method:'GET'
		})
		,baseParams: { xpage: "plain", '_dc':(new Date().getTime()) }

		,reader: new Ext.data.JsonReader({
			root: 'rows'
			,totalProperty: 'resultCount'
			,id: 'page'
		}, data.store.record)

		// turn on remote sorting
		,remoteSort: true
	});
    if(Curriki.isISO8601DateParsing() ) data.store.results.baseParams.dateFormat="ISO8601";
	data.store.results.setDefaultSort('updated', 'desc');



	// Set up renderers
	data.renderer = {
		name: function(value, metadata, record, rowIndex, colIndex, store){
			return String.format('<a href="{1}">{0}</a>', value, record.data.memberUrl);
		}

		,text: function(value, metadata, record, rowIndex, colIndex, store){
			var desc = Ext.util.Format.htmlDecode(value); // Reverse conversion
			desc = Ext.util.Format.stripScripts(value);
			desc = Ext.util.Format.stripTags(desc);
			desc = Ext.util.Format.trim(desc);
			desc = Ext.util.Format.ellipsis(desc, 128);
			//desc = Ext.util.Format.htmlEncode(desc);
			return String.format('<a href="{2}" class="search-blog-title">{1}</a><br /><br />{0}', desc, record.data.title, record.data.blogUrl);
		}

		,comments: function(value, metadata, record, rowIndex, colIndex, store){
			return String.format('{0}', value);
		}

		,updated: function(value, metadata, record, rowIndex, colIndex, store){
            if(typeof("value")!="string") return "";
            var dt = Ext.util.Format.date(value, 'M-d-Y');
            if(typeof(dt)!="string") return "";
			return String.format('{0}', dt);
		}
	};
};

Ext.onReady(function(){
	data.init();
});
})();
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'blog';

Ext.ns('Curriki.module.search.form.'+modName);

var Search = Curriki.module.search;

var form = Search.form[modName];
var data = Search.data[modName];

form.init = function(){
	console.log('form.'+modName+': init');

	form.termPanel = Search.util.createTermPanel(modName, form);

	form.filterPanel = {
		xtype:'form'
		,labelAlign:'left'
		,id:'search-filterPanel-'+modName
		,formId:'search-filterForm-'+modName
		,border:false
		,items:[
			form.termPanel
		]
	};

	form.columnModel = new Ext.grid.ColumnModel([
		{
			id: 'name'
			,header:_('search.blog.column.header.name')
			,width: 160
			,dataIndex: 'name'
			,sortable:true
			,renderer: data.renderer.name
//			,tooltip:_('search.blog.column.header.name')
		},{
			id: 'text'
			,header: _('search.blog.column.header.text')
			,width: 260
			,dataIndex: 'text'
			,sortable:false
			,renderer: data.renderer.text
//			,tooltip:_('search.blog.column.header.text')
		},{
			id: 'comments'
			,header: _('search.blog.column.header.comments')
			,width: 80
			,dataIndex: 'comments'
			,sortable:false
			,renderer: data.renderer.comments
//			,tooltip:_('search.blog.column.header.comments')
		},{
			id: 'updated'
			,width: 96
			,header: _('search.blog.column.header.updated')
			,dataIndex:'updated'
			,sortable:true
			,renderer: data.renderer.updated
//			,tooltip: _('search.blog.column.header.updated')
	}]);

	form.resultsPanel = {
		xtype:'grid'
		,id:'search-results-'+modName
		//,title:'Results'
		,border:false
		,autoHeight:true
		,width:Search.settings.gridWidth
		,autoExpandColumn:'text'
		,stateful:true
		,frame:false
		,stripeRows:true
		,viewConfig: {
			forceFit:true
			,enableRowBody:true
			,showPreview:true
			// Remove the blank space on right of grid (reserved for scrollbar)
			,scrollOffset:0
		}
		,columnsText:_('search.columns.menu.columns')
		,sortAscText:_('search.columns.menu.sort_ascending')
		,sortDescText:_('search.columns.menu.sort_descending')
		,store: data.store.results
		,sm: new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn})
		,cm: form.columnModel
		,loadMask: false
		,bbar: new Ext.PagingToolbar({
			id: 'search-pager-'+modName
			,plugins:new Ext.ux.Andrie.pPageSize({
				 variations: [10, 25, 50]
				,beforeText: _('search.pagination.pagesize.before')
				,afterText: _('search.pagination.pagesize.after')
				,addBefore: _('search.pagination.pagesize.addbefore')
				,addAfter: _('search.pagination.pagesize.addafter')
			})
			,pageSize: 25
			,store: data.store.results
			,displayInfo: true
			,displayMsg: _('search.pagination.displaying.'+modName)
			,emptyMsg: _('search.find.no.results')
			,beforePageText: _('search.pagination.beforepage')
			,afterPageText: _('search.pagination.afterpage')
			,firstText: _('search.pagination.first')
			,prevText: _('search.pagination.prev')
			,nextText: _('search.pagination.next')
			,lastText: _('search.pagination.last')
			,refreshText: _('search.pagination.refresh')
		})
	};

	form.mainPanel = {
		xtype:'panel'
		,id:'search-panel-'+modName
		,autoHeight:true
		,items:[
			form.filterPanel
			,form.resultsPanel
		]
	};

	form.doSearch = function(){
		Search.util.doSearch(modName);
	};

	// Adjust title with count
	Search.util.registerTabTitleListener(modName);
};

Ext.onReady(function(){
	form.init();
});


// TODO:  Register this tab somehow with the main form

})();
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'curriki';

Ext.ns('Curriki.module.search.data.'+modName);

var data = Curriki.module.search.data.curriki;

data.init = function(){
	console.log('data.'+modName+': init');

	// No filters for curriki search

	// Set up data store
	data.store = {};

	data.store.record = new Ext.data.Record.create([
		{ name: 'name' }
//		,{ name: 'text' }
		,{ name: 'updated' }
		,{ name: 'url' }
        ,{ name: 'score'}
	]);

	data.store.results = new Ext.data.Store({
		storeId: 'search-store-'+modName
		,proxy: new Ext.data.HttpProxy({
            url: document.location.pathname.endsWith("Old") ?
                '/xwiki/bin/view/Search/Curriki' : '/currikiExtjs'
            ,method:'GET'
		})
		,baseParams: { xpage: "plain", '_dc':(new Date().getTime()) }

		,reader: new Ext.data.JsonReader({
			root: 'rows'
			,totalProperty: 'resultCount'
//			,id: 'page'
		}, data.store.record)

		// turn on remote sorting
		,remoteSort: true
	});
    if(Curriki.userinfo.userGroups) data.store.results.baseParams.groupsId= Curriki.userinfo.userGroups;
    if(Curriki.userinfo.userName) data.store.results.baseParams.userId = Curriki.userinfo.userName;
    if(Curriki.userinfo.isAdmin) data.store.results.baseParams.isAdmin = true;
    if(Curriki.isISO8601DateParsing() ) data.store.results.baseParams.dateFormat="ISO8601";
    data.store.results.setDefaultSort('score', 'desc');



	// Set up renderers
	data.renderer = {
		name: function(value, metadata, record, rowIndex, colIndex, store){
			return String.format('<a href="{1}">{0}</a>', value, record.data.url);
		}

/*
		,text: function(value, metadata, record, rowIndex, colIndex, store){
			var desc = Ext.util.Format.stripScripts(value);
			desc = Ext.util.Format.stripTags(desc);
			desc = Ext.util.Format.ellipsis(desc, 128);
			desc = Ext.util.Format.htmlEncode(desc);
			return String.format('{0}', desc);
		}
*/

		,updated: function(value, metadata, record, rowIndex, colIndex, store){
            if(typeof("value")!="string") return "";
            var dt = Ext.util.Format.date(value, 'M-d-Y');
            if(typeof(dt)!="string") return "";
			return String.format('{0}', dt);
		}, score: function(value, metadata, record, rowIndex, colIndex, store){
            if(typeof(value)!="number") value=0;
            return value;
        }

    };
};

Ext.onReady(function(){
	data.init();
});
})();
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'curriki';

Ext.ns('Curriki.module.search.form.'+modName);

var Search = Curriki.module.search;

var form = Search.form[modName];
var data = Search.data[modName];

form.init = function(){
	console.log('form.'+modName+': init');

	form.termPanel = Search.util.createTermPanel(modName, form);

	form.filterPanel = {
		xtype:'form'
		,labelAlign:'left'
		,id:'search-filterPanel-'+modName
		,formId:'search-filterForm-'+modName
		,border:false
		,items:[
			form.termPanel
		]
	};

	form.columnModel = new Ext.grid.ColumnModel([
		{
			id: 'name'
			,header:_('search.curriki.column.header.name')
			,width: 500
			,dataIndex: 'name'
			,sortable:true
			,renderer: data.renderer.name
//			,tooltip:_('search.curriki.column.header.name')
		},{
			id: 'updated'
			,width: 96
			,header: _('search.curriki.column.header.updated')
			,dataIndex:'updated'
			,sortable:true
			,renderer: data.renderer.updated
//			,tooltip: _('search.curriki.column.header.updated')
	}]);

	form.resultsPanel = {
		xtype:'grid'
		,id:'search-results-'+modName
		//,title:'Results'
		,border:false
		,autoHeight:true
		,width:Search.settings.gridWidth
		,autoExpandColumn:'name'
		,stateful:true
		,frame:false
		,stripeRows:true
		,viewConfig: {
			forceFit:true
			,enableRowBody:true
			,showPreview:true
			// Remove the blank space on right of grid (reserved for scrollbar)
			,scrollOffset:0
		}
		,columnsText:_('search.columns.menu.columns')
		,sortAscText:_('search.columns.menu.sort_ascending')
		,sortDescText:_('search.columns.menu.sort_descending')
		,store: data.store.results
		,sm: new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn})
		,cm: form.columnModel
		,loadMask: false
		,bbar: new Ext.PagingToolbar({
			id: 'search-pager-'+modName
			,plugins:new Ext.ux.Andrie.pPageSize({
				 variations: [10, 25, 50]
				,beforeText: _('search.pagination.pagesize.before')
				,afterText: _('search.pagination.pagesize.after')
				,addBefore: _('search.pagination.pagesize.addbefore')
				,addAfter: _('search.pagination.pagesize.addafter')
			})
			,pageSize: 25
			,store: data.store.results
			,displayInfo: true
			,displayMsg: _('search.pagination.displaying.'+modName)
			,emptyMsg: _('search.find.no.results')
			,beforePageText: _('search.pagination.beforepage')
			,afterPageText: _('search.pagination.afterpage')
			,firstText: _('search.pagination.first')
			,prevText: _('search.pagination.prev')
			,nextText: _('search.pagination.next')
			,lastText: _('search.pagination.last')
			,refreshText: _('search.pagination.refresh')
		})
	};

	form.mainPanel = {
		xtype:'panel'
		,id:'search-panel-'+modName
		,autoHeight:true
		,items:[
			form.filterPanel
			,form.resultsPanel
		]
	};

	form.doSearch = function(){
		Search.util.doSearch(modName);
	};

	// Adjust title with count
	Search.util.registerTabTitleListener(modName);
};

Ext.onReady(function(){
	form.init();
});


// TODO:  Register this tab somehow with the main form

})();
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */
/* Javascript Shims */

if (!Array.prototype.indexOf) {
    Array.prototype.indexOf = function (searchElement /*, fromIndex */ ) {
        'use strict';
        if (this == null) {
            throw new TypeError();
        }
        var n, k, t = Object(this),
            len = t.length >>> 0;

        if (len === 0) {
            return -1;
        }
        n = 0;
        if (arguments.length > 1) {
            n = Number(arguments[1]);
            if (n != n) { // shortcut for verifying if it's NaN
                n = 0;
            } else if (n != 0 && n != Infinity && n != -Infinity) {
                n = (n > 0 || -1) * Math.floor(Math.abs(n));
            }
        }
        if (n >= len) {
            return -1;
        }
        for (k = n >= 0 ? n : Math.max(len - Math.abs(n), 0); k < len; k++) {
            if (k in t && t[k] === searchElement) {
                return k;
            }
        }
        return -1;
    };
}

/* /Javascript Shims */

(function () {
    Ext.ns('Curriki.module.search.form');

    var Search = Curriki.module.search;
    var forms = Search.form;


    Search.init = function () {
        console.log('search: init');
        if (!Ext.isEmpty(Search.initialized)) {
            alert("Search module already initialized");
            return;
        }

        if (Ext.isEmpty(Search.tabList)) {
            Search.tabList = ['resource',  'group', 'member', 'curriki']; // 'outerResource',
        }

        var comboWidth = 140;

        //Returns current state of the UI

        Search.currentStateUrl=null;

        Search.getState = function () {

            var filterValues = {};
            if (Ext.getCmp('search-termPanel')
                && Ext.getCmp('search-termPanel').getForm) {
                filterValues['all'] = Ext.getCmp('search-termPanel').getForm().getValues(false);
            }

            var pagerValues = {};

            var panelSettings = {};

            Ext.each(
                Search.tabList
                , function (tab) {
                    var module = forms[tab];
                    if (!Ext.isEmpty(module) && !Ext.isEmpty(module.doSearch)) {
                        // Get current values
                        var filterPanel = Ext.getCmp('search-filterPanel-' + tab);
                        if (!Ext.isEmpty(filterPanel)) {
                            var filterForm = filterPanel.getForm();
                            if (!Ext.isEmpty(filterForm)) {
                                filterValues[tab] = filterForm.getValues(false);
                                if ("undefined" !== typeof filterValues[tab]["terms"] && filterValues[tab]["terms"] === _('search.text.entry.label')) {
                                    delete(filterValues[tab]["terms"]);
                                }
                                if ("undefined" !== typeof filterValues[tab]["other"] && filterValues[tab]["other"] === '') {
                                    delete(filterValues[tab]["other"]);
                                }
                                // CURRIKI-5404: almost there: store sort in URL
                                if (Ext.StoreMgr.lookup('search-store-' + tab).sortInfo) {
                                    filterValues[tab]['sort'] = {};
                                    filterValues[tab]['sort'].field = Ext.StoreMgr.lookup('search-store-' + tab).sortInfo.field;
                                    filterValues[tab]['sort'].dir = Ext.StoreMgr.lookup('search-store-' + tab).sortInfo.direction;
                                }

                            }
                        }

                        var advancedSearch = Ext.getCmp('search-advanced-' + tab);
                        if (!Ext.isEmpty(advancedSearch)) {
                            if (!advancedSearch.collapsed) {
                                panelSettings[tab] = {a: true}; // Advanced open
                            }
                        }

                        var pagerPanel = Ext.getCmp('search-pager-' + tab);
                        if (!Ext.isEmpty(pagerPanel)) {
                            var pagerInfo = {};
                            pagerInfo.c = pagerPanel.cursor;
                            pagerInfo.s = pagerPanel.pageSize;
                            pagerValues[tab] = pagerInfo;
                        }
                    }
                }
            );

            var stateObject = {};
            //todo: Implement correct search tab detection
            stateObject['s'] = 'all';
//            stateObject['s'] = Ext.isEmpty(searchTab) ? 'all' : searchTab;
            stateObject['f'] = filterValues;
            stateObject['p'] = pagerValues;
            if (Ext.getCmp('search-tabPanel').getActiveTab) {
                stateObject['t'] = Ext.getCmp('search-tabPanel').getActiveTab().id;
            }
            stateObject['a'] = panelSettings;

            return stateObject;

        };

        Search.saveState = function (stateUrl) {
            var stateObject = Search.getState();
            //@@DEBUG
            if ((stateUrl == null) || (stateUrl == document.location.pathname)) return;


            console.info("pushState:" + stateUrl);
            Search.currentStateUrl = stateUrl;
            History.pushState(stateObject, stateUrl, stateUrl);

            if (console) console.info('Added Token To History');

        };


        Search.doSearch = function (searchTab, resetPage /* default false */, onlyHistory /* default false */) {
            var t = $('search-termPanel-' + searchTab + '-terms').getValue();
            if (t == _('search.text.entry.label')) t = "";
            if (document.savedTitle && t != "") {
                document.title = document.savedTitle;
            } else {
                if (typeof(document.savedTitle) == "undefined") document.savedTitle = document.title;
                document.title = _("search.window.title." + searchTab, [t]);
            }
            try {
                var box = $('curriki-searchbox');
                if (typeof(box) == "object" && typeof(box.style) == "object") {
                    box.style.color = 'lightgrey';
                    box.value = t;
                }
            } catch (e) {
                console.log('search: curriki-searchbox not found. (Ok in embedded mode)');
                console.log('EmbeddedMode: ' + Curriki.module.search.util.isInEmbeddedMode());
            }


            Curriki.numSearches = 0;


            Ext.each(
                Search.tabList
                , function (tab) {
                    var module = forms[tab];
                    if (!Ext.isEmpty(module) && !Ext.isEmpty(module.doSearch)) {

                        // Do the search
                        if ((("undefined" === typeof onlyHistory) || (onlyHistory = false)) && (Ext.isEmpty(searchTab) || searchTab === tab)) {
                            // TODO: MSIE misery here
                            // if(Search['runningSearch' + tab]) throw NO_CONCURRENT_SEARCH_ERRMSG;
                            // Search['runningSearch' + tab] = true;
                            if (Curriki.numSearches > 10) return;

                            var stateObject = Search.getState();
                            var pagerValues = stateObject['p'];

                            Curriki.numSearches++;
                            Search.util.doSearch(tab, (("undefined" !== typeof pagerValues[tab]) ? pagerValues[tab].c : 0));
                        }
                    }
                }
            );
        };

        Search.tabPanel = {
            xtype: (Search.tabList.size() > 1 ? 'tab' : '') + 'panel', id: 'search-tabPanel', activeTab: 0, deferredRender: false, autoHeight: true, layoutOnTabChange: true, frame: false, border: false, plain: true, defaults: {
                autoScroll: false, border: false
            }, listeners: {
                tabchange: function (tabPanel, tab) {
                    // Log changing to view a tab
                    var tabId = tab.id.replace(/(^search-|-tab$)/g, '');
                    Curriki.logView('/features/search/' + tabId);

                    var advancedPanel = Ext.getCmp('search-advanced-' + tabId);
                    if (!Ext.isEmpty(advancedPanel)) {
                        if (!advancedPanel.collapsed) {
                            Ext.select('.x-form-field-wrap', false, 'search-advanced-' + tabId).setWidth(comboWidth);
                        }
                    }
                }
            }, items: [] // Filled in based on tabs available
        };
        Ext.each(
            Search.tabList
            , function (tab) {
                var panel = {
                    title: _('search.' + tab + '.tab.title'), id: 'search-' + tab + '-tab', cls: 'search-' + tab, autoHeight: true
                };
                var module = forms[tab];
                if (!Ext.isEmpty(module) && !Ext.isEmpty(module.mainPanel)) {
                    panel.items = [module.mainPanel];
                    Search.tabPanel.items.push(panel);
                } else {
                    console.log("Dropping " + tab + " (module Curriki.module.search.form[" + tab + "] is empty).")
                }
            }
        );

        Search.mainPanel = {
            el: 'search-div'
            //,title:_('search.top_titlebar')
            , border: false, height: '600px', defaults: {border: false}, cls: 'search-module', items: [
                Search.tabPanel
            ]
        };


        Ext.ns('Curriki.module.search.history');
        var HistoryConsumer = Search.history;
        // Handle this change event in order to restore the UI
        // to the appropriate history state
        HistoryConsumer.historyChange = function (stateObject) {
            if (stateObject) {
                //todo: Check if duplicate token really exist
                if (console) console.info('Updated from History');
                HistoryConsumer.updateFromHistory(stateObject);
            }
        };

        HistoryConsumer.updateFromHistory = function (stateObject) {
            var values = stateObject.data;

            console.log('Got History: ' + stateObject.url, {values: values});

            if (!Ext.isEmpty(values)) {
                var filterValues = values['f'];
                if (!Ext.isEmpty(filterValues) && filterValues['all'] && Ext.getCmp('search-termPanel') && Ext.getCmp('search-termPanel').getForm) {
                    Ext.getCmp('search-termPanel').getForm().setValues(filterValues['all']);
                }

                var pagerValues = values['p'];

                var panelSettings = values['a'];

                if (values['t']) {
                    if (Ext.getCmp('search-tabPanel').setActiveTab) {
                        Ext.getCmp('search-tabPanel').setActiveTab(values['t']);
                    }
                }

                Ext.each(
                    Search.tabList
                    , function (tab) {
                        //##	console.log('Updating '+tab);
                        var module = Search.form[tab];
                        // TODO: MSIE misery here
                        // if(!Ext.isEmpty(Search) && Search['runningSearch' + tab]) throw NO_CONCURRENT_SEARCH_ERRMSG;
                        if (!Ext.isEmpty(module) && !Ext.isEmpty(module.doSearch) && !Ext.isEmpty(filterValues) && !Ext.isEmpty(filterValues[tab])) {
                            var filterPanel = Ext.getCmp('search-filterPanel-' + tab);
                            if (!Ext.isEmpty(filterPanel)) {
                                var filterForm = filterPanel.getForm();
                                if (!Ext.isEmpty(filterForm)) {
                                    try {
                                        filterForm.setValues(filterValues[tab]);

                                        // setValues does not trigger the visiblity change of the sub-lists
                                        var list = Ext.getCmp('combo-subject-' + tab);
                                        if (list) {
                                            list.fireEvent("select", list, list.getValue());
                                            if (!Ext.isEmpty(filterValues[tab].subject)) {
                                                if (Ext.getCmp('combo-subsubject-' + tab)) {
                                                    Ext.getCmp('combo-subsubject-' + tab).setValue(filterValues[tab].subject);
                                                }
                                            }
                                        }
                                        list = Ext.getCmp('combo-ictprfx-' + tab);
                                        if (list) {
                                            list.fireEvent("select", list, list.getValue());
                                            if (!Ext.isEmpty(filterValues[tab].ict)) {
                                                if (Ext.getCmp('combo-subICT-' + tab)) {
                                                    Ext.getCmp('combo-subICT-' + tab).setValue(filterValues[tab].ict);
                                                }
                                            }
                                        }
                                        // attempt at CURRIKI-5404
                                        if (filterValues[tab]['sort']) {
                                            var sortInfo = filterValues[tab]['sort'];
                                            if (sortInfo.field) Ext.StoreMgr.lookup('search-store-' + tab).sortInfo.field = sortInfo['field'];
                                            if (sortInfo.dir)   Ext.StoreMgr.lookup('search-store-' + tab).sortInfo.direction = sortInfo['dir'];
                                        }
                                    } catch (e) {
                                        console.log('ERROR Updating ' + tab, e);
                                    }
                                }
                            }

                            // Open advanced panel if specified
                            if (!Ext.isEmpty(panelSettings) && !Ext.isEmpty(panelSettings[tab]) && panelSettings[tab].a) {
                                var advancedPanel = Ext.getCmp('search-advanced-' + tab);
                                if (!Ext.isEmpty(advancedPanel)) {
                                    advancedPanel.expand(false);
                                }
                            }

                            // Set pager values
                            var pagerPanel = Ext.getCmp('search-pager-' + tab);
                            if (!Ext.isEmpty(pagerPanel) && !Ext.isEmpty(pagerValues)) {
                                if (pagerValues[tab]) {
                                    try {
                                        if (pagerValues[tab]['c']) {
                                            pagerPanel.cursor = pagerValues[tab]['c'];
                                        }
                                        if (pagerValues[tab]['s']) {
                                            if (pagerPanel.pageSize != pagerValues[tab]['s']) {
                                                pagerPanel.setPageSize(pagerValues[tab]['s']);
                                            }
                                        }
                                    } catch (e) {
                                        console.log('ERROR Updating ' + tab, e);
                                    }
                                }
                            }
                        }
                    }
                );

                //TODO: Slavko check here
                 if (values['s']) {
                 console.info('Starting search');
                 if (values['s'] === 'all') {
                 Search.doSearch(values['t'].split('-')[1]);
                 } else {
                 Search.doSearch(values['s']);
                 }
                 }
            }
        };


        HistoryConsumer.parseInitialStateOld = function (initialStateObject) {

            var extHistoryHash = Ext.History.getToken();
            if (extHistoryHash == null) return false;

            var provider = new Ext.state.Provider();
            var amendedData = provider.decodeValue(extHistoryHash);
            initialStateObject = Ext.apply(initialStateObject,amendedData,{},true);
            return true;
        };

        HistoryConsumer.parseInitialStateNew = function (initialStateObject, state) {

            function getQueryParams(qs) {
                qs = qs.split("+").join(" ");

                var params = {}, tokens,
                    re = /[?&]?([^=]+)=([^&]*)/g;

                while (tokens = re.exec(qs)) {
                    params[decodeURIComponent(tokens[1])]
                        = decodeURIComponent(tokens[2]);
                }

                return params;
            }


            var query = getQueryParams(document.location.search);
            var state = query.state;
            if (typeof state == "undefined") return false; //no state

            var elems = state.split("/");
            var additionalTokens = Search.util.logFilterList;
            var mode = "";
            var lastToken = "";
            var elem = "";


            while (typeof elem != "undefined") {
                lastToken = elem;
                elem = elems.shift();
                //noinspection FallthroughInSwitchStatementJS
                switch (elem) {
                    case "":
                    case "features":
                    case "advanced":
                    case "simple":
                        mode = "";
                        continue;
                        break;

                    case "search":
                        mode = "AwaitingForTab";
                        continue;
                        break;

                    case "sort":
                        mode = "AwaitingFSortField";
                        continue;
                        break;

                    case "start":
                        mode = "AwaitingForStart";
                        continue;
                        break;

                    case "rows":
                        mode = "AwaitingForRows";
                        continue;
                        break;


                    case "dir":
                        mode = "AwaitingFDirField";
                        continue;
                        break;


                    default:
                        if ((Search.currentTab) && (additionalTokens[Search.currentTab].indexOf(elem) >= 0)) {
                            mode = "AwaitingFValue";
                            continue;

                        }
                }
                switch (mode) {
                    case "AwaitingForTab":
                    {
                        Search.currentTab = elem;
                        initialStateObject['t'] = "search-" + elem + "-tab";
                        mode = "AwaitingForSearchString";
                        break;
                    }

                    case "AwaitingForSearchString":
                    {
                        initialStateObject['f'][Search.currentTab]['terms'] = elem;
                        break;
                    }

                    case "AwaitingFValue":
                    {
                        initialStateObject['f'][Search.currentTab][lastToken] = elem;
                        break;
                    }

                    case "AwaitingFSortField":
                    {
                        initialStateObject['f'][Search.currentTab]['sort']['field'] = elem;
                        break;
                    }

                    case "AwaitingFDirField":
                    {
                        initialStateObject['f'][Search.currentTab]['sort']['dir'] = elem;
                        break;
                    }

                    case "AwaitingForStart":
                    {
                        initialStateObject['p'][Search.currentTab]['c'] = elem;
                        initialStateObject['p'][Search.currentTab]['s'] = 25;
                        mode = "";
                    }

                    case "AwaitingForRows":
                    {
                        initialStateObject['p'][Search.currentTab]['s'] = elem;
                        mode = "";
                    }



                }

            }

            return true;

        };

        HistoryConsumer.init = function () {


            if (Ext.isEmpty(HistoryConsumer.initialized)) {

                var initialStateObject = Search.getState();
                var state="";

                if (!History.enabled) {
                    alert("your browser is too old to support history, sorry");
                    return;
                }

                History.Adapter.bind(window, 'statechange', function () {

                    var State = History.getState(true,true);
                    History.log('statechange:', State.data, State.title, State.url);
                    if (State.url.indexOf(Search.currentStateUrl)== -1) {
                      console.log("CURRENTSTATE==>  " + Search.currentStateUrl);
                      console.log("NEWSTATE==>  " + State.url);
                      HistoryConsumer.historyChange(State);
                      Search.currentStateUrl = State.url;
                    }

                });

                // ### InitState!
                HistoryConsumer.initialized = true;

                if (HistoryConsumer.parseInitialStateNew(initialStateObject, state)){
                  History.pushState(initialStateObject, "?state="+state, "?state="+state);
                } else if (HistoryConsumer.parseInitialStateOld(initialStateObject)){
                  History.pushState(initialStateObject,"?state=/","?state=/");
                }


            }
        };

        Search.initialized = true;

        console.log('search: init done');

    };


    Search.display = function () {
        Search.init();

        var s = new Ext.Panel(Search.mainPanel);
        s.render();

        if (Curriki.module.search.util.isInEmbeddedMode()) {
            Curriki.module.search.util.sendResizeMessageToEmbeddingWindow(); // Initial resizement of the embedding iframe
        }

        Search.history.init();

    };


    Search.start = function () {
        Ext.onReady(function () {
            Search.display();
        });
    };
})();
