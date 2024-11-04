package tgpr.forms.view;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.forms.controller.ViewEditInstanceController;
import tgpr.forms.model.Answer;
import tgpr.forms.model.Question;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static tgpr.framework.Controller.askConfirmation;

public class ViewEditInstanceView extends DialogWindow {

    private final Label txtBody = new Label("You have already answered this form."+System.lineSeparator()+"You can view your submission or submit again."+System.lineSeparator()+"What would you like to do?");
    private final Label errBody = new Label("");
    private final ViewEditInstanceController controller;
    private final Label lblTitle = new Label("");
    private final Label lblDescription = new Label("");
    private final Label lblStart = new Label("");
    private final Label lblSubmitted = new Label("");
    private final Label lblUser = new Label("");
    private boolean review=false;
    public boolean view_instance;
    private Answer answer;
    private TextBox textBox;
    private final CheckBoxList checkBox = new CheckBoxList();
    private final RadioBoxList radioBox = new RadioBoxList();
    private final ComboBox comboBox = new ComboBox();
    public List<Integer> questionError = new ArrayList<>();

    public ViewEditInstanceView( ViewEditInstanceController controller, boolean view_instance) {
        super("Open a form");
        this.controller = controller;
        errBody.setForegroundColor(TextColor.ANSI.RED);

        setHints(List.of(Hint.CENTERED, Hint.MODAL));
        this.lblTitle.setText( controller.getTitle());
        this.lblDescription.setText(controller.getDescription());
        this.lblStart.setText(controller.getDate());
        this.view_instance = view_instance;
        if (!view_instance){
            if(controller.isCompleted()){
                confirmationPanel();
            } else{
                instancePanel();
            }
        } else{
            review();
        }

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

    private Panel confirmationButtonsPanel() {
        Panel panel = Panel.horizontalPanel(1).center();

        new Button( "View Submission" , this::review ).addTo( panel );
        new Button( "Submit again" , this::submitAgain ).addTo( panel );
        new Button( "Cancel" , this::close ).addTo( panel );

        return panel;
    }

    private void instancePanel(){
        var root = createPanel().sizeTo(85,25).setLayoutManager(new BorderLayout());
        setComponent(root);
        formTextPanel().setLayoutData(BorderLayout.Location.TOP).addTo(root);
        root.addEmpty();
        QuestionPanel(review).setLayoutData(BorderLayout.Location.CENTER).withBorder(Borders.singleLine("Question " + controller.getQuestion().getIdx() + " of " + controller.numQuestions())).addTo(root);
        root.addEmpty();
        formButtonsPanel(review).setLayoutData(BorderLayout.Location.BOTTOM).addTo(root);
        super.setTitle("Answer the Form");
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
        if(view_instance){
            this.lblUser.setText(controller.u.getName());
            new Label("Submitted by:").addTo(panel);
            lblUser.addTo(panel).addStyle(SGR.BOLD);
        }
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
            textBox = new TextBox().sizeTo(100);
            textBox.setTextChangeListener(( txt, byUser) -> validate());
            textBox.setAfterLeaveFocusListener(( txt, byUser) -> validate());
            if(q.getType() == Question.Type.Date){
                textBox.setValidationPattern(Pattern.compile("[/\\d]{0,10}"));
            }
            if (answer.isValid()){
                textBox.setText(answer.getValue());
            }
            textBox.addTo(panel);
        } else if (q.getType() == Question.Type.Long){
            textBox = new TextBox("", TextBox.Style.MULTI_LINE).sizeTo( 100, 8 );
            textBox.setAfterLeaveFocusListener(( txt, byUser) -> validate());
            if (answer.isValid()){
                textBox.setText(answer.getValue());
            }
            textBox.addTo(panel);
        } else if (q.getType() == Question.Type.Check){
            checkBox.clearItems();
            for (Object i : q.getOptionList().getOptionValues()){
                checkBox.addItem(i);
                if (answer.isValid()){
                    for (Object o : q.getAnswerValues(controller.i)){
                        checkBox.setChecked(o, true);
                    }
                }
            }
            checkBox.setAfterLeaveFocusListener( ( txt, byUser) -> validate() );
            checkBox.addTo(panel);
        } else if (q.getType() == Question.Type.Radio){
            radioBox.clearItems();
            for (var i : q.getOptionList().getOptionValues()){
                radioBox.addItem(i);
                if (answer.isValid()){
                    if(i.toString().compareTo(q.getAnswerValueAsString(controller.i))==0){
                        radioBox.setCheckedItem(i);
                    }
                }
            }
            radioBox.setAfterLeaveFocusListener( ( txt, byUser) -> validate() );
            radioBox.addTo(panel);
        } else if (q.getType() == Question.Type.Combo){
            comboBox.clearItems();
            comboBox.addItem( "--- Please choose an option ---" );
            for (Object i : q.getOptionList().getOptionValues()){
                comboBox.addItem(i);
                if (answer.isValid() && answer.getValue().compareTo( "0" )!=0 && answer.getValue().compareTo( "" )!=0){
                    if(i.toString().compareTo(q.getAnswerValueAsString( controller.i ))==0){
                        comboBox.setSelectedItem(i);}
                }
            }
            comboBox.setAfterLeaveFocusListener(  ( txt, byUser) -> validate() );
            comboBox.addTo(panel);
        }
        panel.addEmpty();
        errBody.addTo(panel);
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
            new Button( "Previous" , this::previous ).addTo( panel );


        } if(controller.getQuestion().getIdx() < controller.numQuestions()){
            new Button( "Next" , this::next ).addTo( panel );
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
        saveAnswer();
        validate();
        Object o = controller.numQuestion;
        if(!errBody.getText().isBlank()){
            if(!questionError.contains( o ))
                questionError.add( controller.numQuestion );
        } else {
            questionError.remove( o );
        }
        if(questionError.isEmpty()){
            if(askConfirmation( "Are you sure you want to submit this form?", "Submit Form" )){
                controller.i.submit();
                MessageDialog.showMessageDialog( getTextGUI(),
                        "Information",
                        "The form has been successfully submitted.",
                        MessageDialogButton.OK);
                this.close();}
        }else {
                MessageDialog.showMessageDialog( getTextGUI(),
                        "Error",
                        "You must correct all errors before submitting the form.",
                        MessageDialogButton.OK);
                controller.numQuestion = questionError.getFirst();
                instancePanel();
            }
        }

