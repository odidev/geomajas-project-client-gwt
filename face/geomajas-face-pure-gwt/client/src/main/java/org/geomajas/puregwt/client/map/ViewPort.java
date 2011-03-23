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

package org.geomajas.puregwt.client.map;

import org.geomajas.configuration.client.ClientMapInfo;
import org.geomajas.geometry.Coordinate;
import org.geomajas.global.Api;
import org.geomajas.puregwt.client.spatial.Bbox;
import org.geomajas.puregwt.client.spatial.Geometry;
import org.geomajas.puregwt.client.spatial.Matrix;

/**
 * Central view port definition that determines and influences that position of the map. It allows for zooming in and
 * out, translation, etc.<br/>
 * Next to simply storing and changing the map location, implementation of this interface will also send out several
 * types of events that clearly define the changes in the view on the map.
 * 
 * @author Pieter De Graef
 * @author Oliver May
 * @author Jan De Moerloose
 * @since 1.0.0
 */
@Api(allMethods = true)
public interface ViewPort {

	void initialize(ClientMapInfo mapInfo);

	// -------------------------------------------------------------------------
	// Methods that retrieve what is visible on the map:
	// -------------------------------------------------------------------------

	/**
	 * Return the current scale on the map.
	 */
	double getScale();

	/**
	 * Return the currently visible bounds on the map. These bounds are expressed in the CRS of the map.
	 * 
	 * @return Returns the maps bounding box.
	 */
	Bbox getBounds();

	// -------------------------------------------------------------------------
	// Methods that manipulate what is visible on the map:
	// -------------------------------------------------------------------------

	/**
	 * Re-centers the map to a new position.
	 * 
	 * @param coordinate
	 *            the new center position
	 */
	void applyPosition(Coordinate coordinate);

	/**
	 * Apply a new scale level on the map. In case the are fixed resolutions defined on this MapView, it will
	 * automatically snap to the nearest resolution. In case the maximum extents are exceeded, it will pan to avoid
	 * this.
	 * 
	 * @param scale
	 *            The preferred new scale.
	 * @param option
	 *            zoom option, {@link org.geomajas.puregwt.client.map.ZoomOption}
	 */
	void applyScale(double scale, ZoomOption option);

	/**
	 * Apply a new scale level on the map. In case the are fixed resolutions defined on this MapView, it will
	 * automatically snap to the nearest resolution. In case the maximum extents are exceeded, it will pan to avoid
	 * this.
	 * 
	 * @param scale
	 *            The preferred new scale.
	 * @param option
	 *            zoom option, {@link org.geomajas.puregwt.client.map.ZoomOption}
	 * @param rescalePoint
	 *            After zooming, this point will still be on the same position in the view as before. Makes for easy
	 *            double clicking on the map without it moving away.
	 */
	void applyScale(double scale, ZoomOption option, Coordinate rescalePoint);

	/**
	 * <p>
	 * Change the view on the map by applying a bounding box (world coordinates!). Since the width/height ratio of the
	 * bounding box may differ from that of the map, the fit is "as good as possible".
	 * </p>
	 * <p>
	 * Also this function will almost certainly change the scale on the map, so if there have been resolutions defined,
	 * it will snap to them.
	 * </p>
	 * 
	 * @param bounds
	 *            A bounding box in world coordinates that determines the view from now on.
	 * @param option
	 *            zoom option, {@link org.geomajas.puregwt.client.map.ZoomOption}
	 */
	void applyBounds(Bbox bounds, ZoomOption option);

	/**
	 * Get the maximum zooming extent that is allowed on this view port. These bounds are determined by the map
	 * configuration.
	 * 
	 * @return The maximum zooming extent that is allowed on this view port.
	 */
	Bbox getMaximumBounds();

	/**
	 * Move the view on the map. This happens by translating the camera in turn.
	 * 
	 * @param x
	 *            Translation factor along the X-axis in world space.
	 * @param y
	 *            Translation factor along the Y-axis in world space.
	 */
	void translate(double x, double y);

