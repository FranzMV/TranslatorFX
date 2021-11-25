package com.translatorfx;

import com.translatorfx.utils.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TranslatorFXController implements Initializable {

    @FXML private ListView<FileData> listViewOriginalFiles;
    @FXML private ListView<FileData> listViewTranslatedFiles;
    @FXML private Label lblResult;
    @FXML private Button btnReadLanguages;
    @FXML private Button btnStartTranslation;
    @FXML private TextArea textAreaContent;
    @FXML private Button btnGoToChart;

    private static List<Language> languageListAux;
    private ThreadPoolExecutor executor;
    private ScheduledService<Boolean> service;
    private static String DESTINATION_PATH ="files";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        lblResult.setText("");
        btnStartTranslation.setDisable(true);

        //Click event listener over the original files TableView to show the files on the translated Tableview
        listViewOriginalFiles.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FileData>() {

            @Override
            public void changed(ObservableValue<? extends FileData> observableValue, FileData oldFile, FileData newFile) {
                if(newFile != null) {
                    listViewTranslatedFiles.getItems().clear();
                     String SUBFOLDER_PATH = Paths.get(DESTINATION_PATH).toAbsolutePath() + "/"
                     + newFile.getFilename().substring(0, newFile.getFilename().lastIndexOf("."));
                    try (DirectoryStream<Path> entries = Files.newDirectoryStream(Paths.get(SUBFOLDER_PATH))) {
                        for (Path path : entries)
                            listViewTranslatedFiles.getItems().add(new FileData(path.getFileName().toString(), path, 0));

                    } catch (IOException e) {
                        System.out.println("Error first listener: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });

        //Click event listener over the translated TableView to show 15 lines of the translated filed in the
        // TextAreaContent
        listViewTranslatedFiles.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FileData>() {
               @Override
               public void changed(ObservableValue<? extends FileData> observableValue, FileData oldFile,
                                   FileData newFile) {
                   try{
                       if(newFile != null)
                           textAreaContent.setText(FileUtils.readFile(newFile.getPath()));

                   }catch (IOException e){ MessageUtils.showError("Error", "An error has occurred reading the file");}
               }
           }
        );

        //ScheduleService to handle the current task of reading files and update the result label.
        service = new ScheduledService<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        long totalTasks = executor.getTaskCount();//Gets the total number of Tasks
                        long pendingTasks = executor.getCompletedTaskCount();//Get the number of the pending Tasks
                        lblResult.setDisable(false);
                        Platform.runLater(()-> {
                            lblResult.setText(pendingTasks + " of " + totalTasks + " task finished.");
                        });
                        return executor.isTerminated();
                    }
                };
            }
        };

        service.setDelay(Duration.millis(500));//The service starts over .5 seconds
        service.setPeriod(Duration.seconds(1));//Runs every second after
        service.setOnSucceeded(e->{
            if(service.getValue()){
                System.out.println("ThreadPool finished: "+service.getValue());
                service.cancel();//Cancel the service
                setDisableButtons(false);
            }
        });
    }

    /**
     * Click event over the button ReadLanguages
     * @param actionEvent actionEvent
     */
    @FXML
    private void readLanguages(ActionEvent actionEvent) {
        btnStartTranslation.setDisable(false);
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(((Node)actionEvent.getSource()).getScene().getWindow());
        if(selectedFile != null){
            languageListAux = FileUtils.readLanguages(selectedFile.toPath());
            MessageUtils.showMessage("File loaded", languageListAux.size()+" pair languages found");
        }else
            MessageUtils.showMessage("Information", "You must select a File of languages");
        //languageList.forEach(System.out::println);
    }

    /**
     * Click event over the button StartTranslation
     * @param actionEvent actionEvent
     */
    @FXML
    private void startTranslation(ActionEvent actionEvent) {

        DirectoryChooser dc = new DirectoryChooser();
        File selectedDirectory =  dc.showDialog((Stage)((Node)actionEvent.getSource()).getScene().getWindow());
        if(selectedDirectory != null){
            DESTINATION_PATH = selectedDirectory.getAbsolutePath();//The path to the destination files
            clearListViews();
            executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            setDisableButtons(true);
            service.restart();//Starts the service
            try(DirectoryStream<Path> entries = Files.newDirectoryStream(Paths.get(DESTINATION_PATH))){

                for(Path value: entries) {
                    if (!Files.isDirectory(value) && value.getFileName().toString().indexOf('.') > 0) {

                        executor.execute(() -> {

                            long startTime = System.currentTimeMillis();
                            try {
                                String fileName = value.getFileName().toString();
                                String filePath = value.toAbsolutePath().toString();
                                String directoryName = filePath.substring(0, filePath.lastIndexOf("."));

                                if (Files.exists(Paths.get(directoryName)))
                                    FileUtils.deleteDirectory(Paths.get(directoryName));
                                Files.createDirectory(Paths.get(directoryName));
                                List<Language> resultLanguage = FileUtils.checkLanguages(filePath, languageListAux);

                                for (Language language : resultLanguage) {
                                    String resultTranslation =
                                            directoryName + "/" + fileName +"_"+ language.getTo() ;
                                    //System.out.println(resultTranslation);
                                    FileUtils.translateLanguageFile(filePath, resultTranslation, language);
                                }
                                long finishTime = System.currentTimeMillis();
                                long resultTime = finishTime - startTime;
                                Platform.runLater(() -> listViewOriginalFiles.getItems().add(new FileData(fileName, value, resultTime)));

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            }catch (IOException ex){
                MessageUtils.showError("Error","An error occurred during the translation of the files.");
            }
            //The pool of thread finished its tasks
            executor.shutdown();
        }
    }

    /**
     * Enable or disable the buttons in the View
     * @param value Boolean to determinate its state
     */
    private void setDisableButtons(boolean value){
        btnGoToChart.setDisable(value);
        btnReadLanguages.setDisable(value);
        btnStartTranslation.setDisable(value);
    }

    /**
     * Clear the listviews on the view
     */
    private void clearListViews(){
        listViewOriginalFiles.getItems().clear();
        listViewTranslatedFiles.getItems().clear();
        textAreaContent.setText("");
    }
}