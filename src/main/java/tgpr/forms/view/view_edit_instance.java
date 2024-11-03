package tgpr.forms.view;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.forms.controller.edit_instance;
import tgpr.forms.model.Answer;
import tgpr.forms.model.Question;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

import static tgpr.framework.Controller.askConfirmation;

public class view_edit_instance extends DialogWindow {

    private final Label txtBody = new Label("You have already answered this form."+System.lineSeparator()+"You can view your submission or submit again."+System.lineSeparator()+"What would you like to do?");
    private final Label error = new Label("");
    private final edit_instance controller;
    private final Label lblTitle = new Label("");
    private final Label lblDescription = new Label("");
    private final Label lblStart = new Label("");
    private final Label lblSubmitted = new Label("");
    private final Label lblUser = new Label("");
    private boolean review=false;
    private Answer answer;
    private TextBox rep;
    private final CheckBoxList rep_check = new CheckBoxList();
    private final RadioBoxList rep_radio = new RadioBoxList();
    private final ComboBox rep_combo = new ComboBox();

    public view_edit_instance(edit_instance controller) {
        super("Open a form");
        this.controller = controller;
        error.setForegroundColor(TextColor.ANSI.RED);

        setHints(List.of(Hint.CENTERED, Hint.MODAL));
        this.lblTitle.setText( controller.getTitle());
        this.lblDescription.setText(controller.getDescription());
        this.lblStart.setText(controller.getDate());
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
        super.setTitle("Answer the Form");
    }


    private void confirmationPanel(){
        var root = createPanel();
        setComponent(root);
        root.addEmpty();
        confirmationTextPanel().addTo(root);
        root.addEmpty();
        confirmationButtonsPanel().addTo(root);
    }

    private Panel confirmationTextPanel() {
        Panel panel = Panel.horizontalPanel();
        txtBody.addTo(panel);
        return panel;
    }
    private Panel formTextPanel() {
            Panel panel = Panel.gridPanel(2, Margin.of(1, 1, 1, 0));

            new Label("Title:").addTo(panel);
            lblTitle.addTo(panel).addStyle( SGR.BOLD);

            new Label("Description:").addTo(panel);
            lblDescription.addTo(panel).addStyle(SGR.BOLD);

            new Label("Started on:").addTo(panel);
            lblStart.addTo(panel).addStyle(SGR.BOLD);

            if(review){
                new Label("Submitted on:").addTo(panel);
                lblSubmitted.addTo(panel).addStyle(SGR.BOLD);
            }
            return panel;
    }
    private Panel confirmationButtonsPanel() {
        Panel panel = Panel.horizontalPanel(1).center();

        new Button( "View Submission" , this::review ).addTo( panel );
        new Button( "Submit again" , this::submitAgain ).addTo( panel );
        new Button( "Cancel" , this::close ).addTo( panel );

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
        if (q.getType() == Question.Type.Short){
            rep = new TextBox().sizeTo(100);
            if (answer.isValid()){
                rep.setText(answer.getValue());
            }
            rep.addTo(panel);
        } else if(q.getType() == Question.Type.Date ){
            rep = new TextBox().sizeTo(100).setValidationPattern( Pattern.compile( "[/\\d]{0,10}" ) );
            if (answer.isValid()){
                rep.setText(answer.getValue());
            }
            rep.addTo(panel);
        } else if(q.getType() == Question.Type.Email ){
            rep = new TextBox().sizeTo(100);
            if (answer.isValid()){
                rep.setText(answer.getValue());
            }
            rep.addTo(panel);
        }else if (q.getType() == Question.Type.Long){
            rep = new TextBox().setHorizontalFocusSwitching( true ).sizeTo( 100, 8 );
            if (answer.isValid()){
                rep.setText(answer.getValue());
            }
            rep.addTo(panel);
        } else if (q.getType() == Question.Type.Check){
            rep_check.clearItems();
            for (Object i : q.getOptionList().getOptionValues()){
                rep_check.addItem(i);
                if (answer.isValid()){
                    for (Object o : q.getAnswerValues(controller.i)){
                        rep_check.setChecked(o, true);
                    }
                    }
            }
            rep_check.addTo(panel);
        } else if (q.getType() == Question.Type.Radio){
            rep_radio.clearItems();
            for (var i : q.getOptionList().getOptionValues()){
                rep_radio.addItem(i);
                if (answer.isValid()){
                    if(i.toString().compareTo(q.getAnswerValueAsString(controller.i))==0){
                        rep_radio.setCheckedItem(i);
                    }
                }
            }
            rep_radio.addTo(panel);
        } else if (q.getType() == Question.Type.Combo){
            rep_combo.clearItems();
            rep_combo.addItem( "--- Please choose an option ---" );
            for (Object i : q.getOptionList().getOptionValues()){
                rep_combo.addItem(i);
                if (answer.isValid() && answer.getValue().compareTo( "0" )!=0 && answer.getValue().compareTo( "" )!=0){
                    if(i.toString().compareTo(q.getAnswerValueAsString( controller.i ))==0){
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
            if(answer.getValue().compareTo( "" )!=0 && answer.getValue()!=null){
                Label rep = new Label(q.getAnswerValueAsString( controller.i ));
                rep.addTo(panel);
            }
        }
        return panel;
    }

    private Panel formButtonsPanel(boolean review) {
        Panel panel = Panel.horizontalPanel(1 );
        if(controller.u.isGuest()){
            Button btnClose = new Button( "Close" , this::cancel ).addTo( panel );
            addShortcut(btnClose, KeyStroke.fromString("<esc>" ));
        }else{
            Button btnClose = new Button( "Close" , this::close ).addTo( panel );
            addShortcut(btnClose, KeyStroke.fromString("<esc>" ));
        }
        if(controller.getQuestion().getIdx() > 1){
            Button btnPrev = new Button( "Previous" , this::previous ).addTo( panel );

        } if(controller.getQuestion().getIdx() < controller.numQuestions()){
            Button btnNext = new Button( "Next" , this::next ).addTo( panel );
            addShortcut(btnNext, KeyStroke.fromString("<up>" ));
        } else  {
            if(!review){
                new Button( "Submit" , this::submit ).addTo( panel );
            }
        }
        if(!review){
            new Button( "Cancel" , this::cancel ).addTo( panel );
        }
        return panel;
    }

    private void cancel(){
        if (askConfirmation("You are about to delete this instance and its answers?", "Delete Instance")) {
            controller.i.delete();
            close();
        }
    }


    private void submit(){
        if(askConfirmation( "Are you sure you want to submit this form?", "Submit Form" )){
            answer.save();
            controller.i.submit();
            this.close();
        }
    }

    private void previous(){
        controller.previousQuestion();
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
            for(Object o: rep_check.getCheckedItems()){
                answer.setValue(o.toString());
                answer.save();
                answer = controller.i.getOrCreateAnswer(q);
            }
        } else if (q.getType() == Question.Type.Radio){
            if(!(rep_radio.getCheckedItemIndex() == -1)){
                answer.setValue(""+(rep_radio.getCheckedItemIndex()+1));
            }
        } else if (q.getType() == Question.Type.Combo){
            if(!(rep_combo.getSelectedIndex() == 0)){
                answer.setValue(""+rep_combo.getSelectedIndex());
            }
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
        this.lblSubmitted.setText( controller.i.getCompleted().format( DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        this.review = true;
        instancePanel();
    }

    private Panel createPanel() {
        return Panel.gridPanel(1);
    }

}

