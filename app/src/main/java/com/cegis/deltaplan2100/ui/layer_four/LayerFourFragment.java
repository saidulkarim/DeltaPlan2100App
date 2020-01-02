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
import android.widget.Button;
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
    private Button btnViewReport;

    private ArrayList<MacroEconIndicatorsList> lstMacroEconIndicators;
    private ArrayList<String> list = new ArrayList<String>();
    String groupHeader, itemContentAs;
    int itemID, itemParentLevel;

    boolean isChartSelected = false,
            isLineSelected = false,
            isBarSelected = false,
            isPieSelected = false,
            isTableSelected = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(LayerFourViewModel.class);
        root = inflater.inflate(R.layout.fragment_layer_four, container, false);

        //region control getting from view
        spnrMacroEconIndicator = root.findViewById(R.id.spnrMacroEconIndicator);

        btnLineChart = root.findViewById(R.id.btnLineChart);
        btnBarChart = root.findViewById(R.id.btnBarChart);
        btnPieChart = root.findViewById(R.id.btnPieChart);
        btnTableView = root.findViewById(R.id.btnTableView);
        btnViewReport = root.findViewById(R.id.btnViewReport);

        txtLineChart = root.findViewById(R.id.txtLineChart);
        txtBarChart = root.findViewById(R.id.txtBarChart);
        txtPieChart = root.findViewById(R.id.txtPieChart);
        txtTableView = root.findViewById(R.id.txtTableView);
        //endregion

        //region receiving params from called fragment
        itemID = getArguments().getInt("ItemID");
        groupHeader = getArguments().getString("GroupHeader");
        itemContentAs = getArguments().getString("ItemContentAs");
        itemParentLevel = getArguments().getInt("ItemParentLevel");
        //endregion receiving

        //region loading dropdown data
        ((MainActivity) getActivity()).setToolBar(groupHeader);
        loadMacroEconIndicatorData(spnrMacroEconIndicator, 2); //BAU: 1; BDP: 2
        //endregion

        //region set font-awesome icon in textbox
        txtLineChart.setText("\uf201");
        txtBarChart.setText("\uf080");
        txtPieChart.setText("\uf200");
        txtTableView.setText("\uf0ce");
        //endregion set font-awesome

        //region button click event listener
        btnLineChart.setOnClickListener(this::onClick);
        btnBarChart.setOnClickListener(this::onClick);
        btnPieChart.setOnClickListener(this::onClick);
        btnTableView.setOnClickListener(this::onClick);
        btnViewReport.setOnClickListener(this::onClick);
        //endregion

        return root;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLineChart:
                if (!isChartSelected || !isLineSelected) {
                    btnLineChart.setBackgroundResource(0);
                    btnBarChart.setBackgroundResource(R.drawable.bg_white);
                    btnPieChart.setBackgroundResource(R.drawable.bg_white);

                    btnLineChart.setBackgroundResource(R.drawable.bg_white_selected);
                    isChartSelected = isLineSelected = true;
                    isBarSelected = false;
                    isPieSelected = false;
                } else {
                    btnLineChart.setBackgroundResource(0);
                    btnBarChart.setBackgroundResource(R.drawable.bg_white);
                    btnPieChart.setBackgroundResource(R.drawable.bg_white);

                    btnLineChart.setBackgroundResource(R.drawable.bg_white);
                    isChartSelected = isLineSelected = false;
                }
                break;

            case R.id.btnBarChart:
                if (!isChartSelected || !isBarSelected) {
                    btnLineChart.setBackgroundResource(R.drawable.bg_white);
                    btnBarChart.setBackgroundResource(0);
                    btnPieChart.setBackgroundResource(R.drawable.bg_white);

                    btnBarChart.setBackgroundResource(R.drawable.bg_white_selected);
                    isChartSelected = isBarSelected = true;
                    isLineSelected = false;
                    isPieSelected = false;
                } else {
                    btnLineChart.setBackgroundResource(R.drawable.bg_white);
                    btnBarChart.setBackgroundResource(0);
                    btnPieChart.setBackgroundResource(R.drawable.bg_white);

                    btnBarChart.setBackgroundResource(R.drawable.bg_white);
                    isChartSelected = isBarSelected = false;
                }
                break;

            case R.id.btnPieChart:
                if (!isChartSelected || !isPieSelected) {
                    btnLineChart.setBackgroundResource(R.drawable.bg_white);
                    btnBarChart.setBackgroundResource(R.drawable.bg_white);
                    btnPieChart.setBackgroundResource(0);

                    btnPieChart.setBackgroundResource(R.drawable.bg_white_selected);
                    isChartSelected = isPieSelected = true;
                    isBarSelected = false;
                    isLineSelected = false;
                } else {
                    btnLineChart.setBackgroundResource(R.drawable.bg_white);
                    btnBarChart.setBackgroundResource(R.drawable.bg_white);
                    btnPieChart.setBackgroundResource(0);

                    btnPieChart.setBackgroundResource(R.drawable.bg_white);
                    isChartSelected = isPieSelected = false;
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

            case R.id.btnViewReport:
                String a = "Line Selected: " + isLineSelected;
                a += "\nBar Selected: " + isBarSelected;
                a += "\nPie Selected: " + isPieSelected;
                a += "\n";
                a += "\nTable Selected: " + isTableSelected;

                Toast.makeText(getContext(), a, Toast.LENGTH_LONG).show();


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
                                    ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#2F4F4F"));
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