package tgpr.forms.view;


import com.googlecode.lanterna.gui2.ColumnSpec;
import com.googlecode.lanterna.gui2.ComboBox;
import com.googlecode.lanterna.gui2.ObjectTable;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.model.*;
import tgpr.framework.Model;

import java.util.ArrayList;
import java.util.List;

public class ManageSharesView extends DialogWindow {

    private final Form form;

    private  ObjectTable<Model> sharesTable;

    private final ComboBox<String> potentialBeneficiaries = new ComboBox<>();
    private final ComboBox<String> potentialBeneficiariesAccess = new ComboBox<>();;


    public ManageSharesView(Form form) {
            super("Manage Shares");
            this.form = form;

            Panel root = new Panel();

    }

    private Panel createFormShareLists(){

        Panel formsSharesPanel = new Panel();
        List<Model> beneficiaries = form.getAccesses();

        sharesTable.add(beneficiaries);

        sharesTable = new ObjectTable<>(
                    new ColumnSpec<>("Pseudo", form::getBeneficiaryName),
                    new ColumnSpec<>("Type", this::getBeneficiariesType),
                    new ColumnSpec<>("Access Right", this::getBeneficiariesType)
        ).addTo(formsSharesPanel);

        return formsSharesPanel;
    }


    private String getBeneficiaries(Model beneficiary){

        return form.getBeneficiaryName(beneficiary);
    }

    private String getBeneficiariesType(Model beneficiary){

        if(beneficiary instanceof User){
            return "User";
        } else if(beneficiary instanceof DistList){
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


    private Panel createAddShares(){

        Panel giveSharesPanel = new Panel();

        createPotentialBeneficiariesComboBox();
        potentialBeneficiaries.addTo(giveSharesPanel);

        potentialBeneficiariesAccess.addItem("User");
        potentialBeneficiariesAccess.addItem("Editor");
        potentialBeneficiariesAccess.setSelectedItem("User");

        potentialBeneficiariesAccess.addTo(giveSharesPanel);




        return giveSharesPanel;

    }

    private void createPotentialBeneficiariesComboBox(){

        List<Model> potentialBeneficiariesList = form.getPotentialBeneficiaries();

        for(Model potentialBeneficiary : potentialBeneficiariesList){
            if(potentialBeneficiary instanceof User u){
                potentialBeneficiaries.addItem(u.getFullName());
            } else if (potentialBeneficiary instanceof DistList l) {
                potentialBeneficiaries.addItem(l.getName());

            }

        }

    }






}
