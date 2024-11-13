package tgpr.forms.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.AnalyzeController;
import tgpr.forms.model.Form;
import tgpr.forms.model.Question;
import tgpr.framework.Controller;

import java.util.List;

public class AnalyzeView extends DialogWindow {
    private final AnalyzeController controller;
    private final Form form;
    public Label lblTitle = new Label("");
    public Label lblDescription = new Label("");
    public Label lblNbSubInstances = new Label("");
    private ObjectTable<Question> questionTable;
    private Button btnViewInstances;



    public AnalyzeView(AnalyzeController controller, Form form) {
        super("Statistical Analysis of Submitted Instances");

        this.controller = controller;
        this.form = form;

        lblTitle.setText(controller.getTitle());
        lblDescription.setText(controller.getDescription());
        lblNbSubInstances.setText(form.getCompletedInstances().size()+"");
        setHints(List.of(Window.Hint.CENTERED, Window.Hint.MODAL));

        Panel root = new Panel();
        setComponent(root);
        formTextPanel().addTo(root);
        root.addEmpty();
        analyzePanel().addTo(root);
        root.addEmpty();
        btnsPanel().addTo(root).bottomCenter();

        setCloseWindowWithEscape(true);

        //TODO: not finished I guess

    }

    private Panel formTextPanel() {
        Panel panel = Panel.gridPanel(2, Margin.of(1,1,1,0));

        new Label("Title:").addTo(panel);
        lblTitle.addTo(panel).addStyle(SGR.BOLD);

        new Label("Description:").addTo(panel);
        lblDescription.addTo(panel).addStyle(SGR.BOLD);

        new Label("Number of Submitted Instances:").addTo(panel);
        lblNbSubInstances.addTo(panel).addStyle(SGR.BOLD);

        return panel;
    }

    private Panel analyzePanel() {
        Panel analyzePanel = new Panel();
        Panel.gridPanel(1, Margin.of(1,0,1,1));
        questionTable = new ObjectTable<>(
                new ColumnSpec<>("Index", Question::getIdx).setWidth(5).alignRight(),
                new ColumnSpec<>("Title", Question::getTitle).setWidth(30)
                        .setOverflowHandling(ColumnSpec.OverflowHandling.Wrap),
                new ColumnSpec<>("Value", Question::getAnswers).setWidth(30)
                        .setOverflowHandling(ColumnSpec.OverflowHandling.Wrap),
                new ColumnSpec<>("Nb Occ.", Question::getStats).setWidth(7),
                new ColumnSpec<>("Ratio", Question::getAnswers).setWidth(10)
        ).addTo(analyzePanel);

        return analyzePanel;
    }

    private Panel btnsPanel() {
        Panel panel = Panel.horizontalPanel(1);
        new Button("Close", this::close).addTo(panel);
        btnViewInstances = new Button("View Instances", this::viewInstances).addTo(panel);

        return panel;
    }

    private void viewInstances() {
        controller.viewInstances();
    }


}
