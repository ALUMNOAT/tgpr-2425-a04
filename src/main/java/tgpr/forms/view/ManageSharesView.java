package tgpr.forms.view;


import com.googlecode.lanterna.gui2.ColumnSpec;
import com.googlecode.lanterna.gui2.ObjectTable;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.model.*;
import tgpr.framework.Model;

import java.util.ArrayList;
import java.util.List;

public class ManageSharesView extends DialogWindow {

    private final Form form;
    private final User user;

    private  ObjectTable<DistListFormAccess> sharesTable;

    public ManageSharesView(Form form, User user) {
            super("Manage Shares");
            this.form = form;
            this.user = user;


    }

    private void createFormShareLists(){

        sharesTable = new ObjectTable<>(
                    new ColumnSpec<>("Pseudo", (m) -> {int i=0; String pseudo= getBeneficiaries(i); ++i; return pseudo;}),
                    new ColumnSpec<>("Type", (m) -> m.getDistList().getName() )
        );


    }


    private String getBeneficiaries(int i){

        List<Model> beneficiaries = form.getAccesses();

        List<String> beneficiariesNames = new ArrayList<>();

        for(Model model : beneficiaries){
            beneficiariesNames.add(form.getBeneficiaryName(model));
        }


        return beneficiariesNames.get(i);
    }

    private void getBeneficiariesType(){
        List<Model> beneficiaries = form.getAccesses();

        List<String> beneficiariesAcessType = new ArrayList<>();

        for(Model model : beneficiaries){
            if(model instanceof User){
                beneficiariesAcessType.add("User");
            } else if(model instanceof DistList){
                beneficiariesAcessType.add("DistList");
            }
        }
    }


    private void getBeneficiariesAccessType(){
            List<Model> beneficiaries = form.getAccesses();

            List<AccessType> beneficiariesAccessType = new ArrayList<>();

            for(Model model : beneficiaries){
                if(model instanceof UserFormAccess u){
                    beneficiariesAccessType.add(u.getAccessType());
                } else if (model instanceof DistListFormAccess d) {
                    beneficiariesAccessType.add(d.getAccessType());
                }
            }
    }






}
