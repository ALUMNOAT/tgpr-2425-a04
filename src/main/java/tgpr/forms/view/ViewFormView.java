package tgpr.forms.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.ViewFormController;
import tgpr.forms.model.Form;
import tgpr.forms.model.Question;
import tgpr.framework.Tools;

import java.util.List;

public class ViewFormView extends DialogWindow {

    private final ViewFormController controller;
    private final Form form;
    private boolean reorderMode = false;

    private final Label lblTitle = new Label("");
    private final Label lblDescription = new Label("");
    private final Label lblCreatedBy = new Label("");
    private final Label lblIsPublic = new Label("");
    private final Label lblNoQuestions = new Label("No question yet!");
    private final Label lblReadOnly = new Label("");
    private Panel pnlQuestions;
    private ObjectTable<Question> questionTable;
    private Button btnAdd;
    private Button btnEdit;
    private Button btnDelete;
    private Button btnTogglePublic;
    private Button btnShare;
    private Button btnReorder;
    private Button btnClear;
    private Button btnAnalyze;
    private Button btnClose;
    private Button btnSave;
    private Button btnCancel;

    public ViewFormView(ViewFormController controller, Form form) {
        super("View Form Detail");

        this.controller = controller;
        this.form = form;

        setHints(List.of(Hint.CENTERED));
        setCloseWindowWithEscape(true);

        Panel root = Panel.verticalPanel(1);
        setComponent(root);

        createFields().addTo(root);
        createReadOnlyLabel().addTo(root);
        createQuestionPanel().addTo(root);
        createButtonsPanel().addTo(root);


        refresh();
    }

    private Panel createFields() {
        Panel panel = Panel.gridPanel(2, Margin.of(1, 1, 1, 0));

        new Label("Title:").addTo(panel);
        lblTitle.addTo(panel).addStyle(SGR.BOLD);

        new Label("Description:").addTo(panel);
        lblDescription.addTo(panel).addStyle(SGR.BOLD);

        new Label("Created by:").addTo(panel);
        lblCreatedBy.addTo(panel).addStyle(SGR.BOLD);

        new Label("Is public:").addTo(panel);
        lblIsPublic.addTo(panel).addStyle(SGR.BOLD);

        return panel;
    }

    private Label createReadOnlyLabel() {
        lblReadOnly.setForegroundColor(TextColor.ANSI.BLUE_BRIGHT).addStyle(SGR.BOLD).center();
        return lblReadOnly;
    }

    private Panel createQuestionPanel() {
        var panel = pnlQuestions = new Panel();
        questionTable = new ObjectTable<>(
                new ColumnSpec<>("Index", Question::getIdx).setWidth(5).alignRight(),
                new ColumnSpec<>("Title", Question::getTitle).setWidth(30)
                        .setOverflowHandling(ColumnSpec.OverflowHandling.Wrap),
                new ColumnSpec<>("Type", Question::getType).setWidth(10),
                new ColumnSpec<>("Required", Question::getRequired),
                new ColumnSpec<Question>("Option List", m ->
                        Tools.ifNull(m.getOptionList(), "")).setWidth(25)
        ).addTo(panel);

        lblNoQuestions.setForegroundColor(TextColor.ANSI.RED).addTo(panel);

        return panel;
    }

    private Panel createButtonsPanel() {
        var panel = new Panel()
                .setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        btnAdd =  new Button("New Question", this::addQuestion).addTo(panel);
        btnEdit = new Button("Edit Form", this::editForm).addTo(panel);
        btnDelete = new Button("Delete Form", this::deleteForm).addTo(panel);
        btnTogglePublic = new Button("", this::togglePublic).addTo(panel);
        btnShare = new Button("Share", this::share).addTo(panel);
        btnReorder = new Button("Reorder", this::reorder).addTo(panel);
        btnClear = new Button("Clear Instances", this::clear).addTo(panel);
        btnAnalyze = new Button("Analyze", this::analyze).addTo(panel);
        btnClose = new Button("Close", this::close).addTo(panel);
        btnSave = new Button("Save Order", this::save).addTo(panel);
        btnCancel = new Button("Cancel", this::cancel).addTo(panel);

        return panel;
    }

