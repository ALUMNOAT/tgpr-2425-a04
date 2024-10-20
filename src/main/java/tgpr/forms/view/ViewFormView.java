package tgpr.forms.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.forms.controller.ViewFormController;
import tgpr.forms.model.Form;
import tgpr.forms.model.Question;

import java.util.List;

import static tgpr.framework.Tools.asString;
import static tgpr.framework.Tools.ifNull;

public class ViewFormView extends DialogWindow {

    private final ViewFormController controller;
    private Form form;
    private boolean reorderMode = false;

    private final Label lblTitle = new Label("");
    private final Label lblDescription = new Label("");
    private final Label lblCreatedBy = new Label("");
    private final Label lblIsPublic = new Label("");
    private final Label lblNoQuestions = new Label("No questions");
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
        lblReadOnly.setForegroundColor(TextColor.ANSI.BLUE_BRIGHT).center();
        return lblReadOnly;
    }

    private Border createQuestionPanel() {
        var panel = pnlQuestions = new Panel().fill();
        Border border = panel.withBorder(Borders.singleLine(" Questions "));
        questionTable = new ObjectTable<>(
                new ColumnSpec<>("Index", Question::getIdx),
                new ColumnSpec<>("Title", Question::getTitle),
                new ColumnSpec<>("Type", Question::getType),
                new ColumnSpec<>("Required", Question::getRequired),
                new ColumnSpec<>("Option List", Question::getOptionList)
        ).addTo(panel);
        /*
        questionTable.setSelectAction(this::displayQuestion);
        questionTable.setKeyStrokeHandler(ks -> {
            if (ks.equals(KeyStroke.fromString("<del>")))
                if (controller.deleteQuestion(questionTable.getSelected())) {
                    // met le focus sur qqch de visible avant le refresh pour éviter une exception
                    btnToggleFollow.takeFocus();
                    refresh();
                    // remet le focus sur la liste des messages
                    questionTable.takeFocus();
                }
        });
        */
        lblNoQuestions.setForegroundColor(TextColor.ANSI.RED).addTo(panel);

        return border;
    }

    private Panel createButtonsPanel() {
        var panel = new Panel()
                .setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        btnAdd =  new Button("New Question", this::add).addTo(panel);
        btnEdit = new Button("Edit Form", this::edit).addTo(panel);
        btnDelete = new Button("Delete Form", this::delete).addTo(panel);
        btnTogglePublic = new Button("", this::togglePublic).addTo(panel);
        btnShare = new Button("Share", this::share).addTo(panel);
        btnReorder = new Button("Reorder", this::reorder).addTo(panel);
        btnClear = new Button("Clear Instances", this::clear).addTo(panel);
        btnAnalyze = new Button("Analyze", this::analyze).addTo(panel);
        btnClose = new Button("Close", this::close).addTo(panel);
        btnSave = new Button("Save Order", this::save).addTo(panel);
        btnCancel = new Button("Cancel", this::cancel).addTo(panel);

        /*
        if (!Security.isLoggedUser(member)) {
            btnToggleFollow = new Button("", this::toggleFollow).addTo(panel);
        }

        // Le membre connecté ne peut modifier que ses données ou celles des autres s'il est admin
        if (Security.isAdmin() || Security.isLoggedUser(member)) {
            var btnUpdate = new Button("Update", this::update).addTo(panel);
            addShortcut(btnUpdate, KeyStroke.fromString("<A-u>"));
        }
        // Seul un admin peut supprimer un membre
        if (Security.isAdmin()) {
            var btnDelete = new Button("Delete", this::delete).addTo(panel);
            addShortcut(btnDelete, KeyStroke.fromString("<A-d>"));
        }
         */

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
            lblReadOnly.setText("This form is read only because it has already been answered (" + form.getInstances().size() + " instance(s)).");
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
            btnSave.takeFocus();
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
                btnDelete.takeFocus();
            }
            else {
                btnAdd.setVisible(true);
                btnEdit.setVisible(true);
                btnDelete.setVisible(true);
                btnTogglePublic.setVisible(false);
                btnShare.setVisible(true);
                btnReorder.setVisible(true);
                btnClear.setVisible(false);
                btnAdd.takeFocus();
            }
            btnAnalyze.setVisible(true);
            btnClose.setVisible(true);
            btnSave.setVisible(false);
            btnCancel.setVisible(false);
        }
    }

    private void add() {
        //controller.add();
    }

    private void edit() {
        //controller.edit();
    }

    private void delete() {
        //controller.delete();
    }

    private void share() {
        //controller.share();
    }

    private void reorder() {
        reorderMode = true;
        refresh();
        //controller.reorder();
    }

    private void analyze() {
        //controller.analyze();
    }

    private void clear() {
        //controller.clear();
    }

    private void togglePublic() {
        controller.togglePublic();
        refresh();
    }

    private void save() {
        reorderMode = false;
        refresh();
        //controller.save();
    }

    private void cancel() {
        reorderMode = false;
        refresh();
        //controller.cancel();
    }

    /*
    private void displayQuestion() {
        var question = questionTable.getSelected();
        if (question == null) return;
        if (controller.displayQuestion(question) == null)
            refresh();
     */
}
