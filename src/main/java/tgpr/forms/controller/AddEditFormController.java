package tgpr.forms.controller;

import tgpr.forms.view.AddEditFormView;
import tgpr.framework.Controller;


public class AddEditFormController extends Controller<AddEditFormView> {
    private final AddEditFormView view = new AddEditFormView(); //this?

    @Override
    public AddEditFormView getView() {
        return view;
    }
}
