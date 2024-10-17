package tgpr.forms.view;

import com.googlecode.lanterna.gui2.*;
import tgpr.forms.controller.FormController;
import tgpr.forms.model.Form;

public class FormView extends BasicWindow {

    private final FormController controller;

    private Form form;

    public FormView(Form form, FormController controller) {
        this.form = form;
        this.controller = controller;


    }


    private Panel createPanel() {

        var panel = new Panel();

        panel.setLayoutManager(new GridLayout(1));

        new Label(form.getTitle()).addTo(panel);

        new Label(controller.getFormDescription()).addTo(panel);

        new Label(controller.getFormOwner().getFullName()).addTo(panel);


        if(controller.getRecentInstanceDate()==null) {

            new Label("Not Started").addTo(panel);
        } else {

            new Label(controller.getRecentInstanceDate().toString()).addTo(panel);

        }


        if(controller.getInstanceSubmission()==null) {

            new Label("In progress").addTo(panel);
        } else {

            new Label(controller.getInstanceSubmission().toString()).addTo(panel);
        }


        if(!controller.hasNoQuestions()){


        }


        return panel;

    }
}

