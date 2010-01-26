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
package org.geomajas.layermodel.hibernate;

import org.geomajas.layer.LayerModel;
import org.geomajas.layermodel.hibernate.command.interceptor.HibernateTransactionInterceptor;
import org.geomajas.layermodel.hibernate.pojo.HibernateTestManyToOne;
import org.geomajas.service.FilterCreator;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Unit test that tests filters for the HibernateLayerModel.
 *
 * @author Pieter De Graef
 */
public abstract class AbstractHibernateLayerModelTest {

	public static final String PARAM_TEXT_ATTR = "textAttr";
	public static final String PARAM_INT_ATTR = "intAttr";
	public static final String PARAM_FLOAT_ATTR = "floatAttr";
	public static final String PARAM_DOUBLE_ATTR = "doubleAttr";
	public static final String PARAM_BOOLEAN_ATTR = "booleanAttr";
	public static final String PARAM_DATE_ATTR = "dateAttr";
	public static final String PARAM_MANY_TO_ONE = "manyToOne";
	public static final String PARAM_ONE_TO_MANY = "oneToMany";
	public static final String PARAM_GEOMETRY_ATTR = "the_geom";

	/** Filter: manyToOne.textAttr */
	public static final String ATTR__MANY_TO_ONE__DOT__TEXT = PARAM_MANY_TO_ONE + HibernateLayerUtil.SEPARATOR
			+ HibernateTestManyToOne.PARAM_TEXT_ATTR;

	/** Filter: manyToOne.intAttr */
	public static final String ATTR__MANY_TO_ONE__DOT__INT = PARAM_MANY_TO_ONE + HibernateLayerUtil.SEPARATOR
			+ HibernateTestManyToOne.PARAM_INT_ATTR;

	/** Filter: manyToOne.floatAttr */
	public static final String ATTR__MANY_TO_ONE__DOT__FLOAT = PARAM_MANY_TO_ONE + HibernateLayerUtil.SEPARATOR
			+ HibernateTestManyToOne.PARAM_FLOAT_ATTR;

	/** Filter: manyToOne.doubleAttr */
	public static final String ATTR__MANY_TO_ONE__DOT__DOUBLE = PARAM_MANY_TO_ONE + HibernateLayerUtil.SEPARATOR
			+ HibernateTestManyToOne.PARAM_DOUBLE_ATTR;

	/** Filter: manyToOne.booleanAttr */
	public static final String ATTR__MANY_TO_ONE__DOT__BOOLEAN = PARAM_MANY_TO_ONE + HibernateLayerUtil.SEPARATOR
			+ HibernateTestManyToOne.PARAM_BOOLEAN_ATTR;

	/** Filter: manyToOne.dateAttr */
	public static final String ATTR__MANY_TO_ONE__DOT__DATE = PARAM_MANY_TO_ONE + HibernateLayerUtil.SEPARATOR
			+ HibernateTestManyToOne.PARAM_DATE_ATTR;

	/** Filter: oneToMany.textAttr */
	public static final String ATTR__ONE_TO_MANY__DOT__TEXT = PARAM_ONE_TO_MANY + HibernateLayerUtil.SEPARATOR
			+ HibernateTestManyToOne.PARAM_TEXT_ATTR;

	/** Filter: oneToMany.intAttr */
	public static final String ATTR__ONE_TO_MANY__DOT__INT = PARAM_ONE_TO_MANY + HibernateLayerUtil.SEPARATOR
			+ HibernateTestManyToOne.PARAM_INT_ATTR;

	/** Filter: oneToMany.floatAttr */
	public static final String ATTR__ONE_TO_MANY__DOT__FLOAT = PARAM_ONE_TO_MANY + HibernateLayerUtil.SEPARATOR
			+ HibernateTestManyToOne.PARAM_FLOAT_ATTR;

	/** Filter: oneToMany.doubleAttr */
	public static final String ATTR__ONE_TO_MANY__DOT__DOUBLE = PARAM_ONE_TO_MANY + HibernateLayerUtil.SEPARATOR
			+ HibernateTestManyToOne.PARAM_DOUBLE_ATTR;

	/** Filter: oneToMany.booleanAttr */
	public static final String ATTR__ONE_TO_MANY__DOT__BOOLEAN = PARAM_ONE_TO_MANY + HibernateLayerUtil.SEPARATOR
			+ HibernateTestManyToOne.PARAM_BOOLEAN_ATTR;

	/** Filter: oneToMany.dateAttr */
	public static final String ATTR__ONE_TO_MANY__DOT__DATE = PARAM_ONE_TO_MANY + HibernateLayerUtil.SEPARATOR
			+ HibernateTestManyToOne.PARAM_DATE_ATTR;

	protected static SessionFactory factory;
	protected static LayerModel layerModel;
	protected static HibernateTransactionInterceptor transactionInterceptor;
	protected static FilterCreator filterCreator;

	/** Default constructor initializing the Hibernate HSQL database, and creates the HibernateLayerModel. */
	public AbstractHibernateLayerModelTest() throws Exception {
		// assure initialization is only done once
		if (!isInitialised()) {
			ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
					new String[] {"org/geomajas/spring/geomajasContext.xml", "testContext.xml"});

			factory = applicationContext.getBean("testSessionFactory", SessionFactory.class);
			layerModel = applicationContext.getBean("layerModel", HibernateLayerModel.class);
			filterCreator = applicationContext.getBean("service.FilterCreator", FilterCreator.class);
			transactionInterceptor =
					applicationContext.getBean("hibernateTransactionInterceptor", HibernateTransactionInterceptor.class);
		}
	}

	public abstract boolean isInitialised();


	@Before
	public void before() {
		transactionInterceptor.beforeExecute(null);
	}

	@After
	public void after() {
		transactionInterceptor.afterExecute(null);
	}
}
