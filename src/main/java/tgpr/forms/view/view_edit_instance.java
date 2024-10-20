package tgpr.forms.view;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.edit_instance;

import java.util.List;

public class view_edit_instance extends DialogWindow {

    private final edit_instance controller;
    private final Label txtBody = new Label("You have already answered this form."+System.lineSeparator()+"You can view your submission or submit again."+System.lineSeparator()+"What would you like to do?");
    private final Label txtBodyform = new Label("Title:"+System.lineSeparator()+"Description:"+System.lineSeparator()+"Started on:");

    public view_edit_instance(edit_instance controller) {
        super("Open a form");

        this.controller = controller;
        setHints(List.of(Hint.CENTERED, Hint.MODAL));
        /*if(true){
            var root = createPanel();
            setComponent(root);
            root.addEmpty();
            confirmationTextPanel().addTo(root);
            root.addEmpty();
            confirmationButtonsPanel().addTo(root);
            refresh();
        }*/

        var root = createPanel().setLayoutManager(new BorderLayout());
        setComponent(root);
        formTextPanel().setLayoutData(BorderLayout.Location.TOP).addTo(root);
        root.addEmpty();
        QuizPanel().setLayoutData(BorderLayout.Location.CENTER).withBorder(Borders.singleLine()).addTo(root);
        root.addEmpty();
        formButtonsPanel().setLayoutData(BorderLayout.Location.BOTTOM).addTo(root);
        refresh();
        super.setTitle("Answer the Form");



    }

    private Panel confirmationTextPanel() {
        Panel panel = Panel.horizontalPanel();
        txtBody.addTo(panel);
        return panel;
    }
    private Panel formTextPanel() {
        Panel panel = Panel.horizontalPanel();
        txtBodyform.addTo(panel);
        return panel;
    }
    private Panel confirmationButtonsPanel() {
        Panel panel = Panel.horizontalPanel(1).center();

        Button btnView = new Button("View Submission").addTo(panel);
        Button btnSubmit = new Button("Submit again").addTo(panel);
        Button btnExit = new Button("Cancel", this::close).addTo(panel);

        return panel;
    }

    private Panel QuizPanel(){
        Panel panel = Panel.horizontalPanel();
        return panel;
    }

    private Panel formButtonsPanel() {
        Panel panel = Panel.horizontalPanel(1).center();

        Button btnClose = new Button("Close").addTo(panel);
        Button btnPrev = new Button("Previous").addTo(panel);
        Button btnnext = new Button("Next").addTo(panel);
        Button btnExit = new Button("Cancel", this::close).addTo(panel);

        return panel;
    }

    private Panel createPanel() {
        var panel = Panel.gridPanel(1);
        return panel;
    }


    private void refresh() {
    }
}

