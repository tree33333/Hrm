package de.hswt.hrm.main.ui.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import de.hswt.hrm.main.ui.MPartSwitcher;

public class PartSwitchHandler {

	@Inject
	EPartService service;

	@Inject
	EModelService modelService;
	
	@Execute
	public void execute(MHandledItem item, @Named("de.hswt.hrm.main.switchpart.idinput") String id) {
		
		MWindow window = null;
		if (item == null) {
			window = modelService.getTopLevelWindowFor(service.findPart(id));
		} else {
			window = modelService.getTopLevelWindowFor(item);
		}

		MUIElement result = modelService.find("de.hswt.hrm.main.partSwitchToolBar", window);
		if (result != null) {
			if (id.equals(MPartSwitcher.MAIN_ID)) {
				result.setVisible(false);
			} else {
				result.setVisible(true);
			}
		}
		if (id != null) {
			MPartSwitcher.setPartVisible(service, id);
		}
	}
}
