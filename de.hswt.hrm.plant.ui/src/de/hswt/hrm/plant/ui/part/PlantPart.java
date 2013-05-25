package de.hswt.hrm.plant.ui.part;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hswt.hrm.common.database.exception.DatabaseException;
import de.hswt.hrm.common.ui.swt.table.ColumnComparator;
import de.hswt.hrm.common.ui.swt.table.ColumnDescription;
import de.hswt.hrm.common.ui.swt.table.TableViewerController;
import de.hswt.hrm.common.ui.xwt.XwtHelper;
import de.hswt.hrm.plant.model.Plant;
import de.hswt.hrm.plant.service.PlantService;
import de.hswt.hrm.plant.ui.event.PlantEventHandler;
import de.hswt.hrm.plant.ui.filter.PlantFilter;

public class PlantPart {

    private TableViewer viewer;
    private Collection<Plant> plants;
    @Inject
    private IEclipseContext context;

    @Inject
    EPartService service;
    
    private final static Logger LOG = LoggerFactory.getLogger(PlantPart.class);

    @PostConstruct
    public void postConstruct(Composite parent) {
    	PlantEventHandler eventHandler = ContextInjectionFactory.make(PlantEventHandler.class,context);
        URL url = PlantPart.class.getClassLoader().getResource(
                "de/hswt/hrm/plant/ui/xwt/PlantView" + IConstants.XWT_EXTENSION_SUFFIX);

        try {
            // Obtain root element of the XWT file
//            final Composite comp = (Composite) XWT.load(parent, url);
            final Composite comp = XwtHelper.loadWithEventHandler(parent, url, eventHandler);
            // Obtain TableViwer to fill it with data
            viewer = (TableViewer) XWT.findElementByName(comp, "plantTable");
            initializeTable(parent, viewer);
            refreshTable(parent);
            
            ((Button) XWT.findElementByName(comp, "back2Main")).addListener(SWT.Selection, new Listener() {
 				@Override
 				public void handleEvent(Event event) {
 					service.findPart("Clients").setVisible(false);
 					service.findPart("Places").setVisible(false);
 					service.findPart("Plants").setVisible(false);
 					service.findPart("Scheme").setVisible(false);
 					service.findPart("Catalog").setVisible(false);
 					service.findPart("Category").setVisible(false);
 					service.findPart("Main").setVisible(true);
 					service.showPart("Main", PartState.VISIBLE);
 				}
 			});
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshTable(Composite parent) {

        try {
            plants = PlantService.findAll();
            viewer.setInput(plants);

        }

        catch (DatabaseException e) {
            MessageDialog.openError(parent.getShell(), "Connection Error",
                    "Could not load Plants from Database.");
            LOG.error("Unable to retrieve list of plants.", e);

        }

    }

    private void initializeTable(Composite parent, TableViewer viewer2) {
        List<ColumnDescription<Plant>> columns = PlantPartUtil.getColumns();

        // Create columns in tableviewer
        TableViewerController<Plant> filler = new TableViewerController<>(viewer);
        filler.createColumns(columns);

        // Enable column selection
        filler.createColumnSelectionMenu();

        // Enable sorting
        ColumnComparator<Plant> comparator = new ColumnComparator<>(columns);
        filler.enableSorting(comparator);

        // Add dataprovider that handles our collection
        viewer.setContentProvider(ArrayContentProvider.getInstance());

        // Enable filtering
        viewer.addFilter(new PlantFilter());
    }

}
