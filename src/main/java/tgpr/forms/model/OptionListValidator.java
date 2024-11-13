package tgpr.forms.model;

import tgpr.framework.Error;

import java.util.List;
import java.util.regex.Pattern;

public abstract class OptionListValidator {
    public static Error isValidName(String name){
        if(name == null)
            return Error.NOERROR;
        if(!Pattern.matches(".{1,100}", name))
            return new Error("name required", OptionList.Fields.Name);
        return Error.NOERROR;
    }

    public static Error hasOneValue(List<OptionValue> values){
        if(values == null)
            return Error.NOERROR;
        if(values.isEmpty())
            return new Error("at least one value required", OptionValue.Fields.Label);

        return Error.NOERROR;
    }
}
