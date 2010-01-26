dojo.provide("geomajas.widget.FloatingPane");
/*
 * This file is part of Geomajas, a component framework for building
 * rich Internet applications (RIA) with sophisticated capabilities for the
 * display, analysis and management of geographic information.
 * It is a building block that allows developers to add maps
 * and other geographic data capabilities to their web applications.
 *
 * Copyright 2008-2010 Geosparc, http://www.geosparc.com, Belgium
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
dojo.require("dojox.layout.ContentPane");
dojo.require("dijit._Templated"); 
dojo.require("dijit._Widget"); 
dojo.require("dojo.dnd.Moveable");
dojo.require("dojox.layout.ResizeHandle"); 

dojo.declare("geomajas.widget.FloatingPane", 
	[ dojox.layout.ContentPane, dijit._Templated ],
	{
	// summary:
	//		A non-modal Floating window.
	//
	// description:
	// 		Makes a dijit.ContentPane float and draggable by it's title [similar to TitlePane]
	// 		and over-rides onClick to onDblClick for wipeIn/Out of containerNode
	// 		provides minimize(dock) / show() and hide() methods, and resize [almost] 
	//
	// closable: Boolean
	//		Allow closure of this Node
	closable: true,

	// dockable: Boolean
	//		Allow minimizing of pane if true
	dockable: true,

	// resizable: Boolean
	//		Allow resizing of pane true if true
	resizable: false,

	// maxable: Boolean
	//		Horrible param name for "Can you maximize this floating pane?"
	maxable: false,

	// resizeAxis: String
	//		One of: x | xy | y to limit pane's sizing direction
	resizeAxis: "xy",

	// title: String
	//		Title to use in the header
	title: "",

	// dockTo: DomNode?
	//		if empty, will create private layout.Dock that scrolls with viewport
	//		on bottom span of viewport.	
	dockTo: "",

	// duration: Integer
	//		Time is MS to spend toggling in/out node
	duration: 400,

	/*=====
	// iconSrc: String
	//		[not implemented yet] will be either icon in titlepane to left
	//		of Title, and/or icon show when docked in a fisheye-like dock
	//		or maybe dockIcon would be better?
	iconSrc: null,
	=====*/

	// contentClass: String
	// 		The className to give to the inner node which has the content
	contentClass: "dojoxFloatingPaneContent",

	// animation holders for toggle
	_showAnim: null,
	_hideAnim: null, 
	// node in the dock (if docked)
	_dockNode: null,

	// privates:
	_restoreState: {},
	_allFPs: [],
	_startZ: 100,

	templateString: null,
	templatePath: dojo.moduleUrl("geomajas.widget","html/FloatingPane.html"),
	
	postCreate: function(){
	
		this.setTitle(this.title);
		this.inherited(arguments);
		//var move = new geomajas.util.Moveable(this.domNode,{ handle: this.focusNode });
		var move = new dojo.dnd.move.parentConstrainedMoveable(this.domNode,{ handle: this.focusNode });
		//this._listener = dojo.subscribe("/dnd/move/start",this,"bringToTop"); 

		if(!this.dockable){ this.dockNode.style.display = "none"; } 
		if(!this.closable){ this.closeNode.style.display = "none"; } 
		if(!this.maxable){
			this.maxNode.style.display = "none";
			this.restoreNode.style.display = "none";
		}
		if(!this.resizable){
			this.resizeHandle.style.display = "none"; 	
		}else{
			var foo = dojo.marginBox(this.domNode); 
			this.domNode.style.width = foo.w+"px"; 
		}		
		this._allFPs.push(this);
		this.domNode.style.position = "absolute";
		
		this.domNode.style.overflow = "hidden";
		
		// Change hover styles for the buttons:
		//this.dockNode.o
	},
	
	startup: function(){
		if(this._started){ return; }
		
		this.inherited(arguments);

		if(this.resizable){
			if(dojo.isIE){
					this.canvas.style.overflow = "auto";
			}else{
					this.containerNode.style.overflow = "auto";
			}
			
			this._resizeHandle = new dojox.layout.ResizeHandle({ 
				targetId: this.id, 
				resizeAxis: this.resizeAxis 
			},this.resizeHandle);

		}

		if(this.dockable){ 
			// FIXME: argh.
			var tmpName = this.dockTo; 

			if(this.dockTo){
				this.dockTo = dijit.byId(this.dockTo); 
			}else{
				this.dockTo = dijit.byId('dojoxGlobalFloatingDock');
			}

			if(!this.dockTo){
				var tmpId; var tmpNode;
				// we need to make our dock node, and position it against
				// .dojoxDockDefault .. this is a lot. either dockto="node"
				// and fail if node doesn't exist or make the global one
				// once, and use it on empty OR invalid dockTo="" node?
				if(tmpName){ 
					tmpId = tmpName;
					tmpNode = dojo.byId(tmpName); 
				}else{
					tmpNode = document.createElement('div');
					dojo.body().appendChild(tmpNode);
					dojo.addClass(tmpNode,"dojoxFloatingDockDefault");
					tmpId = 'dojoxGlobalFloatingDock';
				}
				this.dockTo = new geomajas.widget.Dock({ id: tmpId, autoPosition: "south" },tmpNode);
				this.dockTo.startup(); 
			}
			
			if((this.domNode.style.display == "none")||(this.domNode.style.visibility == "hidden")){
				// If the FP is created dockable and non-visible, start up docked.
				this.minimize();
			} 
		} 		
		this.connect(this.focusNode,"onmousedown","bringToTop");
		this.connect(this.domNode,	"onmousedown","bringToTop");

		// Initial resize to give child the opportunity to lay itself out
		this.resize(dojo.coords(this.domNode));
		
		this._started = true;
	},

	setTitle: function(/* String */ title){
		// summary: Update the Title bar with a new string
		this.titleNode.innerHTML = title; 
		this.title = title; 
	},	
		
	close: function(){
		// summary: Close and destroy this widget
		log.info("Close and destroy this widget");
		if(!this.closable){ return; }
		this.hide(dojo.hitch(this,"destroyRecursive",arguments)); 
	},
	
	forcedClose: function(){
		this.hide(dojo.hitch(this,"destroyRecursive",arguments)); 
	},
	

	hide: function(/* Function? */ callback){
		// summary: Close, but do not destroy this FloatingPane
		dojo.fadeOut({
			node:this.domNode,
			duration:this.duration,
			onEnd: dojo.hitch(this,function() { 
				this.domNode.style.display = "none";
				this.domNode.style.visibility = "hidden"; 
				if(this.dockTo && this.dockable){
					this.dockTo._positionDock(null);
				}
				if(callback){
					callback();
				}
			})
		}).play();
	},

	show: function(/* Function? */callback){
		// summary: Show the FloatingPane
		var anim = dojo.fadeIn({node:this.domNode, duration:this.duration,
			beforeBegin: dojo.hitch(this,function(){
				this.domNode.style.display = ""; 
				this.domNode.style.visibility = "visible";
				if (this.dockTo && this.dockable) { this.dockTo._positionDock(null); }
				if (typeof callback == "function") { callback(); }
				this._isDocked = false;
				if (this._dockNode) { 
					this._dockNode.destroy();
					this._dockNode = null;
				}
			})
		}).play();
		this.resize(dojo.coords(this.domNode));
	},

	minimize: function(){
		// summary: Hide and dock the FloatingPane
		if(!this._isDocked){ this.hide(dojo.hitch(this,"_dock")); } 
	},

	maximize: function(){
		// summary: Make this FloatingPane full-screen (viewport)	
		if(this._maximized){ return; }
		this._naturalState = dojo.coords(this.domNode);
		if(this._isDocked){
			this.show();
			setTimeout(dojo.hitch(this,"maximize"),this.duration);
		}
		dojo.addClass(this.focusNode,"floatingPaneMaximized");
		this.resize(dijit.getViewport());
		this._maximized = true;
	},

	_restore: function(){
		if(this._maximized){
			this.resize(this._naturalState);
			dojo.removeClass(this.focusNode,"floatingPaneMaximized");
			this._maximized = false;
		}	
	},

	_dock: function(){
		if(!this._isDocked && this.dockable){
			this._dockNode = this.dockTo.addNode(this);
			this._isDocked = true;
		}
	},
	
	resize: function(/* Object */dim){
		// summary: Size the FloatingPane and place accordingly
		this._currentState = dim;

		// From the ResizeHandle we only get width and height information
		var dns = this.domNode.style;
		
		if(dim.t){ dns.top = dim.t+"px"; }
		if(dim.l){ dns.left = dim.l+"px"; }
		dns.width = dim.w+"px"; 
		dns.height = dim.h+"px";


		// Now resize canvas
		var borderX = 0;
		var borderY = 0;
		if (dojo.isIE) {
			borderX = 2;
			borderY = 2;
		}
		var mbCanvas = { l: 0, t: 0, w: (dim.w-borderX), h: (dim.h - this.focusNode.offsetHeight - 3-borderY) };
		dojo.marginBox(this.canvas, mbCanvas);

		// PDG: minus border and margin
		var width = getMarginBorderPaddingHorizontal(this.canvas);
		var height = getMarginBorderPaddingVertical(this.canvas);
		var mbCanvas = { l: 0, t: 0, w: (mbCanvas.w-width), h: (mbCanvas.h-height) };

		// If the single child can resize, forward resize event to it so it can
		// fit itself properly into the content area
		this._checkIfSingleChild();
		if(this._singleChild && this._singleChild.resize){
			this._singleChild.resize(mbCanvas);
		}
	},
	
	bringToTop: function(){
		// summary: bring this FloatingPane above all other panes
		var windows = dojo.filter(
			this._allFPs,
			function(i){
				return i !== this;
			}, 
		this);
		windows.sort(function(a, b){
			return a.domNode.style.zIndex - b.domNode.style.zIndex;
		});
		windows.push(this);
		
		dojo.forEach(windows, function(w, x){
			w.domNode.style.zIndex = this._startZ + (x * 2);
			dojo.removeClass(w.domNode, "dojoxFloatingPaneFg");
		}, this);
		dojo.addClass(this.domNode, "dojoxFloatingPaneFg");
	},
	
	destroy: function(){
		// summary: Destroy this FloatingPane completely
		this._allFPs.splice(dojo.indexOf(this._allFPs, this), 1);
		if(this._resizeHandle){
			this._resizeHandle.destroy();
		}
		if (this._dockNode) { 
			this._dockNode.destroy();
			this._dockNode = null;
		}
		this.inherited(arguments);
	},

	_onCloseEnter : function (e) {
		dojo.addClass(this.closeNode, "dojoxFloatingCloseIconHover");
	},

	_onCloseLeave : function (e) {
		dojo.removeClass(this.closeNode, "dojoxFloatingCloseIconHover");
	},

	_onDockEnter : function (e) {
		dojo.addClass(this.dockNode, "dojoxFloatingMinimizeIconHover");
	},

	_onDockLeave : function (e) {
		dojo.removeClass(this.dockNode, "dojoxFloatingMinimizeIconHover");
	}
});

