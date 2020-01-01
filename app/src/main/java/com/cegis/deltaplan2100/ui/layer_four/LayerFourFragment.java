package com.cegis.deltaplan2100.ui.layer_four;

import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cegis.deltaplan2100.API;
import com.cegis.deltaplan2100.MainActivity;
import com.cegis.deltaplan2100.R;
import com.cegis.deltaplan2100.models.MacroEconIndicatorsList;
import com.cegis.deltaplan2100.utility.FontawesomeLight;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.R.layout.simple_spinner_item;

public class LayerFourFragment extends Fragment {
    private LayerFourViewModel mViewModel;
    private View root;

    private LinearLayout btnLineChart, btnBarChart, btnPieChart, btnTableView;
    private FontawesomeLight txtLineChart, txtBarChart, txtPieChart, txtTableView;
    private Spinner spnrMacroEconIndicator, spnrMacroEconIndicator2;

    private ArrayList<MacroEconIndicatorsList> lstMacroEconIndicators;
    private ArrayList<String> list = new ArrayList<String>();
    String groupHeader, itemContentAs;
    int itemID, itemParentLevel;

    boolean isLineSelected = false, isBarSelected = false, isPieSelected = false, isTableSelected = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(LayerFourViewModel.class);
        root = inflater.inflate(R.layout.fragment_layer_four, container, false);

        //final TextView textView = root.findViewById(R.id.text_gallery);
        //deltaViewModel.getText().observe(this, new Observer<String>() {
        //    @Override
        //    public void onChanged(@Nullable String s) {
        //        textView.setText(s);
        //    }
        //});

        spnrMacroEconIndicator = root.findViewById(R.id.spnrMacroEconIndicator);
        //spnrMacroEconIndicator2 = root.findViewById(R.id.spnrMacroEconIndicator2);

        btnLineChart = root.findViewById(R.id.btnLineChart);
        btnBarChart = root.findViewById(R.id.btnBarChart);
        btnPieChart = root.findViewById(R.id.btnPieChart);
        btnTableView = root.findViewById(R.id.btnTableView);

        txtLineChart = root.findViewById(R.id.txtLineChart);
        txtBarChart = root.findViewById(R.id.txtBarChart);
        txtPieChart = root.findViewById(R.id.txtPieChart);
        txtTableView = root.findViewById(R.id.txtTableView);

        itemID = getArguments().getInt("ItemID");
        groupHeader = getArguments().getString("GroupHeader");
        itemContentAs = getArguments().getString("ItemContentAs");
        itemParentLevel = getArguments().getInt("ItemParentLevel");

        ((MainActivity) getActivity()).setToolBar(groupHeader);

        loadMacroEconIndicatorData(spnrMacroEconIndicator, 1);
        //loadMacroEconIndicatorData(spnrMacroEconIndicator2, 2);

        txtLineChart.setText("\uf201");
        txtBarChart.setText("\uf080");
        txtPieChart.setText("\uf200");
        txtTableView.setText("\uf0ce");

        btnLineChart.setOnClickListener(this::onClick);
        btnBarChart.setOnClickListener(this::onClick);
        btnPieChart.setOnClickListener(this::onClick);
        btnTableView.setOnClickListener(this::onClick);

        return root;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLineChart:
                if (!isLineSelected) {
                    btnLineChart.setBackgroundResource(0);
                    btnLineChart.setBackgroundResource(R.drawable.bg_white_selected);
                    isLineSelected = true;
                } else {
                    btnLineChart.setBackgroundResource(0);
                    btnLineChart.setBackgroundResource(R.drawable.bg_white);
                    isLineSelected = false;
                }
                break;

            case R.id.btnBarChart:
                if (!isBarSelected) {
                    btnBarChart.setBackgroundResource(0);
                    btnBarChart.setBackgroundResource(R.drawable.bg_white_selected);
                    isBarSelected = true;
                } else {
                    btnBarChart.setBackgroundResource(0);
                    btnBarChart.setBackgroundResource(R.drawable.bg_white);
                    isBarSelected = false;
                }
                break;

            case R.id.btnPieChart:
                if (!isPieSelected) {
                    btnPieChart.setBackgroundResource(0);
                    btnPieChart.setBackgroundResource(R.drawable.bg_white_selected);
                    isPieSelected = true;
                } else {
                    btnPieChart.setBackgroundResource(0);
                    btnPieChart.setBackgroundResource(R.drawable.bg_white);
                    isPieSelected = false;
                }
                break;

            case R.id.btnTableView:
                if (!isTableSelected) {
                    btnTableView.setBackgroundResource(0);
                    btnTableView.setBackgroundResource(R.drawable.bg_white_selected);
                    isTableSelected = true;
                } else {
                    btnTableView.setBackgroundResource(0);
                    btnTableView.setBackgroundResource(R.drawable.bg_white);
                    isTableSelected = false;
                }
                break;

            default:
                break;
        }
    }

    private void loadMacroEconIndicatorData(Spinner spinner, int indicator_type) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        OkHttpClient okHttpClient = API.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        API api = retrofit.create(API.class);
        Call call = api.getMacroEconIndicatorList(itemID, itemParentLevel, indicator_type);

        call.enqueue(new Callback<List<MacroEconIndicatorsList>>() {
            @Override
            public void onResponse(Call<List<MacroEconIndicatorsList>> call, Response<List<MacroEconIndicatorsList>> response) {
                if (response.isSuccessful()) {
                    try {
                        List<MacroEconIndicatorsList> responseList = response.body();
                        list = new ArrayList<>();

                        if (responseList.size() > 0) {
                            lstMacroEconIndicators = new ArrayList<>();

                            for (int i = 0; i < responseList.size(); i++) {
                                MacroEconIndicatorsList spinnerModel = new MacroEconIndicatorsList();
                                spinnerModel.setIndicatorName(responseList.get(i).getIndicatorName());
                                spinnerModel.setIndicatorType(responseList.get(i).getIndicatorType());
                                lstMacroEconIndicators.add(spinnerModel);

                                list.add(responseList.get(i).getIndicatorName());
                            }

                            spinner.setAdapter(null);
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), simple_spinner_item, list);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(spinnerArrayAdapter);

                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    String item = (String) parent.getItemAtPosition(position);
                                    ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#FFFFFF"));
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Sorry, no data found!", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MacroEconIndicatorsList>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}