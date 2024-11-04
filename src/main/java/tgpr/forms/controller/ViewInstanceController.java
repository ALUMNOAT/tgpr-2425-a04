package tgpr.forms.controller;


import tgpr.forms.view.ViewInstanceView;
import tgpr.framework.Controller;


public class ViewInstanceController extends Controller<ViewInstanceView> {


    @Override
    public ViewInstanceView getView() {
        return new ViewInstanceView(this);
    }
}
