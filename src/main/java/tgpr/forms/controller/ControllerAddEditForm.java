package tgpr.forms.controller;

import tgpr.forms.view.ViewAddEditForm;
import tgpr.framework.Controller;


public class ControllerAddEditForm extends Controller<ViewAddEditForm> {
    private final ViewAddEditForm view = new ViewAddEditForm(); //this?

    @Override
    public ViewAddEditForm getView() {
        return view;
    }
}
