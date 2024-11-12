package tgpr.forms.model;

import tgpr.framework.Error;

import java.util.regex.Pattern;

public abstract class OptionValueValidator {
    public static Error notEmptyLabel(String label) {
        if(label == null)
            return Error.NOERROR;
        if(label.isBlank())
            return new Error("cannot be blank", OptionValue.Fields.Label);
        return Error.NOERROR;
    }
}
