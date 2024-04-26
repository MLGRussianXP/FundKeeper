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

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
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
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    HomeFragment homeFragment = new HomeFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    ConverterFragment converterFragment = new ConverterFragment();
    CalculatorFragment calculatorFragment = new CalculatorFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
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