package adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import dev.dkqz.fundkeeper.R;
import models.Account;

public class AccountsSpinnerAdapter extends ArrayAdapter<Account> {
    public AccountsSpinnerAdapter(@NonNull Context context, @NonNull List<Account> objects) {
        super(context, R.layout.spinner_item, objects);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Account account = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        @SuppressLint("ViewHolder") View v = inflater.inflate(R.layout.spinner_item, parent, false);

        if (account != null) {
            TextView tvName = v.findViewById(R.id.text1);
            tvName.setText(account.getName());
        }

        return v;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView tvName = (TextView) super.getDropDownView(position, convertView, parent);
        Account item = getItem(position);
        if (item != null)
            tvName.setText(item.getName());
        return tvName;
    }
}
