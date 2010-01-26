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

package org.geomajas.gwt.client.spatial.snapping;

import org.geomajas.configuration.SnappingRuleInfo;
import org.geomajas.configuration.SnappingRuleInfo.SnappingType;
import org.geomajas.geometry.Coordinate;
import org.geomajas.gwt.client.map.cache.tile.VectorTile;
import org.geomajas.gwt.client.map.feature.Feature;
import org.geomajas.gwt.client.spatial.geometry.Geometry;
import org.geomajas.gwt.client.spatial.geometry.MultiLineString;
import org.geomajas.gwt.client.spatial.geometry.MultiPoint;
import org.geomajas.gwt.client.spatial.geometry.MultiPolygon;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Implementation of the <code>SnappingHandler</code> that treats all geometries equally. All geometries have an equal
 * chance of providing the snapped point.
 * </p>
 * <p>
 * When the {@link Snapper} uses <code>mode = SnapMode.ALL_GEOMETRIES_EQUAL</code> then this implementation of the
 * <code>SnappingHandler</code> is used.
 * </p>
 *
 * @author Pieter De Graef
 */
public class EqualSnappingMode extends SnappingMode {

	public EqualSnappingMode(SnappingRuleInfo rule) {
		super(rule);
	}

	public void execute(VectorTile tile) {
		List<Feature> features = tile.getFeatures();
		List<Geometry> geometries = new ArrayList<Geometry>();

		for (Feature feature : features) {
			Geometry geometry = feature.getGeometry();
			// For MultiPolygons and MultiLinestrings, we calculate bounds intersection
			// for each partial geometry. This way we can send parts of the complex
			// geometries to the snapping list, and not always the entire geometry.(=faster)
			if (geometry instanceof MultiLineString || geometry instanceof MultiPoint
					|| geometry instanceof MultiPolygon) {
				for (int n = 0; n < geometry.getNumGeometries(); n++) {
					Geometry geometryN = geometry.getGeometryN(n);
					if (geometryN.getBounds().intersects(bounds)) {
						geometries.add(geometryN);
					}
				}
			} else {
				if (geometry.getBounds().intersects(bounds)) {
					geometries.add(geometry);
				}
			}
		}

		if (!geometries.isEmpty()) {
			SnappingAlgorithm algorithm;
			if (rule.getType() == SnappingType.CLOSEST_ENDPOINT) {
				algorithm = new ClosestPointAlgorithm(geometries, rule.getDistance());
			} else {
				algorithm = new NearestAlgorithm(geometries, rule.getDistance());
			}
			Coordinate snapPointIfFound = algorithm.getSnappingPoint(coordinate, distance);
			if (snapPointIfFound != null) {
				snappedCoordinate = snapPointIfFound;
				distance = algorithm.getMinimumDistance();
			}
		}
	}
}
