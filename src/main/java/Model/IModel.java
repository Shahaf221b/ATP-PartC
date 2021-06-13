package Model;

import algorithms.mazeGenerators.IBoardGame;
import algorithms.search.AState;
import algorithms.search.Solution;

import java.util.Observer;

/**
 * interface for the bora game's models
 */
public interface IModel {
    void createGame(int rows, int cols);
    IBoardGame getBoard();
    void updateState(Object object);
    AState getState();

    void solveGame();
    Solution getSolution();

    void assignObserver(Observer o);
    void stop(); // end all processes


    void restart();//TODO: needed here?

    boolean saveGame(String FileName);

    boolean loadGame(String fileName);

    void setProperties(String[] properties);
}
