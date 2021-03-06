package de.hswt.hrm.scheme.ui.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import de.hswt.hrm.scheme.model.Direction;
import de.hswt.hrm.scheme.model.RenderedComponent;
import de.hswt.hrm.scheme.model.Scheme;
import de.hswt.hrm.scheme.ui.dnd.DragData;

/**
 * This class represents a Component in the SchemeTreeViewer
 * 
 * @author Michael Sieger
 *
 */
public class ComponentTreeItem extends SchemeTreeItem{
	
	private final RenderedComponent c;
	
	public ComponentTreeItem(CategoryTreeItem parent, RenderedComponent c){
		super(parent);
		this.c = c;
	}

	@Override
	public SchemeTreeItem[] getChildren() {
		List<SchemeTreeItem> list = new ArrayList<>();
		addDirection(list, Direction.downUp);
		addDirection(list, Direction.leftRight);
		addDirection(list, Direction.rightLeft);
		addDirection(list, Direction.upDown);
		SchemeTreeItem[] result = new SchemeTreeItem[list.size()];
		list.toArray(result);
		return result;
	}
	
	private void addDirection(List<SchemeTreeItem> list, Direction d){
		if(c.getByDirection(d) != null){
			list.add(new SchemeComponentTreeItem(this, c, d));
		}
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public String getText() {
		return c.getComponent().getName();
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public DragData getDragItem(Scheme scheme, List<RenderedComponent> renderedComponents) {
		return null;
	}

	public RenderedComponent getRenderedComponent() {
		return c;
	}
	
	

}
