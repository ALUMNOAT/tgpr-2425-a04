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
    private final List<OptionValue> oldValues = new ArrayList<>();
    private final List<OptionValue> newValues = new ArrayList<>();
    private boolean isNew = false;
    boolean reorderMode = false;
    public boolean changeable;


    public AddEditOptionListController(OptionList optionList){
        if(optionList == null) {
            isNew = true;
            optionList = new OptionList();
            optionList.setOwnerId(Security.getLoggedUserId());
            optionList.setName("");
            optionList.setId(OptionList.getMaxId()+1);
        }
        changeable =
                isNew ||
                !optionList.isUsed() && (
                        optionList.getOwner().equals(Security.getLoggedUser()) ||
                                (Security.getLoggedUser().isAdmin())
                );
        this.optionList = optionList;
        newValues.addAll(optionList.getOptionValues());
        oldValues.addAll(optionList.getOptionValues());
        view = new AddEditOptionListView(this, optionList);
    }

    public void setReorderMode(boolean reorderMode) {
        this.reorderMode = reorderMode;
    }

    public boolean getReorderMode() {
        return reorderMode;
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

    public List<OptionValue> getNewValues() {
        return newValues;
    }

    public void setNewValues(List<OptionValue> values) {
        newValues.clear();
        newValues.addAll(values);
    }

    public List<OptionValue> getOldValues() {
        return oldValues;
    }

    public void setOldValues(List<OptionValue> values) {
        oldValues.clear();
        oldValues.addAll(values);
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
            optionList.addValues(newValues);
        }

        optionList.setValues(oldValues);
        if(Security.getLoggedUser().isAdmin()) {
            if (view.isOptionListSystem())
                optionList.setOwnerId(null);
            else
                optionList.setOwnerId(Security.getLoggedUserId());
        }
        optionList.save();
    }

    public void add(String label) {
        OptionValue newValue = new OptionValue(optionList, getMaxValueIndex(getCurrentValues())+1, label);
        if(reorderMode)
            newValues.add(newValue);
        else
            oldValues.add(newValue);
    }

    public void remove(OptionValue value) {
        if(reorderMode)
            newValues.remove(value);
        else
            oldValues.remove(value);
        view.reloadData();
    }

    public void alphabetReorder() {
        //very inefficient but works
        for (int i = 0; i < newValues.size(); i++) {
            OptionValue min = newValues.get(i);
            int indexSwap = i;
            for (int j = i; j < newValues.size(); j++) {
                if(min.getLabel().compareToIgnoreCase(newValues.get(j).getLabel()) > 0) {
                    min = newValues.get(j);
                    indexSwap = j;
                }
            }
            OptionValue temp = newValues.get(i);
            newValues.set(i, min);
            newValues.set(indexSwap, temp);
        }
    }

    public boolean askConfirmationDelete() {
        return askConfirmation(
                "Are you sure you want to delete this option list?",
                "Delete");
    }

    public ErrorList validate(String name){
        var errors = new ErrorList();
        errors.add(OptionListValidator.hasOneValue(getCurrentValues()));
        errors.add(OptionListValidator.isValidName(name));
        return errors;
    }

    public ErrorList validateAdd(String label) {
        var errors = new ErrorList();
        errors.add(OptionValueValidator.notEmptyLabel(label));
        errors.add(OptionValueValidator.uniqueLabelIgnoreCase(oldValues, label));
        return errors;
    }

    public void swap(int oldIndex, int newIndex) {
        OptionValue temp = newValues.get(oldIndex);
        newValues.set(oldIndex, newValues.get(newIndex));
        newValues.set(newIndex, temp);
    }

    public void gravity() {
        OptionValue.indexGravity(newValues);
    }

    public List<OptionValue> getCurrentValues() {
        return reorderMode ? newValues : oldValues;
    }

    public static int getMaxValueIndex(List<OptionValue> values) {
        if(values.isEmpty())
            return 0;
        else {
            int max = 1;
            for(OptionValue value : values) {
                if (value.getIdx() > max)
                    max = value.getIdx();
            }
            return max;
        }
    }
}
