/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wunderlist;

import wunderlist.model.Entry;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import wunderlist.model.Category;

/**
 *
 * @author yashar
 */
public class Wunderlist extends Application {

    AnchorPane mainFrame;
    AnchorPane informationBoard;
    AnchorPane addNewCategory;
    TextField informationBoardText;
    CheckBox informationBoardDone;
    TextArea informationBoardNote;
    ImageView informationBoardFavorite;
    ImageView addImage;
    TextField addTextField;
    VBox middleBox;
    ListView<Entry> items;
    ListView<Category> categories;
    public ObservableList<Entry> listOfItems;
    public ObservableList<Category> listOfCategories;
    boolean informationBoardOpened = false;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws IOException {

        this.primaryStage = primaryStage;
        listOfItems = FXCollections.observableArrayList();
        listOfCategories = FXCollections.observableArrayList();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Wunderlist.class.getResource("frame.fxml"));
        AnchorPane root = (AnchorPane) loader.load();
        //Parent root = loader.load(getClass().getResource("frame.fxml"));
        mainFrame = (AnchorPane) root.lookup("#mainFrame");
        mainFrame.setStyle("-fx-background-image: url('wunderlist/Images/Backgrounds/06.jpg'); -fx-background-repeat: stretch; -fx-background-position: center center");
        addNewCategory = (AnchorPane) root.lookup("#addNewCategory");
        addImage = (ImageView) root.lookup("#addImage");
        addTextField = (TextField) root.lookup("#addTextField");
        addImage.setOnMouseClicked((MouseEvent event) -> {
            FadeTransition fadeTransition = new FadeTransition(new Duration(150), addImage);
            fadeTransition.setFromValue(1);
            fadeTransition.setToValue(0);
            fadeTransition.play();
            fadeTransition.setOnFinished((ActionEvent event1) -> {
                addTextField.setVisible(true);
                addTextField.setEditable(true);
                addTextField.setPromptText("Enter new Category");
                addTextField.toFront();
            });
        });
        addTextField.setOnAction((ActionEvent event) -> {
            Category category = new Category(addTextField.getText());
            listOfCategories.add(category);
            addTextField.setEditable(false);
            addTextField.setVisible(false);
            addTextField.setPromptText("");
            addTextField.setText("");
            FadeTransition fadeTransition = new FadeTransition(new Duration(150), addImage);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.play();
        });

