package tgpr.forms.controller;

import tgpr.forms.model.*;
import tgpr.forms.view.AddEditFormView;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;

import java.util.List;


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
    private void publicDeletesUserAccesses() {
        List<UserFormAccess> ls = form.getUserFormAccesses();
        for(UserFormAccess user : ls) {
            if(user.getAccessType() == AccessType.User)
                user.delete();
        }
    }

    public void createUpdate(String title, String description, boolean isPublic) {
        form = new Form(title, description, Security.getLoggedUser(), isPublic);
        form.save();
        if(isPublic) {
            publicDeletesUserAccesses();
        }
        view.close();
    }

    public ErrorList validate(String title){
        var errors = new ErrorList();
        errors.add(FormValidator.isValidTitle(title));
        errors.add(FormValidator.isNotTakenTitle(title, form));
        return errors;
    }
}
