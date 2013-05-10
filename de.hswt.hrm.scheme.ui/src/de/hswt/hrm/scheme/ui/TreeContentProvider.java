package de.hswt.hrm.scheme.ui;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import de.hswt.hrm.scheme.model.Category;
import de.hswt.hrm.scheme.model.Component;
import de.hswt.hrm.scheme.model.RenderedComponent;

public class TreeContentProvider implements ITreeContentProvider{
    
    private List<RenderedComponent> comps;

    @Override
    public void dispose() {
        comps = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        comps = (List<RenderedComponent>) newInput;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getCategorys();
    }

    @Override
    public Object[] getChildren(final Object parentElement) {
        final Category category = (Category) parentElement;
        Collection<RenderedComponent> f = 
                Collections2.filter(comps, new Predicate<RenderedComponent>() {
                    @Override
                    public boolean apply(RenderedComponent c){
                        return category.equals(
                                c.getComponent().getCategory());
                    }
                });
        RenderedComponent[] result = new RenderedComponent[f.size()];
        f.toArray(result);
        return result;
    }

    @Override
    public Object getParent(Object element) {
        if(element.getClass() == Component.class){
            return ((Component)element).getCategory();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return element.getClass() == Category.class;
    }
    
    private Category[] getCategorys(){
        List<Category> cats = Lists.newArrayList();
        for(RenderedComponent c : comps){
            cats.add(c.getComponent().getCategory());
        }
        Category[] r = new Category[cats.size()];
        cats.toArray(r);
        return r;
    }

}
