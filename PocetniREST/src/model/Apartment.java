package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import enumeration.ApartmentType;

@SuppressWarnings("serial")
public class Apartment implements Serializable{

	private String id;
	private ApartmentType apartmentType;
	private int numberOfRooms;
	private int numberOfGuests;
	private Location location;
	private ArrayList<Date> startDates;
	private ArrayList<Date> endDates;
	private String hostId;
	private ArrayList<String> commentIds;
	private ArrayList<String> photos;
	private float pricePerNight;
	private String checkInTime;
	private String checkOutTime;
	private boolean activityStatus;
	private ArrayList<String> amenityIds;
	private ArrayList<String> reservationsIds;
	private boolean deleted;
	private ArrayList<Double> grades;
	
	public Apartment() {
		super();
		this.startDates = new ArrayList<Date>();
		this.endDates = new ArrayList<Date>();
		this.commentIds = new ArrayList<String>();
		this.photos = new ArrayList<String>();
		this.amenityIds = new ArrayList<String>();
		this.reservationsIds = new ArrayList<String>();
		this.grades = new ArrayList<Double>();
	}

	public Apartment(ApartmentType apartmentType, int numberOfRooms, int numberOfGuests, Location location,
			ArrayList<Date> startDates, ArrayList<Date> endDates, String hostId,
			String host, ArrayList<String> comments, ArrayList<String> photos,
			float pricePerNight, String checkInTime, String checkOutTime, boolean activityStatus,
			ArrayList<String> amenities, ArrayList<String> reservations, boolean deleted, ArrayList<Double> grades) {
		super();
		 
		this.apartmentType = apartmentType;
		this.numberOfRooms = numberOfRooms;
		this.numberOfGuests = numberOfGuests;
		this.location = location;
		this.startDates = startDates;
		this.endDates = endDates;
		this.hostId = hostId;
		this.commentIds = comments;
		this.photos = photos;
		this.pricePerNight = pricePerNight;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;
		this.activityStatus = activityStatus;
		this.amenityIds = amenities;
		this.reservationsIds = reservations;
		this.deleted = deleted;
		this.grades = grades;
	}
	
	public Apartment(ApartmentType apartmentType, int numberOfRooms, int numberOfGuests, Location location,
			ArrayList<Date> startDates, ArrayList<Date> endDates, ArrayList<String> photos,
			float pricePerNight, String checkInTime, String checkOutTime) {
		super();
		 
		this.apartmentType = apartmentType;
		this.numberOfRooms = numberOfRooms;
		this.numberOfGuests = numberOfGuests;
		this.location = location;
		this.startDates = startDates;
		this.endDates = endDates;
		this.photos = photos;
		this.pricePerNight = pricePerNight;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;

		this.commentIds = new ArrayList<String>();
		this.amenityIds = new ArrayList<String>();
		this.reservationsIds = new ArrayList<String>();
		this.grades = new ArrayList<Double>();
		
		this.deleted = false;
		this.activityStatus = true;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ApartmentType getApartmentType() {
		return apartmentType;
	}

	public void setApartmentType(ApartmentType apartmentType) {
		this.apartmentType = apartmentType;
	}

	public int getNumberOfRooms() {
		return numberOfRooms;
	}

	public void setNumberOfRooms(int numberOfRooms) {
		this.numberOfRooms = numberOfRooms;
	}

	public int getNumberOfGuests() {
		return numberOfGuests;
	}

	public void setNumberOfGuests(int numberOfGuests) {
		this.numberOfGuests = numberOfGuests;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public ArrayList<Date> getStartDates() {
		return startDates;
	}

	public void setStartDates(ArrayList<Date> startDates) {
		this.startDates = startDates;
	}

	public ArrayList<Date> getEndDates() {
		return endDates;
	}

	public void setEndDates(ArrayList<Date> endDates) {
		this.endDates = endDates;
	}

	public String getHostId() {
		return hostId;
	}
	
	public ArrayList<String> getCommentIds() {
		return commentIds;
	}

	public void setCommentIds(ArrayList<String> commendIds) {
		this.commentIds = commendIds;
	}
	
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public ArrayList<String> getPhotos() {
		return photos;
	}

	public void setPhotos(ArrayList<String> photos) {
		this.photos = photos;
	}

	public float getPricePerNight() {
		return pricePerNight;
	}

	public void setPricePerNight(float pricePerNight) {
		this.pricePerNight = pricePerNight;
	}

	public String getCheckInTime() {
		return checkInTime;
	}

	public void setCheckInTime(String checkInTime) {
		this.checkInTime = checkInTime;
	}

	public String getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckOutTime(String checkOutTime) {
		this.checkOutTime = checkOutTime;
	}

	public boolean isActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(boolean activityStatus) {
		this.activityStatus = activityStatus;
	}

	public ArrayList<String> getAmenityIds() {
		return amenityIds;
	}

	public void setAmenityIds(ArrayList<String> amenities) {
		this.amenityIds = amenities;
	}

	public ArrayList<String> getReservations() {
		return reservationsIds;
	}

	public void setReservations(ArrayList<String> reservations) {
		this.reservationsIds = reservations;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	public ArrayList<Double> getGrades() {
		return grades;
	}

	public void setGrades(ArrayList<Double> grades) {
		this.grades = grades;
	}

	@Override
	public String toString() {
		return "Apartment [id=" + id + ", apartmentType=" + apartmentType + ", numberOfRooms=" + numberOfRooms
				+ ", numberOfGuests=" + numberOfGuests + ", location=" + location + ", datesForRent="
				+ ", hostId=" + hostId + ", commentIds=" + commentIds + ", photos=" + photos + ", pricePerNight="
				+ pricePerNight + ", checkInTime=" + checkInTime + ", checkOutTime=" + checkOutTime
				+ ", activityStatus=" + activityStatus + ", amenityIds=" + amenityIds + ", reservationsIds="
				+ reservationsIds + ", deleted=" + deleted + ", grades=" + grades + "]";
	}
	
	
}
