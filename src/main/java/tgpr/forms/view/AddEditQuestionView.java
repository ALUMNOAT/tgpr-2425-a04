package tgpr.forms.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.forms.controller.AddEditQuestionController;
import tgpr.forms.model.OptionList;
import tgpr.forms.model.Question;

import java.util.List;
import java.util.regex.Pattern;

import static tgpr.framework.Tools.asString;
import static tgpr.framework.Tools.ifNull;

public class AddEditQuestionView extends DialogWindow {
    private final AddEditQuestionController controller;
    private final Question question;

    private TextBox txtTitle;
    private TextBox txtDescription;
    private ComboBox<String> cboType;
    private CheckBox chkRequired;
    private ComboBox<String> cboOptionsList;
    private final Label errTitle = new Label("");
    private final Label errDescription = new Label("");
    private final Label errType = new Label("");
    private final Label errRequired = new Label("");
    private final Label errOptionsList = new Label("");
    private Button btnAddOptionList;
    private Button btnDelete;
    private Button btnCreateUpdate;

    public AddEditQuestionView(AddEditQuestionController controller, Question question) {
        super((question == null ? "Add" : "Edit") + " a Question");

        this.question = question;
        this.controller = controller;

        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(70, 15));

        Panel root = Panel.verticalPanel();
        setComponent(root);

        createFieldsGrid().addTo(root);
        createOptionsListGrid().addTo(root);
        createButtonsPanel().addTo(root);

        refresh();

        txtTitle.takeFocus();
    }

    private Panel createFieldsGrid() {
        var panel = Panel.gridPanel(2, Margin.of(1,1,1,0));

        new Label("Title:").addTo(panel);
        txtTitle = new TextBox().sizeTo(45).addTo(panel)
                //.setValidationPattern(Pattern.compile("[a-z][a-z\\d]{0,7}"))
                .setTextChangeListener((txt, byUser) -> validate());
        panel.addEmpty();
        errTitle.addTo(panel)
                .setForegroundColor(TextColor.ANSI.RED);

        new Label("Description:").addTo(panel);
        txtDescription = new TextBox("", TextBox.Style.MULTI_LINE)
                .sizeTo(55, 3).addTo(panel)
                .setTextChangeListener((txt, byUser) -> validate());
        panel.addEmpty();
        errDescription.addTo(panel).setForegroundColor(TextColor.ANSI.RED);


        new Label("Type:").addTo(panel);
        cboType = new ComboBox<String>().sizeTo(11).addTo(panel);
        for(var type : Question.Type.values())
            cboType.addItem(type.toString());
        cboType.addListener((selectedIndex, previousSelection, changedByUserInteraction) -> validate());
        panel.addEmpty();
        errType.addTo(panel).setForegroundColor(TextColor.ANSI.RED);

        new Label("Required:").addTo(panel);
        chkRequired = new CheckBox().addTo(panel);
        panel.addEmpty();
        errRequired.addTo(panel).setForegroundColor(TextColor.ANSI.RED);

        return panel;
    }

    private Panel createOptionsListGrid() {
        var panel = Panel.gridPanel(3, Margin.of(1,0,1,1));

        new Label("Options List:").addTo(panel);
        cboOptionsList = new ComboBox<>("").sizeTo(25).addTo(panel);
        for(var ol : OptionList.getAll())
            cboOptionsList.addItem(ol.toString());
        cboOptionsList.addListener((selectedIndex, previousSelection, changedByUserInteraction) -> validate());
        btnAddOptionList = new Button("Add", this::addOptionList).addTo(panel);
        panel.addEmpty();
        errOptionsList.addTo(panel).setForegroundColor(TextColor.ANSI.RED);

        return panel;
    }

    private Panel createButtonsPanel() {
        var panel = new Panel()
                .setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.End));

        btnCreateUpdate = new Button(question == null ? "Create" : "Update", this::addQuestion).addTo(panel).setEnabled(false);
        new Button("Cancel", this::close).addTo(panel);

        //addShortcut(btnCreateUpdate, KeyStroke.fromString(question == null ? "<A-a>" : "<A-u>"));

        return panel;
    }

    private void refresh() {
        if (question != null) {
            txtTitle.setText(question.getTitle());
            txtDescription.setText(question.getDescription());
            cboType.setSelectedItem(question.getType().toString());
            chkRequired.setChecked(question.getRequired());
            cboOptionsList.setSelectedItem(question.getOptionList() == null ?
                    "" : question.getOptionList().toString());
            /*
            txtBirthDate.setText(asString(member.getBirthdate()));
            cboRole.setSelectedItem(member.isAdmin() ? "Admin" : "Member");
             */
        }
    }


    private void addQuestion() {
        /*
        controller.save(
                txtPseudo.getText(),
                txtProfile.getText(),
                txtPassword.getText(),
                txtPasswordConfirm.getText(),
                txtBirthDate.getText(),
                cboRole.getText()
        );
         */
    }

    private void addOptionList() {}

    private void validate() {
        /*
        var errors = controller.validate(
                txtPseudo.getText(),
                txtProfile.getText(),
                txtPassword.getText(),
                txtPasswordConfirm.getText(),
                txtBirthDate.getText(),
                cboRole.getText()
        );

        errPseudo.setText(errors.getFirstErrorMessage(Member.Fields.Pseudo));
        errProfile.setText(errors.getFirstErrorMessage(Member.Fields.Profile));
        errPassword.setText(errors.getFirstErrorMessage(Member.Fields.Password));
        errPasswordConfirm.setText(errors.getFirstErrorMessage(EditMemberController.Fields.PasswordConfirm));
        errBirthDate.setText(errors.getFirstErrorMessage(Member.Fields.BirthDate));
        errRole.setText(errors.getFirstErrorMessage(Member.Fields.Admin));

        btnAddUpdate.setEnabled(errors.isEmpty());
         */
    }
}
