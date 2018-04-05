package tech.yashladha.redrx.Services;

import retrofit2.Call;
import retrofit2.http.GET;
import tech.yashladha.redrx.Models.WorldPopulation;

public interface ApiService {
	@GET("jsonparsetutorial.txt")
	Call<WorldPopulation> getCountries();
}
