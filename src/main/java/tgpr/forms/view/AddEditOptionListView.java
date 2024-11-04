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
    private final ObjectTable<OptionValue> table;
    private final OptionList optionList;

    public AddEditOptionListView(AddEditOptionListController controller, OptionList optionList) {
        //# SETUP #
        super("Test");
        this.controller = controller;
        this.optionList = optionList;
        setHints(List.of(Hint.CENTERED));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(60, 18));

        //## PANEL ROOT ##
        Panel root = new Panel();

        //### PANEL FORMALITIES ###
        Panel updateOptionListFormalities = new Panel().setLayoutManager(new GridLayout(2).setTopMarginSize(1)).addTo(root);

        //#### NAME ####
        new Label("Name:").addTo(updateOptionListFormalities);
        txtName = new TextBox(new TerminalSize(50, 1)).addTo(updateOptionListFormalities);
        new EmptySpace().addTo(updateOptionListFormalities);
        new EmptySpace().addTo(updateOptionListFormalities);
        //#### INDEX LABEL ####
        table = new ObjectTable<>(
                new ColumnSpec<>("Index", OptionValue::getIdx),
                new ColumnSpec<>("Label                                            ", OptionValue::getLabel)
        );
        System.out.println(optionList.getOptionValues());

        //# SETUP #
        root.addComponent(table);
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
}