    private void previous(){
        controller.previousQuestion();
        instancePanel();
        errBody.setText( "" );
    }

    private void next(){
        if (review){
            controller.nextQuestion();
            instancePanel();
        }else {
            validate();
            Object o = controller.numQuestion;
            if(!errBody.getText().isBlank()){
                if(!questionError.contains( o ))
                    questionError.add( controller.numQuestion );
            } else {
                questionError.remove( o );
            }
            errBody.setText( "" );
            saveAnswer();
        }
    }

    private void saveAnswer(){
        Question q = controller.getQuestion();
        if (q.getType() == Question.Type.Short || q.getType() == Question.Type.Email
                || q.getType() == Question.Type.Date || q.getType() == Question.Type.Long){
            answer.setValue( textBox.getText());
            answer.save();
        } if (q.getType() == Question.Type.Check){
            List answersList = new ArrayList<>( );
            for(Object o : checkBox.getCheckedItems()){
                answersList.add( checkBox.indexOf( o )+1);
            }
            q.saveAnswer( controller.i, answersList );
        } else if (q.getType() == Question.Type.Radio){
            if(!(radioBox.getCheckedItemIndex() == -1)){
                answer.setValue(""+(radioBox.getCheckedItemIndex()+1));
            }answer.save();
        } else if (q.getType() == Question.Type.Combo){
            if(!(comboBox.getSelectedIndex() == 0)){
                answer.setValue(""+ comboBox.getSelectedIndex());
            }
            answer.save();
        }
        if (controller.numQuestion < controller.numQuestions()-1){
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

    private void validate() {
        String txt = "";
        if(controller.getQuestion().getType() == Question.Type.Check){
            if( !checkBox.isEmpty()){
                txt = "C'est bon";
            }
        } else if(controller.getQuestion().getType() == Question.Type.Radio){
            if( radioBox.getCheckedItemIndex() > -1){
                txt = ""+(radioBox.getCheckedItemIndex()+1);
            }
        }else if(controller.getQuestion().getType() == Question.Type.Combo){
            if( comboBox.getSelectedIndex() > 0){
                txt = ""+ comboBox.getSelectedIndex();
            }
        }else {
            txt = textBox.getText();
        }
        var errors = controller.validate( txt );
        if(!errors.isEmpty()){

            errBody.setText(errors.getFirst().getMessage());
        }else
            errBody.setText( "" );
    }

    private Panel createPanel() {
        return Panel.gridPanel(1);
    }

}

