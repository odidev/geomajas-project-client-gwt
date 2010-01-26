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
package org.geomajas.extension.printing.configuration;

import org.geomajas.configuration.LayerInfo;
import org.geomajas.configuration.MapInfo;
import org.geomajas.configuration.RasterLayerInfo;
import org.geomajas.configuration.StyleInfo;
import org.geomajas.configuration.VectorLayerInfo;
import org.geomajas.extension.printing.component.LegendComponent;
import org.geomajas.extension.printing.component.MapComponent;
import org.geomajas.extension.printing.component.RasterLayerComponent;
import org.geomajas.extension.printing.component.TopDownVisitor;
import org.geomajas.extension.printing.component.VectorLayerComponent;
import org.geomajas.rendering.painter.PaintFactory;
import org.geomajas.service.ApplicationService;
import org.geomajas.service.BboxService;
import org.geomajas.service.FilterCreator;
import org.geomajas.service.GeoService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Visitor that configures the default layers of all the maps and legends in the print component. Maps and legends
 * should have valid map id's.
 *
 * @author Jan De Moerloose
 */
public class MapConfigurationVisitor extends TopDownVisitor {

	private ApplicationService runtime;

	private GeoService geoService;

	private BboxService bboxService;

	private FilterCreator filterCreator;

	private PaintFactory paintFactory;

	public MapConfigurationVisitor(ApplicationService runtimeParameters, GeoService geoService, BboxService bboxService,
			FilterCreator filterCreator, PaintFactory paintFactory) {
		this.runtime = runtimeParameters;
		this.geoService = geoService;
		this.bboxService = bboxService;
		this.filterCreator = filterCreator;
		this.paintFactory = paintFactory;
	}

	@Override
	public void visit(LegendComponent legend) {
		legend.clearItems();
		String mapId = legend.getMapId();
		MapInfo map = runtime.getMap(mapId);
		for (LayerInfo info : map.getLayers()) {
			if (info instanceof VectorLayerInfo) {
				legend.addVectorLayer((VectorLayerInfo) info);
			} else if (info instanceof RasterLayerInfo) {
				legend.addRasterLayer((RasterLayerInfo) info);
			}
		}
	}

	@Override
	public void visit(MapComponent mapComponent) {
		mapComponent.clearLayers();
		String mapId = mapComponent.getMapId();
		MapInfo map = runtime.getMap(mapId);
		List<LayerInfo> layers = new ArrayList<LayerInfo>(map.getLayers());
		Collections.reverse(layers);
		for (LayerInfo info : layers) {
			if (info instanceof VectorLayerInfo) {
				VectorLayerComponent comp = new VectorLayerComponent(geoService, bboxService, filterCreator,
						paintFactory);
				comp.setLabelsVisible(false);
				comp.setLayerId(info.getId());
				List<StyleInfo> sdef = ((VectorLayerInfo) info).getStyleDefinitions();
				comp.setStyleDefinitions(sdef.toArray(new StyleInfo[sdef.size()]));
				comp.setVisible(true);
				mapComponent.addComponent(0, comp);
			} else if (info instanceof RasterLayerInfo) {
				RasterLayerComponent comp = new RasterLayerComponent();
				comp.setLayerId(info.getId());
				comp.setVisible(true);
				mapComponent.addComponent(0, comp);
			}
		}
	}

}
