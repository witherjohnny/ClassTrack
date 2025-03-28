package com.cao.classtrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextWatcher;
import com.cao.classtrack.databinding.FragmentChoiceBinding;
import java.util.List;

public class ChoiceFragment extends Fragment {
    private FragmentChoiceBinding binding;
    private ApiHelper apiHelper;
    private ArrayAdapter<String> adapter;

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

            if (!sceltaRadio.isEmpty()) {
                salvaScelta(sceltaRadio);  // Salva la scelta dell'utente
            } else {
                Toast.makeText(getActivity(), "Seleziona un'opzione", Toast.LENGTH_SHORT).show();
            }
        });

        apiHelper = new ApiHelper();

        // Inizializza l'adapter vuoto
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, new String[]{});
        binding.autoCompletePiano.setAdapter(adapter);

        // Listener per aggiornare i suggerimenti in base all'input
        binding.autoCompletePiano.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSuggestions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateSuggestions(String input) {
        //List<String> suggestions = databaseHelper.getFiltered(input);
        adapter.clear();
        //adapter.addAll(suggestions);
        adapter.notifyDataSetChanged();
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
