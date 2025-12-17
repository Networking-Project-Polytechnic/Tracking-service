- Swagger UI URL: http://localhost:8080/swagger-ui.html (or http://localhost:8080/swagger-ui/index.html)
- OpenAPI 3 JSON definition URL: http://localhost:8080/v3/api-docs



Use of KafkaProducer example:
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {

    private final KafkaProducerService producerService;

    public ProducerController(KafkaProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/publish")
    public String publishMessage(@RequestParam("message") String message) {
        // We assume you have a topic named "test-events" defined elsewhere
        producerService.sendMessage("test-events", message);
        return "Message sent to Kafka topic 'test-events' successfully!";
    }
}


1. Packages Table (or Shipments, TrackingRequests - to store static details)
This table stores the main, relatively static information about the package and its journey.
id (Primary Key, UUID)
tracking_code (String, unique identifier for the customer)
bus_id (Foreign Key to a Buses table)
start_route (String, name/reference to a Locations table)
end_route (String, name/reference to a Locations table)
departure_time (LocalTime or LocalDateTime)
arrival_time (LocalTime or LocalDateTime, estimated or actual)
current_status (String, e.g., 'In Transit', 'Delivered'. This is a dynamically updated field from the history table for quick access).


// Guidelines
When a package is created via your Spring Boot backend, two database operations occur almost simultaneously:
Insert into Packages table: Store the static details (tracking_code, bus_id, start_route, end_route, departure_time, etc.).
Insert the first record into TrackingUpdates table: Store the initial status, location coordinates, and timestamp, linking it back via the package_id.
From that point forward, every subsequent location update is simply a new insert into the TrackingUpdates table, just like the initial record was.


// More gold
When a user is at your agency registering a package, you can capture the initial latitude and longitude through one of three primary methods: 
1. 📍 Pre-defined Agency Locations (Recommended)
The most reliable approach is to have a separate table in your database for your agency locations. Since the user is physically present at a known, static business location, the coordinates are fixed.
Agencies Table:
agency_id
name (e.g., "Main Street Terminal")
address
latitude
longitude
Workflow:
When you build the system, you manually look up the coordinates for all your agency locations using a tool like Google Maps and store them in the Agencies table.
In your package registration form, the staff member simply selects the correct agency from a dropdown list (e.g., "Main Street Terminal").
Your Spring Boot application then automatically uses the pre-stored latitude and longitude associated with that agency ID when creating the initial TrackingUpdates record. 
2. 🗺️ Geocoding the Address
If you don't want to manage a separate Agencies table, you can use a geocoding API to convert the physical street address the user provides into coordinates in real-time. 
Workflow:
The staff member types the physical address into your web application form.
When the form is submitted, your Spring Boot backend sends the address to a geocoding service API (like Google Maps Geocoding API or OpenStreetMap's Nominatim API).
The API returns the latitude and longitude, which you then store in your database. 
3. 🌐 Using the Browser's Geolocation API
This method relies on the device (computer or tablet) used by the staff member at the agency to capture its current physical location using GPS or network data.
Workflow:
The registration web page includes a small piece of JavaScript code that uses the HTML5 Geolocation API.
The browser will prompt the staff member: "Allow this site to access your location?"
Upon approval, the JavaScript captures the precise latitude and longitude of the device.
These coordinates are then automatically filled into hidden fields in your registration form and sent to your Spring Boot backend when the package is saved. 
The Pre-defined Agency Locations is usually the most robust and accurate method for initial drop-off points, as these locations are fixed and known. The other methods are more dynamic but introduce potential points of failure (e.g., API limits, browser permissions).

