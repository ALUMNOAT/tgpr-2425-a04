package tgpr.forms.view;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.edit_instance;
import tgpr.forms.model.Question;

import java.util.List;

public class view_edit_instance extends DialogWindow {

    private final Label txtBody = new Label("You have already answered this form."+System.lineSeparator()+"You can view your submission or submit again."+System.lineSeparator()+"What would you like to do?");
    private final Label txtBodyform;
    private final edit_instance controller;
    private boolean review=false;

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

    private void instancePanel(){
        var root = createPanel().sizeTo(80,20).setLayoutManager(new BorderLayout());
        setComponent(root);
        formTextPanel().setLayoutData(BorderLayout.Location.TOP).addTo(root);
        root.addEmpty();
        QuestionPanel(review).setLayoutData(BorderLayout.Location.CENTER).withBorder(Borders.singleLine("Question " + controller.getQuestion().getIdx() + " of " + controller.numQuestions())).addTo(root);
        root.addEmpty();
        formButtonsPanel(review).setLayoutData(BorderLayout.Location.BOTTOM).addTo(root);
        refresh();
        super.setTitle("Answer the Form");
    }


    private void confirmationPanel(){
        var root = createPanel();
        setComponent(root);
        root.addEmpty();
        confirmationTextPanel().addTo(root);
        root.addEmpty();
        confirmationButtonsPanel().addTo(root);
        refresh();
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

        Button btnView = new Button("View Submission", this::review).addTo(panel);
        Button btnSubmit = new Button("Submit again", this::instancePanel).addTo(panel);
        Button btnExit = new Button("Cancel", this::close).addTo(panel);

        return panel;
    }

    private Panel QuestionPanel(boolean review){
        Panel panel = createPanel();
        panel.addEmpty();
        titlePanel().addTo(panel);
        panel.addEmpty();
        if(review){
            reviewQuizPanel().addTo(panel);
        } else {
            QuizPanel().addTo(panel);
        }
        return panel;
    }


    private Panel titlePanel(){
        Panel panel = Panel.horizontalPanel();
        Question q = controller.getQuestion();
        Label quest = new Label(q.getTitle());
        if (q.getRequired()){
            Label req = new Label("(*)");
            req.setForegroundColor(TextColor.ANSI.RED).addTo(panel);
        }
        quest.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT).addTo(panel);
        return panel;
    }
    private Panel QuizPanel(){
        Panel panel = Panel.horizontalPanel();
        Question q = controller.getQuestion();
        if (q.getType() == Question.Type.Short){
            TextBox rep = new TextBox().sizeTo(100);
            rep.addTo(panel);
        } else if (q.getType() == Question.Type.Long){
            TextBox rep = new TextBox().sizeTo(100, 8);
            rep.addTo(panel);
        } else if (q.getType() == Question.Type.Email){
            TextBox rep = new TextBox().sizeTo(100);
            rep.addTo(panel);
        } else if (q.getType() == Question.Type.Date){
            TextBox rep = new TextBox().sizeTo(100);
            rep.addTo(panel);
        } else if (q.getType() == Question.Type.Check){
            CheckBoxList rep = new CheckBoxList();
            for (Object i : q.getOptionList().getOptionValues()){
                rep.addItem(i);
            }
            rep.addTo(panel);
        } else if (q.getType() == Question.Type.Radio){
            RadioBoxList rep = new RadioBoxList();
            for (Object i : q.getOptionList().getOptionValues()){
                rep.addItem(i);
            }
            rep.addTo(panel);
        } else if (q.getType() == Question.Type.Combo){
            ComboBox rep = new ComboBox();
            for (Object i : q.getOptionList().getOptionValues()){
                rep.addItem(i);
            }
            rep.addTo(panel);
        }
        return panel;
    }

    private Panel reviewQuizPanel(){
        Panel panel = createPanel();
        Question q = controller.getQuestion();
        if(q.getType() == Question.Type.Check){
            for (Object i : q.getAnswerValues(controller.i)){
                Label rep = new Label(i.toString());
                rep.addTo(panel);
            }
        } else {
            Label rep = new Label(q.getAnswerValueAsString(controller.i));
            rep.addTo(panel);
        }
        return panel;
    }

    private Panel formButtonsPanel(boolean review) {
        Panel panel = Panel.horizontalPanel(1).center();

        Button btnClose = new Button("Close").addTo(panel);
        if(controller.getQuestion().getIdx() > 1){
            Button btnPrev = new Button("Previous", this::previous).addTo(panel);
        } if(controller.getQuestion().getIdx() < controller.numQuestions()){
            Button btnNext = new Button("Next", this::next).addTo(panel);
        } else  {
            if(!review){
                Button btnSubmit = new Button("Submit").addTo(panel);
            }
        }
        if(!review){
            Button btnExit = new Button("Cancel", this::close).addTo(panel);}
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

    private void review(){
        this.review = true;
        instancePanel();
    }

    private Panel createPanel() {
        return Panel.gridPanel(1);
    }


    private void refresh() {
    }
}

