package services;

import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import dao.ApartmentDAO;
import dao.UserDAO;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

public class ApartmentService {

	@Context
	ServletContext context;
	
	public ApartmentService() {
		
	}
	
	@PostConstruct
	public void init() {
		if (context.getAttribute("aparmentDAO") == null) {
			context.setAttribute("aparmentDAO", new ApartmentDAO(context.getRealPath("")));
		}
	}
	
}
