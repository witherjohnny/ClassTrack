package com.cao.classtrack;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton optionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        optionsButton = findViewById(R.id.optionsButton);  // Initialize the FAB

        // Load the initial fragment
        loadInitialFragment();

        // Set the FAB click listener to show options
        optionsButton.setOnClickListener(v -> showOptionsMenu());
    }

    private void loadInitialFragment() {
        SharedPreferences preferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String scelta = preferences.getString("scelta_bacheca", null);

        Fragment fragment = (scelta == null) ? new ChoiceFragment() :
                ("classe".equals(scelta)) ? new PaginaClasse() : new PaginaPiano();

        try {
            // Replace fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .commit();
        } catch (Exception e) {
            Log.e("MainActivity", "Error loading fragment", e);
        }
    }

    private void showOptionsMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options");
        builder.setMessage("Enter password to reset selection:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String password = input.getText().toString();
            if (password.equals("admin123")) { // Change this password as needed
                resetPreference();
            } else {
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        // Handler per chiudere il dialog dopo 5 secondi di inattività
        android.os.Handler handler = new android.os.Handler();
        Runnable dismissRunnable = () -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        };

        // TextWatcher per rilevare quando l'utente scrive
        input.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(dismissRunnable); // Resetta il timer quando l'utente scrive
                handler.postDelayed(dismissRunnable, 5000); // Riavvia il timer a 5 secondi
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Avvia il timer iniziale (se l'utente non interagisce affatto)
        handler.postDelayed(dismissRunnable, 5000);

        dialog.show();
    }




    private void resetPreference() {
        SharedPreferences preferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("scelta_bacheca");
        editor.apply();
        Toast.makeText(this, "Preference reset", Toast.LENGTH_SHORT).show();

        // Controlla se l'activity è ancora attiva prima di sostituire il fragment
        if (!isFinishing()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new ChoiceFragment())
                    .commitAllowingStateLoss();
        }
    }

}
