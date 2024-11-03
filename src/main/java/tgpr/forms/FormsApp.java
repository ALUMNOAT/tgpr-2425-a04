package tgpr.forms;

import tgpr.forms.controller.AddEditFormController;
import tgpr.forms.model.Form;
import tgpr.forms.model.Security;
import tgpr.forms.model.User;
import tgpr.framework.Controller;
import tgpr.framework.Model;

public class FormsApp {
    public final static String DATABASE_SCRIPT_FILE = "/database/tgpr-2425-a04.sql";

    public static void main(String[] args) {
        if (!Model.checkDb(DATABASE_SCRIPT_FILE))
            Controller.abort("Database is not available!");
        else {
            //Controller.navigateTo(new TestController());

            User user = User.getByEmail("bepenelle@epfc.eu");
            Security.login(user);
            Form form = Form.getByTitleAndUser("Abc", user);
            Form form2 = new Form("Test2", "Description", user, false);

            Controller.navigateTo(new AddEditFormController(form));
            //Controller.navigateTo(new AddEditFormController(null));
            //Model.seedData(FormsApp.DATABASE_SCRIPT_FILE);
        }
    }
}
