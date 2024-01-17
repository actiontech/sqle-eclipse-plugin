package sqle.action;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import sqle.util.DialogInfo;
import sqle.config.SQLESettings;

public class AuditSql implements IHandler {
	private boolean isSQLEAuditEnabled = true;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell activeShell = display.getActiveShell();
		ITextEditor editor = null;
		IEditorPart part;

		part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		if (part instanceof ITextEditor) {
			editor = (ITextEditor) part;
		} else if (part instanceof MultiPageEditorPart) {
			Object page = ((MultiPageEditorPart) part).getSelectedPage();
			if (page instanceof ITextEditor)
				editor = (ITextEditor) page;
		}

		if (editor != null) {
			IDocumentProvider provider = editor.getDocumentProvider();
			IDocument document = provider.getDocument(editor.getEditorInput());

			// 获取当前文本选择
			ISelection selection = editor.getSelectionProvider().getSelection();
			if (selection instanceof ITextSelection) {
				ITextSelection textSelection = (ITextSelection) selection;
				int offset = textSelection.getOffset();
				int length = textSelection.getLength();

				String selectedText = "";
				try {
					selectedText = document.get(offset, length);
				} catch (BadLocationException e) {
					DialogInfo dialog = new DialogInfo(activeShell);
					dialog.displayErrorDialog("audit failed", e.getMessage());
				}
				String[] selectedTextArray = {selectedText};
				Audit.AuditSQL(selectedTextArray, SQLESettings.AuditType.SQL);
			}
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return isSQLEAuditEnabled;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
	}

	@Override
	public void dispose() {
	}
}
