package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import tgpr.forms.controller.AddEditOptionListController;
import tgpr.forms.model.OptionList;
import tgpr.forms.model.OptionValue;
import tgpr.forms.model.Security;

import java.util.List;

public class AddEditOptionListView extends DialogWindow {
    private final AddEditOptionListController controller;
    private Label errAdd = new Label("");
    private Label errName = new Label("");
    private TextBox txtAdd = new TextBox();
    private TextBox txtName = new TextBox();
    private Button btnAdd;
    private Button btnDelete;
    private Button btnSave;
    private Button btnDuplicate;
    private Button btnReorder;
    private Button btnClose;
    private ObjectTable<OptionValue> table;
    private final OptionList optionList;

    public AddEditOptionListView(AddEditOptionListController controller, OptionList optionList) {
        //# SETUP #
        super((optionList == null ? "Create" : "Update") + " Option List");
        this.controller = controller;
        this.optionList = optionList;
        setHints(List.of(Hint.CENTERED));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(60, 18));

        //## PANELS ##
        Panel root = new Panel();
        root.addComponent(pnlName());
        root.addComponent(pnlTable());
        if(addPnlAdd())
            root.addComponent(pnlAdd());
        root.addComponent(pnlButtons());

        //# SETUP #
        setComponent(root);
        reloadData();
        txtName.setText(optionList != null ? optionList.getName() : null)
                .setTextChangeListener((txt, byUser) -> validate());
        txtAdd.setTextChangeListener((txt, byUser) -> validate());
        validate();
    }

    public void reloadData() {
        if(optionList != null) {
            table.clear();
            table.add(controller.getValues());
        }
    }

    private void validate() {
        var errors = controller.validate(
                txtName.getText()
        );
        errAdd.setText(errors.getFirstErrorMessage(OptionValue.Fields.Label));
        errName.setText(errors.getFirstErrorMessage(OptionList.Fields.Name));
        if(addPnlAdd())
            btnAdd.setEnabled(errors.isEmpty());
        if(btnSave != null)
            btnSave.setEnabled(errors.isEmpty());
    }

    private boolean validateAdd() {
        var errors = controller.validateAdd(txtAdd.getText());
        errAdd.setText(errors.getFirstErrorMessage(OptionValue.Fields.Label));
        return !errors.hasErrors();
    }

    private void reorder(boolean reorder) {
        root.removeComponent(pnlButtons);
        pnlButtons = pnlButtons(reorder);
        root.addComponent(pnlButtons);
        txtName.takeFocus();
    }

    private void reorder() {
        reorder(true);
    }

    private void delete() {
        controller.delete();
        validate();
    }

    private void duplicate() {
        controller.duplicate();
    }

    private void save() {
        controller.save();
    }

    private void add() {
        if(validateAdd()) {
            controller.add(txtAdd.getText());
            reloadData();
            txtAdd.setText("");
        }
    }

    private void alphabetReorder() {
        controller.alphabetReorder();
        reloadData();
    }

    private void confirmOrder() {
        controller.save();
        reorder(false);
    }

    private void cancelReorder() {

    }

    private Panel pnlName() {
        Panel pnlName = new Panel(new GridLayout(2).setTopMarginSize(1).setRightMarginSize(2));

        new Label("Name:        ").addTo(pnlName);
        txtName = new TextBox(new TerminalSize(52, 1)).addTo(pnlName);
        new EmptySpace().addTo(pnlName);
        errName = new Label("").setForegroundColor(TextColor.ANSI.RED).addTo(pnlName);

        return pnlName;
    }

    private Panel pnlButtons(boolean reorder) {
        Panel pnlButtons = new Panel(new LinearLayout(Direction.HORIZONTAL)).setLayoutData(Layouts.LINEAR_END); //Merci

        if(!reorder) {
            if (optionList != null && optionList.getOwner() != null)
                btnReorder = new Button("Reorder", this::reorder).addTo(pnlButtons);
            if (optionList != null && !optionList.isUsed())
                btnDelete = new Button("Delete", this::delete).addTo(pnlButtons).setEnabled(changeable());
            btnDuplicate = new Button("Duplicate", this::duplicate).addTo(pnlButtons);
            if (optionList != null && optionList.getOwner() != null)
                btnSave = new Button("Save", this::save).addTo(pnlButtons);
            btnClose = new Button("Close", this::closeIt).addTo(pnlButtons);
        } else {
            Button btnAlphabetically = new Button("Alphabetically", this::alphabetReorder).addTo(pnlButtons);
            Button btnConfirmOrder = new Button("Confirm order", this::confirmOrder).addTo(pnlButtons);
            Button btnCancel = new Button("Cancel", this::cancelReorder).addTo(pnlButtons);
        }

        return pnlButtons;
    }

    private Panel pnlAdd() {
        Panel pnlAdd = new Panel(new GridLayout(2).setRightMarginSize(2).setBottomMarginSize(1));

        errAdd = new Label("").addTo(pnlAdd).setForegroundColor(TextColor.ANSI.RED);
        new EmptySpace().addTo(pnlAdd);
        txtAdd = new TextBox(new TerminalSize(48, 1))
                .addTo(pnlAdd);
        btnAdd = new Button("Add", this::add).addTo(pnlAdd);

        return pnlAdd;
    }

    private Panel pnlTable() {
        Panel pnlTable = new Panel(
                new GridLayout(1).setRightMarginSize(4).setBottomMarginSize(1))
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.End, LinearLayout.GrowPolicy.CanGrow));

        table = new ObjectTable<>(
                new ColumnSpec<>("Index ", OptionValue::getIdx),
                new ColumnSpec<>("Label                                         ", OptionValue::getLabel)
        )
        {
            @Override
            public Interactable.Result handleKeyStroke(KeyStroke keyStroke) {
                if (keyStroke.equals(KeyStroke.fromString("<bs>"))) {
                    controller.remove(getSelected());
                    AddEditOptionListView.this.reloadData();
                    return Interactable.Result.HANDLED;
                }
                return super.handleKeyStroke(keyStroke);
            }
        }.addTo(pnlTable);
        //addKeyboardListener(table, this::deleteK);

        return pnlTable;
    }

    private boolean addPnlAdd() {
        return optionList == null ||
                !optionList.isUsed() && (
                        optionList.getOwner().equals(Security.getLoggedUser()) ||
                        (Security.getLoggedUser().isAdmin())
                );
    }

    private boolean deleteK(KeyStroke keyStroke) {
        table.takeFocus();
        if(keyStroke.getKeyType().equals(KeyType.Backspace)) {
            System.out.println("CA MARCHE BABY !!!");
            return true;
        }
        return false;
    }

}
