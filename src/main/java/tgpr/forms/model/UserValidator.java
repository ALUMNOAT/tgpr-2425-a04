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
        if (error != Error.NOERROR)
            return error;
        if (User.getByEmail(email) != null)
            return new Error("not available", User.Fields.Email);
        return Error.NOERROR;
    }

    public static Error isValidPassword(String password) {
        if (password == null || password.isBlank())
            return new Error("password required", User.Fields.Password);
        if (!Pattern.matches("[a-zA-Z0-9!@#$%^&*(),.?\":{}|<>]{3,}", password))
            return new Error("invalid password", User.Fields.Password);
        return Error.NOERROR;
    }

    public static List<Error> validate(User user) {
        var errors = new ErrorList();

        // field validations
        errors.add(isValidEmail(user.getEmail()));

        // cross-fields validations
        if (user.getEmail() != null && !user.getEmail().isBlank() && user.getEmail().equals(user.getName()))
            errors.add("Name must be different from email", User.Fields.Email);

        return errors;
    }
}
