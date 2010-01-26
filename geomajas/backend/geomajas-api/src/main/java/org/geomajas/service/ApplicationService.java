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
package org.geomajas.service;

import org.geomajas.configuration.MapInfo;
import org.geomajas.layer.Layer;
import org.geomajas.layer.LayerException;
import org.geomajas.layer.VectorLayer;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Container class which contains runtime information about the parameters and other information for Geomajas. Values
 * are injected using Spring.
 *
 * @author Joachim Van der Auwera
 */
public interface ApplicationService {

	/**
	 * Get the directory where the tile cache should be stored.
	 *
	 * @return tile cache location
	 */
	String getTileCacheDirectory();

	/**
	 * Set the directory where the tile cache should be stored.
	 *
	 * @param tileCacheDirectory tile cache location
	 */
	void setTileCacheDirectory(String tileCacheDirectory);

	/**
	 * Get maximum number of cached tiles for the tile cache.
	 *
	 * @return maximum number of tiles which are cached
	 */
	int getTileCacheMaximumSize();

	/**
	 * Set the maximum number of tiles which may be cached.
	 *
	 * @param tileCacheMaximumSize maximum number of tiles which are cached
	 */
	void setTileCacheMaximumSize(int tileCacheMaximumSize);

	/**
	 * Check whether the tile cache is enabled.
	 *
	 * @return true when tile cache should be used
	 */
	boolean isTileCacheEnabled();

	/**
	 * Set whether the tile cache should be used or not.
	 *
	 * @param tileCacheEnabled new status
	 */
	void setTileCacheEnabled(boolean tileCacheEnabled);

	/**
	 * Get the layer with given id.
	 *
	 * @param id layer id
	 * @return layer
	 */
	VectorLayer getVectorLayer(String id);

	/**
	 * Get the vector layer with given id.
	 *
	 * @param id vector layer id
	 * @return vector layer
	 */
	Layer getLayer(String id);

	/**
	 * Get tinfo about the map with given id.
	 *
	 * @param id map id
	 * @return {@link org.geomajas.configuration.MapInfo}
	 */
	MapInfo getMap(String id);

	/**
	 * Get the {@link org.opengis.referencing.crs.CoordinateReferenceSystem} with given code.
	 *
	 * @param crs crs code
	 * @return {@link org.opengis.referencing.crs.CoordinateReferenceSystem}
	 * @throws LayerException crs code not found
	 */
	CoordinateReferenceSystem getCrs(String crs) throws LayerException;

}
