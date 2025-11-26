import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.nio.file.*;
import java.io.IOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherAppGUI extends JFrame {

    private static final String API_KEY = "a3320ef7b2725b5646915b393309d779";
    private static final String LAST_CITY_FILE = "lastCity.txt";

    private JTextField cityInput;
    private JButton fetchButton;
    private JTextArea outputArea;
    private JComboBox<String> unitSelector;
    private JComboBox<String> historyDropdown;
    private ArrayList<String> history = new ArrayList<>();

    public WeatherAppGUI() {
        setTitle("Java Weather App");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(true);

        // Top panel
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(200, 230, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel label = new JLabel("City:");
        label.setFont(new Font("Arial", Font.BOLD, 16));

        cityInput = new JTextField(12);
        cityInput.setFont(new Font("Arial", Font.PLAIN, 14));

        String lastCity = loadLastCity();
        if (lastCity != null) {
            cityInput.setText(lastCity);
        }

        fetchButton = new JButton("Get Weather");
        fetchButton.setFont(new Font("Arial", Font.BOLD, 14));
        fetchButton.setBackground(new Color(0, 120, 215));
        fetchButton.setForeground(Color.WHITE);
        fetchButton.setFocusPainted(false);

        unitSelector = new JComboBox<>(new String[]{"Celsius", "Fahrenheit"});
        historyDropdown = new JComboBox<>();
        historyDropdown.setPrototypeDisplayValue("Select previous city");

        topPanel.add(label);
        topPanel.add(cityInput);
        topPanel.add(fetchButton);
        topPanel.add(unitSelector);
        topPanel.add(historyDropdown);

        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBackground(new Color(245, 245, 245));
        outputArea.setForeground(new Color(50, 50, 50));
        outputArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(outputArea);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Button click
        fetchButton.addActionListener(e -> {
            String city = cityInput.getText().trim();
            if (city.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a city name.");
            } else {
                fetchWeather(city);
                updateHistory(city);
            }
        });

        // History selection
        historyDropdown.addActionListener(e -> {
            String selected = (String) historyDropdown.getSelectedItem();
            if (selected != null) {
                cityInput.setText(selected);
                fetchWeather(selected);
            }
        });
    }

    private void saveLastCity(String city) {
        try {
            Files.writeString(Path.of(LAST_CITY_FILE), city);
        } catch (IOException e) {
            System.out.println("Failed to save last city: " + e.getMessage());
        }
    }

    private String loadLastCity() {
        try {
            if (Files.exists(Path.of(LAST_CITY_FILE))) {
                return Files.readString(Path.of(LAST_CITY_FILE)).trim();
            }
        } catch (IOException e) {
            System.out.println("Failed to load last city: " + e.getMessage());
        }
        return null;
    }

    private void fetchWeather(String city) {
        try {
            String unit = unitSelector.getSelectedItem().equals("Celsius") ? "metric" : "imperial";
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city +
                    "&appid=" + API_KEY + "&units=" + unit;

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
                JsonObject wind = json.getAsJsonObject("wind");
                JsonObject sys = json.getAsJsonObject("sys");
                JsonObject weather = json.getAsJsonArray("weather").get(0).getAsJsonObject();

                double temperature = main.get("temp").getAsDouble();
                double feelsLike = main.get("feels_like").getAsDouble();
                int humidity = main.get("humidity").getAsInt();
                int pressure = main.get("pressure").getAsInt();
                double windSpeed = wind.get("speed").getAsDouble();
                String description = weather.get("description").getAsString();
                String cityName = json.get("name").getAsString();
                saveLastCity(cityName);

                //Sunrise and Sunset
                long sunriseUnix = sys.get("sunrise").getAsLong() * 1000L;
                long sunsetUnix = sys.get("sunset").getAsLong() * 1000L;
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String sunrise = sdf.format(new Date(sunriseUnix));
                String sunset = sdf.format(new Date(sunsetUnix));

                String unitSymbol = unit.equals("metric") ? "°C" : "°F";

                outputArea.setText(
                        "City: " + cityName + "\n" +
                        "Temperature: " + temperature + unitSymbol + " (Feels Like: " + feelsLike + unitSymbol + ")\n" +
                        "Humidity: " + humidity + "%\n" +
                        "Wind Speed: " + windSpeed + " m/s\n" +
                        "Pressure: " + pressure + " hPa\n" +
                        "Description: " + description + "\n" +
                        "Sunrise: " + sunrise + "\n" +
                        "Sunset: " + sunset
                );

            } else if (responseCode == 404) {
                outputArea.setText("City not found. Please check the spelling.");
            } else if (responseCode == 401) {
                outputArea.setText("Invalid API key.");
            } else {
                outputArea.setText("Error fetching data. HTTP code: " + responseCode);
            }

        } catch (Exception e) {
            outputArea.setText("An error occurred: " + e.getMessage());
        }
    }

    private void updateHistory(String city) {
        if (!history.contains(city)) {
            history.add(0, city);
            if (history.size() > 5) history.remove(5);
            historyDropdown.removeAllItems();
            for (String c : history) historyDropdown.addItem(c);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeatherAppGUI().setVisible(true));
    }
}
