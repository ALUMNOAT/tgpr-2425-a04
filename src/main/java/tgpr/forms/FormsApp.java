package tgpr.forms;

import tgpr.forms.controller.LoginController;
import tgpr.framework.Controller;
import tgpr.framework.Model;

public class FormsApp {
    public static final String DATABASE_SCRIPT_FILE = "/database/tgpr-2425-a04.sql";

    public static void main(String[] args) {
        if (!Model.checkDb(DATABASE_SCRIPT_FILE))
            Controller.abort("Database is not available!");
        else {

            Controller.navigateTo(new LoginController());
            //Controller.navigateTo(new LoginController());
            //Controller.navigateTo(new TestController());

        }
    }
}
