package tgpr.forms.controller;

import tgpr.forms.view.AnalyzeView;
import tgpr.framework.Controller;
import tgpr.forms.model.Form;

public class AnalyzeController extends Controller<AnalyzeView> {
    private final AnalyzeView view;
    private final Form form;

    public AnalyzeController(Form form){
        this.form = form;
        view = new AnalyzeView(this, form);
    }

    public String getTitle(){
        return form.getTitle();
    }

    public String getDescription(){
        return form.getDescription();
    }

    public String getInstances(){
        return form.getInstances().toString();
    }

    @Override
    public AnalyzeView getView(){
        return view;
    }

    public void viewInstances(){
        navigateTo(new ViewInstanceController(form));
    }

}
