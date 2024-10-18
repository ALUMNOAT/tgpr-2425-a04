package tgpr.forms.view;

import com.googlecode.lanterna.gui2.*;
import tgpr.forms.controller.FormController;
import tgpr.forms.model.AccessType;
import tgpr.forms.model.Form;

public class FormView extends BasicWindow {

    private final FormController controller;
    private Form form;

    private final Label lblTitle = new Label("");
    private final Label lblDescription = new Label("");;
    private final Label lblOwner = new Label("");;
    private final Label lblInstanceStart = new Label("");;
    private final Label lblInstanceEnd = new Label("");

    public FormView(Form form, FormController controller) {
        this.form = form;
        this.controller = controller;


    }


    private Panel createPanel() {

        var panel = new Panel();

        panel.setLayoutManager(new GridLayout(1));


        return panel;

    }


    private void createFormDescription(){

        lblTitle.setText(form.getTitle());
        lblDescription.setText(form.getDescription());
        lblOwner.setText(form.getOwner().getFullName());
    }


    private void createFormInstances(){
        var instance = form.getMostRecentInstance(form.getOwner());

        if(instance == null) {
            lblInstanceStart.setText("Not Started");
        } else {
            lblInstanceStart.setText(instance.getStarted().toString());

            if(instance.getCompleted() != null){
                lblInstanceEnd.setText(instance.getCompleted().toString());
            } else {
                lblInstanceEnd.setText("In progress");
            }
        }

    }

    private void createFormButtons(){

        var panel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        if(!form.getQuestions().isEmpty()){
            Button open = new Button("Open").addTo(panel);
            open.setEnabled(true);
        }

        if(form.getAccessType(form.getOwner())== AccessType.Editor){
            Button manage = new Button("Manage").addTo(panel);
            manage.setEnabled(true);
        }


    }





}

