package com.khonsong.apis.khonsongapis;

public class Payload {
    private String status;
    private String[] checkpoints;
    private Integer routeID;
    private boolean error;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(String[] checkpoints) {
        this.checkpoints = checkpoints;
    }

    public Integer getRouteID() {
        return routeID;
    }

    public void setRouteID(Integer routeID) {
        this.routeID = routeID;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    

}
