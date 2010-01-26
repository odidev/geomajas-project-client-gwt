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
package org.geomajas.internal.rendering.writers.svg.geometry;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import org.geomajas.internal.rendering.writers.GraphicsWriter;
import org.geomajas.rendering.GraphicsDocument;
import org.geomajas.rendering.RenderException;

/**
 * <p>
 * SVG writer for <code>Polygon</code> objects.
 * </p>
 *
 * @author Pieter De Graef, Jan De Moerloose
 */
public class PolygonWriter implements GraphicsWriter {

	public void writeObject(Object o, GraphicsDocument document, boolean asChild) throws RenderException {
		document.writeElement("path", asChild);
		document.writeAttribute("fill-rule", "evenodd");
		document.writeAttributeStart("d");
		Polygon poly = (Polygon) o;
		LineString shell = poly.getExteriorRing();
		int nHoles = poly.getNumInteriorRing();
		document.writeClosedPathContent(shell.getCoordinates());
		for (int j = 0; j < nHoles; j++) {
			document.writeClosedPathContent(poly.getInteriorRingN(j).getCoordinates());
		}
		document.writeAttributeEnd();
	}
}