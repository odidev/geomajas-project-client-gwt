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
package org.geomajas.dojo.server.json;

import com.metaparadigm.jsonrpc.AbstractSerializer;
import com.metaparadigm.jsonrpc.MarshallException;
import com.metaparadigm.jsonrpc.ObjectMatch;
import com.metaparadigm.jsonrpc.SerializerState;
import com.metaparadigm.jsonrpc.UnmarshallException;
import org.geomajas.geometry.Coordinate;
import org.geomajas.geometry.Geometry;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Serializer for the DTO geometry object.
 *
 * @author Pieter De Graef
 */
public class DtoGeometrySerializer extends AbstractSerializer {

	private static final long serialVersionUID = -1265127912351766084L;

	private static String TYPE_POINT = "Point";

	private static String TYPE_MULTIPOINT = "MultiPoint";

	private static String TYPE_LINESTRING = "LineString";

	private static String TYPE_LINEARRING = "LinearRing";

	private static String TYPE_POLYGON = "Polygon";

	private static String TYPE_MULTILINESTRING = "MultiLineString";

	private static String TYPE_MULTIPOLYGON = "MultiPolygon";

	private static String ATTRIBUTE_TYPE = "type";

	private static String ATTRIBUTE_SRID = "srid";

	private static String ATTRIBUTE_PRECISION = "precision";

	private static String ATTRIBUTE_COORDINATES = "coordinates";

	private static Class<?>[] SERIALIZABLE_CLASSES = new Class[] {Geometry.class};

	private static Class<?>[] JSON_CLASSES = new Class[] {JSONObject.class};

	public Class<?>[] getSerializableClasses() {
		return SERIALIZABLE_CLASSES;
	}

	public Class<?>[] getJSONClasses() {
		return JSON_CLASSES;
	}

