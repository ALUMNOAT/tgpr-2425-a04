package tgpr.forms.controller;

import tgpr.forms.model.Form;
import tgpr.forms.model.User;
import tgpr.forms.view.TestView;
import tgpr.framework.Controller;

import java.util.List;

public class TestController extends Controller<TestView> {
    private final TestView view = new TestView(this);

    @Override
    public TestView getView() {
        return view;
    }

    public List<User> getUsers() {
        return User.getAll();
    }

    public void displayUser(User user) {
        showInfo("You selected: " + user.getFullName());
    }

    public Form getForm(){
        return Form.getAll().get(0);
    }

    public void viewForm(Form form) {
        navigateTo(new FormController(form));
    }
}
