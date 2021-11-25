package com.translatorfx.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 *  <p>Create some context messages.</p>
 *  @author Francisco David Manzanedo Valle.
 *  @version 1.0
 */

public class MessageUtils {

    /**
     * Show a built-in Error dialog.
     * @param header String corresponding to the dialog header.
     * @param message String corresponding to the actual error.
     */
    public static void showError(String header, String message){

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Show built-in Information Message dialog.
     * @param header String corresponding to the dialog header.
     * @param message String corresponding to the actual message.
     */
    public static void showMessage(String header, String message){

        Alert msg = new Alert(Alert.AlertType.INFORMATION);
        msg.setTitle("Information");
        msg.setHeaderText(header);
        msg.setContentText(message);
        msg.showAndWait();
    }


    /**
     * Show a built-in Confirmation dialog.
     * @param header String corresponding to the dialog header .
     * @param message String corresponding to the warning message.
     * @return boolean to check the option selected by the user.
     */
    public static boolean showConfirmation(String header, String message){

        boolean confirmation = false;
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setHeaderText(header);
        dialog.setTitle("Confirmation");
        dialog.setContentText(message);
        Optional<ButtonType> result = dialog.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK)
            confirmation = true;

        return  confirmation;
    }
}
