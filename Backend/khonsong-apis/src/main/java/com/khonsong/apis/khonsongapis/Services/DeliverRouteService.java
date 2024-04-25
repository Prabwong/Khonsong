package com.khonsong.apis.khonsongapis.Services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khonsong.apis.khonsongapis.DeliverRoute;
import com.khonsong.apis.khonsongapis.Payload;
import com.khonsong.apis.khonsongapis.Route;
import com.khonsong.apis.khonsongapis.Repository.DeliverRes;
import com.khonsong.apis.khonsongapis.Repository.RouteRes;

@Service
public class DeliverRouteService {
    @Autowired
    DeliverRes deliverRepository;
    RouteRes routeRes;

    public DeliverRouteService(DeliverRes mySqlRepository, RouteRes routeRes) {
        this.deliverRepository = mySqlRepository;
        this.routeRes = routeRes;
    }

    // * Get one deliver info */
    public Map<String, Object> getDeliverInfo(Integer deliverRouteID) {
        DeliverRoute route = deliverRepository.findByDeliverRouteID(deliverRouteID);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status", 200);
        if (route != null) {
            try {
                String[] checkpoints = route.getCheckpoints()
                        .replaceAll("[^a-zA-Z0-9,]", "").split(",");
                route.setCheckpointsList(checkpoints);
                responseData.put("data", route);
            } catch (DataAccessException e) {
                responseData.put("error", "Error creating delivery route: " + e.getMessage());
            }
        } else {
            responseData.put("error", "error cannot find the deliver route");
        }

        return responseData;
    }

    // * Get one deliver info not in response form */
    public DeliverRoute updateDeliverInfo(Integer deliverRouteID) {
        DeliverRoute route = deliverRepository.findByDeliverRouteID(deliverRouteID);
        if (route != null) {
            try {
                String[] checkpoints = route.getCheckpoints()
                        .replaceAll("[^a-zA-Z0-9,]", "").split(",");
                route.setCheckpointsList(checkpoints);
                return route;
            } catch (DataAccessException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    // * Generate deliver route ID when is created */
    private Integer generateDeliverRouteID() {
        DeliverRoute route = deliverRepository.findFirstByOrderByDeliverRouteIDDesc();
        if (route == null) {
            return 1;
        } else {
            return route.getDeliverRouteID() + 1;
        }
    }

    // * create deliver route*/
    public String create(DeliverRoute deliverRoute) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status", 200);
        if (deliverRoute != null) {
            if (deliverRoute.getIssuedBy() != null && deliverRoute.getCheckpointsList() != null
                    && deliverRoute.getStartTime() != null) {
                try {
                    // assign deliver route ID
                    Integer assignedID = generateDeliverRouteID();
                    deliverRoute.setDeliverRouteID(assignedID);
                    deliverRoute.setDeliverStatus("incomplete");
                    deliverRoute.setRestart(false);
                    deliverRepository.save(deliverRoute);
                    // Create a nested Map for the data field
                    Map<String, Object> data = new HashMap<>();
                    data.put("deliverRouteID", assignedID);
                    responseData.put("data", data);
                } catch (DataAccessException e) {
                    responseData.put("error", "Error saving delivery route: " + e.getMessage());

                }
            } else {
                responseData.put("error", "Insufficient input data");
            }
        } else {
            responseData.put("error", "No input for Delivery Route");
        }

        try {
            return objectMapper.writeValueAsString(responseData);
        } catch (JsonProcessingException e) {

            return "cannot convert to string for response due to" + e.getMessage();
        }
    }

    // * Get all deliver route */
    public List<DeliverRoute> getAllDeliverInfo() {
        return deliverRepository.findAll();
    }

    public void updateRouteIDs(Route route) {
        DeliverRoute deliver = deliverRepository.findFirstByOrderByDeliverRouteIDDesc();
        String routeIDs = deliver.getRouteIDs();
        if (routeIDs != null) {
            routeIDs = routeIDs + "," + route.getRouteID().toString();
        } else {
            routeIDs = route.getRouteID().toString();
        }
        deliver.setRouteIDs(routeIDs);
        deliverRepository.save(deliver);
    }

    // * Check for deliver status */
    public String checkLastCheckpoint(Route route) {
        DeliverRoute deliver = deliverRepository.findFirstByOrderByDeliverRouteIDDesc();
        // check if the last checkpoint
        String checkpoints = deliver.getCheckpoints();
        String cleanedString = checkpoints.replaceAll("[^a-zA-Z0-9]", "");
        String lastCheckpoint = cleanedString.substring(cleanedString.length() - 1);
        // System.out.println("lastCheckpoint");
        // System.out.println(lastCheckpoint);
        // System.out.println("currCheckpoint");
        // System.out.println(route.getCheckpoint());
        if (route.getCheckpoint().equals(lastCheckpoint)) {
            deliver.setFinishTime(route.getReceivedTime());
            deliver.setDeliverStatus("complete");
            deliverRepository.save(deliver);
            return "finish";
        } else {
            return "start";
        }

    }

    // * Get Deliver data and routes data */
    public Map<String, Object> getDeliverUpdateData(Integer deliverRouteID) {
        Map<String, Object> responseData = new HashMap<>();
        // find the routes of deliverRouteID
        DeliverRoute deliverRoute = deliverRepository.findByDeliverRouteID(deliverRouteID);
        String routes = deliverRepository.findRouteIDsByDeliverRouteID(deliverRoute.deliverRouteID);
        List<Integer> routeIdsList = Arrays.stream(routes.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        DeliverRoute deliverData = updateDeliverInfo(deliverRoute.deliverRouteID);
        if (deliverData != null) {
            responseData.put("deliverData", deliverData);
            List<Route> routesData = routeRes.findByRouteIDIn(routeIdsList);
            responseData.put("routesData", routesData);
        } else {
            responseData.put("error", "cannot find Deliver Data!");
        }
        return responseData;
    }

    // ! for arduino
    // update restart status of deliver route
    public Payload updateRestart() {
        Payload payload = new Payload();
        DeliverRoute deliver = deliverRepository.findFirstByOrderByStartTimeDesc();
        deliver.setRestart(true);
        payload.setStatus("finish");
        deliverRepository.save(deliver);
        return payload;
    }

    // check restart
    public Map<String, Object> checkRestart() {
        DeliverRoute deliverRoute = deliverRepository.findFirstByOrderByStartTimeDesc();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("restart", deliverRoute.getRestart());
        return responseData;
    }

    // ! Get Input for Arduino */
    public String checkInput() {
        String response = "";
        DeliverRoute deliver = deliverRepository.findFirstByOrderByDeliverRouteIDDesc();
        if (deliver.getDeliverStatus().equals("complete")) {
            response += "no";
        } else {
            String checkpoints = deliver.getCheckpoints();
            checkpoints = checkpoints.replaceAll("[^a-zA-Z0-9]", "");
            response += "start";
            response += ",";
            response += checkpoints;
        }

        return response;
    }

    public String setRestart(Boolean status) {
        String response = "";
        DeliverRoute deliver = deliverRepository.findFirstByOrderByDeliverRouteIDDesc();
        if (deliver != null) {
            deliver.setRestart(status);
            deliverRepository.save(deliver);
            response += "restart";
        } else {
            response += "no";
        }
        return response;
    }

}
