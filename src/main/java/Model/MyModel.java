package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import Server.Configurations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

/**
 * the maze's model
 */
public class MyModel extends Observable implements IModel {
    private Maze maze;
    private int playerRow;
    private int playerCol;
    private Solution solution;
    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;
    private final Logger LOG = LogManager.getLogger();


    private final int generatePort = 5400;
    private final int solvePort = 5401;

    public MyModel() {

        //Configurations.setPath("resources/config.properties");
        createServer();
    }

    void createServer() {
        //Initializing servers
        mazeGeneratingServer = new Server(generatePort, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(solvePort, 1000, new ServerStrategySolveSearchProblem());

        //Starting  servers
        solveSearchProblemServer.start();
        LOG.info("solving server started at port : "+solvePort);
        mazeGeneratingServer.start();
        LOG.info("generating server started at port : "+generatePort);
    }



    public void stop() {
        mazeGeneratingServer.stop();
        LOG.info("generating server stopped");
        solveSearchProblemServer.stop();
        LOG.info("solving server stopped");
    }

    @Override
    public void createGame(int rows, int cols) {
        LOG.debug("testing..............");
        generateFromServer(rows, cols);
        if (this.maze != null) {
            setChanged();
            notifyObservers("maze generated");
            // start position:
            movePlayer(maze.getStartPosition().getRowIndex(), maze.getStartPosition().getColumnIndex());
        }
    }

    // helper function to create the generate maze server
    private void generateFromServer(int rows, int cols) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{rows, cols};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[1000000000 /*CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        maze = new Maze(decompressedMaze);
                        Properties prop = Configurations.getInstance();

/*                        InputStream istream = getClass().getResourceAsStream("./resources/config.properties");
                        Properties prop = new Properties();
                        try {
                            prop.load(istream);
                            istream.close();
                        }
                        catch (Exception e) {}*/

                        LOG.info("maze was generated with: " +prop.getProperty("mazeGeneratingAlgorithm")+" algorithm");

                    } catch (Exception e) {
                        LOG.error(e.getMessage());
                    }
                }
            });
            LOG.info("user at: "+InetAddress.getLocalHost()+ " connected to generating server...");
            client.communicateWithServer();

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public Maze getBoard() {
        return maze;
    }

    @Override
    public void updateState(Object o) {
        MovementDirection direction = (MovementDirection)o;
        switch (direction) {
            case BACK -> {
                if (playerRow > 0 && maze.getCellValue(new Position(playerRow - 1, playerCol)) == 0) {
                    movePlayer(playerRow - 1, playerCol);
                    notifyObservers("player moved back");
                    LOG.info("player moved back");
                }
            }
            case FORWARD -> {
                if (playerRow < maze.getRows() - 1 && maze.getCellValue(new Position(playerRow + 1, playerCol)) == 0){
                    movePlayer(playerRow + 1, playerCol);
                    notifyObservers("player moved forward");
                    LOG.info("player moved forward");
                }
            }
            case LEFT -> {
                if (playerCol > 0 && maze.getCellValue(new Position(playerRow, playerCol - 1)) == 0) {
                    movePlayer(playerRow, playerCol - 1);
                    notifyObservers("player moved left");
                    LOG.info("player moved left");
                }
            }
            case RIGHT -> {
                if (playerCol < maze.getColumns() - 1 && maze.getCellValue(new Position(playerRow, playerCol + 1)) == 0) {
                    movePlayer(playerRow, playerCol + 1);
                    notifyObservers("player moved right");
                    LOG.info("player moved right");
                }
            }
            case FORWARD_LEFT -> {
                if (playerRow > 0 && playerCol > 0 && maze.getCellValue(new Position(playerRow, playerCol + 1)) == 0)
                    movePlayer(playerRow - 1, playerCol - 1);
                    notifyObservers("player moved up left");
                LOG.info("player moved up left");
            }
            case FORWARD_RIGHT -> {
                if (playerRow > 0 && playerCol < maze.getColumns() - 1 && maze.getCellValue(new Position(playerRow, playerCol + 1)) == 0)
                    movePlayer(playerRow - 1, playerCol + 1);
                    notifyObservers("player moved up right");
               LOG.info("player moved up right");
            }
            case BACK_LEFT -> {
                if (playerCol > 0 && playerRow < maze.getRows() - 1 && maze.getCellValue(new Position(playerRow, playerCol + 1)) == 0)
                    movePlayer(playerRow + 1, playerCol - 1);
                    notifyObservers("player moved down left");
                LOG.info("player moved down left");
            }
            case BACK_RIGHT -> {
                if (playerCol < maze.getColumns() - 1 && playerRow < maze.getRows() - 1 && maze.getCellValue(new Position(playerRow, playerCol + 1)) == 0)
                    movePlayer(playerRow + 1, playerCol + 1);
                    notifyObservers("player moved down right");
                LOG.info("player moved down right");
            }
        }

        if(playerRow==maze.getGoalPosition().getRowIndex() && playerCol==maze.getGoalPosition().getColumnIndex()){

            setChanged();
            notifyObservers("maze solved by user");
            LOG.info("maze solved by user");
        }
    }

    // helper function to the update state
    private void movePlayer(int row, int col) {
        this.playerRow = row;
        this.playerCol = col;
        LOG.info("player was moved to row: "+row+"  and to column: "+col);
        setChanged();
//        notifyObservers("player moved");
    }

    @Override
    public MazeState getState() {
        return new MazeState(playerRow, playerCol);
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    @Override
    public void solveGame() {
        try {
            Client client= new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        toServer.flush();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        solution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        Properties prop = Configurations.getInstance();
                        LOG.info("user at: "+InetAddress.getLocalHost()+ " got from solving maze server a solution solved by: " +prop.getProperty("mazeSearchingAlgorithm"));
                    } catch (Exception e) {
                        LOG.error(e.getMessage());
                    }
                }
            });
            LOG.info("user at: "+InetAddress.getLocalHost()+ " connected to solving server...");
            client.communicateWithServer();

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        setChanged();
        notifyObservers("maze solved");
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    @Override
    public void restart() {
        this.maze = null;
        this.solution = null;
    }

    @Override
    public boolean saveGame(String FileName) {

        try {
            OutputStream out = new MyCompressorOutputStream(new FileOutputStream(FileName));
            out.write(maze.toByteArray());
            out.flush();
            out.close();
            LOG.info("game was saved");
            return true;
        } catch (IOException var8) {
            LOG.debug("failed to save maze.");
        }
        return false;
    }

    @Override
    public boolean loadGame(String fileName) {
        byte[] savedMazeBytes = new byte[500];
        boolean stat = false;
        try {
            InputStream in = new MyDecompressorInputStream(new FileInputStream(fileName));
            //savedMazeBytes = new byte[0];
            in.read(savedMazeBytes);
            in.close();
            stat = true;
        } catch (IOException var7) {
            LOG.debug("failed to read maze bytes.");
        }

        if(!stat)return false;

        Maze loadedMaze = null;
        try{
            loadedMaze = new Maze(savedMazeBytes);
            this.maze = loadedMaze;
            LOG.info("game was loaded");
            return true;
        }
        catch (Exception e){
           LOG.debug("failed to load maze.");
        }

        return false;
    }

    @Override
    public void setProperties(String[] properties) {
        if(properties.length!=3){
            setChanged();
            notifyObservers("properties set failed");
            return;
        }

        //stop();

        Object stat1= null,stat2= null,stat3 = null;
        Properties prop = Configurations.getInstance();
        stat1=prop.setProperty("threadPoolSize",properties[0]);
        stat2=prop.setProperty("mazeGeneratingAlgorithm",properties[1]);
        stat3=prop.setProperty("mazeSearchingAlgorithm",properties[2]);

        if(stat1==null||stat2==null||stat3==null){
            setChanged();
            notifyObservers("properties set failed");
            //create server?
            return;
        }

        solveSearchProblemServer.restartServer();
        mazeGeneratingServer.restartServer();

        LOG.info("new properties were set");
        //refreshThreadPoolSize();
        setChanged();
        notifyObservers("properties set succeed");
    }
}
