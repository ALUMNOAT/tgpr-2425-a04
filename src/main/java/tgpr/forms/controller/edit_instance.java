package tgpr.forms.controller;

import tgpr.forms.model.*;
import tgpr.forms.view.view_edit_instance;
import tgpr.framework.Controller;
import tgpr.framework.Error;
import tgpr.framework.ErrorList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tgpr.forms.model.Answer.Fields.Instance;
import static tgpr.framework.LocaleFormats.INPUT_DATE_FORMATTER;

public class edit_instance extends Controller<view_edit_instance> {

    public User u = User.getByKey(8);
    public Form f = Form.getByKey(1 );
    public Instance i;
    public int numQuestion = 0;


    public String description = f.getDescription();
    public String getDescription() {
        return description;
    }

    public String date;
    public String getDate() {
        return date;
    }

    public String title = f.getTitle();
    public String getTitle() {
        return title;
    }


    public boolean isCompleted() {
        return i.isCompleted();
    }

    public Question getQuestion() {
        return f.getQuestions().get(numQuestion);
    }

    public Answer getAnswer() {
        return i.getAnswers().get(numQuestion);
    }

    public int numQuestions() {
        return f.getQuestions().size();
    }

    public void nextQuestion() {
        numQuestion++;
    }

    public void previousQuestion() {
        numQuestion--;
    }

    public ErrorList validate( String body) {
        var errors = new ErrorList();
        if(!(!getQuestion().getRequired() || body != null && !body.isBlank())){
            errors.add(  new Error("This question is required"));
        } if (getQuestion().getType() == Question.Type.Date){
            try {
                assert body != null;
                LocalDate.parse(body, INPUT_DATE_FORMATTER);
            } catch (Exception e) {
                errors.add(  new Error("Invalide date format"));
            }
        } if(getQuestion().getType() == Question.Type.Email){
            String regex = "^[\\w\\.-]+@[a-zA-Z\\d\\.-]+\\.[a-zA-Z]{2,}$";

            Pattern pattern = Pattern.compile(regex);
            assert body != null;
            if(!pattern.matcher(body).matches()){
                errors.add(  new Error("Invalide email format"));
            }
        }
        return errors;
    }

    @Override
    public view_edit_instance getView() {
        if (u.isGuest()){
            i = f.createInstance(u);
        } else {
            i = f.getMostRecentInstance(u);
            if(i == null){
                i = f.getCurrentOrCreateInstance(u);
            }
        }
        date = i.getStarted().format( DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss") );
        return new view_edit_instance(this);
    }
}
