package com.cao.classtrack;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cao.classtrack.classes.ApiHelper;
import com.cao.classtrack.classes.RoomItem;
import com.cao.classtrack.databinding.FragmentPaginaPianoBinding;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PaginaPiano extends Fragment {

    private FragmentPaginaPianoBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ApiHelper apiHelper = new ApiHelper();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPaginaPianoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        enforceFullScreen();
        SharedPreferences preferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String lotto = preferences.getString("lotto_name","Lotto");
        String piano = preferences.getString("piano","Piano");
        String colorLotto = lotto.equals("l3") ? "arancione" : (lotto.equals("l2") ? "giallo": "rosso");
        handler.post(() -> {
            binding.textLotto.setText("Lotto "+colorLotto);
            binding.textPiano.setText("Piano: "+piano.trim());
        });

        inizializzaSearchBar();
        ApiHelper apiHelper = new ApiHelper();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            ArrayList<JSONObject> docenti = apiHelper.getDocenti();
            ArrayList<JSONObject> classi = apiHelper.getClassi();
            ArrayList<JSONObject> rooms = apiHelper.getRooms();

            try {
                ArrayList<String> suggestions = new ArrayList<>();
                for (int i = 0; i < docenti.size(); i++) {
                    JSONObject obj = docenti.get(i);
                    String docente = obj.getString("nomeCognome");

                    suggestions.add(docente);
                }
                for (int i = 0; i < classi.size(); i++) {
                    JSONObject obj = classi.get(i);
                    String classe = obj.getString("annoSezione")+ obj.getString("indirizzo");

                    suggestions.add(classe);
                }
                for (int i = 0; i < rooms.size(); i++) {
                    JSONObject obj = rooms.get(i);
                    String room = obj.getString("room_name");

                    suggestions.add(room);
                }


                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            suggestions
                    );
                    binding.searchView.setAdapter(adapter);
                    binding.searchView.setThreshold(1);
                });

            } catch (JSONException e) {
                Log.e("ApiRequest", "Errore nel parsing del JSON: " + e.getMessage());
            }
        });

    }
    public void inizializzaSearchBar(){
        AutoCompleteTextView searchView = binding.searchView;

//        String[] suggerimenti = {"Aula 101", "Aula 102", "Laboratorio 3", "Aula Magna"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
//                android.R.layout.simple_dropdown_item_1line, suggerimenti);
//
//        searchView.setAdapter(adapter);
//        searchView.setThreshold(1);


        // Gestisci invio
        searchView.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchView.getText().toString();
            search(query);
            return true;
        });

        // Gestisci selezione da suggerimenti
        searchView.setOnItemClickListener((parent, itemView, position, id) -> {
            String query = (String) parent.getItemAtPosition(position);
            search(query);
        });

    }
    private void search(String q){
        // Ottieni ora corrente
        SimpleDateFormat sdfOra = new SimpleDateFormat("H", Locale.ITALY);
        sdfOra.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        int oraCorrente = Integer.parseInt(sdfOra.format(new Date()));

        SimpleDateFormat sdfGiorno = new SimpleDateFormat("EEEE", Locale.ITALY);
        sdfGiorno.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        String giornoConAccento = sdfGiorno.format(new Date()).toLowerCase();
        String giorno = giornoConAccento
                .replace("lunedì", "lunedi")
                .replace("martedì", "martedi")
                .replace("mercoledì", "mercoledi")
                .replace("giovedì", "giovedi")
                .replace("venerdì", "venerdi");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() ->{
            //ArrayList<JSONObject> classeDocente = apiHelper.searchDocentiOClasse(q,"lunedi",11);
            ArrayList<JSONObject> classeDocente = apiHelper.searchDocentiOClasse(q,giorno,oraCorrente);
            String txt = "";
            if(classeDocente !=null){
                String room = "";
                for (JSONObject ris: classeDocente) {
                    try{
                        String type = ris.getString("type");
                        JSONObject result = ris.getJSONObject("result");
                        String docente = result.getString("nomeCognome").trim();

                        String classe = result.getString("annoSezione").trim();
                        String indirizzo = result.getString("indirizzo").trim();
                        String lotto = result.getString("area_name").trim();
                        String colorLotto = lotto.equals("l3") ? "arancione" : (lotto.equals("l2") ? "giallo": "rosso");
                        room = result.getString("room_name").trim();
                        Integer piano = null;
                        if (room.length() >= 3) {
                            char lettera = room.charAt(2);
                            if (lettera >= 'a' && lettera <= 'd') {
                                if(!lotto.equals("l3") )
                                    piano = (lettera - 'a')-1; // 'a' = -1, 'b' = 0, etc.
                                else{
                                    piano = (lettera - 'a'); // a = 0 , b = 1
                                }
                            }
                        }
                        if(type.equals("docente")){
                            if(piano != null){
                                txt +="Il prof. "+docente+" si trova nel lotto "+ colorLotto +" al piano "+ piano+" in aula "+ room +"\r\n";
                            }else{
                                txt +="Il prof. "+docente+" si trova nel lotto "+ colorLotto +" in aula "+ room +"\r\n";
                            }

                        }else if ( type.equals("classe")){
                            if(piano != null) {
                                txt += "La classe " + classe + indirizzo + " si trova nel lotto " + colorLotto + " al piano " + piano + " in aula " + room + "\r\n";
                            }else{
                                txt +="La classe "+classe+indirizzo+" si trova nel lotto "+  colorLotto +" in aula "+ room +"\r\n";
                            }
                        }else if( type.equals("room")) {
                            if (piano != null) {
                                txt += "L'aula " + room + " si trova nel lotto " + colorLotto + " al piano " + piano + "\r\n";
                            } else {
                                txt += "L'aula " + room + " si trova nel lotto " + colorLotto + "\r\n";
                            }
                        }

                    } catch (JSONException e) {
                        Log.e("ApiRequest", "Errore nel parsing del JSON: " + e.getMessage());
                    }
                }
                String finalTxt = txt;
                String finalRoom = room;
                handler.post(() ->{
                    binding.textViewRisultato.setText(finalTxt);


                    int id = getResources().getIdentifier(finalRoom, "drawable", requireContext().getPackageName());

                    if (id != 0) {
                        binding.imageView.setImageResource(id);
                    } else {
                        // immagine non trovata
                    }
                });


            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void enforceFullScreen() {
        int flags = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
}
