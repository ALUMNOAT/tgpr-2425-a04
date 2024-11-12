package tgpr.forms.controller;

import tgpr.forms.model.Form;
import tgpr.forms.model.Security;
import tgpr.forms.model.User;
import tgpr.forms.view.ViewFormsView;
import tgpr.framework.Controller;

public class ViewFormsController extends Controller<ViewFormsView> {

    private User user;
    private ViewFormsView view;


    public ViewFormsController(User user) {
        this.user = user;
        view = new ViewFormsView(user,this);

    }

    public void manageOptionLists() {
        navigateTo(new ManageOptionListsController());
    }


    @Override
    public ViewFormsView getView() {
        return view;
    }

    public void manage(Form form) {
        Controller.navigateTo(new ViewFormController(form));
    }

    public void logOut() {
        Security.logout();
        navigateTo(new LoginController());
    }

    public void exit() {
        System.exit(0);
    }
}
