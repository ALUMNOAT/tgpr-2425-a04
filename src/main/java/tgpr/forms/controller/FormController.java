package tgpr.forms.controller;

import tgpr.forms.model.*;
import tgpr.forms.model.Instance;
import tgpr.forms.view.FormView;
import tgpr.framework.Controller;

import java.time.LocalDateTime;


public class FormController extends Controller<FormView>{

    private Form form;

    private FormView view;

    public FormController(Form form) {
        this.form= form;
        view= new FormView(form, this);

    }

    public FormView getView(){
        return view;
    }

    public String getFormTitle(){

        return form.getTitle();
    }

    public String getFormDescription(){

        return form.getDescription();
    }

    public User getFormOwner(){

        return form.getOwner();
    }


    public Instance getRecentInstance(){

        return form.getMostRecentInstance(form.getOwner());
    }

    public LocalDateTime getRecentInstanceDate(){
        var instance = form.getMostRecentInstance(form.getOwner());

        if(instance == null) {
            return null;
        }
        return (form.getMostRecentInstance(form.getOwner())).getStarted();
    }


    public LocalDateTime getInstanceSubmission(){

        var instance = getRecentInstance();

        if(instance!=null && instance.getCompleted() == null) {
            return null;
        }

        return (form.getMostRecentInstance(form.getOwner())).getCompleted();

    }


    public boolean hasNoQuestions(){
        return (form.getQuestions()).isEmpty();
    }



    public AccessType getAccessType(){

        return form.getAccessType(form.getOwner());
    }




}
