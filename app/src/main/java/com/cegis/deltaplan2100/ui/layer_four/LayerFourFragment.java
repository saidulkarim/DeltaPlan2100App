package com.cegis.deltaplan2100.ui.layer_four;

import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
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
import static androidx.constraintlayout.widget.Constraints.TAG;
import static br.com.zbra.androidlinq.Linq.stream;

public class LayerFourFragment extends Fragment {
    private LayerFourViewModel mViewModel;
    private View root;

    private LinearLayout btnLineChart, btnBarChart, btnPieChart, btnTableView,
            lilaLineChart, lilaBarChart, lilaPieChartBDP, lilaPieChartBAU, lilaTableContent;
    private FontawesomeLight txtLineChart, txtBarChart, txtPieChart, txtTableView;
    private Spinner spnrMacroEconIndicator, spnrMacroEconIndicator2;
    private Button btnViewReport;

    private LineChart lineChart;
    private BarChart barChart;
    private PieChart pieChartBDP, pieChartBAU;
    private WebView webViewTblContent;

    private ArrayList<MacroEconIndicatorsList> lstMacroEconIndicators;
    public List<MacroEconIndicatorPivotData> lstMEIPivotData;

    private ArrayList<String> list = new ArrayList<String>();
    String groupHeader, itemContentAs;
    List<String> fiscalYearList;
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

        lilaLineChart = root.findViewById(R.id.lilaLineChart);
        lilaBarChart = root.findViewById(R.id.lilaBarChart);
        lilaPieChartBDP = root.findViewById(R.id.lilaPieChartBDP);
        lilaPieChartBAU = root.findViewById(R.id.lilaPieChartBAU);
        lilaTableContent = root.findViewById(R.id.lilaTableContent);

        txtLineChart = root.findViewById(R.id.txtLineChart);
        txtBarChart = root.findViewById(R.id.txtBarChart);
        txtPieChart = root.findViewById(R.id.txtPieChart);
        txtTableView = root.findViewById(R.id.txtTableView);

        lineChart = root.findViewById(R.id.lineChart);
        barChart = root.findViewById(R.id.barChart);
        pieChartBDP = root.findViewById(R.id.pieChartBDP);
        pieChartBAU = root.findViewById(R.id.pieChartBAU);
        webViewTblContent = root.findViewById(R.id.webViewTblContent);
        webViewTblContent.getSettings().setJavaScriptEnabled(true);
        webViewTblContent.setVerticalScrollBarEnabled(true);
        webViewTblContent.setHorizontalScrollBarEnabled(true);

