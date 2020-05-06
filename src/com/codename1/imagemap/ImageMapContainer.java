/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.imagemap;

import com.codename1.ui.Button;
import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.ActionSource;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Rectangle;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.util.EventDispatcher;
import java.util.HashMap;
import java.util.Map;

/**
 * A UI component for displaying an image map.  I.e. An image with clickable "hot" areas.
 * 
 * Note that this container can be resized, and the image and clickable areas will be resized 
 * accordingly.
 * @author shannah
 */
public class ImageMapContainer extends Container implements ActionSource {
    private EventDispatcher listeners = new EventDispatcher();
    private Image image;
    private Dimension mapSize;
    private Map<Button,ClickableArea> areas = new HashMap<>();
    
    /**
     * Creates a new Image map container.
     * @param image The image to display.
     * @param width The width of the image map.  ClickableAreas are sized relative to this.
     * @param height The height of the image map. ClickableAreas are sized relative to this.
     */
    public ImageMapContainer(Image image, int width, int height) {
        this.image = image;
        this.mapSize = new Dimension(width, height);
        setLayout(new ImageMapLayout());
        getAllStyles().setBackgroundType(Style.BACKGROUND_IMAGE_SCALED_FIT);
        getAllStyles().setBgImage(image);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
    }
    
    /**
     * Adds a clickable area to the image map.
     * @param type THe type of area.  
     * @param rect The bounds of the clicable area, relative to the map size (as defined in the ImageMapContainer constructor).
     * @param l Handle when the area is clicked.
     */
    public void addClickableArea(ClickableAreaType type, Rectangle rect, ActionListener l) {
        addClickableAreas(new ClickableArea(type, rect, l));
        
    }
    
    /**
     * Add one or more clickable areas to the image map.
     * @param areas 
     */
    public void addClickableAreas(ClickableArea... areas) {
        for (ClickableArea a : areas) {
            Button btn = new Button("");
            btn.setUIID("ImageMapContainerClickableArea");
            btn.setCursor(Component.HAND_CURSOR);
            this.areas.put(btn, a);
            btn.addActionListener(evt->{
                if (evt.isConsumed()) {
                    return;
                }
                if (a.clickListener != null) {
                    a.clickListener.actionPerformed(evt);
                }
                if (evt.isConsumed()) {
                    return;
                }
                listeners.fireActionEvent(evt);
            });
            add(btn);
        }
        
        
    }

    @Override
    protected void initComponent() {
        super.initComponent();
        getComponentForm().setEnableCursors(true);
    }
    
    
    
    /**
     * A clickable area on an image map.
     */
    public class ClickableArea {
        private ClickableAreaType shape;
        private Rectangle bounds;
        private ActionListener clickListener;
        
        /**
         * Create a new clickable area.
         * @param type THe type of area.
         * @param bounds The bounds of the area, relative to the image map size (as defined in its constructor).
         * @param clickListener Listener called when area is clicked.
         */
        public ClickableArea(ClickableAreaType type, Rectangle bounds, ActionListener clickListener) {
            shape = type;
            this.bounds = new Rectangle(bounds);
            this.clickListener = clickListener;
        }
        
        private void setBounds(Component cmp) {
            double sx = getWidth() / (double)mapSize.getWidth();
            double sy = getHeight() / (double)mapSize.getHeight();
            cmp.setX((int)Math.round(bounds.getX() * sx));
            cmp.setY((int)Math.round(bounds.getY()*sy));
            cmp.setWidth((int)Math.round(bounds.getWidth()*sx));
            cmp.setHeight((int)Math.round(bounds.getHeight()*sy));
        }
    } 
    
    private class ImageMapLayout extends Layout {

        @Override
        public boolean isOverlapSupported() {
            return true;
        }

        
        @Override
        public void layoutContainer(Container cnt) {
            
            for (Component child : cnt) {
                if (areas.containsKey(child)) {
                    Button btn = (Button)child;
                    ClickableArea area = areas.get(btn);
                    area.setBounds(btn);
                }
            }
        }

        @Override
        public Dimension getPreferredSize(Container arg0) {
            return new Dimension(mapSize.getWidth(), mapSize.getHeight());
        }
        
    }
               
            
    /**
     * Enum with clicable area types.
     */
    public static enum ClickableAreaType {
        /**
         * A rectanglular clickable area.
         */
        Rect,
        
        /**
         * Oval clickable area.
         */
        Oval
    }
    
    
    /**
     * {@inheritDoc }
     * @param l 
     */
    @Override
    public void addActionListener(ActionListener l) {
        listeners.addListener(l);
    }

    /**
     * {@inheritDoc }
     * @param l 
     */
    @Override
    public void removeActionListener(ActionListener l) {
        listeners.removeListener(l);
    }
    
    
}
