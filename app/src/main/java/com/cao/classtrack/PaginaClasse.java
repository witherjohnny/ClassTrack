package com.cao.classtrack;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.fragment.app.Fragment;
import com.cao.classtrack.databinding.FragmentPaginaClasseBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaginaClasse extends Fragment {
    private FragmentPaginaClasseBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ApiHelper apiHelper = new ApiHelper();
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

        aggiornaDati();
    }
    private void aggiornaDati() {
        new Thread(() -> {
            // Ottieni i dati dalla API
            ArrayList<String> classeDocente = apiHelper.getClasseEDocenteAttuale(101,"lunedi",8);

            String classe = classeDocente.get(1);
            String docente = classeDocente.get(0);

            // Aggiorna i TextView nella UI Thread
            handler.post(() -> {
                binding.textClasse.setText(classe);
                binding.textDocente.setText(docente);
            });
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
        binding = null;
    }
}
