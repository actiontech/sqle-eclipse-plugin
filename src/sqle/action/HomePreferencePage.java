package sqle.action;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.widgets.Text;

import sqle.util.HttpClientSQLE;
import sqle.config.SQLESettings;
import sqle.Activator;
import sqle.util.DialogInfo;
import sqle.util.CustomComboFieldEditor;

public class HomePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private StringFieldEditor addrInput;
	private StringFieldEditor userInput;
	private StringFieldEditor passwordInput;
	private StringFieldEditor tokenInput;

	private CustomComboFieldEditor projectCombo;
	private CustomComboFieldEditor dbTypeCombo;
	private CustomComboFieldEditor dataSourceCombo;
	private CustomComboFieldEditor schemaCombo;
	private RadioGroupFieldEditor httpHttpsRadioGroup;

	private SQLESettings settings;
	private HttpClientSQLE client;
	private DialogInfo dialog;

	private String loginType;

	public HomePreferencePage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		this.settings = SQLESettings.getInstance();
		this.client = new HttpClientSQLE();

		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell activeShell = display.getActiveShell();
		this.dialog = new DialogInfo(activeShell);
		this.loginType = settings.getLoginType();
	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();
		
		// 添加sqle地址输入框
		addrInput = new StringFieldEditor(SQLESettings.SQLE_ADDR_PREFERENCE_KEY, "SQLE Addr:", parent);
		
		addField(addrInput);
		// 添加http/https选项框
		String[][] httpHttpsOptions = { { "HTTP", "http" }, { "HTTPS", "https" } };
		httpHttpsRadioGroup = new RadioGroupFieldEditor(SQLESettings.HTTPS_PREFERENCE_KEY, "HTTP", 2, httpHttpsOptions,
				parent, true);
		addField(httpHttpsRadioGroup);
		
		addCustomRadio(parent, SQLESettings.PasswordLogin);
		addCustomRadio(parent, SQLESettings.TokenLogin);

		// 添加用户输入框
		userInput = new StringFieldEditor(SQLESettings.USER_PREFERENCE_KEY, "用户:", parent);
		addField(userInput);

		// 添加密码输入框
		passwordInput = new StringFieldEditor(SQLESettings.PASSWORD_PREFERENCE_KEY, "密码:", parent);
		passwordInput.getTextControl(parent).setEchoChar('*');

		addField(passwordInput);

		tokenInput = new StringFieldEditor(SQLESettings.ACCESS_TOKEN_KEY, "token:", parent);
		addField(tokenInput);

		changeRadioSelect(settings.getLoginType(), parent);

		// 添加按钮
		addCustomButtonField(getFieldEditorParent());

		// 初始化选项
		String[][] projectOptions = settings.getProjectList();
		projectCombo = new CustomComboFieldEditor(SQLESettings.PROJECT_PREFERENCE_KEY, "Project", projectOptions,
				parent);
		addField(projectCombo);
		projectCombo.fCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				settings.setProjectName(projectCombo.fCombo.getText());
				addDBSource();
			}
		});

		String[][] dbTypeOptions = settings.getDBTypeList();
		dbTypeCombo = new CustomComboFieldEditor(SQLESettings.DBTYPE_PREFERENCE_KEY, "DBType", dbTypeOptions, parent);
		addField(dbTypeCombo);
		dbTypeCombo.fCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				settings.setDBType(dbTypeCombo.fCombo.getText());
				addDBSource();
			}
		});

		String[][] dbSourceOptions = settings.getDBSourceList();
		dataSourceCombo = new CustomComboFieldEditor(SQLESettings.DATASOURCE_PREFERENCE_KEY, "Data Source",
				dbSourceOptions, parent);
		addField(dataSourceCombo);
		dataSourceCombo.fCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				settings.setDataSourceName(dataSourceCombo.fCombo.getText());
				addSchema();
			}
		});

		String[][] schemaOptions = settings.getSchemaList();
		schemaCombo = new CustomComboFieldEditor(SQLESettings.SCHEMA_PREFERENCE_KEY, "Schema", schemaOptions, parent);
		addField(schemaCombo);
		schemaCombo.fCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				settings.setSchemaName(schemaCombo.fCombo.getText());
			}
		});
	}

	private void addCustomButtonField(Composite parent) {
		Button customButton = new Button(parent, SWT.PUSH);
		customButton.setText("Test Connection");

		// 创建按钮的布局数据
		GridData buttonLayoutData = new GridData();
		buttonLayoutData.horizontalSpan = 2; // 设置水平跨度为2，使其跨越两列

		// 应用布局数据到按钮上
		customButton.setLayoutData(buttonLayoutData);

		// 添加选择监听器以处理按钮点击事件
		customButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleButtonClick();
			}
		});
	}

	private void handleButtonClick() {
		settings.setSQLEAddr(addrInput.getStringValue());
		settings.setUserName(userInput.getStringValue());
		settings.setPassword(passwordInput.getStringValue());
		settings.setAccessToken(tokenInput.getStringValue());

		Composite radioBoxControl = httpHttpsRadioGroup.getRadioBoxControl(getFieldEditorParent());

		String selectedValue = "http";
		if (radioBoxControl != null) {
			Control[] children = radioBoxControl.getChildren();

			for (Control child : children) {
				if (child instanceof Button) {
					Button button = (Button) child;

					if (button.getSelection()) {
						// 获取选中的按钮的数据
						selectedValue = (String) button.getData();
						break;
					}
				}
			}
		}

		settings.setEnableHttps(selectedValue);
		try {
			if (settings.getLoginType().equals(SQLESettings.PasswordLogin) || settings.getLoginType().isEmpty()) {
				client.Login();
			} else {
				client.GetProjectList();
			}

			dialog.displaySuccessDialog("Test Connect", "Test Connection Success");
		} catch (Exception e) {
			e.printStackTrace();
			dialog.displayErrorDialog("Exception", "An exception occurred: " + e.getMessage());
			return;
		}

		// 添加project下拉框中的值
		addProjectInfo();
		// 添加DBType下拉框中的值
		addDBType();
	}

	private void addProjectInfo() {
		try {
			Map<String, String> projectMap = client.GetProjectList();
			List<String[]> resultList = new ArrayList<>();

			for (Map.Entry<String, String> entry : projectMap.entrySet()) {
				String[] row = { entry.getKey(), entry.getKey() };
				resultList.add(row);
			}

			// 将 List 转换为二维数组
			String[][] resultArray = new String[resultList.size()][2];
			resultList.toArray(resultArray);
			settings.setProjectList(resultArray);
			projectCombo.addNewValues(resultArray);
			projectCombo.setSelectOption(settings.getProjectName());
		} catch (Exception e) {
			e.printStackTrace();
			dialog.displayErrorDialog("Exception", "An exception occurred: " + e.getMessage());
			return;
		}
	}

	private void addDBType() {
		try {
			ArrayList<String> dbtypeList = client.GetDBTypes();
			String[][] result = convertArrayListToArray(dbtypeList);
			settings.setDBTypeList(result);
			dbTypeCombo.addNewValues(result);
			dbTypeCombo.setSelectOption(settings.getDBType());
		} catch (Exception e) {
			e.printStackTrace();
			dialog.displayErrorDialog("Exception", "An exception occurred: " + e.getMessage());
			return;
		}
	}

	private void addDBSource() {
		String projectName = settings.getProjectName();
		String dbType = settings.getDBType();
		if (projectName == "" || dbType == "") {
			return;
		}
		try {
			ArrayList<String> dbSourceList = client.GetDataSourceNameList(projectName, dbType);
			String[][] result = convertArrayListToArray(dbSourceList);
			settings.setDBSourceList(result);
			dataSourceCombo.addNewValues(result);
			dataSourceCombo.setSelectOption(settings.getDataSourceName());
		} catch (Exception e) {
			e.printStackTrace();
			dialog.displayErrorDialog("Exception", "An exception occurred: " + e.getMessage());
			return;
		}
	}

	private void addSchema() {
		try {
			ArrayList<String> schemaList = client.GetSchemaList(settings.getProjectName(),
					settings.getDataSourceName());
			String[][] result = convertArrayListToArray(schemaList);
			settings.setSchemaList(result);
			schemaCombo.addNewValues(result);
			schemaCombo.setSelectOption(settings.getSchemaName());
		} catch (Exception e) {
			e.printStackTrace();
			dialog.displayErrorDialog("Exception", "An exception occurred: " + e.getMessage());
			return;
		}
	}

	private static String[][] convertArrayListToArray(ArrayList<String> arrayList) {
		int size = arrayList.size();
		String[][] resultArray = new String[size][2];

		for (int i = 0; i < size; i++) {
			resultArray[i][0] = arrayList.get(i);
			resultArray[i][1] = arrayList.get(i);
		}
		return resultArray;
	}

	private void addCustomRadio(Composite parent, String layout) {
		Button radioButton = new Button(parent, SWT.RADIO);
		radioButton.setText(layout);

		if (settings.getLoginType().equals(layout)) {
			radioButton.setSelection(true);
		}

		// 添加选择监听器以处理按钮点击事件
		radioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loginType = layout;
				settings.setLoginType(layout);
				changeRadioSelect(layout, parent);
			}
		});
	}

	private void changeRadioSelect(String layout, Composite parent) {
		if (layout.equals(SQLESettings.PasswordLogin) || layout.isEmpty()) {
			userInput.setEnabled(true, parent);
			passwordInput.setEnabled(true, parent);
			tokenInput.setEnabled(false, parent);
		} else {
			userInput.setEnabled(false, parent);
			passwordInput.setEnabled(false, parent);
			tokenInput.setEnabled(true, parent);
		}
	}

	@Override
	public boolean performOk() {
		super.performOk();
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		preferenceStore.setValue(SQLESettings.LOGIN_TYPE_PREFERENCE_KEY, loginType);
		return true;
	}
}
