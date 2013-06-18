package de.hswt.hrm.component.ui.wizard;


import javax.inject.Inject;

import org.eclipse.jface.wizard.Wizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import de.hswt.hrm.common.database.exception.DatabaseException;
import de.hswt.hrm.common.database.exception.SaveException;
import de.hswt.hrm.component.model.Category;
import de.hswt.hrm.component.model.Component;
import de.hswt.hrm.component.service.CategoryService;
import de.hswt.hrm.component.service.ComponentService;

public class ComponentWizard extends Wizard {
    private static final Logger LOG = LoggerFactory.getLogger(ComponentWizard.class);
    
    private ComponentService service;
    
    private CategoryService catService;
    
    private ComponentWizardPageOne first;
    private ComponentWizardPageTwo second;
    private Optional<Component> component;
    
    public ComponentWizard(Optional<Component> component, ComponentService compSer, CategoryService catSer) {
        this.service = compSer;
        this.catService = catSer;
    	
    	this.component = component;
        first = new ComponentWizardPageOne("Erste Seite", component, catService);
        second = new ComponentWizardPageTwo("Second Page", component);
        
        if (component.isPresent()) {
            setWindowTitle("Edit Component : "+component.get().getName());
        } else {
            setWindowTitle("Add new Component");
        }
    }
    
    public void addPages() {
        addPage(first);
        addPage(second);
    }
    
    public boolean canFinish() {
        return first.isPageComplete() && second.isPageComplete();
    }

    @Override
    public boolean performFinish() {
        if (component.isPresent()) {
            return editExistingComponent();
        } else {
            return insertNewComponent();
        }
    }
    
    private boolean editExistingComponent() {
//        Component c = this.component.get();
//        try {
//            c = service.findById(c.getId());
//            c = setValues(component);
//            component.update(c);
//            component = Optional.of(c);
//        } catch (DatabaseException e) {
//            LOG.error("An error occured: ", e);
//        }
        return true;
    }
    
    private boolean insertNewComponent() {
        Component c = setValues(Optional.<Component>absent());
//        try {
//           component = Optional.of(service.insert(c));
//        } catch (SaveException e) {
//            LOG.error("Could not save Element: "+component+" into Database", e);
//        }
        return true;
    }
    
    private Component setValues(Optional<Component> c) {
        Component component = null;
        if (c.isPresent()) {
            component = c.get();
            component.setName(first.getName());
            component.setQuantifier(first.getQuantifier());            
            component.setCategory(first.getCategory());
            component.setBoolRating(first.getRating());
            if(second.getImageLR()!= null){
            	component.setLeftRightImage(second.getImageLR());            	
            }
            if(second.getImageRL()!= null){
            	component.setRightLeftImage(second.getImageRL());            	
            }
            if(second.getImageDU()!= null){
            	component.setDownUpImage(second.getImageDU());            	
            }
            if(second.getImageUD()!= null){
            	component.setUpDownImage(second.getImageUD());            	
            }            
            
        } else {        	
        	component = new Component(first.getName(),second.getImageLR() , second.getImageRL(), second.getImageUD(), second.getImageDU(), first.getQuantifier(), first.getRating());
        }
        
        return component;
    }
    
    public Optional<Component> getComponent() {
        return component;
    }

}