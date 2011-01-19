dojo.provide("geomajas.map.print.LegendItemComponentInfo");
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
dojo.declare("LegendItemComponentInfo", PrintComponentInfo, {

	/**
	 * @class 
	 * A map component (mirror of server object)
	 * @author Jan De Moerloose
	 *
	 * @constructor
	 */
	constructor : function () {
		this.javaClass = "org.geomajas.plugin.printing.component.dto.LegendItemComponentInfo";

		this.getLayoutConstraint().setAlignmentX(geomajas.LayoutConstraints.LEFT);
		this.getLayoutConstraint().setAlignmentY(geomajas.LayoutConstraints.BOTTOM);
		this.getLayoutConstraint().setFlowDirection(geomajas.LayoutConstraints.FLOW_X);
		this.getLayoutConstraint().setMarginX(5);
		this.getLayoutConstraint().setMarginY(5);
	}

});
