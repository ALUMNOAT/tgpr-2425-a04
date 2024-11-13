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
    private Panel root = new Panel();
    private Panel pnlButtons;
    private Panel pnlAdd;
    private Panel pnlTable;
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
        root.addComponent(pnlName());
        pnlTable = pnlTable(false);
        root.addComponent(pnlTable);
        if(changeable()) {
            pnlAdd = pnlAdd();
            root.addComponent(pnlAdd);
        }
        pnlButtons = pnlButtons(false);
        root.addComponent(pnlButtons);

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
            controller.gravity();
            table.add(controller.getValues());
        }
    }

    private boolean validate() {
        var errors = controller.validate(
                txtName.getText()
        );
        errName.setText(errors.getFirstErrorMessage(OptionList.Fields.Name));
        if(changeable())
            btnAdd.setEnabled(true);
        if(btnSave != null)
            btnSave.setEnabled(errors.isEmpty());
        if(btnDuplicate != null)
            btnDuplicate.setEnabled(errors.isEmpty());
        errAdd.setText(errors.getFirstErrorMessage(OptionValue.Fields.Label));

        return !errors.hasErrors();
    }

    private boolean validateAdd() {
        var errors = controller.validateAdd(txtAdd.getText());
        errAdd.setText(errors.getFirstErrorMessage(OptionValue.Fields.Label));
        return !errors.hasErrors();
    }

    private void reorder(boolean reorder) {
        if(reorder) {
            controller.save();
        }
        root.removeComponent(pnlTable);
        pnlTable = pnlTable(reorder);
        root.addComponent(pnlTable);

        if(changeable()) {
            root.removeComponent(pnlAdd);
            pnlAdd = pnlAdd();
            root.addComponent(pnlAdd);
        }

        root.removeComponent(pnlButtons);
        pnlButtons = pnlButtons(reorder);
        root.addComponent(pnlButtons);

        reloadData();

        txtName.takeFocus();
    }

    private void reorder() {
        reorder(true);
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

    private Panel pnlName() {
        Panel pnlName = new Panel(new GridLayout(2).setTopMarginSize(1).setRightMarginSize(2));

        new Label("Name:        ").addTo(pnlName);
        txtName = new TextBox(new TerminalSize(52, 1)).addTo(pnlName);
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

    private Panel pnlTable(boolean reorder) {
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
        };
        if(reorder) {
            //on entre dans la méthode move quand on appuie sur 'enter'
            table.setSelectAction(this::move);
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

    private void swap(int oldRow, int newRow, boolean byUser) {
        table.addSelectionChangeListener(null);
        controller.swap(oldRow, newRow);
        reloadData();
        table.setSelectedRow(newRow);
        move();
    }

    private void stopMove() {
        table.addSelectionChangeListener(null);
        table.setSelectAction(this::move);
    }
    private boolean changeable() {
        return optionList == null ||
                !optionList.isUsed() && (
                        optionList.getOwner().equals(Security.getLoggedUser()) ||
                        (Security.getLoggedUser().isAdmin())
                );
    }

    public boolean askConfirmationClose() {
        return askConfirmation(
                "Are you sure you want to cancel ?",
                "Cancel");
    }

    public boolean isOptionListSystem() {
        return checkBoxSystem.isChecked();
    }

}
