package com.techelevator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.park.model.Park;
import com.techelevator.park.model.jdbc.ParkJDBCDAO;

public class ParkJDBCDAOTest {
	
	private static SingleConnectionDataSource dataSource;
	private ParkJDBCDAO sut;
	private Long parkId;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/park");
		dataSource.setUsername("postgres");
		/* The following line disables autocommit for connections 
		 * returned by this DataSource. This allows us to rollback
		 * any changes after each test */
		dataSource.setAutoCommit(false);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dataSource.destroy();
	}

	@Before
	public void setUp() throws Exception {
		sut = new ParkJDBCDAO(dataSource);
		String estblString = "1999-10-10";
		LocalDate estblDate = LocalDate.parse(estblString);
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String newPark = "INSERT INTO park (name, location, establish_date, area, visitors, description) VALUES (?,?,?,?,?,?) RETURNING park_id";
		parkId = jdbcTemplate.queryForObject(newPark, Long.class, "TEST", "TEST LOCATION", estblDate, 100, 200, "TEST DESCRIPTION");
	}

	@After
	public void tearDown() throws Exception {
		dataSource.getConnection().rollback();
	}

	@Test
	public void testGetAllParks() {
		List<Park> allParks = sut.getAllParks();
		
		for(Park park : allParks) {
			if(park.getParkName().equals("TEST") && park.getParkId().equals(parkId)) {
				assertEquals(parkId, park.getParkId());
				assertEquals("TEST", park.getParkName());
				return;
			}
		}
		
		fail("No parks found.");
	}

}
