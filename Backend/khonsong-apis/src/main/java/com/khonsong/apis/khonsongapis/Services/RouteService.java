package com.khonsong.apis.khonsongapis.Services;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.khonsong.apis.khonsongapis.DeliverRoute;
import com.khonsong.apis.khonsongapis.Payload;
import com.khonsong.apis.khonsongapis.Route;
import com.khonsong.apis.khonsongapis.Staff;
import com.khonsong.apis.khonsongapis.Model.ArduinoPublish;
import com.khonsong.apis.khonsongapis.Repository.DeliverRes;
import com.khonsong.apis.khonsongapis.Repository.RouteRes;
import com.khonsong.apis.khonsongapis.Repository.StaffRes;

@Service
public class RouteService {
    @Autowired
    RouteRes routeRepository;
    @Autowired
    StaffRes staffRes;
    @Autowired
    DeliverRes deliverRes;
    DeliverRouteService deliverService;

    public RouteService(RouteRes mySqlRepository,
            DeliverRouteService deliverService, StaffRes staffRes) {
        this.routeRepository = mySqlRepository;
        this.deliverService = deliverService;
        this.staffRes = staffRes;
    }

    private Integer generateRouteID() {
        Route route = routeRepository.findFirstByOrderByRouteIDDesc();
        if (route == null) {
            return 100;
        } else {
            return route.getRouteID() + 1;
        }
    }

    // ! for arduino
    // * Create route */
    public String create(ArduinoPublish data) {
        boolean create = false;
        String response = "";
        LocalDateTime currentDateTime = LocalDateTime.now();
        Instant instant = currentDateTime.atZone(ZoneId.systemDefault()).toInstant();
        // Convert Instant to Date
        Date date = Date.from(instant);
        if (data != null) {
            // check whether it is resend or not
            DeliverRoute deliver = deliverRes.findFirstByOrderByDeliverRouteIDDesc();
            System.out.println(deliver.getRouteIDs());
            if (!(deliver.getRouteIDs() == null)) {
                Integer numberCheckpoints = deliver.getRouteIDs().split(",").length;
                if (numberCheckpoints < data.getNumberCheckpoint()) {
                    create = true;
                }
            } else {
                create = true;
            }
            if (create) {
                // set one var to triger create
                try {
                    Route route = new Route();
                    route.setCheckpoint(data.getCheckpoint());
                    route.setRouteID(generateRouteID());
                    route.setRouteStatus("incomplete");
                    route.setArrivedTime(date);
                    routeRepository.save(route);

                    // * update route to deliver route */
                    Integer routeID = route.getRouteID();
                    deliverService.updateRouteIDs(route);
                    response += routeID;
                    // * send finish status to arduino */
                } catch (DataAccessException e) {
                    response += "no";
                }
            } else {
                response += routeRepository.findFirstByOrderByRouteIDDesc().getRouteID();
            }
        } else {
            response += "no";
        }

        // * send payload to arduino: routeID*/
        return response;
    }

    public Map<String, Object> getRoutesforDeliver(List<Integer> routeIDs) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status", 200);
        if (routeIDs.size() == 0) {
            responseData.put("error", "no input routes!");
        } else {
            List<Route> routes = routeRepository.findByRouteIDIn(routeIDs);
            responseData.put("data", routes);
        }
        return responseData;
    }

    public List<Route> getUpdateRoutes(List<Integer> routeIDs) {
        List<Route> routes = routeRepository.findByRouteIDIn(routeIDs);
        return routes;
    }

    // ! for arduino
    // * update route */
    public String updateRoute(Route route) {
        String response = "";
        if (route == null) {
            response += "no";
        } else {
            System.out.println(route.getRouteID());
            // check whether resend or not
            Route updateRoute = routeRepository.findByRouteID(route.getRouteID());
            if (updateRoute.getReceivedTime() == null) {
                String status = updateRoute.getRouteStatus();
                if (status.equals("incomplete")) {
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    Instant instant = currentDateTime.atZone(ZoneId.systemDefault()).toInstant();
                    // Convert Instant to Date
                    Date date = Date.from(instant);
                    updateRoute.setReceivedTime(date);
                    updateRoute.setStaffName(route.getStaffName());
                    updateRoute.setRouteStatus("complete");
                    String processStatus = deliverService.checkLastCheckpoint(updateRoute);
                    response += processStatus;
                    routeRepository.save(updateRoute);
                } else {
                    response += "no";
                }
            } else {
                String processStatus = deliverService.checkLastCheckpoint(updateRoute);
                response += processStatus;
            }
        }
        return response;
    }

    public String getStaffName(Integer staffID) {
        Staff staff = staffRes.findByStaffID(staffID);
        String staffName = staff.getStaffFname() + " " + staff.getStaffLname();
        return staffName;
    }

    // * Get latest route to update */
    public void getLatestRoute(Route inputRoute) {
        Route route = routeRepository.findByRouteID(inputRoute.getRouteID());
        route.setReceivedImage(inputRoute.getReceivedImage());
        routeRepository.save(route);
    }
}
