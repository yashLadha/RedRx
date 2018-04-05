package tech.yashladha.redrx.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorldPopulation {

	@SerializedName("worldpopulation")
	@Expose
	private List<Country> countries;

	public WorldPopulation() {
	}

	public WorldPopulation(List<Country> countries) {
		this.countries = countries;
	}

	public List<Country> getCountries() {
		return countries;
	}

	public void setCountries(List<Country> countries) {
		this.countries = countries;
	}
}
