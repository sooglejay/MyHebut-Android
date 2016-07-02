package com.myhebut.entity;

public class Score {
	
	/** 课程 */
	private String course;
	/** 分数 */
	private String score;
	/** 学分 */
	private String credit;
	/** 课程属性 */
	private String type;
	/** 对应学期 */
	private int term;

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String string) {
		this.credit = string;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTerm() {
		return term;
	}

	public void setTerm(int term) {
		this.term = term;
	}

	@Override
	public String toString() {
		return "ScoreMsg [course=" + course + ", score=" + score + ",credit=" + credit + "]";
	}
}
