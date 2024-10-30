package tgpr.forms.view;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.edit_instance;
import tgpr.forms.model.Answer;
import tgpr.forms.model.Question;

import java.time.LocalDateTime;
import java.util.List;

public class view_edit_instance extends DialogWindow {

    private final Label txtBody = new Label("You have already answered this form."+System.lineSeparator()+"You can view your submission or submit again."+System.lineSeparator()+"What would you like to do?");
    private final Label txtBodyform;
    private final Label error = new Label("");
    private final edit_instance controller;
    private boolean review=false;
    private Answer answer;
    private TextBox rep;
    private CheckBoxList rep_check = new CheckBoxList();
    private RadioBoxList rep_radio = new RadioBoxList();
    private ComboBox rep_combo = new ComboBox();

    public view_edit_instance(edit_instance controller) {
        super("Open a form");
        this.controller = controller;
        error.setForegroundColor(TextColor.ANSI.RED);

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
        Button btnSubmit = new Button("Submit again", this::submitAgain).addTo(panel);
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
        answer = controller.i.getOrCreateAnswer(q);
        Label quest = new Label(q.getTitle());
        if (q.getRequired()){
            Label req = new Label("(*)");
            req.setForegroundColor(TextColor.ANSI.RED).addTo(panel);
        }
        quest.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT).addTo(panel);
        return panel;
    }
    private Panel QuizPanel(){
        Panel panel = createPanel();
        Question q = controller.getQuestion();
        if (q.getType() == Question.Type.Short || q.getType() == Question.Type.Email || q.getType() == Question.Type.Date){
            rep = new TextBox().sizeTo(100);
            if (answer.isValid()){
                rep.setText(answer.getValue());
            }
            rep.addTo(panel);
        } else if (q.getType() == Question.Type.Long){
            rep = new TextBox().sizeTo(100, 8);
            if (answer.isValid()){
                rep.setText(answer.getValue());
            }
            rep.addTo(panel);
        } else if (q.getType() == Question.Type.Check){
            for (Object i : q.getOptionList().getOptionValues()){
                rep_check.addItem(i);
                if (answer.isValid()){
                    if(i.toString().compareTo(answer.getValue())==0){
                        rep_check.setChecked(i, true);}
                    }
            }
            rep_check.addTo(panel);
        } else if (q.getType() == Question.Type.Radio){
            for (Object i : q.getOptionList().getOptionValues()){
                rep_radio.addItem(i);
                if (answer.isValid()){
                    if(i.toString().compareTo(answer.getValue())==0){
                        rep_radio.setCheckedItem(i);
                    }
                }
            }
            rep_radio.addTo(panel);
        } else if (q.getType() == Question.Type.Combo){
            for (Object i : q.getOptionList().getOptionValues()){
                rep_combo.addItem(i);
                if (answer.isValid()){
                    if(i.toString().compareTo(answer.getValue())==0){
                        rep_combo.setSelectedItem(i);}
                }
            }
            rep_combo.addTo(panel);
        }
        panel.addEmpty();
        error.addTo(panel);
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
            Label rep = new Label(q.getAnswerValue(controller.i));
            rep.addTo(panel);
        }
        return panel;
    }

    private Panel formButtonsPanel(boolean review) {
        Panel panel = Panel.horizontalPanel(1).center();

        Button btnClose = new Button("Close", this::close).addTo(panel);
        if(controller.getQuestion().getIdx() > 1){
            Button btnPrev = new Button("Previous", this::previous).addTo(panel);
        } if(controller.getQuestion().getIdx() < controller.numQuestions()){
            Button btnNext = new Button("Next", this::next).addTo(panel);
        } else  {
            if(!review){
                Button btnSubmit = new Button("Submit", this::submit).addTo(panel);
            }
        }
        if(!review){
            Button btnExit = new Button("Cancel", this::close).addTo(panel);}
        return panel;
    }

    private void submit(){
        answer.save();
        controller.i.submit();
        this.close();
    }

    private void previous(){
        controller.previousQuestion();
        rep_radio.clearItems();
        rep_combo.clearItems();
        rep_check.clearItems();
        instancePanel();
    }

    private void next(){
        if (review){
            controller.nextQuestion();
            instancePanel();
        }else {
            verifAnswer();
        }
    }

    private void verifAnswer(){
        Question q = controller.getQuestion();
        if (q.getType() == Question.Type.Short || q.getType() == Question.Type.Email || q.getType() == Question.Type.Date){
            answer.setValue(rep.getText());
        } if (q.getType() == Question.Type.Check){
            answer.setValue(rep_check.getSelectedItem().toString());
            rep_radio.clearItems();
        } else if (q.getType() == Question.Type.Radio){
            answer.setValue(rep_radio.getSelectedItem().toString());
            rep_radio.clearItems();
        } else if (q.getType() == Question.Type.Combo){
            answer.setValue(rep_combo.getSelectedItem().toString());
            rep_combo.clearItems();
        }
        if(!answer.isValid()){
            error.setVisible(true);
            error.setText("This question is required.");
            instancePanel();
        } else {
            error.setVisible(false);
            answer.save();
            System.out.println(answer.getValue());
            controller.nextQuestion();
            instancePanel();
        }
    }

    private void submitAgain(){
        controller.i = controller.f.createInstance(controller.u);
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

