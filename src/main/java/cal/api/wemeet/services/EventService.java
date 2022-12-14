package cal.api.wemeet.services;
import java.util.Properties;
import java.util.List;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import cal.api.wemeet.models.Event;
import cal.api.wemeet.models.User;
import cal.api.wemeet.models.dto.request.EventCreationEntry;
import cal.api.wemeet.models.dto.response.EventDto;
import cal.api.wemeet.repositories.EventRepository;
import cal.api.wemeet.repositories.UserRepository;

@Service
public class EventService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    EventRepository eventRepo;

    @Autowired
    UserService userService;

    public List<Event> getAllEvents(){
        return eventRepo.findAll();
    }

    public List<EventDto> getAllPublicEvents() {
        return eventRepo.findByIsPublic(true, Sort.by(Sort.Direction.ASC, "date"))
                        .stream()
                        .filter(event -> event.getDate().after(new Date()))
                        .map(event -> converEventToEventDto(event))
                        .collect(Collectors.toList());
    }

    public List<EventDto> getAllUserEvents() {
        return eventRepo.findByOrganizerId(userService.getAuthenticatedUser().getId(), Sort.by(Sort.Direction.DESC, "date"))
                        .stream()
                        .map(event -> converEventToEventDto(event))
                        .collect(Collectors.toList());
    }

    public Object getAllUserEventParticipations() {
        return userRepo.findById(userService.getAuthenticatedUser().getId()).get().getEvents()
                        .stream()
                        .map(event -> converEventToEventDto(event))
                        .collect(Collectors.toList());
    }

    public EventDto converEventToEventDto(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setDescription(event.getDescription());
        eventDto.setDate(event.getDate());
        eventDto.setIsPublic(event.getIsPublic());
        eventDto.setOrganizer(userService.convertUserToUserDto(event.getOrganizer()));
        eventDto.setParticipants(event.getParticipants()
                                    .stream()
                                    .map(user -> userService.convertUserToUserDto(user))
                                    .collect(Collectors.toList()));
        eventDto.setCo_organizers(event.getCo_organizers()
                                    .stream()
                                    .map(user -> userService.convertUserToUserDto(user))
                                    .collect(Collectors.toList()));
        eventDto.setAddress(event.getAddress());
        eventDto.setCountry(event.getCountry());
        eventDto.setCity(event.getCity());
        eventDto.setState(event.getState());
        eventDto.setPostalCode(event.getPostalCode());
        eventDto.setPrice(event.getPrice());
        eventDto.setTitle(event.getTitle());
        eventDto.setLatitude(event.getLatitude());
        eventDto.setLongitude(event.getLongitude());
        eventDto.setMaxParticipants(event.getMaxParticipants());
        return eventDto;
    }

    public Event getEventFromEventEntry(EventCreationEntry entry) {
        Event event = new Event();
        event.setDate(buildDate(entry));
        event.setAddress(entry.getAddress());
        event.setTitle(entry.getTitle());
        event.setCity(entry.getCity());
        event.setPostalCode(entry.getPostalCode());
        event.setCountry(entry.getCountry());
        event.setPrice(entry.getPrice());
        event.setDescription(entry.getDescription());
        event.setMaxParticipants(entry.getMaxParticipants());
        event.setIsPublic(entry.getIsPublic());
        return event;
    }

    public void setEventOrganizer(Event event) {
        event.setOrganizer(userService.getAuthenticatedUser());
    }

    public void saveEvent(Event event) {
        eventRepo.save(event);
    }

    public Event getEventById(String id) {
        if (! eventRepo.findById(id).isPresent()) {
            return null;
        } else {
            return eventRepo.findById(id).get();
        }
    }

    public boolean isOrganizer(Event event, User user){
        // System.out.println("event.getOrganizer().getId() : " + event.getOrganizer().getId());
        // System.out.println("user.getId() : " + user.getId());
        return user.getId().contentEquals(event.getOrganizer().getId());
    }

    public boolean isCoOrganizer(Event event, User user){
        return event.getCo_organizers().contains(user);
    }

    public Date buildDate(EventCreationEntry entry) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(entry.getDate());
        LocalDateTime localDateTime = entry.getTime().atDate(LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)));
        localDateTime = localDateTime.plusHours(1);
        Instant instant = localDateTime.atZone(ZoneId.of("Europe/Paris")).toInstant();
        Date date = Date.from(instant);
        return date;
    }

    public boolean isAlreadyParticipating(Event event, User authenticatedUser) {
        for(User user : event.getParticipants()) {
            if (user.getId().contentEquals(authenticatedUser.getId())) {
                return true;
            }
        }
        return false;
    }



}
