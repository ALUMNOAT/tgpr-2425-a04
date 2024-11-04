package tgpr.forms.controller;

import tgpr.forms.model.OptionList;
import tgpr.forms.view.ManageOptionListsView;
import tgpr.framework.Controller;
import tgpr.forms.model.Form;
import tgpr.forms.model.Question;
import tgpr.forms.view.ViewFormView;

import java.util.List;

public class ManageOptionListsController extends Controller<ManageOptionListsView> {
    private final ManageOptionListsView view;

    public ManageOptionListsController() {
        view = new ManageOptionListsView(this);
    }

    public List<OptionList> getOptionLists() {
        return OptionList.getAll();
    }

    public void addOptionList() {
    }

    public void editOptionList(OptionList optionList) {}

    @Override
    public ManageOptionListsView getView() {
        return view;
    }
}
