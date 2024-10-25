package tgpr.forms.view;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.TestController;
import tgpr.forms.model.Form;
import tgpr.forms.model.User;

import java.util.List;

public class TestView extends DialogWindow {

    private final TestController controller;

    private Form form;
    private User user;

    public TestView(TestController controller) {
        super("Test");

        this.controller = controller;

        setHints(List.of(Hint.EXPANDED));

        //form =controller.getForm();
        //controller.viewForm(form);

        user = controller.getUser();
        controller.viewForms(user);

    }


}
