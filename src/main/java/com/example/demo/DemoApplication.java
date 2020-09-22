package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.*;
import org.json.*;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RestController
public class DemoApplication {
	Random random;
	String baseURL;
	HashMap<String, List<String>> trainers;

	public DemoApplication() {
		random = new Random();
		trainers = new HashMap<>();
		baseURL = "https://pokeapi.co/api/v2/pokemon/";
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	/* This function generates a random Pokemon team and returns a string representation of the team.
	* 'name' = The name of the trainer who the team belongs to.
	* 'count' = The number of Pokemon requested from the team. Count must be positive and less than or equal to 6.
	* 'change' = The number of the team member who is to be replaced. Change must be positive and less than or equal to 6. */
	@GetMapping()
	public String getTeam(@RequestParam(value = "name", defaultValue = "Ash") String name,
						  @RequestParam(value = "count", defaultValue = "6") int count,
						  @RequestParam(value = "change", defaultValue = "0") int change) throws IOException, JSONException {

		List<String> team = new ArrayList<>();
		if (trainers.containsKey(name)) {
			team = trainers.get(name);

			if (team.size() < count) { addToTeam(team, team.size(), count); }
			if (change != 0 && team.size() >= change) { changeMember(team, change); }

		} else { addToTeam(team,0, count); }

		trainers.put(name, team);
		return computeOutputString(name, count, team);
	}

	/* This function adds a variable number of new Pokemon to a team.
	* 'team' = The team to which Pokemon are added.
	* 'start' = The slot number at which to start adding Pokemon. Start must be greater than or equal to 0 and less than or equal to 5.
	* 'count' = The number of new Pokemon to be added. Count must be positive and less than or equal to 6. */
	public List<String> addToTeam(List<String> team, int start, int count) throws JSONException {
		for (int i = start; i < count; i++) {
			team.add(generatePokemon());
		}
		return team;
	}

	/* This function replaces a member on a Pokemon team with a new randomly generated Pokemon.
	* 'team' = The team containing a Pokemon to be replaced.
	* 'member' = The number of the team member who is to be replaced. Member must be positive and less than or equal to 6. */
	public List<String> changeMember(List<String> team, int member) throws JSONException {
		team.remove(member - 1);
		team.add(member - 1, generatePokemon());
		return team;
	}

	/* This function generates a random Pokemon. */
	public String generatePokemon() throws JSONException {
		String nextPokedexNumber = Integer.toString(random.nextInt(893) + 1);
		StringBuilder nextURL = new StringBuilder(baseURL);
		nextURL.append(nextPokedexNumber);

		RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.getForObject(nextURL.toString(), String.class);

		JSONObject json = new JSONObject(response);

		return ((String) json.get("name")).toUpperCase();
	}

	/* This function computes the string output of a Pokemon team and returns the computed string.
	* 'name' = The name of the trainer who the team belongs to.
	* 'count' = The number of Pokemon requested from the team. Count must be positive and less than or equal to 6.
	* 'team' = The team belonging to the trainer. */
	public String computeOutputString(String name, int count, List<String> team) {
		StringBuilder outputString = new StringBuilder();

		outputString.append("Hello ");
		outputString.append(name);
		outputString.append(", and welcome to the Battle Tower! Your team for this run will be : ");

		for (int i = 0; i < count; i++) {
			outputString.append(team.get(i)); outputString.append(" ");
		}

		System.out.println(outputString.toString());
		return outputString.toString();
	}
}