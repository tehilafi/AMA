package com.tehilafi.ama.db;

public class Question {
    private int numQuestion;
    private int idAsking;
    private String titleQuestion;
    private String contentQuestion;
    private Boolean ugentQuestion;
    private String location;
    private String important_questions;

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

    public String getImportant_questions() {
        return important_questions;
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

    public void setImportant_questions(String important_questions) {
        this.important_questions = important_questions;
    }


    public String title() {
        return  this.titleQuestion;
    }
    public String location() {
        return this.location;
    }
    public String content() {
        return this.contentQuestion;
    }
    public String numQuestion() {
        return String.valueOf(this.numQuestion);
    }
    public String id_user() {
        return String.valueOf(this.idAsking);
    }
    public String important_questions() {
        return this.important_questions;
    }


}
