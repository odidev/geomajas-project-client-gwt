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
package org.geomajas.plugin.deskmanager.client.gwt.manager.blueprints;

import org.geomajas.plugin.deskmanager.client.gwt.manager.common.AbstractConfigurationLayout;
import org.geomajas.plugin.deskmanager.client.gwt.manager.common.SaveButtonBar;
import org.geomajas.plugin.deskmanager.client.gwt.manager.common.themeconfig.ThemeConfigurationPanel;
import org.geomajas.plugin.deskmanager.client.gwt.manager.events.BlueprintEvent;
import org.geomajas.plugin.deskmanager.client.gwt.manager.events.BlueprintSelectionHandler;
import org.geomajas.plugin.deskmanager.client.gwt.manager.events.Whiteboard;
import org.geomajas.plugin.deskmanager.client.gwt.manager.service.DataCallback;
import org.geomajas.plugin.deskmanager.client.gwt.manager.service.ManagerCommandService;
import org.geomajas.plugin.deskmanager.client.gwt.manager.util.GeodeskDtoUtil;
import org.geomajas.plugin.deskmanager.command.manager.dto.SaveBlueprintRequest;
import org.geomajas.plugin.deskmanager.domain.dto.BlueprintDto;
import org.geomajas.widget.advancedviews.configuration.client.ThemesInfo;

import com.smartgwt.client.widgets.events.ClickEvent;

/**
 * Panel to allow theme configuration on the blueprint.
 * 
 * @author Oliver May
 * 
 */
public class BlueprintThemeConfig extends AbstractConfigurationLayout implements BlueprintSelectionHandler {

	private BlueprintDto blueprint;

	private ThemeConfigurationPanel themePanel;

	public BlueprintThemeConfig() {
		super();
		setMargin(5);

		SaveButtonBar buttonBar = new SaveButtonBar(this);
		addMember(buttonBar);

		// -------------------------------------------------

		themePanel = new ThemeConfigurationPanel();
		// themePanel.setDisabled(true);
		themePanel.setWidth100();
		themePanel.setHeight100();
		// themePanel.setOverflow(Overflow.AUTO);
		themePanel.setDisabled(true);

		addMember(themePanel);
	}

	public void setBlueprint(BlueprintDto blueprint) {
		this.blueprint = blueprint;
		if (blueprint != null) {
			themePanel.setMainMap(GeodeskDtoUtil.getMainMap(blueprint));
			
			if (blueprint.getMainMapClientWidgetInfos().get(ThemesInfo.IDENTIFIER) != null
					&& blueprint.getMainMapClientWidgetInfos().get(ThemesInfo.IDENTIFIER) instanceof ThemesInfo) {
				themePanel.setThemeConfig((ThemesInfo) blueprint.getMainMapClientWidgetInfos().get(
						ThemesInfo.IDENTIFIER));
			} else if (blueprint.getUserApplicationInfo().getMainMapWidgetInfos().get(ThemesInfo.IDENTIFIER) != null
					&& blueprint.getUserApplicationInfo().getMainMapWidgetInfos().get(ThemesInfo.IDENTIFIER) 
					instanceof ThemesInfo) {
				themePanel.setThemeConfig((ThemesInfo) blueprint.getUserApplicationInfo().getMainMapWidgetInfos()
						.get(ThemesInfo.IDENTIFIER));
			} else {
				themePanel.setThemeConfig(new ThemesInfo());
			}
		}
		fireChangedHandler();
	}

	// -- SaveButtonBar events --------------------------------------------------------

	public boolean onEditClick(ClickEvent event) {
		themePanel.setDisabled(false);
		return true;
	}

	public boolean onSaveClick(ClickEvent event) {
		// If we save and the clientwidgetinfo was not yet defined on the blueprint, overwrite it.
		if (blueprint.getMainMapClientWidgetInfos().get(ThemesInfo.IDENTIFIER) == null) {
			blueprint.getMainMapClientWidgetInfos().put(ThemesInfo.IDENTIFIER,
					blueprint.getUserApplicationInfo().getMainMapWidgetInfos().get(ThemesInfo.IDENTIFIER));
		}
		// blueprint.setLayout(themePanel.getLoketLayout());
		ManagerCommandService.saveBlueprint(blueprint, SaveBlueprintRequest.SAVE_CLIENTWIDGETINFO);
		themePanel.setDisabled(true);
		return true;
	}

	public boolean onCancelClick(ClickEvent event) {
		setBlueprint(blueprint);
		themePanel.setDisabled(true);
		//Reload the blueprint
		ManagerCommandService.getBlueprint(blueprint.getId(), new DataCallback<BlueprintDto>() {
			public void execute(BlueprintDto result) {
				Whiteboard.fireEvent(new BlueprintEvent(result));
			}
		});
		return true;
	}

	public void onBlueprintSelectionChange(BlueprintEvent bpe) {
		setBlueprint(bpe.getBlueprint());
	}


	public boolean onResetClick(ClickEvent event) {
		blueprint.getMainMapClientWidgetInfos().remove(ThemesInfo.IDENTIFIER);
		ManagerCommandService.saveBlueprint(blueprint, SaveBlueprintRequest.SAVE_CLIENTWIDGETINFO);
		themePanel.setDisabled(true);
		return true;
	}

	public boolean isDefault() {
		return !blueprint.getMainMapClientWidgetInfos().containsKey(ThemesInfo.IDENTIFIER);
	}	

}
