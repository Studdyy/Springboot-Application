package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

@SpringBootApplication
@RestController
public class DemoApplication {
	String baseURL;
	HashMap<String, List<String>> trainers;

	public DemoApplication() {
		baseURL = "https://pokeapi.co/api/v2/pokemon/";
		trainers = new HashMap<>();
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@GetMapping()
	public String getTeam(@RequestParam(value = "name", defaultValue = "Ash") String name) throws IOException, JSONException {
		List<String> team;
		if (trainers.containsKey(name)) {
			team = trainers.get(name);
			return String.format("Hello %s, and welcome to the Battle Tower! Your team for this run will be" +
					": %s, %s, %s, %s, %s, %s", name, team.get(0), team.get(1), team.get(2), team.get(3), team.get(4), team.get(5));
		}

		Random random = new Random();
		team = new ArrayList<>();
		for (int i = 1; i <= 6; i++) {
			String nextPokedexNumber = Integer.toString(random.nextInt(893) + 1);
			StringBuilder nextURL = new StringBuilder(baseURL);
			nextURL.append(nextPokedexNumber);

			URL url = new URL(nextURL.toString());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder content = new StringBuilder();

			String input;
			while ((input = reader.readLine()) != null) {
				content.append(input);
			}

			JSONObject response = new JSONObject(content.toString());
			team.add(((String)response.get("name")).toUpperCase());
		}

		trainers.put(name, team);
		return String.format("Hello %s, and welcome to the Battle Tower! Your team for this run will be" +
				": %s, %s, %s, %s, %s, %s", name, team.get(0), team.get(1), team.get(2), team.get(3), team.get(4), team.get(5));
	}
}