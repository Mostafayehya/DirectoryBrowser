package org.mostafayehya;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class PrimaryController implements Initializable {

    @FXML
    private TextField searchBox;

    @FXML
    private TreeView<File> treeView;

    @FXML
    private Button button;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        searchBox.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                populate();
            }
        });
    }

    @FXML
    public void populate() {
        // todo check for the right assginment
        // todo handle error
        try {
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/root.png")));
            treeView.setRoot(createNode(new File(searchBox.getText()), imageView));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Enter pressed ");
    }

    // This method creates a TreeItem to represent the given File.
    // It does this by overriding the TreeItem.getChildren() and TreeItem.isLeaf() methods
    // anonymously, but this could be better abstracted by creating a
    // 'FileTreeItem' subclass of TreeItem. However, this is left as an exercise
    // for the reader.
    private TreeItem<File> createNode(final File f, ImageView imageView) throws FileNotFoundException {
        return new TreeItem<File>(f, imageView) {
            // We cache whether the File is a leaf or not. A File is a leaf if
            // it is not a directory and does not have any files contained within
            // it. We cache this as isLeaf() is called often, and doing the
            // actual check on File is expensive.
            private boolean isLeaf;

            // We do the children and leaf testing only once, and then set these
            // booleans to false so that we do not check again during this
            // run. A more complete implementation may need to handle more
            // dynamic file system situations (such as where a folder has files
            // added after the TreeView is shown). Again, this is left as an
            // exercise for the reader.
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;

            @Override
            public ObservableList<TreeItem<File>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;

                    try {
                        super.getChildren().setAll(buildChildren(this));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return super.getChildren();
            }

            @Override
            public boolean isLeaf() {
                if (isFirstTimeLeaf) {
                    isFirstTimeLeaf = false;
                    File f = (File) getValue();
                    isLeaf = f.isFile();
                }

                return isLeaf;
            }

            private ObservableList<TreeItem<File>> buildChildren(TreeItem<File> TreeItem) throws FileNotFoundException {
                File f = TreeItem.getValue();
                if (f != null && f.isDirectory()) {
                    File[] files = f.listFiles();
                    if (files != null) {
                        ObservableList<TreeItem<File>> children = FXCollections.observableArrayList();

                        for (File childFile : files) {
                            if (childFile.isFile()) {
                                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/file.png")));
                                children.add(createNode(childFile, imageView));

                            } else {
                                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/closed.png")));
                                children.add(createNode(childFile, imageView));

                            }

                        }

                        return children;
                    }
                }

                return FXCollections.emptyObservableList();
            }
        };
    }
}
