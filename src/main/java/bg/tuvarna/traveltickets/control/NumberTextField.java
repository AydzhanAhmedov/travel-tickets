package bg.tuvarna.traveltickets.control;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;

public class NumberTextField extends TextField {

    public NumberTextField() {
        setAlignment(Pos.CENTER_RIGHT);
    }

    @Override
    public void replaceText(final int start, final int end, final String text) {
        if (validate(text)) super.replaceText(start, end, text);
    }

    @Override
    public void replaceSelection(final String text) {
        if (validate(text)) super.replaceSelection(text);
    }

    private boolean validate(final String text) {
        return text.matches("[0-9]*");
    }

}