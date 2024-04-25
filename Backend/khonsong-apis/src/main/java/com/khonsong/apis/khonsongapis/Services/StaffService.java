package com.khonsong.apis.khonsongapis.Services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khonsong.apis.khonsongapis.Staff;
import com.khonsong.apis.khonsongapis.Repository.StaffRes;

@Service
public class StaffService {

    @Autowired
    StaffRes mySqlRepository;

    public StaffService(StaffRes mySqlRepository) {
        this.mySqlRepository = mySqlRepository;
    }

    public String getStaffNameById(Integer staffID) {
        ObjectMapper objectMapper = new ObjectMapper();
        Staff staff = mySqlRepository.findByStaffID(staffID);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", 200);
        if (staff != null) {
            responseData.put("data", staff);
        } else {
            responseData.put("error", "error cannot find the staff");
        }

        try {
            return objectMapper.writeValueAsString(responseData);
        } catch (JsonProcessingException e) {

            return "cannot convert to string for response due to" + e.getMessage();
        }

    }
}
