package tgpr.forms.model;

import tgpr.framework.Error;

import java.util.regex.Pattern;

public abstract class UserValidator {

    public static Error isValidEmail(String email) {
        if (email == null || email.isBlank())
            return new Error("email required", User.Fields.Email);
        if (!Pattern.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", email))
            return new Error("invalid email", User.Fields.Email);
        return Error.NOERROR;
    }

    public static Error isValidPassword(String password) {
        if (password == null || password.isBlank())
            return new Error("password required", User.Fields.Password);
        if (!Pattern.matches("[a-zA-Z0-9!@#$%^&*(),.?\":{}|<>]{3,}", password))
            return new Error("invalid password", User.Fields.Password);
        return Error.NOERROR;
    }
}
