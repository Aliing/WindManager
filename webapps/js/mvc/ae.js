/**
 *@module AE
 *@version 1.0
 *@author wanpeng
 */
(function(win){
	win.AE = win.AE || {};

	var D = AE,
		SUBREG = /{([^{}]+)}/g;
	/**
	 *@method namespace
	 *@static
	 *@param {String*} arguments 1-n namespace
	 *@return {Object} namesapce
	 */
	D.namespace = function(){
		var a = arguments,len = a.length,i,j,dl,dd,o;
		for( i = 0; i < len; i++ ){
			dl = ( '' + a[i] ).split('.');
			o = this;
			for( j = ( win[dl[0]] == o ? 1 : 0 ),dd = dl.length; j < dd; j++ ){
				o[dl[j]] = o[dl[j]] || {};
				o = o[dl[j]];
			}
		}
	};
	
	/**
	 *@method substitute
	 *@static
	 *@param str {String}
	 *@param o {Object}
	 *@return {String}
	 */
	D.substitue = function( str, o ){
		return str.replace(SUBREG,function(a,s){
			var r = o[s];
			return typeof r === 'string' || typeof r === 'number' ? r : a;
		});
	}
})(window);

/**
 *@describe AE Module
 *@static
 *@author wp
 *@requires AE
 */
(function($,D){
	D.namespace('AE.Mod');
	
	D.Mod = {
		data : {},
		add : function(name,config,option){
			var n,p;
			if(!name) return;
			n = D.namespace(name);
			
			(p = this.data[name] = {}).mod = n = D.Controller.create(config);
			p.enable = false;
			p.option = option;
			return this;
		},
		remove : function(){
			//!name ? this.data = {} : name in this.data ? delete this.data[name] : null; 
			var i,dl,modules,len,args = arguments;
			 
			modules = (args[0] && 'string' === typeof args[0]) ? args : args[0];
			
			if(!modules) this.data = {};

			for(i=0,len=modules.length;i<len;i++){
				dl = modules[i];
				if(dl in this.data) delete this.data[dl];
			}
			return this;
		},
		enable : function(){
			var args = arguments,modules,i,dl,len; 
			modules = (args[0] && 'string' === typeof args[0]) ? args : args[0];

			if(!modules) return;
			for(i=0,len=modules.length;i<len;i++){
				dl = modules[i];
				if(dl in this.data) this.data[dl].enable = true;
			}
			return this;
		},
		init : function(name){
			var i,dl,mod = name || 'div.J-mod',_self=this;
			
			$(mod).length &&
					$(mod).each(function(){
						var that = $(this),
							mod = that.attr('data-mod'),
							able = that.attr('data-switch') === 'on' ? true : false;
						( mod in _self.data ) && (_self.data[mod].enable = able,_self.data[mod].el = this);
					});
			
			for(i in this.data){
				dl = this.data[i];
				if(dl.enable) dl.instance = dl.mod.init($.extend({el:dl.el},dl.option || {}));
			}		  
			return this;
		}
	};
})(jQuery,AE);


/**
  *@module AE.Class
  *@describe MVC
  *
  */
