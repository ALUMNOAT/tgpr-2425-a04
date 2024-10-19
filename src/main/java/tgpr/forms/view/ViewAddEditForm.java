package tgpr.forms.view;

import com.googlecode.lanterna.gui2.*;
import tgpr.forms.controller.ControllerAddEditForm;

import java.util.List;

public class ViewAddEditForm extends BasicWindow {
    public ViewAddEditForm(ControllerAddEditForm controller){
        setTitle("Add a form");
        setHints(List.of(Hint.CENTERED));

        Panel root = new Panel();
        setComponent(root);

        new EmptySpace().addTo(root);
    }

}