    private void refresh() {
        if (form != null) {
            refreshFields();
            refreshQuestions();
            refreshButtons();
        }
    }

    private void refreshFields() {
        lblTitle.setText(form.getTitle());
        lblDescription.setText(form.getDescription());
        lblCreatedBy.setText(form.getOwner().getFullName());
        lblIsPublic.setText(form.getIsPublic() ? "true" : "false");
        if(form.isUsed()) {
            lblReadOnly.setText("This form is read only because it has already been answered ("
                    + form.getInstances().size() + " instance(s)).");
            lblReadOnly.setVisible(true);
        }
        else
            lblReadOnly.setVisible(false);
    }

    private void refreshQuestions() {
        var questions = controller.getQuestions();
        pnlQuestions.removeAllComponents();
        if (questions.isEmpty()) {
            pnlQuestions.addComponent(lblNoQuestions);
        } else {
            pnlQuestions.addComponent(questionTable);
            questionTable.clear();
            questionTable.add(questions);
        }
        if(reorderMode)
            questionTable.setSelectAction(this::reorderSelected);
        else
            if(form.isUsed())
                questionTable.setSelectAction(null);
            else
                questionTable.setSelectAction(this::editQuestion);
    }

    private void refreshButtons() {
        btnTogglePublic.setLabel(form.getIsPublic() ? "Make Private" : "Make Public");
        if(reorderMode) {
            btnAdd.setVisible(false);
            btnEdit.setVisible(false);
            btnDelete.setVisible(false);
            btnTogglePublic.setVisible(false);
            btnShare.setVisible(false);
            btnReorder.setVisible(false);
            btnClear.setVisible(false);
            btnAnalyze.setVisible(false);
            btnClose.setVisible(false);
            btnSave.setVisible(true);
            btnCancel.setVisible(true);
        }
        else {
            if(form.isUsed()) {
                btnAdd.setVisible(false);
                btnEdit.setVisible(false);
                btnDelete.setVisible(true);
                btnTogglePublic.setVisible(true);
                btnShare.setVisible(true);
                btnReorder.setVisible(false);
                btnClear.setVisible(true);
            }
            else {
                btnAdd.setVisible(true);
                btnEdit.setVisible(true);
                btnDelete.setVisible(true);
                btnTogglePublic.setVisible(false);
                btnShare.setVisible(true);
                btnReorder.setVisible(!form.getQuestions().isEmpty());
                btnClear.setVisible(false);
            }
            btnAnalyze.setVisible(true);
            btnClose.setVisible(true);
            btnSave.setVisible(false);
            btnCancel.setVisible(false);
        }
    }

    private void addQuestion() {
        controller.addQuestion();
        refresh();
    }

    private void editForm() {
        controller.editForm();
        refresh();
    }

    private void deleteForm() {
        controller.deleteForm();
    }

    private void share() {
        controller.share();
        refresh();
    }

    private void reorder() {
        reorderMode = true;
        refresh();
        btnSave.takeFocus();
    }

    private void analyze() {
        controller.analyze();
        refresh();
    }

    private void clear() {
        controller.clear();
        refresh();
    }

    private void togglePublic() {
        controller.togglePublic();
        refresh();
    }

    private void save() {
        reorderMode = false;
        controller.save(questionTable.getItems());
        refresh();
        btnReorder.takeFocus();
    }

    private void cancel() {
        reorderMode = false;
        refresh();
        btnReorder.takeFocus();
    }

    private void editQuestion() {
        int selectedRow = questionTable.getSelectedRow();
        controller.editQuestion(questionTable.getSelected());
        refresh();
        questionTable.takeFocus();
        questionTable.setSelectedRow(selectedRow);
    }

    private void reorderSelected() {
        questionTable.addSelectionChangeListener(this::reorderMoving);
        questionTable.setSelectAction(this::reorderPlaced);
    }

    private void reorderMoving(int oldIndex, int newIndex, boolean byUser) {
        var tmp = questionTable.getItem(newIndex);
        questionTable.setItem(newIndex, questionTable.getItem(oldIndex));
        questionTable.setItem(oldIndex, tmp);

        questionTable.refresh();
    }

    private void reorderPlaced() {
        questionTable.addSelectionChangeListener(null);
        questionTable.setSelectAction(this::reorderSelected);
    }
}
