package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
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

        //## PANEL ROOT ##
        Panel root = new Panel();

        //### PANEL NAME ###
        Panel pnlName = new Panel().setLayoutManager(new GridLayout(2).setTopMarginSize(1).setRightMarginSize(2));
        new Label("Name:        ").addTo(pnlName);
        txtName = new TextBox(new TerminalSize(52, 1)).addTo(pnlName).setTextChangeListener((txt, byUser) -> validate());
        new EmptySpace().addTo(pnlName);
        errName = new Label("").addTo(pnlName).setForegroundColor(TextColor.ANSI.RED);

        root.addComponent(pnlName);

        //### PANEL INDEX LABEL ###
        Panel pnlOptionValues = new Panel().setLayoutManager(new GridLayout(1)
                .setRightMarginSize(4).setBottomMarginSize(1))
                .setLayoutData(LinearLayout
                        .createLayoutData(LinearLayout.Alignment.End, LinearLayout.GrowPolicy.CanGrow));
        table = new ObjectTable<>(
                new ColumnSpec<>("Index ", OptionValue::getIdx),
                new ColumnSpec<>("Label                                         ", OptionValue::getLabel)
        );
        pnlOptionValues.addComponent(table);
        root.addComponent(pnlOptionValues);

        //### PANEL ADD ###
        Panel pnlAdd = new Panel().setLayoutManager(new GridLayout(2).setRightMarginSize(2).setBottomMarginSize(1));
        errAdd = new Label("").addTo(pnlAdd).setForegroundColor(TextColor.ANSI.RED);
        new EmptySpace().addTo(pnlAdd);
        txtAdd = new TextBox(new TerminalSize(48, 1))
                .addTo(pnlAdd).setTextChangeListener((txt, byUser) -> validate());
        btnAdd = new Button("Add", this::add).addTo(pnlAdd);
        if(optionList == null)
            root.addComponent(pnlAdd);
        else if(optionList.getOwner() != null && (Security.getLoggedUser().isAdmin() || optionList.getOwner().equals(Security.getLoggedUser()))
        && !optionList.isUsed())
            root.addComponent(pnlAdd);

        //### PANEL BUTTONS ###
        root.addComponent(pnlButtons());


        //# SETUP #
        setComponent(root);
        reloadData();
        if (optionList != null) {
            txtName.setText(optionList.getName());
        }
        validate();
    }
    public void reloadData() {
        if(optionList != null) {
            table.clear();
            var values = controller.getValuesByOptionList(optionList.getId());
            table.add(values);
            table.add(controller.getValuesToAdd());
        }
    }

    private void validate() {
        if(optionList == null) {
            var errors = controller.validate(
                    txtAdd.getText(),
                    txtName.getText()
            );
            errAdd.setText(errors.getFirstErrorMessage(OptionValue.Fields.Label));
            errName.setText(errors.getFirstErrorMessage(OptionList.Fields.Name));

            btnAdd.setEnabled(errors.isEmpty());
        } else
            btnAdd.setEnabled(true);
    }

    private void reorder() {
        controller.reorder();
    }

    private void delete() {
        controller.delete();
    }

    private void duplicate() {
        controller.duplicate();
    }

    private void save() {
        controller.save();
    }

    private void add() {
        controller.add(txtAdd.getText());
        reloadData();
        txtAdd.setText("");
    }

    private Panel pnlButtons() {
        Panel pnlButtons = new Panel(new LinearLayout(Direction.HORIZONTAL)).setLayoutData(Layouts.LINEAR_END); //Merci

        if(optionList != null && optionList.getOwner() != null)
            btnReorder = new Button("Reorder", this::reorder).addTo(pnlButtons);
        if(optionList != null && !optionList.isUsed())
            btnDelete = new Button("Delete", this::delete).addTo(pnlButtons).setEnabled(!optionList.isUsed());
        btnDuplicate = new Button("Duplicate", this::duplicate).addTo(pnlButtons);
        if(optionList != null && optionList.getOwner() != null)
            btnSave = new Button("Save", this::save).addTo(pnlButtons);
        btnClose = new Button("Close", this::close).addTo(pnlButtons);

        return pnlButtons;
    }
}
