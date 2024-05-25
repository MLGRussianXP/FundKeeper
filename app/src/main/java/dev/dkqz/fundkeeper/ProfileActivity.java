package dev.dkqz.fundkeeper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import adapters.AccountsAdapter;
import models.Account;

public class ProfileActivity extends AppCompatActivity {
    private final ArrayList<Account> accounts = new ArrayList<>();
    private int accountsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Toolbar

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Accounts list

        RecyclerView recyclerView = findViewById(R.id.rvAccounts);
        AccountsAdapter adapter = new AccountsAdapter(this, accounts);
        recyclerView.setAdapter(adapter);

        Account.accounts.orderByChild("ownerUid").equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                accounts.clear();
                accountsCount = 0;

                for (DataSnapshot account : snapshot.getChildren()) {
                    Account ac = account.getValue(Account.class);
                    if (ac != null) {
                        accounts.add(ac);
                        accountsCount++;
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error while loading your \"bank\" accounts", Toast.LENGTH_SHORT).show();
            }
        });

        // New account button

        findViewById(R.id.btnNewAccount).setOnClickListener(v -> {
            if (accountsCount >= 4) {
                Toast.makeText(ProfileActivity.this, "You can't have more than 4 accounts", Toast.LENGTH_LONG).show();
                return;
            }

            startActivity(new Intent(this, CreateEditAccountActivity.class));
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}