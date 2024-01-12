package sqle.config;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class AuditResult {
    @SerializedName("level")
    private String level;

    @SerializedName("message")
    private String message;

    @SerializedName("rule_name")
    private String ruleName;
    
    public String getLevel() {
    	return level;
    }
    
    public String getMessage() {
    	return message;
    }
}
