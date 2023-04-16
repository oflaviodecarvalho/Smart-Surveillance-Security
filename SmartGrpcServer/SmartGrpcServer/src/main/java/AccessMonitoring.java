import com.sun.javafx.scene.control.DoubleField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.flavio.server.LatLng;
import org.flavio.server.Main;
import org.flavio.server.clients.RPCClientsMain;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class AccessMonitoring implements Initializable {

    public DoubleField latitudeInput;
    public Button setPerimeter;
    public ListView<String> usersList;
    public DoubleField longitudeInput;
    public ListView responseList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usersList.setItems(Main.userList);
        Main.userList.addListener((ListChangeListener<String>) c -> {
            usersList.refresh();
        });
        responseList.setItems(Main.accessMessages);
        Main.listControlMessages.addListener((ListChangeListener<String>) c -> {
            responseList.refresh();
        });
    }
    @FXML
    public void loadUsers(MouseEvent mouseEvent) {
        RPCClientsMain.rpcClients.loadUsers();
    }

    public void setPerimeter(MouseEvent mouseEvent) {
        var selected=usersList.getSelectionModel().getSelectedItem();
        if (selected==null){
            JOptionPane.showMessageDialog(null,"Please select a user first");
            return;
        }
       var userName=selected.split("=>")[0];
        var latitude=latitudeInput.getValue();
        var longitude=longitudeInput.getValue();
        RPCClientsMain.rpcClients.setPerimeter(userName, LatLng.newBuilder().setLongitude(longitude).setLatitude(latitude));
    }

    public void ringBel(MouseEvent mouseEvent) {
        RPCClientsMain.rpcClients.ringBell();
    }
}
