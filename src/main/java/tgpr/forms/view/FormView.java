package tgpr.forms.view;

import tgpr.forms.controller.FormController;
import tgpr.forms.model.Form;

public class FormView {

    private Form form;

    private final FormController controller;

    public FormView(FormController controller) {
        this.controller = controller;
    }
}
