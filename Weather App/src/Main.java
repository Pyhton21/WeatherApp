import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {
    private static final String API_KEY = "a3320ef7b2725b5646915b393309d779"; // API Key

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Java Weather App ===");
        System.out.print("Enter a city name: ");
        String city = scanner.nextLine().trim();
        scanner.close();

        System.out.println("\nFetching weather data, please wait...\n");

        try {
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city +
                            "&appid=" + API_KEY + "&units=metric";

            URL url = new URI(apiUrl).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                conn.disconnect();

                JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
                JsonObject main = json.getAsJsonObject("main");
                JsonObject weather = json.getAsJsonArray("weather").get(0).getAsJsonObject();

                double temperature = main.get("temp").getAsDouble();
                int humidity = main.get("humidity").getAsInt();
                String description = weather.get("description").getAsString();
                String cityName = json.get("name").getAsString();

                System.out.println("==============================");
                System.out.println("      Current Weather ");
                System.out.println("==============================");
                System.out.println("City: " + cityName);
                System.out.println("Temperature: " + temperature + "°C");
                System.out.println("Humidity: " + humidity + "%");
                System.out.println("Condition: " + description);

            } else if (responseCode == 404) {
                System.out.println(" City not found. Please check the spelling and try again.");
            } else if (responseCode == 401) {
                System.out.println(" Invalid API key. Please check your OpenWeatherMap key.");
            } else {
                System.out.println(" Error fetching data. HTTP code: " + responseCode);
            }

        } catch (Exception e) {
            System.out.println(" An error occurred: " + e.getMessage());
        }
    }
}