        lilaLineChart.setVisibility(View.GONE);
        lilaBarChart.setVisibility(View.GONE);
        lilaPieChartBDP.setVisibility(View.GONE);
        lilaPieChartBAU.setVisibility(View.GONE);
        lilaTableContent.setVisibility(View.GONE);
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
        getFiscalYearList();
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
                if (isLineSelected || isBarSelected || isPieSelected || isTableSelected) {
                    getMEIPivotData();
                } else {
                    Toast.makeText(getContext(), "Please select data view", Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
    }

    public void getFiscalYearList() {
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
        Call call = api.getFiscalYearList();

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    try {
                        fiscalYearList = response.body();
                    } catch (Exception e) {
                        fiscalYearList = new ArrayList<>();
                        e.printStackTrace();
                    }
                } else {
                    fiscalYearList = new ArrayList<>();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                fiscalYearList = new ArrayList<>();
            }
        });
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

                            if (isLineSelected) {
                                lilaLineChart.setVisibility(View.VISIBLE);

                                barChart.setData(null);
                                lilaBarChart.setVisibility(View.GONE);
                                pieChartBDP.setData(null);
                                lilaPieChartBDP.setVisibility(View.GONE);
                                pieChartBAU.setData(null);
                                lilaPieChartBAU.setVisibility(View.GONE);

                                loadLineChart(indicatorName, lstMEIPivotData);
                            } else if (isBarSelected) {
                                lilaBarChart.setVisibility(View.VISIBLE);

                                lineChart.setData(null);
                                lilaLineChart.setVisibility(View.GONE);
                                pieChartBDP.setData(null);
                                lilaPieChartBDP.setVisibility(View.GONE);
                                pieChartBAU.setData(null);
                                lilaPieChartBAU.setVisibility(View.GONE);

                                loadBarChart(indicatorName, lstMEIPivotData);
                            } else if (isPieSelected) {
                                lilaPieChartBDP.setVisibility(View.VISIBLE);
                                lilaPieChartBAU.setVisibility(View.VISIBLE);

                                lineChart.setData(null);
                                lilaLineChart.setVisibility(View.GONE);
                                barChart.setData(null);
                                lilaBarChart.setVisibility(View.GONE);

                                loadPieChart(indicatorName, lstMEIPivotData);
                            }

                            if (isTableSelected) {
                                lilaTableContent.setVisibility(View.VISIBLE);
                                loadTableData(indicatorName, lstMEIPivotData);

                                if (isLineSelected) {
                                    lilaLineChart.setVisibility(View.VISIBLE);
                                } else {
                                    lilaLineChart.setVisibility(View.GONE);
                                }

                                if (isBarSelected) {
                                    lilaBarChart.setVisibility(View.VISIBLE);
                                } else {
                                    lilaBarChart.setVisibility(View.GONE);
                                }

                                if (isPieSelected) {
                                    lilaPieChartBAU.setVisibility(View.VISIBLE);
                                    lilaPieChartBDP.setVisibility(View.VISIBLE);
                                } else {
                                    lilaPieChartBAU.setVisibility(View.GONE);
                                    lilaPieChartBDP.setVisibility(View.GONE);
                                }
                            } else {
                                lilaTableContent.setVisibility(View.GONE);
                                webViewTblContent.loadDataWithBaseURL(null, "", "text/HTML", "UTF-8", null);
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

    private void loadLineChart(String indicatorName, List<MacroEconIndicatorPivotData> lstMEIPivotData) {
        //region line chart initial config
//        LimitLine llXAxis = new LimitLine(10f, "Index 10");
//        llXAxis.setLineWidth(2f);
//        llXAxis.enableDashedLine(10f, 10f, 0f);
//        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
//        llXAxis.setTextSize(10f);
//
        XAxis xAxis = lineChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMaximum(10f);
        xAxis.setAxisMinimum(0f);
        xAxis.setDrawLimitLinesBehindData(true);
//
//        LimitLine ll1 = new LimitLine(215f, "Maximum Limit");
//        ll1.setLineWidth(4f);
//        ll1.enableDashedLine(10f, 10f, 0f);
//        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
//        ll1.setTextSize(10f);
//
//        LimitLine ll2 = new LimitLine(70f, "Minimum Limit");
//        ll2.setLineWidth(4f);
//        ll2.enableDashedLine(10f, 10f, 0f);
//        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
//        ll2.setTextSize(10f);
//
//        YAxis leftAxis = lineChart.getAxisLeft();
//        leftAxis.removeAllLimitLines();
//        leftAxis.addLimitLine(ll1);
//        leftAxis.addLimitLine(ll2);
//        leftAxis.setAxisMaximum(350f);
//        leftAxis.setAxisMinimum(0f);
//        leftAxis.enableGridDashedLine(10f, 10f, 0f);
//        leftAxis.setDrawZeroLine(false);
//        leftAxis.setDrawLimitLinesBehindData(false);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setText(indicatorName);
        //endregion

        //region set data
        List<ILineDataSet> dataSets = new ArrayList<>();

//        List<Entry> sinEntries = new ArrayList<>();
//        List<Entry> cosEntries = new ArrayList<>();

        if (lstMEIPivotData.size() > 0) {
            for (int i = 0; i < lstMEIPivotData.size(); i++) {
                ArrayList<Entry> lineEntries = new ArrayList<>();

                lineEntries.add(new Entry(1, Float.parseFloat(lstMEIPivotData.get(i).getFY2016().toString())));
                lineEntries.add(new Entry(2, Float.parseFloat(lstMEIPivotData.get(i).getFY2020().toString())));
                lineEntries.add(new Entry(3, Float.parseFloat(lstMEIPivotData.get(i).getFY2021().toString())));
                lineEntries.add(new Entry(4, Float.parseFloat(lstMEIPivotData.get(i).getFY2025().toString())));
                lineEntries.add(new Entry(5, Float.parseFloat(lstMEIPivotData.get(i).getFY2026().toString())));
                lineEntries.add(new Entry(6, Float.parseFloat(lstMEIPivotData.get(i).getFY2030().toString())));
                lineEntries.add(new Entry(7, Float.parseFloat(lstMEIPivotData.get(i).getFY2031().toString())));
                lineEntries.add(new Entry(8, Float.parseFloat(lstMEIPivotData.get(i).getFY2035().toString())));
                lineEntries.add(new Entry(9, Float.parseFloat(lstMEIPivotData.get(i).getFY2036().toString())));
                lineEntries.add(new Entry(10, Float.parseFloat(lstMEIPivotData.get(i).getFY2040().toString())));
                lineEntries.add(new Entry(11, Float.parseFloat(lstMEIPivotData.get(i).getFY2041().toString())));

                LineDataSet lineDataSet = new LineDataSet(lineEntries, lstMEIPivotData.get(i).getIndicatorType());
                dataSets.add(lineDataSet);

                lineDataSet.setDrawIcons(false);
                lineDataSet.enableDashedLine(10f, 5f, 0f);
                lineDataSet.enableDashedHighlightLine(10f, 5f, 0f);
                lineDataSet.setColor(Color.parseColor(color[i]));
                lineDataSet.setCircleColor(Color.parseColor(color[i]));
                lineDataSet.setLineWidth(1f);
                lineDataSet.setCircleRadius(3f);
                lineDataSet.setDrawCircleHole(false);
                lineDataSet.setValueTextSize(9f);
                lineDataSet.setDrawFilled(true);
                lineDataSet.setFormLineWidth(1f);
                lineDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                lineDataSet.setFormSize(15.f);
            }
        }

//        for (float i = 0; i < 10; i++) {
//            sinEntries.add(new Entry(i, i * 9F));
//            cosEntries.add(new Entry(i, i * 4.5F));
//        }
//
//        LineDataSet sinSet = new LineDataSet(sinEntries, "sin curve");
//        LineDataSet cosSet = new LineDataSet(cosEntries, "cos curve");
//
//        // Adding each plot data to a List
//        dataSets.add(sinSet);
//        dataSets.add(cosSet);
//
//        sinSet.setDrawIcons(false);
//        cosSet.setDrawIcons(false);
//
//        sinSet.enableDashedLine(10f, 5f, 0f);
//        cosSet.enableDashedLine(10f, 5f, 0f);
//
//        sinSet.enableDashedHighlightLine(10f, 5f, 0f);
//        cosSet.enableDashedHighlightLine(10f, 5f, 0f);
//
//        sinSet.setColor(Color.GREEN);
//        cosSet.setColor(Color.BLUE);
//
//        sinSet.setCircleColor(Color.GREEN);
//        cosSet.setCircleColor(Color.BLUE);
//
//        sinSet.setLineWidth(1f);
//        cosSet.setLineWidth(1f);
//
//        sinSet.setCircleRadius(3f);
//        cosSet.setCircleRadius(3f);
//
//        sinSet.setDrawCircleHole(false);
//        cosSet.setDrawCircleHole(false);
//
//        sinSet.setValueTextSize(9f);
//        cosSet.setValueTextSize(9f);
//
//        sinSet.setDrawFilled(true);
//        cosSet.setDrawFilled(true);
//
//        sinSet.setFormLineWidth(1f);
//        cosSet.setFormLineWidth(1f);
//
//        sinSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
//        cosSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
//
//        sinSet.setFormSize(15.f);
//        cosSet.setFormSize(15.f);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();
        //endregion
    }

    private void loadBarChart(String indicatorName, List<MacroEconIndicatorPivotData> lstMEIPivotData) {
        BarData data = new BarData();
        BarDataSet barDataSet;
        int groupCount = 0;
        String[] xAxisLabel = fiscalYearList.toArray(new String[fiscalYearList.size()]);

        if (lstMEIPivotData.size() > 0) {
            groupCount = xAxisLabel.length;
            for (int i = 0; i < lstMEIPivotData.size(); i++) {
                ArrayList<BarEntry> barEntries = new ArrayList<>();

                for (int j = 0; j < xAxisLabel.length; j++) {
                    String val = getFieldNamesAndValues(xAxisLabel[j], lstMEIPivotData.get(i));
                    barEntries.add(new BarEntry(j + 1, Float.parseFloat(val)));
                }

                String groupName = lstMEIPivotData.get(i).getIndicatorType();
                barDataSet = new BarDataSet(barEntries, groupName);
                barDataSet.setColors(Color.parseColor(color[i]));
                data.addDataSet(barDataSet);
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

        //measurement data
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
        barChart.animateXY(1000, 1000);
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
            entriesBDP.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2035().toString()), 7));
            entriesBDP.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2036().toString()), 8));
            entriesBDP.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2040().toString()), 9));
            entriesBDP.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2041().toString()), 10));

            PieDataSet set = new PieDataSet(entriesBDP, indicatorName);
            PieData data = new PieData(set);
            pieChartBDP.setData(data);
            set.setColors(ColorTemplate.COLORFUL_COLORS);
            pieChartBDP.getDescription().setText("BDP: " + indicatorName);
            pieChartBDP.animateXY(1500, 1500);
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
            entriesBAU.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2035().toString()), 7));
            entriesBAU.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2036().toString()), 8));
            entriesBAU.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2040().toString()), 9));
            entriesBAU.add(new PieEntry(Float.parseFloat(bdpMEI.getFY2041().toString()), 10));

            PieDataSet setBau = new PieDataSet(entriesBAU, indicatorName);
            PieData dataBau = new PieData(setBau);
            pieChartBAU.setData(dataBau);
            setBau.setColors(ColorTemplate.COLORFUL_COLORS);
            pieChartBAU.getDescription().setText("BAU: " + indicatorName);
            pieChartBAU.animateXY(1500, 1500);
            pieChartBAU.invalidate();
            //endregion
        }
    }

    private void loadTableData(String indicatorName, List<MacroEconIndicatorPivotData> lstMEIPivotData) {
        String content = new String();

        if (lstMEIPivotData.size() > 0) {
            content = "<html>\n" +
                    "\t<head>\n" +
                    "\t\t<style>\n" +
                    "\t\t\ttable {\n" +
                    "\t\t\t\tborder-collapse: collapse;\n" +
                    "\t\t\t\twidth: 100%;\n" +
                    "\t\t\t\tfont-size: 80%;\n" +
                    "\t\t\t}\n" +
                    "\t\t\tth, td {\n" +
                    "\t\t\t\tborder: 1px solid #c6c7cc; padding: 10px 15px;\n" +
                    "\t\t\t}\n" +
                    "\t\t\tth {\n" +
                    "\t\t\t\tfont-weight: bold;\n" +
                    "\t\t\t}\n" +
                    "\t\t\t.group-header {\n" +
                    "\t\t\t\tbackground: #D3DADF;\n" +
                    "\t\t\t\tcolor: #237A98;\n" +
                    "\t\t\t\tfont-weight: bold;\n" +
                    "\t\t\t}\n" +
                    "\t\t\t.odd {\n" +
                    "\t\t\t\tbackground: #F2F2F2;\n" +
                    "\t\t\t}\n" +
                    "\t\t\t.even {\n" +
                    "\t\t\t\tbackground: #FAFAF7;\n" +
                    "\t\t\t}\n" +
                    "\t\t</style>\n" +
                    "\t</head>\n" +
                    "\t<body>\n" +
                    "\t\t<table>\n" +
                    "\t\t\t<tbody>\n" +
                    "\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t<td class=\"group-header\" colspan=\"3\">Indicator: " + indicatorName + "</td>\n" +
                    "\t\t\t\t</tr>\n" +
                    "\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t<td class=\"group-header\" style=\"text-align: center;\"></td>\n" +
                    "\t\t\t\t\t<td class=\"group-header\" style=\"text-align: center;\">BAU</td>\n" +
                    "\t\t\t\t\t<td class=\"group-header\" style=\"text-align: center;\">BDP</td>\n" +
                    "\t\t\t\t</tr>\n" +

                    "\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t<td class=\"even\">FY-2016</td>\n" +
                    "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + lstMEIPivotData.get(0).getFY2016() + "</td>\n" +
                    "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + lstMEIPivotData.get(1).getFY2016() + "</td>\n" +
                    "\t\t\t\t</tr>\n" +

                    "\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t<td class=\"odd\">FY-2020</td>\n" +
                    "\t\t\t\t\t<td class=\"odd\" style=\"text-align: right;\">" + lstMEIPivotData.get(0).getFY2020() + "</td>\n" +
                    "\t\t\t\t\t<td class=\"odd\" style=\"text-align: right;\">" + lstMEIPivotData.get(1).getFY2020() + "</td>\n" +
                    "\t\t\t\t</tr>\n" +

                    "\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t<td class=\"even\">FY-2021</td>\n" +
                    "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + lstMEIPivotData.get(0).getFY2021() + "</td>\n" +
                    "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + lstMEIPivotData.get(1).getFY2021() + "</td>\n" +
                    "\t\t\t\t</tr>\n" +

                    "\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t<td class=\"odd\">FY-2025</td>\n" +
                    "\t\t\t\t\t<td class=\"odd\" style=\"text-align: right;\">" + lstMEIPivotData.get(0).getFY2025() + "</td>\n" +
                    "\t\t\t\t\t<td class=\"odd\" style=\"text-align: right;\">" + lstMEIPivotData.get(1).getFY2025() + "</td>\n" +
                    "\t\t\t\t</tr>\n" +

                    "\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t<td class=\"even\">FY-2026</td>\n" +
                    "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + lstMEIPivotData.get(0).getFY2026() + "</td>\n" +
                    "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + lstMEIPivotData.get(1).getFY2026() + "</td>\n" +
                    "\t\t\t\t</tr>\n" +

                    "\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t<td class=\"odd\">FY-2030</td>\n" +
                    "\t\t\t\t\t<td class=\"odd\" style=\"text-align: right;\">" + lstMEIPivotData.get(0).getFY2030() + "</td>\n" +
                    "\t\t\t\t\t<td class=\"odd\" style=\"text-align: right;\">" + lstMEIPivotData.get(1).getFY2030() + "</td>\n" +
                    "\t\t\t\t</tr>\n" +

                    "\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t<td class=\"even\">FY-2031</td>\n" +
                    "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + lstMEIPivotData.get(0).getFY2031() + "</td>\n" +
                    "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + lstMEIPivotData.get(1).getFY2031() + "</td>\n" +
                    "\t\t\t\t</tr>\n" +

                    "\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t<td class=\"odd\">FY-2035</td>\n" +
                    "\t\t\t\t\t<td class=\"odd\" style=\"text-align: right;\">" + lstMEIPivotData.get(0).getFY2035() + "</td>\n" +
                    "\t\t\t\t\t<td class=\"odd\" style=\"text-align: right;\">" + lstMEIPivotData.get(1).getFY2035() + "</td>\n" +
                    "\t\t\t\t</tr>\n" +

                    "\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t<td class=\"even\">FY-2036</td>\n" +
                    "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + lstMEIPivotData.get(0).getFY2036() + "</td>\n" +
                    "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + lstMEIPivotData.get(1).getFY2036() + "</td>\n" +
                    "\t\t\t\t</tr>\n" +

                    "\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t<td class=\"odd\">FY-2040</td>\n" +
                    "\t\t\t\t\t<td class=\"odd\" style=\"text-align: right;\">" + lstMEIPivotData.get(0).getFY2040() + "</td>\n" +
                    "\t\t\t\t\t<td class=\"odd\" style=\"text-align: right;\">" + lstMEIPivotData.get(1).getFY2040() + "</td>\n" +
                    "\t\t\t\t</tr>\n" +

                    "\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t<td class=\"even\">FY-2041</td>\n" +
                    "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + lstMEIPivotData.get(0).getFY2041() + "</td>\n" +
                    "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + lstMEIPivotData.get(1).getFY2041() + "</td>\n" +
                    "\t\t\t\t</tr>\n" +

                    "\t\t\t</tbody>  \n" +
                    "\t\t</table>\n" +
                    "\t</body>\n" +
                    "</html>";

            //for (int i = 0; i < lstMEIPivotData.size(); i++) {
            //    content += "";
            //}
        }

        webViewTblContent.setVisibility(View.VISIBLE);
        webViewTblContent.loadDataWithBaseURL(null, content, "text/HTML", "UTF-8", null);
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

    private String getFieldNamesAndValues(String fieldName, MacroEconIndicatorPivotData data) {
        /*
        for (Field f : lstMEIPivotData.get(i).getClass().getDeclaredFields()) {
            f.setAccessible(true);
            Object o;
            try {
                o = f.get(lstMEIPivotData.get(i));
            } catch (Exception e) {
                o = e;
            }

            Log.i(TAG, f.getGenericType() + " " + f.getName() + "(rony) = " + o);
        }
        */

        String result = "";

        for (Field f : data.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            Object o;

            try {
                o = f.get(data);
            } catch (Exception e) {
                o = e;
                result = "";
            }

            //Log.i(TAG, f.getGenericType() + " " + f.getName() + "(rony) = " + o);

            if (f.getName().contains(fieldName)) {
                result = o.toString();
                break;
            }
        }

        return result;
    }
}