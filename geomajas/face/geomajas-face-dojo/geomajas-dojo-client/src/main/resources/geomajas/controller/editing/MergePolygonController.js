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

dojo.provide("geomajas.controller.editing.MergePolygonController");
dojo.require("geomajas.event.MouseListener");
dojo.require("geomajas.action.menu.editing.MergePolygonAction");

dojo.require("geomajas.gfx.menu.Menu");

dojo.declare("MergePolygonController", MouseListener, {

	constructor : function (mapWidget) {
		/** @private Reference to the MapWidget object. */
		this.mapWidget = mapWidget;

		/** @private */
		this.transform = new WorldViewTransformation(this.mapWidget.getMapView());

		/** @private Used in the DOM. */
		this.menuId = "mergingMenu";
		this._menu = this._getOrCreateMenu();
	},

	/**
	 * Return a unique name.
	 */
	getName : function () {
		return "MergePolygonController";
	},

	/**
	 * Initialize this controller on activation.
	 */
	onActivate : function () {
		log.info(this.getName() + ".onActivate()");
		this._menu.bindDomNode(this.mapWidget.id);
	},

	/**
	 * cleanup this controller on deactivation.
	 */
	onDeactivate : function () {
		log.info(this.getName() + ".onDeactivate()");
		this._menu.unBindDomNode(this.mapWidget.id);
	},

	//DVB: why would we need this? - look like a real good way to shoot yourself in the foot
	getMenuId : function () {
		return this.menuId;
	},

	mouseClicked : function (/*HtmlMouseEvent*/event) {
		if (event.getButton() == event.statics.LEFT_MOUSE_BUTTON) {
			var action = new ToggleSelectionAction(this.menuId+".toggle", this.mapWidget, false);
			action.actionPerformed(event);
		}
	},

	mouseReleased : function (/*HtmlMouseEvent*/event) {
		if (event.getButton() != event.statics.RIGHT_MOUSE_BUTTON) {
			if (this._menu.isShowingNow) {
				dijit.popup.close(this._menu);
			}
		}
	},

	mousePressed : function (/*HtmlMouseEvent*/event) {
	},

	mouseMoved : function (/*HtmlMouseEvent*/event) {
	},

	contextMenu : function (/*HtmlMouseEvent*/event) {
		this._configureMenu(event);
		 // Override the stop-propagation! We need a right mouse menu in this controller.
	},

	/**
	 * @private
	 */
	_getOrCreateMenu : function () {
		log.info(this.getName() + "._getOrCreateMenu()");

		var pMenu = dijit.byId (this.menuId);
        if (pMenu != null){ return pMenu; } 

		pMenu = new Menu({id:this.menuId});
  
		var action0 = new ToggleSelectionAction(this.menuId+".toggle", this.mapWidget, false);
		var item0 = new geomajas.gfx.menu.MenuItem({label:action0.getText(),action:action0});
		pMenu.addChild(item0);

		var action1 = new DeselectAllAction(this.menuId+".deselectall", this.mapWidget.getMapModel().getSelectionTopic());
		var item1 = new geomajas.gfx.menu.MenuItem({label:action1.getText(),action:action1});
		pMenu.addChild(item1);

		pMenu.addChild(new dijit.MenuSeparator()); //item2

		var action3 = new MergePolygonAction(this.menuId+".merge", this.mapWidget);
		var item3 = new geomajas.gfx.menu.MenuItem({label:action3.getText(),action:action3});
		pMenu.addChild(item3);

		return pMenu;
	},
	
	/**
	 * @private
	 */
	_configureMenu : function (event) {
		log.info(this.getName() + "._configureMenu(event) for targetId: " + event.getTargetId());

		//NOTE: pass the HtmlMouseEvent we get here to each of the menuitems.
		dojo.forEach(this._menu.getChildren(), function(child){ if(child.setOriginalEvent) {child.setOriginalEvent(event);} });

		//disable these unless specified otherwise later on
		this._menu.getChildren()[3].setDisabled(true);

		var selection = this.mapWidget.getMapModel().getSelection();
		if (selection.count > 1) {
			var check = true;
			for (var i=0; i<selection.count; i++) {
				var geom = selection.item(i).getGeometry();
				if (geom instanceof MultiPolygon && geom.getNumGeometries() == 1) {
					geom = geom.getGeometryN(0);
				}
				if (!(geom instanceof Polygon)) {
					check = false;
					break;
				}
			}
			this._menu.getChildren()[3].setDisabled(!check);
		}
	}

});