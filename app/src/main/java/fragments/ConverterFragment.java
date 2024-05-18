package fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;

import dev.dkqz.fundkeeper.BuildConfig;
import dev.dkqz.fundkeeper.R;


public class ConverterFragment extends Fragment {
    TextView convertFromDropdownTextView, convertToDropdownTextView, conversionRateText;
    EditText amountToConvert;
    ArrayList<String> arrayList;
    Dialog fromDialog, toDialog;
    Button convertButton;
    String convertFromValue, convertToValue, conversionValue;
    String[] country = {
            "AFN", // Afghan Afghani
            "EUR", // Euro
            "ALL", // Albanian Lek
            "DZD", // Algerian Dinar
            "USD", // United States Dollar
            "AOA", // Angolan Kwanza
            "XCD", // East Caribbean Dollar
            "ARS", // Argentine Peso
            "AMD", // Armenian Dram
            "AWG", // Aruban Florin
            "AUD", // Australian Dollar
            "AZN", // Azerbaijani Manat
            "BSD", // Bahamian Dollar
            "BHD", // Bahraini Dinar
            "BDT", // Bangladeshi Taka
            "BBD", // Barbadian Dollar
            "BYN", // Belarusian Ruble
            "BZD", // Belize Dollar
            "XOF", // West African CFA Franc
            "BMD", // Bermudian Dollar
            "BTN", // Bhutanese Ngultrum
            "BOB", // Bolivian Boliviano
            "BAM", // Bosnia-Herzegovina Convertible Mark
            "BWP", // Botswanan Pula
            "BRL", // Brazilian Real
            "BND", // Brunei Dollar
            "BGN", // Bulgarian Lev
            "BIF", // Burundian Franc
            "CVE", // Cape Verdean Escudo
            "KHR", // Cambodian Riel
            "XAF", // Central African CFA Franc
            "CAD", // Canadian Dollar
            "KYD", // Cayman Islands Dollar
            "CLP", // Chilean Peso
            "CNY", // Chinese Yuan
            "COP", // Colombian Peso
            "KMF", // Comorian Franc
            "CDF", // Congolese Franc
            "CRC", // Costa Rican Colón
            "HRK", // Croatian Kuna
            "CUP", // Cuban Peso
            "CZK", // Czech Republic Koruna
            "DKK", // Danish Krone
            "DJF", // Djiboutian Franc
            "DOP", // Dominican Peso
            "EGP", // Egyptian Pound
            "SVC", // Salvadoran Colón
            "ERN", // Eritrean Nakfa
            "SZL", // Swazi Lilangeni
            "ETB", // Ethiopian Birr
            "FKP", // Falkland Islands Pound
            "FJD", // Fijian Dollar
            "GMD", // Gambian Dalasi
            "GEL", // Georgian Lari
            "GHS", // Ghanaian Cedi
            "GIP", // Gibraltar Pound
            "GTQ", // Guatemalan Quetzal
            "GNF", // Guinean Franc
            "GYD", // Guyanaese Dollar
            "HTG", // Haitian Gourde
            "HNL", // Honduran Lempira
            "HKD", // Hong Kong Dollar
            "HUF", // Hungarian Forint
            "ISK", // Icelandic Króna
            "INR", // Indian Rupee
            "IDR", // Indonesian Rupiah
            "IRR", // Iranian Rial
            "IQD", // Iraqi Dinar
            "ILS", // Israeli New Sheqel
            "JMD", // Jamaican Dollar
            "JPY", // Japanese Yen
            "JOD", // Jordanian Dinar
            "KZT", // Kazakhstani Tenge
            "KES", // Kenyan Shilling
            "KWD", // Kuwaiti Dinar
            "KGS", // Kyrgystani Som
            "LAK", // Laotian Kip
            "LBP", // Lebanese Pound
            "LSL", // Lesotho Loti
            "LRD", // Liberian Dollar
            "LYD", // Libyan Dinar
            "MOP", // Macanese Pataca
            "MKD", // Macedonian Denar
            "MGA", // Malagasy Ariary
            "MWK", // Malawian Kwacha
            "MYR", // Malaysian Ringgit
            "MVR", // Maldivian Rufiyaa
            "MRU", // Mauritanian Ouguiya
            "MUR", // Mauritian Rupee
            "MXN", // Mexican Peso
            "MDL", // Moldovan Leu
            "MNT", // Mongolian Tugrik
            "MAD", // Moroccan Dirham
            "MZN", // Mozambican Metical
            "MMK", // Myanma Kyat
            "NAD", // Namibian Dollar
            "NPR", // Nepalese Rupee
            "ANG", // Netherlands Antillean Guilder
            "TWD", // New Taiwan Dollar
            "NZD", // New Zealand Dollar
            "NIO", // Nicaraguan Córdoba
            "NGN", // Nigerian Naira
            "KPW", // North Korean Won
            "NOK", // Norwegian Krone
            "OMR", // Omani Rial
            "PKR", // Pakistani Rupee
            "PAB", // Panamanian Balboa
            "PGK", // Papua New Guinean Kina
            "PYG", // Paraguayan Guarani
            "PEN", // Peruvian Nuevo Sol
            "PHP", // Philippine Peso
            "PLN", // Polish Zloty
            "QAR", // Qatari Rial
            "RON", // Romanian Leu
            "RUB", // Russian Ruble
            "RWF", // Rwandan Franc
            "SHP", // Saint Helena Pound
            "WST", // Samoan Tala
            "STD", // São Tomé and Príncipe Dobra
            "SAR", // Saudi Riyal
            "RSD", // Serbian Dinar
            "SCR", // Seychellois Rupee
            "SLL", // Sierra Leonean Leone
            "SGD", // Singapore Dollar
            "SBD", // Solomon Islands Dollar
            "SOS", // Somali Shilling
            "ZAR", // South African Rand
            "KRW", // South Korean Won
            "SSP", // South Sudanese Pound
            "LKR", // Sri Lankan Rupee
            "SDG", // Sudanese Pound
            "SRD", // Surinamese Dollar
            "SZL", // Swazi Lilangeni
            "SEK", // Swedish Krona
            "CHF", // Swiss Franc
            "SYP", // Syrian Pound
            "TJS", // Tajikistani Somoni
            "TZS", // Tanzanian Shilling
            "THB", // Thai Baht
            "TOP", // Tongan Paʻanga
            "TTD", // Trinidad and Tobago Dollar
            "TND", // Tunisian Dinar
            "TRY", // Turkish Lira
            "TMT", // Turkmenistani Manat
            "UGX", // Ugandan Shilling
            "UAH", // Ukrainian Hryvnia
            "AED", // United Arab Emirates Dirham
            "UYU", // Uruguayan Peso
            "UZS", // Uzbekistan Som
            "VUV", // Vanuatu Vatu
            "VEF", // Venezuelan Bolívar
            "VND", // Vietnamese Dong
            "YER", // Yemeni Rial
            "ZMW", // Zambian Kwacha
            "ZWL"  // Zimbabwean Dollar
    };

