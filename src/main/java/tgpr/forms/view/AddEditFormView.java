package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;

import java.util.List;

import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.AddEditFormController;
import tgpr.forms.model.Form;


public class AddEditFormView extends DialogWindow {
    private final TextBox txtTitle;
    private final TextBox txtDescription;
    private final CheckBox blnPublic;
    private final Label errTitle;
    private final Label errDescription;
    private final AddEditFormController controller;
    private final Button btnCreateUpdate;
    private final Form form;

    public AddEditFormView(AddEditFormController controller, Form form){
        //# SETUP #
        super((form == null ? "Create " : "Update ") + "a form");
        this.form = form;
        this.controller = controller;
        setHints(List.of(Hint.CENTERED));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(70, 10));

        //## PANEL ROOT ##
        Panel root = new Panel();

        //### PANEL FORM_FORMALITIES ###
        Panel formFormalities = new Panel().setLayoutManager(new GridLayout(2).setTopMarginSize(1)).addTo(root);

        //#### TITLE ####
        new Label("Title:").addTo(formFormalities);
        txtTitle = new TextBox(new TerminalSize(21, 1)).addTo(formFormalities)
                .setTextChangeListener((txt, byUser) -> validate());
        new EmptySpace().addTo(formFormalities);
        errTitle = new Label("").addTo(formFormalities)
                .setForegroundColor(TextColor.ANSI.RED);

        //#### DESCRIPTION ####
        new Label("Description:").addTo(formFormalities);
        txtDescription = new TextBox(new TerminalSize(50, 3)).addTo(formFormalities)
                .setTextChangeListener((txt, byUser) -> validate());
        new EmptySpace().addTo(formFormalities);
        errDescription = new Label("").addTo(formFormalities)
                .setForegroundColor(TextColor.ANSI.RED);

        //#### PUBLIC ####
        new Label("Public:").addTo(formFormalities);
        blnPublic = new CheckBox().addTo(formFormalities);
        new EmptySpace().addTo(formFormalities);
        new EmptySpace().addTo(formFormalities);

        //### BUTTONS PANEL ###
        Panel buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL)).addTo(root);
        //#### BUTTON CREATE/UPDATE ####
        btnCreateUpdate = new Button(form == null ? "Create" : "Update", this::createUpdate).addTo(buttons).setEnabled(false);
        //#### BUTTON CANCEL ####
        new Button("Cancel", this::close).addTo(buttons);

        //# SETUP #
        setComponent(root);

        if (form != null) {
            txtTitle.setText(form.getTitle());
            txtDescription.setText(form.getDescription());
            blnPublic.setChecked(form.getIsPublic());
        }
        validate();
    }

    private void validate() {
        var errors = controller.validate(
            txtTitle.getText(), txtDescription.getText()
        );
        errTitle.setText(errors.getFirstErrorMessage(Form.Fields.Title));
        errDescription.setText(errors.getFirstErrorMessage(Form.Fields.Description));

        btnCreateUpdate.setEnabled(errors.isEmpty());
    }
    private void createUpdate(){
        controller.createUpdate(
                txtTitle.getText(),
                txtDescription.getText(),
                blnPublic.isChecked()
        );
    }

}
