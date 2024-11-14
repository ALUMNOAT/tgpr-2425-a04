package tgpr.forms.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.AnalyzeController;
import tgpr.forms.model.Form;
import tgpr.forms.model.Question;
import tgpr.forms.model.ValueStat;

import java.util.List;

public class AnalyzeView extends DialogWindow {
    private final AnalyzeController controller;
    private final Form form;
    public Label lblTitle = new Label("");
    public Label lblDescription = new Label("");
    public Label lblNbSubInstances = new Label("");
    private ObjectTable<Question> questionTable;
    private ObjectTable<ValueStat> answerTable;
    private Button btnViewInstances;
    private int currentQuestionIndex = 0;





    public AnalyzeView(AnalyzeController controller, Form form) {
        super("Statistical Analysis of Submitted Instances");

        this.controller = controller;
        this.form = form;
        setFixedSize(new TerminalSize(100, 18));

        setHints(List.of(Hint.EXPANDED));

        lblTitle.setText(controller.getTitle());
        lblDescription.setText(controller.getDescription());
        lblNbSubInstances.setText(form.getCompletedInstances().size() + "");
        setHints(List.of(Window.Hint.CENTERED, Window.Hint.MODAL));

        Panel root = new Panel();
        setComponent(root);
        formTextPanel().addTo(root);
        root.addEmpty();
        analyzePanel().addTo(root);
        root.addEmpty();
        btnsPanel().addTo(root).bottomCenter();

        setCloseWindowWithEscape(true);

        refresh();
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
        Panel analyzePanel = Panel.gridPanel(2, Margin.of(1, 0, 1, 1));
        questionTable = new ObjectTable<>(
                new ColumnSpec<>("Index", Question::getIdx).setWidth(5).alignRight(),
                new ColumnSpec<>("Title", Question::getTitle).setWidth(30)
                        .setOverflowHandling(ColumnSpec.OverflowHandling.Wrap)
        ).addTo(analyzePanel).addSelectionChangeListener(this::test);
        answerTable = new ObjectTable<>(
                new ColumnSpec<>("Value", ValueStat::getValue).setWidth(30)
                        .setOverflowHandling(ColumnSpec.OverflowHandling.Wrap),
                new ColumnSpec<>("Nb Occ.", ValueStat::getCount).setWidth(7),
                new ColumnSpec<>("Ratio", this::getRatiosAsString).setWidth(10)
        ).addTo(analyzePanel);

        return analyzePanel;
    }

    private void test(int oldrow, int newrow, boolean byuser) {
        questionTable.addSelectionChangeListener(null);
        if(oldrow < newrow)
            currentQuestionIndex++;
        else
            currentQuestionIndex--;
        refresh();
        questionTable.setSelectedRow(currentQuestionIndex);
        questionTable.addSelectionChangeListener(this::test);
        /*debug
        System.out.println(oldrow+" "+newrow);
        */
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

    private void refresh() {
        // Get the list of questions
        var questions = controller.getQuestions();

        // Set the items for the question table
        questionTable.setItems(questions);

        // For each question, update the stats for the answer table
        // This should ideally be dynamically set when selecting the question.
        var selectedQuestion = questions.get(currentQuestionIndex);
        var stats = controller.getAnswerStatsForQuestion(selectedQuestion);

        // Update the answer table with the selected question's stats
        answerTable.setItems(stats);
    }

    private String getRatiosAsString(ValueStat valueStat) {
        double count = valueStat.getCount();
        double instanceCount = valueStat.getInstanceCount();
        double ratio = (count / instanceCount) * 100;

        // Format ratio with 1 decimal place
        return String.format("%.1f%%", ratio);
    }
}
