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

package org.geomajas.gwt.client.action.menu;

import java.util.ArrayList;
import java.util.List;

import org.geomajas.global.GeomajasConstant;
import org.geomajas.gwt.client.action.MenuAction;
import org.geomajas.gwt.client.controller.editing.ParentEditController;
import org.geomajas.gwt.client.controller.editing.EditController.EditMode;
import org.geomajas.gwt.client.gfx.MenuGraphicsContext;
import org.geomajas.gwt.client.i18n.I18nProvider;
import org.geomajas.gwt.client.map.feature.Feature;
import org.geomajas.gwt.client.map.feature.FeatureTransaction;
import org.geomajas.gwt.client.map.feature.LazyLoadCallback;
import org.geomajas.gwt.client.map.feature.LazyLoader;
import org.geomajas.gwt.client.map.feature.TransactionGeomIndex;
import org.geomajas.gwt.client.map.feature.TransactionGeomIndexUtil;
import org.geomajas.gwt.client.spatial.geometry.Geometry;
import org.geomajas.gwt.client.spatial.geometry.LinearRing;
import org.geomajas.gwt.client.spatial.geometry.MultiPolygon;
import org.geomajas.gwt.client.spatial.geometry.Polygon;
import org.geomajas.gwt.client.spatial.geometry.operation.AddRingOperation;
import org.geomajas.gwt.client.widget.MapWidget;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemIfFunction;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * Insert a new ring in the {@link Polygon} or {@link MultiPolygon} at a given index.
 * 
 * @author Pieter De Graef
 */
public class InsertRingAction extends MenuAction implements MenuItemIfFunction {

	private MapWidget mapWidget;

	private ParentEditController controller;

	private TransactionGeomIndex index;

	/**
	 * @param mapWidget
	 *            The map on which editing is going on.
	 * @param controller
	 *            The current parent editing controller active on the map.
	 */
	public InsertRingAction(MapWidget mapWidget, ParentEditController controller) {
		super(I18nProvider.getMenu().insertRing(), "[ISOMORPHIC]/geomajas/ring-add.png");
		this.mapWidget = mapWidget;
		this.controller = controller;
		setEnableIfCondition(this);
	}

	/**
	 * Insert a new point in the geometry at a given index. The index is taken from the context menu event. This
	 * function will add a new empty interior ring in the polygon in question.
	 * 
	 * @param event
	 *            The {@link MenuItemClickEvent} from clicking the action.
	 */
	public void onClick(MenuItemClickEvent event) {
		final FeatureTransaction ft = mapWidget.getMapModel().getFeatureEditor().getFeatureTransaction();
		if (ft != null && index != null) {
			List<Feature> features = new ArrayList<Feature>();
			features.add(ft.getNewFeatures()[index.getFeatureIndex()]);
			LazyLoader.lazyLoad(features, GeomajasConstant.FEATURE_INCLUDE_GEOMETRY, new LazyLoadCallback() {
				public void execute(List<Feature> response) {
					controller.setEditMode(EditMode.INSERT_MODE);
					Geometry geometry = response.get(0).getGeometry();
					if (geometry instanceof Polygon) {
						geometry = addRing((Polygon) geometry);
					} else if (geometry instanceof MultiPolygon) {
						geometry = addRing((MultiPolygon) geometry);
					}
					ft.getNewFeatures()[index.getFeatureIndex()].setGeometry(geometry);
					controller.setGeometryIndex(index);
					controller.hideGeometricInfo();
				}
			});
		}
	}

	/**
	 * Implementation of the <code>MenuItemIfFunction</code> interface. This will determine if the menu action should be
	 * enabled or not. In essence, this action will be enabled when the context menu event occurred on the area of a
	 * polygon's exterior ring.
	 */
	public boolean execute(Canvas target, Menu menu, MenuItem item) {
		FeatureTransaction featureTransaction = mapWidget.getMapModel().getFeatureEditor().getFeatureTransaction();
		if (featureTransaction != null) {
			MenuGraphicsContext graphics = mapWidget.getGraphics();
			String targetId = graphics.getRightButtonName();
			if (targetId != null && TransactionGeomIndexUtil.isExteriorRing(targetId, true)) {
				index = TransactionGeomIndexUtil.getIndex(targetId);
				return true;
			}
		}
		return false;
	}

	// -------------------------------------------------------------------------
	// Private functions:
	// -------------------------------------------------------------------------

	private Polygon addRing(Polygon polygon) {
		LinearRing interiorRing = polygon.getGeometryFactory().createLinearRing(null);
		AddRingOperation op = new AddRingOperation(interiorRing);
		index.setInteriorRingIndex(polygon.getNumInteriorRing());
		index.setExteriorRing(false);
		return (Polygon) op.execute(polygon);
	}

	private MultiPolygon addRing(MultiPolygon multiPolygon) {
		if (index.getGeometryIndex() >= 0) {
			Polygon[] polygons = new Polygon[multiPolygon.getNumGeometries()];
			for (int n = 0; n < multiPolygon.getNumGeometries(); n++) {
				if (n == index.getGeometryIndex()) {
					polygons[n] = addRing((Polygon) multiPolygon.getGeometryN(n));
				} else {
					polygons[n] = (Polygon) multiPolygon.getGeometryN(n);
				}
			}
			return multiPolygon.getGeometryFactory().createMultiPolygon(polygons);
		}
		return multiPolygon;
	}
}
