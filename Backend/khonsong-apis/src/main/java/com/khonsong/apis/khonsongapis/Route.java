package com.khonsong.apis.khonsongapis;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Route")
public class Route {

    @Id
    public Integer routeID;

    // @JsonIgnore
    private Date arrivedTime;
    private Date receivedTime;
    private String staffName;
    private String receivedImage;
    private String checkpoint;
    private String routeStatus;

    public Route() {
    }

    public Route(Integer routeID, Date arrivedTime, Date receivedTime, 
    String staffName, String receivedImage,
    String checkpoint, String routeStatus) {
        this.routeID = routeID;
        this.arrivedTime = arrivedTime;
        this.receivedTime = receivedTime;
        this.staffName = staffName;
        this.receivedImage = receivedImage;
        this.checkpoint = checkpoint;
        this.routeStatus = routeStatus;
    }

    public Integer getRouteID() {
        return routeID;
    }

    public Date getArrivedTime() {
        return arrivedTime;
    }

    public Date getReceivedTime() {
        return receivedTime;
    }


    public String getReceivedImage() {
        return receivedImage;
    }

    public String getRouteStatus() {
        return routeStatus;
    }

    public void setRouteID(Integer routeID) {
        this.routeID = routeID;
    }

    public void setArrivedTime(Date arrivedTime) {
        this.arrivedTime = arrivedTime;
    }
    

    public void setReceivedTime(Date receivedTime) {
        this.receivedTime = receivedTime;
    }

    public void setReceivedImage(String receivedImage) {
        this.receivedImage = receivedImage;
    }

    public void setCheckpoint(String checkpoint) {
        this.checkpoint = checkpoint;
    }

    public void setRouteStatus(String routeStatus) {
        this.routeStatus = routeStatus;
    }

    public String getCheckpoint() {
        return checkpoint;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

}
