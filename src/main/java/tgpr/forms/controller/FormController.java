package tgpr.forms.controller;

import tgpr.forms.model.Form;
import tgpr.forms.model.Instance;
import tgpr.forms.model.User;
import tgpr.forms.model.Instance;
import tgpr.forms.view.FormView;
import tgpr.framework.Controller;


public class FormController extends Controller<FormView>{

    private Form form;
    private FormView view = new FormView(this);

    public FormView getView(){
        return view;
    }

    public String getFormTitle(){

        return form.getTitle();
    }

    public String getFormDescription(){

        return form.getDescription();
    }

    public User GetFormOwner(){

        return form.getOwner();
    }

    public Instance GetRecentInstance(){

        return form.getMostRecentInstance(form.getOwner());
    }


    public boolean hasNoQuestions(){
        return (form.getQuestions()).isEmpty();
    }










}
