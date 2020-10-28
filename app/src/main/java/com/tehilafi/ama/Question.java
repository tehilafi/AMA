package com.tehilafi.ama;

public class Question {
    private int numQuestion;
    private int idAsking;
    private String question;
    private String location;

    public Question(){
    }

    public int getNumQuestion() {
        return numQuestion;
    }

    public int getIdAsking() {
        return idAsking;
    }

    public String getQuestion() {
        return question;
    }

    public String getLocation() {
        return location;
    }

    public void setNumQuestion(int numQuestion) {
        this.numQuestion = numQuestion;
    }

    public void setIdAsking(int idAsking) {
        this.idAsking = idAsking;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
