package ViewModel;

import algorithms.mazeGenerators.IBoardGame;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;

public interface IViewModel {
    IBoardGame getGame();
    Solution getSolution();
    void changeState(KeyEvent keyEvent);
    AState getState();
    void solveGame();
    void exitAll();
    void createGame(int rows, int cols);

    boolean saveGame(String FileName);

    boolean loadGame(String FileName);

    void setProperties(String[] properties);
}
