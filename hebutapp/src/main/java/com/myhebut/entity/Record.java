package com.myhebut.entity;

public class Record {

	/** 考试记录Id */
	private int recordId;
	/** 题目科目 0为马原,1为毛概上 */
	private int subject;
	/** 考试记录绑定的用户Id */
	private int userId;
	/** 考试开始时间 */
	private String begin_time;
	/** 考试持续时间 */
	private String last_time;
	/** 考试得分 */
	private float score;
	/** 得分率 */
	private String percent;
	/** 用户资料 */
	private User user;

	public int getRecordId() {
		return recordId;
	}

	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}

	public int getSubject() {
		return subject;
	}

	public void setSubject(int subject) {
		this.subject = subject;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getBegin_time() {
		return begin_time;
	}

	public void setBegin_time(String begin_time) {
		this.begin_time = begin_time;
	}

	public String getLast_time() {
		return last_time;
	}

	public void setLast_time(String last_time) {
		this.last_time = last_time;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public String getPercent() {
		return percent;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Record [recordId=" + recordId + "userId=" + userId + ", begin_time=" + begin_time + ",last_time="
				+ last_time + ",percent=" + percent + "]";
	}

}
