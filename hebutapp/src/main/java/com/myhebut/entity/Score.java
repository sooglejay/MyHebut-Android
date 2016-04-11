package com.myhebut.entity;

public class Score {
	
	/** 课程 */
	private String course;

	/** 分数 */
	private String score;

	/** 学分 */
	private String credit;

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

	
	@Override
	public String toString() {
		return "ScoreMsg [course=" + course + ", score=" + score + ",credit=" + credit + "]";
	}
}
