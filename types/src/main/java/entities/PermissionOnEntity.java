package entities;

import java.util.UUID;

public class PermissionOnEntity {

	public PermissionOnEntity() {}
	public PermissionOnEntity(Permission permission, UUID roleId) {
		this.permission = permission;
		this.roleId = roleId;
	}
	Permission permission;
	UUID roleId;
	public Permission getPermission() {
		return permission;
	}
	public void setPermission(Permission permission) {
		this.permission = permission;
	}
	public UUID getRoleId() {
		return roleId;
	}
	public void setRoleId(UUID roleId) {
		this.roleId = roleId;
	}
}
