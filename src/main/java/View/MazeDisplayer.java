package View;

import algorithms.mazeGenerators.IBoardGame;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * displaying the maze's board
 */
public class MazeDisplayer extends Canvas implements IBoardDisplayer {

    GraphicsContext graphicsContext;
    Image playerImage;
    private double cellHeight;
    private double cellWidth;
    int curOption = -1;
    int[] prevOption = {-1, 0};


    private ImageView imageview;
    private Maze maze;
    private Solution solution;
    // player position:
    private int playerRow = 0;
    private int playerCol = 0;
    // wall and player images:
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNameWall1 = new SimpleStringProperty();
    StringProperty imageFileNameWall2 = new SimpleStringProperty();

    StringProperty imageFileNamePlayerFront = new SimpleStringProperty();
    StringProperty imageFileNamePlayerFront2 = new SimpleStringProperty();

    StringProperty imageFileNamePlayerBack = new SimpleStringProperty();
    StringProperty imageFileNamePlayerBack2 = new SimpleStringProperty();

    StringProperty imageFileNamePlayerLeft1 = new SimpleStringProperty();
    StringProperty imageFileNamePlayerLeft2 = new SimpleStringProperty();
    StringProperty imageFileNamePlayerLeft3 = new SimpleStringProperty();

    StringProperty imageFileNamePlayerRight1 = new SimpleStringProperty();
    StringProperty imageFileNamePlayerRight2 = new SimpleStringProperty();
    StringProperty imageFileNamePlayerRight3 = new SimpleStringProperty();

    StringProperty imageFileNameGoal = new SimpleStringProperty();
    StringProperty imageFileNameFootsteps = new SimpleStringProperty();


    public int getPlayerRow() {
        return playerRow;
    }

    public int getPlayerCol() {
        return playerCol;
    }

    @Override
    public void setState(AState state) {
        MazeState mazeState = (MazeState) state;
        this.playerRow = mazeState.getPosition().getRowIndex();
        this.playerCol = mazeState.getPosition().getColumnIndex();
        draw();
    }

    public double[] getCellMeasurement() {
        return new double[]{cellHeight, cellWidth};
    }

    public void setStateOptions(AState state, int option) {
        curOption = option;
        setState(state);
    }

    @Override
    public void setSolution(Solution solution) {
        this.solution = solution;
        draw();
        this.solution = null;
    }


    @Override
    public void drawGame(IBoardGame game) {
        this.maze = (Maze) game;

        playerRow = maze.getStartPosition().getRowIndex();
        playerCol = maze.getStartPosition().getColumnIndex();

        draw();
    }

    private void draw() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();

            int rows = maze.getRows();
            int cols = maze.getColumns();

            cellHeight = canvasHeight / rows;
            cellWidth = canvasWidth / cols;

