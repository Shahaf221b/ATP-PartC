package View;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

/**
 * the main stage for all the games
 */
public class GameViewController implements Initializable, Observer{

    private static Stage curStage;

/*    private void stageSizeChageListener(Stage stage) {
        stage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                System.out.println("Width changed!!");
            }
        });

        stage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                System.out.println("Height changed!!");
            }
        });
    }*/


    @Override
    public void update(Observable o, Object arg) {

        String change = (String) arg;
        switch (change){
            case "retry" -> {
                try {
                    curStage.close();
                    startMazeGame(new ActionEvent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            case "main menu" -> returnToMain();
            default -> System.out.println("Not implemented change: " + change);
        }
    }

    private void returnToMain() {
        curStage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }

    public void startMazeGame(ActionEvent actionEvent) throws IOException {


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("MazeView.fxml"));

        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        curStage = stage;
        stage.setTitle("Maze Game");

        stage.setScene(new Scene(root1,700,500));
        stage.show();

        IModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);
        MyViewController view = fxmlLoader.getController();
        view.setViewModel(viewModel);
        view.addObserver(this);


    }}
