package com.example.tracking.travyotei.tracking_service_travyotei.geocoding_service;


import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class NominatimGeocoding {

    // Define the required User-Agent value once
    private static final String USER_AGENT_VALUE = "TrackingService/1.0 (1enowarreyntow@gmail.com)";

    /**
     * Gets the latitude and longitude for a given location name and returns the full JSON object.
     * @param placeName The location name to search for.
     * @return An Optional containing the first matching JSONObject from the API, or Optional.empty() if not found or an error occurred.
     */
    public Optional<JSONObject> getCoordinatesFromPlaceName(String placeName) {
        String encodedName = URLEncoder.encode(placeName, StandardCharsets.UTF_8);

        // Correct URL: include scheme, host and "search?q=" path
        String urlString = "https://nominatim.openstreetmap.org/search?q=" + encodedName + "&format=json&limit=1";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .header("User-Agent", USER_AGENT_VALUE)
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONArray jsonArray = new JSONArray(response.body());
                if (!jsonArray.isEmpty()) {
                    JSONObject firstResult = jsonArray.getJSONObject(0);
                    return Optional.of(firstResult);
                } else {
                    return Optional.empty();
                }
            } else {
                System.err.println("API request failed with status code: " + response.statusCode());
                return Optional.empty();
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("HTTP request error: " + e.getMessage());
            return Optional.empty();
        }
    }

    // // Example Main method to test the functionality
    // public static void main(String[] args) {
    //     Optional<JSONObject> result = getCoordinatesFromPlaceName("Eiffel Tower Paris");
        
    //     if (result.isPresent()) {
    //         System.out.println("\nSuccessfully retrieved JSON object:");
    //         // Print the entire raw JSON object returned by the method
    //         System.out.println(result.get().toString(4)); // toString(4) formats the JSON nicely
    //     } else {
    //         System.out.println("\nFailed to get coordinates.");
    //     }
    // }
}
