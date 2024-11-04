package tgpr.forms.controller;

import tgpr.forms.model.OptionList;
import tgpr.forms.model.OptionValue;
import tgpr.forms.view.AddEditOptionListView;
import tgpr.framework.Controller;

import java.util.List;

public class AddEditOptionListController extends Controller<AddEditOptionListView> {
    private final AddEditOptionListView view;

    public AddEditOptionListController(){
        view = new AddEditOptionListView(this, OptionList.getByKey(1));
    }

    @Override
    public AddEditOptionListView getView() {
        return view;
    }

    public List<OptionValue> getValuesByOptionList(int optionListID) {
        return OptionValue.getAllByOptionList(optionListID);
    }
}
