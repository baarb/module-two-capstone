package com.techelevator.park.model;

import java.math.BigDecimal;
import java.util.List;

public interface ReservationDAO {
	
	
	public List<Campsite> checkReservation(Long id, String arrivalDate, String departureDate);
	
	public BigDecimal costOfStay(String arrivalDate, String departureDate, BigDecimal price);

	public Long makeReservation(Long siteId, String name, String arrivalDate, String departureDate);

}
