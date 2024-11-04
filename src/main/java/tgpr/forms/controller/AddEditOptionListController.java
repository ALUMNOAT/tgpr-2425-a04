package tgpr.forms.controller;

import tgpr.forms.view.AddEditOptionListView;
import tgpr.framework.Controller;

public class AddEditOptionListController extends Controller<AddEditOptionListView> {
    private final AddEditOptionListView view;

    public AddEditOptionListController(){
        view = new AddEditOptionListView(this);
    }

    @Override
    public AddEditOptionListView getView() {
        return view;
    }
}
