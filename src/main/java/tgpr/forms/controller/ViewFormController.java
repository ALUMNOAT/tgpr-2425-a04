package tgpr.forms.controller;

import tgpr.forms.view.TestView;
import tgpr.framework.Controller;
import tgpr.framework.Model;
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

    public void delete() {
        if(askConfirmation("Are you sure you want to delete this form?", "Delete Form")) {
            form.delete();
            view.close();
        }
    }

    @Override
    public ViewFormView getView() {
        return view;
    }

    /*
    public void delete() {
        if (Security.isLoggedUser(member))
            showError("You may not delete yourself!");
        else if (askConfirmation("You are about to delete this member. Please confirm.", "Delete member")) {
            member.delete();
            view.close();
        }
    }

    public Form update() {
        var controller = new EditMemberController(member);
        navigateTo(controller);
        return controller.getMember();
    }

    public void toggleFollow() {
        Security.getLoggedUser().toggleFollowUnfollow(member);
    }

    public boolean deleteMessage(Message message) {
        if (message == null) return false;
        if (!message.canDelete(Security.getLoggedUser()))
            showError("You're not allowed to delete this message.");
        else if (askConfirmation("Are you sure you want to delete this message?", "Delete Message")) {
            message.delete();
            return true;
        }
        return false;
    }

    public void postMessage() {
        navigateTo(new AddMessageController(member));
    }

    public Message displayMessage(Message message) {
        var controller = new DisplayMessageController(message);
        navigateTo(controller);
        return controller.getMessage();
    }
     */
}
