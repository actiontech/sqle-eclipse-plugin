package sqle.config;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
public class SQLEAuditResult {
    @SerializedName("audit_level")
    public String AuditLevel;
    @SerializedName("score")
    private int Score;
    @SerializedName("pass_rate")
    private float PassRate;
    @SerializedName("sql_results")
    public ArrayList<SQLEAuditResultItem> SQLResults;
    
	public ArrayList<SQLEAuditResultItem> getSQLResults() {
		return SQLResults;
	}
}
