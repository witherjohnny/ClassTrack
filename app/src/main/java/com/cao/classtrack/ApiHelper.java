package com.cao.classtrack;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
public class ApiHelper {

    private static final String API_URL = "http://localhost/cao/tablet/"; // Modifica l'URL secondo necessità
    private final OkHttpClient client;

    public ApiHelper() {
        this.client = new OkHttpClient();
    }

    public ArrayList<String> getDocente() {
        ArrayList<String> result = new ArrayList<>();
        Request request = new Request.Builder().url(API_URL).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String jsonData = response.body().string();
                JSONArray jsonArray = new JSONArray(jsonData);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<String> getClasseEDocenteAttuale(int ID_aula,String giorno, int orario) {
        ArrayList<String> result = new ArrayList<>();

        String payload = String.format("aula=%s&giorno=%s&orario=%s", ID_aula, giorno, orario);
        String fullUrl = "http://172.16.102.79/cao/tablet/docenteInAula.php?" + payload; // Cambia localhost con IP server

        Log.d("ApiRequest","URL chiamata API: " + fullUrl); // DEBUG

        Request request = new Request.Builder().url(fullUrl).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String jsonData = response.body().string();
                Log.d("ApiRequest","JSON ricevuto: " + jsonData); // DEBUG

                JSONArray jsonArray = new JSONArray(jsonData);
                if (jsonArray.length() == 0) {
                    Log.d("ApiRequest","Nessun dato ricevuto dall'API.");
                    return result;
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    if (!obj.has("nome") || !obj.has("cognome") || !obj.has("annoSezione") || !obj.has("indirizzo")) {
                        Log.d("ApiRequest","Dati mancanti nel JSON!");
                        continue;
                    }
                    result.add(obj.getString("nome") + " " + obj.getString("cognome")); // Docente
                    result.add(obj.getString("annoSezione") + " " + obj.getString("indirizzo")); // Classe
                }
            } else {
                Log.d("ApiRequest","Errore HTTP: " + response.code());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


}
