package com.example.success.entity;


import com.fasterxml.jackson.dataformat.xml.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JacksonXmlRootElement(localName = "job")
public class Job {
    private String id;
    private String state;
    private String progress;


    @JacksonXmlProperty(isAttribute = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JacksonXmlElementWrapper
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @JacksonXmlElementWrapper
    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id='" + id + '\'' +
                ", state='" + state + '\'' +
                ", progress='" + progress + '\'' +
                '}';
    }
}
