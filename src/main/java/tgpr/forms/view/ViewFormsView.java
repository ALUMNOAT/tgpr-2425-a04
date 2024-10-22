package tgpr.forms.view;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.menu.*;
import tgpr.forms.controller.FormController;
import tgpr.forms.model.Form;
import tgpr.forms.model.Security;
import tgpr.forms.model.User;
import tgpr.framework.Controller;

import java.util.ArrayList;
import java.util.List;

public class ViewFormsView extends BasicWindow {

    private Form form;
    private User user;

    private final FormController formController;

    private final MenuBar menuBar = new MenuBar();
    private final Menu menuFile = new Menu("File");
    private final Menu menuParameters = new Menu("Parameters");

    private final MenuItem ViewProfile = new MenuItem("View Profile");
    private final MenuItem logOut = new MenuItem("Log out");
    private final MenuItem Exit = new MenuItem("Exit");

    private final Label lblFilter = new Label("Filter: ");
    private final TextBox txtFilter = new TextBox();

    private int currentPage = 0;
    private final int nbFormsPerPage = 9;

    private final Panel formsPanel = new Panel();

    Paginator paginator = new Paginator(this,9,this::pageChanged);

    public ViewFormsView(User user, FormController controller) {
        this.user = user;
        this.formController = controller;


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

    private Panel createFilterPanel() {
        Panel filterPanel = new Panel();

        txtFilter.setTextChangeListener((txt, byUser) -> reloadData());

        return filterPanel;
    }

    public void reloadData() {

        formsPanel.removeAllComponents();


        /* start donne le début d'index des résultats de la requete sql dans getMyForms
        Page0  start=0 et count=10 la requete donne les formulaires de (0-8) , start = 0 * 9 = 0
        Page1  start=9 et count=10 la requete donne les formulaires (9-17), start = 1 * 9 = 9
        Page2  start=18 et count=10 la requete donne les formulaires (18-26), start = 2 * 9 = 18
        pour obtenir 9 résultats differents pour chaque page, start = (currentPage)* nbFormsPerPage
         */

        List<Form> formsList = user.getMyForms(txtFilter.getText(),(currentPage) * nbFormsPerPage,nbFormsPerPage);

        if(formsList.isEmpty()) {
            Label lblNoMatch = new Label("No form found");
            lblNoMatch.setForegroundColor(TextColor.ANSI.RED);
            lblNoMatch.addTo(formsPanel);
        } else {

            for(Form form : formsList) {

            }
        }

    }

    private void pageChanged(Integer integer) {
        currentPage++;

    }

}