dojo.declare("geomajas.widget.Dock",
		[dijit._Widget,dijit._Templated],
		{
		// summary:
		//		A widget that attaches to a node and keeps track of incoming / outgoing FloatingPanes
		// 		and handles layout

		templateString: '<div class="dojoxDock"><ul dojoAttachPoint="containerNode" class="dojoxDockList"></ul></div>',

		// private _docked: array of panes currently in our dock
		_docked: [],
		
		_inPositioning: false,
		
		autoPosition: false,
		
		addNode: function(refNode){
			// summary: Instert a dockNode refernce into the dock
			
			var div = document.createElement('li');
			this.containerNode.appendChild(div);
			var node = new geomajas.widget._DockNode({ title: refNode.title, paneRef: refNode },div);
			node.startup();
			return node;
		},

		startup: function(){
					
			if (this.id == "dojoxGlobalFloatingDock" || this.isFixedDock) {
				// attach window.onScroll, and a position like in presentation/dialog
				dojo.connect(window,'onresize',this,"_positionDock");
				dojo.connect(window,'onscroll',this,"_positionDock");
				if(dojo.isIE){
					this.connect(this.domNode, "onresize", "_positionDock");
				}
			}
			this._positionDock(null);
			this.inherited(arguments);

		},
		
		_positionDock: function(/* Event? */e){
			if(!this._inPositioning){	
				if(this.autoPosition == "south"){
					// Give some time for scrollbars to appear/disappear
					setTimeout(dojo.hitch(this, function() {
						this._inPositiononing = true;
						var viewport = dijit.getViewport();
						var s = this.domNode.style;
						s.left = viewport.l + "px";
						s.width = (viewport.w-2) + "px";
						s.top = (viewport.h + viewport.t) - this.domNode.offsetHeight + "px";
						this._inPositioning = false;
					}), 125);
				}
			}
		}


	});

