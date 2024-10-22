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

    private final MenuBar menuBar = new MenuBar();
    private final Menu menuFile = new Menu("File");
    private final Menu menuParameters = new Menu("Parameters");

    private final MenuItem ViewProfile = new MenuItem("View Profile");
    private final MenuItem logOut = new MenuItem("Log out");
    private final MenuItem Exit = new MenuItem("Exit");


    public ViewFormsView(User user) {

        this.user = user;
        setTitle(getTitleWithUser());
        setHints(List.of(Hint.EXPANDED));


        Panel root = new Panel();
        setComponent(root);

        createMenuBar().addTo(root);


    }

    private Panel createMenuBar(){

        Panel menu = new Panel();

        menuBar.add(menuFile);
        menuBar.add(menuParameters);

        menuFile.add(ViewProfile);
        menuFile.add(logOut);
        menuFile.add(Exit);

        menuBar.addTo(menu);

        return menu;
    }

    private String getTitleWithUser() {

        return "MyForms (" + Security.getLoggedUser().getEmail() + " - " + (Security.isGuest() ? "User" : "Guest") + ")";
    }

}
