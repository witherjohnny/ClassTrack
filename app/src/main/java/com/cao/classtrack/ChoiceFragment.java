package com.cao.classtrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cao.classtrack.classes.ApiHelper;
import com.cao.classtrack.classes.LottoItem;
import com.cao.classtrack.classes.RoomItem;
import com.cao.classtrack.databinding.FragmentChoiceBinding;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChoiceFragment extends Fragment {
    private FragmentChoiceBinding binding;
    private ApiHelper apiHelper;
    private ArrayList<RoomItem> roomSuggestions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChoiceBinding.inflate(inflater, container, false); // Initialize binding
        return binding.getRoot(); // Return the root view from binding
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Controlla se una scelta è già stata effettuata
        SharedPreferences preferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String scelta = preferences.getString("scelta_bacheca", null);

        if (scelta != null) {
            // Se una scelta è già stata fatta, passa direttamente al fragment corrispondente
            vaiAllaBacheca(scelta);
            return;
        }

        // Altrimenti, mostra i bottoni per scegliere
        Button btnConferma = view.findViewById(R.id.btnConferma);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);

        btnConferma.setOnClickListener(v -> {
            // Ottieni l'ID del RadioButton selezionato
            int selectedId = radioGroup.getCheckedRadioButtonId();
            String sceltaRadio = "";

            if (selectedId == R.id.radioBacheca) {
                sceltaRadio = "classe";  // Se il RadioButton "bacheca" è selezionato
            } else if (selectedId == R.id.radioLotto) {
                sceltaRadio = "lotto";    // Se il RadioButton "Lotto" è selezionato
            }

            if (sceltaRadio.isEmpty()) {
                Toast.makeText(getActivity(), "Seleziona un'opzione", Toast.LENGTH_SHORT).show();
                return;
            }

            // Controllo input aula se la scelta è "classe"
            if (sceltaRadio.equals("classe")) {
                String testoInserito = binding.autoCompleteAula.getText().toString().trim();
                boolean trovato = false;

                for (RoomItem item : roomSuggestions) {
                    if (item.name.equalsIgnoreCase(testoInserito)) {
                        trovato = true;

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("aula", item.id);
                        editor.apply();
                        break;
                    }
                }

                if (!trovato) {
                    Toast.makeText(getActivity(), "Aula non valida o non selezionata dalla lista", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            else if (sceltaRadio.equals("lotto")) {
                SharedPreferences.Editor editor = preferences.edit();
                LottoItem lottoSelezionato = (LottoItem) binding.spinnerLotti.getSelectedItem();

                if (lottoSelezionato != null) {
                    editor.putInt("lotto_id", lottoSelezionato.id);
                    editor.putString("lotto_name", lottoSelezionato.name);
                }

                String piano = binding.spinnerPiani.getSelectedItem().toString();
                editor.putString("piano", piano);
                editor.apply();
            }

            salvaScelta(sceltaRadio);
        });
        //codice per settare la visibilita dei campi
        Spinner spinnerLotti = view.findViewById(R.id.spinnerLotti);
        Spinner spinnerPiani = view.findViewById(R.id.spinnerPiani);
        AutoCompleteTextView autoCompleteAula = view.findViewById(R.id.autoCompleteAula);
        autoCompleteAula.setVisibility(View.GONE);
        spinnerLotti.setVisibility(View.GONE);
        spinnerPiani.setVisibility(View.GONE);
        // Gestione visibilità in base alla scelta
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioBacheca) {
                autoCompleteAula.setVisibility(View.VISIBLE);
                spinnerLotti.setVisibility(View.GONE);
                spinnerPiani.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioLotto) {
                autoCompleteAula.setVisibility(View.GONE);
                spinnerLotti.setVisibility(View.VISIBLE);
                spinnerPiani.setVisibility(View.VISIBLE);
            }else{
                autoCompleteAula.setVisibility(View.GONE);
                spinnerLotti.setVisibility(View.GONE);
                spinnerPiani.setVisibility(View.GONE);
            }
        });
        //codice per riempire le opzioni dei campi
        apiHelper = new ApiHelper();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ArrayList<JSONObject> rooms = apiHelper.getLotti();

            try {
                ArrayList<LottoItem> lotti = new ArrayList<>();
                for (int i = 0; i < rooms.size(); i++) {
                    JSONObject obj = rooms.get(i);
                    String name = obj.getString("area_name");
                    int id = obj.getInt("id");
                    lotti.add(new LottoItem(name, id));
                }

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    ArrayAdapter<LottoItem> adapterLotti = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            lotti
                    );
                    adapterLotti.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerLotti.setAdapter(adapterLotti);
                });

            } catch (JSONException e) {
                Log.e("ApiRequest", "Errore nel parsing del JSON: " + e.getMessage());
            }
        });





        String[] opzioniArancio = {"0", "1"};
        String[] opzioniGiallo = {"-1","0","1", "2"};
        String[] opzioniRosso = {"-1","0","1", "2"};

        // Crea l'adapter con le opzioni
        ArrayAdapter<String> adapterPiani = new ArrayAdapter<>(
                requireContext(), // o "this" se sei in Activity
                android.R.layout.simple_spinner_item,
                opzioniGiallo
        );
        // Layout dropdown (opzionale ma consigliato)
        adapterPiani.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Assegna l'adapter allo spinner
        spinnerPiani.setAdapter(adapterPiani);



        executor.execute(() -> {
            ArrayList<JSONObject> rooms = apiHelper.getRooms();

            try {
                roomSuggestions = new ArrayList<>();
                for (int i = 0; i < rooms.size(); i++) {
                    JSONObject obj = rooms.get(i);
                    String name = obj.getString("room_name");
                    int id = obj.getInt("id");
                    roomSuggestions.add(new RoomItem(name, id));
                }

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    ArrayAdapter<RoomItem> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            roomSuggestions
                    );
                    binding.autoCompleteAula.setAdapter(adapter);
                    binding.autoCompleteAula.setThreshold(1);
                });

            } catch (JSONException e) {
                Log.e("ApiRequest", "Errore nel parsing del JSON: " + e.getMessage());
            }
        });
    }


    private void salvaScelta(String scelta) {
        // Salva la scelta dell'utente nelle SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("scelta_bacheca", scelta);
        editor.apply();

        Toast.makeText(getActivity(), "Scelta salvata: " + scelta, Toast.LENGTH_SHORT).show();

        // Passa al fragment corrispondente
        vaiAllaBacheca(scelta);
    }

    private void vaiAllaBacheca(String scelta) {
        Fragment nuovoFragment;
        if ("classe".equals(scelta)) {
            nuovoFragment = new PaginaClasse();
        } else {
            nuovoFragment = new PaginaPiano();
        }

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, nuovoFragment)
                .commitAllowingStateLoss();  // Commit con gestione delle perdite di stato
    }
}
