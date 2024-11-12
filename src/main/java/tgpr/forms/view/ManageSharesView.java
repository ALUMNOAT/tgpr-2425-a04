package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import tgpr.forms.controller.ManageSharesController;
import tgpr.forms.model.*;
import tgpr.framework.Model;
import java.util.List;
import static tgpr.framework.Controller.askConfirmation;



public class ManageSharesView extends DialogWindow {

    private final ManageSharesController controller;

    private final Form form;

    private  ObjectTable<Model> sharesTable;
    private Panel formsSharesPanel;

    private final ComboBox<Model> potentialBeneficiaries = new ComboBox<>();
    private final ComboBox<AccessType> potentialBeneficiariesAccessType = new ComboBox<>();;

    private Button btnAddShare;
    private Button btnClose;


    public ManageSharesView(ManageSharesController controller, Form form) {
        super("Manage Shares");
        this.controller = controller;
        this.form = form;

        Panel root = new Panel();
        
        setTitle("Manage Shares");
        setHints(List.of(Hint.CENTERED));
        setComponent(root);

        root.addEmpty();
        createFormAccessLists().addTo(root);

        root.addEmpty(new TerminalSize(1,3));

        addAccessPanel().addTo(root);

        root.addEmpty();

        btnClose = new Button("Close", this::close).addTo(root)
                    .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

    }

    private Panel createFormAccessLists(){
        /*
        Creates the table with all the shares of a form (Users and DistLists) and adds it to a Panel.
        The table has 3 columns: The beneficiary's name (Users names and DistLists names), the type of beneficiary (User or DistList) and
        the AccessType that this beneficiary has on this form (Editor ou User)
         */

        formsSharesPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        formsSharesPanel.addEmpty();

        List<Model> beneficiaries = form.getAccesses();


        sharesTable = new ObjectTable<>(
                    new ColumnSpec<>("Beneficiary", form::getBeneficiaryName).setWidth(25),
                    new ColumnSpec<>("Type", this::getBeneficiariesType).setWidth(25),
                    new ColumnSpec<>("Access Right", this::getBeneficiariesAccessType).setWidth(12)
        ).addTo(formsSharesPanel);

        sharesTable.add(beneficiaries);

        formsSharesPanel.addEmpty();

        changeAccessList();
        delAccessList();

        return formsSharesPanel;
    }


    private String getBeneficiariesType(Model beneficiary){

        if(beneficiary instanceof UserFormAccess ua){
            return "User";
        } else if(beneficiary instanceof DistListFormAccess da){
            return "Distribution List";
        }
        return null;
    }


    private AccessType getBeneficiariesAccessType(Model beneficiary){


        if(beneficiary instanceof UserFormAccess u){
            return u.getAccessType();
        } else if (beneficiary instanceof DistListFormAccess d) {
            return d.getAccessType();
        }

        return null;
    }


    private Panel addAccessPanel(){

        /*
        Creates the comboBoxes, the Button Add and adds them to a Panel
         */

        Panel addAccessPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));;

        addAccessPanel.addEmpty();

        createPotentialBeneficiariesCboBox();

        potentialBeneficiaries.addTo(addAccessPanel);

        createPotentialBeneficiariesAccessTypeCboBox();

        potentialBeneficiariesAccessType.addTo(addAccessPanel);

        btnAddShare = new Button("Add", this::addFormAccess).addTo(addAccessPanel);
        btnAddShare.setEnabled(true);

        return addAccessPanel;

    }

    private void createPotentialBeneficiariesCboBox(){
        /*
        Creates the potential beneficiaries comboBox. The beneficiary can be either a User or a Distribution List
         */

        List<Model> potentialBeneficiariesList = form.getPotentialBeneficiaries();

        for(Model potentialBeneficiary : potentialBeneficiariesList){
            if(potentialBeneficiary instanceof User u){

                potentialBeneficiaries.addItem(u);

            } else if (potentialBeneficiary instanceof DistList l) {

                potentialBeneficiaries.addItem(l);

            }

        }

        potentialBeneficiaries.setReadOnly(false);
        potentialBeneficiaries.setSelectedItem(null);

    }

    private void createPotentialBeneficiariesAccessTypeCboBox(){
        /*
        Creates the potential beneficiaries AccessType comboBox. The beneficiary's AccessType can be either an Editor or a User
         */

        potentialBeneficiariesAccessType.addItem(AccessType.Editor);

        potentialBeneficiariesAccessType.addItem(AccessType.User);
        potentialBeneficiariesAccessType.setSelectedItem(AccessType.User);


    }

    private void addFormAccess(){
        /*
        To add a new list to the table using the selected beneficiary and the selected AccessType to add a new share of a form to the table
         */

        Model beneficiaryToAdd = potentialBeneficiaries.getSelectedItem();

        AccessType beneficiaryAccessTypeToAdd = potentialBeneficiariesAccessType.getSelectedItem();

        if(beneficiaryToAdd != null) {
            if (beneficiaryToAdd instanceof User u) {

                form.addAccess(u, beneficiaryAccessTypeToAdd);
            } else if (beneficiaryToAdd instanceof DistList l) {

                form.addAccess(l, beneficiaryAccessTypeToAdd);
            }

            refresh();
        }


    }

    private void changeAccessList(){
        /*
        Changes the AccessType of the selected list, from User to Editor and vice versa, when we press Enter or the space bar.
        This action has no effect if the form is public because the only AccessType possible in that situation is Editor.
         */

        sharesTable.setSelectAction(() -> {

            Model selectedList = sharesTable.getSelected();

            if((askConfirmation("Are you sure you want to modify this share?", "Modify Share"))) {
                if (!form.getIsPublic() && selectedList != null) {
                    if (selectedList instanceof UserFormAccess ua) {
                        if (ua.getAccessType() == AccessType.Editor) {
                            ua.setAccessType(AccessType.User);
                        } else if (ua.getAccessType() == AccessType.User) {
                            ua.setAccessType(AccessType.Editor);
                        }
                    } else if (selectedList instanceof DistListFormAccess d) {
                        if (d.getAccessType() == AccessType.Editor) {
                            d.setAccessType(AccessType.User);
                        } else if (d.getAccessType() == AccessType.User) {
                            d.setAccessType(AccessType.Editor);
                        }
                    }

                    sharesTable.refresh();
                }
            }

        });

    }


    private void delAccessList(){
        /*
        Deletes the selected list from the table of shares when we press the key Delete
         */

        sharesTable.takeFocus(); //Table needs to be on focus for addKeyboardListener to be used
        addKeyboardListener(sharesTable,this::deleteKeyPressed);
    }

    private Boolean deleteKeyPressed(KeyStroke keyStroke) {
        if(keyStroke.getKeyType()==KeyType.Delete){

            Model selectedList = sharesTable.getSelected();
            controller.deleteShare(selectedList);

            refresh();
            refreshPotentialBeneficiaries();

            return false;
        }

        return true;
    }


    private void refresh(){
        /*
        Updates the table after the user deletes or adds a list of shares to the table of shares.
         */

        List<Model> beneficiaries = form.getAccesses();
        sharesTable.clear();
        sharesTable.add(beneficiaries);

    }

    private void refreshPotentialBeneficiaries(){
        /*
        Updates the ComboBox after the user deletes a list in order to put that same list in the PotentialBeneficiariesComboBox, and
        to allow that list to be added again to the table of shares.
         */

        potentialBeneficiaries.clearItems();

        createPotentialBeneficiariesCboBox();
    }


}
