package com.tehilafi.ama.db;

public class Answer {

    private int numQuestion;
    private int numAnswer;
    private int idAnswering;
    private String contentAnswer;

    public Answer() {
    }

    public int getNumQuestion() {
        return numQuestion;
    }

    public int getNumAnswer() {
        return numAnswer;
    }

    public int getIdAnswering() {
        return idAnswering;
    }

    public String getContentAnswer() {
        return contentAnswer;
    }

    public void setNumQuestion(int numQuestion) {
        this.numQuestion = numQuestion;
    }

    public void setNumAnswer(int numAnswer) {
        this.numAnswer = numAnswer;
    }

    public void setIdAnswering(int idAnswering) {
        this.idAnswering = idAnswering;
    }

    public void setContentAnswer(String contentAnswer) {
        this.contentAnswer = contentAnswer;
    }
    
}

