package bg.tuvarna.traveltickets.controller;

import bg.tuvarna.traveltickets.controller.base.BaseUndecoratedDialogController;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.Constants.CLIENT_DIALOG_FXML_PATH;

public class ProfileController implements Initializable {

    AuthService authService = AuthServiceImpl.getInstance();

    @FXML
    private BorderPane root;

    @FXML
    private VBox vbox;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        loadClientDialog();
    }

    private void loadClientDialog() {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(CLIENT_DIALOG_FXML_PATH), getLangBundle());
            final DialogPane dialogPane = loader.load();
            final ClientDialogController clientDialogController = loader.getController();

            if (authService.loggedUserIsAdmin()) {
                clientDialogController.hideAllClientFields();
                clientDialogController.injectDialogMode(BaseUndecoratedDialogController.DialogMode.VIEW, authService.getLoggedUser());
            } else {
                Client client = authService.getLoggedClient();
                clientDialogController.injectDialogMode(BaseUndecoratedDialogController.DialogMode.VIEW, client, null);
            }
            clientDialogController.hideExitButton();
            dialogPane.setStyle("-fx-background-color: transparent");
            root.setCenter(dialogPane);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addField(final String strlabel, final String strtext) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(15);

        Label label = new Label(strlabel);
        label.setText(strlabel);

        TextField textField = new TextField();
        textField.setEditable(false);
        textField.setText(strtext);

        hBox.getChildren().addAll(label, textField);
        vbox.getChildren().add(hBox);
    }
}
