package bg.tuvarna.traveltickets.controller.base;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Base class for each controller. Each fxml file should define the id of the {@link Parent} as 'root'
 * and the id of the {@link Button} that's responsible for exiting as 'exitButton'.
 */
public abstract class BaseUndecoratedController implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    protected Parent root;

    @FXML
    protected Button exitButton;

    @FXML
    protected void onExitButtonClicked(final MouseEvent event) {
        final Window window = root.getScene().getWindow();
        window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        makeSceneDraggable();
    }

    public void makeSceneDraggable() {
        root.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        root.setOnMouseDragged(e -> {
            final Stage stage = (Stage) root.getScene().getWindow();
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });
    }

}