(function(win,$){
	win.AE = win.AE || {};
	
	var slice = Array.prototype.slice,
		EXCLUDE = ['included','extended'],
		REGEVENT = /^(\w+)\s*(.*)$/,
		REGSPACE = /\s+/;

	//AE.$ = $ ? $ : null;
	/**
	  *@method arrIndexOf
	  *@private 
	  *@params arr {Array}
	  *@params val {value}
	  */
	//if('function' !== typeof AP.indexOf){
	var	arrIndexOf = function(arr,val){
			for(var i=0,len = arr.length;i<len;i++){
				if(val==arr[i]){
					return i;
				}
			}	
			return -1;
		};
	//}

	/**
	  *@Class Events
	  *@protected
	  */
	var Events = AE.Events = {
		/**
		  *@method subscribe
	 	  *@describe 
		  *@param name {String} 'before' or 'before  after launch'
		  *@param fn {function} fn1,fn2,fn3,fn4
		  */
		subscribe : function(name,fn){
			var names = name.split(REGSPACE),args = arguments,i,len,dd,j,dl;
			this._callback || (this._callback = {});
			for(i=0,len=names.length;i<len;i++){
				dd = names[i];
				this._callback[dd] || (this._callback[dd] = []);
				for(j=1,dl=args.length;j<dl;j++){
					this._callback[dd].push(args[j]);
				}
			}
		},
		/**
		  *@method fire
	 	  *@describe fire events
		  *@param name {String} 'before'
		  */
		fire : function(name){
			var i,len,calls,list;
			 
			if(!(calls = this._callback)) return;
			if(!(list = this._callback[name])) return;

			for(i=0,len = list.length;i<len;i++){
				if(list[i].apply(this,arguments) == false) break;
			}
		},
		/**
		  *@method unsubscribe
	 	  *@describe unsubscribe events
		  *@param name {String} 'before'
		  *@param fn {function} fn1,fn2,fn3,fn4
		  */
		unsubscribe : function(name,callback){
			var args = arguments,len,i,call,j,calls; 
			if(!args){
				this._callback = {};	
				return;
			}
			if(!(calls = this._callback[name])) return;
			if(!callback){
				calls = [];
				return;
			}else{
				for(i=1,len=args.length;i<len;i++){
					call = args[i];
					if((j = arrIndexOf(calls,call)) !== -1){
						calls.splice(j,1);
					}
				}
			}
		}
	};
	/**
	  *@method Objecte.create
	  *@describe MVC
	  *@param o {Object} 
	  *
	  */
	if('undefined' === typeof Object.create){
		Object.create = function(o){
			var F = function(){};
			F.prototype = o;
			return new F;
		}
	}

	var Class = AE.Class = {
		inherited : function(){},
		created : function(){},
		prototype : {
			init : function(){},
			initialize : function(){}
		},
		create : function(include,extend){
			var object = Object.create(this);		 
			object.parent = this;
			object.fn = object.prototype = Object.create(this.prototype);

			if(include) object.include(include);
			if(extend) object.extend(extend);
			
			object.created();
			return object;
		},
		init : function(){
			var instance = Object.create(this.prototype);	   
			instance.parent = this;

			instance.initialize.apply(instance,arguments);
			instance.init.apply(instance,arguments);
			return instance;
		},
		proxy : function(fn){
			var _self = this,
				args = slice.call(arguments,1);
			return function(){
				return fn.apply(_self,args.concat(slice.call(arguments)));
			};
		},
		proxyAll : function(arr){
			var i = 0,len = arr.length;
		   for(;i<len;i++){
				if(typeof this[i] === 'function'){
					this[i] = this.proxy(this[i]);
				}
		   }	
		},
		include : function(o){
			for(var i in o){
				if(arrIndexOf(EXCLUDE,i) === -1){
					this.fn[i] = o[i];
				}
			} 
		},
		extend : function(o){
			for(var i in o){
				if(arrIndexOf(EXCLUDE,i) === -1){
					this[i] = o[i];
				}
			}	 
		}
	};
	
	Class.fn = Class.prototype;
	Class.prototype.proxy = Class.proxy; 
	Class.prototype.proxyAll = Class.proxyAll;

	var Controller = AE.Controller = Class.create({
		tag : 'div',
		initialize : function(config){
			if(config){
				for(var i in config){
					this[i] = config[i];
				}
			}	
			if(!this.el) this.el = document.createElement(this.tag);
			this.el = $(this.el);

			if(!this.elems) this.elems = this.parent.elem;
			if(!this.events) this.events = this.parent.events;
			this.elems && this.refleshElements();
			this.events && this.bindEvents();
		},
		$ : function(selector){
			return $(selector,this.el);
		},
		refleshElements : function(){
			for(var i in this.elems){
				this[this.elems[i]] = this.$(i);
			}				  
		},
		bindEvents : function(){
			var match,method,selector,eventName;
			for( var i in this.events){
				method = this.proxy(this[this.events[i]]);
				match = i.match(REGEVENT);
				eventName = match[1];
				selector = match[2];

				if(selector == ''){
					this.el.bind(eventName,method);	
				}else{
					this.el.delegate(selector,eventName,method);
				}
			} 
		}
	});
	
	Controller.include(Events);
	
	AE.APP = Class.create();
	AE.APP.extend(Events);
	Controller.fn.app = AE.APP;
	
})(window,jQuery);