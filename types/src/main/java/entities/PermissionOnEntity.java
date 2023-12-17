package entities;

public class PermissionOnEntity {

	public PermissionOnEntity() {}
	public PermissionOnEntity(Permission permission, String roleId) {
		this.permission = permission;
		this.roleId = roleId;
	}
	Permission permission;
	String roleId;
	public Permission getPermission() {
		return permission;
	}
	public void setPermission(Permission permission) {
		this.permission = permission;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
}
