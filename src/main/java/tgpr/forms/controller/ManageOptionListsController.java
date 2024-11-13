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
        navigateTo(new AddEditOptionListController(null));
    }

    public void editOptionList(OptionList optionList) {
        navigateTo(new AddEditOptionListController(optionList));
    }

    @Override
    public ManageOptionListsView getView() {
        return view;
    }
}
