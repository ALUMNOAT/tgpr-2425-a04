package tgpr.forms.controller;

import tgpr.forms.model.*;
import tgpr.forms.view.AddEditOptionListView;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;

import java.util.ArrayList;
import java.util.List;

public class AddEditOptionListController extends Controller<AddEditOptionListView> {
    private final AddEditOptionListView view;
    private final OptionList optionList;
    private final List<OptionValue> values = new ArrayList<>();
    private int maxValueIndex;

    public AddEditOptionListController(OptionList optionList){
        values.addAll(optionList.getOptionValues());
        view = new AddEditOptionListView(this, optionList);
        this.optionList = optionList;
        maxValueIndex = optionList.getMaxValueIndex(values);
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
        optionList.duplicate(Security.getLoggedUser());
        view.close();
    }

    public void save() {
        optionList.addValues(values);
        view.close();
    }

    public void add(String label) {
        OptionValue newValue = new OptionValue(optionList, maxValueIndex+values.size()+1, label);
        values.add(newValue);
    }

    public void remove(OptionValue value) {
        values.remove(value);
        view.reloadData();
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

    public List<OptionValue> getValuesToAdd() {
        return values;
    }
}
