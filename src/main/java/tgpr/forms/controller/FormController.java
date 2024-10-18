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
        view = new FormView(form, this);

    }

    public FormView getView(){
        return view;
    }



    public boolean hasNoQuestions(){
        return (form.getQuestions()).isEmpty();
    }



    public AccessType getAccessType(){

        return form.getAccessType(form.getOwner());
    }




}
