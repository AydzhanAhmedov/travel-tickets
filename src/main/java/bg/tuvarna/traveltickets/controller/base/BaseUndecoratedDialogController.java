package bg.tuvarna.traveltickets.controller.base;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Base class for each undecorated dialog. It defines different dialog modes with view, add and edit
 * as well as methods for setting the mode.
 */
public abstract class BaseUndecoratedDialogController extends BaseUndecoratedController {

    /**
     * Defines different dialog modes.
     */
    public enum DialogMode {VIEW, ADD, EDIT}

    private DialogMode dialogMode;

    public final void setDialogMode(final DialogMode dialogMode) {
        this.dialogMode = Objects.requireNonNull(dialogMode);
        switch (dialogMode) {
            case VIEW -> onViewModeSet();
            case ADD -> onAddModeSet();
            case EDIT -> onEditMode();
        }
    }

    public final DialogMode getDialogMode() {
        return dialogMode;
    }

    protected final DialogPane getDialogPane() {
        return (DialogPane) root;
    }

    protected final Button addDialogButton(final String text, final ButtonBar.ButtonData buttonData) {
        final ButtonType buttonType = new ButtonType(text, buttonData);
        getDialogPane().getButtonTypes().add(buttonType);
        return (Button) getDialogPane().lookupButton(buttonType);
    }

    /**
     * This method is called whenever the dialogMode is set to VIEW.
     */
    protected abstract void onViewModeSet();

    /**
     * This method is called whenever the dialogMode is set to ADD.
     */
    protected abstract void onAddModeSet();

    /**
     * This method is called whenever the dialogMode is set to EDIT.
     */
    protected abstract void onEditMode();

}
