package com.myhebut.manager;

import com.myhebut.listener.ExamListener;

public class ExamManager {

	private ExamListener listener;

	private static ExamManager manager = null;

	public static synchronized ExamManager getInstance() {
		if (manager == null) {
			manager = new ExamManager();
		}
		return manager;
	}

	public void setPageChangedListener(ExamListener listener) {
		this.listener = listener;
	}

	public void sendNextPage() {
		listener.nextPage();
	}

	public void sendAddTrueCount() {
		listener.addTrueCount();
	}

	public void sendAddFalseCount() {
		listener.addFalseCount();
	}

	public void sendChangeCard2Default() {
		listener.changeCard2Default();
	}
	
	public void sendChangeCard2True() {
		listener.changeCard2True();
	}

	public void sendChangeCard2False() {
		listener.changeCard2False();
	}

	public void sendChangeCard2Checked() {
		listener.changeCard2Checked();
	}

	public void sendChangeAnswerStatus2T() {
		listener.changeAnswerStatus2T();
	}

	public void sendChangeAnswerStatus2F() {
		listener.changeAnswerStatus2F();
	}

}
