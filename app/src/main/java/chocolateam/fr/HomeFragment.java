package chocolateam.fr;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    Context context;
    private ArrayList<String> contacts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        context = getContext();

        Button buttonProblem = rootView.findViewById(R.id.bouton_problem);

        buttonProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                problemPopup();
            }
        });

        // Récupérer les contacts de la base de données
        ContactsDatabaseHelper dbHelper = new ContactsDatabaseHelper(getContext());
        contacts = dbHelper.getContactsFromDatabase();

        return rootView;
    }

    private void problemPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_valid_problem, null);
        builder.setView(popupView);
        final AlertDialog problem_popup = builder.create();
        Button buttonClose = popupView.findViewById(R.id.button_close);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                problem_popup.dismiss();
            }
        });
        Button buttonConfirm = popupView.findViewById(R.id.button_confirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                problem_popup.dismiss();
                // Passer les contacts à la méthode
                new EmergencyCallFunction().startCallFunction(0, context, contacts);
            }

        });
        problem_popup.setCancelable(false);
        problem_popup.show();
    }
}