package sqle.util;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.gson.*;
import sqle.config.*;

public class HttpClientSQLE {
	private static final String loginPath = "/v1/dms/sessions";
	private static final String projectPath = "/v1/dms/projects";
	private static final String driversPath = "/sqle/v1/configurations/drivers";
	private static final String dataSourcePath = "/v1/dms/projects/%s/db_services";
	private static final String schemaPath = "/sqle/v1/projects/%s/instances/%s/schemas";
	private static final String auditPath = "/sqle/v2/audit_files";

	private SQLESettings settings;
	private String uriHead;
	private String token;

	public HttpClientSQLE() {
		settings = SQLESettings.getInstance();
	}
	
	private void DetermineHaveToken() throws Exception {
		if (settings.getLoginType().equals(SQLESettings.PasswordLogin)) {
			if (token == null || token.isEmpty()) {
				Login();
			}
		} else {
			token = settings.getAccessToekn();
		}

	}

	private String GetAddr() {
		String uriHead;
		boolean EnableHttps = settings.isEnableHttps();
		if (EnableHttps) {
			uriHead = "https://" + settings.getSQLEAddr();
		} else {
			uriHead = "http://" + settings.getSQLEAddr();
		}
		return uriHead;
	}

	public void Login() throws Exception {
		Map<String, String> req = new HashMap<>();
		uriHead = GetAddr();
		req.put("username", settings.getUserName());
		req.put("password", settings.getPassword());

        Gson gson = new Gson();
        String reqJson = gson.toJson(req);

		String formatStr = String.format("{\"session\":%s}", reqJson);

        JsonObject resp = sendPOST(uriHead + loginPath, formatStr);
        if (resp.get("code").getAsInt() != 0) {
            throw new Exception("login failed: " + resp.get("message").getAsString());
        }

        token = resp.get("data").getAsJsonObject().get("token").getAsString();

		this.settings.setToken(token);
	}

	public Map<String, String> GetProjectList() throws Exception {
		DetermineHaveToken();

		String reqPath = String.format("%s?page_index=%s&page_size=%s", projectPath, "1", "999999");
		JsonObject resp = sendGet(GetAddr() + reqPath);
        if (resp.get("code").getAsInt() != 0) {
            throw new Exception("get data source list failed: " + resp.get("message").getAsString());
        }

        JsonArray projectsArray = resp.getAsJsonArray("data");
		Map<String, String> projectMap = new HashMap<>();
		try {
		    for (JsonElement element : projectsArray) {
		        if (element.isJsonObject()) {
		            JsonObject projectObject = element.getAsJsonObject();
		            ListProject project = parseJsonToProject(projectObject);
		            projectMap.put(project.getName(), project.getProjectUid());
		        }
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    // 处理其他异常
		}
		settings.setProjectUidMap(projectMap);
		return projectMap;
	}

	private static ListProject parseJsonToProject(JsonObject json) throws Exception {
	    String uid = json.get("uid").getAsString();
	    String name = json.get("name").getAsString();

	    return new ListProject(uid, name);
	}

	public ArrayList<String> GetDBTypes() throws Exception {
		DetermineHaveToken();

		JsonObject resp  = sendGet(GetAddr() + driversPath);
        if (resp.get("code").getAsInt() != 0) {
            throw new Exception("get db type failed: " + resp.get("message").getAsString());
        }

        JsonArray array = resp.get("data").getAsJsonObject().get("driver_name_list").getAsJsonArray();
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            list.add(array.get(i).getAsString());
        }
        return list;
	}

