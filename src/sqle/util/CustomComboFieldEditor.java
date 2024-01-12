package sqle.util;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import java.lang.reflect.Field;

public class CustomComboFieldEditor extends ComboFieldEditor {
	public Combo fCombo;
	public String[][] values;
	
	public CustomComboFieldEditor(String name, String labelText, String[][] entryNamesAndValues, Composite parent) {
		super(name, labelText, entryNamesAndValues, parent);
		this.fCombo = getCustomComboControl();
	}

	public void addNewValues(String[][] newValues) {
		values = newValues;
		setfEntryNamesAndValues();
		fCombo.removeAll();
		for (int i = 0; i < newValues.length; i++) {
			fCombo.add(newValues[i][0], i);
		}
	}
	
    public Combo getCustomComboControl() {
        try {
            // 获取 ComboFieldEditor 中的私有字段 fCombo
            Field fComboField = ComboFieldEditor.class.getDeclaredField("fCombo");

            // 设置字段为可访问
            fComboField.setAccessible(true);
            Combo fComboValue = (Combo) fComboField.get(this);

            return fComboValue;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void setfEntryNamesAndValues() {
        try {
            // 获取 ComboFieldEditor 中的私有字段 fCombo
            Field fEntryNamesAndValuesField = ComboFieldEditor.class.getDeclaredField("fEntryNamesAndValues");

            // 设置字段为可访问
            fEntryNamesAndValuesField.setAccessible(true);
            
            fEntryNamesAndValuesField.set(this, values);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    public void setSelectOption(String value) {
    	if (value == "") {
    		return;
    	}
        if (fCombo != null && !fCombo.isDisposed()) {
        	for (int i = 0; i < values.length; i++) {
        		if (values[i][0].equals(value)) {
        			fCombo.select(i);
                    break;
        		}
            }
        }
    }
}

