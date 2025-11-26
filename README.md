Java Weather App

A simple, beginner-friendly Java console application that fetches real-time weather information for any city using the OpenWeatherMap API.

 Features

 Fetches weather for any city worldwide

 Shows temperature (°C)

 Displays humidity (%)

 Shows weather description (e.g. “clear sky”)

 Handles errors such as:

City not found

Invalid API key

Network issues

 Technologies Used

Java (JDK 8 or later)

HTTPURLConnection – for sending API requests

Gson – for JSON parsing

OpenWeatherMap API

 Project Structure
JavaWeatherApp/
├── Main.java
└── README.md

 Getting Started
1️ Clone the repository
git clone https://github.com/YourUsername/JavaWeatherApp.git
cd JavaWeatherApp

2️ Add Gson to your project

If you’re using Maven, add this to your pom.xml:

<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>


If you're not using Maven, download the Gson JAR and add it to your project libraries.

3️ Add Your API Key

Replace this line inside Main.java:

private static final String API_KEY = "YOUR_API_KEY_HERE";


Get your API key from: https://openweathermap.org/api

 Running the Application

Compile:

javac Main.java


Run:

java Main


Enter a city:

Enter a city name: Cape Town


Example output:

==============================
      Current Weather
==============================
City: Cape Town
Temperature: 19°C
Humidity: 60%
Condition: scattered clouds

 Example API Request
https://api.openweathermap.org/data/2.5/weather?q=London&appid=YOUR_API_KEY&units=metric

 Error Handling

The program handles:

404 → City not found

401 → Invalid API key

Other HTTP errors

General exceptions (bad input, connection issues)
