import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.flavio.server.Main;
import org.flavio.server.clients.RPCClientsMain;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MonitoringCenter implements Initializable {
    public Button loadUsers;
    @FXML
    public ListView<String> alertMessages;

    @FXML
    public ListView<String> usersList;
    public Button deleteUser;
    public Button loadAlerts;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usersList.setItems(Main.userList);
        Main.userList.addListener((ListChangeListener<String>) c -> usersList.refresh());
        RPCClientsMain.rpcClients.loadUsers();

        alertMessages.setItems(Main.alertMessages);
        Main.alertMessages.addListener((ListChangeListener<String>) c -> alertMessages.refresh());
        RPCClientsMain.rpcClients.loadAlerts();


    }

    public void loadUsers(MouseEvent mouseEvent) {
RPCClientsMain.rpcClients.loadUsers();
    }

    public void deleteUser(MouseEvent mouseEvent) {
        if (usersList.getSelectionModel().getSelectedItem()==null){
            JOptionPane.showMessageDialog(null,"Select A User From The List");
return;
        }
    var selected=usersList.getSelectionModel().getSelectedItem().split("=>")[0];

       var confirm= JOptionPane.showConfirmDialog(null,"Delete User And Report Invasion?","Delete User",JOptionPane.OK_CANCEL_OPTION);
        if (confirm==JOptionPane.OK_OPTION){
            RPCClientsMain.rpcClients.deleteUser(selected);
        }
    }

    @FXML
    public void loadAlerts(MouseEvent mouseEvent) {
     RPCClientsMain.rpcClients.loadAlerts();
    }
}
