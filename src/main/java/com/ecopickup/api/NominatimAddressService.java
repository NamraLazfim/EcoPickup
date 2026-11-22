package com.ecopickup.api;

import com.ecopickup.model.Address;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link AddressService} backed by the OpenStreetMap Nominatim API
 * ({@code https://nominatim.openstreetmap.org/search?q=<address>&format=json}). It sends a User-Agent,
 * waits one second between requests, and keeps a cache so a known address still works when the API is down.
 */
public class NominatimAddressService implements AddressService {

    /** The public Nominatim server. */
    public static final String DEFAULT_BASE_URL = "https://nominatim.openstreetmap.org";
    /** Shown when nothing matches the address. */
    public static final String NOT_FOUND_MESSAGE = "Address not found, please check it.";
    /** Shown when the API cannot be reached. */
    public static final String UNAVAILABLE_MESSAGE = "Address service is temporarily unavailable.";

    private static final String USER_AGENT = "EcoPickup/1.0 (CSC61204 student project, Taylor's University)";
    private static final Duration TIMEOUT = Duration.ofSeconds(8);
    private static final long MIN_GAP_MILLIS = 1000;
    private static final int HTTP_OK_MIN = 200;
    private static final int HTTP_OK_MAX = 299;
    private static final int HTTP_NOT_FOUND = 404;

    private final HttpClient client = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    private final String baseUrl;
    private final Map<String, Address> cache = new ConcurrentHashMap<>();
    private long lastRequestMillis;

    /**
     * Creates a service that calls the public Nominatim server.
     */
    public NominatimAddressService() {
        this(DEFAULT_BASE_URL);
    }

    /**
     * Creates a service that calls another server, e.g. a mock server in tests.
     *
     * @param baseUrl the server address without a trailing slash
     */
    public NominatimAddressService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public Address lookup(String query) throws AddressLookupException {
        String key = query.trim().toLowerCase(Locale.ENGLISH);
        try {
            Address address = request(query.trim());
            cache.put(key, address);
            return address;
        } catch (AddressLookupException failure) {
            // API is down: fall back to the last result we got for this address
            Address known = cache.get(key);
            if (failure.getReason() == AddressLookupException.Reason.UNAVAILABLE && known != null) {
                return known;
            }
            throw failure;
        }
    }

    private Address request(String query) throws AddressLookupException {
        HttpResponse<String> response = send(buildRequest(query));
        int status = response.statusCode();
        if (status == HTTP_NOT_FOUND) {
            throw new AddressLookupException(AddressLookupException.Reason.NOT_FOUND, NOT_FOUND_MESSAGE);
        }
        if (status < HTTP_OK_MIN || status > HTTP_OK_MAX) {
            throw new AddressLookupException(AddressLookupException.Reason.UNAVAILABLE, UNAVAILABLE_MESSAGE);
        }
        return parseFirstResult(response.body());
    }

    private HttpRequest buildRequest(String query) {
        String url = baseUrl + "/search?format=json&limit=1&q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
        return HttpRequest.newBuilder(URI.create(url))
                .timeout(TIMEOUT)
                .header("User-Agent", USER_AGENT)
                .header("Accept", "application/json")
                .GET()
                .build();
    }

    private HttpResponse<String> send(HttpRequest request) throws AddressLookupException {
        try {
            waitForRateLimit();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException noConnection) {
            throw new AddressLookupException(AddressLookupException.Reason.UNAVAILABLE, UNAVAILABLE_MESSAGE);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new AddressLookupException(AddressLookupException.Reason.UNAVAILABLE, UNAVAILABLE_MESSAGE);
        }
    }

    // Nominatim allows about one request per second
    private synchronized void waitForRateLimit() throws InterruptedException {
        long wait = lastRequestMillis + MIN_GAP_MILLIS - System.currentTimeMillis();
        if (wait > 0) {
            Thread.sleep(wait);
        }
        lastRequestMillis = System.currentTimeMillis();
    }

    static Address parseFirstResult(String json) throws AddressLookupException {
        String name = JsonFields.firstString(json, "display_name");
        String lat = JsonFields.firstString(json, "lat");
        String lon = JsonFields.firstString(json, "lon");
        if (name == null || lat == null || lon == null) {
            throw new AddressLookupException(AddressLookupException.Reason.NOT_FOUND, NOT_FOUND_MESSAGE);
        }
        try {
            return new Address(name, Double.parseDouble(lat), Double.parseDouble(lon));
        } catch (NumberFormatException badNumber) {
            throw new AddressLookupException(AddressLookupException.Reason.UNAVAILABLE, UNAVAILABLE_MESSAGE);
        }
    }
}
