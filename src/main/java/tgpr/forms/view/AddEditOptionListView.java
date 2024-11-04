package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.AddEditOptionListController;
import tgpr.forms.model.OptionList;
import tgpr.forms.model.OptionValue;

import java.util.List;

public class AddEditOptionListView extends DialogWindow {
    private final AddEditOptionListController controller;
    private final TextBox txtName;
    private final TextBox txtAdd;
    private final Button btnAdd;
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
        txtName = new TextBox(new TerminalSize(52, 1)).addTo(pnlName);
        root.addComponent(pnlName);

        //### PANEL INDEX LABEL ###
        Panel pnlOptionValues = new Panel().setLayoutManager(new GridLayout(1).setTopMarginSize(1).setRightMarginSize(4)).sizeTo(60, 11);
        table = new ObjectTable<>(
                new ColumnSpec<>("Index ", OptionValue::getIdx),
                new ColumnSpec<>("Label                                          ", OptionValue::getLabel)
        );
        pnlOptionValues.addComponent(table);
        root.addComponent(pnlOptionValues);

        //### PANEL ADD ###
        Panel pnlAdd = new Panel().setLayoutManager(new GridLayout(2).setTopMarginSize(1).setRightMarginSize(2));
        txtAdd = new TextBox(new TerminalSize(48, 1)).addTo(pnlAdd);
        btnAdd = new Button("Add").addTo(pnlAdd);
        root.addComponent(pnlAdd);

        //### PANEL BUTTONS ###
        Panel pnlButtons = new Panel(new LinearLayout(Direction.HORIZONTAL)).addTo(root);
        new Button("Reorder", this::reorder).addTo(pnlButtons);
        new Button("Delete", this::delete).addTo(pnlButtons);
        new Button("Duplicate", this::duplicate).addTo(pnlButtons);
        new Button("Save", this::save).addTo(pnlButtons);
        new Button("Close", this::close).addTo(pnlButtons);


        //# SETUP #
        setComponent(root);
        reloadData();
        if (optionList != null) {
            txtName.setText(optionList.getName());
        }
    }
    public void reloadData() {
        table.clear();
        var values = controller.getValuesByOptionList(optionList.getId());
        table.add(values);
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
}
