package sqle.util;

public class ListProject {
    private String projectUid;
    private String name;

    // 以下是构造函数、getter和setter方法等，具体根据需要定义

    public ListProject() {
    }

    public ListProject(String projectUid, String name) {
        this.projectUid = projectUid;
        this.name = name;
    }

    public String getProjectUid() {
        return projectUid;
    }

    public void setProjectUid(String projectUid) {
        this.projectUid = projectUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
