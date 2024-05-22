package fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import adapters.AccountsSpinnerAdapter;
import adapters.TransactionsAdapter;
import decorations.DividerTransactionDecoration;
import dev.dkqz.fundkeeper.CreateEditTransactionActivity;
import dev.dkqz.fundkeeper.ProfileActivity;
import dev.dkqz.fundkeeper.R;
import dev.dkqz.fundkeeper.WelcomeActivity;
import models.Account;
import models.Transaction;

public class AccountFragment extends Fragment {
    SharedPreferences sharedPreferences;
    private Spinner accountSpinner;
    private int currentPosition;

    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        sharedPreferences = requireContext().getSharedPreferences("app", MODE_PRIVATE);

        // Account spinner

        accountSpinner = view.findViewById(R.id.spinnerAccount);

        ArrayList<Account> accountArray = new ArrayList<>();
        Account.accounts.orderByChild("ownerUid").equalTo(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                currentPosition = -1;
                accountArray.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Account account = ds.getValue(Account.class);
                    if (account != null) {
                        accountArray.add(account);
                        if (account.getKey().equals(WelcomeActivity.accountKey))
                            currentPosition = i;
                        i++;
                    }
                }

                if (currentPosition == -1) {
                    Toast.makeText(getContext(), "You don't have this \"bank\" account. Please, reload", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getContext(), WelcomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return;
                }

                if (!accountArray.isEmpty()) {
                    AccountsSpinnerAdapter adapter = new AccountsSpinnerAdapter(requireContext(), accountArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    accountSpinner.setAdapter(adapter);
                    accountSpinner.setSelection(currentPosition);
                    setSpinnerOnClickListener();
                } else
                    Toast.makeText(getContext(), "There are no accounts.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error while loading your \"bank\" accounts", Toast.LENGTH_LONG).show();
            }
        });

        // Profile activity

        view.findViewById(R.id.btnProfile).setOnClickListener(v -> startActivity(new Intent(getContext(), ProfileActivity.class)));

        return view;
    }

    private void setSpinnerOnClickListener() {
        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == currentPosition)
                    return;

                String accountKey = ((Account) parentView.getItemAtPosition(position)).getKey();
                WelcomeActivity.accountKey = accountKey;
                sharedPreferences.edit().putString("accountKey", accountKey).apply();

                Intent intent = new Intent(getContext(), WelcomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }
}