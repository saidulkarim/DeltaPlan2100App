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
import com.cegis.deltaplan2100.models.MacroEconIndicatorPivotData;
import com.cegis.deltaplan2100.models.MacroEconIndicatorsList;
import com.cegis.deltaplan2100.utility.FontawesomeLight;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.R.layout.simple_spinner_item;
import static br.com.zbra.androidlinq.Linq.stream;

public class LayerFourFragment extends Fragment {
    private LayerFourViewModel mViewModel;
    private View root;

    private LinearLayout btnLineChart, btnBarChart, btnPieChart, btnTableView,
            lilaBarChart, lilaPieChartBDP, lilaPieChartBAU;
    private FontawesomeLight txtLineChart, txtBarChart, txtPieChart, txtTableView;
    private Spinner spnrMacroEconIndicator, spnrMacroEconIndicator2;
    private Button btnViewReport;
    private BarChart barChart;
    private PieChart pieChartBDP, pieChartBAU;

    private ArrayList<MacroEconIndicatorsList> lstMacroEconIndicators;
    public List<MacroEconIndicatorPivotData> lstMEIPivotData;

    private ArrayList<String> list = new ArrayList<String>();
    String groupHeader, itemContentAs;
    String[] color = {"#3297BB", "#0C4C6F", "#BB09BB", "#DC4C4E", "#7C7C7C", "#B47C2C", "#0FA18D", "#5A8732", "#04A9E1", "#4169E1", "#5E0B9B", "#42700D"};
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

        lilaBarChart = root.findViewById(R.id.lilaBarChart);
        lilaPieChartBDP = root.findViewById(R.id.lilaPieChartBDP);
        lilaPieChartBAU = root.findViewById(R.id.lilaPieChartBAU);

        txtLineChart = root.findViewById(R.id.txtLineChart);
        txtBarChart = root.findViewById(R.id.txtBarChart);
        txtPieChart = root.findViewById(R.id.txtPieChart);
        txtTableView = root.findViewById(R.id.txtTableView);

        barChart = root.findViewById(R.id.barChart);
        pieChartBDP = root.findViewById(R.id.pieChartBDP);
        pieChartBAU = root.findViewById(R.id.pieChartBAU);

        lilaBarChart.setVisibility(View.GONE);
        lilaPieChartBDP.setVisibility(View.GONE);
        lilaPieChartBAU.setVisibility(View.GONE);
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

                getMEIPivotData();
                break;

