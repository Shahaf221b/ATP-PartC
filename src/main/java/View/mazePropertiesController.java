package View;

import Server.Configurations;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Observable;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * setting the maze's properties option
 */
public class mazePropertiesController extends Observable implements Initializable {

    public TextField textField_poolSize;
    public ChoiceBox mazeGeneratingAlgoritmMenu;
    public ChoiceBox mazeSearchingAlgoMenu;

    private String poolSize;
    private String mazeGeneratingAlgorithm;
    private String mazeSearchingAlgorithm;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Properties prop = Configurations.getInstance();
        String curGA = prop.getProperty("mazeGeneratingAlgorithm");
        mazeGeneratingAlgoritmMenu.setValue("current: "+ curGA);
        String curSA = prop.getProperty("mazeSearchingAlgorithm");
        mazeSearchingAlgoMenu.setValue("current: "+curSA);
        String curPool = prop.getProperty("threadPoolSize");
        textField_poolSize.setText(curPool);

        mazeGeneratingAlgoritmMenu.getItems().addAll("MyMazeGenerator","SimpleMazeGenerator","EmptyMazeGenerator");
        mazeSearchingAlgoMenu.getItems().addAll("BestFirstSearch","BreadthFirstSearch","DepthFirstSearch");

    }

    public void setProperties(ActionEvent actionEvent) {


        Properties prop = Configurations.getInstance();
        String poolSize, mazeGeneratingAlgorithm, mazeSearchingAlgorithm;
        poolSize = textField_poolSize.getText();
        if(poolSize==null ){
            poolSize = prop.getProperty("threadPoolSize");
        }
        mazeGeneratingAlgorithm = (String) mazeGeneratingAlgoritmMenu.getValue();
        if(mazeGeneratingAlgorithm==null){
            mazeGeneratingAlgorithm = prop.getProperty("mazeGeneratingAlgorithm");
        }
        mazeSearchingAlgorithm = (String) mazeSearchingAlgoMenu.getValue();
        if(mazeSearchingAlgorithm==null){
            mazeSearchingAlgorithm = prop.getProperty("mazeSearchingAlgorithm");
        }
        this.poolSize = poolSize;
        this.mazeGeneratingAlgorithm =mazeGeneratingAlgorithm;
        this.mazeSearchingAlgorithm = mazeSearchingAlgorithm;
        setChanged();
        notifyObservers("newProperties");

        //TODO: check if the implementation should be here
/*        Object stat1= null,stat2= null,stat3 = null;
        Properties prop = Configurations.getInstance();
        stat1=prop.setProperty("threadPoolSize",poolSize);
        stat2=prop.setProperty("mazeGeneratingAlgorithm",mazeGeneratingAlgorithm);
        stat3=prop.setProperty("mazeSearchingAlgorithm",mazeSearchingAlgorithm);

        if(stat1==null||stat2==null||stat3==null){
            setChanged();
            notifyObservers("properties set failed");
        }
        setChanged();
        notifyObservers("properties set succeed");*/

    }

    public String[] getProperties() {
        String arr[] = new String[3];
        arr[0] = poolSize;
        arr[1] = mazeGeneratingAlgorithm;
        arr[2] = mazeSearchingAlgorithm;
        return arr;
    }
}
