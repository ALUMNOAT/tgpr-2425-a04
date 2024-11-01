package tgpr.forms.controller;

import tgpr.forms.model.Form;
import tgpr.forms.model.OptionList;
import tgpr.forms.model.Question;
import tgpr.forms.model.QuestionValidator;
import tgpr.forms.view.AddEditQuestionView;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;

public class AddEditQuestionController extends Controller<AddEditQuestionView> {
    private final AddEditQuestionView view;
    private Question question;
    private final boolean isNew;
    private final Form form;

    public AddEditQuestionController(Form form) {
        this(form, null);
    }

    public AddEditQuestionController(Form form, Question question) {
        this.form = form;
        this.question = question;
        isNew = question == null;
        view = new AddEditQuestionView(this, question);
    }

    public void saveQuestion(String title, String description, String type, boolean required, OptionList optionList) {
        if(isNew) {
            question = new Question();
            question.setFormId(form.getId());
            question.setIdx(form.getNextIdx());
        }
        question.setTitle(title);
        question.setDescription(description);
        question.setType(Question.Type.valueOfIgnoreCase(type));
        question.setRequired(required);
        question.setOptionListId(optionList == null ? null : optionList.getId());

        question.save();
        view.close();
    }

    public void deleteQuestion() {
        if(askConfirmation("Are you sure you want to delete this question?", "Delete Question")) {
            question.delete();
            view.close();
        }
    }

    public OptionList addOptionList() {
        //TODO: add_edit_option_list
        return null;
    }

    public ErrorList validate(String title, String description, String type, OptionList optionList) {

        var errors = new ErrorList();
        errors.addAll(QuestionValidator.validate(title, isNew ? null : question.getTitle(),
                description, type, optionList, form));
        return errors;
    }

    @Override
    public AddEditQuestionView getView() {
        return view;
    }
}
