package com.myhebut.entity;

import java.io.Serializable;

public class Question implements Serializable {

    /**
     * 题库中的题号
     */
    private int questionId;
    /**
     * 题目科目 0为马原,1为毛概上
     */
    private int subject;
    /**
     * 题目类型 0为单选,1为多选
     */
    private int type;
    /**
     * 章节
     */
    private String section;
    /**
     * 章节中的题号
     */
    private int number;
    /**
     * 题目
     */
    private String content;
    /**
     * A选项
     */
    private String answerA;
    /**
     * B选项
     */
    private String answerB;
    /**
     * C选项
     */
    private String answerC;
    /**
     * D选项
     */
    private String answerD;
    /**
     * E选项
     */
    private String answerE;
    /**
     * 正确选项
     */
    private String answerT;

    /**
     * 在线考试时题号(与数据库无关)
     */
    private int examId;
    /**
     * 在线考试时用户选项(与数据库无关)
     */
    private String answerF;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getSubject() {
        return subject;
    }

    public void setSubject(int subject) {
        this.subject = subject;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnswerA() {
        return answerA;
    }

    public void setAnswerA(String answerA) {
        this.answerA = answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public void setAnswerB(String answerB) {
        this.answerB = answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public void setAnswerC(String answerC) {
        this.answerC = answerC;
    }

    public String getAnswerD() {
        return answerD;
    }

    public void setAnswerD(String answerD) {
        this.answerD = answerD;
    }

    public String getAnswerE() {
        return answerE;
    }

    public void setAnswerE(String answerE) {
        this.answerE = answerE;
    }

    public String getAnswerT() {
        return answerT;
    }

    public void setAnswerT(String answerT) {
        this.answerT = answerT;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public String getAnswerF() {
        return answerF;
    }

    public void setAnswerF(String answerF) {
        this.answerF = answerF;
    }

    @Override
    public String toString() {
        return "Question [section=" + section + ", questionId=" + questionId + ",content=" + content + ",answerT="
                + answerT + "]";
    }
}
