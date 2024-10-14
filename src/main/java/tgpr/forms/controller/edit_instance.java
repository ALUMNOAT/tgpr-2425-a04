package tgpr.forms.controller;

import tgpr.forms.view.TestView;
import tgpr.forms.view.view_edit_instance;
import tgpr.framework.Controller;

public class edit_instance extends Controller<view_edit_instance> {
    private final view_edit_instance view = new view_edit_instance(this);

    @Override
    public view_edit_instance getView() {
        return view;
    }

}