	// -------------------------------------------------------------------------
	// UNMARSHALL - attempt: client-to-server deserialization attempt
	// -------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public ObjectMatch tryUnmarshall(SerializerState state, Class clazz, Object o) throws UnmarshallException {
		JSONObject jso = (JSONObject) o;
		String type = jso.getString(ATTRIBUTE_TYPE);
		if (type == null) {
			throw new UnmarshallException("no type hint");
		}
		int srid = jso.getInt(ATTRIBUTE_SRID);
		if (srid <= 0) {
			throw new UnmarshallException("no srid");
		}
		int precision = jso.getInt(ATTRIBUTE_PRECISION);
		if (precision <= 0) {
			throw new UnmarshallException("no precision");
		}
		if (!(type.equals(TYPE_POINT) || type.equals(TYPE_LINESTRING) || type.equals(TYPE_POLYGON)
				|| type.equals(TYPE_LINEARRING) || type.equals(TYPE_MULTILINESTRING) ||
				type.equals(TYPE_MULTIPOLYGON))) {
			throw new UnmarshallException(type + " is not a supported geometry");
		}
		JSONArray coordinates = jso.getJSONArray(ATTRIBUTE_COORDINATES);
		if (coordinates == null) {
			throw new UnmarshallException("coordinates missing");
		}
		return ObjectMatch.OKAY;
	}

	// -------------------------------------------------------------------------
	// UNMARSHALL: client-to-server deserialization
	// -------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public Object unmarshall(SerializerState state, Class clazz, Object o) throws UnmarshallException {
		return deserialize((JSONObject) o);
	}

	private Geometry deserialize(JSONObject json) throws UnmarshallException {
		// Read the basics, and create a Geometry object:
		String type = json.getString(ATTRIBUTE_TYPE);
		int srid = json.getInt(ATTRIBUTE_SRID);
		int precision = json.getInt(ATTRIBUTE_PRECISION);
		Geometry geometry = new Geometry(type, srid, precision);

		if (type.equals(TYPE_POINT)) {
			geometry = createPoint(geometry, json);
		} else if (type.equals(TYPE_LINESTRING)) {
			geometry = createLineString(geometry, json);
		} else if (type.equals(TYPE_LINEARRING)) {
			geometry = createLinearRing(geometry, json);
		} else if (type.equals(TYPE_POLYGON)) {
			geometry = createPolygon(geometry, json);
		} else if (type.equals(TYPE_MULTIPOINT)) {
			geometry = createMultiPoint(geometry, json);
		} else if (type.equals(TYPE_MULTILINESTRING)) {
			geometry = createMultiLineString(geometry, json);
		} else if (type.equals(TYPE_MULTIPOLYGON)) {
			geometry = createMultiPolygon(geometry, json);
		}
		return geometry;
	}

	private Geometry createPoint(Geometry geometry, JSONObject json) throws UnmarshallException {
		JSONArray jsonCoords = json.getJSONArray(ATTRIBUTE_COORDINATES);
		if (jsonCoords == null) {
			throw new UnmarshallException("coordinates missing");
		}
		if (jsonCoords.length() != 1) {
			throw new UnmarshallException("wrong number of coordinates " + jsonCoords.length() + " for point");
		}
		JSONObject coord = jsonCoords.getJSONObject(0);
		if (coord == null) {
			throw new UnmarshallException("inner coordinate missing");
		}

		Coordinate coordinate = new Coordinate(coord.getDouble("x"), coord.getDouble("y"));
		geometry.setCoordinates(new Coordinate[] {coordinate});
		return geometry;
	}

	private Geometry createLineString(Geometry geometry, JSONObject json) throws UnmarshallException {
		JSONArray jsonCoords = json.getJSONArray(ATTRIBUTE_COORDINATES);
		if (jsonCoords == null) {
			throw new UnmarshallException("coordinates missing");
		}
		Coordinate[] coordinates = new Coordinate[jsonCoords.length()];
		for (int i = 0; i < jsonCoords.length(); i++) {
			JSONObject nextCoord = jsonCoords.getJSONObject(i);
			if (nextCoord == null) {
				throw new UnmarshallException("inner coordinate missing");
			}
			coordinates[i] = new Coordinate(nextCoord.getDouble("x"), nextCoord.getDouble("y"));
		}
		geometry.setCoordinates(coordinates);
		return geometry;
	}

	private Geometry createLinearRing(Geometry geometry, JSONObject json) throws UnmarshallException {
		JSONArray jsonCoords = json.getJSONArray(ATTRIBUTE_COORDINATES);
		if (jsonCoords == null) {
			throw new UnmarshallException("coordinates missing");
		}
		Coordinate[] coordinates = new Coordinate[jsonCoords.length()];
		for (int i = 0; i < jsonCoords.length(); i++) {
			JSONObject nextCoord = jsonCoords.getJSONObject(i);
			if (nextCoord == null) {
				throw new UnmarshallException("inner coordinate missing");
			}
			coordinates[i] = new Coordinate(nextCoord.getDouble("x"), nextCoord.getDouble("y"));
		}
		coordinates = checkIfClosed(coordinates);
		geometry.setCoordinates(coordinates);
		return geometry;
	}

	private Geometry createPolygon(Geometry geometry, JSONObject json) throws UnmarshallException {
		JSONObject shell = json.getJSONObject("shell");
		if (shell == null) {
			throw new UnmarshallException("exterior ring is missing");
		}
		int len = 1;
		JSONArray holes = json.getJSONArray("holes");
		if (holes != null) {
			len += holes.length();
		}
		Geometry[] geometries = new Geometry[len];
		for (int i = 0; i < len; i++) {
			Geometry linearRing = new Geometry("LinearRing", geometry.getSrid(), geometry.getPrecision());
			if (i == 0) {
				geometries[0] = createLinearRing(linearRing, shell);
			} else {
				geometries[i] = createLinearRing(linearRing, holes.getJSONObject(i - 1));
			}
		}
		geometry.setGeometries(geometries);
		return geometry;
	}

	private Geometry createMultiPoint(Geometry geometry, JSONObject json) throws UnmarshallException {
		JSONArray points = json.getJSONArray("points");
		if (points == null) {
			throw new UnmarshallException("points are missing");
		}
		Geometry[] geometries = new Geometry[points.length()];
		for (int i = 0; i < points.length(); i++) {
			Geometry point = new Geometry("Point", geometry.getSrid(), geometry.getPrecision());
			geometries[i] = createPoint(point, points.getJSONObject(i));
		}
		geometry.setGeometries(geometries);
		return geometry;
	}

	private Geometry createMultiLineString(Geometry geometry, JSONObject json) throws UnmarshallException {
		JSONArray lineStrings = json.getJSONArray("lineStrings");
		if (lineStrings == null) {
			throw new UnmarshallException("lineStrings are missing");
		}
		Geometry[] geometries = new Geometry[lineStrings.length()];
		for (int i = 0; i < lineStrings.length(); i++) {
			Geometry lineString = new Geometry("LineString", geometry.getSrid(), geometry.getPrecision());
			geometries[i] = createLineString(lineString, lineStrings.getJSONObject(i));
		}
		geometry.setGeometries(geometries);
		return geometry;
	}

	private Geometry createMultiPolygon(Geometry geometry, JSONObject json) throws UnmarshallException {
		JSONArray polygons = json.getJSONArray("polygons");
		if (polygons == null) {
			throw new UnmarshallException("polygons are missing");
		}
		Geometry[] geometries = new Geometry[polygons.length()];
		for (int i = 0; i < polygons.length(); i++) {
			Geometry polygon = new Geometry("Polygon", geometry.getSrid(), geometry.getPrecision());
			geometries[i] = createPolygon(polygon, polygons.getJSONObject(i));
		}
		geometry.setGeometries(geometries);
		return geometry;
	}

	private Coordinate[] checkIfClosed(Coordinate[] coords) {
		int length = coords.length;
		if (coords[0].equals(coords[length - 1])) {
			return coords;
		}
		Coordinate[] newCoords = new Coordinate[length + 1];
		for (int i = 0; i < length; i++) {
			newCoords[i] = coords[i];
		}
		newCoords[length] = coords[0];
		return newCoords;
	}

	// -------------------------------------------------------------------------
	// MARSHALL: server-to-client serialization
	// -------------------------------------------------------------------------

	public Object marshall(SerializerState state, Object o) throws MarshallException {
		Geometry geometry = (Geometry) o;
		JSONObject json = null;
		if (geometry.getGeometryType().equals(TYPE_POINT)) {
			json = serializePoint(geometry);
		} else if (geometry.getGeometryType().equals(TYPE_LINESTRING)) {
			json = serializeLineString(geometry);
		} else if (geometry.getGeometryType().equals(TYPE_LINEARRING)) {
			json = serializeLinearRing(geometry);
		} else if (geometry.getGeometryType().equals(TYPE_POLYGON)) {
			json = serializePolygon(geometry);
		} else if (geometry.getGeometryType().equals(TYPE_MULTIPOINT)) {
			json = serializeMultiPoint(geometry);
		} else if (geometry.getGeometryType().equals(TYPE_MULTILINESTRING)) {
			json = serializeMultiLineString(geometry);
		} else if (geometry.getGeometryType().equals(TYPE_MULTIPOLYGON)) {
			json = serializeMultiPolygon(geometry);
		}
		return json;
	}

	private JSONObject serializePoint(Geometry geometry) {
		JSONObject json = new JSONObject();
		putBasics(json, geometry);
		putCoordinates(json, geometry);
		return json;
	}

	private JSONObject serializeLineString(Geometry geometry) {
		JSONObject json = new JSONObject();
		putBasics(json, geometry);
		putCoordinates(json, geometry);
		return json;
	}

	private JSONObject serializeLinearRing(Geometry geometry) {
		JSONObject json = new JSONObject();
		putBasics(json, geometry);
		putCoordinates(json, geometry);
		return json;
	}

	private JSONObject serializePolygon(Geometry geometry) {
		JSONObject json = new JSONObject();
		putBasics(json, geometry);

		JSONObject shell = null;
		if (geometry.getGeometries() != null && geometry.getGeometries().length > 0) {
			Geometry exteriorRing = geometry.getGeometries()[0];
			exteriorRing.setGeometryType(TYPE_LINESTRING);
			shell = serializeLinearRing(exteriorRing);
		}
		json.put("shell", shell);

		JSONArray holes = new JSONArray();
		if (geometry.getGeometries() != null && geometry.getGeometries().length > 1) {
			for (int i = 1; i < geometry.getGeometries().length; i++) {
				Geometry interiorRing = geometry.getGeometries()[i];
				interiorRing.setGeometryType(TYPE_LINESTRING);
				holes.put(serializeLinearRing(interiorRing));
			}
		}
		json.put("holes", holes);

		return json;
	}

	private JSONObject serializeMultiPoint(Geometry geometry) {
		JSONObject json = new JSONObject();
		putBasics(json, geometry);

		JSONArray points = null;
		if (geometry.getGeometries() != null && geometry.getGeometries().length > 0) {
			points = new JSONArray();
			for (int i = 0; i < geometry.getGeometries().length; i++) {
				points.put(serializePoint(geometry.getGeometries()[i]));
			}
		}
		json.put("points", points);
		return json;
	}

	private JSONObject serializeMultiLineString(Geometry geometry) {
		JSONObject json = new JSONObject();

		JSONArray lineStrings = null;
		if (geometry.getGeometries() != null && geometry.getGeometries().length > 0) {
			lineStrings = new JSONArray();
			for (int i = 0; i < geometry.getGeometries().length; i++) {
				lineStrings.put(serializeLineString(geometry.getGeometries()[i]));
			}
		}
		json.put("lineStrings", lineStrings);
		putBasics(json, geometry);
		return json;
	}

	private JSONObject serializeMultiPolygon(Geometry geometry) {
		JSONObject json = new JSONObject();
		putBasics(json, geometry);

		JSONArray polygons = null;
		if (geometry.getGeometries() != null && geometry.getGeometries().length > 0) {
			polygons = new JSONArray();
			for (int i = 0; i < geometry.getGeometries().length; i++) {
				polygons.put(serializePolygon(geometry.getGeometries()[i]));
			}
		}
		json.put("polygons", polygons);
		return json;
	}

	private void putBasics(JSONObject json, Geometry geometry) {
		// Put the basics in the JSON object:
		json.put(ATTRIBUTE_TYPE, geometry.getGeometryType());
		json.put(ATTRIBUTE_SRID, geometry.getSrid());
		json.put(ATTRIBUTE_PRECISION, geometry.getPrecision());
	}

	private void putCoordinates(JSONObject json, Geometry geometry) {
		// Put a coordinate array into the JSON object:
		JSONArray coordinates = new JSONArray();
		for (int i = 0; i < geometry.getCoordinates().length; i++) {
			coordinates.put(geometry.getCoordinates()[i].getX());
			coordinates.put(geometry.getCoordinates()[i].getY());
		}
		json.put(ATTRIBUTE_COORDINATES, coordinates);
	}
}
