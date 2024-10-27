package tgpr.forms.controller;

import tgpr.forms.model.Question;
import tgpr.forms.view.AddEditQuestionView;
import tgpr.framework.Controller;

public class AddEditQuestionController extends Controller<AddEditQuestionView> {
    private final AddEditQuestionView view;
    private Question question;
    private final boolean isNew;

    public AddEditQuestionController() {
        this(null);
    }

    public AddEditQuestionController(Question question) {
        this.question = question;
        isNew = question == null;
        view = new AddEditQuestionView(this, question);
    }

    @Override
    public AddEditQuestionView getView() {
        return view;
    }
}
