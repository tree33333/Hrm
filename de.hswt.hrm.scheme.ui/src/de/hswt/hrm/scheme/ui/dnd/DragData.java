package de.hswt.hrm.scheme.ui.dnd;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

import de.hswt.hrm.common.database.exception.DatabaseException;
import de.hswt.hrm.common.database.exception.ElementNotFoundException;
import de.hswt.hrm.component.service.ComponentService;
import de.hswt.hrm.scheme.model.Direction;
import de.hswt.hrm.scheme.model.RenderedComponent;
import de.hswt.hrm.scheme.model.Scheme;
import de.hswt.hrm.scheme.model.SchemeComponent;
import de.hswt.hrm.scheme.service.SchemeService;
import de.hswt.hrm.scheme.ui.SchemeGridItem;

/**
 * This class represents a object, that is dragged in the SchemePart.
 * It is a Serializable version of SchemeGridItem which is not
 * Serialized by itself for Performance reasons.
 * 
 * @author Michael Sieger
 *
 */
public class DragData 
    implements Serializable
{
    private static final long serialVersionUID = 3635333453619223967L;
    
    /**
     * The index in the RenderedComponent set.
     */
    private final int renderedComponentIndex;
    
    private final int id;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int schemeId;
    private final int componentId;
    private final Direction direction;
    
    public DragData(int renderedComponentIndex, SchemeComponent schemeComponent) {
        super();
        this.renderedComponentIndex = renderedComponentIndex;
        id = schemeComponent.getId();
        x = schemeComponent.getX();
        y = schemeComponent.getY();
        width = schemeComponent.getWidth();
        height = schemeComponent.getHeight();
        schemeId = (schemeComponent.getScheme() != null ? schemeComponent.getScheme().getId() : -1);
        componentId = (schemeComponent.getComponent() != null ? schemeComponent.getComponent().getId() : -1);
        direction = schemeComponent.getDirection();
    }
    
    public boolean hasPosition(){
    	return !(x == -1 && y == -1);
    }
    
    /**
     * Creates the SchemeGridItem, that is DragData is representing
     * 
     * @param comps0
     * @return
     * @throws DatabaseException 
     * @throws ElementNotFoundException 
     */
    public SchemeGridItem toSchemeGridItem(List<RenderedComponent> comps, Scheme scheme, ComponentService componentService){
    	Preconditions.checkNotNull(comps);
    	Preconditions.checkNotNull(scheme);
    	Preconditions.checkNotNull(componentService);
    	try {
			return new SchemeGridItem(comps.get(renderedComponentIndex), 
												new SchemeComponent(scheme, 
														x, 
														y, 
														direction, 
														componentService.findById(componentId)));
		} catch (DatabaseException e) {
			//All these items were loaded before, so this should not happen
			throw Throwables.propagate(e);
		}
    }

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

}
