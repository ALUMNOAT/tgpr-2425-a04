package tgpr.forms.model;

import tgpr.framework.Error;

import java.util.List;
import java.util.regex.Pattern;

public abstract class OptionListValidator {
    public static Error isValidName(String name, int olID){
        OptionList dbOptionList = OptionList.getByNameAndUser(name, Security.getLoggedUser());
        if(name == null)
            return Error.NOERROR;
        else if(name.isBlank())
            return new Error("name required", OptionList.Fields.Name);
        else if(!Pattern.matches("[\\Wa-zA-Z\\d][\\Wa-zA-Z\\d\\s]{2,}", name))
            return new Error("name requires 3 char minimum", OptionList.Fields.Name);
        else if(!Pattern.matches("[\\Wa-zA-Z\\d\\s]{0,21}", name))
            return new Error("name cannot be longer than 21 char", OptionList.Fields.Name);
        else if(dbOptionList != null && olID != dbOptionList.getId())
            return new Error("name already taken", OptionList.Fields.Name);
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
