package tgpr.forms.controller;

import tgpr.forms.model.User;
import tgpr.forms.model.UserValidator;
import tgpr.framework.Controller;
import tgpr.framework.Error;
import tgpr.framework.ErrorList;
import tgpr.framework.Model;
import tgpr.forms.FormsApp;
import tgpr.forms.view.LoginView;
import tgpr.forms.model.Security;

import java.util.List;

public class LoginController extends Controller<LoginView> {
    public void exit(){
        System.exit(0);
    }

    public void refreshData(){
        Model.seedData(FormsApp.DATABASE_SCRIPT_FILE);
    }

    public List<Error> login(String email, String password) {
//        System.out.println("Email: " + email); // Ajoutez cela pour le débogage
//        System.out.println("Password: " + password); // Ajoutez cela pour le débogage

        var errors = new ErrorList();
        errors.add(UserValidator.isValidEmail(email));
        errors.add(UserValidator.isValidPassword(password));

        if (errors.isEmpty()) {
            var user = User.checkCredentials(email, password);
//            System.out.println("User: " + user); //Ajoutez cela pour le débogage
            if (user != null) {
                Security.login(user);
            } else
                showError(new Error("invalid credentials"));
        } else
            showErrors(errors);

        return errors;
    }

    @Override
    public LoginView getView() {
        return new LoginView(this);
    }
}