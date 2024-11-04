package tgpr.forms.controller;

import tgpr.forms.model.OptionList;
import tgpr.forms.model.Security;
import tgpr.forms.view.ManageOptionListsView;
import tgpr.framework.Controller;

import java.util.List;

public class ManageOptionListsController extends Controller<ManageOptionListsView> {
    private final ManageOptionListsView view;

    public ManageOptionListsController() {
        view = new ManageOptionListsView(this);
    }

    public List<OptionList> getOptionLists() {
        return OptionList.getForUser(Security.getLoggedUser());
    }

    public void addOptionList() {
        //TODO: edit_add_option_list
        //navigateTo(new AddEditOptionListController())
    }

    public void editOptionList(OptionList optionList) {
        //TODO: edit_add_option_list
        //navigateTo(new AddEditOptionListController(optionList))
    }

    @Override
    public ManageOptionListsView getView() {
        return view;
    }
}
