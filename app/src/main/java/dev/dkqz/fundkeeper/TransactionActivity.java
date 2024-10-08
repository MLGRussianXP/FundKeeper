package dev.dkqz.fundkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.Calendar;

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
                    if (tr != null) {
                        transaction = tr;
                        break;
                    }
                }

                ((TextView) findViewById(R.id.tvName)).setText(transaction.getTitle());
                ((TextView) findViewById(R.id.tvDescription)).setText(transaction.getDescription());

                TextView amount = findViewById(R.id.tvAmount);
                amount.setText(getResources().getString(R.string.amount_with_currency, transaction.getAmount()));
                if (transaction.getType() == Transaction.TransactionType.EXPENSE)
                    amount.setTextColor(getColor(R.color.red));
                else
                    amount.setTextColor(getColor(R.color.green));

                ArrayList<String> categories = new ArrayList<>();
                for (Transaction.Category category : transaction.getCategories()) {
                    if (category != null)
                        categories.add(Transaction.getReadableName(TransactionActivity.this, category));
                }
                ((TextView) findViewById(R.id.tvCategories)).setText(getResources().getString(R.string.categories_with_list, String.join(", ", categories)));

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(transaction.getDate());
                ((TextView) findViewById(R.id.tvDatetime)).setText(DateFormat.format("HH:mm, dd.MM.yyyy", calendar));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TransactionActivity.this, getResources().getString(R.string.error_loading_transactions), Toast.LENGTH_LONG).show();
            }
        });

        // Toolbar

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

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
                    .setTitle(getResources().getString(R.string.delete, transaction.getTitle()))
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> Account.accounts.orderByChild("key").equalTo(transaction.getAccountKey()).addListenerForSingleValueEvent(new ValueEventListener() {

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
                            Toast.makeText(TransactionActivity.this, getResources().getString(R.string.error_deleting_transaction), Toast.LENGTH_LONG).show();
                        }
                    })).setNegativeButton(getResources().getString(R.string.no), null);
            builder.show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}