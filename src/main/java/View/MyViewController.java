package View;

import ViewModel.MyViewModel;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;


/**
 * implementing the board game interface and creating the maze
 */
public class MyViewController extends Observable implements Initializable, Observer, IView {
    public MyViewModel viewModel;
    public SolvedMazeViewController solvedMazeViewController;
    public mazePropertiesController mazePropertiesController;
    public MenuItem fileSave;
    public Button solveMazeButton;
    public MediaView mediaView;
    public Button soundButton;
    public ScrollPane scrollPane;

    private boolean isSound = false;

    private Stage currentStage;
    private Stage propStage;

    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;
    public Label playerRow;
    public Label playerCol;


    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();


    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addObserver(this);

    }

    public void setSolvedMazeViewController(SolvedMazeViewController solvedMazeViewController) {
        this.solvedMazeViewController = solvedMazeViewController;
        this.solvedMazeViewController.addObserver(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerRow.textProperty().bind(updatePlayerRow);
        playerCol.textProperty().bind(updatePlayerCol);
    }

    @Override
    public void createGame(ActionEvent actionEvent) {
        int rows = Integer.parseInt(textField_mazeRows.getText());
        int cols = Integer.parseInt(textField_mazeColumns.getText());

        viewModel.createGame(rows, cols);
        solveMazeButton.setDisable(false);
        fileSave.setDisable(false);

        if(!isSound){
            soundON();
        }
        soundButton.setDisable(false);

    }

    public void getAbout(ActionEvent actionEvent) {
        Alert aboutInfo = new Alert(Alert.AlertType.INFORMATION);
        aboutInfo.setContentText("This Maze game was build by Chen Mordehai and Shahaf Erez.\n" +
                " \nEach maze in this game is build by a maze-generator server and a solving-generator server." +
                " \nIt is possible to set thw algorithm that will generate and solve the maze." +
                "\nThe project was created at the advanced programming course at BGU university");
        aboutInfo.setTitle("About the Maze Game");
        aboutInfo.setHeight(600);
        aboutInfo.setHeaderText("About");
        aboutInfo.show();
    }

    @Override
    public void solveGame(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Maze was solved");
        alert.show();
        viewModel.solveGame();
    }

    // TODO
    public void openFile(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open maze");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fc.setInitialDirectory(new File("./resources"));
        File chosen = fc.showOpenDialog(null);
        //...
    }

    public void keyPressed(KeyEvent keyEvent) {
        viewModel.changeState(keyEvent);
        keyEvent.consume();
//        if (viewModel.isGoal()){
//            goalReached();
//        }
    }

    @Override
    public void setState(AState state) {
        int row = ((MazeState) state).getPosition().getRowIndex();
        int col = ((MazeState) state).getPosition().getColumnIndex();
        mazeDisplayer.setState(new MazeState(row, col));
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
    }

    public void setStateWithOption(AState state, int option) {
        int row = ((MazeState) state).getPosition().getRowIndex();
        int col = ((MazeState) state).getPosition().getColumnIndex();
        mazeDisplayer.setStateOptions(new MazeState(row, col), option);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
    }


    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();




    }

    public void mousePressed(MouseEvent e) {
        //if(handleDrag){
        viewModel.mousePressed(e);
        //}
    }


    public void mouseDragged(MouseEvent event) {
//        if(!handleDrag){
//            return;
//        }
        double cellHeight = mazeDisplayer.getCellMeasurement()[0];
        double cellWidth = mazeDisplayer.getCellMeasurement()[1];

        try {
            Thread.sleep(290);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        viewModel.changeMouseState(event, cellHeight, cellWidth);

    }

    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;
        int row = viewModel.getState().getPosition().getRowIndex();
        int col = viewModel.getState().getPosition().getColumnIndex();
        switch (change) {
            case "maze generated" -> mazeDisplayer.drawGame(viewModel.getGame());
            case "player moved forward" -> setStateWithOption(new MazeState(row, col), 2);
            case "player moved left", "player moved up left", "player moved down left" -> setStateWithOption(new MazeState(row, col), 4);
            case "player moved right", "player moved up right", "player moved down right" -> setStateWithOption(new MazeState(row, col), 6);
            case "player moved back" -> setStateWithOption(new MazeState(row, col), 8);
            case "maze solved" -> mazeDisplayer.setSolution(viewModel.getSolution());
            case "maze solved by user" -> joodJob();
            case "play again" -> retry();
            case "main menu" -> mainMenu();
            case "newProperties"-> viewModel.setProperties(mazePropertiesController.getProperties());
            case "properties set failed" -> propSetFailed();
            case "properties set succeed" -> propSetSucceed();
            default -> System.out.println("Not implemented change: " + change);
        }
    }

    private void propSetSucceed() {
        propStage.close();
        stopSound();
        retry();
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("new properties were saved");
        a.show();

    }

    private void propSetFailed() {
        propStage.close();
        stopSound();
        retry();
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText("new properties were not saved!\nTry again.");
        a.show();
    }


    public void exitAll(ActionEvent actionEvent) {
        viewModel.exitAll();

        Platform.exit();//TODO: needed?
    }

    public void zooming(ScrollEvent scrollEvent) {
        double delta = scrollEvent.getDeltaY();
        scrollPane.setDisable(true);
        mazeDisplayer.zooming(delta);
        scrollPane.setDisable(false);
    }


    private void retry() {
        if(currentStage!=null){
            currentStage.close();
        }
        viewModel.restart();
        setChanged();
        notifyObservers("retry");
    }

    private void mainMenu() {
        currentStage.close();
        setChanged();
        notifyObservers("main menu");
    }


    private void joodJob() {

        mediaView.getMediaPlayer().stop();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("MazeSolveView.fxml"));
            Parent root2 = (Parent) fxmlLoader.load();
            solvedMazeViewController = fxmlLoader.getController();
            setSolvedMazeViewController(solvedMazeViewController);
            Stage stage = new Stage();
            stage.setTitle("End of Game");


            stage.setScene(new Scene(root2));
            currentStage = stage;
            stage.show();
            //goalReached();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    public void saveMaze(ActionEvent actionEvent) {


        Maze curMaze = (Maze) viewModel.getGame();
        if (curMaze == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("there is no maze to save.");
            alert.show();
            return;
        }

        TextInputDialog td = new TextInputDialog("myMaze");
        td.setHeaderText("How would you like to call your maze?\nEnter file name:");
        td.setWidth(300);


        Optional<String> mazeFileName = td.showAndWait();
        mazeFileName.ifPresent(string -> {
            boolean stat = viewModel.saveGame(string);
            if (!stat) {
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                alert2.setContentText("Oops! Something went wrong...\nMaze wasn't saved.");
                alert2.show();
                return;
            }
            Alert status = new Alert(Alert.AlertType.CONFIRMATION);
            status.setTitle("save status");
            status.setContentText("current maze was saved successfully");
            status.setHeaderText("Maze was saved");
            status.show();
        });
    }


    public void loadNewMaze(ActionEvent actionEvent) {
        TextInputDialog td = new TextInputDialog("path to file");
        td.setHeaderText("enter path to file:");
        //Button button = new Button("Load Maze");

        Optional<String> path = td.showAndWait();
        path.ifPresent(string -> {
            boolean stat = viewModel.loadGame(string);
            if (!stat) {
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                alert2.setContentText("Oops! Something went wrong...\nMaze path seems wrong.\n Try again.");
                alert2.show();
                return;
            }
            mazeDisplayer.drawGame(viewModel.getGame());
            solveMazeButton.setDisable(false);
            fileSave.setDisable(false);

            soundON();
            soundButton.setDisable(false);
        });

    }

    public void getProperties(ActionEvent actionEvent) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("mazeProperties.fxml"));
            Parent root2 = (Parent) fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Properties");

            stage.setScene(new Scene(root2));
            this.mazePropertiesController = fxmlLoader.getController();
            this.mazePropertiesController.addObserver(this);
            propStage = stage;
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void getHelp(ActionEvent actionEvent) {

        Alert helpInfo = new Alert(Alert.AlertType.INFORMATION);
        helpInfo.setContentText("Help SpongeBob to get to his job at the \"krusty krab\" before Mr. Krabs will get mad!" +
                "\nUse your numpad, your arrows or you mouse to move through the maze:" +
                "\n\nforward= 8, back= 2, left= 4, right= 6." +
                "\nYou can also move diagonally when available:" +
                "\nback-left= 7, back-right= 9, forward-left= 1, forward-right= 3." +
                "\nTry getting to SpongeBob's goal as fast as you can with minimum moves!\n \nGood Luck!");
        helpInfo.setTitle("Help Information");
        helpInfo.setHeight(600);
        helpInfo.setHeaderText("Rules & Goals");
        helpInfo.show();
    }

    public void exitMazeGame(ActionEvent actionEvent) {
        exitAll(actionEvent);
    }


    @Override
    public void loadGame(ActionEvent actionEvent) {
        this.loadNewMaze(actionEvent);
    }

    @Override
    public void getGameProperties(ActionEvent actionEvent) {
        this.getProperties(actionEvent);
    }

    @Override
    public void exitGame(ActionEvent actionEvent) {
        this.exitMazeGame(actionEvent);
    }

    @Override
    public void saveGame(ActionEvent actionEvent) {
        this.saveMaze(actionEvent);
    }


    @Override
    public void newGame(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("New Maze was generated !");

        this.createGame(actionEvent); //TODO: check
        alert.show();
    }


    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }

    public void setUpdatePlayerRow(int updatePlayerRow) {
        this.updatePlayerRow.set(updatePlayerRow + "");
    }

    public String getUpdatePlayerCol() {
        return updatePlayerCol.get();
    }

    public void setUpdatePlayerCol(int updatePlayerCol) {
        this.updatePlayerCol.set(updatePlayerCol + "");
    }

    private void soundON() {

        String musicFile ="src/main/resources/Audio/backgroundMusic.mp3";
        Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);

        mediaPlayer.setAutoPlay(true);
        mediaView = new MediaView(mediaPlayer);
        isSound = true;

    }

    public void changeSound(ActionEvent actionEvent) {
        if(isSound){
            mediaView.getMediaPlayer().stop();
            isSound=false;
            soundButton.setText("Sound On");
        }
        else{
            mediaView.getMediaPlayer().play();
            isSound=true;
            soundButton.setText("Sound Off");
        }

    }

    public void stopSound() {
        if(isSound){
            mediaView.getMediaPlayer().stop();
            isSound=false;
        }
    }


}
