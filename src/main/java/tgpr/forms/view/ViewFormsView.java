package tgpr.forms.view;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.ObjectTable;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.menu.*;
import tgpr.forms.model.Form;
import tgpr.forms.model.Security;
import tgpr.forms.model.User;

import java.util.List;

public class ViewFormsView extends BasicWindow {

    private Form form;
    private User user;

    private ObjectTable<Form> tblForms;

    private final MenuBar menuBar;
    private final Menu menuFile;
    private final Menu menuParameters;
    private final MenuItem ViewProfile;
    private final MenuItem logOut;
    private final MenuItem Exit;


    public ViewFormsView(User user) {

        this.user = user;
        setTitle(getTitleWithUser());
        setHints(List.of(Hint.EXPANDED));


        Panel root = new Panel();
        setComponent(root);

        menuBar = new MenuBar().addTo(root);

        menuFile = new Menu("File");
        menuBar.add(menuFile);

        menuParameters = new Menu("Parameters");
        menuBar.add(menuParameters);

        ViewProfile = new MenuItem("View Profile");
        menuFile.add(ViewProfile);

        logOut = new MenuItem("Log out");
        menuFile.add(logOut);

        Exit = new MenuItem("Exit");
        menuFile.add(Exit);
    }

    private String getTitleWithUser() {

        return "MyForms (" + Security.getLoggedUser().getEmail() + " - " + (Security.isGuest() ? "User" : "Guest") + ")";
    }

}
