package chartreux.applilabv2.Entity;

public class UserInLab {
    private String userId;
    private String labId;
    private String roleId;

    public UserInLab(String userId, String labId, String roleId) {
        this.userId = userId;
        this.labId = labId;
        this.roleId = roleId;
    }

    public String getUserId() {
        return userId;
    }

    public String getLabId() {
        return labId;
    }

    public String getRoleId() {
        return roleId;
    }
}
