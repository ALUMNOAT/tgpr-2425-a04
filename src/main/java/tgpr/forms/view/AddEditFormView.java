package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;

import java.util.List;
import java.util.regex.Pattern;

public class AddEditFormView extends BasicWindow {

    private final TextBox txtTitle;
    private final TextBox txtDescription;
    private final CheckBox blnPublic;

    private final Label errTitle;
    private final Label errDescription;

    public AddEditFormView(){ //ControllerAddEditForm controller ?
        setTitle("Add a form");
        setHints(List.of(Hint.CENTERED));

        Panel root = new Panel();
        root.setLayoutManager(new GridLayout(2).setTopMarginSize(1));

        new Label("Title:").addTo(root);
        txtTitle = new TextBox(new TerminalSize(21, 1)).addTo(root)
                .setValidationPattern(Pattern.compile("[a-zA-Z\\d][a-z\\d]{0,21}"))
                .setTextChangeListener((txt, byUser) -> validate());
        new EmptySpace().addTo(root);
        errTitle = new Label("").addTo(root)
                .setForegroundColor(TextColor.ANSI.RED);
        setComponent(root);

        new Label("Description:").addTo(root);
        txtDescription = new TextBox(new TerminalSize(50, 3)).addTo(root)
                .setTextChangeListener((txt, byUser) -> validate());
        new EmptySpace().addTo(root);
        errDescription = new Label("").addTo(root).setForegroundColor(TextColor.ANSI.RED);

        new Label("Public:").addTo(root);
        blnPublic = new CheckBox().addTo(root);
        new EmptySpace().addTo(root);
        new EmptySpace().addTo(root);

        var buttons = new Panel().addTo(root).setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        var btnUpdate = new Button("Update", this::update).addTo(buttons);
        var btnCancel = new Button("Cancel", this::close).addTo(buttons);

        setFixedSize(new TerminalSize(70, 10));
    }



    private void validate() {

    }
     private void update(){

     }

}
