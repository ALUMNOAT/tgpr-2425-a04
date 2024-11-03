package tgpr.forms.controller;

import tgpr.forms.model.*;
import tgpr.forms.view.view_edit_instance;
import tgpr.framework.Controller;

import java.time.format.DateTimeFormatter;

import static tgpr.forms.model.Answer.Fields.Instance;

public class edit_instance extends Controller<view_edit_instance> {

    public User u = User.getByKey(8);
    public Form f = Form.getByKey(15);
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
