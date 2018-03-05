package com.techelevator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.park.model.Campsite;
import com.techelevator.park.model.jdbc.CampsiteJDBCDAO;
import com.techelevator.park.model.jdbc.ReservationJDBCDAO;

public class ReservationJDBCDAOTest {
	
	private static SingleConnectionDataSource dataSource;
	private ReservationJDBCDAO sut;
	private Long confirmationNumber;
	private Long siteId;
	private Long campgroundId;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/park");
		dataSource.setUsername("postgres");
		
		dataSource.setAutoCommit(false);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dataSource.destroy();
	}

	@Before
	public void setUp() throws Exception {
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		campgroundId = 6L; //// WHY NOT 2?????
		
		String makeNewCampsite = "INSERT INTO site (campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities) "
				+ "VAlUES (?,?,?,?,?,?) RETURNING site_id";
		siteId = jdbcTemplate.queryForObject(makeNewCampsite, Long.class, campgroundId, 2, 6, true, 20, true);
		
		
		sut = new ReservationJDBCDAO(dataSource);
		
		Date startDate = Date.valueOf("2018-02-20");
		Date endDate = Date.valueOf("2018-02-24");
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime dateTime = LocalDateTime.now();
		String formattedDate = dateTime.format(formatter);
		Date createDate = Date.valueOf(formattedDate);
		
		
		String newReservation = "INSERT INTO reservation (site_id, name, from_date, to_date, create_date) "
				+ "VALUES (?,?,?,?,?) RETURNING reservation_id";
		confirmationNumber = jdbcTemplate.queryForObject(newReservation, Long.class, 1, "Joe", startDate, endDate, createDate);
	}

	@After
	public void tearDown() throws Exception {
		dataSource.getConnection().rollback();
	}
	
	
	protected DataSource getDataSource() {
		return dataSource;
	}
	

	@Test
	public void testCheckReservation() {
		
//		CampsiteJDBCDAO sites = new CampsiteJDBCDAO(dataSource);
		
		List<Campsite> availableSites = sut.checkReservation(campgroundId, "2018-07-15", "2018-07-19");
		
		for(Campsite camp : availableSites) {
			if(camp.getSiteId().equals(siteId) && camp.getCampgroundId().equals(campgroundId)) {
				assertEquals(siteId, camp.getSiteId());
				
				return;
			}
		}
		
		fail("No campsites found.");
		
	}

	@Test
	public void testCostOfStay() {
		BigDecimal cost = sut.costOfStay("2018-02-20", "2018-02-24", new BigDecimal("25.00"));
		assertEquals(new BigDecimal("100.00"), cost);
	}

	@Test
	public void testMakeReservation() {
	
		Long testRes = sut.makeReservation(1L, "Camper", "2018-02-15", "2018-02-19");
		
		String sqlCheckReservation = "SELECT reservation_id FROM reservation WHERE name = ? LIMIT 1";
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		Long newResId = jdbcTemplate.queryForObject(sqlCheckReservation, Long.class, "Camper");
		
		assertEquals(newResId, testRes);
	}
}
