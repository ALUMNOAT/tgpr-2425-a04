package tgpr.forms.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import tgpr.forms.controller.FormController;
import tgpr.forms.model.AccessType;
import tgpr.forms.model.Form;

public class FormView extends BasicWindow {

    private final FormController controller;
    private final Form form;

    private final Label lblTitle = new Label("");
    private final Label lblDescription = new Label("No description");;
    private final Label lblOwner = new Label("");;
    private final Label lblInstanceStart = new Label("");;
    private final Label lblInstanceEnd = new Label("");

    public FormView(Form form, FormController controller) {
        this.form = form;
        this.controller = controller;


        Panel root = new Panel();
        root.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        root.withBorder(Borders.singleLine());

        setComponent(root);

        createFormDescription().addTo(root);
        createFormInstances().addTo(root);
        createFormButtons().addTo(root);

    }


    private Panel createFormDescription(){

        Panel descriptionsPanel = new Panel();
        descriptionsPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        lblTitle.setText(form.getTitle());
        lblTitle.setForegroundColor(TextColor.ANSI.BLUE);
        lblTitle.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        lblTitle.addTo(descriptionsPanel);

        lblDescription.setText(form.getDescription());
        lblDescription.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        lblDescription.addTo(descriptionsPanel);

        return descriptionsPanel;
    }


    private Panel createFormInstances(){

        Panel instancesPanel = new Panel();
        instancesPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));


        lblOwner.setText("Created by " + form.getOwner().getFullName());
        lblOwner.addStyle(SGR.BOLD);
        lblOwner.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        lblOwner.addTo(instancesPanel);

        var instance = form.getMostRecentInstance(form.getOwner());

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

        lblInstanceStart.addStyle(SGR.BOLD);
        lblInstanceEnd.addStyle(SGR.BOLD);

        lblInstanceStart.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        lblInstanceEnd.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        lblInstanceStart.addTo(instancesPanel);
        lblInstanceEnd.addTo(instancesPanel);

        return instancesPanel;
    }

    private Panel createFormButtons(){

        Panel buttonsPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        if(!form.getQuestions().isEmpty()){
            Button open = new Button("Open").addTo(buttonsPanel);
            open.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
            open.setEnabled(true);

        }

        if(form.getAccessType(form.getOwner())== AccessType.Editor){
            Button manage = new Button("Manage").addTo(buttonsPanel);
            manage.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
            manage.setEnabled(true);
        }

        return buttonsPanel;


    }





}

