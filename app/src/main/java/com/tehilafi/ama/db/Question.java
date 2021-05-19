package com.tehilafi.ama.db;

import java.util.ArrayList;

public class Question {
    private int numQuestion;
    private int idAsking;
    private String usernameAsk;
    private String contentQuestion;
    private String location;
    private String important_questions;
    private String latLngString;
    private ArrayList<String> send_to_tokens;
    private String dateTimeQuestion;
    private int numLikes;
    private int numComments;


    public Question(){
    }

    public String getDateTimeQuestion() {
        return dateTimeQuestion;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public int getNumComments() {
        return numComments;
    }

    public String getUsernameAsk() {
        return usernameAsk;
    }

    public void setUsernameAsk(String usernameAsk) {
        this.usernameAsk = usernameAsk;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public void setDateTimeQuestion(String dateTime) {
        this.dateTimeQuestion = dateTime;
    }

    public ArrayList<String> getSend_to_tokens() {
        return send_to_tokens;
    }

    public void setSend_to_tokens(ArrayList<String> send_to_tokens) {
        this.send_to_tokens = send_to_tokens;
    }

    public int getNumQuestion() {
        return numQuestion;
    }

    public int getIdAsking() {
        return idAsking;
    }

    public String getContentQuestion() {
        return contentQuestion;
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


    public void setContentQuestion(String contentQuestion) {
        this.contentQuestion = contentQuestion;
    }


    public void setLocation(String location) {
        this.location = location;
    }

    public void setImportant_questions(String important_questions) {
        this.important_questions = important_questions;
    }

    public String getLatLngString() {
        return latLngString;
    }

    public void setLatLngString(String latLngString) {
        this.latLngString = latLngString;
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
