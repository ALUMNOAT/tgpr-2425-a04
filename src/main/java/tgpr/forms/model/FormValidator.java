package tgpr.forms.model;

import tgpr.framework.Error;
import tgpr.framework.ErrorList;

import java.util.regex.Pattern;

public abstract class FormValidator {
    public static Error isValidTitle(String title){
        if(title == null)
            return Error.NOERROR;
        if(!Pattern.matches("[a-zA-Z\\d][a-zA-Z\\d\\s]{0,21}", title))
            return new Error("Invalid title format", Form.Fields.Title);
        return Error.NOERROR;
    }

    public static Error isNotTakenTitle(String title, Form form){
        Form dbForm = Form.getByTitleAndUser(title, Security.getLoggedUser());
        if(dbForm != null && dbForm.getOwner().equals(Security.getLoggedUser()))
            return new Error("This title is already taken", Form.Fields.Title);
        return Error.NOERROR;
    }
}
