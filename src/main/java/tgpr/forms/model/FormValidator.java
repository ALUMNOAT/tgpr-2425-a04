package tgpr.forms.model;

import tgpr.framework.Error;
import tgpr.framework.ErrorList;

import java.util.regex.Pattern;

public abstract class FormValidator {
    public static Error isValidTitle(String title){
        if(title == null)
            return Error.NOERROR;

        if(title.isBlank())
            return new Error("title required", Form.Fields.Title);
        else if(!Pattern.matches("[a-zA-Z\\d][a-zA-Z\\d\\s]{2,21}", title))
            return new Error("Invalid title format", Form.Fields.Title);

        return Error.NOERROR;
    }

    public static Error isValidDescription(String description){
        if(description == null)
            return Error.NOERROR;

        if(description.isBlank())
            return new Error("Description required", Form.Fields.Description);
        else if(!Pattern.matches(".{3,21}", description))
            return new Error("Description requires at least 3 char", Form.Fields.Description);

        return Error.NOERROR;
    }

    public static Error isNotTakenTitle(String title, Form form){
        Form dbForm = Form.getByTitleAndUser(title, Security.getLoggedUser());
        if(dbForm != null && dbForm.getOwner().equals(Security.getLoggedUser())) {
            if(form == null || dbForm.getId() != form.getId())
                return new Error("This title is already taken", Form.Fields.Title);
        }
        return Error.NOERROR;
    }
}
