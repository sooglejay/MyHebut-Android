
package com.myhebut.entity;

import java.util.List;

public class Term {
	
	private String time;
	
	private List<Score> scores;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public List<Score> getScores() {
		return scores;
	}

	public void setScores(List<Score> scores) {
		this.scores = scores;
	}
	
	
}