	public ArrayList<String> GetDataSourceNameList(String projectName, String dbType) throws Exception {
		DetermineHaveToken();

		String projectID = settings.getProjectUidMap().get(projectName);

		String dataSourcePath = String.format(HttpClientSQLE.dataSourcePath, projectID);
		String encodedDbType = URLEncoder.encode(dbType, "UTF-8");
		String reqPath = String.format("%s?filter_by_db_type=%s&page_index=%s&page_size=%s", dataSourcePath,
				encodedDbType, "1", "999999");
		JsonObject resp = sendGet(GetAddr() + reqPath);
		
        if (resp.get("code").getAsInt() != 0) {
            throw new Exception("get data source list failed: " + resp.get("message").getAsString());
        }
		

        JsonArray projectsArray = resp.getAsJsonArray("data");
		ArrayList<String> list = new ArrayList<>();
		try {
		    for (JsonElement element : projectsArray) {
		        if (element.isJsonObject()) {
		            JsonObject projectObject = element.getAsJsonObject();
		            SQLEDataSourceNameListResult dataSource = parseJsonToDataSource(projectObject);
		            list.add(dataSource.getName());
		        }
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}

		return list;
	}

	private static SQLEDataSourceNameListResult parseJsonToDataSource(JsonObject json) throws Exception {
	    String name = json.getAsJsonPrimitive("name").getAsString();

		return new SQLEDataSourceNameListResult(name);
	}

	public ArrayList<String> GetSchemaList(String projectName, String dataSourceName) throws Exception {
		DetermineHaveToken();

		String reqPath = String.format(HttpClientSQLE.schemaPath, projectName, dataSourceName);
		JsonObject resp = sendGet(GetAddr() + reqPath);
        if (resp.get("code").getAsInt() != 0) {
            throw new Exception("get schema name list: " + resp.get("message").getAsString());
        }

        JsonArray array = resp.get("data").getAsJsonObject().get("schema_name_list").getAsJsonArray();
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            list.add(array.get(i).getAsString());
        }
		return list;
	}

	public SQLEAuditResult AuditSQL(String[] contents, SQLESettings.AuditType type) throws Exception {
		DetermineHaveToken();

		Map<String, Object> req = new HashMap<>();
		req.put("instance_type", settings.getDBType());
		req.put("file_contents", contents);
		req.put("project_name", settings.getProjectName());
//		req.put("instance_name", settings.getDataSourceName());
//		req.put("schema_name", settings.getSchemaName());

		switch (type) {
		case SQL:
			req.put("sql_type", "sql");
			break;
		case MyBatis:
			req.put("sql_type", "mybatis");
			break;
		}

        Gson newGson = new Gson();
        String reqJson = newGson.toJson(req);
        JsonObject resp = sendPOST(GetAddr() + auditPath, reqJson);
        if (resp.get("code").getAsInt() != 0) {
            throw new Exception("audit failed: " + resp.get("message").getAsString());
        }

        JsonObject data = resp.get("data").getAsJsonObject();
        return newGson.fromJson(data, new SQLEAuditResult().getClass());
	}

	private JsonObject sendGet(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        int responseCode = conn.getResponseCode();
        String respStr = response.toString();

        if (responseCode == 401) {
            if (settings.getLoginType().equals(SQLESettings.PasswordLogin)) {
            	Login();
                return sendGet(path);
            } else {
            	throw new Exception("response code != 200, message: " + respStr);
            }
        }
        if (responseCode != 200) {
            throw new Exception("response code != 200, message: " + respStr);
        }
        return new JsonParser().parse(respStr).getAsJsonObject();
	}

	private JsonObject sendPOST(String path, String request) throws Exception {
		URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("Content-Type", "application/json");
        
        conn.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.write(request.getBytes(StandardCharsets.UTF_8));
        wr.flush();
        wr.close();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        int responseCode = conn.getResponseCode();
        String respStr = response.toString();

        if (responseCode == 401) {
            if (settings.getLoginType().equals(SQLESettings.PasswordLogin)) {
            	Login();
                return sendGet(path);
            } else {
            	throw new Exception("response code != 200, message: " + respStr);
            }
        }
        if (responseCode != 200) {
            throw new Exception("response code != 200, message: " + respStr);
        }
        return new JsonParser().parse(respStr).getAsJsonObject();
	}
}
