package com.cao.classtrack;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton optionsButton;
    private final Handler autoRestartHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        optionsButton = findViewById(R.id.optionsButton);

        loadInitialFragment();
        optionsButton.setOnClickListener(v -> showOptionsMenu());
    }

    private void loadInitialFragment() {
        SharedPreferences preferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String scelta = preferences.getString("scelta_bacheca", null);

        Fragment fragment = (scelta == null) ? new ChoiceFragment() :
                ("classe".equals(scelta)) ? new PaginaClasse() : new PaginaPiano();

        try {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .commitAllowingStateLoss(); // Use commitAllowingStateLoss to prevent crashes
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
            if (password.equals("admin123")) {
                resetPreference();
            } else {
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        Handler handler = new Handler();
        Runnable dismissRunnable = () -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        };
        input.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(dismissRunnable);
                handler.postDelayed(dismissRunnable, 5000);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
        handler.postDelayed(dismissRunnable, 5000);
        dialog.show();
    }

    private void resetPreference() {
        SharedPreferences preferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("scelta_bacheca");
        editor.apply();
        Toast.makeText(this, "Preference reset", Toast.LENGTH_SHORT).show();

        if (!isFinishing()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new ChoiceFragment())
                    .commitAllowingStateLoss(); // Commit with allowing state loss
        }
    }

    @Override
    public void onBackPressed() {
        // Disables back button
    }
}
