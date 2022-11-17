package cal.api.wemeet.controllers;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cal.api.wemeet.models.Event;
import cal.api.wemeet.models.EventState;
import cal.api.wemeet.models.User;
import cal.api.wemeet.models.dto.request.EventCreationEntry;
import cal.api.wemeet.models.dto.response.SimpleResponse;
import cal.api.wemeet.services.EventService;
import cal.api.wemeet.services.UserService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    EventService eventService;

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<?> allPublicEvents() {
        return ResponseEntity.ok().body(eventService.getAllPublicEvents());
    }

    @GetMapping("/mine")
    public ResponseEntity<?> allMyEvents() {
        return ResponseEntity.ok().body(eventService.getAllUserEvents());
    }

    
    @PostMapping("/create")
    public ResponseEntity<?> registerEvent(@Valid @RequestBody EventCreationEntry entry) {
        
        Event event = eventService.getEventFromEventEntry(entry);
        if (event.getDate().before(new Date())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SimpleResponse("Event date cannot be in the past"));
        }
        eventService.setEventOrganizer(event);
        eventService.saveEvent(event);

        return ResponseEntity.status(HttpStatus.CREATED)
        .body(new SimpleResponse("Event created successfully!"));
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelEvent(@PathVariable("id") String id) {
        Event event = eventService.getEventById(id);
        if (event == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new SimpleResponse("The event to cancel is not found!"));
        }

        if (event.getState() == EventState.DONE){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new SimpleResponse("The event is already done!"));
        }

        User user = userService.getAuthenticatedUser();
        if (eventService.isOrganizer(event, user) || eventService.isCoOrganizer(event, user)){
            event.setState(EventState.CANCELLED);
            eventService.saveEvent(event);
            return ResponseEntity.status(HttpStatus.OK)
            .body(new SimpleResponse("Event cancelled successfully!"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new SimpleResponse("You are not allowed to cancel this event!"));
        }
    }


}
