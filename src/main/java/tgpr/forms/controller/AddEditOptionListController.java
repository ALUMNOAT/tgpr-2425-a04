package tgpr.forms.controller;

import tgpr.forms.model.OptionList;
import tgpr.forms.model.OptionListValidator;
import tgpr.forms.model.OptionValue;
import tgpr.forms.model.OptionValueValidator;
import tgpr.forms.view.AddEditOptionListView;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;

import java.util.List;

public class AddEditOptionListController extends Controller<AddEditOptionListView> {
    private final AddEditOptionListView view;
    private final OptionList optionList;

    public AddEditOptionListController(OptionList optionList){
        view = new AddEditOptionListView(this, optionList);
        this.optionList = optionList;
    }

    @Override
    public AddEditOptionListView getView() {
        return view;
    }

    public List<OptionValue> getValuesByOptionList(int optionListID) {
        return OptionValue.getAllByOptionList(optionListID);
    }

    public void reorder() {
        System.out.println("it works !");
    }

    public void delete() {
        if(!optionList.isUsed() && askConfirmationDelete()) {
            optionList.delete();
            view.close();
        }
    }

    public void duplicate() {
        System.out.println("it works ! it works !");
    }

    public void save() {
        System.out.println("doews it work ? yes");
    }

    public void addOptionValue() {

    }

    public boolean askConfirmationDelete() {
        return askConfirmation(
                "Are you sure you want to delete this option list?",
                "Delete");
    }

    public ErrorList validate(String label, String name){
        var errors = new ErrorList();
        errors.add(OptionValueValidator.isValidLabel(label));
        errors.add(OptionListValidator.isValidName(name));
        return errors;
    }
}
