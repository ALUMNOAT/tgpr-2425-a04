package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.menu.*;
import tgpr.forms.controller.FormController;
import tgpr.forms.controller.ViewFormsController;
import tgpr.forms.model.Form;
import tgpr.forms.model.Security;
import tgpr.forms.model.User;

import java.util.List;

public class ViewFormsView extends BasicWindow {

    private final User user;

    private final ViewFormsController controller;

    private final MenuBar menuBar = new MenuBar();
    private final Menu menuFile = new Menu("File");
    private final Menu menuParameters = new Menu("Parameters");

    private final MenuItem ViewProfile = new MenuItem("View Profile");
    private final MenuItem logOut = new MenuItem("Log out");
    private final MenuItem Exit = new MenuItem("Exit");

    private final Label lblFilter = new Label(" Filter: ");
    private final TextBox txtFilter = new TextBox(new TerminalSize(30,1));

    private int currentPage = 0;
    private final int nbFormsPerPage = 9;

    private final Panel formsPanel = new Panel();

    Paginator paginator = new Paginator(this,9,this::pageChanged);

    public ViewFormsView(User user, ViewFormsController controller ) {
        this.user = user;
        this.controller = controller;

        setTitle(getTitleWithUser());
        setHints(List.of(Hint.EXPANDED));


        Panel root = new Panel().setLayoutManager(new LinearLayout(Direction.VERTICAL));
        setComponent(root);

        createMenuBar().addTo(root);

        createFilterPanel().addTo(root);

        formsPanel.setLayoutManager(new GridLayout(3).setTopMarginSize(1));
        formsPanel.setPreferredSize(new TerminalSize(140,40));

        formsPanel.addTo(root);
        createButtonsPanel().addTo(root);
        reloadData();
    }

    private String getTitleWithUser() {
        String title;

        if(Security.getLoggedUser()==null){
            title = "MyForms (Guest)";
        } else if (Security.isAdmin()) {
            title = "MyForms (" + Security.getLoggedUser().getEmail() + " - Admin)";
        } else if (!Security.isGuest()){
            title = "MyForms (" + Security.getLoggedUser().getEmail() + " - " + Security.getLoggedUser().getRole()+")";
        } else {
            title = "MyForms (Guest)";
        }

        return title;
    }

    private Panel createMenuBar(){

        Panel menu = new Panel();

        menuBar.add(menuFile);
        menuBar.add(menuParameters);

        menuFile.add(ViewProfile);
        menuFile.add(logOut);
        menuFile.add(Exit);

        menuBar.addTo(menu);

        menu.addEmpty();

        return menu;
    }


    private Panel createFilterPanel() {
        Panel filterPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));;

        lblFilter.addTo(filterPanel);

        txtFilter.setTextChangeListener((txt, byUser) -> reloadData());

        txtFilter.addTo(filterPanel);

        return filterPanel;
    }

    public void reloadData() {

        formsPanel.removeAllComponents();


        /*
        count donne le n° de resultats de la requete sql dans getMyForms
        start donne le début d'index des résultats de la requete sql dans getMyForms
        Page0  start=0 et count=10 la requete donne les formulaires de (0-8) , start = 0 * 9 = 0
        Page1  start=9 et count=10 la requete donne les formulaires (9-17), start = 1 * 9 = 9
        Page2  start=18 et count=10 la requete donne les formulaires (18-26), start = 2 * 9 = 18
        pour obtenir 9 résultats differents pour chaque page, start = (currentPage)* nbFormsPerPage
         */

        List<Form> formsList = user.getMyForms(txtFilter.getText(),(currentPage) * nbFormsPerPage,nbFormsPerPage);

        paginator.setCount(user.countMyForms(txtFilter.getText()));

        if(formsList.isEmpty()) {
            Label lblNoMatch = new Label("No form found");
            lblNoMatch.setForegroundColor(TextColor.ANSI.RED);
            lblNoMatch.addTo(formsPanel);
        } else {
            createFormCards(formsList);

        }

        txtFilter.takeFocus();
    }

    private void createFormCards(List<Form> formsList) {

        for(Form form : formsList) {

            FormController formToAdd = new FormController(form);
            formToAdd.getFormCard().setPreferredSize(new TerminalSize(34, 8))
                    .withBorder((Borders.singleLine()))
                    .addTo(formsPanel);

        }

    }


    private void pageChanged(Integer integer) {

        currentPage = paginator.getPage();
        reloadData();

    }

    private Panel createButtonsPanel(){
        Panel buttonsPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        Button createForm = new Button("Create a new form");

        createForm.addTo(buttonsPanel).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning));

        Panel emptySpace = new Panel().setPreferredSize(new TerminalSize(43,1));

        buttonsPanel.addComponent(emptySpace);

        paginator.addTo(buttonsPanel).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.End));

        return buttonsPanel;
    }

}
