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
package org.geomajas.extension.command.geometry;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.geomajas.command.Command;
import org.geomajas.extension.command.dto.SplitPolygonRequest;
import org.geomajas.extension.command.dto.SplitPolygonResponse;
import org.geomajas.geometry.Geometry;
import org.geomajas.service.DtoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * This command splits a polygon or multipolygon by a linestring, and returns an array of resulting
 * polygons/multipolygons.
 * </p>
 *
 * @author Pieter De Graef
 */
@Component()
public class SplitPolygonCommand implements Command<SplitPolygonRequest, SplitPolygonResponse> {

	@Autowired
	private DtoConverter converter;

	public SplitPolygonResponse getEmptyCommandResponse() {
		return new SplitPolygonResponse();
	}

	public void execute(SplitPolygonRequest request, SplitPolygonResponse response) throws Exception {
		// convert to most accurate precision model
		Polygon polygon = null;
		try {
			polygon = (Polygon) converter.toJts(request.getPolygon());
		} catch (Exception e) {
			// throw new GeomajasException();
		}
		GeometryFactory factory = new GeometryFactory(new PrecisionModel(), polygon.getFactory().getSRID());
		Polygon p = (Polygon) factory.createGeometry(polygon);
		LineString l = (LineString) factory.createGeometry(converter.toJts(request.getLineString()));
		int precision = 11;
		com.vividsolutions.jts.geom.Geometry buffered = factory.createGeometryCollection(null);
		while (buffered.isEmpty()) {
			buffered = l.buffer(Math.pow(10.0, -(precision--)));
		}
		com.vividsolutions.jts.geom.Geometry diff = p.difference(buffered);
		if (diff instanceof Polygon) {
			response.setPolygons(new Geometry[] {converter.toDto(diff)});
		} else if (diff instanceof MultiPolygon) {
			Geometry[] polygons = new Geometry[diff.getNumGeometries()];
			for (int i = 0; i < diff.getNumGeometries(); i++) {
				polygons[i] = converter.toDto(diff.getGeometryN(i));
				// makePrecise(polygon.getPrecisionModel(), polygons[i]);
			}
			response.setPolygons(polygons);
		}
	}

	private static void makePrecise(final PrecisionModel precision,
			final com.vividsolutions.jts.geom.Geometry geometry) {
		geometry.apply(new CoordinateSequenceFilter() {

			public void filter(CoordinateSequence coordinates, int index) {
				for (int i = 0; i < coordinates.getDimension(); i++) {
					double ordinate = coordinates.getOrdinate(index, i);
					double preciseOrdinate = precision.makePrecise(ordinate);
					coordinates.setOrdinate(index, i, preciseOrdinate);
				}
			}

			public boolean isDone() {
				return false;
			}

			public boolean isGeometryChanged() {
				return true;
			}
		});
	}
}