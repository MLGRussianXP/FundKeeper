package fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import adapters.TransactionsAdapter;
import decorations.DividerTransactionDecoration;
import dev.dkqz.fundkeeper.CreateEditTransactionActivity;
import dev.dkqz.fundkeeper.R;
import dev.dkqz.fundkeeper.WelcomeActivity;
import models.Account;
import models.Transaction;


public class HomeFragment extends Fragment {
    private final ArrayList<Transaction> transactions = new ArrayList<>();
    private long balance = 0, todayPlus = 0, todayMinus = 0;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton addTransactionButton = view.findViewById(R.id.btnNewTransaction);
        addTransactionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateEditTransactionActivity.class);
            startActivity(intent);
        });

        // Transactions list

        RecyclerView recyclerView = view.findViewById(R.id.rvTransactions);
        recyclerView.addItemDecoration(new DividerTransactionDecoration(requireContext()));
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

                transactions.sort((t1, t2) -> Long.compare(t2.getDate(), t1.getDate()));

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), getResources().getString(R.string.error_loading_transactions), Toast.LENGTH_LONG).show();
            }
        });

        // Account balance

        TextView tvBalance = view.findViewById(R.id.tvAmount);
        tvBalance.setText(String.valueOf(balance));

        Account.accounts.orderByChild("key").equalTo(WelcomeActivity.accountKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Account account = ds.getValue(Account.class);
                    if (account != null) {
                        balance = account.getBalance();
                        if (balance >= 1e12)
                            tvBalance.setText("∞");
                        else
                            tvBalance.setText(String.valueOf(balance));
                        return;
                    }
                }
                Toast.makeText(getContext(), getResources().getString(R.string.error_loading_bank_account), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), getResources().getString(R.string.error_loading_bank_account), Toast.LENGTH_LONG).show();
            }
        });

        // Today

        TextView tvTodayPlus = view.findViewById(R.id.tvIncome);
        TextView tvTodayMinus = view.findViewById(R.id.tvExpense);

        tvTodayPlus.setText(String.valueOf(todayPlus));
        tvTodayMinus.setText(String.valueOf(todayMinus));

        Transaction.transactions.orderByChild("accountKey").equalTo(WelcomeActivity.accountKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todayPlus = 0;
                todayMinus = 0;
                Calendar calendar = Calendar.getInstance();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Transaction transaction = ds.getValue(Transaction.class);
                    if (transaction == null)
                        continue;

                    calendar.setTimeInMillis(transaction.getDate());
                    if (calendar.get(Calendar.DAY_OF_YEAR) != Calendar.getInstance().get(Calendar.DAY_OF_YEAR) && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR))
                        continue;

                    if (transaction.getType() == Transaction.TransactionType.EXPENSE)
                        todayMinus += transaction.getAmount();
                    else
                        todayPlus += transaction.getAmount();
                }

                tvTodayPlus.setText(String.valueOf(todayPlus));
                tvTodayMinus.setText(String.valueOf(todayMinus));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), getResources().getString(R.string.error_loading_transactions), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}