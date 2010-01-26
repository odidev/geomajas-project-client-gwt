package org.geomajas.gwt.client.spatial.snapping;

import org.geomajas.configuration.ApplicationInfo;
import org.geomajas.configuration.SnappingRuleInfo;
import org.geomajas.gwt.client.map.layer.VectorLayer;
import org.geomajas.gwt.client.spatial.geometry.Geometry;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class SnappingModeTest {

	private static final String SHAPE_FILE = "org/geomajas/testdata/shapes/cities_world/cities.shp";

	@Autowired
	private ApplicationInfo application;

	private List<Geometry> geometries = new ArrayList<Geometry>();

	private SnappingRuleInfo rule;

	private VectorLayer vectorLayer;

	/**
	 * Constructor that sets up an initial list of geometries to snap to.
	 */
	public SnappingModeTest() {
//		rule = new SnappingRuleInfo();
//		rule.setDistance(5);
//		rule.setLayer("cities");
//		rule.setType(SnappingRuleInfo.SNAPPING_TYPE_NEAREST);
//
//		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] {
//		        "org/geomajas/spring/geomajasContext.xml", "org/geomajas/testdata/layerCountries.xml",
//		        "org/geomajas/testdata/simplevectorsContext.xml" });
//		ApplicationService runtimeParameters = applicationContext.getBean("service.ApplicationService",
//		        ApplicationService.class);
//		Layer<VectorLayerInfo> layer = runtimeParameters.getLayer("countries");
//		vectorLayer = new VectorLayer(new MapModel("sampleFeaturesMap"), layer.getLayerInfo());
//
//		GeometryFactory factory = new GeometryFactory(4326, -1);
//		LinearRing shell1 = factory.createLinearRing(new Coordinate[] { new Coordinate(10.0, 10.0),
//		        new Coordinate(20.0, 10.0), new Coordinate(20.0, 20.0), new Coordinate(10.0, 20.0),
//		        new Coordinate(10.0, 10.0) });
//		LinearRing hole1 = factory.createLinearRing(new Coordinate[] { new Coordinate(12.0, 12.0),
//		        new Coordinate(18.0, 12.0), new Coordinate(18.0, 18.0), new Coordinate(12.0, 18.0),
//		        new Coordinate(12.0, 12.0) });
//		geometries.add(factory.createPolygon(shell1, new LinearRing[] { hole1 }));
//		LinearRing shell2 = factory.createLinearRing(new Coordinate[] { new Coordinate(5.0, 5.0),
//		        new Coordinate(15.0, 5.0), new Coordinate(15.0, 25.0), new Coordinate(5.0, 25.0) });
//		geometries.add(factory.createPolygon(shell2, null));
	}

	@Test
	public void testEqualHandler() {
//		EqualSnappingMode snappingMode = new EqualSnappingMode(rule);
//		snappingMode.setCoordinate(new Coordinate(16,16));
//		vectorLayer.getFeatureStore().query(snappingMode.getBounds(), snappingMode);
//		Coordinate snapped = snappingMode.getSnappedCoordinate();
//		System.out.println(snapped.toString());
	}
}
