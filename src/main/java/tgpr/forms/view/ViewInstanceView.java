package tgpr.forms.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Margin;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.ViewInstanceController;
import tgpr.forms.model.Form;
import tgpr.framework.Controller;

import java.util.List;

public class ViewInstanceView extends DialogWindow {
    private final ViewInstanceController controller;
    private final Form form;
    public Label lblTitle = new Label( "" );
    public Label lblDescription = new Label( "" );

    public ViewInstanceView( ViewInstanceController controller) {
        super( "List of Submitted Instances" );
        this.controller = controller;
        this.form = new Form();
        lblTitle.setText( form.getTitle());lblDescription.setText( form.getDescription() );
        setHints( List.of( Window.Hint.CENTERED , Window.Hint.MODAL ) );

    }

    private Panel formTextPanel() {
        Panel panel = Panel.gridPanel(2, Margin.of(1, 1, 1, 0));

        new Label("Title:").addTo(panel);
        lblTitle.addTo(panel).addStyle( SGR.BOLD);

        new Label("Description:").addTo(panel);
        lblDescription.addTo(panel).addStyle(SGR.BOLD);

        return panel;
    }
}
