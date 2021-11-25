package com.translatorfx.utils;

import com.translatorfx.TranslatorFX;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * <p>Class to load different views.</p>
 * @author Francisco David Manzanedo Valle.
 * @version 1.0
 */
public class ScreenLoader {

    /**
     * Static method to load a different view.
     * @param viewPath String with the path of the view to load.
     * @param stage stage to set the selected scene.
     * @throws IOException Exception.
     */
    public static Stage loadScreen(String viewPath, Stage stage) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(TranslatorFX.class.getResource(viewPath)));
        Scene viewScene = new Scene(root);
        Stage newStage = new Stage();
        newStage.setScene(viewScene);
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(stage);
        return newStage;
    }
}
