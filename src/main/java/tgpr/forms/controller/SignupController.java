package tgpr.forms.controller;

import tgpr.forms.model.User;
import tgpr.forms.model.UserValidator;
import tgpr.forms.view.SignupView;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;

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

    public User getUser() {return user;}

//    public void save(String email, String fullName, String password, String confirmPassword){
//        var errors = validate(email, fullName, password, confirmPassword);
//        if (errors.isEmpty()) {
//            var hashedPassword = password.isBlank() ? password : hash(password);
//            user = User.createUser(email, fullName, hashedPassword, User.Role.valueOf("User"));
//            user.save();
//            view.close();
//        } else {
//            showErrors(errors);
//        }

//        public ErrorList validate(String mail, String name, String password, String confirmPassword){
//            var errors = new ErrorList();
//
//            if (isNew) {
//                errors.add(UserValidator.isValidAvailableEmail(mail));
//                errors.add(UserValidator.isValidPassword(password));
//            }
//
//            if(!password.equals(confirmPassword))
//                errors.add("must match password", Fields.PasswordConfirm);
//
//            var hashedPassword = password.isBlank() ? password : hash(password);
//            var user = User.createUser(name, mail, hashedPassword, User.Role.valueOf("user"));
//            errors.addAll(UserValidator.validate(user));
//
//            return errors;
//
//        }
//    }

}
