package com.cao.classtrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChoiceFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choice, container, false);
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
        Button btnClasse = view.findViewById(R.id.btnClasse);
        Button btnPiano = view.findViewById(R.id.btnPiano);

        btnClasse.setOnClickListener(v -> salvaScelta("classe"));
        btnPiano.setOnClickListener(v -> salvaScelta("piano"));
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
                .replace(R.id.fragmentContainerView, nuovoFragment) // Usato il container corretto
                .commit();
    }
}
