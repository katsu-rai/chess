package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ServerFacade {

    private final String baseURL = "http://localhost:8080";
    private final Gson gson = new Gson();

    public boolean register(String username, String password, String email) {
        Map<String, String> input = Map.of("username", username, "password", password, "email", email);
        String body = gson.toJson(input);
        return post("/user", body);
    }

    public boolean login(String username, String password) {
        Map<String, String> input = Map.of("username", username, "password", password);
        String body = gson.toJson(input);
        return post("/session", body);
    }

    private boolean post(String endpoint, String body) {
        HttpURLConnection http = null;

        try {
            URI uri = new URI(baseURL + endpoint);
            http = (HttpURLConnection) uri.toURL().openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/json");

            // Send request body
            try (OutputStream outputStream = http.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }

            // Connect and check response
            int responseCode = http.getResponseCode();
            if (responseCode == 401) {
                return false;
            }

            if (responseCode >= 200 && responseCode < 300) {
                try (InputStream respBody = http.getInputStream();
                     InputStreamReader reader = new InputStreamReader(respBody, StandardCharsets.UTF_8)) {
                    JsonObject jsonResponse = gson.fromJson(reader, JsonObject.class);
                    String authToken = jsonResponse.has("authToken") ? jsonResponse.get("authToken").getAsString() : null;
                    return authToken != null;
                }
            } else {
                return false;
            }

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }
}
