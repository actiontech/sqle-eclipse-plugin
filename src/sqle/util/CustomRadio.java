package sqle.util;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

public class CustomRadio extends FieldEditor {
    private Composite groupComposite;
    private Button radioButton1;
    private Button radioButton2;
    private Text textField;

    public CustomRadio(String name, String labelText, Composite parent) {
        super(name, labelText, parent);
    }

    @Override
    protected void adjustForNumColumns(int numColumns) {
    }

    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        groupComposite = new Composite(parent, SWT.NONE);
        RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
        rowLayout.marginLeft = 0;
        rowLayout.marginTop = 0;
        groupComposite.setLayout(rowLayout);

        getLabelControl(groupComposite); // Removed setting layout data for label

        radioButton1 = new Button(groupComposite, SWT.RADIO);
        radioButton1.setText("Option 1");

        radioButton2 = new Button(groupComposite, SWT.RADIO);
        radioButton2.setText("Option 2");

        textField = new Text(groupComposite, SWT.SINGLE | SWT.BORDER);
        textField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }

    @Override
    protected void doLoad() {
        // Load value from preferences and set it to the radio buttons and text field
    }

    @Override
    protected void doLoadDefault() {
        // Load default value and set it to the radio buttons and text field
    }

    @Override
    protected void doStore() {
        // Store value from radio buttons and text field to preferences
    }

    @Override
    public int getNumberOfControls() {
        return 3; // Including label
    }

    @Override
    public void setFocus() {
        if (textField != null && !textField.isDisposed()) {
            textField.setFocus();
        }
    }
}

