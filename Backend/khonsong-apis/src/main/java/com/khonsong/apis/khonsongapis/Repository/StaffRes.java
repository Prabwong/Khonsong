package com.khonsong.apis.khonsongapis.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.khonsong.apis.khonsongapis.Staff;

public interface StaffRes extends JpaRepository<Staff, Integer> {

    Staff findByStaffID(Integer staffID);
}
