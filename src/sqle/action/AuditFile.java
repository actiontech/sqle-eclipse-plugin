package sqle.action;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import java.nio.file.*;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import sqle.config.SQLESettings;

public class AuditFile implements IHandler {
	 private ArrayList<String> filePaths;
	 private SQLESettings.AuditType type = SQLESettings.AuditType.MyBatis;
	 private boolean isSQLEAuditEnabled = true;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = HandlerUtil.getCurrentStructuredSelection(event);
        filePaths = new ArrayList<>();

		if (selection == null) {
			return null;
		}

		Object firstElement = selection.getFirstElement();
		// 获取审核文件或文件夹
		if (firstElement instanceof IAdaptable) {
			IResource resource = (IResource) ((IAdaptable) firstElement).getAdapter(IResource.class);
			if (resource != null) {
				String filePath = resource.getLocation().toString();
				audit(filePath);
			}
		}
		return null;
	}

	private void audit(String auditContent) {
        ArrayList<String> texts = new ArrayList<>();
        gatherFilesFromDir(auditContent);
        for (String filePath : filePaths) {
            try {
                String text = Files.readString(Path.of(filePath));
                texts.add(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		Audit.AuditSQL(texts.toArray(new String[0]), type);
	}
	
    private void gatherFilesFromDir(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            File dir = new File(path);
            File[] files = dir.listFiles();
            if (files == null) {
                return;
            }
            for (File f : files) {
                if (f.isDirectory()) {
                    gatherFilesFromDir(f.getPath());
                } else if (f.getName().endsWith(".xml")) {
                    addFilePath(f.getPath());
                }
            }
        } else if (file.isFile()) {
        	if (file.getName().endsWith(".sql")) {
                type = SQLESettings.AuditType.SQL;
        	} 
    		addFilePath(file.getPath());
        }
    }
    
    private void addFilePath(String path) {
        if (filePaths.contains(path)) {
            return;
        }
        filePaths.add(path);
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
