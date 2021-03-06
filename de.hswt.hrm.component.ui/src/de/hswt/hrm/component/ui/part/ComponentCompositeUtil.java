package de.hswt.hrm.component.ui.part;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Optional;

import de.hswt.hrm.common.ui.swt.table.ColumnDescription;
import de.hswt.hrm.common.ui.swt.wizards.WizardCreator;
import de.hswt.hrm.component.model.Component;
import de.hswt.hrm.component.service.CategoryService;
import de.hswt.hrm.component.service.ComponentService;
import de.hswt.hrm.component.ui.wizard.ComponentWizard;
import de.hswt.hrm.i18n.I18n;
import de.hswt.hrm.i18n.I18nFactory;



public final class ComponentCompositeUtil {
    
    private static final I18n I18N = I18nFactory.getI18n(ComponentCompositeUtil.class);
	
    private ComponentCompositeUtil() {

    }

    public static Optional<Component> showWizard(ComponentService compSer, CategoryService catSer, IEclipseContext context, Shell shell,
            Optional<Component> component) {
    	
        ComponentWizard cw = new ComponentWizard(component);
        ContextInjectionFactory.inject(cw, context);

        WizardDialog wd = WizardCreator.createWizardDialog(shell, cw);
        wd.open();
        return cw.getComponent();
    }

    public static List<ColumnDescription<Component>> getColumns() {
        List<ColumnDescription<Component>> columns = new ArrayList<>();
        columns.add(getName());
        columns.add(getQuantifier());
        columns.add(getCategory());
        columns.add(getRating());
//        columns.add(getAttribute());

        return columns;

    }

    private static ColumnDescription<Component> getRating() {
        return new ColumnDescription<>(I18N.tr("with rating"), new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Component c = (Component) element;
               if(c.getBoolRating() == true){
            	   return I18N.tr("Yes");
               }else{
            	   return I18N.tr("No");
               }
            }
        }, new Comparator<Component>() {
            @Override
            public int compare(Component c1, Component c2) {
                String q = new Boolean(c1.getBoolRating()).toString();
                String q2 = new Boolean(c2.getBoolRating()).toString();
                return q.compareToIgnoreCase(q2);
            }
        });
    }

	private static ColumnDescription<Component> getCategory() {
        return new ColumnDescription<>(I18N.tr("Category"), new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
               	Component c = (Component) element;
                return c.getCategory().get().getName();
            }
        }, new Comparator<Component>() {
            @Override
            public int compare(Component c1, Component c2) {
                return c1.getCategory().get().getName().compareToIgnoreCase(c2.getCategory().get().getName());
            }
        });
    }

	private static ColumnDescription<Component> getName() {
        return new ColumnDescription<>(I18N.tr("Name"), new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
               	Component c = (Component) element;
                return c.getName();
            }
        }, new Comparator<Component>() {
            @Override
            public int compare(Component c1, Component c2) {
                return c1.getName().compareToIgnoreCase(c2.getName());
            }
        });
    }
    
    
    private static ColumnDescription<Component> getQuantifier() {
        return new ColumnDescription<>(I18N.tr("Weight"), new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Component c = (Component) element;
                if (c.getQuantifier().isPresent()) {
                	return c.getQuantifier().get().toString();
                }
                
                return "";
            }
        }, new Comparator<Component>() {
            @Override
            public int compare(Component c1, Component c2) {
            	int q1 = c1.getQuantifier().or(Integer.MAX_VALUE);
            	int q2 = c2.getQuantifier().or(Integer.MAX_VALUE);
            	return q1 - q2;
            }
        });
    }
}
