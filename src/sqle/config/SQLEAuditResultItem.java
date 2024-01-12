package sqle.config;


import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;
import java.util.ArrayList;

@Data
@ToString
public class SQLEAuditResultItem {
    @SerializedName("number")
    private int Number;
    @SerializedName("exec_sql")
    private String ExecSQL;
    @SerializedName("audit_result")
    private ArrayList<AuditResult> AuditResult;
    @SerializedName("audit_level")
    private String AuditLevel;
    
    public String getExecSQL() {
    	return ExecSQL;
    }
    
    public String getAuditLevel() {
    	return AuditLevel;
    }
    
    public ArrayList<AuditResult> getAuditResult() {
    	return AuditResult;
    }
}
