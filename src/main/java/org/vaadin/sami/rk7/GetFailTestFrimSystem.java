package org.vaadin.sami.rk7;

import com.sun.org.apache.xerces.internal.xs.StringList;
import org.vaadin.sami.javaday.TetrisUI;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.vaadin.sami.javaday.TetrisUI.fileSystemView;
import static org.vaadin.sami.javaday.TetrisUI.listFile;

public class GetFailTestFrimSystem {
    /**
     * Add the files that are contained within the directory of this node.
     * Thanks to Hovercraft Full Of Eels.
     */
    public static void showChildrenRes(String pathResult) {
//        tree.setEnabled(false);
//        progressBar.setVisible(true);
//        progressBar.setIndeterminate(true);
        System.out.println("Метод showChildrenRes");
        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
                File file = new File(pathResult);
                if (file.isDirectory()) {
                    file = new File(pathResult);
                    File[] files = fileSystemView.getFiles(file, true);
                    //!!
//                    if (node.isLeaf()) {
//                        for (File child : files) {
//                            if (child.isDirectory()) {
//                                publish(child);
//                            }
//                        }
//                    }
                    System.out.println("Заполнение списка файлов!");
                    listFile.clear();
                    ArrayList<File> sList = new ArrayList<>(Arrays.asList(files));
                    sList.forEach(x -> {
                        if (x.isDirectory())listFile.add(x);});
                }
                return null;
            }
        };
        worker.execute();
    }
}
