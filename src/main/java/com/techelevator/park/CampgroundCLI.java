package com.techelevator.park;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.techelevator.park.model.Campground;
import com.techelevator.park.model.CampgroundDAO;
import com.techelevator.park.model.Campsite;
import com.techelevator.park.model.Park;
import com.techelevator.park.model.ParkDAO;
import com.techelevator.park.model.ReservationDAO;
import com.techelevator.park.model.jdbc.CampgroundJDBCDAO;
import com.techelevator.park.model.jdbc.ParkJDBCDAO;
import com.techelevator.park.model.jdbc.ReservationJDBCDAO;
import com.techelevator.park.view.Menu;

public class CampgroundCLI {

	private static final String PARK_MENU_OPTION_VIEW = "View Campgrounds";
	private static final String PARK_MENU_OPTION_SEARCH = "Search for Reservation";
	private static final String PARK_MENU_OPTION_RETURN = "Return to Previous Screen";
	private static final String[] PARK_MENU_OPTIONS = new String[] { PARK_MENU_OPTION_VIEW, PARK_MENU_OPTION_SEARCH,
			PARK_MENU_OPTION_RETURN };

	private static final String CAMPGROUND_MENU_AVAILABLE_RESERVATION = "Search for Available Reservation";
	private static final String CAMPGROUND_MENU_PREVIOUS_SCREEN = "Return to Previous Screen";
	private static final String[] CAMPGROUND_MENU_OPTIONS = new String[] { CAMPGROUND_MENU_AVAILABLE_RESERVATION,
			CAMPGROUND_MENU_PREVIOUS_SCREEN };

	private Menu menu;

	private ParkDAO parkDAO;
	private CampgroundDAO campgroundDAO;
	private ReservationDAO reservationDAO;

	private final static int MAX_WIDTH = 100;

	public static void main(String[] args) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/park");
		dataSource.setUsername("postgres");

