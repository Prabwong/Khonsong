package com.khonsong.apis.khonsongapis;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.khonsong.apis.khonsongapis.Repository.DeliverRes;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "DeliverRoute")
public class DeliverRoute {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer deliverRouteID;

    // @JsonRawValue
    @Transient // persisted to the database
    private String[] checkpointsList;
    @Transient
    private String[] routesList;

    @JsonIgnore
    private String checkpoints;

    private String routeIDs;
    private Date startTime;
    private Date finishTime;
    private Integer issuedBy;
    private String deliverStatus;
    private Boolean restart;

    public DeliverRoute() {
    }

    public DeliverRoute(DeliverRes mySqlRespiratory, Integer deliverRouteID, String checkpoints,
            String[] checkpointsList,
            Date startTime,
            Date finishTime, Integer issuedBy,
            String routeIDs, String deliverStatus, String[] routesList, Boolean restart) {
        this.deliverRouteID = deliverRouteID;
        this.checkpoints = checkpoints;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.issuedBy = issuedBy;
        this.routeIDs = routeIDs;
        this.deliverStatus = deliverStatus;
        this.checkpointsList = checkpointsList;
        this.routesList = routesList;
        this.restart = restart;
    }

    public String getCheckpoints() {
        return checkpoints;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public Integer getIssuedBy() {
        return issuedBy;
    }

    public String getRouteIDs() {
        return routeIDs;
    }

    public String getDeliverStatus() {
        return deliverStatus;
    }

    public void setCheckpoints(String checkpoints) {
        this.checkpoints = checkpoints;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public void setIssuedBy(Integer issuedBy) {
        this.issuedBy = issuedBy;
    }

    public void setRouteIDs(String routeIDs) {
        this.routeIDs = routeIDs;
    }

    public void setDeliverStatus(String deliverStatus) {
        this.deliverStatus = deliverStatus;
    }

    public void setDeliverRouteID(Integer deliverRouteID) {
        this.deliverRouteID = deliverRouteID;
    }

    public String[] getCheckpointsList() {
        return checkpointsList;
    }

    public void setCheckpointsList(String[] checkpointsList) {
        this.checkpointsList = checkpointsList;
    }

    public Integer getDeliverRouteID() {
        return deliverRouteID;
    }

    public Boolean getRestart() {
        return restart;
    }

    public void setRestart(Boolean restart) {
        this.restart = restart;
    }

    

}
