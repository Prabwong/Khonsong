package com.khonsong.apis.khonsongapis.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.khonsong.apis.khonsongapis.Route;

public interface RouteRes extends JpaRepository<Route, Integer> {

    // single route info
    Route findByRouteID(Integer routeID);

    // multiple routes info
    List<Route> findByRouteIDIn(List<Integer> routeIDs);

    // get latest routeID
    Route findFirstByOrderByRouteIDDesc();
}