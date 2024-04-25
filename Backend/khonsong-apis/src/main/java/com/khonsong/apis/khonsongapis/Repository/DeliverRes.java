package com.khonsong.apis.khonsongapis.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.khonsong.apis.khonsongapis.DeliverRoute;

public interface DeliverRes extends JpaRepository<DeliverRoute, Integer> {

    DeliverRoute findByDeliverRouteID(Integer deliverRouteID);

    // order time and get latest
    DeliverRoute findFirstByOrderByStartTimeDesc();

    DeliverRoute findFirstByOrderByDeliverRouteIDDesc();

    // get contain routeIDs
    @Query("SELECT dr.routeIDs FROM DeliverRoute dr WHERE dr.deliverRouteID = ?1")
    String findRouteIDsByDeliverRouteID(Integer deliverRouteID);
}