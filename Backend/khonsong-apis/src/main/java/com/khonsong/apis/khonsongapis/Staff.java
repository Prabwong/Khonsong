package com.khonsong.apis.khonsongapis;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer staffID;

    private String staffFname;
    private String staffLname;
    private String fingerprint;
    // @JsonIgnore
    private String staffPhoto;
    // This field will not be mapped to the database
    // @Transient
    // private String covertStaffPhoto;

    public Staff() {
    }

    public Staff(Integer staffID, String staffFname,
            String staffLname, String fingerprint, String covertStaffPhoto) {
        this.staffID = staffID;
        this.staffFname = staffFname;
        this.staffLname = staffLname;
        this.fingerprint = fingerprint;
        this.staffPhoto = staffPhoto;
    }

    public Integer getStaffID() {
        return staffID;
    }

    public String getStaffFname() {
        return staffFname;
    }

    public String getStaffLname() {
        return staffLname;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getStaffPhoto() {
        return staffPhoto;
    }

}
