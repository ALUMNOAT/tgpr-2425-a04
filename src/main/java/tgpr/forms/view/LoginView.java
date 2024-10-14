package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import tgpr.forms.controller.LoginController;
import tgpr.forms.controller.TestController;
import tgpr.framework.Configuration;
import tgpr.framework.Controller;

import java.util.List;

//Creation of the window (idk the difference between 'BasicWindow' and 'DialogWindow' yet)
public class LoginView extends BasicWindow{
    //Enabling the controller & Creation of the boxes and button
    private final LoginController controller;
    private final TextBox emailTextBox;
    private final TextBox passwordTextBox;
    private final Button btnLogin;

    public LoginView(LoginController controller){
        super("Login");
        this.controller = controller;
        //Center the entire box
        setHints(List.of(Hint.CENTERED));

        //Setup of the 'root' panel
        Panel root = new Panel();
        setComponent(root);

        //Setup of the 'login' panel
        Panel login = new Panel();
        //Setup of rows and spacing
        login.setLayoutManager(new GridLayout(2).setTopMarginSize(1).setVerticalSpacing(1));
        login.setLayoutData(Layouts.LINEAR_BEGIN);
        //Add 'login' panel to 'root' panel
        login.addTo(root);

        //pseudo
        emailTextBox = new TextBox();
        emailTextBox.setPreferredSize(new TerminalSize(20, 1));
        login.addComponent(new Label("Email:"));
        login.addComponent(emailTextBox);

        //password with '*'
        passwordTextBox = new TextBox();
        passwordTextBox.setPreferredSize(new TerminalSize(20, 1));
        passwordTextBox.setMask('*');
        login.addComponent(new Label("Password:"));
        login.addComponent(passwordTextBox);

        new EmptySpace().addTo(root);

        //Setup of the 3 'buttons' panel
        Panel buttons = new Panel();
        //Setup of rows and spacing
        buttons.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        buttons.setLayoutData(Layouts.LINEAR_CENTER);
        //Add 'buttons' panel to 'root' panel
        buttons.addTo(root);

        btnLogin = new Button("Login", this::login);
        btnLogin.addTo(buttons);
        Button btnSignup = new Button("Signup", this::signup);
        btnSignup.addTo(buttons);
        Button btnExit = new Button("Exit", this::exit);
        btnExit.addTo(buttons);

        //Setup of the 'login as guest' button
        //For that we create a new panel
        Panel guest = new Panel();
        guest.setLayoutData(Layouts.LINEAR_CENTER);
        guest.addTo(root);
        Button btnGuest = new Button("Login as guest", this::logAsGuest);
        btnGuest.addTo(guest);

        new EmptySpace().addTo(root);

        //Debugging panel
        Button btnDebug = new Button("Reset Database", this::refreshData);
        Panel debug = Panel.verticalPanel(LinearLayout.Alignment.Center,
                new Button("Login as default admin", this::logAsDefaultAdmin),
                new Button("Login as bepenelle@epfc.eu", this::logAsPenelle),
                new Button("Login as mamichel@epfc.eu", this::logAsMichel),
                new Button("Login as xapigeolet@epfc.eu", this::logAsPigeolet),
                btnDebug
        );
        debug.setLayoutData(Layouts.LINEAR_CENTER);
        debug.withBorder(Borders.singleLine(" For debug purpose ")).addTo(root);

        emailTextBox.takeFocus();
    }

    private void refreshData(){
        controller.refreshData();
        btnLogin.takeFocus();
    }

    private void exit(){
        controller.exit();
    }

    private void login(){
        var errors = controller.login(emailTextBox.getText(), passwordTextBox.getText());
        if (!errors.isEmpty()) {
            emailTextBox.takeFocus();
        } else {
            Controller.navigateTo(new TestController());
            // TODO: change 'Controller.navigateTo(new TestController());' to the main page after logging in
        }
    }

    private void signup() {
        // TODO: Add signup feature
    }

    private void logAsDefaultAdmin() {
        controller.login(Configuration.get("default.admin.email"), Configuration.get("default.admin.password"));
        Controller.navigateTo(new TestController());
        // TODO: change 'Controller.navigateTo(new TestController());' to the main page after logging in
    }

    private void logAsGuest() {
        controller.login(Configuration.get("default.guest.email"), Configuration.get("default.guest.password"));
        Controller.navigateTo(new TestController());
        // TODO: change 'Controller.navigateTo(new TestController());' to the main page after logging in
    }

    private void logAsPenelle() {
        controller.login(Configuration.get("default.penelle.email"), Configuration.get("default.penelle.password"));
        Controller.navigateTo(new TestController());
        // TODO: change 'Controller.navigateTo(new TestController());' to the main page after logging in
    }

    private void logAsMichel() {
        controller.login(Configuration.get("default.michel.email"), Configuration.get("default.michel.password"));
        Controller.navigateTo(new TestController());
        // TODO: change 'Controller.navigateTo(new TestController());' to the main page after logging in
    }

    private void logAsPigeolet() {
        controller.login(Configuration.get("default.pigeolet.email"), Configuration.get("default.pigeolet.password"));
        Controller.navigateTo(new TestController());
        // TODO: change 'Controller.navigateTo(new TestController());' to the main page after logging in
    }
}