package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.SignupController;
import tgpr.forms.model.User;

import java.util.List;
import java.util.regex.Pattern;

import static tgpr.framework.Tools.asString;
import static tgpr.framework.Tools.ifNull;

public class SignupView extends DialogWindow {

    private final SignupController controller;
    private final TextBox txtEmail;
    private final TextBox txtFullName;
    private final TextBox txtPassword;
    private final TextBox txtPasswordConfirm;
    private final Label errEmail;
    private final Label errFullName;
    private final Label errPassword;
    private final Label errPasswordConfirm;
    private final Button btnSignup;

    private final User user;

    public SignupView(SignupController controller, User user){
        //Title of the window
        super("Signup");

        this.user = user;
        this.controller = controller;
        //Center the box
        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        //Closes the window with "esc" button
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(60, 11));

        //Setup of the 'root'
        Panel root = new Panel();
        root.setLayoutManager(new GridLayout(2).setTopMarginSize(1));

        //Setup Email button
        new Label("Mail:").addTo(root);
        txtEmail = new TextBox(new TerminalSize(25, 1)).addTo(root)
                .setValidationPattern(Pattern.compile("[A-Za-z0-9+_.-]*+[@]{0,1}+[A-Za-z0-9-]*+[.]{0,1}+[A-Za-z]{0,3}"))
                .setTextChangeListener((txt, byUser) -> validate())
                .setReadOnly(user != null);
        new EmptySpace().addTo(root);
        errEmail = new Label("").addTo(root)
                .setForegroundColor(TextColor.ANSI.RED);

        //Setup FullName button
        new Label("Full Name:").addTo(root);
        txtFullName = new TextBox(new TerminalSize(40, 1)).addTo(root)
                .setTextChangeListener((txt, byUser) -> validate());
        new EmptySpace().addTo(root);
        errFullName = new Label("").addTo(root)
                .setForegroundColor(TextColor.ANSI.RED);

        //Setup Password button
        new Label("Password:").addTo(root);
        txtPassword = new TextBox(new TerminalSize(25, 1)).addTo(root)
                .setMask('*')
                .setTextChangeListener((txt, byUser) -> validate());
        new EmptySpace().addTo(root);
        errPassword = new Label("").addTo(root)
                .setForegroundColor(TextColor.ANSI.RED);

        //Setup ConfirmPassword button
        new Label("Confirm Password:").addTo(root);
        txtPasswordConfirm = new TextBox(new TerminalSize(25, 1)).addTo(root)
                .setMask('*')
                .setTextChangeListener((txt, byUser) -> validate());
        new EmptySpace().addTo(root);
        errPasswordConfirm = new Label("").addTo(root)
                .setForegroundColor(TextColor.ANSI.RED);

        //Add space to dissociate and push the button panel to the bottom of the root panel (esthetic)
        new EmptySpace().addTo(root);

        var buttons = new Panel().addTo(root).setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
                .setLayoutData(Layouts.LINEAR_CENTER).addTo(root);
        //To center and change the position of the 2 buttons
        new EmptySpace(new TerminalSize(1, 1)).addTo(buttons);
        btnSignup = new Button("Signup", this::add).addTo(buttons).setEnabled(false);
        new Button("Close", this::close).addTo(buttons);

        setComponent(root);

    }

    private void add() {
        controller.save(
                txtEmail.getText(),
                txtFullName.getText(),
                txtPassword.getText(),
                txtPasswordConfirm.getText()
        );
    }

    private void validate() {
        var errors = controller.validate(
                txtEmail.getText(),
                txtFullName.getText(),
                txtPassword.getText(),
                txtPasswordConfirm.getText()
        );

        errEmail.setText(errors.getFirstErrorMessage(User.Fields.Email));
        errFullName.setText(errors.getFirstErrorMessage(User.Fields.FullName));
        errPassword.setText(errors.getFirstErrorMessage(User.Fields.Password));
        errPasswordConfirm.setText(errors.getFirstErrorMessage(SignupController.Fields.PasswordConfirm));
        btnSignup.setEnabled(errors.isEmpty());
    }
}
