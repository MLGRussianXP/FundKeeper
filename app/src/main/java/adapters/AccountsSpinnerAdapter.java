package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import models.Account;

public class AccountsSpinnerAdapter extends ArrayAdapter<Account> {
    public AccountsSpinnerAdapter(@NonNull Context context, @NonNull List<Account> objects) {
        super(context, android.R.layout.simple_spinner_dropdown_item, objects);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Account account = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(android.R.layout.simple_spinner_item, null, false);

        TextView tvName = v.findViewById(android.R.id.text1);
        tvName.setText(account.getName());

        return v;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView tvName = (TextView) super.getDropDownView(position, convertView, parent);
        tvName.setText(getItem(position).getName());
        return tvName;
    }
}
