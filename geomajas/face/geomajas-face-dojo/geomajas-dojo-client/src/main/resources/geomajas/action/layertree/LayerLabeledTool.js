dojo.provide("geomajas.action.layertree.LayerLabeledTool");
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
dojo.require ("geomajas.action.LayerTreeTool");

dojo.declare("LayerLabeledTool", LayerTreeTool, {

	/**
	 * @fileoverview Activate/Deactivate labelling (for LayerTree widget).
	 * @class Implementation of the LayerTreeTool class, for the
	 * LayerTree widget. This activesettable determines whether or not the
	 * labels for a layer should be visible or not.
	 * @author Pieter De Graef
	 *
	 * @constructor
	 * @extends LayerTreeTool
	 * @param layerTree The LayerTree widget where this object should be
	 *                  added.
	 */
	constructor : function (layerTree) {
		/** Reference to the LayerTree widget */
		this.layerTree = layerTree;

		/** Identifier */
		this.id = layerTree.id + ".layerLabeled";

		/** The name of the image that is to represent this tool. */
		this.image = "layerLabeledIcon";

		/** Tooltip */
		this.tooltip = this.tooltipLocale.LayerLabeledTool;
	},

	/**
	 * This function is called when the LTButton widget, that represents this 
	 * class in the LayerTree, is activated. In this case it will activate a
	 * Layer's labels. The layer we are talking about, is the one that's
	 * currently selected in the LayerTree.
	 * @param event The browser's mouse event.
	 */
	onSelect : function (event) {
		var layer = this.layerTree.getSelected();
		if (layer != null && !layer.isLabeled()) {
			layer.setLabeled (true);
			log.info("Labels for layer "+layer.getLabel()+" activated");
		}
	},

	/**
	 * This function is called when the LTButton widget, that represents this
	 * class in the LayerTree, is deactivated. In this case the labels, for the
	 * currently selected Layer in the LayerTree, will be disabled.
	 * @param event The browser's mouse event.
	 */
	onDeSelect : function (event) {
		var layer = this.layerTree.getSelected();
		if (layer != null && layer.isLabeled()) {
			layer.setLabeled (false);
			log.info("Labels for layer "+layer.getLabel()+" deactivated");
		}
	},

	/**
	 * Returns true if the layer has labels enabled. False otherwise.
	 */
	getSelectionByLayer : function (layer) {
		if (layer == null || !(layer instanceof VectorLayer)) {
			return false;
		}
		return layer.isLabeled();
	},

	getEnabledByLayer : function (layer) {
		if (layer == null) {
			return false;
		} else if (!(layer instanceof VectorLayer)) {
			return false;
		} else if (!layer.isVisible()) {
			return false;
		}
		return true;
	}
});
