package com.myhebut.entity;

public class Score {
	
	/** 课程 */
	private String course;

	/** 分数 */
	private String score;

	/** 学分 */
	private String credit;

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
