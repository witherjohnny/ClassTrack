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

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import java.security.NoSuchAlgorithmException;
import java.security.KeyManagementException;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;

public class ApiHelper {

    private static final String API_URL = "http://192.168.71.1/cao/tablet/"; // Cambia localhost con IP server
    private OkHttpClient client;

    public ApiHelper() {

        this.client = new OkHttpClient();
    }


    public ArrayList<String> getDocente() {
        ArrayList<String> result = new ArrayList<>();
        Request request = new Request.Builder().url(API_URL).build();

        try (Response response = client.newCall(request).execute()) {  // Automaticamente chiude la risposta
            if (response.isSuccessful() && response.body() != null) {
                String jsonData = response.body().string();
                JSONArray jsonArray = new JSONArray(jsonData);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    // Aggiungi i dati come necessario
                    // Esempio: result.add(obj.getString("nome"));
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<String> getClasseEDocenteAttuale(int ID_aula, String giorno, int orario) {
        ArrayList<String> result = new ArrayList<>();

        // Codifica i parametri per evitare problemi con caratteri speciali
        String payload = String.format("aula=%s&giorno=%s&orario=%s", ID_aula, giorno, orario);
        String fullUrl = API_URL + "docenteInAula.php?" + payload;

        Log.d("ApiRequest", "URL chiamata API: " + fullUrl); // DEBUG

        Request request = new Request.Builder().url(fullUrl).build();

        try (Response response = client.newCall(request).execute()) {
            // Verifica se la risposta è stata eseguita con successo
            if (response.isSuccessful() && response.body() != null) {
                String jsonData = response.body().string();
                Log.d("ApiRequest", "JSON ricevuto: " + jsonData); // DEBUG

                // Parsing JSON
                try {
                    JSONArray jsonArray = new JSONArray(jsonData);
                    if (jsonArray.length() == 0) {
                        Log.d("ApiRequest", "Nessun dato ricevuto dall'API.");
                        return result;
                    }

                    // Itera sugli oggetti JSON
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        // Verifica se tutti i dati richiesti sono presenti
                        if (obj.has("nome") && obj.has("cognome") && obj.has("annoSezione") && obj.has("indirizzo")) {
                            // Aggiungi i dati alla lista
                            result.add(obj.getString("nome") + " " + obj.getString("cognome")); // Docente
                            result.add(obj.getString("annoSezione") + " " + obj.getString("indirizzo")); // Classe
                        } else {
                            Log.d("ApiRequest", "Dati mancanti nel JSON!");
                        }
                    }
                } catch (JSONException e) {
                    Log.e("ApiRequest", "Errore nel parsing del JSON: " + e.getMessage());
                }
            } else {
                Log.d("ApiRequest", "Errore HTTP: " + response.code());
            }
        } catch (IOException e) {
            Log.e("ApiRequest", "Errore nella richiesta API: " + e.getMessage());
        }

        return result;
    }

}
