package com.myhebut.entity;

public class User {

	/** 用户Id */
	private int userId;
	/** 用户名 */
	private String userName;
	/** 用户密码 */
	private String userPass;
	/** 用户昵称 */
	private String nickName;
	/** 邮箱 */
	private String email;
	/** 注册时间 */
	private String create_time;
	/** 头像 0为默认头像,1为用户自定义头像 */
	private String avatar;
	/** 用户查询成绩权限 */
	private int power;

	/** 用于接受确认的密码,与数据库无关 */
	private String confirmUserPass;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPass() {
		return userPass;
	}

	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getConfirmUserPass() {
		return confirmUserPass;
	}

	public void setConfirmUserPass(String confirmUserPass) {
		this.confirmUserPass = confirmUserPass;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", userName=" + userName
				+ ", userName=" + userName + ", userPass=" + userPass
				+ ", nickName=" + nickName + ", power=" + power + "]";
	}
}
