package tgpr.forms.controller;

import com.googlecode.lanterna.gui2.Panel;
import tgpr.forms.model.*;
import tgpr.forms.model.Instance;
import tgpr.forms.view.FormView;
import tgpr.forms.view.ViewFormsView;
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

    public Panel getFormCard(){
        return view.getFormCard();
    }


}
