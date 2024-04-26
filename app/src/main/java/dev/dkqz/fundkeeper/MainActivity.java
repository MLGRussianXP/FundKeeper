package dev.dkqz.fundkeeper;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this::onItemSelected);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    private final HomeFragment homeFragment = new HomeFragment();
    private final HistoryFragment historyFragment = new HistoryFragment();
    private final ConverterFragment converterFragment = new ConverterFragment();
    private final CalculatorFragment calculatorFragment = new CalculatorFragment();

    public boolean onItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, homeFragment).commit();
            return true;
        } else if (menuItem.getItemId() == R.id.history) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, historyFragment).commit();
            return true;
        } else if (menuItem.getItemId() == R.id.converter) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, converterFragment).commit();
            return true;
        } else if (menuItem.getItemId() == R.id.calculator) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, calculatorFragment).commit();
            return true;
        }
        return false;
    }
}