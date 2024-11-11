package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import tgpr.forms.controller.ManageSharesController;
import tgpr.forms.model.*;
import tgpr.framework.Model;
import java.util.List;
import static tgpr.framework.Controller.askConfirmation;



public class ManageSharesView extends BasicWindow {

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
        setTitle("Manage Shares");
        setHints(List.of(Hint.CENTERED));
        setComponent(root);

        boolean test = form.getIsPublic();
        System.out.println(test);

        root.addEmpty();
        createFormAccessLists().addTo(root);
        root.addEmpty(new TerminalSize(1,4));
        addAccessPanel().addTo(root);

        root.addEmpty();

        btnClose = new Button("Close", this::close).addTo(root)
                    .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        System.out.println(form);
    }

    private Panel createFormAccessLists(){

        Panel formsSharesPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

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

        potentialBeneficiariesAccessType.addItem(AccessType.Editor);

        if(form.getIsPublic()) {

            potentialBeneficiariesAccessType.addItem(AccessType.User);
            potentialBeneficiariesAccessType.setSelectedItem(AccessType.User);
        }

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

        sharesTable.refresh();

    }

    private void changeAccessList(){

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

        sharesTable.takeFocus();
        addKeyboardListener(sharesTable,this::deleteKeyPressed);

    }

    private Boolean deleteKeyPressed(KeyStroke keyStroke) {
        if(keyStroke.getKeyType()==KeyType.Delete){
            Model selectedList = sharesTable.getSelected();
            controller.deleteShare(selectedList);
            sharesTable.refresh();

            return false;
        }

        return true;
    }


}
