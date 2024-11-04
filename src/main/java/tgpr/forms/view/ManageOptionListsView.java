package tgpr.forms.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.ManageOptionListsController;
import tgpr.forms.controller.ViewFormController;
import tgpr.forms.model.Form;
import tgpr.forms.model.OptionList;
import tgpr.forms.model.Question;
import tgpr.framework.Tools;

import java.util.List;

public class ManageOptionListsView extends DialogWindow {

    private final ManageOptionListsController controller;

    private final Label lblNoOptionLists = new Label("No option list found");
    private Panel pnlOptionLists;
    private ObjectTable<OptionList> optionListsTable;
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

    public ManageOptionListsView(ManageOptionListsController controller) {
        super("Option Lists Management");

        this.controller = controller;

        setHints(List.of(Window.Hint.CENTERED));
        setCloseWindowWithEscape(true);

        Panel root = Panel.verticalPanel(1);
        setComponent(root);

        createOptionListsPanel().addTo(root);
        createButtonsPanel().addTo(root);


        refresh();
    }

    private Panel createOptionListsPanel() {
        var panel = pnlOptionLists = new Panel();
        optionListsTable = new ObjectTable<>(
                new ColumnSpec<>("Name", OptionList::getName).setWidth(5).alignRight(),
                new ColumnSpec<>("Values", OptionList::getSize).setWidth(30)
                        .setOverflowHandling(ColumnSpec.OverflowHandling.Wrap),
                new ColumnSpec<OptionList>("Type", m ->
                        Tools.ifNull(m.getOwner(), "System")).setWidth(25)
        ).addTo(panel).setSelectAction(this::editOptionList);

        lblNoOptionLists.setForegroundColor(TextColor.ANSI.RED).addTo(panel);

        return panel;
    }

    private Panel createButtonsPanel() {
        var panel = new Panel()
                .setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        btnAdd =  new Button("New List", this::addOptionList).addTo(panel);
        btnClose = new Button("Close", this::close).addTo(panel);

        return panel;
    }

    private void refresh() {
        var optionLists = controller.getOptionLists();
        pnlOptionLists.removeAllComponents();
        if (optionLists.isEmpty()) {
            pnlOptionLists.addComponent(lblNoOptionLists);
        } else {
            pnlOptionLists.addComponent(optionListsTable);
            optionListsTable.clear();
            optionListsTable.add(optionLists);
        }
    }

    private void addOptionList() {
        controller.addOptionList();
        refresh();
    }

    private void editOptionList() {
        int selectedRow = optionListsTable.getSelectedRow();
        controller.editOptionList(optionListsTable.getSelected());
        refresh();
        optionListsTable.takeFocus();
        optionListsTable.setSelectedRow(selectedRow);
    }
}
