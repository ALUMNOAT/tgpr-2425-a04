package tgpr.forms.view;


import com.googlecode.lanterna.gui2.ColumnSpec;
import com.googlecode.lanterna.gui2.ObjectTable;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.model.DistList;
import tgpr.forms.model.Form;
import tgpr.forms.model.User;
import tgpr.framework.Model;

import java.util.List;

public class ManageSharesView extends DialogWindow {

        private final Form form;
        private final User user;

        public ManageSharesView(Form form, User user) {
            super("Manage Shares");
            this.form = form;
            this.user = user;


        }

        private Panel createFormShareLists(){

            Panel FormShareLists = new Panel();

            ObjectTable<ManageSharesView> table = new ObjectTable<>(
                    new ColumnSpec<>("Pseudo", ManageSharesView::getBeneficiaries)

            );

            return FormShareLists;
    }


    private String getBeneficiaries(){

        List<Model> beneficiaries = form.getAccesses();

        String beneficiary="";

        return beneficiary;
    }






}
