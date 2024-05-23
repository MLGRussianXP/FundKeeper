package dev.dkqz.fundkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import models.Account;
import models.Transaction;

public class TransactionActivity extends AppCompatActivity {
    private String transactionKey;
    private Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        transactionKey = getIntent().getStringExtra("transactionKey");

        Transaction.transactions.orderByChild("key").equalTo(transactionKey).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Transaction tr = ds.getValue(Transaction.class);
                    if (tr != null)
                        transaction = tr;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TransactionActivity.this, "Error loading transaction", Toast.LENGTH_SHORT).show();
            }
        });

        // Toolbar

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Edit button

        findViewById(R.id.btnEdit).setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateEditTransactionActivity.class);
            intent.putExtra("transactionKey", transactionKey);
            startActivity(intent);
        });

        // Delete button

        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Delete " + transaction.getTitle() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> Account.accounts.orderByChild("key").equalTo(transaction.getAccountKey()).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Account account = ds.getValue(Account.class);
                                if (account != null) {
                                    if (transaction.getType() == Transaction.TransactionType.EXPENSE)
                                        account.setBalance(account.getBalance() + transaction.getAmount());
                                    else
                                        account.setBalance(account.getBalance() - transaction.getAmount());
                                    Account.accounts.child(account.getKey()).setValue(account);

                                    Transaction.transactions.child(transactionKey).removeValue();
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(TransactionActivity.this, "Error deleting transaction", Toast.LENGTH_SHORT).show();
                        }
                    })).setNegativeButton("No", null);
            builder.show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}