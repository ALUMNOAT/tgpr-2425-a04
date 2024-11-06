package tgpr.forms.controller;

import tgpr.forms.model.DistListFormAccess;
import tgpr.forms.model.Form;
import tgpr.forms.model.UserFormAccess;
import tgpr.forms.view.ManageSharesView;
import tgpr.framework.Controller;
import tgpr.framework.Model;


public class ManageSharesController extends Controller<ManageSharesView> {

    private final ManageSharesView view;

    private final Form form;

    public ManageSharesController(ManageSharesView view, Form form) {
        this.view = view;
        this.form = form;
    }

    @Override
    public ManageSharesView getView() {
        return view;
    }


    public void deleteShare(Model share) {
        if (askConfirmation("Are you sure you want to delete this share?", "Delete Share")) {
            if (share instanceof UserFormAccess ua) {
                ua.delete();
                view.close();
            } else if (share instanceof DistListFormAccess da) {
                da.delete();
                view.close();
            }
        }

    }
}
