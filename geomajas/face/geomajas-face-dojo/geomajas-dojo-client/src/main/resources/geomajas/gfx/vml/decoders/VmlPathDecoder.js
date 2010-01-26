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

dojo.provide("geomajas.gfx.vml.decoders.VmlPathDecoder");
dojo.require("geomajas._base");

dojo.declare("VmlPathDecoder", null, {

	/**
	 * @class Decoder for geometry object to SVG path d-attributes.
	 * This is not applicable on Point geometries, only lines and polygons.
	 * @author Pieter De Graef
	 *
	 * @constructor
	 */
	constructor : function () {
	},

	/**
	 * This function decodes a Geometry (line or polygon) to a path's
	 * d-attribute.
	 * @param geometry The Line of Polygon object to be decoded.
	 * @return Returns the d-attribute as a string.
	 */
	decode : function (geometry, scale) {
		if (geometry == null) {
			return "";
		}
		if (geometry.declaredClass == geomajas.GeometryTypes.LINESTRING) {
			return this._decodeLine (geometry,scale);
		} else if (geometry.declaredClass == geomajas.GeometryTypes.LINEARRING) {
			return this._decodeLinearRing (geometry,scale);
		} else if (geometry.declaredClass == geomajas.GeometryTypes.POLYGON) {
			return this._decodePolygon (geometry,scale);
		} else if (geometry.declaredClass == geomajas.GeometryTypes.MULTIPOLYGON) {
			return this._decodeMultiPolygon (geometry,scale);
		} else if (geometry.declaredClass == geomajas.GeometryTypes.MULTILINESTRING) {
			return this._decodeMultiLine (geometry,scale);
		}
		return "";
	},

	/**
	 * Private function for decoding line-geometries.
	 * @private
	 */
	_decodeLine : function (line, scale) {
		if (line == null || line.isEmpty()) {
			return "";
		}
		var factor = scale ? 1 : 1;
		var d = "m"+(factor*line.getCoordinates()[0].getX()).toFixed() + "," + (factor*line.getCoordinates()[0].getY()).toFixed();
		var pstr = [];
		for (var i=1; i<line.getCoordinates().length; i++) {
			var x = line.getCoordinates()[i].getX();
			var y = line.getCoordinates()[i].getY();
			pstr.push(" l "+(factor*x).toFixed() +" "+(factor*y).toFixed() );
		}
		return d + pstr.join("") + "e";
	},

	/**
	 * Private function for decoding multiline string-geometries.
	 * @private
	 */
	_decodeMultiLine : function (multiline, scale) {
		var n = multiline.getNumGeometries();
		var pstr = [];
		for(var i = 0; i <= n; i++){
			pstr.push(this._decodeLine(multiline.getGeometryN(i),scale));
		}
		var result = pstr.join("");
		//log.info("_decodeMultiLine "+result);
		return result;
	},

	/**
	 * Private function for decoding polygon-geometries.
	 * @private
	 */
	_decodePolygon : function (polygon, scale) {
		if (polygon == null || polygon.isEmpty()) {
			return "";
		}
		var pstr = [];
		pstr.push(this._decodeLinearRing(polygon.getExteriorRing(), scale));
		for (var i=0; i<polygon.getNumInteriorRing(); i++) {
			if (polygon.getInteriorRingN(i).getNumPoints() > 2) { // Needed because IE doesn't show the even-odd as it should.
				pstr.push(this._decodeLinearRing(polygon.getInteriorRingN(i), scale));
			}
		}
		return pstr.join("")+ " e";
	},
	
	/**
	 * Private function for decoding multipolygon-geometries.
	 * @private
	 */
	_decodeMultiPolygon : function (multipoly, scale) {
		var n = multipoly.getNumGeometries();
		var pstr = [];
		for(var i = 0; i < n; i++){
			pstr.push(this._decodePolygon(multipoly.getGeometryN(i), scale));
		}
		return pstr.join("");
	},

	_decodeLinearRing : function (linearRing, scale) {
		if (linearRing == null || linearRing.isEmpty()) {
			return "";
		}
		var factor = scale ? 1 : 1;
		var d = "";
		var coords = linearRing.getCoordinates();
		var pstr = [];
		for (var i=0; i<coords.length-1; i++) {
			var pt = (factor*coords[i].getX()).toFixed()  + "," + (factor*coords[i].getY()).toFixed();
			if (i<(coords.length - 2)) {
				pt += " ";
			}
			if(i == 0){
				d += "m"+pt;
			} else if(i == 1){
				d += "l"+pt;
			} else {
				pstr.push(pt);
			}
		}
		return d + pstr.join("")+ " x";
	}

});
