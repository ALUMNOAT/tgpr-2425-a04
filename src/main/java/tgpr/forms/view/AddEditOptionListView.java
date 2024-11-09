package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.AddEditOptionListController;
import tgpr.forms.model.OptionList;
import tgpr.forms.model.OptionValue;

import java.util.List;

public class AddEditOptionListView extends DialogWindow {
    private final AddEditOptionListController controller;
    private final Label errAdd;
    private final Label errName;
    private final TextBox txtName;
    private final TextBox txtAdd;
    private final Button btnAdd;
    private final Button btnReorder;
    private final Button btnDelete;
    private final Button btnSave;
    private final ObjectTable<OptionValue> table;
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
//                .sizeTo(59, 11);
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
        btnAdd = new Button("Add", this::addOptionValue).addTo(pnlAdd);
        if(Security.getLoggedUser().isAdmin() && !optionList.isUsed())
            root.addComponent(pnlAdd);

        //### PANEL BUTTONS ###
        Panel pnlButtons = new Panel(new LinearLayout(Direction.HORIZONTAL)).setLayoutData(Layouts.LINEAR_END).addTo(root); //Merci
        btnReorder = new Button("Reorder", this::reorder);
        btnDelete = new Button("Delete", this::delete);
        new Button("Duplicate", this::duplicate).addTo(pnlButtons);
        btnSave = new Button("Save", this::save);
        new Button("Close", this::close).addTo(pnlButtons);

        if(optionList != null && optionList.getOwner() != null)
            btnSave.addTo(pnlButtons);
        if(optionList != null && optionList.getOwner() != null)
            btnReorder.addTo(pnlButtons);
        if(optionList != null && !optionList.isUsed())
            btnDelete.addTo(pnlButtons).setEnabled(!optionList.isUsed());

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
        }
    }

    private void validate() {
        var errors = controller.validate(
                txtAdd.getText()
        );
        errAdd.setText(errors.getFirstErrorMessage(OptionValue.Fields.Label));

        btnAdd.setEnabled(errors.isEmpty());
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

    private void addOptionValue() {
        controller.addOptionValue();
    }
}
