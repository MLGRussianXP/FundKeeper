package dev.dkqz.fundkeeper;

import android.os.Bundle;
import android.text.Layout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

        ArrayList<String> accountArray = new ArrayList<>();
        Account.accounts.orderByChild("ownerUid").equalTo(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Account account = ds.getValue(Account.class);
                    if (account != null)
                        accountArray.add(account.getName());
                }

                if (!accountArray.isEmpty()) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateEditTransaction.this, android.R.layout.simple_spinner_item, accountArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Spinner sItems = findViewById(R.id.spinnerAccount);
                    sItems.setAdapter(adapter);
                }
                else
                    Toast.makeText(CreateEditTransaction.this, "Error while loading your \"bank\" accounts. There are no accounts.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateEditTransaction.this, "Error while loading your \"bank\" accounts", Toast.LENGTH_LONG).show();
            }
        });


        // Checkboxes layout

        LinearLayout vlCheckboxes1 = findViewById(R.id.vlCheckboxes1);
        LinearLayout vlCheckboxes2 = findViewById(R.id.vlCheckboxes2);

        int i = 0;
        for (Transaction.Category category : Transaction.Category.values()) {
            CheckBox cb = new CheckBox(this);
            cb.setText(category.toString());
            if (i % 2 == 0)
                vlCheckboxes1.addView(cb);
            else
                vlCheckboxes2.addView(cb);
            i++;
        }

        Button btnCreate = findViewById(R.id.btnCreateTransaction);
        btnCreate.setOnClickListener(v -> {

        });
    }
}