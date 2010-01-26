dojo.provide("geomajas.action.menu.editing.InsertPointAction");
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
dojo.require("geomajas.action.Action");

dojo.declare("InsertPointAction", Action, {

	/**
	 * @fileoverview Insert a point from a feature's geometry (rightmouse menu).
	 * @class Insert a point in a feature's geometry.
	 * @author Pieter De Graef
	 *
	 * @constructor 
	 * @extends Action
	 * @param id This action's unique identifier.
	 * @param mapWidget Reference to a MapWidget object.
	 * @param index Every point in a geometry has an index.
	 * @param position Coordinate holding the position for the new point.
	 */
	constructor : function (id, mapWidget, index, position) {
		/** @private */
		this.mapWidget = mapWidget;

		/** @private */
		this.index = index;

		/** Unique identifier */
		this.id = id;

		/** The action can be displayed as text only. */
		this.text = "Insert point";

		/** The point's position. */
		this.position = position;
	},

	/**
	 * Execute a {@link InsertCoordinateCommand} on the feature in the
	 * {@link FeatureTransaction}, stored in the {@link FeatureEditor}. Also
	 * redraws the {@link FeatureTransaction}.
	 * @param event The {@link HtmlMouseEvent} from clicking this action.
	 */
	actionPerformed : function (event) {
		var trans = this.mapWidget.getMapModel().getFeatureEditor().getFeatureTransaction();
		if (trans != null) {
			dojo.publish (this.mapWidget.getRenderTopic(), [trans, "delete"]);
			var command = new InsertCoordinateCommand (this.index, this.position);
			trans.execute (command);
			dojo.publish (this.mapWidget.getRenderTopic(), [trans, "all"]);
		}
	}
});
