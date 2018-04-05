package tech.yashladha.redrx.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Country implements Serializable{

	@SerializedName("rank")
	@Expose
	private int rank;

	@SerializedName("country")
	@Expose
	private String name;

	@SerializedName("population")
	@Expose
	private String population;

	@SerializedName("flag")
	@Expose
	private String flagUrl;

	public Country() {
	}

	public Country(int rank, String name, String population, String flagUrl) {
		this.rank = rank;
		this.name = name;
		this.population = population;
		this.flagUrl = flagUrl;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPopulation() {
		return population;
	}

	public void setPopulation(String population) {
		this.population = population;
	}

	public String getFlagUrl() {
		return flagUrl;
	}

	public void setFlagUrl(String flagUrl) {
		this.flagUrl = flagUrl;
	}
}
