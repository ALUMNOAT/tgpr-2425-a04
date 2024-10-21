package tgpr.forms.controller;

import tgpr.forms.view.view_edit_instance;
import tgpr.framework.Controller;

public class edit_instance extends Controller<view_edit_instance> {

    public boolean isCompleted = true;

    public String description = "Test";
    public String getDescription() {
        return description;
    }

    public String date = "Test/Test/Test";
    public String getDate() {
        return date;
    }

    public String title = "Test";
    public String getTitle() {
        return title;
    }


    public boolean isCompleted() {
        return isCompleted;
    }


    @Override
    public view_edit_instance getView() {
        return new view_edit_instance(this);
    }
}
