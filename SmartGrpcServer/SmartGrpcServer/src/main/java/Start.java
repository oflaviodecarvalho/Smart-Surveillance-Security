import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.flavio.server.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Start implements Initializable {

    @FXML
    public ListView eventsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventsList.setItems(Main.eventsList);
        Main.eventsList.addListener((ListChangeListener<String>) c -> {
            System.out.println("Data Changed");
            eventsList.refresh();
        });

    }

    @FXML
    public void openListControl(MouseEvent mouseEvent) throws IOException {
        Stage stage=new Stage();
        Parent root= FXMLLoader.load(getClass().getClassLoader().getResource("listcontrol.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("User List Service Client");
        stage.show();

    }

    public void openMonitoring(MouseEvent mouseEvent) throws IOException {
        Stage stage=new Stage();
        Parent root= FXMLLoader.load(getClass().getClassLoader().getResource("monitoring.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Monitoring Center Client");
        stage.show();
    }

    public void openAccess(MouseEvent mouseEvent) throws IOException {
        Stage stage=new Stage();
        Parent root= FXMLLoader.load(getClass().getClassLoader().getResource("accessmonitoring.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Access Monitoring Client");
        stage.show();
    }
}
