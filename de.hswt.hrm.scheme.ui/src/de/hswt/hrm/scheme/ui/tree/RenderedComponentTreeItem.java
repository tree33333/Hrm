package de.hswt.hrm.scheme.ui.tree;

import org.eclipse.swt.graphics.Image;

import de.hswt.hrm.scheme.model.Direction;
import de.hswt.hrm.scheme.model.RenderedComponent;

public class RenderedComponentTreeItem implements SchemeTreeItem{
	
	private final RenderedComponent comp;
	private final Direction dir;
	
	public RenderedComponentTreeItem(RenderedComponent comp, Direction dir){
		this.comp = comp;
		this.dir = dir;
	}

	@Override
	public SchemeTreeItem[] getChildren() {
		return null;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public SchemeTreeItem getParent() {
		return null;
	}

	@Override
	public String getText() {
		return null;
	}

	@Override
	public Image getImage() {
		return comp.getByDirection(dir).getThumbnail();
	}

}
