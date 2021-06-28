package com.tehilafi.ama.db;

public class Answer {

    private int numQuestion;
    private int numAnswer;
    private int idAnswering;
    private int idAsking;
    private String contentAnswer;
    private String dateTimeAnswer;
    private String userNameAns;
    private int numLikes;
    private boolean newAnswer;

    public Answer() {
    }

    public String getUserNameAns() {
        return userNameAns;
    }

    public void setUserNameAns(String userNameAns) {
        this.userNameAns = userNameAns;
    }

    public String getDateTimeAnswer() {
        return dateTimeAnswer;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public void setDateTimeAnswer(String dateTimeAnswer) {
        this.dateTimeAnswer = dateTimeAnswer;
    }

    public int getIdAsking() {
        return idAsking;
    }

    public void setIdAsking(int idAsking) {
        this.idAsking = idAsking;
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

    public boolean isNewAnswer() {
        return newAnswer;
    }

    public void setNewAnswer(boolean newAnswer) {
        this.newAnswer = newAnswer;
    }
}

