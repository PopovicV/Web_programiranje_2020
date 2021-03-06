package services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import dao.AmenityDAO;
import dao.ApartmentDAO;
import dao.CommentDAO;
import dao.ReservationDAO;
import dao.UserDAO;
import dto.FilterReservationsDTO;
import dto.ReservationDTO;
import dto.SearchReservationsDTO;
import dto.UserDetailsDTO;
import enumeration.ApartmentType;
import enumeration.ReservationStatus;
import enumeration.UserGender;
import enumeration.UserRole;
import model.Apartment;
import model.Reservation;
import model.User;

@Path("Reservation")
public class ReservationService {

	@Context
	ServletContext context;
	
	public ReservationService() {
		
	}
	
	@PostConstruct
	public void init() {
		
		if (context.getAttribute("amenities") == null) {
			System.out.println("Inicijalizaovao amenities");
			context.setAttribute("amenities", new AmenityDAO(context.getRealPath("")));
		}
		
		if (context.getAttribute("apartments") == null) {
			context.setAttribute("apartments", new ApartmentDAO(context.getRealPath("")));
		}
		
		if (context.getAttribute("comments") == null) {
			context.setAttribute("comments", new CommentDAO(context.getRealPath("")));
		}
		
		if (context.getAttribute("reservations") == null) {
			context.setAttribute("reservations", new ReservationDAO(context.getRealPath("")));
		}
		
		if (context.getAttribute("users") == null) {
			context.setAttribute("users", new UserDAO(context.getRealPath("")));
		}
	
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<ReservationDTO> getAllReservations(@Context HttpServletRequest request) {
		
		ArrayList<ReservationDTO> reservationsToSend = new ArrayList<ReservationDTO>();
		
		ReservationDAO reservations = getReservations();
		UserDAO users = getUsers();
		ApartmentDAO apartments = getApartments();
		
		ArrayList<Reservation> reservationsList = new ArrayList<Reservation>(reservations.findAll());
		
		for (Reservation reservation : reservationsList) {
			Apartment apartment = apartments.find(reservation.getApartmentId());
			ReservationDTO dto = convertReservationToDTO(reservation, apartment, users.findById(apartment.getHostId()), users.findById(reservation.getGuestId()));
			reservationsToSend.add(dto);
		}
		return reservationsToSend;
	}

	@GET
	@Path("GuestReservations/{guestId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<ReservationDTO> getAllGuestReservations(@PathParam("guestId") String guestId, @Context HttpServletRequest request) {
		System.out.println("ENDPOINT POGODJEN, GUEST ID JE: " + guestId);
		ArrayList<ReservationDTO> reservationsToSend = new ArrayList<ReservationDTO>();
		ReservationDAO reservations = getReservations();
		UserDAO users = getUsers();
		ApartmentDAO apartments = getApartments();
		ArrayList<Reservation> reservationsByGuestId = reservations.findAllByGuestId(guestId);
		
		for (Reservation reservation : reservationsByGuestId) {
			Apartment apartment = apartments.find(reservation.getApartmentId());
			ReservationDTO dto = convertReservationToDTO(reservation, apartment, users.findById(apartment.getHostId()), users.findById(reservation.getGuestId()));
			reservationsToSend.add(dto);
		}
		
		return reservationsToSend;
	}
	
	@GET
	@Path("HostReservations/{hostId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<ReservationDTO> getAllHostReservations(@PathParam("hostId") String hostId, @Context HttpServletRequest request) {
		
		ArrayList<ReservationDTO> reservationsToSend = new ArrayList<ReservationDTO>();
		ReservationDAO reservations = getReservations();
		UserDAO users = getUsers();
		ApartmentDAO apartments = getApartments();
		ArrayList<Reservation> allReservations = new ArrayList<Reservation>(reservations.findAll());
		
		for (Reservation reservation : allReservations) {
			Apartment apartment = apartments.find(reservation.getApartmentId());
			User host = users.findById(apartment.getHostId());
			if (host.getId().equals(hostId) && reservation.isDeleted() == false) {
				ReservationDTO dto = convertReservationToDTO(reservation, apartment, host, users.findById(reservation.getGuestId()));
				reservationsToSend.add(dto);
			}
		}
		
		return reservationsToSend;
	}
	
	@GET
	@Path("UsersMadeReservations/{hostId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<UserDetailsDTO> getAllUsersThatMadeReservations(@PathParam("hostId") String hostId, @Context HttpServletRequest request) {
		
		ArrayList<UserDetailsDTO> usersToSend = new ArrayList<UserDetailsDTO>();
		ReservationDAO reservations = getReservations();
		UserDAO users = getUsers();
		ApartmentDAO apartments = getApartments();
		ArrayList<Reservation> allReservations = new ArrayList<Reservation>(reservations.findAll());
		
		for (Reservation reservation : allReservations) {
			if (!reservation.isDeleted()) {
				
				Apartment apartment = apartments.find(reservation.getApartmentId());
				User host = users.findById(apartment.getHostId());
				
				if (host.getId().equals(hostId)) {
					User guest = users.findById(reservation.getGuestId());
					boolean duplicate = false;
					for (UserDetailsDTO userDTO : usersToSend) {
						if (userDTO.getUserName().equals(guest.getUserName())) {
							duplicate = true;
						} else {
							continue;
						}
					}
					if(!duplicate) {
						UserDetailsDTO dto = convertUserToUserDetails(guest);
						usersToSend.add(dto);
					}
				}
				
				
			}
		}
		return usersToSend;
	}
	
	@POST
	@Path("registerReservation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void registerReservations(@Context HttpServletRequest request, Reservation reservation) {
		ReservationDAO reservations = getReservations();
		
		int reservationId = reservations.getReservations().size();
		reservation.setId(reservationId + "");
		
		ApartmentDAO apartments = getApartments();
		Apartment apartment = apartments.find(reservation.getApartmentId());
		
		System.out.println(reservation);
		
		apartment.getReservations().add(reservation.getId());
		reservation.setReservationStatus(ReservationStatus.CREATED);
		apartments.getApartments().put(apartment.getId(), apartment);
		saveApartments(apartments);
		
		reservations.getReservations().put(reservation.getId(), reservation);
		saveReservations(reservations);
	}
	
	@GET
	@Path("searchReservations/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ReservationDTO> findReservationByGuestUsername(@Context HttpServletRequest request, @PathParam("username") String username) {
		UserDAO users = getUsers();
		ApartmentDAO apartments = getApartments();
		
		ReservationDAO reservations = getReservations();

		List<ReservationDTO> ret = new ArrayList<ReservationDTO>();
		if(username.equals("-1")) {
			for(Map.Entry<String, Reservation> res : reservations.getReservations().entrySet()) {
				Apartment temp = apartments.find(res.getValue().getApartmentId());
				ret.add(convertReservationToDTO(res.getValue(), temp, users.findById(temp.getHostId()), users.findById(res.getValue().getGuestId())));
			}
			return ret;
		}
		
		List<Reservation> resses = reservations.findReservationsByUsername(username, users);
		System.out.println(resses); 
		
		for(Reservation res : resses) {
			System.out.println(res);
			Apartment temp = apartments.find(res.getApartmentId());
			ret.add(convertReservationToDTO(res, temp, users.findById(temp.getHostId()), users.findById(res.getGuestId())));
		}
		
		return ret;
	}
	
	@POST
	@Path("searchReservationsByHostAndGuestUsername")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ReservationDTO> searchReservationsByHostAndGuestUsername(@Context HttpServletRequest request, SearchReservationsDTO searchItems) {
		UserDAO users = getUsers();
		ApartmentDAO apartments = getApartments();
		ReservationDAO reservations = getReservations();
		List<ReservationDTO> ret = new ArrayList<ReservationDTO>();
		if(searchItems.getGuestUsername().equals("")) {
			for(Map.Entry<String, Reservation> res : reservations.getReservations().entrySet()) {
				Apartment temp = apartments.find(res.getValue().getApartmentId());
				if(temp.getHostId().equals(searchItems.getHostId())) {
					ret.add(convertReservationToDTO(res.getValue(), temp, users.findById(temp.getHostId()), users.findById(res.getValue().getGuestId())));
				}
			}
			return ret;
		}
		
		
		List<Reservation> resses = reservations.findReservationsByUsername(searchItems.getGuestUsername(), users);
		 
		for(Reservation res : resses) {
			Apartment temp = apartments.find(res.getApartmentId());
			if(temp.getHostId().equals(searchItems.getHostId())){
				ret.add(convertReservationToDTO(res, temp, users.findById(temp.getHostId()), users.findById(res.getGuestId())));
			}
		}
		
		return ret;
	}
	
	@GET
	@Path("filterReservationsByStatus/{status}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ReservationDTO> filterReservationsByStatus(@Context HttpServletRequest request, @PathParam("status") String status) {
		ReservationDAO reservations = getReservations();
		ApartmentDAO apartments = getApartments();
		UserDAO users = getUsers();
		ReservationStatus comp = ReservationStatus.ACCEPTED;
		List<ReservationDTO> ret = new ArrayList<ReservationDTO>();
		if(status.equals("0")) {
			comp = ReservationStatus.CREATED;
		}else if(status.equals("1")) {
			comp = ReservationStatus.DECLINED;
		}else if(status.equals("2")) {
			comp = ReservationStatus.ACCEPTED;
		}else if(status.equals("3")) {
			comp = ReservationStatus.FINISHED;
		}else if(status.equals("4")) {
			comp = ReservationStatus.CANCELED;
		}else {
			for(Map.Entry<String, Reservation> entry: reservations.getReservations().entrySet()) {
				Apartment temp = apartments.find(entry.getValue().getApartmentId());
				ret.add(convertReservationToDTO(entry.getValue(), temp, users.findById(temp.getHostId()), users.findById(entry.getValue().getGuestId())));
			}
			return ret;
		}
		
		for(Map.Entry<String, Reservation> entry: reservations.getReservations().entrySet()) {
			if(entry.getValue().getReservationStatus() == comp) {
				Apartment temp = apartments.find(entry.getValue().getApartmentId());
				ret.add(convertReservationToDTO(entry.getValue(), temp, users.findById(temp.getHostId()), users.findById(entry.getValue().getGuestId())));
			}
		}
		
		
		return ret;
	}
	
	@POST
	@Path("filterReservationsByStatusAndHostID")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ReservationDTO> filterReservationsByStatusAndHostID(@Context HttpServletRequest request, FilterReservationsDTO filter) {
		ReservationDAO reservations = getReservations();
		ApartmentDAO apartments = getApartments();
		UserDAO users = getUsers();
		ReservationStatus comp = ReservationStatus.ACCEPTED;
		List<ReservationDTO> ret = new ArrayList<ReservationDTO>();
		if(filter.getStatus().equals("0")) {
			comp = ReservationStatus.CREATED;
		}else if(filter.getStatus().equals("1")) {
			comp = ReservationStatus.DECLINED;
		}else if(filter.getStatus().equals("2")) {
			comp = ReservationStatus.ACCEPTED;
		}else if(filter.getStatus().equals("3")) {
			comp = ReservationStatus.FINISHED;
		}else if(filter.getStatus().equals("4")) {
			comp = ReservationStatus.CANCELED;
		}else {
			for(Map.Entry<String, Reservation> entry: reservations.getReservations().entrySet()) {
				Apartment temp = apartments.find(entry.getValue().getApartmentId());
				if(temp.getHostId().equals(filter.getHostId())) {
					ret.add(convertReservationToDTO(entry.getValue(), temp, users.findById(temp.getHostId()), users.findById(entry.getValue().getGuestId())));
				}
			}
			return ret;
		}
		
		for(Map.Entry<String, Reservation> entry: reservations.getReservations().entrySet()) {
			if(entry.getValue().getReservationStatus() == comp) {
				Apartment temp = apartments.find(entry.getValue().getApartmentId());
				if(temp.getHostId().equals(filter.getHostId())) {
					ret.add(convertReservationToDTO(entry.getValue(), temp, users.findById(temp.getHostId()), users.findById(entry.getValue().getGuestId())));
				}
			}
		}
		
		
		return ret;
	}
	
	@GET
	@Path("getActiveReservationsByApartment/{apartmentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Reservation> getActiveReservationsByApartment(@Context HttpServletRequest request, @PathParam("apartmentId") String apartmentId) {
		ReservationDAO reservations = getReservations();
		
		List<Reservation> ret = new ArrayList<Reservation>();
		for(Map.Entry<String, Reservation> entry : reservations.getReservations().entrySet()) {
			if(entry.getValue().getApartmentId().equals(apartmentId) && entry.getValue().getReservationStatus() == ReservationStatus.ACCEPTED) {
				System.out.println(entry.getValue());
				ret.add(entry.getValue());
			}
		}
		
		
		return ret;
	}
	
	@PUT
	@Path("cancelReservation/{reservationId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean cancelReservation(@Context HttpServletRequest request, @PathParam("reservationId") String reservationId) {
		ReservationDAO reservations = getReservations();
		
		Reservation reservation = reservations.findById(reservationId);
		if (reservation != null) {
			reservation.setReservationStatus(ReservationStatus.CANCELED);
			saveReservations(reservations);
			return true;
		} else {
			return false;
		}
	}
	
	@PUT
	@Path("acceptReservation/{reservationId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean acceptReservation(@Context HttpServletRequest request, @PathParam("reservationId") String reservationId) {
		ReservationDAO reservations = getReservations();
		
		Reservation reservation = reservations.findById(reservationId);
		if (reservation != null) {
			reservation.setReservationStatus(ReservationStatus.ACCEPTED);
			saveReservations(reservations);
			return true;
		} else {
			return false;
		}
	}
	
	@PUT
	@Path("declineReservation/{reservationId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean declineReservation(@Context HttpServletRequest request, @PathParam("reservationId") String reservationId) {
		ReservationDAO reservations = getReservations();
		
		Reservation reservation = reservations.findById(reservationId);
		if (reservation != null) {
			reservation.setReservationStatus(ReservationStatus.DECLINED);
			saveReservations(reservations);
			return true;
		} else {
			return false;
		}
	}
	
	@PUT
	@Path("finishReservation/{reservationId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean finishReservation(@Context HttpServletRequest request, @PathParam("reservationId") String reservationId) {
		ReservationDAO reservations = getReservations();
		
		Reservation reservation = reservations.findById(reservationId);
		if (reservation != null) {
			reservation.setReservationStatus(ReservationStatus.FINISHED);
			saveReservations(reservations);
			return true;
		} else {
			return false;
		}
	}
	
	public ReservationDAO getReservations() { 
		ReservationDAO reservations = (ReservationDAO) context.getAttribute("reservations");
		return reservations;
	}
	
	public UserDAO getUsers() {
		UserDAO users = (UserDAO) context.getAttribute("users");
		return users;
	}
	
	public ApartmentDAO getApartments() {
		ApartmentDAO apartments = (ApartmentDAO) context.getAttribute("apartments");
		return apartments;
	}
	
	public void saveReservations(ReservationDAO reservations) {
		reservations.saveReservations(context.getRealPath(""));
	}
	
	public UserDetailsDTO convertUserToUserDetails(User user) {
		UserDetailsDTO dto = new UserDetailsDTO();
		
		dto.setId(user.getId());
		dto.setName(user.getName());
		dto.setSurname(user.getSurname());
		dto.setUserName(user.getUserName());
		dto.setAvailableApartments(user.getAvailableApartments());
		dto.setNumberOfReservationsMade(user.getReservationsIds().size());
		dto.setRentedApartments(user.getRentedApartments());
		if (user.getUserGender().equals(UserGender.MALE)) {
			dto.setUserGender("MALE");
		} else {
			dto.setUserGender("FEMALE");
		}
		if (user.getUserRole().equals(UserRole.GUEST)) {
			dto.setUserRole("GUEST");
		} else {
			dto.setUserRole("HOST");
		}
		return dto;
		
	}
	
	public ReservationDTO convertReservationToDTO(Reservation reservation, Apartment apartment, User host, User guest) {
		
		ReservationDTO dto = new ReservationDTO();
		
		dto.setId(reservation.getId());
		dto.setApartmentType(convertApartmentType(apartment.getApartmentType()));
		dto.setNumberOfRooms(apartment.getNumberOfRooms());
		dto.setLocation(apartment.getLocation());
		dto.setHostUserName(host.getUserName());
		dto.setGuestUserName(guest.getUserName());
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy.");
		dto.setDate(df.format(reservation.getStartingDate()));
		dto.setStartingDate(reservation.getStartingDate());
		dto.setRentalDuration(reservation.getRentalDuration());
		dto.setFullPrice(reservation.getFullPrice());
		dto.setOptionalMessage(reservation.getOptionalMessage());
		dto.setReservationStatus(convertReservationStatus(reservation.getReservationStatus()));
		
		return dto;
	}
	
public String convertApartmentType(ApartmentType type) {
		
		String convertedType = "";
		
		if (type.equals(ApartmentType.APARTMENT)) {
			convertedType = "Apartment";
		} else if (type.equals(ApartmentType.ROOM)) {
			convertedType = "Room";
		} 
		
		return convertedType;
	}
	
	
	public String convertReservationStatus(ReservationStatus status) {
		
		String convertedStatus = "";
		
		if (status.equals(ReservationStatus.CREATED)) {
			convertedStatus = "Created";
		} else if (status.equals(ReservationStatus.CANCELED)) {
			convertedStatus = "Canceled";
		} else if (status.equals(ReservationStatus.DECLINED)) {
			convertedStatus = "Declined";
		} else if (status.equals(ReservationStatus.ACCEPTED)) {
			convertedStatus = "Accepted";
		} else {
			convertedStatus = "Finished";
		}
		
		return convertedStatus;
	}
	
	public void saveApartments(ApartmentDAO apartments) {
		apartments.saveApartments(context.getRealPath(""));
    }
}
