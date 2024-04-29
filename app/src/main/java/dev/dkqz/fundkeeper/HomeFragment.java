package dev.dkqz.fundkeeper;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class HomeFragment extends Fragment {
    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button addTransactionButton = view.findViewById(R.id.btnNewTransaction);
        addTransactionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateEditTransaction.class);
            startActivity(intent);
        });

        return view;
    }
}