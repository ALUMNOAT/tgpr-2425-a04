package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.AddEditFormController;
import tgpr.forms.controller.AddEditOptionListController;

import java.util.List;

public class AddEditOptionListView extends DialogWindow {
    private final AddEditOptionListController controller;
    private final TextBox txtName;

    public AddEditOptionListView(AddEditOptionListController controller) {
        //# SETUP #
        super("Test");
        this.controller = controller;
        setHints(List.of(Hint.CENTERED));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(60, 18));

        //## PANEL ROOT ##
        Panel root = new Panel();

        //### PANEL FORMALITIES ###
        Panel updateOptionListFormalities = new Panel().setLayoutManager(new GridLayout(2).setTopMarginSize(1)).addTo(root);

        //#### NAME ####
        new Label("Name:").addTo(updateOptionListFormalities);
        txtName = new TextBox(new TerminalSize(21, 1)).addTo(updateOptionListFormalities);
        new EmptySpace().addTo(updateOptionListFormalities);
        new EmptySpace().addTo(updateOptionListFormalities);

        //# SETUP #
        setComponent(root);
    }
}
