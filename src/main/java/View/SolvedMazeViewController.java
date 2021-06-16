package View;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

/**
 * the maze's stage after the games has been solved by the user
 */
public class SolvedMazeViewController extends Observable implements Initializable{
    public Canvas goalReached;
    public AnchorPane windowSolve;
    public MediaView mediaView;
    private ImageView imageview;
    private boolean soundOn=false;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        goalReached();
//        drawFinalImage();
//        playMusic();
    }


    private void goalReached() {
        String musicFile = "./resources/Audio/SpongebobHooray.mp3";

        //String musicFile ="resources/Audio/Californication.mp3";
        Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        soundOn=true;

        mediaPlayer.setAutoPlay(true);
        mediaView = new MediaView(mediaPlayer);

    }

    private void drawFinalImage() {

        GraphicsContext graphicsContext = goalReached.getGraphicsContext2D();
        //clear the canvas:
        graphicsContext.clearRect(0, 0, goalReached.getWidth(), goalReached.getHeight());
        graphicsContext.setFill(Color.PINK);

        Image HurrayImage = null;
        try {
            HurrayImage = new Image(new FileInputStream("./resources/images/SMILE.jpg"));
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall image file");
        }
        if (HurrayImage == null)
            graphicsContext.fillRect(0, 0, goalReached.getWidth(), goalReached.getHeight());
        else
            graphicsContext.drawImage(HurrayImage, 0, 0, goalReached.getWidth(), goalReached.getHeight());


    }


    public void retryMaze(ActionEvent actionEvent) {
        soundOn=false;
        setChanged();
        notifyObservers("play again");
    }

    public void mainMenu(ActionEvent actionEvent) {
        soundOn=false;
        setChanged();
        notifyObservers("main menu");
    }
}
