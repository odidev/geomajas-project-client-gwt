/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2012 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */
package org.geomajas.command.dto;

import java.util.List;

import org.geomajas.annotation.Api;
import org.geomajas.command.CommandResponse;
import org.geomajas.geometry.Geometry;

/**
 * Response class for {@link org.geomajas.plugin.editing.server.command.geometry}.
 * 
 * @author Pieter De Graef
 * @since 1.11.0
 */
@Api(allMethods = true)
public class GeometrySplitResponse extends CommandResponse {

	private static final long serialVersionUID = 100L;

	private List<Geometry> geometries;

	public GeometrySplitResponse() {
	}

	public List<Geometry> getGeometries() {
		return geometries;
	}

	public void setGeometries(List<Geometry> geometries) {
		this.geometries = geometries;
	}
}