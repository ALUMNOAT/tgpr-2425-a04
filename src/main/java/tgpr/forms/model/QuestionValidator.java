package tgpr.forms.model;

import tgpr.framework.Error;
import tgpr.framework.ErrorList;

import java.util.ArrayList;
import java.util.List;

public abstract class QuestionValidator {

    public static Error isValidTitle(String title) {
        if (title == null || title.isBlank())
            return new Error("title required", Question.Fields.Title);
        if (title.length() < 3)
            return new Error("minimum 3 chars", Question.Fields.Title);
        return Error.NOERROR;
    }

    public static Error isValidAvailableTitle(String newTitle, String oldTitle, Form form) {
        var error = isValidTitle(newTitle);
        if (error != Error.NOERROR)
            return error;
        List<String> questionTitles = new ArrayList<>();
        for(Question question : form.getQuestions())
            questionTitles.add(question.getTitle());
        if(oldTitle != null)
            questionTitles.remove(oldTitle);
        if(questionTitles.contains(newTitle))
            return new Error("title already exists", Question.Fields.Title);
        return Error.NOERROR;
    }

    public static Error isValidDescription(String description) {
        if (!(description == null || description.isBlank()) && description.length() < 3)
            return new Error("minimum 3 chars", Question.Fields.Description);
        return Error.NOERROR;
    }

    public static Error isValidOptionList(String type, OptionList optionList) {
        if(optionList == null && Question.Type.valueOfIgnoreCase(type).requiresOptionList())
            return new Error("required for this type", Question.Fields.OptionList);
        if(optionList != null && !Question.Type.valueOfIgnoreCase(type).requiresOptionList())
            return new Error("not required for this type", Question.Fields.OptionList);
        return Error.NOERROR;
    }

    public static List<Error> validate(Question question, String oldTitle) {
        var errors = new ErrorList();

        errors.add(isValidAvailableTitle(question.getTitle(), oldTitle, question.getForm()));
        errors.add(isValidDescription(question.getDescription()));
        errors.add(isValidOptionList(question.getType().toString(), question.getOptionList()));

        return errors;
    }
}
