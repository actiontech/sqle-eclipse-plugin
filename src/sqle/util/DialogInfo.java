package sqle.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.jface.dialogs.Dialog;

public class DialogInfo extends Dialog {
    public DialogInfo(Shell parentShell) {
		super(parentShell);
	}

	public void displayErrorDialog(String title, String message) {	
        MessageDialog.openError(
        		getShell(),
                title,
                message);
    }

    public void displaySuccessDialog(String title, String message) {	
        MessageDialog.openInformation(
        		getShell(),
                title,
                message);
    }
}
