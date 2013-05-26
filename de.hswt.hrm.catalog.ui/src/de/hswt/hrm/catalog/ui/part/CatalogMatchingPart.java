package de.hswt.hrm.catalog.ui.part;

import java.net.URL;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

import de.hswt.hrm.catalog.model.Activity;
import de.hswt.hrm.catalog.model.ICatalogItem;
import de.hswt.hrm.catalog.model.Target;
import de.hswt.hrm.catalog.service.CatalogService;

public class CatalogMatchingPart {

    @Inject
    CatalogService catalogService;

    @PostConstruct
    public void postConstruct(Composite parent, IEclipseContext context) {

        URL url = CatalogPart.class.getClassLoader().getResource(
                "de/hswt/hrm/catalog/ui/xwt/CatalogMatching" + IConstants.XWT_EXTENSION_SUFFIX);

        try {
            final Composite composite = (Composite) XWT.load(parent, url);

            final List targets = ((List) XWT.findElementByName(composite, "availableTarget"));
            final List currents = (List) XWT.findElementByName(composite, "availableCurrent");
            final List activities = (List) XWT.findElementByName(composite, "availableActivity");

            Collection<ICatalogItem> items = catalogService.findAllCatalogItem();
            initializeAvailableItems(items, targets, currents, activities);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeAvailableItems(Collection<ICatalogItem> items, List targets,
            List currents, List activities) {

        for (ICatalogItem i : items) {
            if (i instanceof Activity) {
                activities.add(i.getName());
            }
            else if (i instanceof Target) {
                targets.add(i.getName());
            }
            else {
                currents.add(i.getName());
            }
        }

    }
}
