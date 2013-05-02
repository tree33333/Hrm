package de.hswt.hrm.scheme.ui;


import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.hswt.hrm.scheme.model.RenderedGridImage;

/**
 * A Widget that displays components in a grid.
 * 
 * @author Michael Sieger
 * 
 */
public class SchemeGrid extends Canvas{
    
    private static class RenderedGridImageContainer{
        RenderedGridImage image;
        int x, y;
        public RenderedGridImageContainer(RenderedGridImage image, int x, int y) {
            super();
            this.image = image;
            this.x = x;
            this.y = y;
        }
        
        /**
         * Returns true if the image intersects the other image.
         * 
         * @param o
         * @return
         */
        boolean intersects(RenderedGridImageContainer o){
            return getBoundingBox().intersects(o.getBoundingBox());
        }
        
        boolean intersects(Point p){
            return getBoundingBox().contains(p);
        }
        
        Rectangle getBoundingBox(){
            return new Rectangle(x, y, 
                    image.getGridImage().getWidth(),
                    image.getGridImage().getHeight());
        }
    }
    
    private final List<RenderedGridImageContainer> images = new ArrayList<>();
    private final int width, height;
    
    public SchemeGrid(Composite parent, int style, int width, int height) {
        super(parent, style);
        this.width = width;
        this.height = height;
        super.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent ev) {
                draw(ev.gc);
            }
        });
      
    }

    private void draw(GC gc) {
        drawImages(gc);
        drawHorizontalLines(gc);
        drawVerticalLines(gc);
    }

    private void drawImages(GC gc) {
        for(RenderedGridImageContainer container : images){
            drawImage(gc, container.image, container.x, container.y);
        }
    }

    private void drawImage(GC gc, RenderedGridImage RenderedGridImage, int x, int y) {
        Image image = RenderedGridImage.getImage();
        final float quadW = getQuadWidth();
        final float quadH = getQuadHeight();
        Rectangle rec = image.getBounds();
        gc.drawImage(image, 0, 0, rec.width, rec.height, Math.round(quadW * x), Math.round(quadH * y),
                Math.round(quadW * RenderedGridImage.getGridImage().getWidth()), Math.round(quadH * RenderedGridImage.getGridImage().getHeight()));
    }

    private void drawHorizontalLines(GC gc) {
        Rectangle rec = this.getBounds();
        final float quadH = getQuadHeight();
        for (int i = 0; i < height; i++) {
            final int y = Math.round(i * quadH);
            gc.drawLine(0, y, rec.width, y);
        }
        gc.drawLine(0, rec.height-1, rec.width, rec.height-1);
    }

    private void drawVerticalLines(GC gc) {
        Rectangle rec = this.getBounds();
        final float quadW = getQuadWidth();
        for (int i = 0; i < width; i++) {
            final int x = Math.round(i * quadW);
            gc.drawLine(x, 0, x, rec.height);
        }
        gc.drawLine(rec.width-1, 0, rec.width-1, rec.height);
    }

    private float getQuadWidth() {
        return ((float) getBounds().width) / width;
    }

    private float getQuadHeight() {
        return ((float) getBounds().height) / height;
    }
    
    /**
     * Coverts pixel position to grid position
     * 
     * @param x
     * @return
     */
    private int getGridX(int x){
    	return (int)(((float)x)/getQuadWidth());
    }
    
    /**
     * Coverts pixel position to grid position
     * 
     * @param x
     * @return
     */
    private int getGridY(int y){
    	return (int)(((float)y)/getQuadHeight());
    }

    /**
     * The image is placed at the given grid position.
     * Throws an IllegalArgumentException if the image is outside of the grid
     * 
     * @param image
     * @param x
     * @param y
     * @throws PlaceOccupiedException
     */
    public void setImageAt(RenderedGridImage image, int x, int y) throws PlaceOccupiedException {
        final int w = image.getGridImage().getWidth();
        final int h = image.getGridImage().getHeight();
        checkArgument(x >= 0 && x + w <= width);
        checkArgument(y >= 0 && y + h <= height);
        RenderedGridImageContainer toInsert = new RenderedGridImageContainer(image, x, y);
        for(RenderedGridImageContainer c : images){
            if(c.intersects(toInsert)){
                throw new PlaceOccupiedException("The image intersects with an image that is already there");
            }
        }
        images.add(toInsert);
        this.redraw();
    }
    
    /**
     * The image is placed at the given position in pixel.
     * The image snaps to the nearest grid position.
     * 
     * @param image
     * @param x
     * @param y
     * @throws PlaceOccupiedException
     */
    public void setImageAtPixel(RenderedGridImage image, int x, int y) throws PlaceOccupiedException{
        setImageAt(image, getGridX(x), getGridY(y));
    }
    
    /**
     * Removes and returns the image at the given grid position.
     * Returns null if there is no image.
     * 
     * @param x
     * @param y
     * @return
     */
    public RenderedGridImage removeImage(int x, int y){
    	Point p = new Point(x, y);
    	RenderedGridImageContainer res = null;
    	for(RenderedGridImageContainer cont : images){
    		if(cont.intersects(p)){
    			res = cont;
    			break;
    		}
    	}
    	if(res != null){
    		images.remove(res);
    		this.redraw();
    		return res.image;
    	}
    	return null;
    }

    /**
     * Removes and returns the image at the given pixel position
     * Returns null if there is no image.
     * 
     * @param x
     * @param y
     * @return
     */
	public RenderedGridImage removeImagePixel(int x, int y) {
		return removeImage(getGridX(x), getGridY(y));
	}

}
