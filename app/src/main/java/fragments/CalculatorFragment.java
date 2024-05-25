package fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;

import dev.dkqz.fundkeeper.R;


public class CalculatorFragment extends Fragment {
    public CalculatorFragment() {
    }

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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                double buyPrice;
                double buyAmount;
                double sellPrice;

                try {
                    buyPrice = Double.parseDouble(etBuyPrice.getText().toString());
                } catch (NumberFormatException e) {
                    return;
                }

                try {
                    buyAmount = Double.parseDouble(etBuyAmount.getText().toString());
                } catch (NumberFormatException e) {
                    return;
                }

                try {
                    sellPrice = Double.parseDouble(etSellPrice.getText().toString());
                } catch (NumberFormatException e) {
                    return;
                }

                double afterBalance = (double) Math.round(buyAmount * sellPrice / buyPrice * 100) / 100;
                double profit = (double) Math.round((afterBalance - buyAmount) * 100) / 100;

                if (profit < 0)
                    tvProfit.setTextColor(requireContext().getColor(R.color.red));
                else if (profit > 0)
                    tvProfit.setTextColor(requireContext().getColor(R.color.green));
                else
                    tvProfit.setTextColor(requireContext().getColor(R.color.gray));

                DecimalFormat df = new DecimalFormat("#.##");
                String profitStr = df.format(profit);
                String afterBalanceStr = df.format(afterBalance);

                if (profit > 1e12)
                    profitStr = "∞";
                if (afterBalance > 1e12)
                    afterBalanceStr = "∞";

                tvProfit.setText(getResources().getString(R.string.profit, profitStr));
                tvAfterBalance.setText(getResources().getString(R.string.final_balance, afterBalanceStr));
            }
        };

        etBuyPrice.addTextChangedListener(watcher);
        etBuyAmount.addTextChangedListener(watcher);
        etSellPrice.addTextChangedListener(watcher);

        return view;
    }
}