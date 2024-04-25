package com.khonsong.apis.khonsongapis.Model;

public class ArduinoPublish {
    private String checkpoint;
    private Integer numberCheckpoint;
    
    public ArduinoPublish(String checkpoint, Integer numberCheckpoint) {
        this.checkpoint = checkpoint;
        this.numberCheckpoint = numberCheckpoint;
    }
    public String getCheckpoint() {
        return checkpoint;
    }
    public void setCheckpoint(String checkpoint) {
        this.checkpoint = checkpoint;
    }
    public Integer getNumberCheckpoint() {
        return numberCheckpoint;
    }
    public void setNumberCheckpoint(Integer numberCheckpoint) {
        this.numberCheckpoint = numberCheckpoint;
    }
    
}
