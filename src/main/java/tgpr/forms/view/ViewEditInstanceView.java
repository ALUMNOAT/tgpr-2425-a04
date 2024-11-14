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

    private final ViewEditInstanceController controller;

    //Labels
    private final Label errBody = new Label("");
    private final Label lblTitle = new Label("");
    private final Label lblDescription = new Label("");
    private final Label lblStart = new Label("");
    private final Label lblSubmitted = new Label("");
    private final Label lblUser = new Label("");

    private Answer answer;
    public List<Integer> questionError = new ArrayList<>();

    //différentes boxs
    private TextBox textBox;
    private final CheckBoxList checkBox = new CheckBoxList();
    private final RadioBoxList radioBox = new RadioBoxList();
    private final ComboBox comboBox = new ComboBox();

    public ViewEditInstanceView( ViewEditInstanceController controller) {
        super("Open a form");
        this.controller = controller;

        //initialisation des variables
        errBody.setForegroundColor(TextColor.ANSI.RED);
        setHints(List.of(Hint.CENTERED, Hint.MODAL));
        this.lblTitle.setText( controller.getTitle());
        this.lblDescription.setText(controller.getDescription());

        if (!controller.view_instance){
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
        /*
          gestion fenetre si la dernière instances est déjà soumise
         */
        var root = createPanel();
        setComponent(root);
        root.addEmpty();
        confirmationTextPanel().addTo(root);
        root.addEmpty();
        confirmationButtonsPanel().addTo(root);
    }

    private Panel confirmationTextPanel() {
        /*
          gestion texte si la dernière instances est déjà soumise
         */
        Panel panel = Panel.horizontalPanel();
        new Label("You have already answered this form."
                +System.lineSeparator()+"You can view your submission or submit again."
                +System.lineSeparator()+"What would you like to do?")
                .addTo(panel);
        return panel;
    }

    private Panel confirmationButtonsPanel() {
        /*
          gestion boutons si la dernière instances est déjà soumise
         */
        Panel panel = Panel.horizontalPanel(1).center();

        new Button( "View Submission" , this::review ).addTo( panel );
        new Button( "Submit again" , this::submitAgain ).addTo( panel );
        new Button( "Cancel" , this::close ).addTo( panel );

        return panel;
    }

    private void instancePanel(){
        /*
          gestion de la fenetre
         */
        var root = Panel.verticalPanel(1);
        setComponent(root);

        formTextPanel().addTo(root);
        QuestionPanel().withBorder(Borders.singleLine("Question " + (controller.numQuestion+1) + " of " + controller.numQuestions())).addTo(root);
        formButtonsPanel().addTo(root);

        super.setTitle(controller.review?"View Answers":"Answer the Form");
    }

    private Panel formTextPanel() {
        /*
          gestion titres et informations de l'instance
         */
        Panel panel = Panel.gridPanel(2, Margin.of(1, 1, 1, 0));

        new Label("Title:").addTo(panel);
        lblTitle.addTo(panel).addStyle( SGR.BOLD);

        new Label("Description:").addTo(panel);
        lblDescription.addTo(panel).addStyle(SGR.BOLD);

        new Label("Started on:").addTo(panel);
        lblStart.setText(controller.i.getStarted().format( DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss") ));
        lblStart.addTo(panel).addStyle(SGR.BOLD);

        if(controller.review){
            new Label("Submitted on:").addTo(panel);
            lblSubmitted.addTo(panel).addStyle(SGR.BOLD);
        }
        if(controller.view_instance){
            this.lblUser.setText(controller.u.getName());
            new Label("Submitted by:").addTo(panel);
            lblUser.addTo(panel).addStyle(SGR.BOLD);
        }
        return panel;
    }

    private Panel QuestionPanel(){
        /*
          gestion panel de la question
         */
        Panel panel = Panel.verticalPanel().sizeTo( 80,19 );
        panel.addEmpty();
        titlePanel().addTo(panel);
        panel.addEmpty();
        if(controller.review){
            reviewQuizPanel().addTo(panel);
        } else {
            QuizPanel().addTo(panel);
        }
        return panel;
    }

    private Panel titlePanel(){
        /*
          Panel du titre de la question et affiche si requis ou non
         */
        Panel panel = Panel.horizontalPanel();
        Question q = controller.getQuestion();
        answer = controller.i.getOrCreateAnswer(q);
        Label quest = new Label( q.getTitle());
        if (q.getRequired()){
            Label req = new Label("(*)");
            req.setForegroundColor(TextColor.ANSI.RED).addTo(panel);
        }
        quest.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT).addTo(panel);
        return panel;
    }

    private Panel QuizPanel(){
        /*
          affiche la question avec une box pour la reponse en fonction de son type
         */
        Panel panel = Panel.verticalPanel(  );
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
            textBox.setTextChangeListener( ( txt, byUser) -> validate() );
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
            checkBox.addListener( ( txt, byUser) -> validate() );
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
            radioBox.addListener( ( txt, byUser) -> validate() );
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
        errBody.bottom().addTo(panel);
        return panel;
    }

    private Panel reviewQuizPanel(){
        /*
          En lecture de l'instance, affiche les réponses
         */
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

    private Panel formButtonsPanel() {
        /*
          Gestion des boutons du formulaire
         */
        Panel panel = new Panel()
                .setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        if(controller.u.isGuest() && !controller.review){
            Button btnClose = new Button( "Close" , this::cancel ).addTo( panel );
            addShortcut(btnClose, KeyStroke.fromString("<esc>" ));
        } else if(controller.review){
            Button btnClose = new Button( "Close" , this::close ).addTo( panel );
            addShortcut(btnClose, KeyStroke.fromString("<esc>" ));
        }else{
            Button btnClose = new Button( "Close" , this::closeSave ).addTo( panel );
            addShortcut(btnClose, KeyStroke.fromString("<esc>" ));
        }
        if(controller.numQuestion > 0){
            Button btnPrev =new Button( "Previous" , this::previous ).addTo( panel );
            addShortcut(btnPrev, KeyStroke.fromString("<pagedown>" ));

        } if(controller.numQuestion+1 < controller.numQuestions()){
            Button btnNext =new Button( "Next" , this::next ).addTo( panel );
            addShortcut(btnNext, KeyStroke.fromString("<pageup>" ));
        } else  {
            if(!controller.review){
                new Button( "Submit" , this::submit ).addTo( panel );
            }
        }
        if(!controller.review){
            new Button( "Cancel" , this::cancel ).addTo( panel );
        }
        return panel;
    }

    private void cancel(){
        /*
          Demande confirmation avant de supprimer l'instance, automique pour Guest quand il quitte l'instance
         */
        if (askConfirmation("You are about to delete this instance and its answers?", "Delete Instance")) {
            controller.i.delete();
            close();
        }
    }


    private void submit(){
        /*
          Sauvegarde l'instance et la soumet
         */
        saveAnswer();
        validate();
        Object o = controller.numQuestion;
        if(!errBody.getText().isBlank()){
            if(errBody.getText().compareTo( "This question is required" ) == 0){
                if(!questionError.contains( o ))
                    questionError.add( controller.numQuestion );
            } else {
                textBox.setText( "" );
            }
            }else {
            questionError.remove( o );
        }
        if(questionError.isEmpty()){
            /*
          Si il n'y a pas de question avec des réponses non valide
         */
            if(askConfirmation( "Are you sure you want to submit this form?", "Submit Form" )){
                controller.i.submit();
                MessageDialog.showMessageDialog( getTextGUI(),
                        "Information",
                        "The form has been successfully submitted.",
                        MessageDialogButton.OK);
                this.close();}
        }else {
            /*
          Si au moins une question a une réponse non valide
         */
                MessageDialog.showMessageDialog( getTextGUI(),
                        "Error",
                        "You must correct all errors before submitting the form.",
                        MessageDialogButton.OK);
                controller.numQuestion = questionError.getFirst();
                instancePanel();
            }
        }

    private void previous(){
        /*
          retourne à la question précedante, efface message error
         */
        controller.previousQuestion();
        instancePanel();
        errBody.setText( "" );
    }

    private void next(){
        /*
          passe à la prochaine question:
          if (!review):
              -Vérifie si la réponse est valide
              -Ajoute la question à la liste des question non validé si réponse non valide
              -Sauvegarde la réponse
         */
        if (controller.review){
            controller.nextQuestion();
            instancePanel();
        }else {
            validate();
            Object o = controller.numQuestion;
            if(!errBody.getText().isBlank()){
                if(errBody.getText().compareTo( "This question is required" ) == 0){
                    if(!questionError.contains( o )){
                        questionError.add( controller.numQuestion );
                        saveAnswer();
                    }
                } else {
                    textBox.setText( "" );
                    controller.nextQuestion();
                    instancePanel();
                }
            } else {
                questionError.remove( o );
                saveAnswer();
            }
            errBody.setText( "" );

        }
    }

    private void saveAnswer(){
        /*
          sauvegarde la réponse dépendant du type de la question. Dans le cas de check,
          sauvegarde chaque réponse dans un Answer() different.
         */
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
        /*
          Crée une nouvelle instance et lance le questionnaire pour cette instance
         */
        controller.i = controller.f.createInstance(controller.u);
        instancePanel();
    }

    private void review(){
        /*
          fonction lanncé dans le cas de la relecture d'une instance
         */
        this.lblSubmitted.setText( controller.i.getCompleted().format( DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        controller.review = true;
        instancePanel();
    }

    private void validate() {
        /*
          recupere la réponse et appelle la fonction validate(String txt) du controller avec en paramètre
          la réponse sous forme de String.
          Dans le cas de check, vérifie qu'il y a au moins une réponse si il y a une réponse envoie "c'est bon"
           sinon envoie un String blank
         */
        String txt = "";
        if(controller.getQuestion().getType() == Question.Type.Check){
            if(!checkBox.getCheckedItems().isEmpty()){
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

    public void closeSave(){
        /*
          ferme la fonction en sauvegardant la réponse
         */
        saveAnswer();
        close();
    }

    private Panel createPanel() {
        return Panel.gridPanel(1);
    }

}

