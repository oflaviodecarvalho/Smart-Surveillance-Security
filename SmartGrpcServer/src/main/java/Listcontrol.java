import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.flavio.server.Main;
import org.flavio.server.clients.RPCClientsMain;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class Listcontrol implements Initializable {

    @FXML
    public ListView usersList;
    public ComboBox roles;
    public Button saveUser;
    public PasswordField passwordinput;
    public TextField usernameInput;
    public static ObservableList<String> users= FXCollections.observableArrayList();
    public ListView responseList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
         usersList.setItems(Main.userList);
         roles.setItems(FXCollections.observableArrayList(Main.rolesAndBounds.keySet()));
         Main.userList.addListener((ListChangeListener<String>) c -> {
             usersList.refresh();
         });
         usersList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
             @Override
             public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                 usernameInput.setText(newValue.toString().split("=>")[0]);
                 roles.getSelectionModel().select(newValue.toString().split("=>")[1]);
             }
         });
         responseList.setItems(Main.listControlMessages);
         Main.listControlMessages.addListener((ListChangeListener<String>) c -> {
             responseList.refresh();
         });

    }

    @FXML
    public void saveUser(MouseEvent mouseEvent) {
      String userName=usernameInput.getText();
      String password=passwordinput.getText();
      List<String> rolesList = Collections.singletonList(roles.getSelectionModel().getSelectedItem().toString());
        RPCClientsMain.rpcClients.createUser(userName,password,rolesList);
    }

    @FXML
    public void loadUsers(MouseEvent mouseEvent) {
     RPCClientsMain.rpcClients.loadUsers();
    }

    @FXML
    public void modifyRoles(MouseEvent mouseEvent) {
        String userName=usernameInput.getText();
        List<String> rolesList = Collections.singletonList(roles.getSelectionModel().getSelectedItem().toString());
        RPCClientsMain.rpcClients.modifyAccessLevel(userName,rolesList);

    }
}
