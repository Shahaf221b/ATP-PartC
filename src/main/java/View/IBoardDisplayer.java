package View;

import algorithms.mazeGenerators.IBoardGame;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.scene.input.MouseEvent;

/**
 * interface for displaying a the game's board
 */
public interface IBoardDisplayer {

    void setState(AState state);
    void setSolution(Solution solution);
    void drawGame(IBoardGame game);


}
