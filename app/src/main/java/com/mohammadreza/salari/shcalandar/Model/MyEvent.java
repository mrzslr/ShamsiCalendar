package com.mohammadreza.salari.shcalandar.Model;





public class MyEvent {
    private

    String id;
    private

    String summary;
    private

    String location;
    private

    String description;
    private

    String start;
    private

    String end;

    public MyEvent() {

    }

    public MyEvent(String id, String summary, String description, String location, String start, String end) {
        this.id = id;
        this.summary = summary;
        this.location = location;
        this.description = description;
        this.start = start;
        this.end = end;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }


}
