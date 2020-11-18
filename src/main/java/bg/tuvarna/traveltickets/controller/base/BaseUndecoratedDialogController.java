package bg.tuvarna.traveltickets.controller.base;

import javafx.scene.control.DialogPane;

import java.util.Objects;

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
