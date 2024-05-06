package fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.EventListener;
import java.util.Objects;

import dev.dkqz.fundkeeper.R;


public class CalculatorFragment extends Fragment {
    public CalculatorFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);

        EditText etBuyPrice = view.findViewById(R.id.etBuyPrice);
        EditText etBuyAmount = view.findViewById(R.id.etBuyAmount);
        EditText etSellPrice = view.findViewById(R.id.etSellPrice);

        TextView tvProfit = view.findViewById(R.id.tvProfit);
        TextView tvAfterBalance = view.findViewById(R.id.tvAfterBalance);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                double buyPrice;
                double buyAmount;
                double sellPrice;

                try {
                    buyPrice = Double.parseDouble(etBuyPrice.getText().toString());
                }
                catch (NumberFormatException e) {
                    return;
                }

                try {
                    buyAmount = Double.parseDouble(etBuyAmount.getText().toString());
                }
                catch (NumberFormatException e) {
                    return;
                }

                try {
                    sellPrice = Double.parseDouble(etSellPrice.getText().toString());
                }
                catch (NumberFormatException e) {
                    return;
                }

                double afterBalance = Double.parseDouble(String.format("%.2f", buyAmount * sellPrice / buyPrice));
                double profit = Double.parseDouble(String.format("%.2f", afterBalance - buyAmount));

                if (profit < 0)
                    tvProfit.setTextColor(requireContext().getColor(R.color.red));
                else if (profit > 0)
                    tvProfit.setTextColor(requireContext().getColor(R.color.green));
                else
                    tvProfit.setTextColor(requireContext().getColor(R.color.gray));

                tvProfit.setText("Профит: " + profit);
                tvAfterBalance.setText("Итоговый баланс: " + afterBalance);
            }
        };

        etBuyPrice.addTextChangedListener(watcher);
        etBuyAmount.addTextChangedListener(watcher);
        etSellPrice.addTextChangedListener(watcher);

        return view;
    }
}