	/**
	 * Adjust the current scale on the map by a new factor.
	 * 
	 * @param delta
	 *            Adjust the scale by factor "delta".
	 * @param option
	 *            The zooming option to use when applying the scaling transformation.
	 */
	void scale(double delta, ZoomOption option);

	/**
	 * Adjust the current scale on the map by a new factor, keeping a coordinate in place.
	 * 
	 * @param delta
	 *            Adjust the scale by factor "delta".
	 * @param option
	 *            The zooming option to use when applying the scaling transformation.
	 * @param center
	 *            Keep this coordinate on the same position as before.
	 * 
	 */
	void scale(double delta, ZoomOption option, Coordinate center);

	/**
	 * Drag the view on the map, without firing definitive ViewPortChanged events. This is used while dragging the map.
	 * Other than the events, it behaves the same as a translate.
	 * 
	 * @param x
	 *            Translation factor along the X-axis in world space.
	 * @param y
	 *            Translation factor along the Y-axis in world space.
	 */
	void drag(double x, double y);

	void setMapSize(int mapWidth, int mapHeight);

	Coordinate getPanOrigin();

	/**
	 * Get the current map width in pixels.
	 * 
	 * @return The current map width in pixels.
	 */
	int getMapWidth();

	/**
	 * Get the current map height in pixels.
	 * 
	 * @return The current map height in pixels.
	 */
	int getMapHeight();

	/**
	 * Return the description of the coordinate reference system used in the map. Usually this value returns an EPSG
	 * code.
	 * 
	 * @return The CRS code. Example: 'EPSG:4326'.
	 */
	String getCrs();

	// ------------------------------------------------------------------------
	// ViewPort transformation methods:
	// ------------------------------------------------------------------------

	/**
	 * Transform the given coordinate from a certain rendering space to another.
	 * 
	 * @param coordinate
	 *            The coordinate to transform. The X and Y ordinates are expected to be expressed in the 'from'
	 *            rendering space.
	 * @param from
	 *            The rendering space that expresses the X and Y ordinates of the given coordinate.
	 * @param to
	 *            The rendering space where to the coordinate should be transformed.
	 * @return The transformed coordinate.
	 */
	Coordinate transform(Coordinate coordinate, RenderSpace from, RenderSpace to);

	/**
	 * Transform the given geometry from a certain rendering space to another.
	 * 
	 * @param geometry
	 *            The geometry to transform. The coordinates are expected to be expressed in the 'from' rendering space.
	 * @param from
	 *            The rendering space that expresses the coordinates of the given geometry.
	 * @param to
	 *            The rendering space where to the geometry should be transformed.
	 * @return The transformed geometry.
	 */
	Geometry transform(Geometry geometry, RenderSpace from, RenderSpace to);

	/**
	 * Transform the given bounding box from a certain rendering space to another.
	 * 
	 * @param bbox
	 *            The bounding box to transform. The coordinates are expected to be expressed in the 'from' rendering
	 *            space.
	 * @param from
	 *            The rendering space that expresses the values (x, y, width, height) of the given bounding box.
	 * @param to
	 *            The rendering space where to the bounding box should be transformed.
	 * @return The transformed bounding box.
	 */
	Bbox transform(Bbox bbox, RenderSpace from, RenderSpace to);

	/**
	 * Get the transformation matrix to transform spatial objects from one render space to another. This matrix should
	 * contain both scale and translation factors.
	 * 
	 * @param from
	 *            The rendering space that describes the origin of the objects to transform.
	 * @param to
	 *            The rendering space that describes where to objects should be transformed.
	 * @return The matrix that describes the requested transformation.
	 */
	Matrix getTransformationMatrix(RenderSpace from, RenderSpace to);

	/**
	 * Get the translation matrix to transform spatial objects from one render space to another. This matrix should
	 * contain only translation factors, no scaling factors.
	 * 
	 * @param from
	 *            The rendering space that describes the origin of the objects to transform.
	 * @param to
	 *            The rendering space that describes where to objects should be transformed.
	 * @return The matrix that describes the requested transformation.
	 */
	Matrix getTranslationMatrix(RenderSpace from, RenderSpace to);
}