package com.cao.classtrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.database.CursorJoiner;
import android.os.Bundle;
import android.content.Context;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Controlla la scelta salvata
        SharedPreferences preferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String scelta = preferences.getString("scelta_bacheca", null);

        Fragment fragment;
        if (scelta == null) {
            // Nessuna scelta salvata, mostra il ChoiceFragment
            fragment = new ChoiceFragment();
        } else if ("classe".equals(scelta)) {
            // L'utente ha scelto "Bacheca Classe"
            fragment = new PaginaClasse();
        } else {
            // L'utente ha scelto "Bacheca Piano"
            fragment = new PaginaPiano();
        }

        // Carica il fragment selezionato
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .commit();
    }
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
        fragmentTransaction.commit();
    }

}