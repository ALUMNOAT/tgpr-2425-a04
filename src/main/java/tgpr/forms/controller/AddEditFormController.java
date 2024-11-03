package tgpr.forms.controller;

import tgpr.forms.model.FormValidator;
import tgpr.forms.model.Security;
import tgpr.forms.view.AddEditFormView;
import tgpr.framework.Controller;
import tgpr.forms.model.Form;
import tgpr.framework.ErrorList;


public class AddEditFormController extends Controller<AddEditFormView> {

    private Form form;
    private final AddEditFormView view; //this?

    public AddEditFormController(Form form){
        this.form = form;
        view = new AddEditFormView(this, form);
    }

    @Override
    public AddEditFormView getView() {
        return view;
    }

    public void createUpdate(String title, String description, boolean isPublic) {
        System.out.println("this works so far !");
        form = new Form(title, description, Security.getLoggedUser(), isPublic);
        form.save();
        view.close();
    }

    public ErrorList validate(String title){
        var errors = new ErrorList();
        errors.add(FormValidator.isValidTitle(title));
        errors.add(FormValidator.isNotTakenTitle(title, form));
        return errors;
    }
}
