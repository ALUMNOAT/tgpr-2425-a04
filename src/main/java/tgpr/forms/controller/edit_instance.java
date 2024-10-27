package tgpr.forms.controller;

import tgpr.forms.model.Form;
import tgpr.forms.model.Instance;
import tgpr.forms.model.Question;
import tgpr.forms.model.User;
import tgpr.forms.view.view_edit_instance;
import tgpr.framework.Controller;

import static tgpr.forms.model.Answer.Fields.Instance;

public class edit_instance extends Controller<view_edit_instance> {

    public User u = User.getByKey(9);
    public Form f = Form.getByKey(1);
    public Instance i = f.getInstances().get(2);
    public int numQuestion = 0;

    public boolean isCompleted = i.isCompleted();

    public String description = f.getDescription();
    public String getDescription() {
        return description;
    }

    public String date = i.getStarted().toString();
    public String getDate() {
        return date;
    }

    public String title = f.getTitle();
    public String getTitle() {
        return title;
    }


    public boolean isCompleted() {
        return isCompleted;
    }

    public Question getQuestion() {
        return f.getQuestions().get(numQuestion);
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
        return new view_edit_instance(this);
    }
}
