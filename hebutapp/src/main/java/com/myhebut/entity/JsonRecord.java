package com.myhebut.entity;

import java.io.Serializable;
import java.util.List;

public class JsonRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Record> records;

	private float bestScore;

	public float getBestScore() {
		return bestScore;
	}

	public void setBestScore(float bestScore) {
		this.bestScore = bestScore;
	}

	public List<Record> getRecords() {
		return records;
	}

	public void setRecords(List<Record> records) {
		this.records = records;
	}

}
