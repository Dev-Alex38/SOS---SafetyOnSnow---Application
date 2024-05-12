package chocolateam.fr.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import chocolateam.fr.R;
import chocolateam.fr.Utils.ReceiveFallFunction;

public class HistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        Button btnTest = rootView.findViewById(R.id.btn_test);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReceiveFallFunction(getContext()).handleEmergencyNotification();
            }
        });

        return rootView;
    }
}