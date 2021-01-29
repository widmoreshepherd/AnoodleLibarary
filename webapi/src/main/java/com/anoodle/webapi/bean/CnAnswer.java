package com.anoodle.webapi.bean;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class CnAnswer {

    private String session_id;
    private String question_type;
    private String intention;
    private List<Map<String,String>> entities;
    private String answer;

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(String question_type) {
        this.question_type = question_type;
    }

    public String getIntention() {
        return intention;
    }

    public void setIntention(String intention) {
        this.intention = intention;
    }

    public List<Map<String,String>> getEntities() {
        return entities;
    }

    public void setEntities(List<Map<String,String>> entities) {
        this.entities = entities;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
