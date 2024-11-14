package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.forms.controller.AddEditOptionListController;
import tgpr.forms.model.OptionList;
import tgpr.forms.model.OptionValue;
import tgpr.forms.model.Security;

import java.util.List;

import static tgpr.framework.Controller.askConfirmation;

public class AddEditOptionListView extends DialogWindow {
    private final AddEditOptionListController controller;
    private final Panel root = new Panel();
    private Panel pnlName;
    private Panel pnlTable;
    private Panel pnlAdd;
    private Panel pnlButtons;
    private Label errAdd = new Label("");
    private Label errName = new Label("");
    private TextBox txtAdd = new TextBox();
    private TextBox txtName = new TextBox();
    private CheckBox checkBoxSystem;
    private Button btnAdd;
    private Button btnDelete;
    private Button btnSave;
    private Button btnDuplicate;
    private Button btnReorder;
    private Button btnClose;
    private Button btnAlphabetically;
    private Button btnConfirmOrder;
    private Button btnCancel;
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

        pnlName();
        pnlTable();
        pnlButtons();
        if(controller.changeable)
            pnlAdd();

        root.addComponent(pnlName);
        root.addComponent(pnlTable);
        if(controller.changeable)
            root.addComponent(pnlAdd);
        root.addComponent(pnlButtons);

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
            table.add(controller.getReorderMode() ? controller.getNewValues() : controller.getOldValues());
        }
    }

    private boolean validate() {
        var errors = controller.validate(
                txtName.getText()
        );
        errName.setText(errors.getFirstErrorMessage(OptionList.Fields.Name));
        errAdd.setText(errors.getFirstErrorMessage(OptionValue.Fields.Label));

        btnSave.setEnabled(errors.isEmpty());
        btnDuplicate.setEnabled(errors.isEmpty());
        return !errors.hasErrors();
    }

    private boolean validateAdd() {
        var errors = controller.validateAdd(txtAdd.getText());
        errAdd.setText(errors.getFirstErrorMessage(OptionValue.Fields.Label));
        return !errors.hasErrors();
    }

    private void pnlName() {
        pnlName = new Panel(new GridLayout(2).setTopMarginSize(1).setRightMarginSize(2));

        new Label("Name:        ").addTo(pnlName);
        txtName = new TextBox(new TerminalSize(52, 1)).setReadOnly(!controller.changeable).addTo(pnlName);
        new EmptySpace().addTo(pnlName);
        errName = new Label("").setForegroundColor(TextColor.ANSI.RED).addTo(pnlName);

        if(Security.getLoggedUser().isAdmin() &&
                (optionList.getOwner().equals(Security.getLoggedUser()) || optionList.getOwner() == null)
        ) {
            new Label("System:        ").addTo(pnlName);
            checkBoxSystem = new CheckBox().addTo(pnlName);
            new EmptySpace().addTo(pnlName);
            new EmptySpace().addTo(pnlName);
        }
    }

    private void pnlButtons() {
        pnlButtons = new Panel(new LinearLayout(Direction.HORIZONTAL)).setLayoutData(Layouts.LINEAR_END); //Merci
        btnReorder = new Button("Reorder", this::reorder);
        btnDelete = new Button("Delete", this::delete);
        btnDuplicate = new Button("Duplicate", this::duplicate);
        btnSave = new Button("Save", this::save);
        btnClose = new Button("Close", this::closeIt);
        btnAlphabetically = new Button("Alphabetically", this::alphabetReorder);
        btnConfirmOrder = new Button("Confirm order", this::confirmOrder);
        btnCancel = new Button("Cancel", this::cancelReorder);
        setPnlButtons();
    }

    private void setPnlButtons() {
        pnlButtons.removeAllComponents();
        if(controller.getReorderMode()) {
            pnlButtons.addComponent(btnAlphabetically);
            pnlButtons.addComponent(btnConfirmOrder);
            pnlButtons.addComponent(btnCancel);
        } else {
            if (controller.changeable) {
                pnlButtons.addComponent(btnReorder);
                pnlButtons.addComponent(btnDelete);
            }
            pnlButtons.addComponent(btnDuplicate);
            if (controller.changeable)
                pnlButtons.addComponent(btnSave);
            pnlButtons.addComponent(btnClose);
        }
    }

    private void pnlAdd() {
        pnlAdd = new Panel(new GridLayout(2).setRightMarginSize(2).setBottomMarginSize(1));

        errAdd = new Label("").addTo(pnlAdd).setForegroundColor(TextColor.ANSI.RED);
        new EmptySpace().addTo(pnlAdd);
        txtAdd = new TextBox(new TerminalSize(48, 1)).addTo(pnlAdd);
        btnAdd = new Button("Add", this::add);
        pnlAdd.addComponent(btnAdd);
    }

    private void pnlTable() {
        pnlTable = new Panel(
                new GridLayout(1).setRightMarginSize(4).setBottomMarginSize(1))
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.End, LinearLayout.GrowPolicy.CanGrow));

        table = new ObjectTable<>(
                new ColumnSpec<>("Index ", OptionValue::getIdx),
                new ColumnSpec<>("Label                                         ", OptionValue::getLabel)
        ) {
            @Override
            public Interactable.Result handleKeyStroke(KeyStroke keyStroke) {
                if (keyStroke.equals(KeyStroke.fromString("<bs>"))) {
                    removeRow(getSelected(), getSelectedRow());
                    return Interactable.Result.HANDLED;
                }
                return super.handleKeyStroke(keyStroke);
            }
        };

        pnlTable.addComponent(table);
    }

    private void setPnlTable() {
        if(controller.getReorderMode()) {
            table.setSelectAction(this::move);
        }
        else
            table.setSelectAction(null);
    }

    private void delete() {
        controller.delete();
    }

    private void duplicate() {
        controller.duplicate();
    }

    private void save() {
        if(validate()) {
            optionList.setName(txtName.getText());
            controller.save();
            this.close();
        }
    }

    public void closeIt() {
        if(changeable()) {
            if (askConfirmationClose())
                this.close();
        } else
            this.close();
    }

    private void add() {
        if(validateAdd()) {
            controller.add(txtAdd.getText());
            reloadData();
            txtAdd.setText("");
        }
    }

    private void reloadAndSet() {
        reloadData();
        setPnlTable();
        setPnlButtons();
        txtName.takeFocus();
    }

    private void reorder() {
        controller.setReorderMode(true);
        controller.setNewValues(controller.getOldValues());
        reloadAndSet();
    }

    private void alphabetReorder() {
        controller.alphabetReorder();
        reloadData();
    }

    private void confirmOrder() {
        optionList.setName(txtName.getText());
        controller.save();
        reorder(false);
    }

    private void cancelReorder() {
        reorder(false);
        controller.refreshValues();
        reloadData();
    }

    private void removeRow(OptionValue value, int row) {
        if(controller.changeable) {
            controller.remove(value);
            reloadData();
            if(!controller.getCurrentValues().isEmpty()) {
                if(row < controller.getCurrentValues().size())
                    table.setSelectedRow(row);
                else
                    table.setSelectedRow(row-1);
            } else
                txtName.takeFocus();
        }

        pnlTable.addComponent(table);
        return pnlTable;
    }

    private void move() {
        //si on re-appuie sur 'enter', on arrête de bouger
        table.setSelectAction(this::stopMove);
        //move écoute pour voir si on bouge l'élément, si oui on swap() !
        table.addSelectionChangeListener(this::swap);
    }

    private void stopMove() {
        table.addSelectionChangeListener(null);
        table.setSelectAction(this::move);
    }

    private void swap(int oldRow, int newRow, boolean byUser) {
        table.addSelectionChangeListener(null);
        controller.swap(oldRow, newRow);
        reloadData();
        table.setSelectedRow(newRow);
        move();
    }

    public boolean isOptionListSystem() {
        return checkBoxSystem.isChecked();
    }
}
