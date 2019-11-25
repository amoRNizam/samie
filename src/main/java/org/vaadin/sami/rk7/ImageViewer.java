package org.vaadin.sami.rk7;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

import java.util.ArrayList;
import java.util.List;

public class ImageViewer {

    /**
     * Creates a list of Resources to be shown in the ImageViewer.
     *
     * @return List of Resource instances
     */
    public static List<Resource> createImageList() {
        List<Resource> img = new ArrayList<Resource>();
        for (int i = 1; i < 10; i++) {
            img.add(new ThemeResource("WEB-INF/images/" + i + ".jpg"));
        }
        return img;
    }
}
