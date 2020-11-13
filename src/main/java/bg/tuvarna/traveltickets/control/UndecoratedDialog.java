package bg.tuvarna.traveltickets.control;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import static bg.tuvarna.traveltickets.common.AppConfig.getPrimaryStage;

public class UndecoratedDialog<R> extends Dialog<R> {

    public UndecoratedDialog(final Parent parentSceneRoot) {
        initOwner(parentSceneRoot.getScene().getWindow());
        initStyle(StageStyle.UNDECORATED);

        setOnShowing(this::onShowing);
    }

    public UndecoratedDialog(final Parent parentSceneRoot, final DialogPane dialogPane) {
        this(parentSceneRoot);
        setDialogPane(dialogPane);
    }

    private void onShowing(final DialogEvent event) {
        centerDialog();
        setOnCloseWindowRequest(this::onCloseWindowRequest);
    }

    private void centerDialog() {
        final Stage stage = (Stage) getDialogPane().getScene().getWindow();

        final Bounds mainBounds = getPrimaryStage().getScene().getRoot().getLayoutBounds();
        final Bounds rootBounds = getDialogPane().getScene().getRoot().getLayoutBounds();

        stage.setX(getPrimaryStage().getX() + (mainBounds.getWidth() - rootBounds.getWidth()) / 2);
        stage.setY(getPrimaryStage().getY() + (mainBounds.getHeight() - rootBounds.getHeight()) / 2);
    }

    public void setOnCloseWindowRequest(final EventHandler<WindowEvent> onCloseRequest) {
        getDialogPane().getScene().getWindow().setOnCloseRequest(onCloseRequest);
    }

    protected void onCloseWindowRequest(final WindowEvent event) {
    }

}
