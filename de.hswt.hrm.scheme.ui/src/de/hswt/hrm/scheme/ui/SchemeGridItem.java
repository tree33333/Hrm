package de.hswt.hrm.scheme.ui;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import de.hswt.hrm.scheme.model.RenderedGridImage;

/**
 * Contains the propertys of a image in the SchemeGrid.
 * 
 * @author Michael Sieger
 *
 */
public class SchemeGridItem {
	
	private RenderedGridImage gridImage;
	private int x;
	private int y;
	
	public SchemeGridItem(RenderedGridImage gridImage, int x, int y) {
		super();
		this.gridImage = gridImage;
		this.x = x;
		this.y = y;
	}

	public RenderedGridImage getGridImage() {
		return gridImage;
	}

	public void setGridImage(RenderedGridImage gridImage) {
		this.gridImage = gridImage;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
    /**
     * Returns true if the image intersects the other image.
     * 
     * @param o
     * @return
     */
    boolean intersects(SchemeGridItem o){
        return getBoundingBox().intersects(o.getBoundingBox());
    }
    
    boolean intersects(Point p){
        return getBoundingBox().contains(p);
    }
    
    Rectangle getBoundingBox(){
        return new Rectangle(x, y, 
        		gridImage.getGridImage().getWidth(),
                gridImage.getGridImage().getHeight());
    }

}
