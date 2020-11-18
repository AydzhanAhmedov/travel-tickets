package bg.tuvarna.traveltickets.control;

import javafx.scene.control.TextField;

public class NumberTextField extends TextField {

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