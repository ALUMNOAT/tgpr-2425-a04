package tgpr.forms.controller;


import tgpr.forms.model.Form;
import tgpr.forms.model.Instance;
import tgpr.forms.view.ViewInstanceView;
import tgpr.framework.Controller;

import java.util.List;


public class ViewInstanceController extends Controller<ViewInstanceView> {
    public Form form;

    public ViewInstanceController( Form form) {
        this.form = form;
    }

    public String getTitle() {
        return form.getTitle();
    }

    public String getDescription() {
        return form.getDescription();
    }

    public void deleteAllInstances() {
        if(askConfirmation( "Are you sure you want to delete all the submitted instances?"
                        + System.lineSeparator() +
                        "Note: This will not delete instances that are currently being edited (not submitted).",
                "Delete All Instances" )){
            form.deleteAllSubmittedInstances();
        }
    }


    public List<Instance> getCompletedInstances(){
        return form.getCompletedInstances();
    }

    @Override
    public ViewInstanceView getView() {
        return new ViewInstanceView(this);
    }
}
