package dev.dkqz.fundkeeper;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapters.AccountsSpinnerAdapter;
import models.Account;
import models.Transaction;

public class CreateEditTransaction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_edit_transaction);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Accounts spinner

        ArrayList<Account> accountArray = new ArrayList<>();
        Account.accounts.orderByChild("ownerUid").equalTo(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Account account = ds.getValue(Account.class);
                    if (account != null)
                        accountArray.add(account);
                }

                if (!accountArray.isEmpty()) {
                    AccountsSpinnerAdapter adapter = new AccountsSpinnerAdapter(CreateEditTransaction.this, accountArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Spinner sItems = findViewById(R.id.spinnerAccount);
                    sItems.setAdapter(adapter);
                }
                else
                    Toast.makeText(CreateEditTransaction.this, "There are no accounts.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateEditTransaction.this, "Error while loading your \"bank\" accounts", Toast.LENGTH_LONG).show();
            }
        });


        // Checkboxes layout

        LinearLayout vlCheckboxes1 = findViewById(R.id.vlCheckboxes1);
        LinearLayout vlCheckboxes2 = findViewById(R.id.vlCheckboxes2);

        int categoriesCount = Transaction.Category.values().length;
        ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        for (int i = 0; i < categoriesCount; i++) {
            CheckBox cb = new CheckBox(this);
            cb.setText(Transaction.Category.values()[i].name());
            checkBoxes.add(cb);

            if (i % 2 == 0)
                vlCheckboxes1.addView(cb);
            else
                vlCheckboxes2.addView(cb);
        }

        Button btnCreate = findViewById(R.id.btnCreateTransaction);
        btnCreate.setOnClickListener(v -> {
            Transaction transaction = new Transaction();
            transaction.setAccountKey(((Account) ((Spinner) findViewById(R.id.spinnerAccount)).getSelectedItem()).getKey());
            transaction.setTitle(((EditText) findViewById(R.id.etName)).getText().toString());

            RadioGroup rg = findViewById(R.id.rgType);
            transaction.setType(rg.getCheckedRadioButtonId() == R.id.rbIncome ? Transaction.TransactionType.INCOME : Transaction.TransactionType.EXPENSE);

            transaction.setDescription(((EditText) findViewById(R.id.etDescription)).getText().toString());

            transaction.setAmount(Long.parseLong(((EditText) findViewById(R.id.etAmount)).getText().toString()));

            ArrayList<Transaction.Category> categories = new ArrayList<>();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isChecked())
                    categories.add(Transaction.Category.values()[i]);
            }

            transaction.setCategories(categories);

            transaction.setDate(System.currentTimeMillis());

            DatabaseReference push = Transaction.transactions.push();
            String transactionKey = push.getKey();
            transaction.setKey(transactionKey);
            push.setValue(transaction);

            finish();
        });
    }
}