package services;

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
import dto.ChangePasswordDTO;
import dto.ChangeUserDTO;
import dto.UserDetailsDTO;
import dto.UsersFilterDTO;
import enumeration.UserGender;
import enumeration.UserRole;
import dto.SearchUserDTO;
import model.User;

@Path("User")
public class UserService {

	@Context
	ServletContext context;
	
	public UserService() {
		
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
	
	@PUT
	@Path("blockUser/{id}")
	public String changePassword(@PathParam("id") String id, @Context HttpServletRequest request) {
		
		UserDAO users = getUsers();
		User currentUser = users.findById(id);
		currentUser.setBlocked(true);
		saveUsers(users);
		
		return "Korisnik uspesno blokiran!";
	}
	
	@PUT
	@Path("changePassword")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean changePassword(ChangePasswordDTO userInfo, @Context HttpServletRequest request) {
		System.out.println(userInfo.toString());
		UserDAO users = getUsers();
		User currentUser = users.findByUsernameAndPassword(userInfo.getUserName(), userInfo.getPassword());
		if (currentUser != null) {
			System.out.println("Nasao usera!");
			currentUser.setPassword(userInfo.getNewPassword());
			saveUsers(users);
			return true;
		} else {
			return false;
		}
		
	}
	
	@PUT
	@Path("changeUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean changeUser(ChangeUserDTO userDTO, @Context HttpServletRequest request) {
		
		UserDAO users = getUsers();
		
		User currentUser = users.findByUsername(userDTO.getPreviousUserName());
		
		User checkForNewUserName = users.findByUsername(userDTO.getNewUserName());
		
		if (checkForNewUserName == null) {
			
			currentUser.setName(userDTO.getName());
			if (!userDTO.getPreviousUserName().equals(userDTO.getNewUserName())) {
				currentUser.setUserName(userDTO.getNewUserName());
			}
			currentUser.setSurname(userDTO.getSurname());
			saveUsers(users);
			
			return true;
		} else {
			return false;
		}
	}
	
	@GET
	@Path("getUsers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<User> getAllUsers(@Context HttpServletRequest request) {
		
		UserDAO users = getUsers();
		ArrayList<User> usersToSend = new ArrayList<User>(users.findAll());
		return usersToSend;
	}
	
	@POST
	@Path("FilterUsers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<User> getFilteredUsers(@Context HttpServletRequest request, UsersFilterDTO dto) {
		
		UserDAO users = getUsers();
		ArrayList<User> allUsers = new ArrayList<User>(users.findAll());
		ArrayList<User> usersToSend = new ArrayList<User>();
		
		
		if (dto.getUserGender().equals("all")) {
			UserRole role = getRole(dto.getUserRole());
			for (User user : allUsers) {
				if (user.getUserRole().equals(role)) {
					usersToSend.add(user);
				}
			}
			return usersToSend;
		}
		
		if (dto.getUserRole().equals("all")) {
			UserGender gender = getGender(dto.getUserGender());
			for (User user : allUsers) {
				if (user.getUserGender().equals(gender)) {
					usersToSend.add(user);
				}
			}
			return usersToSend;
		}
		
		UserRole role = getRole(dto.getUserRole());
		UserGender gender = getGender(dto.getUserGender());
		
		for (User user : allUsers) {
			if (user.getUserGender().equals(gender) && user.getUserRole().equals(role)) {
				usersToSend.add(user);
			}
		}
		
		return usersToSend;
		
		}
		
	public UserGender getGender(String gender) {
		if (gender.equals("male")) {
			return UserGender.MALE;
		} else {
			return UserGender.FEMALE;
		}
	}
	
	public UserRole getRole(String role) {
		if (role.equals("administrator")) {
			return UserRole.ADMINISTRATOR;
		} else if (role.equals("host")) {
			return UserRole.HOST;
		} else {
			return UserRole.GUEST;
		}
	}

	@GET
	@Path("UserDetails/{userName}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserDetailsDTO getUserDetails(@PathParam("userName") String userName, @Context HttpServletRequest request) {
		
		UserDAO users = getUsers();
		User user = users.findByUsername(userName);
		UserDetailsDTO dto = convertUserToUserDetails(user);
		return dto;
		
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
		} 
		else if (user.getUserRole().equals(UserRole.ADMINISTRATOR)) {
			dto.setUserRole("ADMINISTRATOR");
		}
		else {
			dto.setUserRole("HOST");
		}
		return dto;
		
	}
	

	@POST
	@Path("findUsersBySearchUserDTO")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> findUsersBySearchUserDTO(@Context HttpServletRequest request, SearchUserDTO userDTO) {
		UserDAO users = getUsers();
		List<User> ret = new ArrayList<User>();		
		
		for(Map.Entry<String, User> entry : users.getUsers().entrySet()) {
			boolean condition = false;
			boolean initialized = false;
			
			if(userDTO.getUserRole() != null) {
				condition = entry.getValue().getUserRole() == userDTO.getUserRole();
				initialized = true;
			}
			
			if(userDTO.getUserGender() != null) {
				if(initialized) {
					condition = condition && (entry.getValue().getUserGender() == userDTO.getUserGender());
				}else {
					condition = entry.getValue().getUserGender() == userDTO.getUserGender();
					initialized = true;
				}
			}
			
			if(userDTO.getUsername() != "" || userDTO.getUsername() != null) {
				if(initialized) {
					condition = condition && (entry.getValue().getUserName().equals(userDTO.getUsername()));
				}else {
					condition = entry.getValue().getUserName().equals(userDTO.getUsername());
					initialized = true;
				}
			}
			
			if(condition) {
				ret.add(entry.getValue());
			}
		}
		
		return ret;
	}
	
	public UserDAO getUsers() {
		UserDAO users = (UserDAO) context.getAttribute("users");
		return users;
	}
	
	public void saveUsers(UserDAO users) {
		users.saveUsers(context.getRealPath(""));
	}
}
