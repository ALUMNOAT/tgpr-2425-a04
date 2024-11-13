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
    private boolean isNew = false;

    public AddEditOptionListController(OptionList optionList){
        if(optionList == null) {
            isNew = true;
            optionList = new OptionList();
            optionList.setOwnerId(Security.getLoggedUserId());
            optionList.setName("");
            optionList.setId(OptionList.getMaxId()+1);
        }
        this.optionList = optionList;
        values.addAll(optionList.getOptionValues());
        view = new AddEditOptionListView(this, optionList);
        maxValueIndex = OptionList.getMaxValueIndex(values);
    }

    @Override
    public AddEditOptionListView getView() {
        return view;
    }

    public OptionList getOptionList() {
        return optionList;
    }

    public List<OptionValue> getValuesByOptionList(int optionListID) {
        return OptionValue.getAllByOptionList(optionListID);
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

        if(isNew) {
            optionList.save();
        }
        if(isNew)
            optionList.addValues(values);

        optionList.setValues(values);
        if(Security.getLoggedUser().isAdmin())
            if(view.isOptionListSystem())
                optionList.setOwnerId(null);
            else
                optionList.setOwnerId(Security.getLoggedUserId());
    }

    public void add(String label) {
        OptionValue newValue = new OptionValue(optionList, OptionList.getMaxValueIndex(values)+1, label);
        values.add(newValue);
    }

    public void remove(OptionValue value) {
        values.remove(value);
        view.reloadData();
    }

    public void alphabetReorder() {
        //very inefficient but works
        for (int i = 0; i < values.size(); i++) {
            OptionValue min = values.get(i);
            int indexSwap = i;
            for (int j = i; j < values.size(); j++) {
                if(min.getLabel().compareToIgnoreCase(values.get(j).getLabel()) > 0) {
                    min = values.get(j);
                    indexSwap = j;
                }
            }
            OptionValue temp = values.get(i);
            values.set(i, min);
            values.set(indexSwap, temp);
        }
    }

    public boolean askConfirmationDelete() {
        return askConfirmation(
                "Are you sure you want to delete this option list?",
                "Delete");
    }

    public ErrorList validate(String name){
        var errors = new ErrorList();
        errors.add(OptionListValidator.hasOneValue(values));
        errors.add(OptionListValidator.isValidName(name));
        return errors;
    }

    public ErrorList validateAdd(String label) {
        var errors = new ErrorList();
        errors.add(OptionValueValidator.notEmptyLabel(label));
        return errors;
    }

    public List<OptionValue> getValues() {
        return values;
    }

    public void swap(int oldIndex, int newIndex) {
        OptionValue temp = values.get(oldIndex);
        values.set(oldIndex, values.get(newIndex));
        values.set(newIndex, temp);
    }

    public void gravity() {
        OptionValue.indexGravity(values);
    }

    public void refreshValues() {
        values.clear();
        values.addAll(optionList.getOptionValues());
    }
}