            graphicsContext = getGraphicsContext2D();
            //clear the canvas:
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);

            if (prevOption[0] == curOption) {
                prevOption[1]++;
            } else {
                prevOption[1] = 0;
                prevOption[0] = curOption;
            }

            drawMazeWalls(rows, cols);
            if (solution != null)
                drawSolution();
            drawPlayer();
            drawGoal();
        }

        curOption = -1;

    }

    private void drawSolution() {

        Image footPrintImage = null;
        try {
            footPrintImage = new Image(new FileInputStream(getImageFileNameFootsteps()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no image file");
        }

        ArrayList<AState> solutionPath = solution.getSolutionPath();
        graphicsContext.setFill(Color.BLUE);

        for (int i = 0; i < solution.getSolutionPath().size(); i++) {
            Position position = ((MazeState) solutionPath.get(i)).getPosition();
            if (footPrintImage == null)
                graphicsContext.fillRect(cellWidth * position.getColumnIndex(), cellHeight * position.getRowIndex(), cellWidth, cellHeight);
            else
                graphicsContext.drawImage(footPrintImage, cellWidth * position.getColumnIndex(), cellHeight * position.getRowIndex(), cellWidth, cellHeight);
        }

    }

    private void drawMazeWalls(int rows, int cols) {
        graphicsContext.setFill(Color.RED);

        Image wallImage = null;
        try {
            wallImage = new Image(new FileInputStream(getImageFileNameWall()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall image file");
        }

        Image wallImage2 = null;
        try {
            wallImage2 = new Image(new FileInputStream(getImageFileNameWall1()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall image file");
        }
        Image wallImage3 = null;
        try {
            wallImage3 = new Image(new FileInputStream(getImageFileNameWall2()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall image file");
        }


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (maze.getCellValue(new Position(i, j)) == 1) {

                    Random rand = new Random();
                    int val = rand.nextInt(3);

                    //if it is a wall:
                    double x = j * cellWidth;
                    double y = i * cellHeight;
                    if ((i % 3 == 2 && j % 2 == 1) || j % 6 == 4 || i % 10 == 5) {
                        if (wallImage2 == null)
                            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                        else
                            graphicsContext.drawImage(wallImage2, x, y, cellWidth, cellHeight);

                    } else if ((i % 7 == 4 && j % 5 == 1) || (i % 4 == 1 && j % 6 == 3)) {
                        if (wallImage2 == null)
                            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                        else
                            graphicsContext.drawImage(wallImage3, x, y, cellWidth, cellHeight);
                    } else {
                        if (wallImage == null)
                            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                        else
                            graphicsContext.drawImage(wallImage, x, y, cellWidth, cellHeight);
                    }
                }
            }
        }
    }

    private void drawPlayer() {
        double x = getPlayerCol() * cellWidth;
        double y = getPlayerRow() * cellHeight;
        graphicsContext.setFill(Color.GREEN);
        playerImage = null;

        // down
        if (curOption == -1 || curOption == 2) { // forward
            if (prevOption[1] % 2 == 0) {
                setPlayerImage(getImageFileNamePlayerFront());
            } else {
                setPlayerImage(getImageFileNamePlayerFront2());
            }


        } else if (curOption == 4) { // left
            if (prevOption[0] != 4 || prevOption[1] % 4 == 0 || prevOption[1] % 4 == 2) {
                setPlayerImage(getImageFileNamePlayerLeft2());
            } else if (prevOption[1] % 4 == 1) {
                setPlayerImage(getImageFileNamePlayerLeft1());
            } else if (prevOption[1] % 4 == 3) {
                setPlayerImage(getImageFileNamePlayerLeft3());
            }

        } else if (curOption == 6) { // right
            if (prevOption[0] != 6 || prevOption[1] % 4 == 0) {
                setPlayerImage(getImageFileNamePlayerRight1());
            } else if (prevOption[1] % 4 == 1 || prevOption[1] % 4 == 3) {
                setPlayerImage(getImageFileNamePlayerRight2());
            } else if (prevOption[1] % 4 == 2) {
                setPlayerImage(getImageFileNamePlayerRight3());
            }

        } else if (curOption == 8) { // back
            if (prevOption[1] % 2 == 0) {
                setPlayerImage(getImageFileNamePlayerBack());
            } else {
                setPlayerImage(getImageFileNamePlayerBack2());
            }
        }

        if (playerImage == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(playerImage, x, y, cellWidth, cellHeight);
    }

    private void setPlayerImage(String imageFileNamePlayerFront) {
        try {
            playerImage = new Image(new FileInputStream(imageFileNamePlayerFront));

        } catch (FileNotFoundException e) {
            System.out.println("There is no image file");
        }
    }

    private void drawGoal() {
        double goalX = maze.getGoalPosition().getRowIndex() * cellWidth;
        double goalY = maze.getGoalPosition().getColumnIndex() * cellHeight;
        graphicsContext.setFill(Color.GREEN);

        Image goalImage = null;

        try {
            goalImage = new Image(new FileInputStream(getImageFileNameGoal()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no goal image file");
        }
        if (goalImage == null)
            graphicsContext.fillRect(goalY, goalX, cellWidth, cellHeight);
        else {
            graphicsContext.drawImage(goalImage, goalY, goalX, cellWidth, cellHeight);
        }

    }

    public void zooming(double delta) {
        setHeight(getHeight() + delta);
        setWidth(getWidth() + delta);
        draw();
    }


    /* images setters and getters */
    public String getImageFileNamePlayerLeft2() {
        return imageFileNamePlayerLeft2.get();
    }

    public StringProperty imageFileNamePlayerLeft2Property() {
        return imageFileNamePlayerLeft2;
    }

    public void setImageFileNamePlayerLeft2(String imageFileNamePlayerLeft2) {
        this.imageFileNamePlayerLeft2.set(imageFileNamePlayerLeft2);
    }


    public String getImageFileNamePlayerRight1() {
        return imageFileNamePlayerRight1.get();
    }

    public StringProperty imageFileNamePlayerRight1Property() {
        return imageFileNamePlayerRight1;
    }

    public void setImageFileNamePlayerRight1(String imageFileNamePlayerRight1) {
        this.imageFileNamePlayerRight1.set(imageFileNamePlayerRight1);
    }

    public String getImageFileNamePlayerBack() {
        return imageFileNamePlayerBack.get();
    }

    public StringProperty imageFileNamePlayerBackProperty() {
        return imageFileNamePlayerBack;
    }

    public void setImageFileNamePlayerBack(String imageFileNamePlayerBack) {
        this.imageFileNamePlayerBack.set(imageFileNamePlayerBack);
    }

    public String getImageFileNamePlayerLeft1() {
        return imageFileNamePlayerLeft1.get();
    }

    public StringProperty imageFileNamePlayerLeft1Property() {
        return imageFileNamePlayerLeft1;
    }

    public void setImageFileNamePlayerLeft1(String imageFileNamePlayerLeft1) {
        this.imageFileNamePlayerLeft1.set(imageFileNamePlayerLeft1);
    }

    public StringProperty imageFileNameGoalProperty() {
        return imageFileNameGoal;
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.imageFileNameGoal.set(imageFileNameGoal);
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public String getImageFileNameGoal() {
        return imageFileNameGoal.get();
    }

    public String imageFileNameWallProperty() {
        return imageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNamePlayerFront() {
        return imageFileNamePlayerFront.get();
    }

    public String imageFileNamePlayerFrontProperty() {
        return imageFileNamePlayerFront.get();
    }

    public void setImageFileNamePlayerFront(String imageFileNamePlayerFront) {
        this.imageFileNamePlayerFront.set(imageFileNamePlayerFront);
    }

    public String getImageFileNamePlayerLeft3() {
        return imageFileNamePlayerLeft3.get();
    }

    public StringProperty imageFileNamePlayerLeft3Property() {
        return imageFileNamePlayerLeft3;
    }

    public void setImageFileNamePlayerLeft3(String imageFileNamePlayerLeft3) {
        this.imageFileNamePlayerLeft3.set(imageFileNamePlayerLeft3);
    }

    public String getImageFileNamePlayerRight2() {
        return imageFileNamePlayerRight2.get();
    }

    public StringProperty imageFileNamePlayerRight2Property() {
        return imageFileNamePlayerRight2;
    }

    public void setImageFileNamePlayerRight2(String imageFileNamePlayerRight2) {
        this.imageFileNamePlayerRight2.set(imageFileNamePlayerRight2);
    }

    public String getImageFileNamePlayerRight3() {
        return imageFileNamePlayerRight3.get();
    }

    public StringProperty imageFileNamePlayerRight3Property() {
        return imageFileNamePlayerRight3;
    }

    public void setImageFileNamePlayerRight3(String imageFileNamePlayerRight3) {
        this.imageFileNamePlayerRight3.set(imageFileNamePlayerRight3);
    }

    public String getImageFileNamePlayerFront2() {
        return imageFileNamePlayerFront2.get();
    }

    public StringProperty imageFileNamePlayerFront2Property() {
        return imageFileNamePlayerFront2;
    }

    public void setImageFileNamePlayerFront2(String imageFileNamePlayerFront2) {
        this.imageFileNamePlayerFront2.set(imageFileNamePlayerFront2);
    }

    public String getImageFileNamePlayerBack2() {
        return imageFileNamePlayerBack2.get();
    }

    public StringProperty imageFileNamePlayerBack2Property() {
        return imageFileNamePlayerBack2;
    }

    public void setImageFileNamePlayerBack2(String imageFileNamePlayerBack2) {
        this.imageFileNamePlayerBack2.set(imageFileNamePlayerBack2);
    }

    public String getImageFileNameWall2() {
        return imageFileNameWall2.get();
    }

    public StringProperty imageFileNameWall2Property() {
        return imageFileNameWall2;
    }

    public void setImageFileNameWall2(String imageFileNameWall2) {
        this.imageFileNameWall2.set(imageFileNameWall2);
    }


    public String getImageFileNameWall1() {
        return imageFileNameWall1.get();
    }

    public StringProperty imageFileNameWall1Property() {
        return imageFileNameWall1;
    }

    public void setImageFileNameWall1(String imageFileNameWall1) {
        this.imageFileNameWall1.set(imageFileNameWall1);
    }

    public String getImageFileNameFootsteps() {
        return imageFileNameFootsteps.get();
    }

    public StringProperty imageFileNameFootstepsProperty() {
        return imageFileNameFootsteps;
    }

    public void setImageFileNameFootsteps(String imageFileNameFootsteps) {
        this.imageFileNameFootsteps.set(imageFileNameFootsteps);
    }

}


