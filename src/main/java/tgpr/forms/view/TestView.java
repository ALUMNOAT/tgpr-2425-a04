package tgpr.forms.view;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.TestController;
import tgpr.forms.model.User;
import tgpr.forms.model.Form;

import java.util.List;

public class TestView extends DialogWindow {

    private final TestController controller;

    private ObjectTable<User> tblUsers;
    private ObjectTable<Form> tblForms;

    public TestView(TestController controller) {
        super("Test");

        this.controller = controller;

        setHints(List.of(Hint.EXPANDED));

        var root = createPanel();
        setComponent(root);

        refresh();
    }

    private Panel createPanel() {
        var panel = Panel.gridPanel(1);

        tblUsers = new ObjectTable<>(
                new ColumnSpec<>("FullName", User::getFullName),
                new ColumnSpec<>("Email", User::getEmail),
                new ColumnSpec<>("Role", User::getRole)
        ).centerCenter()
                .grow(true, true)
                .addTo(panel);

        tblUsers.setSelectAction(this::displayUser);

        tblForms = new ObjectTable<>(
                new ColumnSpec<>("Title", Form::getTitle)
        ).centerCenter()
                .grow(true, true)
                .addTo(panel);

        tblForms.setSelectAction(this::displayForm);

        return panel;
    }


    private void displayUser() {
        controller.displayUser(tblUsers.getSelected());
    }

    private void displayForm() {
        controller.displayForm(tblForms.getSelected());
    }

    private void refresh() {
        var users = controller.getUsers();
        var forms = controller.getForms();
        tblUsers.setItems(users);
        tblForms.setItems(forms);
    }
}
