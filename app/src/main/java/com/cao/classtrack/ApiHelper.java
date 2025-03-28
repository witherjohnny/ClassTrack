package com.cao.classtrack;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiHelper {

    private static final String API_URL = "http://localhost:5000/api/countries"; // Modifica l'URL secondo necessità
    private final OkHttpClient client;

    public ApiHelper() {
        this.client = new OkHttpClient();
    }

    public List<String> fetchCountries() {
        List<String> countries = new ArrayList<>();
        Request request = new Request.Builder().url(API_URL).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String jsonData = response.body().string();
                JSONArray jsonArray = new JSONArray(jsonData);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    countries.add(obj.getString("name"));
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return countries;
    }

    public List<String> getFiltered(String input) {
        List<String> filtered = new ArrayList<>();
        List<String> countries = fetchCountries();

        for (String country : countries) {
            if (country.toLowerCase().startsWith(input.toLowerCase())) {
                filtered.add(country);
            }
        }

        return filtered;
    }
}
