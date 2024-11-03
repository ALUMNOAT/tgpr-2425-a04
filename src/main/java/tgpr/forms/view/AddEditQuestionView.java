package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.AddEditQuestionController;
import tgpr.forms.model.OptionList;
import tgpr.forms.model.Question;
import tgpr.forms.model.Security;

import java.util.List;

public class AddEditQuestionView extends DialogWindow {
    private final AddEditQuestionController controller;
    private final Question question;

    private List<OptionList> optionLists;

    private TextBox txtTitle;
    private TextBox txtDescription;
    private ComboBox<String> cboType;
    private CheckBox chkRequired;
    private ComboBox<String> cboOptionList;
    private final Label errTitle = new Label("");
    private final Label errDescription = new Label("");
    private final Label errOptionsList = new Label("");
    private Button btnAddOptionList;
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
        loadData();

        txtTitle.takeFocus();
    }

    private Panel createFieldsGrid() {
        var panel = Panel.gridPanel(2, Margin.of(1,1,1,0));

        new Label("Title:").addTo(panel);
        txtTitle = new TextBox().sizeTo(45).addTo(panel)
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
        for(var type : Question.Type.getSortedTypeList())
            cboType.addItem(type);
        cboType.addListener((selectedIndex, previousSelection, changedByUserInteraction) -> {
            refreshEnableOptionList();
            validate();});
        panel.addEmpty();panel.addEmpty();

        new Label("Required:").addTo(panel);
        chkRequired = new CheckBox().addTo(panel);
        panel.addEmpty();

        return panel;
    }

    private Panel createOptionsListGrid() {
        var panel = Panel.gridPanel(3, Margin.of(1,0,1,1));

        new Label("Options List:").addTo(panel);
        cboOptionList = new ComboBox<String>().sizeTo(25).addTo(panel);
        cboOptionList.addListener((selectedIndex, previousSelection, changedByUserInteraction) -> validate());
        btnAddOptionList = new Button("Add", this::addOptionList).addTo(panel);
        panel.addEmpty();
        errOptionsList.addTo(panel).setForegroundColor(TextColor.ANSI.RED);

        return panel;
    }

    private Panel createButtonsPanel() {
        var panel = new Panel()
                .setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.End));

        new Button("Delete", this::deleteQuestion).setVisible(question != null).addTo(panel);
        btnCreateUpdate = new Button(question == null ? "Create" : "Update", this::saveQuestion).addTo(panel);
        new Button("Cancel", this::close).addTo(panel);

        return panel;
    }

    private void refreshOptionList() {
        optionLists = OptionList.getForUser(Security.getLoggedUser());
        cboOptionList.clearItems();
        cboOptionList.addItem("");
        for(var ol : optionLists)
            cboOptionList.addItem(ol.toString());
    }

    private void refreshEnableOptionList() {
        boolean enableOptionList = Question.Type.valueOfIgnoreCase(cboType.getText()).requiresOptionList();
        if(!enableOptionList)
            cboOptionList.setSelectedItem("");
        cboOptionList.setEnabled(enableOptionList);
        btnAddOptionList.setEnabled(enableOptionList);
    }

    private void refresh() {
        refreshOptionList();
        refreshEnableOptionList();
    }

    private void loadData() {
        if (question != null) {
            txtTitle.setText(question.getTitle());
            txtDescription.setText(question.getDescription());
            cboType.setSelectedItem(question.getType().toString());
            chkRequired.setChecked(question.getRequired());
            cboOptionList.setSelectedItem(question.getOptionList() == null ?
                    "" : question.getOptionList().toString());
        }
        else
            cboType.setSelectedItem("Short");
    }

    private void saveQuestion() {
        int optionListIdx = cboOptionList.getSelectedIndex();
        controller.saveQuestion(
                txtTitle.getText(),
                txtDescription.getText(),
                cboType.getText(),
                chkRequired.isChecked(),
                optionListIdx == 0 ? null : optionLists.get(optionListIdx - 1)
        );
    }

    private void deleteQuestion() {
        controller.deleteQuestion();
    }

    private void addOptionList() {
        var optionlist = controller.addOptionList();
        if(optionlist != null) {
            refresh();
            cboOptionList.setSelectedItem(optionlist.toString());
        }
    }

    private void validate() {
        int optionListIdx = cboOptionList.getSelectedIndex();
        var errors = controller.validate(
                txtTitle.getText(),
                txtDescription.getText(),
                cboType.getText(),
                (optionListIdx == 0 || optionListIdx == -1) ? null : optionLists.get(optionListIdx - 1)
        );

        errTitle.setText(errors.getFirstErrorMessage(Question.Fields.Title));
        errDescription.setText(errors.getFirstErrorMessage(Question.Fields.Description));
        errOptionsList.setText(errors.getFirstErrorMessage(Question.Fields.OptionList));

        btnCreateUpdate.setEnabled(errors.isEmpty());
    }
}
