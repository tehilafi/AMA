package com.tehilafi.ama;

public class Question {
    private int numQuestion;
    private int idAsking;
    private String titleQuestion;
    private String contentQuestion;
    private Boolean ugentQuestion;
    private String location;

    public Question(){
    }

    public int getNumQuestion() {
        return numQuestion;
    }

    public int getIdAsking() {
        return idAsking;
    }

    public String getTitleQuestion() {
        return titleQuestion;
    }

    public String getContentQuestion() {
        return contentQuestion;
    }

    public Boolean getUgentQuestion() {
        return ugentQuestion;
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

    public void setTitleQuestion(String titleQuestion) {
        this.titleQuestion = titleQuestion;
    }

    public void setContentQuestion(String contentQuestion) {
        this.contentQuestion = contentQuestion;
    }

    public void setUgentQuestion(Boolean ugentQuestion) {
        this.ugentQuestion = ugentQuestion;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
