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
package org.geomajas.sld.editor.client.view;

import org.geomajas.sld.client.presenter.MainPagePresenter;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Main view with content slots for child presenters.
 * 
 * @author Jan De Moerloose
 * 
 */
public class MainPageView extends ViewImpl implements MainPagePresenter.MyView {

	private VLayout mainContentPanel;

	private VLayout sideContentPanel;

	private HLayout topContentPanel;

	private VLayout widget;

	public MainPageView() {

		sideContentPanel = new VLayout();
		Label sideLabel = new Label();
		sideLabel.setContents("Side");
		sideLabel.setAlign(Alignment.CENTER);
		sideLabel.setOverflow(Overflow.HIDDEN);
		sideContentPanel.addMember(sideLabel);
		sideContentPanel.setWidth("30%");
		sideContentPanel.setHeight100();
		sideContentPanel.setShowResizeBar(true);

		topContentPanel = new HLayout();
		Label topLabel = new Label();
		topLabel.setContents("Top");
		topLabel.setAlign(Alignment.CENTER);
		topLabel.setOverflow(Overflow.HIDDEN);
		topContentPanel.addMember(topLabel);
		topContentPanel.setHeight(100);
		topContentPanel.setWidth100();
		topContentPanel.setShowResizeBar(true);

		mainContentPanel = new VLayout();
		Label mainLabel = new Label();
		mainLabel.setContents("Main");
		mainLabel.setAlign(Alignment.CENTER);
		mainLabel.setOverflow(Overflow.HIDDEN);
		mainContentPanel.addMember(mainLabel);
		mainContentPanel.setHeight100();
		mainContentPanel.setWidth("70%");

		HLayout hLayout = new HLayout();
		hLayout.addMember(sideContentPanel);
		hLayout.addMember(mainContentPanel);
		hLayout.setHeight("*");
		hLayout.setWidth100();

		widget = new VLayout();
		widget.setWidth100();
		widget.setHeight100();
		widget.addMember(topContentPanel);
		widget.addMember(hLayout);

	}

	public Widget asWidget() {
		return widget;
	}

	// GWTP will call setInSlot when a child presenter asks to be added under
	// this view.
	@Override
	public void setInSlot(Object slot, Widget content) {
		if (slot == MainPagePresenter.TYPE_MAIN_CONTENT) {
			setMainContent(content);
		} else if (slot == MainPagePresenter.TYPE_SIDE_CONTENT) {
			setSideContent(content);
		} else if (slot == MainPagePresenter.TYPE_TOP_CONTENT) {
			setTopContent(content);
		} else {
			super.setInSlot(slot, content);
		}
	}

	private void setMainContent(Widget content) {
		for (Canvas member : mainContentPanel.getMembers()) {
			member.removeFromParent();
		}
		if (content != null) {
			mainContentPanel.addMember(content);
		}
	}

	private void setSideContent(Widget content) {
		for (Canvas member : sideContentPanel.getMembers()) {
			member.removeFromParent();
		}
		if (content != null) {
			sideContentPanel.addMember(content);
		}
	}

	private void setTopContent(Widget content) {
		for (Canvas member : topContentPanel.getMembers()) {
			member.removeFromParent();
		}
		if (content != null) {
			topContentPanel.addMember(content);
		}
	}

	public void showLoading(boolean visibile) {
		// TODO Auto-generated method stub

	}
}