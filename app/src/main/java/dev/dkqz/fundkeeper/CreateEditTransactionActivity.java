package dev.dkqz.fundkeeper;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.Calendar;

import adapters.AccountsSpinnerAdapter;
import models.Account;
import models.Transaction;

public class CreateEditTransactionActivity extends AppCompatActivity {
    private Calendar calendar;

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
                int i = 0, selectedPosition = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Account account = ds.getValue(Account.class);
                    if (account != null) {
                        accountArray.add(account);
                        if (account.getKey().equals(WelcomeActivity.accountKey))
                            selectedPosition = i;
                        i++;
                    }
                }

                if (!accountArray.isEmpty()) {
                    AccountsSpinnerAdapter adapter = new AccountsSpinnerAdapter(CreateEditTransactionActivity.this, accountArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Spinner sItems = findViewById(R.id.spinnerAccount);
                    sItems.setAdapter(adapter);
                    sItems.setSelection(selectedPosition);
                } else
                    Toast.makeText(CreateEditTransactionActivity.this, "There are no accounts.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateEditTransactionActivity.this, "Error while loading your \"bank\" accounts", Toast.LENGTH_LONG).show();
            }
        });


        // Checkboxes layout

        LinearLayout vlCheckboxes1 = findViewById(R.id.vlCheckboxes1);
        LinearLayout vlCheckboxes2 = findViewById(R.id.vlCheckboxes2);

        int categoriesCount = Transaction.Category.values().length;
        ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        for (int i = 0; i < categoriesCount; i++) {
            CheckBox cb = new CheckBox(this);
            cb.setText(Transaction.getReadableName(this, Transaction.Category.values()[i]));
            cb.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.main, getTheme())));

            checkBoxes.add(cb);

            if (i % 2 == 0)
                vlCheckboxes1.addView(cb);
            else
                vlCheckboxes2.addView(cb);
        }

        // Date and time

        Button btnDatePicker = findViewById(R.id.btnDate);
        Button btnTimePicker = findViewById(R.id.btnTime);
        EditText etDate = findViewById(R.id.etDate);
        EditText etTime = findViewById(R.id.etTime);

        btnDatePicker.setOnClickListener(v -> {
            if (calendar == null)
                calendar = Calendar.getInstance();
            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        etDate.setText(String.format("%02d-%02d-%d", dayOfMonth, monthOfYear + 1, year));
                        calendar.set(year, monthOfYear, dayOfMonth);
                    },
                    mYear, mMonth, mDay
            );
            datePickerDialog.show();
        });

        btnTimePicker.setOnClickListener(v -> {
            if (calendar == null)
                calendar = Calendar.getInstance();
            int mHour = calendar.get(Calendar.HOUR_OF_DAY);
            int mMinute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {
                        etTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);
                    },
                    mHour, mMinute, true
            );
            timePickerDialog.show();
        });

        // Create

        Button btnCreate = findViewById(R.id.btnCreateTransaction);
        btnCreate.setOnClickListener(v -> {
            Transaction transaction = new Transaction();

            String accountKey = ((Account) ((Spinner) findViewById(R.id.spinnerAccount)).getSelectedItem()).getKey();
            transaction.setAccountKey(accountKey);
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

            if (calendar == null)
                calendar = Calendar.getInstance();
            transaction.setDate(calendar.getTimeInMillis());

            Account.accounts.orderByChild("key").equalTo(accountKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Account account = ds.getValue(Account.class);
                        if (account != null) {
                            long balanceDiff = transaction.getAmount();
                            if (transaction.getType() == Transaction.TransactionType.EXPENSE)
                                balanceDiff *= -1;
                            snapshot.getRef().child(accountKey + "/balance").setValue(account.getBalance() + balanceDiff);

                            DatabaseReference push = Transaction.transactions.push();
                            String transactionKey = push.getKey();
                            transaction.setKey(transactionKey);
                            push.setValue(transaction);

                            finish();
                            return;
                        }
                    }
                    Toast.makeText(CreateEditTransactionActivity.this, "Error while fetching selected \"bank\" account", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CreateEditTransactionActivity.this, "Error while fetching selected \"bank\" account", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}