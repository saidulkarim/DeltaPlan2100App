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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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
    private BarChart barChart;
    private PieChart pieChart;

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

        barChart = root.findViewById(R.id.barChart);
        pieChart = root.findViewById(R.id.pieChart);
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
                //String a = "Line Selected: " + isLineSelected;
                //a += "\nBar Selected: " + isBarSelected;
                //a += "\nPie Selected: " + isPieSelected;
                //a += "\n";
                //a += "\nTable Selected: " + is/
                //Toast.makeText(getContext(), a, Toast.LENGTH_LONG).show();

                loadBarChart();
                loadPieChart();
                break;

            default:
                break;
        }
    }

    private void loadBarChart() {
//        ArrayList NoOfEmp = new ArrayList();
//        NoOfEmp.add(new BarEntry(945f, 0));
//        NoOfEmp.add(new BarEntry(1040f, 1));
//        NoOfEmp.add(new BarEntry(1133f, 2));
//        NoOfEmp.add(new BarEntry(1240f, 3));
//        NoOfEmp.add(new BarEntry(1369f, 4));
//        NoOfEmp.add(new BarEntry(1487f, 5));
//        NoOfEmp.add(new BarEntry(1501f, 6));
//        NoOfEmp.add(new BarEntry(1645f, 7));
//        NoOfEmp.add(new BarEntry(1578f, 8));
//        NoOfEmp.add(new BarEntry(1695f, 9));
//
//        ArrayList year = new ArrayList();
//        year.add("2008");
//        year.add("2009");
//        year.add("2010");
//        year.add("2011");
//        year.add("2012");
//        year.add("2013");
//        year.add("2014");
//        year.add("2015");
//        year.add("2016");
//        year.add("2017");
//
//        BarDataSet bardataset = new BarDataSet(NoOfEmp, "No Of Employee");
//        barChart.animateY(5000);
//        BarData data = new BarData(year, bardataset);
//        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
//        barChart.setData(data);

        ////

        BarDataSet barDataSet1 = new BarDataSet(barEntry1(), "DataSet 1");
        barDataSet1.setColor(Color.RED);
        BarDataSet barDataSet2 = new BarDataSet(barEntry2(), "DataSet 2");
        barDataSet2.setColor(Color.GREEN);
        BarDataSet barDataSet3 = new BarDataSet(barEntry3(), "DataSet 3");
        barDataSet3.setColor(Color.BLUE);

        BarData data = new BarData(barDataSet1, barDataSet2, barDataSet3);
        barChart.setData(data);

        String[] days = new String[]{"Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);

        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(3);

        float barSpace = 0.08f;
        float groupSpace = 0.44f;
        data.setBarWidth(0.30f);

        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * 7);
        barChart.getAxisLeft().setAxisMinimum(0);

        barChart.groupBars(0, groupSpace, barSpace);
        barChart.invalidate();
    }

    private void loadPieChart() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(945f, 0));
        entries.add(new PieEntry(1040f, 1));
        entries.add(new PieEntry(1133f, 2));
        entries.add(new PieEntry(1240f, 3));
        entries.add(new PieEntry(1369f, 4));
        entries.add(new PieEntry(1487f, 5));
        entries.add(new PieEntry(1501f, 6));
        entries.add(new PieEntry(1645f, 7));
        entries.add(new PieEntry(1578f, 8));
        entries.add(new PieEntry(1695f, 9));

        PieDataSet set = new PieDataSet(entries, "Election Results");
        PieData data = new PieData(set);
        pieChart.setData(data);
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.animateXY(5000, 5000);
        pieChart.invalidate(); // refresh
    }

    private ArrayList<BarEntry> barEntry1() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        barEntries.add(new BarEntry(1, 900));
        barEntries.add(new BarEntry(2, 631));
        barEntries.add(new BarEntry(3, 1040));
        barEntries.add(new BarEntry(4, 458));
        barEntries.add(new BarEntry(5, 2651));
        barEntries.add(new BarEntry(6, 500));
        barEntries.add(new BarEntry(7, 350));

        return barEntries;
    }

    private ArrayList<BarEntry> barEntry2() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        barEntries.add(new BarEntry(1, 900));
        barEntries.add(new BarEntry(2, 631));
        barEntries.add(new BarEntry(3, 1040));
        barEntries.add(new BarEntry(4, 458));
        barEntries.add(new BarEntry(5, 2651));
        barEntries.add(new BarEntry(6, 500));
        barEntries.add(new BarEntry(7, 350));

        return barEntries;
    }

    private ArrayList<BarEntry> barEntry3() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        barEntries.add(new BarEntry(1, 900));
        barEntries.add(new BarEntry(2, 631));
        barEntries.add(new BarEntry(3, 1040));
        barEntries.add(new BarEntry(4, 458));
        barEntries.add(new BarEntry(5, 2651));
        barEntries.add(new BarEntry(6, 500));
        barEntries.add(new BarEntry(7, 350));

        return barEntries;
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