package tgpr.forms.controller;

import tgpr.forms.model.Security;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;
import tgpr.forms.model.User;
import tgpr.forms.model.UserValidator;
import tgpr.forms.view.SignupView;

import static tgpr.framework.Tools.*;

public class SignupController extends Controller<SignupView> {
    public enum Fields {
        PasswordConfirm
    }

    private final SignupView view;
    private User user;
    private final boolean isNew;

    public SignupController(){
        this(null);
    }

    public SignupController(User user){
        this.user = user;
        isNew = user == null;
        view = new SignupView(this, user);
    }

    @Override
    public SignupView getView() {
        return view;
    }

    public User getUser() {
        return user;
    }

    public void save(String email, String fullName, String password, String confirmPassword) {
        var errors = validate(email, fullName, password, confirmPassword);
        if (errors.isEmpty()) {
            var hashedPassword = password.isBlank() ? password : hash(password);
            /**
             * It works and don't change it.
             * I would have to go to {User.java} and change the order of @User definition
             * and @createUser
             */
            user = User.createUser(fullName, email, hashedPassword, User.Role.valueOf("User"));
            user.save();
            view.close();
            var member = User.checkCredentials(email, password);
            Security.login(member);
            navigateTo(new ViewFormsController(member));

        } else
            showErrors(errors);
    }

        public ErrorList validate (String email, String fullName, String password, String confirmPassword) {
            var errors = new ErrorList();

            if (isNew) {
                errors.add(UserValidator.isValidAvailableEmail(email));
                errors.add(UserValidator.isValidPassword(password));
                errors.add(UserValidator.isValidName(fullName));
            }

            if (!password.equals(confirmPassword))
                errors.add("must match password", Fields.PasswordConfirm);

            var user = User.createUser(fullName, email, password, User.Role.valueOf("User"));

            errors.addAll(UserValidator.validate(user));

            return errors;
        }
    }