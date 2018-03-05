package com.techelevator.park.model.jdbc;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.park.model.Campsite;
import com.techelevator.park.model.Reservation;
import com.techelevator.park.model.ReservationDAO;

public class ReservationJDBCDAO implements ReservationDAO {
	
	private JdbcTemplate jdbcTemplate;
	
	public ReservationJDBCDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	@Override
	public List<Campsite> checkReservation(Long CampgroundId, String arrivalDate, String departureDate) {
		
		Date startDate = Date.valueOf(arrivalDate);
		Date endDate = Date.valueOf(departureDate);
		
		ArrayList<Campsite> AvailableSites = new ArrayList<>();
		String sqlCheckAvailableSites = "SELECT * FROM site WHERE site_id NOT IN ("
				+ "SELECT site_id FROM reservation WHERE "
				+ "? NOT BETWEEN from_date AND to_date"
				+ " OR ? NOT BETWEEN from_date AND to_date"
				+ " OR ? BETWEEN from_date AND to_date"
				+ " OR ? BETWEEN from_date AND to_date)"
				+ "	AND campground_id =?"
				+ " ORDER BY site_id"
				+ " LIMIT 5";
						
		SqlRowSet checkAvailable = jdbcTemplate.queryForRowSet(sqlCheckAvailableSites, startDate, endDate, startDate, endDate, CampgroundId);
		
		while(checkAvailable.next()) {
			Campsite theCampsite = mapRowToCampsite(checkAvailable);
			AvailableSites.add(theCampsite);
		}
		
		return AvailableSites;
	}

	
	// GENERATE COST METHOD
	public BigDecimal costOfStay(String arrivalDate, String departureDate, BigDecimal price) {
		Date startDate = Date.valueOf(arrivalDate);
		Date endDate = Date.valueOf(departureDate);
		long diff = endDate.getTime() - startDate.getTime();
		int numDays = (int) (diff / (1000*60*60*24));
		
		BigDecimal total = generateCost(numDays, price);
		
		return total;
		
	}
	
	private BigDecimal generateCost(int days, BigDecimal price) {
		BigDecimal total = new BigDecimal("0.00");
		BigDecimal daysBD = BigDecimal.valueOf(days);
		
		total = daysBD.multiply(price);
		
		return total;
	}
	
	@Override
	public Long makeReservation(Long siteId, String name, String arrivalDate, String departureDate) {
		
		Date startDate = Date.valueOf(arrivalDate);
		Date endDate = Date.valueOf(departureDate);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime dateTime = LocalDateTime.now();
		String formattedDate = dateTime.format(formatter);
		Date createDate = Date.valueOf(formattedDate);
		
		String sqlMakeReservation = "INSERT INTO reservation (site_id, name, from_date, to_date, create_date) VALUES (?,?,?,?,?) RETURNING reservation_id";
		Long newResId = jdbcTemplate.queryForObject(sqlMakeReservation, Long.class, siteId, name, startDate, endDate, createDate);
		
		return newResId;
	}	
	
	private Campsite mapRowToCampsite(SqlRowSet results) {
		Campsite theCampsite = new Campsite();
		theCampsite.setCampgroundId(results.getLong("campground_id"));
		theCampsite.setSiteId(results.getLong("site_id"));
		theCampsite.setSiteNumber(results.getInt("site_number"));
		theCampsite.setMaxOccupancy(results.getInt("max_occupancy"));
		theCampsite.setAccessible(results.getBoolean("accessible"));
		theCampsite.setHasUtilities(results.getBoolean("utilities"));
		theCampsite.setMaxRvLength(results.getInt("max_rv_length"));
		
		return theCampsite;
	}

}
