package tgpr.forms.view;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.edit_instance;
import tgpr.forms.model.Question;
import tgpr.framework.Controller;

import java.util.List;

public class view_edit_instance extends DialogWindow {

    private final Label txtBody = new Label("You have already answered this form."+System.lineSeparator()+"You can view your submission or submit again."+System.lineSeparator()+"What would you like to do?");
    private Label txtBodyform;
    private edit_instance controller;

    public view_edit_instance(edit_instance controller) {
        super("Open a form");
        this.controller = controller;

        setHints(List.of(Hint.CENTERED, Hint.MODAL));
        this.txtBodyform = new Label(" Title:"+controller.getTitle() + System.lineSeparator()
                +" Description:"+controller.getDescription()+System.lineSeparator()
                +" Started on:"+controller.getDate());
        if(controller.isCompleted()){
            confirmationPanel();
        } else{
            instancePanel();
        }
    }

    private Panel instancePanel(){
        var root = createPanel().sizeTo(80,20).setLayoutManager(new BorderLayout());
        setComponent(root);
        formTextPanel().setLayoutData(BorderLayout.Location.TOP).addTo(root);
        root.addEmpty();
        QuizPanel().setLayoutData(BorderLayout.Location.CENTER).withBorder(Borders.singleLine()).addTo(root);
        root.addEmpty();
        formButtonsPanel().setLayoutData(BorderLayout.Location.BOTTOM).addTo(root);
        refresh();
        super.setTitle("Answer the Form");
        return root;
    }

    private Panel confirmationPanel(){
        var root = createPanel();
        setComponent(root);
        root.addEmpty();
        confirmationTextPanel().addTo(root);
        root.addEmpty();
        confirmationButtonsPanel().addTo(root);
        refresh();
        return root;
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
        Button btnSubmit = new Button("Submit again", this::instancePanel).addTo(panel);
        Button btnExit = new Button("Cancel", this::close).addTo(panel);

        return panel;
    }

    private Panel QuizPanel(){
        Panel panel = Panel.horizontalPanel();
        Question q = controller.getQuestion();
        Label quest = new Label(q.getTitle());
        quest.addTo(panel);
        return panel;
    }

    private Panel formButtonsPanel() {
        Panel panel = Panel.horizontalPanel(1).center();

        Button btnClose = new Button("Close").addTo(panel);
        if(controller.getQuestion().getIdx() > 1){
            Button btnPrev = new Button("Previous", this::previous).addTo(panel);
        } if(controller.getQuestion().getIdx() < 16){
            Button btnnext = new Button("Next", this::next).addTo(panel);
        }
        Button btnExit = new Button("Cancel", this::close).addTo(panel);

        return panel;
    }

    private void previous(){
        controller.previousQuestion();
        instancePanel();
    }

    private void next(){
        controller.nextQuestion();
        instancePanel();
    }
    private Panel createPanel() {
        var panel = Panel.gridPanel(1);
        return panel;
    }


    private void refresh() {
    }
}

