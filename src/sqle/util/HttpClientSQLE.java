package sqle.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.http.HttpRequest.BodyPublishers;
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
    	token = settings.getToken();
        uriHead = "http://" + settings.getSQLEAddr();
    }
    
//    // 使用单例模式，减少调用login函数的次数
//    public static HttpClientSQLE getInstance() {  
//        if (instance == null) {  
//            instance = new HttpClientSQLE();  
//        }  
//        return instance;  
//    }  
    
    public void Login() throws Exception {
        Map<String, String> req = new HashMap<>();
        req.put("username", settings.getUserName());
        req.put("password", settings.getPassword());
        
        JSONObject jsonObject = new JSONObject(req);
        String reqJson = jsonObject.toString();

        String formatStr = String.format("{\"session\":%s}", reqJson);

        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(uriHead + loginPath))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(formatStr))
          .build();
        
        CompletableFuture<HttpResponse<String>> responseFuture 
        = client.sendAsync(request, 
           HttpResponse.BodyHandlers.ofString());
        
        HttpResponse<String> response = responseFuture.join();
        int statusCode = response.statusCode();
        String body = response.body();
        JSONObject jsonResponse = new JSONObject(body);
        
        if (statusCode != 200) {
        	throw new Exception("login failed: " + jsonResponse.getString("message"));
        }
        token = jsonResponse.getJSONObject("data").getString("token");
        this.settings.setToken(token);
    }
    
    public Map<String,String> GetProjectList() throws Exception {
        if (token == null || token.equals("")) {
            Login();
        }

        String reqPath = String.format("%s?page_index=%s&page_size=%s", projectPath, "1", "999999");
        HttpResponse<String> response = sendGet(uriHead + reqPath);
        String body = response.body();
        JSONObject jsonResponse = new JSONObject(body);
        
        JSONArray projectsArray = jsonResponse.getJSONArray("data");
        Map<String, String> projectMap = new HashMap<>();
        try {
            for (int i = 0; i < projectsArray.length(); i++) {
                JSONObject projectObject = projectsArray.getJSONObject(i);
                ListProject project = parseJsonToProject(projectObject);
                projectMap.put(project.getName(), project.getProjectUid());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        settings.setProjectUidMap(projectMap);
        return projectMap;
    }
    
    private static ListProject parseJsonToProject(JSONObject json) throws Exception {
        String uid = json.getString("uid");
        String name = json.getString("name");

        return new ListProject(uid, name);
    }
    
    public ArrayList<String> GetDBTypes() throws Exception {
        if (token == null || token.equals("")) {
            Login();
        }
        
        
        HttpResponse<String> response = sendGet(uriHead + driversPath);
        String body = response.body();
        JSONObject jsonResponse = new JSONObject(body);
        
        JSONArray dataTypeJsonArrray = jsonResponse.getJSONObject("data").getJSONArray("driver_name_list");
        
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < dataTypeJsonArrray.length(); i++) {
            list.add(dataTypeJsonArrray.getString(i));
        }
        return list;
    }
    
    public ArrayList<String> GetDataSourceNameList(String projectName, String dbType) throws Exception {
        if (token == null || token.equals("")) {
            Login();
        }
        
        String projectID = settings.getProjectUidMap().get(projectName);

        String dataSourcePath = String.format(HttpClientSQLE.dataSourcePath, projectID);
        String encodedDbType = URLEncoder.encode(dbType, "UTF-8");
        String reqPath = String.format("%s?filter_by_db_type=%s&page_index=%s&page_size=%s", dataSourcePath, encodedDbType, "1", "999999");
        HttpResponse<String> response = sendGet(uriHead + reqPath);
        String body = response.body();
        JSONObject jsonResponse = new JSONObject(body);

        JSONArray projectsArray = jsonResponse.getJSONArray("data");
        ArrayList<String> list = new ArrayList<>();
        try {
            for (int i = 0; i < projectsArray.length(); i++) {
                JSONObject projectObject = projectsArray.getJSONObject(i);
                SQLEDataSourceNameListResult dataSource = parseJsonToDataSource(projectObject);
                list.add(dataSource.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    private static SQLEDataSourceNameListResult parseJsonToDataSource(JSONObject json) throws Exception {
        String name = json.getString("name");

        return new SQLEDataSourceNameListResult(name);
    }
    
    public ArrayList<String> GetSchemaList(String projectName, String dataSourceName) throws Exception {
        if (token == null || token.equals("")) {
            Login();
        }
        
        String reqPath = String.format(HttpClientSQLE.schemaPath, projectName, dataSourceName);
        HttpResponse<String> response = sendGet(uriHead + reqPath);
        String body = response.body();
        JSONObject jsonResponse = new JSONObject(body);
        
        JSONArray dataTypeJsonArrray = jsonResponse.getJSONObject("data").getJSONArray("schema_name_list");

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < dataTypeJsonArrray.length(); i++) {
            list.add(dataTypeJsonArrray.getString(i));
        }
        return list;
    }
    
    public SQLEAuditResult AuditSQL(String[] contents, SQLESettings.AuditType type) throws Exception {
        if (token == null || token.equals("")) {
            Login();
        }
        Gson gson = new Gson();

        Map<String, Object> req = new HashMap<>();
        req.put("instance_type", settings.getDBType());
        req.put("file_contents", contents);
        req.put("project_name", settings.getProjectName());
        req.put("instance_name", settings.getDataSourceName());
        req.put("schema_name", settings.getSchemaName());
        
        switch (type) {
        case SQL:
            req.put("sql_type", "sql");
            break;
        case MyBatis:
            req.put("sql_type", "mybatis");
            break;
    }


        JSONObject jsonObject = new JSONObject(req);
        String reqJson = jsonObject.toString();
        
        HttpResponse<String> response = sendPOST(uriHead + auditPath, reqJson);
        String body = response.body();
        
        JsonParser parser = new JsonParser();
        JsonObject jsonResponse = parser.parse(body).getAsJsonObject();
        
        if (jsonResponse.get("code").getAsInt() != 0) {
        	throw new Exception("audit failed: " + jsonResponse.get("message").getAsString());
        }
        JsonObject data = jsonResponse.get("data").getAsJsonObject();
        return gson.fromJson(data, new SQLEAuditResult().getClass());
    }

    private HttpResponse<String> sendGet(String path) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(path))
          .header("Authorization", "Bearer " + token)
          .GET()
          .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = httpResponse.statusCode();
        if (statusCode == 401) {
        	Login();
        	return sendGet(path);
        }
        if (statusCode != 200) {
        	throw new Exception("response code != 200, message: " + httpResponse.body());
        }
        return httpResponse;
    }
    
    private HttpResponse<String> sendPOST(String path, String reqJSON) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(path))
          .header("Authorization", "Bearer " + token)
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(reqJSON))
          .build();
        
        CompletableFuture<HttpResponse<String>> responseFuture 
        = client.sendAsync(request, 
           HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = responseFuture.join();
        int statusCode = response.statusCode();
        if (statusCode != 200) {
        	throw new Exception("response code != 200, message: " + response.body());
        }
        
        return response;
    }
}
