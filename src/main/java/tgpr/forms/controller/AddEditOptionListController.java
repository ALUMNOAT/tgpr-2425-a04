package tgpr.forms.controller;

import tgpr.forms.model.OptionList;
import tgpr.forms.model.OptionValue;
import tgpr.forms.view.AddEditOptionListView;
import tgpr.framework.Controller;

import java.util.List;

public class AddEditOptionListController extends Controller<AddEditOptionListView> {
    private final AddEditOptionListView view;

    public AddEditOptionListController(OptionList optionList){
        view = new AddEditOptionListView(this, optionList);
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
        System.out.println("it works too !");
    }

    public void duplicate() {
        System.out.println("it works ! it works !");
    }

    public void save() {
        System.out.println("doews it work ? yes");
    }
}