		CampgroundCLI application = new CampgroundCLI(dataSource);
		application.run();
	}

	public CampgroundCLI(DataSource datasource) {
		this.menu = new Menu(System.in, System.out);
		parkDAO = new ParkJDBCDAO(datasource);
		campgroundDAO = new CampgroundJDBCDAO(datasource);
		reservationDAO = new ReservationJDBCDAO(datasource);
	}

	// Called in main()
	public void run() {
		while (true) {
			printHeading("Select a Park for Further Details");
			List<Park> allParks = parkDAO.getAllParks();
			Object[] parkArray = allParks.toArray();
			
			Park choice = (Park) menu.getChoiceFromOptions(parkArray, "Q) Quit");
			handleParkSelection(choice);
		}

	}

	// Park Selection & return PARK INFORMATION SCREEN
	private void handleParkSelection(Park park) {
		while (true) {
			printHeading("Park Information");
			System.out.printf("%-10s%n", park.getParkName());
			System.out.printf("%-18s%-15s%n", "Location: ", park.getParkLocation());
			System.out.printf("%-18s%-15s%n", "Established: ", park.getEstblDate().toString());
			System.out.printf("%-18s%,-15d%n", "Area: ", park.getArea());
			System.out.printf("%-18s%,-15d%n", "Annual Visitors: ", park.getVisitors());
			System.out.println();
			System.out.println(wrap(park.getDescription()));


			String choice = (String) menu.getChoiceFromOptions(PARK_MENU_OPTIONS);
			if (choice.equals(PARK_MENU_OPTION_VIEW)) {
				handleListAllCampgrounds(park);
			} else if (choice.equals(PARK_MENU_OPTION_SEARCH)) {
				handleReservationSearch(park);
			} else if (choice.equals(PARK_MENU_OPTION_RETURN)) {
				break;

			}
		}
	}

	private void handleListAllCampgrounds(Park park) {
		printHeading("Park Campgrounds");

		List<Campground> allCampgrounds = campgroundDAO.getAllCampgrounds(park.getParkId());

		System.out.printf("%-3s%-38s%-15s%-15s%-15s%n", "  ", "Name", "Open", "Close", "Daily Fee");
		for (Campground camp : allCampgrounds) {
			System.out.printf("%-1s%-2d%-38s%-15s%-15s%1s%-15.2f%n", "#", camp.getCampgroundId(),
					camp.getCampgroundName(), camp.toMonth(camp.getOpenDate()), camp.toMonth(camp.getCloseDate()), "$",
					camp.getDailyFee());
		}

		while (true) {
			// search for available reservation
			String choice = (String) menu.getChoiceFromOptions(CAMPGROUND_MENU_OPTIONS);
			if (choice.equals(CAMPGROUND_MENU_AVAILABLE_RESERVATION)) {
				handleReservationSearch(park);
			}
			// previous screen option
			else if (choice.equals(CAMPGROUND_MENU_PREVIOUS_SCREEN)) {
				break;
			}
		}
	}

	// AVAILABLE RESERVATIONS -- CAMPGROUND
	private void handleReservationSearch(Park park) {
		printHeading("Search for Camground Reservation");

		List<Campground> allCampgrounds = campgroundDAO.getAllCampgrounds(park.getParkId());

		System.out.printf("%-3s%-38s%-15s%-15s%-15s%n", "  ", "Name", "Open", "Close", "Daily Fee");
		for (Campground camp : allCampgrounds) {
			System.out.printf("%-1s%-2d%-38s%-15s%-15s%1s%-15.2f%n", "#", camp.getCampgroundId(),
					camp.getCampgroundName(), camp.toMonth(camp.getOpenDate()), camp.toMonth(camp.getCloseDate()), "$",
					camp.getDailyFee());
		}
		

		// GET USER INPUT FOR RESERVATION QUERY

		System.out.println();
		String campgroundToReserve = getUserInput("Which campgound (enter 0 to cancel)? ");

		if (campgroundToReserve.equals("0")) {
			handleParkSelection(park);
		}

			Long selectedOption = Long.valueOf(campgroundToReserve);

			for (Campground camps : allCampgrounds) {
				if (camps.getCampgroundId() == selectedOption) {

					String arrivalDate = getUserInput("What is the arrival date (yyyy-mm-dd)? ");
					String departureDate = getUserInput("What is the departure date (yyyy-mm-dd)? ");

					if (isValidDate(arrivalDate, camps) && isValidDate(departureDate, camps)) {

						Long campId = Long.parseLong(campgroundToReserve);

						// Grab CampId and Camp's Daily Fee to calculate cost of stay
						Campground selectedCamp = campgroundDAO.getCampgroundById(campId);

						BigDecimal reservationCost = reservationDAO.costOfStay(arrivalDate, departureDate,
								selectedCamp.getDailyFee());

						List<Campsite> availableSites = reservationDAO.checkReservation(campId, arrivalDate,
								departureDate);

						// print heading FOR AVAILABLE CAMPSITES
						System.out.println();
						System.out.printf("%-15s%-12s%-15s%-15s%-15s%-15s%n", "Site No.", "Max Occup.", "Accessible?",
								"Max RV Length", "Utility", "Cost");

						for (Campsite site : availableSites) {
							System.out.printf("%-15d%-12d%-15s%-15s%-15s%1s%-15.2f%n", site.getSiteNumber(),
									site.getMaxOccupancy(), site.convertBooleanForAccess(site.isAccessible()),
									site.convertMaxRVLength(site.getMaxRvLength()),
									site.convertBooleanForUtil(site.isHasUtilities()), "$", reservationCost);
						}

						// USER INPUT TO MAKE RESERVATION -- site no. and name
						System.out.println();
						String campsiteToReserve = getUserInput("Which site should be reserved (enter 0 to cancel)? ");
						int siteInt = Integer.parseInt(campsiteToReserve);

						if (campsiteToReserve.equals("0")) {
							handleReservationSearch(park);
						}
						// CHECKS VALID SITE NO.
						for (Campsite site : availableSites) {
							if (site.getSiteId() == siteInt) {
								String reservationName = getUserInput(
										"What name should the reservation be made under? ");

								// parse user input from string to a long
								Long campsiteId = Long.parseLong(campsiteToReserve);

								Long reservationId = reservationDAO.makeReservation(campsiteId, reservationName,
										arrivalDate, departureDate);

								// SUCCESSFUL RESERVATION!
								System.out.println();
								System.out.println(
										"The reservation has been made and the confirmation id is: " + reservationId);
								String input = getUserInput("Would you like to make another reservation? (Yes/No) ");
									if(input.equalsIgnoreCase("No")) {
										System.out.println();
										System.out.println("Thank you for your reservation!");
										System.exit(0);
									} 								
								return;
							}
						}
						System.out.println();
						System.out.println("*** Please enter a valid site no. ***");
						
						

					} else {
						System.out.println();
						System.out.println("**** Please enter a valid date ****");
					}
					return;
				}

			System.out.println();
			System.out.println("**** Please enter a valid campground number ****");

		} 

	}


	// Called in run(), prints out Heading
	private void printHeading(String headingText) {
		System.out.println("\n" + headingText);
		for (int i = 0; i < headingText.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
	}

	// SCANNER
	private String getUserInput(String prompt) {
		System.out.print(prompt + " >>> ");
		return new Scanner(System.in).nextLine();
	}

	// CHECKS VALIDITY OF REQUESTED RESERVATION
	// parses user's date from string to a date and stores it in a date object
	// converts their Date into a local date using some java magic
	// grabs the month value and stores it as the int "month"
	// parses the open and close dates of the camp ground into integers so we can
	// compare them
	// return in "if" statement checks the date to make sure it's in the future
	public static boolean isValidDate(String inDate, Campground camp) {
		Date date;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(inDate);
		} catch (ParseException e) {
			return false;
		}
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int month = localDate.getMonthValue();

		int openMonth = Integer.parseInt(camp.getOpenDate());
		int closeMonth = Integer.parseInt(camp.getCloseDate());

		if (openMonth <= month && closeMonth >= month) {
			return new Date().before(date);
		} else {
			return false;
		}

	}

	// WRAPS PARK DESCRIPTION TEXT
	public String wrap(String longString) {
		String[] splittedString = longString.split(" ");
		String resultString = "";
		String lineString = "";

		for (int i = 0; i < splittedString.length; i++) {
			if (lineString.isEmpty()) {
				lineString += splittedString[i] + " ";
			} else if (lineString.length() + splittedString[i].length() < MAX_WIDTH) {
				lineString += splittedString[i] + " ";
			} else {
				resultString += lineString + "\n";
				lineString = "";
			}
		}

		if (!lineString.isEmpty()) {
			resultString += lineString + "\n";
		}

		return resultString;
	}

}
