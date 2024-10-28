package tgpr.forms.controller;

import tgpr.forms.model.Security;
import tgpr.forms.model.User;
import tgpr.forms.view.ViewFormsView;
import tgpr.framework.Controller;

public class ViewFormsController extends Controller<ViewFormsView> {

    private User user;
    private ViewFormsView view;


    public ViewFormsController(User user) {
        this.user = user;
        //Security.login(user);
        view = new ViewFormsView(user, this);

    }

    @Override
    public ViewFormsView getView() {
        return view;
    }


}
