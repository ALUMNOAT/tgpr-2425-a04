package tgpr.forms.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.forms.controller.ViewInstanceController;
import tgpr.forms.model.Form;
import tgpr.forms.model.Instance;
import tgpr.forms.model.Question;
import tgpr.framework.Controller;
import tgpr.framework.Tools;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static tgpr.framework.Controller.askConfirmation;

public class ViewInstanceView extends DialogWindow {
    private final ViewInstanceController controller;
    private final Form form;
    public Label lblTitle = new Label( "" );
    public Label lblDescription = new Label( "" );
    private ObjectTable<Instance> table;

    public ViewInstanceView( ViewInstanceController controller) {
        super( "List of Submitted Instances" );
        this.controller = controller;
        this.form = controller.form;
        lblTitle.setText( form.getTitle());lblDescription.setText( form.getDescription() );
        setHints( List.of( Window.Hint.CENTERED , Window.Hint.MODAL ) );

        Panel root = new Panel();
        setComponent( root );
        formTextPanel().addTo( root );
        root.addEmpty();
        instancePanel().addTo( root );
        root.addEmpty();
        buttonsPanel().addTo( root ).bottomCenter();

    }

    private Panel formTextPanel() {
        Panel panel = Panel.gridPanel(2, Margin.of(1, 1, 1, 0));

        new Label("Title:").addTo(panel);
        lblTitle.addTo(panel).addStyle( SGR.BOLD);

        new Label("Description:").addTo(panel);
        lblDescription.addTo(panel).addStyle(SGR.BOLD);

        return panel;
    }

    private Panel instancePanel() {
        Panel instancePanel = new Panel();
         table = new ObjectTable<>(
                new ColumnSpec<>("Id", Instance::getId).setWidth(5).alignRight(),
                new ColumnSpec<>("User", Instance::getUser).setWidth(30)
                        .setOverflowHandling(ColumnSpec.OverflowHandling.Wrap),
                new ColumnSpec<>("Submiited", Instance::getCompleted).setWidth(20)
                        .setOverflowHandling(ColumnSpec.OverflowHandling.Wrap)
        ).addTo( instancePanel );

        table.add( form.getCompletedInstances() );
        return instancePanel;
    }
    private Panel buttonsPanel() {
        Panel panel = Panel.horizontalPanel(1 );
        Button btnDelSel = new Button( "Delete Selected" , this::deleteSelected ).addTo( panel );
        addShortcut(btnDelSel, KeyStroke.fromString("<del>" ));
        Button btnDelAll = new Button( "Delete All" , this::deleteAll ).addTo( panel );
        new Button( "Close" , this::close ).addTo( panel );

        return panel;
    }

    private void deleteSelected() {
        Instance i = table.getSelected();
        if(askConfirmation( "Are you sure you want to delete this instances?",
                "Delete Instance" )){
            i.delete();
        }

    }

    private void deleteAll( ) {
        if(askConfirmation( "Are you sure you want to delete all the submitted instances?"
                + System.lineSeparator() +
                "Note: This will not delete instances that are currently being edited (not submitted).",
                "Delete All Instances" )){
            form.deleteAllSubmittedInstances();
            }

    }

    public void refresh(){
        instancePanel();
    }


}
