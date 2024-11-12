package tgpr.forms.controller;

import tgpr.framework.Controller;
import tgpr.forms.model.Form;
import tgpr.forms.model.Question;
import tgpr.forms.view.ViewFormView;

import java.util.List;

public class ViewFormController extends Controller<ViewFormView> {
    private final ViewFormView view;
    private final Form form;

    public ViewFormController(Form form) {
        this.form = form;
        view = new ViewFormView(this, form);
    }

    public List<Question> getQuestions() {
        return form.getQuestions();
    }

    public void togglePublic() {
        if(form.getIsPublic())
            form.togglePublic();
        else
            if(form.isSharedToUser()) {
                if(askConfirmation("Are you sure you want to make this form public? " +
                        "This will delete all existing shares", "Confirmation")) {
                    form.togglePublic();
                }
            }
            else
                form.togglePublic();
    }

    public void save(List<Question> questions) {
       form.reorderQuestions(questions);
    }

    public void deleteForm() {
        if(askConfirmation("Are you sure you want to delete this form?", "Delete Form")) {
            form.delete();
            view.close();
        }
    }

    public void addQuestion(Form form) {
        navigateTo(new AddEditQuestionController(form));
    }

    public void editQuestion(Question question) {
        navigateTo(new AddEditQuestionController(form, question));
    }

    public void editForm() {
        navigateTo(new AddEditFormController(form));
    }

    public void share() {
        navigateTo(new ManageSharesController(form));
    }

    public void analyze() {
        //TODO: analyze
    }

    public void clear() {
        //TODO: view_instances
    }

    @Override
    public ViewFormView getView() {
        return view;
    }
}