    public ConverterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_converter, container, false);

        convertFromDropdownTextView = view.findViewById(R.id.convert_from_dropwdown_menu);
        convertToDropdownTextView = view.findViewById(R.id.convert_to_dropwdown_menu);
        convertButton = view.findViewById(R.id.conversionButton);
        conversionRateText = view.findViewById(R.id.conversionRateText);
        amountToConvert = view.findViewById(R.id.amountToConvertValueEditText);

        arrayList = new ArrayList<>();
        Collections.addAll(arrayList, country);

        convertFromDropdownTextView.setOnClickListener(v -> {
            fromDialog = new Dialog(requireContext());
            fromDialog.setContentView(R.layout.converter_country_spinner);
            fromDialog.getWindow().setLayout(650, 800);
            fromDialog.show();

            EditText editText = fromDialog.findViewById(R.id.edit_text);
            ListView listView = fromDialog.findViewById(R.id.list_view);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(adapter);

            editText.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    adapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            listView.setOnItemClickListener((adapterView, view1, i, l) -> {
                convertFromDropdownTextView.setText(adapter.getItem(i));
                fromDialog.dismiss();
                convertFromValue = adapter.getItem(i);
            });
        });

        convertToDropdownTextView.setOnClickListener(view12 -> {
            toDialog = new Dialog(requireContext());
            toDialog.setContentView(R.layout.converter_country_spinner);
            toDialog.getWindow().setLayout(650, 800);
            toDialog.show();

            EditText editText = toDialog.findViewById(R.id.edit_text);
            ListView listView = toDialog.findViewById(R.id.list_view);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(adapter);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    adapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            listView.setOnItemClickListener((adapterView, view1, i, l) -> {
                convertToDropdownTextView.setText(adapter.getItem(i));
                toDialog.dismiss();
                convertToValue = adapter.getItem(i);
            });
        });

        convertButton.setOnClickListener(view1 -> {
            try {
                Double amountToConvertValue = Double.parseDouble(amountToConvert.getText().toString());
                getConversionRate(convertFromValue, convertToValue, amountToConvertValue);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error getting conversion rate", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void getConversionRate(String conversionFrom, String conversionTo, Double amountToConvert) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = "https://v6.exchangerate-api.com/v6/" + BuildConfig.EXCHANGERATE_KEY + "/pair/" + conversionFrom + "/" + conversionTo;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, s -> {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(s);
                Double conversionRateValue = (Double) jsonObject.get("conversion_rate");
                conversionValue = "" + round((conversionRateValue * amountToConvert), 2);
                conversionRateText.setText(conversionValue);
            }
            catch (Exception e) {
                Toast.makeText(requireContext(), "Error getting conversion rate", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(requireContext(), "Error getting conversion rate", Toast.LENGTH_SHORT).show());

        queue.add(stringRequest);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}