        FlowPane flowPane = (FlowPane) mainFrame.lookup("#flowPane");
        middleBox = (VBox) flowPane.lookup("#middleBox");
        informationBoard = (AnchorPane) root.lookup("#informationBoard");
        informationBoardText = (TextField) informationBoard.lookup("#informationBoardText");
        informationBoardDone = (CheckBox) informationBoard.lookup("#informationBoardDone");
        informationBoardNote = (TextArea) informationBoard.lookup("#informationBoardNote");
        informationBoardFavorite = (ImageView) informationBoard.lookup("#informationBoardFavorite");
        categories = (ListView<Category>) root.lookup("#categories");
        categories.setItems(listOfCategories);
        categories.setCellFactory(lv -> new ListCell<Category>() {
            @Override
            public void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    Label title = new Label(item.getTitle());
                    Label size = new Label(item.getList().size() + "");
                    size.textProperty().bind(item.getSize());
                    BorderPane bp = new BorderPane(null, null, size, null, title);
                    setGraphic(bp);
                }
            }
        });
        categories.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Category> observable, Category oldValue, Category newValue) -> {
            if (categories.getSelectionModel().getSelectedIndex() != -1) {
                if (oldValue != null) {

                }
                items.setItems(newValue.getList());
            }
        });

        items = (ListView<Entry>) root.lookup("#items");
        items.setItems(listOfItems);
        items.setCellFactory(new Callback<ListView<Entry>, ListCell<Entry>>() {

            @Override
            public ListCell<Entry> call(ListView<Entry> lv) {
                return new ListCell<Entry>() {

                    @Override
                    public void updateItem(Entry item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {

                            setText(null);
                            setGraphic(null);

                        } else {
                            ImageView iv = new ImageView(new Image(getClass().getResourceAsStream("Images/favorite" + (item.favorite().get() ? "1" : "0") + ".png")));
                            iv.setOnMouseClicked((MouseEvent event) -> {
                                items.getSelectionModel().selectedItemProperty().get().setFavorite(!items.getSelectionModel().selectedItemProperty().get().favorite().get());
                                iv.setImage(new Image(getClass().getResourceAsStream("Images/favorite" + (item.favorite().get() ? "1" : "0") + ".png")));
                                //informationBoardFavorite.setImage(iv.getImage());
                                //iv.imageProperty().bindBidirectional(new Image(getClass().getResourceAsStream("Images/favorite"+(item.favorite.get()?"1":"0")+".png")));
                                event.consume();
                            });
                            iv.setFitHeight(25);
                            iv.setFitWidth(25);
                            String text = item.title().get();
                            setText(null);
                            AnchorPane ap = new AnchorPane();
                            ap.setPrefHeight(40);
                            CheckBox cb = new CheckBox("");
                            cb.setLayoutY(10);
                            cb.setLayoutX(10);
                            cb.selectedProperty().bindBidirectional(item.done());
                            Label label = new Label(text);
                            label.setLayoutX(cb.getLayoutX() + 30);
                            label.setLayoutY(10);
                            label.textProperty().bindBidirectional(item.title());
                            ap.getChildren().addAll(cb, label);
                            BorderPane borderPane = new BorderPane(null, null, iv, null, ap);
                            setGraphic(borderPane);
                        }
                    }
                };
            }
        });
        items.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Entry> observable, Entry oldValue, Entry newValue) -> {
            if (items.getSelectionModel().getSelectedIndex() != -1) {
                if (oldValue != null) {
                    informationBoardText.textProperty().unbindBidirectional(oldValue.title());
                    informationBoardDone.selectedProperty().unbindBidirectional(oldValue.done());
                    informationBoardNote.textProperty().unbindBidirectional(oldValue.note());
                    //informationBoardFavorite.imageProperty().unbindBidirectional(oldValue.favoriteImage());
                }
                informationBoardText.setText(newValue.getTitle());
                informationBoardText.textProperty().bindBidirectional(newValue.title());
                informationBoardText.textProperty().addListener((ObservableValue<? extends String> observable1, String oldValue1, String newValue1) -> {
                    items.getSelectionModel().selectedItemProperty().get().setTitle(newValue1);
                });
                informationBoardDone.setSelected(newValue.done().get());
                informationBoardDone.selectedProperty().bindBidirectional(newValue.done());
                informationBoardNote.setText(newValue.note().get());
                informationBoardNote.textProperty().bindBidirectional(newValue.note());
                informationBoardNote.textProperty().addListener((ObservableValue<? extends String> observable1, String oldValue1, String newValue1) -> {
                    items.getSelectionModel().selectedItemProperty().get().setNote(newValue1);
                });
                //----------------set suitable image on favorite image
                ///newValue.favoriteImage().set(new Image(getClass().getResourceAsStream("Images/favorite" + (newValue.favorite().get() ? "1" : "0") + ".png")));
                //informationBoardFavorite.imageProperty().bindBidirectional(newValue.favoriteImage());
            }
        });
        items.setOnMouseClicked((MouseEvent event) -> {
            if (items.getSelectionModel().getSelectedItem() != null) {
                if (event.getClickCount() == 2) {
                    final Timeline timeline = new Timeline();
                    timeline.setCycleCount(200);
                    KeyFrame kf = new KeyFrame(Duration.millis(1), (ActionEvent event1) -> {
                        if (!informationBoardOpened) {
                            informationBoard.setLayoutX(informationBoard.getLayoutX() - 1.9);
                            middleBox.setPrefWidth(middleBox.getPrefWidth() - 1.9);
                        } else {
                            informationBoard.setLayoutX(informationBoard.getLayoutX() + 1.9);
                            middleBox.setPrefWidth(middleBox.getPrefWidth() + 1.9);
                        }
                    });
                    timeline.getKeyFrames().add(kf);
                    timeline.play();
                    timeline.setOnFinished((ActionEvent event1) -> {
                        informationBoardOpened = !informationBoardOpened;
                    });
                }
            }

        });
        TextField addItemTextField = (TextField) middleBox.lookup("#addItemTextField");
        addItemTextField.setOnAction((ActionEvent event) -> {
            if (addItemTextField.getText().length() > 0) {
                Entry e = new Entry(addItemTextField.getText());
                //listOfItems.add(0, e);
                if (categories.getSelectionModel().getSelectedItem() != null) {
                    categories.getSelectionModel().getSelectedItem().add(e);
                } else {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("No Category to add your Entry");
                    alert.setContentText("First create a category at down left!");
                    alert.showAndWait();

                }
                addItemTextField.setText("");
            }

        });
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        FrameController fc = loader.getController();
        fc.setMainApp(this);
        primaryStage.show();

    }

    public File getEntryFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(Wunderlist.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    public void setEntryFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(Wunderlist.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());
        } else {
            prefs.remove("filePath");
        }
    }

    public void loadEntryFromFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(EntryListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();
            EntryListWrapper wrapper = (EntryListWrapper) um.unmarshal(file);
            listOfItems.clear();
            listOfItems.addAll(wrapper.getEntries());

            setEntryFilePath(file);

        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());
            alert.showAndWait();
        }
    }

    public void saveEntryToFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(EntryListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            EntryListWrapper wrapper = new EntryListWrapper();
            wrapper.setEntries(listOfItems);

            m.marshal(wrapper, file);

            setEntryFilePath(file);
        } catch (Exception e) {
            System.out.println(e);
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

}
