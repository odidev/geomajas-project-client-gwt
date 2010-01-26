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

package org.geomajas.gwt.client.spatial.geometry.operation;

import org.geomajas.geometry.Coordinate;
import org.geomajas.gwt.client.spatial.geometry.Geometry;
import org.geomajas.gwt.client.spatial.geometry.LineString;
import org.geomajas.gwt.client.spatial.geometry.LinearRing;

/**
 * This operation inserts a <code>Coordinate</code> into the coordinates array of a {@link LineString} or
 * {@link LinearRing} geometry.
 *
 * @author Pieter De Graef
 */
public class InsertCoordinateOperation implements GeometryOperation {

	/**
	 * The integer index in the coordinates array. If this index is smaller then 0, then 0 is used instead. If this
	 * index is too large, then the coordinates is added at the back of the {@link LineString} (in the case of a
	 * {@link LinearRing}, it will be placed at the last position before the closing coordinate).
	 */
	private int coordIndex;

	/**
	 * The actual coordinate to insert into the array.
	 */
	private Coordinate coordinate;

	/**
	 * Initialize the operation with the correct parameters.
	 *
	 * @param coordIndex
	 *            The integer index in the coordinates array. If this index is smaller then 0, then 0 is used instead.
	 *            If this index is too large, then the coordinates is added at the back of the {@link LineString} (in
	 *            the case of a {@link LinearRing}, it will be placed at the last position before the closing
	 *            coordinate).
	 * @param coordinate
	 *            The actual coordinate to insert into the array.
	 */
	public InsertCoordinateOperation(int coordIndex, Coordinate coordinate) {
		this.coordIndex = coordIndex;
		this.coordinate = coordinate;
	}

	/**
	 * Execute the operation!
	 *
	 * @return Returns a new geometry. If the given geometry is null, or the given geometry is not a {@link LineString}
	 *         or {@link LinearRing}, then null is returned.
	 */
	public Geometry execute(Geometry geometry) {
		if (geometry == null) {
			return null;
		}
		if (geometry instanceof LinearRing) {
			if (coordIndex <= 0) {
				coordIndex = 0;
			} else if (coordIndex > geometry.getNumPoints() - 1) {
				coordIndex = geometry.getNumPoints() - 1;
			}
			Coordinate[] coordinates = new Coordinate[geometry.getNumPoints() + 1];
			int count = 0;
			for (int i = 0; i < coordinates.length; i++) {
				if (i == coordIndex) {
					coordinates[i] = coordinate;
				} else if (i == coordinates.length - 1) {
					coordinates[i] = coordinates[0];
				} else {
					coordinates[i] = geometry.getCoordinates()[count++];
				}
			}
			return geometry.getGeometryFactory().createLinearRing(coordinates);
		} else if (geometry instanceof LineString) {
			if (coordIndex <= 0) {
				coordIndex = 0;
			} else if (coordIndex > geometry.getNumPoints()) {
				coordIndex = geometry.getNumPoints();
			}
			Coordinate[] coordinates = new Coordinate[geometry.getNumPoints() + 1];
			int count = 0;
			for (int i = 0; i < coordinates.length; i++) {
				if (i == coordIndex) {
					coordinates[i] = coordinate;
				} else {
					coordinates[i] = geometry.getCoordinates()[count++];
				}
			}
			return geometry.getGeometryFactory().createLineString(coordinates);
		}
		return null;
	}

}
