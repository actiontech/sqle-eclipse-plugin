package sqle.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import sqle.config.*;

public class CustomView extends ViewPart {

	public static final String ID = "your.plugin.id.customView";

	private SashForm sashForm;
	private TableColumn leftColumnId;
	private TableColumn leftColumnSQL;
	private TableColumn leftColumnLevel;
	private TableColumn rightColumnResult;
	private Map<String, ArrayList<AuditResult>> auditResultsIdMap;
	private Table leftTable;
	private Table rightTable;

//    private ListViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		sashForm = new SashForm(parent, SWT.HORIZONTAL);

		// Left side table
		Composite leftComposite = new Composite(sashForm, SWT.NONE);
		leftComposite.setLayout(new FillLayout());

		leftTable = new Table(leftComposite, SWT.BORDER | SWT.FULL_SELECTION);
		leftTable.setHeaderVisible(true);
		leftTable.setLinesVisible(true);

		// Add your left side table columns and content here
		leftColumnId = new TableColumn(leftTable, SWT.NONE);
		leftColumnId.setText("序号");

		leftColumnSQL = new TableColumn(leftTable, SWT.NONE);
		leftColumnSQL.setText("审核SQL");
		leftColumnSQL.setWidth(200);

		leftColumnLevel = new TableColumn(leftTable, SWT.NONE);
		leftColumnLevel.setText("审核等级");

		leftColumnId.pack();
		leftColumnLevel.pack();

		// Sash
		sashForm.setSashWidth(5);

		// Right side table
		Composite rightComposite = new Composite(sashForm, SWT.NONE);
		rightComposite.setLayout(new FillLayout());

		rightTable = new Table(rightComposite, SWT.BORDER | SWT.FULL_SELECTION);
		rightTable.setHeaderVisible(true);
		rightTable.setLinesVisible(true);

		// Add your right side table columns and content here
		rightColumnResult = new TableColumn(rightTable, SWT.NONE);
		rightColumnResult.setText("sql审核结果");
		rightColumnResult.setWidth(1000);

		// Set layout data for SashForm
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Set weights for initial sizes of left and right sides
		sashForm.setWeights(new int[] { 1, 2 });

		// Listener to handle selection in left table
		leftTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 获取选择行
				TableItem selectedItem = leftTable.getSelection()[0];

				// 获取选中行中的序号
				String leftItemId = selectedItem.getText(0);

				rightTable.removeAll();

				// 填充右表的值
				ArrayList<AuditResult> results = auditResultsIdMap.get(leftItemId);
				if (results != null) {
					for (AuditResult result : results) {
						String level = result.getLevel();
						String message = result.getMessage();
						TableItem item = new TableItem(rightTable, SWT.NONE);
						item.setText(new String[] { String.format("[%s]%s", level, message) });
					}
				}
			}
		});
	}

	public void fillAuditContent(SQLEAuditResult auditResult) {
		// 清空左右表
		leftTable.removeAll();
		rightTable.removeAll();

		ArrayList<SQLEAuditResultItem> sqlResults = auditResult.getSQLResults();
		auditResultsIdMap = new HashMap<>();

		// 往左表中添加值
		int index = 1;
		for (SQLEAuditResultItem resultItem : sqlResults) {
			TableItem item = new TableItem(leftTable, SWT.NONE);
			String sql = resultItem.getExecSQL();
			String auditLevel = resultItem.getAuditLevel();
			item.setText(new String[] { String.valueOf(index), sql, auditLevel });

			ArrayList<AuditResult> results = resultItem.getAuditResult();
			auditResultsIdMap.put(String.valueOf(index), results);
			index++;
		}
	}

	@Override
	public void setFocus() {
//        viewer.getControl().setFocus();
	}
}
