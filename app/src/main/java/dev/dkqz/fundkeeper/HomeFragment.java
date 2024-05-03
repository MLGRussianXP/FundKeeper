package dev.dkqz.fundkeeper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapters.TransactionsAdapter;
import models.Transaction;


public class HomeFragment extends Fragment {
    private final ArrayList<Transaction> transactions = new ArrayList<>();

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

        RecyclerView recyclerView = view.findViewById(R.id.rvTransactions);
        TransactionsAdapter adapter = new TransactionsAdapter(getContext(), transactions);
        recyclerView.setAdapter(adapter);

        Transaction.transactions.orderByChild("accountKey").equalTo(WelcomeActivity.accountKey).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactions.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Transaction transaction = ds.getValue(Transaction.class);
                    if (transaction != null)
                        transactions.add(transaction);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading your transactions", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}