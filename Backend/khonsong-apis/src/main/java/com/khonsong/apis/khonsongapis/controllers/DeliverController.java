package com.khonsong.apis.khonsongapis.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.iot.client.AWSIotException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khonsong.apis.khonsongapis.DeliverRoute;
import com.khonsong.apis.khonsongapis.Payload;
import com.khonsong.apis.khonsongapis.Services.DeliverRouteService;

@RestController
@CrossOrigin
@RequestMapping("/deliverRoute")
public class DeliverController {

    private DeliverRouteService deliverRouteService;

    public DeliverController(DeliverRouteService deliverRouteService) {
        this.deliverRouteService = deliverRouteService;
    }

    // * Get Deliver Route info */
    @GetMapping("/info")
    public Map<String, Object> getMethodName(@RequestParam Integer deliverRouteID) {
        return deliverRouteService.getDeliverInfo(deliverRouteID);
    }

    // * create Deliver Route */
    @PostMapping("/create")
    public String createDeliverRoute(@RequestBody DeliverRoute deliverRoute) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonArray = objectMapper.writeValueAsString(deliverRoute.getCheckpointsList());
            deliverRoute.setCheckpoints(jsonArray);

            // * Send message start and checkpoints to arduino */
            Payload payload = new Payload();
            payload.setCheckpoints(deliverRoute.getCheckpointsList());
            payload.setStatus("start");
            // try {
            // mqttController.publishMessage(payload);
            // } catch (AWSIotException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return deliverRouteService.create(deliverRoute);
    }

    // * Get all Deliver Route */
    @GetMapping("/allDeliver")
    public Map<String, Object> getAllDeliver() {
        List<DeliverRoute> deliverList = deliverRouteService.getAllDeliverInfo();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status", 200);
        for (DeliverRoute deliverRoute : deliverList) {
            try {
                // map the checkpoints form json to array
                JsonNode jsonNode = objectMapper.readTree(deliverRoute.getCheckpoints());
                List<String> checkpointsList = new ArrayList<>();
                for (JsonNode node : jsonNode) {
                    checkpointsList.add(node.asText());
                }
                deliverRoute.setCheckpointsList(checkpointsList.toArray(new String[0]));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                responseData.put("error", "error mapping list " + e.getMessage());
                return responseData;
            }
        }

        responseData.put("data", deliverList);
        return responseData;
    }

    // * For update real-time Deliver route */
    @GetMapping("/currentDeliver")
    public Map<String, Object> currentDeliver(@RequestParam Integer deliverRouteID) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status", 200);
        // method to get update data
        Map<String, Object> data = deliverRouteService.getDeliverUpdateData(deliverRouteID);
        responseData.put("data", data);
        return responseData;
    }

    // * Get restart status */
    @GetMapping("/restart")
    public Map<String, Object> getRestart(@RequestParam Integer deliverRouteID) {
        // call deliverRouteService
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status", 200);
        responseData.put("data", deliverRouteService.checkRestart());
        return responseData;
    }

    

}
