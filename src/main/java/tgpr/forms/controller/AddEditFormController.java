package tgpr.forms.controller;

import tgpr.forms.model.Security;
import tgpr.forms.view.AddEditFormView;
import tgpr.framework.Controller;
import tgpr.forms.model.Form;


public class AddEditFormController extends Controller<AddEditFormView> {

    private final AddEditFormView view = new AddEditFormView(this); //this?
    private Form form;
    private boolean isNewForm;

    public AddEditFormController(Form form){
        this.form = form;
        isNewForm = form == null;
    }

    @Override
    public AddEditFormView getView() {
        return view;
    }

    public void setForm(String title, String description, boolean isPublic) {
        System.out.println("this works so far !");
        form = new Form(title, description, Security.getLoggedUser(), isPublic);
        form.save();
        view.close();
    }
}
