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
package org.geomajas.plugin.editing.dto;

import java.util.List;

import org.geomajas.annotation.Api;
import org.geomajas.command.CommandRequest;
import org.geomajas.geometry.Geometry;

/**
 * Request object for {@link org.geomajas.plugin.editing.server.command.GeometryAreaCommand}.
 * 
 * @author Jan De Moerloose
 * @since 1.0.0
 */
@Api
public class GeometryAreaRequest implements CommandRequest {

	private static final long serialVersionUID = 100L;

	/** Command name for this request. **/
	public static final String COMMAND = "command.GeometryArea";

	private List<Geometry> geometries;
	
	private String crs;

	public GeometryAreaRequest() {
	}

	public List<Geometry> getGeometries() {
		return geometries;
	}

	public void setGeometries(List<Geometry> geometries) {
		this.geometries = geometries;
	}
	
	public String getCrs() {
		return crs;
	}
	
	public void setCrs(String crs) {
		this.crs = crs;
	}

}