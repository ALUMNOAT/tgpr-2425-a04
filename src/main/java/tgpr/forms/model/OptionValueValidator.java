package tgpr.forms.model;

import tgpr.framework.Error;

import java.util.regex.Pattern;

public abstract class OptionValueValidator {
    public static Error isValidLabel(String label){
        if(label == null)
            return Error.NOERROR;
        if(!Pattern.matches(".{1,100}", label))
            return new Error("at least one value required", OptionValue.Fields.Label);
        return Error.NOERROR;
    }
}
