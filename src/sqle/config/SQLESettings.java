package sqle.config;

import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;

import sqle.Activator;

public class SQLESettings {
	private static SQLESettings instance;
    private IPreferenceStore store;
    
	public static final String SQLE_ADDR_PREFERENCE_KEY = "sqleAddrPreference";
	public static final String USER_PREFERENCE_KEY = "userPreference";
	public static final String PASSWORD_PREFERENCE_KEY = "passwordPreference";
	public static final String BUTTON_PREFERENCE_KEY = "buttonPreference";
	public static final String PROJECT_PREFERENCE_KEY = "projectPreference";
	public static final String DBTYPE_PREFERENCE_KEY = "dbTypePreference";
	public static final String DATASOURCE_PREFERENCE_KEY = "dataSourcePreference";
	public static final String SCHEMA_PREFERENCE_KEY = "schemaPreference";
	public static final String HTTPS_PREFERENCE_KEY = "httpPreference";
	public static final String LOGIN_TYPE_PREFERENCE_KEY = "loginTypePreference";
	public static final String ACCESS_TOKEN_KEY = "accessTokenKey";
	
	public static final String PasswordLogin = "账号密码登录";
	public static final String TokenLogin = "token登录";


	
    private String SQLEAddr;
    private boolean EnableHttps;
    private String UserName;
    private String Password;
    private String DBType;
    private String ProjectName;
    private String DataSourceName;
    private String SchemaName;
    private String Token;
    private String LoginType;
    private String AccessToken;
    
    public String[][] ProjectList = {};
    public String[][] DBTypeList = {};
    public String[][] DBSourceList = {};
    public String[][] SchemaList = {};
    
    public Map<String,String> projectUidMap;
    
    public void UpdateSettings() {
    	store = Activator.getDefault().getPreferenceStore();
        String addr = store.getString(SQLE_ADDR_PREFERENCE_KEY);
        String userName = store.getString(USER_PREFERENCE_KEY);
        String password = store.getString(PASSWORD_PREFERENCE_KEY);
        String project = store.getString(PROJECT_PREFERENCE_KEY);
        String dbType = store.getString(DBTYPE_PREFERENCE_KEY);
        String dataSource = store.getString(DATASOURCE_PREFERENCE_KEY);
        String schema = store.getString(SCHEMA_PREFERENCE_KEY);
        String httpType = store.getString(HTTPS_PREFERENCE_KEY);
        String loginType = store.getString(LOGIN_TYPE_PREFERENCE_KEY);
        String accessToken = store.getString(ACCESS_TOKEN_KEY);
        
        String[][] projectList = {{project, project}};
        this.setProjectList(projectList);
        String[][] dbTypelist = {{dbType, dbType}};
        this.setDBTypeList(dbTypelist);
        String[][] dataSourceList = {{dataSource, dataSource}};
        this.setDBSourceList(dataSourceList);
        String[][] schemaList = {{schema, schema}};
        this.setSchemaList(schemaList);

        this.setEnableHttps(httpType);
        this.SQLEAddr = addr;
        this.UserName = userName;
        this.Password = password;
        this.ProjectName = project;
        this.DBType = dbType;
        this.DataSourceName = dataSource;
        this.SchemaName = schema;
        this.LoginType = loginType;
        this.AccessToken = accessToken;
    }
    
    private SQLESettings() {
    }
    
    public static SQLESettings getInstance() {
    	if (instance == null) {
    		instance = new SQLESettings();
    	}
    	instance.UpdateSettings();
    	return instance;
    }
    
    public enum AuditType {
        SQL, MyBatis;
    }
    
	public String getToken() {
		return Token;
	}
	public void setToken(String token) {
		Token = token;
	}
	public String getSchemaName() {
		return SchemaName;
	}
	public void setSchemaName(String schemaName) {
		SchemaName = schemaName;
	}
	public String getDataSourceName() {
		return DataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		DataSourceName = dataSourceName;
	}
	public String getProjectName() {
		return ProjectName;
	}
	public void setProjectName(String projectName) {
		ProjectName = projectName;
	}
	public String getDBType() {
		return DBType;
	}
	public void setDBType(String dBType) {
		DBType = dBType;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public boolean isEnableHttps() {
		return EnableHttps;
	}
	public void setEnableHttps(String enableHttps) {
		if (enableHttps.equals("https")) {
			EnableHttps = true;
		} else {
			EnableHttps = false;
		}
	}
	public String getSQLEAddr() {
		return SQLEAddr;
	}
	public void setSQLEAddr(String sQLEAddr) {
		SQLEAddr = sQLEAddr;
	}
    public String[][] getProjectList() {
        return ProjectList;
    }
    public void setProjectList(String[][] projectList) {
        ProjectList = projectList;
    }
    public String[][] getDBTypeList() {
        return DBTypeList;
    }

    public void setDBTypeList(String[][] dbTypeList) {
        DBTypeList = dbTypeList;
    }

    public String[][] getDBSourceList() {
        return DBSourceList;
    }

    public void setDBSourceList(String[][] dbSourceList) {
    	DBSourceList = dbSourceList;
    }

    public String[][] getSchemaList() {
        return SchemaList;
    }
    public void setSchemaList(String[][] schemaList) {
    	SchemaList = schemaList;
    }
    public Map<String, String> getProjectUidMap() {
        return projectUidMap;
    }
    public void setProjectUidMap(Map<String, String> projectUidMap) {
        this.projectUidMap = projectUidMap;
    }
	public String getLoginType() {
		if (LoginType.isEmpty()) {
			return PasswordLogin;
		}
		return LoginType;
	}
	public void setLoginType(String loginType) {
		LoginType = loginType;
	}
	public String getAccessToekn() {
		return AccessToken;
	}
	public void setAccessToken(String accessToken) {
		AccessToken = accessToken;
	}
}
