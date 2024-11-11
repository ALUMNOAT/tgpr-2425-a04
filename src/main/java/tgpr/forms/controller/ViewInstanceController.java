package tgpr.forms.controller;


import tgpr.forms.model.Form;
import tgpr.forms.view.ViewInstanceView;
import tgpr.framework.Controller;


public class ViewInstanceController extends Controller<ViewInstanceView> {
    public Form form;

    public ViewInstanceController( Form form) {
        this.form = form;
    }


    @Override
    public ViewInstanceView getView() {
        return new ViewInstanceView(this);
    }
}
