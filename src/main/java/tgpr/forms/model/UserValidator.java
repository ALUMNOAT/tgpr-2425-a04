package tgpr.forms.model;

import tgpr.framework.Error;
import tgpr.framework.ErrorList;

import java.util.List;
import java.util.regex.Pattern;

public abstract class UserValidator {

    public static Error isValidEmail(String email) {
        if (email == null || email.isBlank())
            return new Error("email required", User.Fields.Email);
        if (!Pattern.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", email))
            return new Error("invalid email", User.Fields.Email);
        return Error.NOERROR;
    }

    public static Error isValidAvailableEmail(String email) {
        var error = isValidEmail(email);
        //System.out.println(User.getByEmail(email));
        if (error != Error.NOERROR)
            return error;
        if (User.getByEmail(email) != null)
            return new Error("already taken", User.Fields.Email);
        return Error.NOERROR;
    }

    public static Error isValidPassword(String password) {
        if (password == null || password.isBlank()) {
            return new Error("password required", User.Fields.Password);
        }

        /*---------For debugging to see if patterns are working (here uppercase)--------------*/
        //System.out.println("Password: " + password);
        //System.out.println("Contains uppercase? " + password.matches(".*[A-Z].*"));
        /*-----------------------------------------------------------------------------------*/

        if (password.length() < 8)
            return new Error("at least 8 characters", User.Fields.Password);
        if (!password.matches(".*[A-Z].*"))
            return new Error("at least one uppercase", User.Fields.Password);
        if (!password.matches(".*\\d.*"))
            return new Error("at least one digit", User.Fields.Password);
        if (!password.matches(".*[^a-zA-Z0-9].*"))
            return new Error("at least one special character", User.Fields.Password);
        return Error.NOERROR;
    }

    public static Error isValidName(String fullName) {
        if (fullName == null || fullName.isBlank())
            return new Error("name required", User.Fields.FullName);
        if(!Pattern.matches("[a-zA-Z]{3,}( [a-zA-Z]{3,})*", fullName))
            return new Error("invalid name", User.Fields.FullName);
        return Error.NOERROR;
    }

    public static Error isValidAvailableName(String fullName) {
        var error = isValidName(fullName);
        /** Blame Benoit Penelle
         * (IDK who the fuck putted unique names as a key in the database
         * when more people can have the same name, lost 1 hour losing my mind thinking that I'm dumb
         * but actually the code was ok)
         */
        //System.out.println(User.getByFullName(fullName);
        if (error != Error.NOERROR)
            return error;
        if (User.getByFullName(fullName) != null)
            return new Error("already taken", User.Fields.FullName);
        return Error.NOERROR;
    }

    public static List<Error> validate(User user) {
        var errors = new ErrorList();

        // field validation
        errors.add(isValidEmail(user.getEmail()));
        errors.add(isValidAvailableEmail(user.getEmail()));
        errors.add(isValidPassword(user.getPassword()));
        errors.add(isValidName(user.getFullName()));
        errors.add(isValidAvailableName(user.getFullName()));

        //cross-fields validations

        if (user.getName() != null && !user.getName().isBlank() && user.getName().equals(user.getEmail()))
            errors.add("name must be different from email", User.Fields.FullName);

        return errors;
    }
}
