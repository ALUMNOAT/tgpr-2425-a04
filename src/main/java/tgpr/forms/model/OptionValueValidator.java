package tgpr.forms.model;

import tgpr.framework.Error;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public abstract class OptionValueValidator {
    public static Error notEmptyLabel(String label) {
        if(label == null)
            return Error.NOERROR;
        if(label.isBlank())
            return new Error("cannot be blank", OptionValue.Fields.Label);
        return Error.NOERROR;
    }

    public static Error uniqueLabelIgnoreCase(List<OptionValue> values, String label) {
        if(label == null)
            return Error.NOERROR;
        if(labels(values).contains(label.toLowerCase()))
            return new Error("Label already exists/has similar case", OptionValue.Fields.Label);
        return Error.NOERROR;
    }

    private static List<String> labels(List<OptionValue> values) {
        List<String> labels = new ArrayList<>();
        for(OptionValue value : values)
            labels.add(value.getLabel().toLowerCase());
        return labels;
    }
}
