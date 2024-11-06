package tgpr.forms.view;


import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import tgpr.forms.controller.ManageSharesController;
import tgpr.forms.model.*;
import tgpr.framework.Model;

import java.util.List;

public class ManageSharesView extends DialogWindow {

    private final ManageSharesController controller;

    private final Form form;

    private  ObjectTable<Model> sharesTable;

    private final ComboBox<Model> potentialBeneficiaries = new ComboBox<>();
    private final ComboBox<AccessType> potentialBeneficiariesAccessType = new ComboBox<>();;

    private Button btnAddShare;
    private Button btnClose;


    public ManageSharesView(ManageSharesController controller, Form form) {
            super("Manage Shares");
        this.controller = controller;
        this.form = form;

            Panel root = new Panel();


            createFormAccessLists().addTo(root);
            addAccessPanel().addTo(root);


            btnClose = new Button("Close", this::close).addTo(root);

    }

    private Panel createFormAccessLists(){
        // TODO: put the formats and background blue color on the comboBox
        Panel formsSharesPanel = new Panel();
        List<Model> beneficiaries = form.getAccesses();

        sharesTable.add(beneficiaries);

        sharesTable = new ObjectTable<>(
                    new ColumnSpec<>("Pseudo", form::getBeneficiaryName),
                    new ColumnSpec<>("Type", this::getBeneficiariesType),
                    new ColumnSpec<>("Access Right", this::getBeneficiariesAccessType)
        ).addTo(formsSharesPanel);

        return formsSharesPanel;
    }


    private String getBeneficiaries(Model beneficiary){

        return form.getBeneficiaryName(beneficiary);
    }

    private String getBeneficiariesType(Model beneficiary){

        if(beneficiary instanceof UserFormAccess ua){
            return "User";
        } else if(beneficiary instanceof DistListFormAccess da){
            return "DistList";
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

        Panel addAccessPanel = new Panel();

        // TODO: put the formats on the panel and background blue color on the comboBox

        createPotentialBeneficiariesCboBox();
        potentialBeneficiaries.addTo(addAccessPanel);

        createPotentialBeneficiariesAccessTypeCboBox();
        potentialBeneficiariesAccessType.addTo(addAccessPanel);


        btnAddShare = new Button("Add Share", this::addFormAccess).addTo(addAccessPanel);

        return addAccessPanel;

    }

    private void createPotentialBeneficiariesCboBox(){

        List<Model> potentialBeneficiariesList = form.getPotentialBeneficiaries();

        for(Model potentialBeneficiary : potentialBeneficiariesList){
            if(potentialBeneficiary instanceof User u){
                potentialBeneficiaries.addItem(u);
            } else if (potentialBeneficiary instanceof DistList l) {
                potentialBeneficiaries.addItem(l);

            }

        }

        potentialBeneficiaries.addListener((selectedIndex, previousSelection, changedByUserInteraction) -> {potentialBeneficiaries.setSelectedIndex(selectedIndex);});

    }

    private void createPotentialBeneficiariesAccessTypeCboBox(){

        potentialBeneficiariesAccessType.addItem(AccessType.Editor);

        if(form.getIsPublic()) {
            potentialBeneficiariesAccessType.addItem(AccessType.User);
            potentialBeneficiariesAccessType.setSelectedItem(AccessType.User);
        }

        potentialBeneficiariesAccessType.addListener((selectedIndex, previousSelection, changedByUserInteraction) -> {potentialBeneficiariesAccessType.setSelectedIndex(selectedIndex);});

    }


    private void addFormAccess(){

        Model beneficiaryToAdd = potentialBeneficiaries.getSelectedItem();

        AccessType beneficiaryAccessTypeToAdd = potentialBeneficiariesAccessType.getSelectedItem();

        if(beneficiaryToAdd != null) {
            if (beneficiaryToAdd instanceof User u) {

                form.addAccess(u, beneficiaryAccessTypeToAdd);
            } else if (beneficiaryToAdd instanceof DistList l) {

                form.addAccess(l, beneficiaryAccessTypeToAdd);
            }
        }

    }

    private void changeAccessList(){

        sharesTable.setSelectAction(() -> {

            var selectedList = sharesTable.getSelected();

            if(!form.getIsPublic() && selectedList!=null){
                if(selectedList instanceof UserFormAccess ua){
                    if(ua.getAccessType() == AccessType.Editor){
                        ua.setAccessType(AccessType.User);
                    } else if(ua.getAccessType() == AccessType.User){
                        ua.setAccessType(AccessType.Editor);
                    }
                } else if(selectedList instanceof DistListFormAccess d) {
                    if(d.getAccessType() == AccessType.Editor){
                        d.setAccessType(AccessType.User);
                    } else if(d.getAccessType() == AccessType.User){
                        d.setAccessType(AccessType.Editor);
                    }
                }
            }

        });

    }


    private void delAccessList(){

        addKeyboardListener(sharesTable,this::deleteKeyPressed);
    }

    private Boolean deleteKeyPressed(KeyStroke keyStroke) {

        if(keyStroke.getKeyType()==KeyType.Delete){
            var selectedList = sharesTable.getSelected();

            controller.deleteShare(selectedList);
        }

        return false;
    }



}
