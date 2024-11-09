package tgpr.forms.model;

import tgpr.framework.Error;

import java.util.regex.Pattern;

public abstract class OptionListValidator {
    public static Error isValidName(String name){
        if(name == null)
            return Error.NOERROR;
        if(!Pattern.matches(".{1,100}", name))
            return new Error("name required", OptionList.Fields.Name);
        return Error.NOERROR;
    }
}
