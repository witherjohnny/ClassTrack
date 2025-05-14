package com.cao.classtrack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import androidx.fragment.app.Fragment;

import com.cao.classtrack.classes.ApiHelper;
import com.cao.classtrack.databinding.FragmentPaginaClasseBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.TimeZone;

public class PaginaClasse extends Fragment {
    private FragmentPaginaClasseBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ApiHelper apiHelper = new ApiHelper();
    private final int UPDATE_INTERVAL_MS = 60 * 1000; // 1 minuto
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            aggiornaDati();
            handler.postDelayed(this, UPDATE_INTERVAL_MS);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPaginaClasseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        enforceFullScreen();

        handler.post(updateRunnable);
    }
    private void aggiornaDati() {
        new Thread(() -> {
            // Ottieni i dati dalla API
            SharedPreferences preferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            int ID_aula = preferences.getInt("aula",-1);
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

            JSONObject classeDocente = apiHelper.getClasseEDocenteAttuale(ID_aula,giorno,oraCorrente);
            if(classeDocente != null){
                try {
                    String classe = classeDocente.getString("annoSezione") + " " + classeDocente.getString("indirizzo");
                    String docente = classeDocente.getString("nomeCognome");
                    String docente2 = classeDocente.getString("nomeCognome2");
                    // Aggiorna i TextView nella UI Thread
                    handler.post(() -> {
                        binding.textClasse.setText(classe.trim());
                        binding.textDocente.setText(docente.trim()+"\n\r"+docente2.trim());
                    });
                } catch (JSONException e) {
                    Log.e("ApiRequest", "Errore nel parsing del JSON: " + e.getMessage());
                }
            }else{
                handler.post(() -> {
                    binding.textClasse.setText("CLASSE");
                    binding.textDocente.setText("DOCENTE");
                });
            }

        }).start();
    }

    private String ottieniOraCorrente() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.getDefault());
        return sdf.format(new Date());
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

    @Override
    public void onResume() {
        super.onResume();
        enforceFullScreen();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateRunnable);
        binding = null;
    }
}