            default:
                break;
        }
    }

    public void getMEIPivotData() {
        String indicatorName = spnrMacroEconIndicator.getSelectedItem().toString();

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
        Call call = api.getMacroEconIndiPivotDataList(indicatorName);

        call.enqueue(new Callback<List<MacroEconIndicatorPivotData>>() {
            @Override
            public void onResponse(Call<List<MacroEconIndicatorPivotData>> call, Response<List<MacroEconIndicatorPivotData>> response) {
                lstMEIPivotData = new ArrayList<>();

                if (response.isSuccessful()) {
                    try {
                        List<MacroEconIndicatorPivotData> responseList = response.body();

                        if (responseList.size() > 0) {
                            for (int i = 0; i < responseList.size(); i++) {
                                lstMEIPivotData.add(responseList.get(i));
                            }

                            //String a = "Line Selected: " + isLineSelected;
                            //a += "\nBar Selected: " + isBarSelected;
                            //a += "\nPie Selected: " + isPieSelected;
                            //a += "\n";
                            //a += "\nTable Selected: " + is/
                            //Toast.makeText(getContext(), a, Toast.LENGTH_LONG).show();

                            if (isLineSelected) {
                                barChart.setData(null);
                                lilaBarChart.setVisibility(View.GONE);
                                pieChartBDP.setData(null);
                                lilaPieChartBDP.setVisibility(View.GONE);
                                pieChartBAU.setData(null);
                                lilaPieChartBAU.setVisibility(View.GONE);

                                // line
                            } else if (isBarSelected) {
                                lilaBarChart.setVisibility(View.VISIBLE);

                                pieChartBDP.setData(null);
                                lilaPieChartBDP.setVisibility(View.GONE);
                                pieChartBAU.setData(null);
                                lilaPieChartBAU.setVisibility(View.GONE);

                                loadBarChart(indicatorName, lstMEIPivotData);
                            } else if (isPieSelected) {
                                lilaPieChartBDP.setVisibility(View.VISIBLE);
                                lilaPieChartBAU.setVisibility(View.VISIBLE);

                                barChart.setData(null);
                                lilaBarChart.setVisibility(View.GONE);

                                loadPieChart(indicatorName, lstMEIPivotData);
                            }
                        } else {
                            lstMEIPivotData = new ArrayList<>();
                            Toast.makeText(getContext(), "Sorry, no data found!", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        lstMEIPivotData = new ArrayList<>();
                        e.printStackTrace();
                    }
                } else {
                    lstMEIPivotData = new ArrayList<>();
                    Toast.makeText(getContext(), "Response not found.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<MacroEconIndicatorPivotData>> call, Throwable t) {
                lstMEIPivotData = new ArrayList<>();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadBarChart(String indicatorName, List<MacroEconIndicatorPivotData> lstMEIPivotData) {
        BarData data = new BarData();
        BarDataSet barDataSet;
        int groupCount = 0;
        //ArrayList<String> labels = new ArrayList<>();
        String[] xAxisLabel = {"2016", "2020", "2021", "2025", "2026", "2030", "2031", "2036", "2040", "2041"};

        if (lstMEIPivotData.size() > 0) {
            groupCount = xAxisLabel.length;
            for (int i = 0; i < lstMEIPivotData.size(); i++) {
                ArrayList<BarEntry> barEntries = new ArrayList<>();

                barEntries.add(new BarEntry(1, Float.parseFloat(lstMEIPivotData.get(i).getFY2016().toString())));
                barEntries.add(new BarEntry(2, Float.parseFloat(lstMEIPivotData.get(i).getFY2020().toString())));
                barEntries.add(new BarEntry(3, Float.parseFloat(lstMEIPivotData.get(i).getFY2021().toString())));
                barEntries.add(new BarEntry(4, Float.parseFloat(lstMEIPivotData.get(i).getFY2025().toString())));
                barEntries.add(new BarEntry(5, Float.parseFloat(lstMEIPivotData.get(i).getFY2026().toString())));
                barEntries.add(new BarEntry(6, Float.parseFloat(lstMEIPivotData.get(i).getFY2030().toString())));
                barEntries.add(new BarEntry(7, Float.parseFloat(lstMEIPivotData.get(i).getFY2031().toString())));
                barEntries.add(new BarEntry(1, Float.parseFloat(lstMEIPivotData.get(i).getFY2016().toString())));
                barEntries.add(new BarEntry(8, Float.parseFloat(lstMEIPivotData.get(i).getFY2035().toString())));
                barEntries.add(new BarEntry(9, Float.parseFloat(lstMEIPivotData.get(i).getFY2036().toString())));
                barEntries.add(new BarEntry(10, Float.parseFloat(lstMEIPivotData.get(i).getFY2040().toString())));
                barEntries.add(new BarEntry(11, Float.parseFloat(lstMEIPivotData.get(i).getFY2041().toString())));

                String groupName = lstMEIPivotData.get(i).getIndicatorType();
                //String labelName = lstMEIPivotData.get(i).getFY2016().getClass().getName();
                //labels.add(labelName);

                barDataSet = new BarDataSet(barEntries, groupName);
                //Toast.makeText(getContext(), labelName, Toast.LENGTH_LONG).show();
                //barDataSet.setColor(chart_color[i + 1]);
                barDataSet.setColors(Color.parseColor(color[i]));

                data.addDataSet(barDataSet);
                //data.setBarWidth(0.30f);
            }

            barChart.setData(data);
        }

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);

        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(3);
        barChart.getXAxis().setDrawGridLines(true);
        barChart.getAxisLeft().setDrawGridLines(true);
        barChart.getAxisRight().setDrawGridLines(false);

        //data
        //float groupSpace = 0.04f;
        //float barSpace = 0.02f;
        //float barWidth = 0.46f;
        // (0.46 + 0.02) * 2 + 0.04 = 1.00 -> interval per "group"

        float groupSpace = 0.04f;
        float barSpace = 0.12f;
        float barWidth = 0.36f;
        data.setBarWidth(barWidth);

        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        barChart.getAxisLeft().setAxisMinimum(0);

        barChart.groupBars(0, groupSpace, barSpace);
        barChart.getDescription().setText(indicatorName);
        barChart.animateXY(2000, 2000);
        barChart.invalidate();
    }

    private void loadPieChart(String indicatorName, List<MacroEconIndicatorPivotData> lstMEIPivotData) {
        MacroEconIndicatorPivotData bdpMEI;

        if (lstMEIPivotData.size() > 0) {
            //region BDP
            bdpMEI = stream(lstMEIPivotData).where(x -> x.getIndicatorType().equals("BDP")).first();

            List<PieEntry> entriesBDP = new ArrayList<>();
            entriesBDP.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2016().toString()), 0));
            entriesBDP.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2020().toString()), 1));
            entriesBDP.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2021().toString()), 2));
            entriesBDP.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2025().toString()), 3));
            entriesBDP.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2026().toString()), 4));
            entriesBDP.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2030().toString()), 5));
            entriesBDP.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2031().toString()), 6));
            entriesBDP.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2036().toString()), 7));
            entriesBDP.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2040().toString()), 8));
            entriesBDP.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2041().toString()), 9));

            PieDataSet set = new PieDataSet(entriesBDP, indicatorName);
            PieData data = new PieData(set);
            pieChartBDP.setData(data);
            set.setColors(ColorTemplate.COLORFUL_COLORS);
            pieChartBDP.getDescription().setText("BDP: " + indicatorName);
            pieChartBDP.animateXY(5000, 5000);
            pieChartBDP.invalidate();
            //endregion

            //region BAU
            bdpMEI = stream(lstMEIPivotData).where(x -> x.getIndicatorType().equals("BAU")).first();

            List<PieEntry> entriesBAU = new ArrayList<>();
            entriesBAU.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2016().toString()), 0));
            entriesBAU.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2020().toString()), 1));
            entriesBAU.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2021().toString()), 2));
            entriesBAU.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2025().toString()), 3));
            entriesBAU.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2026().toString()), 4));
            entriesBAU.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2030().toString()), 5));
            entriesBAU.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2031().toString()), 6));
            entriesBAU.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2036().toString()), 7));
            entriesBAU.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2040().toString()), 8));
            entriesBAU.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2041().toString()), 9));

            PieDataSet setBau = new PieDataSet(entriesBAU, indicatorName);
            PieData dataBau = new PieData(setBau);
            pieChartBAU.setData(dataBau);
            setBau.setColors(ColorTemplate.COLORFUL_COLORS);
            pieChartBAU.getDescription().setText("BAU: " + indicatorName);
            pieChartBAU.animateXY(5000, 5000);
            pieChartBAU.invalidate();
            //endregion
        }
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