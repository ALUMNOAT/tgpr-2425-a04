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
        for(UserFormAccess userFormAccess : ls) {
            if(userFormAccess.getAccessType() == AccessType.User)
                userFormAccess.delete();
        }
    }

    public boolean askConfirmationPublic() {
        return askConfirmation(
                "Rendering the form public will delete accesses",
                "Render the form public ?");
    }

    public void createUpdate(String title, String description, boolean isPublic) {
        //# NEW FORM #
        if(form == null) {
            form = new Form(title, description, Security.getLoggedUser(), isPublic);
            form.save();
            view.close();
        }
        //# UPDATE FORM #
        else {
            form.setTitle(title);
            form.setDescription(description);
            if(!isPublic || form.getUserFormAccesses().isEmpty() || askConfirmationPublic()) {
                publicDeletesUserAccesses();
                form.setIsPublic(isPublic);
                form.save();
                view.close();
            }
        }
    }

    public ErrorList validate(String title){
        var errors = new ErrorList();
        errors.add(FormValidator.isValidTitle(title));
        errors.add(FormValidator.isNotTakenTitle(title, form));
        return errors;
    }
}
