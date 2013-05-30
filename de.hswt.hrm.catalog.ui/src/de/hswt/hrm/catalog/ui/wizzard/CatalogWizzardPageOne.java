package de.hswt.hrm.catalog.ui.wizzard;

import java.net.URL;
import java.util.HashMap;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.forms.XWTForms;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import de.hswt.hrm.catalog.model.Activity;
import de.hswt.hrm.catalog.model.ICatalogItem;

public class CatalogWizzardPageOne extends WizardPage {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogWizzardPageOne.class);

    private Composite container;
    private HashMap<String, Widget> widgets;
    private Optional<ICatalogItem> item;

    public CatalogWizzardPageOne(String pageName, Optional<ICatalogItem> item) {
        super(pageName);
        this.item = item;
        setDescription(createDiscription());
    }

    public void createControl(Composite parent) {
        parent.setLayout(new FillLayout());
        URL url = CatalogWizzardPageOne.class.getClassLoader().getResource(
                "de/hswt/hrm/catalog/ui/xwt/CatalogWizard" + IConstants.XWT_EXTENSION_SUFFIX);

        try {
            container = (Composite) XWTForms.load(parent, url);
        }
        catch (Exception e) {
            LOG.error("Coult not load Wizzard XWT file.", e);
            return;
        }

        setKeyListener();
        setControl(container);
        setPageComplete(false);

    }

    public void setKeyListener() {
        HashMap<String, Widget> widgets = getWidgets();
        for (Widget w : widgets.values()) {

            if (w instanceof Text) {
                Text t = (Text) w;
                t.addKeyListener(new KeyListener() {

                    @Override
                    public void keyPressed(KeyEvent e) {
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        getWizard().getContainer().updateButtons();
                    }
                });
            }

            else if (w instanceof Button) {
                Button b = (Button) w;
                b.addSelectionListener(new SelectionListener() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        getWizard().getContainer().updateButtons();
                    }

                    @Override
                    public void widgetDefaultSelected(SelectionEvent e) {
                    }
                });
            }

        }
    }

    private HashMap<String, Widget> getWidgets() {
        if (widgets == null) {
            widgets = new HashMap<>();
            widgets.put(Fields.ACTIVITY, (Button) XWT.findElementByName(container, Fields.ACTIVITY));
            widgets.put(Fields.CURRENT, (Button) XWT.findElementByName(container, Fields.CURRENT));
            widgets.put(Fields.TARGET, (Button) XWT.findElementByName(container, Fields.TARGET));
            widgets.put(Fields.NAME, (Text) XWT.findElementByName(container, Fields.NAME));
            widgets.put(Fields.DESCRIPTION,
                    (Text) XWT.findElementByName(container, Fields.DESCRIPTION));

        }

        return widgets;
    }

    private String createDiscription() {
        if (item.isPresent()) {
            return "Soll/Ist/Maßnahme bearbeiten";
        }

        return "Neue Soll/Ist/Maßnahme anlegen";
    }

    public ICatalogItem getItem() {

        return updateItem(item);
    }

    private ICatalogItem updateItem(Optional<ICatalogItem> item) {

        HashMap<String, Widget> w = getWidgets();

        Text t = (Text) w.get(Fields.NAME);
        String s = t.getText();
        Text t2 = (Text) w.get(Fields.DESCRIPTION);
        String s2 = t2.getText();

        ICatalogItem a = new Activity(s, s2);
        return a;
    }

    @Override
    public boolean isPageComplete() {
        // FIXME implement Logic
        return true;
    }

    private static final class Fields {
        public static final String CURRENT = "current";
        public static final String TARGET = "target";
        public static final String ACTIVITY = "activity";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "desc";

    }
}
