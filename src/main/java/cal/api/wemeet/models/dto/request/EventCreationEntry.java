package cal.api.wemeet.models.dto.request;

import java.time.LocalTime;
import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


public class EventCreationEntry {

    private Date date;
    private LocalTime time;

    @NotEmpty(message = "Title is required")
    private String title;

    @NotEmpty(message = "The Address is required")
    private String Address;

    @NotEmpty(message = "The City is required")
    private String city;

    @NotEmpty(message = "The Postal Code is required")
    private String postalCode;

    @NotEmpty(message = "The Country is required")
    private String country;

    @Min(value = 0, message = "The price should be positive")
    private double price = 0;

    @Size(min = 20, message = "Description should have at least 20 characters")
    private String description;

    private boolean isPublic;

    private int maxParticipants = 0;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    
    
}