dojo.declare("geomajas.widget._DockNode",
		[dijit._Widget,dijit._Templated],
		{
		// summary:
		//		dojox.layout._DockNode is a private widget used to keep track of
		//		which pane is docked.
		//
		// title: String
		// 		Shown in dock icon. should read parent iconSrc?	
		title: "",

		// paneRef: Widget
		//		reference to the FloatingPane we reprasent in any given dock
		paneRef: null,

		templateString:
			'<li dojoAttachEvent="onclick: restore" class="dojoxDockNode">'+
				'<span dojoAttachPoint="restoreNode" class="dojoxDockRestoreButton" dojoAttachEvent="onclick: restore,onmouseenter:_onRestoreEnter,onmouseleave:_onRestoreLeave"></span>'+
				'<span class="dojoxDockTitleNode" dojoAttachPoint="titleNode">${title}</span>'+
			'</li>',

		restore: function(){
			// summary: remove this dock item from parent dock, and call show() on reffed floatingpane
			this.paneRef.show();
			this.paneRef.bringToTop();
			this.destroy();
		},

		_onRestoreEnter : function () {
			dojo.addClass(this.restoreNode, "dojoxDockRestoreButtonHover");
		},

		_onRestoreLeave : function () {
			dojo.removeClass(this.restoreNode, "dojoxDockRestoreButtonHover");
		}
});
