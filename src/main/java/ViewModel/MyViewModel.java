package ViewModel;

import Model.IModel;
import Model.MovementDirection;
import algorithms.mazeGenerators.IBoardGame;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer, IViewModel {
    private IModel model;
    private double curXCoordinate;
    private double curYCoordinate;

    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this); //Observe the Model for it's changes
    }


    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    @Override
    public IBoardGame getGame() {
        return model.getBoard();
    }

    /*
    public int getPlayerRow(){
        MazeState mazeState = (MazeState) model.getState();
        return mazeState.getPosition().getRowIndex();
    }

    public int getPlayerCol(){
        MazeState mazeState = (MazeState) model.getState();
        return mazeState.getPosition().getColumnIndex();
    }

     */


    public MazeState getState() {
        return (MazeState) model.getState();
    }

    @Override
    public Solution getSolution() {
        return model.getSolution();
    }

    @Override
    public void createGame(int rows, int cols) {
        model.createGame(rows, cols);
    }

    public void mousePressed(MouseEvent e) {
        curXCoordinate = e.getX();
        curYCoordinate = e.getY();
    }

    public void changeMouseState(MouseEvent event, double height, double width) {

        if (Math.abs(curXCoordinate - event.getX()) > 0.3 * width || Math.abs(curYCoordinate - event.getY()) > 0.3 * height) {
            if (Math.abs(curXCoordinate - event.getX()) < width) { // no movement to the sides
                if (event.getY() > curYCoordinate) { // forward
                    model.updateState(MovementDirection.FORWARD);
                } else {
                    model.updateState(MovementDirection.BACK);
                }
            } else if (Math.abs(curXCoordinate - event.getX()) > width) { // movement to the sides
                // checking for diagonal movement
                if (Math.abs(curYCoordinate - event.getY()) < height) { // no movement up or down
                    if (event.getX() > curXCoordinate) { // right
                        model.updateState(MovementDirection.RIGHT);
                    } else {
                        model.updateState(MovementDirection.LEFT);
                    }
                } else { // diagonal movement
                    if (event.getY() > curYCoordinate) { // forward
                        if (curXCoordinate < event.getX()) { // right
                            model.updateState(MovementDirection.FORWARD_RIGHT);
                        } else {
                            model.updateState(MovementDirection.FORWARD_LEFT);
                        }
                    } else { // back
                        if (curXCoordinate < event.getX()) { // right
                            model.updateState(MovementDirection.BACK_RIGHT);
                        } else {
                            model.updateState(MovementDirection.BACK_LEFT);
                        }
                    }
                }
            }
        }
        curXCoordinate = event.getX();
        curYCoordinate = event.getY();
    }


    @Override
    public void changeState(KeyEvent keyEvent) {
        MovementDirection direction;
        switch (keyEvent.getCode()) {
            case UP, NUMPAD8 -> direction = MovementDirection.BACK;
            case DOWN, NUMPAD2 -> direction = MovementDirection.FORWARD;
            case RIGHT, NUMPAD6 -> direction = MovementDirection.RIGHT;
            case LEFT, NUMPAD4 -> direction = MovementDirection.LEFT;
            case NUMPAD7 -> direction = MovementDirection.FORWARD_LEFT;
            case NUMPAD9 -> direction = MovementDirection.FORWARD_RIGHT;
            case NUMPAD1 -> direction = MovementDirection.BACK_LEFT;
            case NUMPAD3 -> direction = MovementDirection.BACK_RIGHT;
            default -> {
                // no need to move the player...
                return;
            }
        }
        model.updateState(direction);
    }

    @Override
    public void solveGame() {
        model.solveGame();
    }

    @Override
    public void exitAll() {
        model.stop();
    }

    public void restart() {
        model.restart();
    }

    public boolean saveGame(String mazeFileName) {

        return model.saveGame(mazeFileName);
    }

    @Override
    public boolean loadGame(String FileName) {
        return model.loadGame(FileName);
    }

    @Override
    public void setProperties(String[] properties) {
        model.setProperties(properties);
    }


}
