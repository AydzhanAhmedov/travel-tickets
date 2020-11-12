package bg.tuvarna.traveltickets.controller.base;

import javafx.geometry.Bounds;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

import static bg.tuvarna.traveltickets.common.AppConfig.getPrimaryStage;

public abstract class BaseDialogController extends BaseController {

    @Override
    public void makeSceneDraggable() {
        final Stage primaryStage = getPrimaryStage();

        root.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        root.setOnMouseDragged(e -> {
            // move the primary stage
            primaryStage.setX(e.getScreenX() - xOffset);
            primaryStage.setY(e.getScreenY() - yOffset);

            final Stage stage = (Stage) root.getScene().getWindow();

            final Bounds mainBounds = primaryStage.getScene().getRoot().getLayoutBounds();
            final Bounds rootBounds = root.getScene().getRoot().getLayoutBounds();

            // center the dialog
            stage.setX(primaryStage.getX() + (mainBounds.getWidth() - rootBounds.getWidth()) / 2);
            stage.setY(primaryStage.getY() + (mainBounds.getHeight() - rootBounds.getHeight()) / 2);
        });
    }

    protected final DialogPane getDialogPane() {
        return (DialogPane) root;
    }

}
