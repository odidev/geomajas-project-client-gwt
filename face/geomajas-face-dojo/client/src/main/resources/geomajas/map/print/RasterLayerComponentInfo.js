dojo.provide("geomajas.map.print.RasterLayerComponentInfo");
/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2011 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */
dojo.declare("RasterLayerComponentInfo", BaseLayerComponentInfo, {

	/**
	 * @class 
	 * A map component (mirror of server object)
	 * @author Jan De Moerloose
	 *
	 * @constructor
	 */
	constructor : function () {
//		private String style = "";

		this.javaClass = "org.geomajas.plugin.printing.component.dto.RasterLayerComponentInfo";
		
		this.style = "";
	},

	setStyle : function (style) {
		this.style = style;
	}
	
});
