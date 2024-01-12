package sqle.action;

import sqle.util.HttpClientSQLE;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import sqle.config.SQLEAuditResult;
import sqle.config.SQLESettings;
import sqle.util.DialogInfo;

public class Audit {

	public static void AuditSQL(String[] contents, SQLESettings.AuditType type) {
		Display display = PlatformUI.getWorkbench().getDisplay();
        Shell activeShell = display.getActiveShell();

		HttpClientSQLE client = new HttpClientSQLE();
		try {
			// 发送选中sql进行审核
			SQLEAuditResult result = client.AuditSQL(contents, type);

			// 打开审核结果显示视图
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IViewPart viewPart = page.showView("your.plugin.id.customView");

			if (viewPart instanceof CustomView) {
				CustomView customView = (CustomView) viewPart;
				customView.fillAuditContent(result);
			}

		} catch (Exception e) {
			DialogInfo dialog = new DialogInfo(activeShell);
			dialog.displayErrorDialog("audit failed", e.getMessage());
		}
	}
}
