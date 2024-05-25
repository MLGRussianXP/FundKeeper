package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapters.HistoryTransactionsAdapter;
import decorations.DividerTransactionDecoration;
import dev.dkqz.fundkeeper.R;
import dev.dkqz.fundkeeper.SearchActivity;
import dev.dkqz.fundkeeper.WelcomeActivity;
import models.Transaction;


public class HistoryFragment extends Fragment {
    private final ArrayList<Transaction> transactions = new ArrayList<>();
    HistoryTransactionsAdapter adapter;

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Transactions list

        RecyclerView recyclerView = view.findViewById(R.id.rvTransactions);
        recyclerView.addItemDecoration(new DividerTransactionDecoration(requireContext()));

        adapter = new HistoryTransactionsAdapter(getContext(), transactions, HistoryTransactionsAdapter.Period.DAY, isEmpty -> {
            if (isEmpty)
                view.findViewById(R.id.tvEmpty).setVisibility(View.VISIBLE);
            else
                view.findViewById(R.id.tvEmpty).setVisibility(View.GONE);
        });
        recyclerView.setAdapter(adapter);

        Transaction.transactions.orderByChild("accountKey").equalTo(WelcomeActivity.accountKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactions.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Transaction transaction = ds.getValue(Transaction.class);
                    if (transaction != null)
                        transactions.add(transaction);
                }

                transactions.sort((t1, t2) -> Long.compare(t2.getDate(), t1.getDate()));

                adapter.filterTransactionsAndNotify();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), getResources().getString(R.string.error_loading_transactions), Toast.LENGTH_LONG).show();
            }
        });

        // Radio buttons

        view.findViewById(R.id.rbDay).setOnClickListener(v -> adapter.setPeriod(HistoryTransactionsAdapter.Period.DAY));
        view.findViewById(R.id.rbWeek).setOnClickListener(v -> adapter.setPeriod(HistoryTransactionsAdapter.Period.WEEK));
        view.findViewById(R.id.rbMonth).setOnClickListener(v -> adapter.setPeriod(HistoryTransactionsAdapter.Period.MONTH));
        view.findViewById(R.id.rbYear).setOnClickListener(v -> adapter.setPeriod(HistoryTransactionsAdapter.Period.YEAR));

        // Search button

        view.findViewById(R.id.btnSearch).setOnClickListener(v -> requireContext().startActivity(new Intent(requireContext(), SearchActivity.class)));

        return view;
    }
}