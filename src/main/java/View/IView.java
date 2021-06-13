package View;

import algorithms.search.AState;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public interface IView {

    /**
     * interface for creating a board game
     */
    void createGame(ActionEvent actionEvent);
    void solveGame(ActionEvent actionEvent);
    void keyPressed(KeyEvent keyEvent);
    void setState(AState state);
    void mouseClicked(MouseEvent mouseEvent);

    //menu options
    void newGame(ActionEvent actionEvent);
    void saveGame(ActionEvent actionEvent);
    void loadGame(ActionEvent actionEvent);
    void getGameProperties(ActionEvent actionEvent);
    void exitGame(ActionEvent actionEvent);
    void getHelp(ActionEvent actionEvent);
    void getAbout(ActionEvent actionEvent);

}
