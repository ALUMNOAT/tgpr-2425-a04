package tgpr.forms.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import tgpr.forms.controller.FormController;
import tgpr.forms.controller.ViewFormsController;
import tgpr.forms.model.AccessType;
import tgpr.forms.model.Form;
import tgpr.forms.model.Security;

import java.util.List;

public class FormView extends BasicWindow {

    private final FormController controller;
    private final Form form;


    private final Panel formCard = new Panel();
    private final Label lblTitle = new Label("");
    private final Label lblDescription = new Label("No description");;
    private final Label lblOwner = new Label("");;
    private final Label lblInstanceStart = new Label("");;
    private final Label lblInstanceEnd = new Label("");

    public FormView(Form form, FormController controller) {
        this.form = form;
        this.controller = controller;


        formCard.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        setHints(List.of(Hint.CENTERED));
        setComponent(formCard.withBorder(Borders.singleLine()));

        createFormDescription().addTo(formCard).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center,
                LinearLayout.GrowPolicy.CanGrow));
        createFormInstances().addTo(formCard).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center,
                LinearLayout.GrowPolicy.CanGrow));


    }


    private Panel createFormDescription(){

        Panel descriptionsPanel = new Panel();
        descriptionsPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL)).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));;

        lblTitle.setText(form.getTitle());
        lblTitle.setForegroundColor(TextColor.ANSI.BLUE);
        lblTitle.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        lblTitle.addTo(descriptionsPanel);

        lblDescription.setText(form.getDescription());
        lblDescription.addStyle(SGR.BOLD);
        lblDescription.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center,LinearLayout.GrowPolicy.CanGrow ));
        lblDescription.addTo(descriptionsPanel);

        descriptionsPanel.addEmpty();

        return descriptionsPanel;
    }


    private Panel createFormInstances(){

        Panel instancesPanel = new Panel();
        instancesPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL)).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));;


        lblOwner.setText("Created by " + form.getOwner().getFullName());
        lblOwner.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center,LinearLayout.GrowPolicy.CanGrow));
        lblOwner.addTo(instancesPanel);

        var instance = form.getMostRecentInstance(Security.getLoggedUser());

        if(instance == null) {
            lblInstanceStart.setText("Not Started");
        } else {
            lblInstanceStart.setText("Started on " + instance.getStarted().toString());

            if(instance.getCompleted() != null){
                lblInstanceEnd.setText("Submitted on " +instance.getCompleted().toString());
            } else {
                lblInstanceEnd.setText("In progress");
            }
        }


        lblInstanceStart.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center,LinearLayout.GrowPolicy.CanGrow));
        lblInstanceEnd.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center,LinearLayout.GrowPolicy.CanGrow));

        lblInstanceStart.addTo(instancesPanel);
        lblInstanceEnd.addTo(instancesPanel);

        instancesPanel.addEmpty();

        return instancesPanel;
    }



    public Panel getFormCard() {
        return formCard;
    }





}

