package com.cegis.deltaplan2100.ui.layer_four;

import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cegis.deltaplan2100.API;
import com.cegis.deltaplan2100.MainActivity;
import com.cegis.deltaplan2100.R;
import com.cegis.deltaplan2100.models.ClimateChangePivotData;
import com.cegis.deltaplan2100.models.ClimateScenarioSubItemList;
import com.cegis.deltaplan2100.models.MacroEconIndicatorPivotData;
import com.cegis.deltaplan2100.models.MacroEconIndicatorsList;
import com.cegis.deltaplan2100.utility.FontawesomeLight;
import com.cegis.deltaplan2100.utility.GenerateHtmlContent;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
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
    private Spinner spnrMacroEconIndicator, spnrClimateChangeScenarios;
    private TextView txtOptionItemsParentName;

    private LineChart lineChart;
    private BarChart barChart;
    private PieChart pieChartBDP, pieChartBAU;
    private WebView webViewTblContent;

    private ArrayList<MacroEconIndicatorsList> lstMacroEconIndicators;
    private ArrayList<ClimateScenarioSubItemList> listCSSL;
    public List<MacroEconIndicatorPivotData> lstMEIPivotData;
    private List<ClimateChangePivotData> lstClimateChangePivotData;

    private ArrayList<String> list = new ArrayList<String>();

    private int itemID, itemParentLevel;
    private String groupHeader, itemContentAs;
    private List<String> fiscalYearList, climateScenarioYearList;
    private String[] color = {"#3297BB", "#0C4C6F", "#BB09BB", "#DC4C4E", "#7C7C7C", "#B47C2C", "#0FA18D", "#5A8732", "#04A9E1", "#4169E1", "#5E0B9B", "#42700D"};

    private boolean isChartSelected = false,
            isLineSelected = false,
            isBarSelected = false,
            isPieSelected = false,
            isTableSelected = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(LayerFourViewModel.class);
        root = inflater.inflate(R.layout.fragment_layer_four, container, false);

        //region control getting from view
        txtOptionItemsParentName = root.findViewById(R.id.txtOptionItemsParentName);
        spnrMacroEconIndicator = root.findViewById(R.id.spnrMacroEconIndicator);
        spnrClimateChangeScenarios = root.findViewById(R.id.spnrClimateChangeScenarios);

        btnLineChart = root.findViewById(R.id.btnLineChart);
        btnBarChart = root.findViewById(R.id.btnBarChart);
        btnPieChart = root.findViewById(R.id.btnPieChart);
        btnTableView = root.findViewById(R.id.btnTableView);

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

        txtOptionItemsParentName.setText(groupHeader + " Options");
        //endregion receiving

        //region loading dropdown data
        ((MainActivity) getActivity()).setToolBar(groupHeader);
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
        //endregion

        //region action type activity
        if (itemParentLevel == 2) {
            Toast.makeText(this.getContext(), "Item Parent Level 2", Toast.LENGTH_SHORT).show();
        } else if (itemParentLevel == 3) {
            if (itemContentAs.toLowerCase().contains("text") ||
                    itemContentAs.toLowerCase().contains("table") ||
                    itemContentAs.toLowerCase().contains("html")) {
                lineChart.setData(null);
                lilaLineChart.setVisibility(View.GONE);
                barChart.setData(null);
                lilaBarChart.setVisibility(View.GONE);
                pieChartBDP.setData(null);
                lilaPieChartBDP.setVisibility(View.GONE);
                pieChartBAU.setData(null);
                lilaPieChartBAU.setVisibility(View.GONE);

                getTextTableHtmlContent();
            } else if (itemContentAs.toLowerCase().contains("map")) {
                Log.i("Map Implementation", "Implemented in LayerThreeFragment");
            } else if (itemContentAs.toLowerCase().contains("graph") ||
                    itemContentAs.toLowerCase().contains("chart") ||
                    itemContentAs.toLowerCase().contains("graphchart")) {
                if (!groupHeader.toLowerCase().equals("climate change")) {
                    spnrClimateChangeScenarios.setVisibility(View.GONE);
                    spnrMacroEconIndicator.setVisibility(View.VISIBLE);

                    spnrMacroEconIndicator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            //code
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            //code
                        }
                    });

                    getFiscalYearList();
                    loadMacroEconIndicatorData(spnrMacroEconIndicator, 2); //BAU: 1; BDP: 2
                } else {
                    btnLineChart.setVisibility(View.GONE);
                    btnBarChart.setVisibility(View.GONE);
                    btnPieChart.setVisibility(View.GONE);
                    btnTableView.setVisibility(View.GONE);

                    barChart.setData(null);
                    lilaBarChart.setVisibility(View.GONE);
                    lineChart.setData(null);
                    lilaLineChart.setVisibility(View.GONE);
                    pieChartBDP.setData(null);
                    lilaPieChartBDP.setVisibility(View.GONE);
                    pieChartBAU.setData(null);
                    lilaPieChartBAU.setVisibility(View.GONE);

                    spnrMacroEconIndicator.setVisibility(View.GONE);
                    spnrClimateChangeScenarios.setVisibility(View.VISIBLE);

//                    spnrClimateChangeScenarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            //code
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//                            //code
//                        }
//                    });

                    loadClimateChangeListData(spnrClimateChangeScenarios);
                }

                //Toast.makeText(getContext(), "Parent ID: " + itemID, Toast.LENGTH_LONG).show();
            }
        } else if (itemParentLevel == 4) {
            Log.i("Parent Level 4", "Not Implemented");
        }
        //endregion

        return root;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLineChart:
                if (!isChartSelected || !isLineSelected) {
                    btnLineChart.setBackgroundResource(0);
                    txtBarChart.setTextColor(getResources().getColor(R.color.White));
                    btnBarChart.setBackgroundResource(R.drawable.bg_white);
                    txtPieChart.setTextColor(getResources().getColor(R.color.White));
                    btnPieChart.setBackgroundResource(R.drawable.bg_white);

                    txtLineChart.setTextColor(getResources().getColor(R.color.Crimson));
                    btnLineChart.setBackgroundResource(R.drawable.bg_white_selected);

                    isChartSelected = isLineSelected = true;
                    isBarSelected = false;
                    isPieSelected = false;

                    if (isLineSelected || isBarSelected || isPieSelected || isTableSelected) {
                        getMEIPivotData();
                    } else {
                        Toast.makeText(getContext(), "Please select data view", Toast.LENGTH_LONG).show();
                    }
                } else {
                    txtLineChart.setTextColor(getResources().getColor(R.color.White));
                    btnLineChart.setBackgroundResource(0);
                    btnBarChart.setBackgroundResource(R.drawable.bg_white);
                    btnPieChart.setBackgroundResource(R.drawable.bg_white);

                    btnLineChart.setBackgroundResource(R.drawable.bg_white);
                    isChartSelected = isLineSelected = false;

                    lineChart.setData(null);
                    lilaLineChart.setVisibility(View.GONE);
                }
                break;

            case R.id.btnBarChart:
                if (!isChartSelected || !isBarSelected) {
                    btnLineChart.setBackgroundResource(R.drawable.bg_white);
                    btnBarChart.setBackgroundResource(0);
                    btnPieChart.setBackgroundResource(R.drawable.bg_white);

                    txtBarChart.setTextColor(getResources().getColor(R.color.SeaGreen));
                    btnBarChart.setBackgroundResource(R.drawable.bg_white_selected);

                    txtLineChart.setTextColor(getResources().getColor(R.color.White));
                    txtPieChart.setTextColor(getResources().getColor(R.color.White));

                    isChartSelected = isBarSelected = true;
                    isLineSelected = false;
                    isPieSelected = false;

                    if (isLineSelected || isBarSelected || isPieSelected || isTableSelected) {
                        getMEIPivotData();
                    } else {
                        Toast.makeText(getContext(), "Please select data view", Toast.LENGTH_LONG).show();
                    }
                } else {
                    btnLineChart.setBackgroundResource(R.drawable.bg_white);
                    btnBarChart.setBackgroundResource(0);
                    btnPieChart.setBackgroundResource(R.drawable.bg_white);

                    txtBarChart.setTextColor(getResources().getColor(R.color.White));
                    btnBarChart.setBackgroundResource(R.drawable.bg_white);
                    isChartSelected = isBarSelected = false;

                    barChart.setData(null);
                    lilaBarChart.setVisibility(View.GONE);
                }
                break;

            case R.id.btnPieChart:
                if (!isChartSelected || !isPieSelected) {
                    txtLineChart.setTextColor(getResources().getColor(R.color.White));
                    btnLineChart.setBackgroundResource(R.drawable.bg_white);
                    txtBarChart.setTextColor(getResources().getColor(R.color.White));
                    btnBarChart.setBackgroundResource(R.drawable.bg_white);
                    btnPieChart.setBackgroundResource(0);

                    txtPieChart.setTextColor(getResources().getColor(R.color.DeepPink));
                    btnPieChart.setBackgroundResource(R.drawable.bg_white_selected);
                    isChartSelected = isPieSelected = true;
                    isBarSelected = false;
                    isLineSelected = false;

                    if (isLineSelected || isBarSelected || isPieSelected || isTableSelected) {
                        getMEIPivotData();
                    } else {
                        Toast.makeText(getContext(), "Please select data view", Toast.LENGTH_LONG).show();
                    }
                } else {
                    txtLineChart.setTextColor(getResources().getColor(R.color.White));
                    btnLineChart.setBackgroundResource(R.drawable.bg_white);
                    txtBarChart.setTextColor(getResources().getColor(R.color.White));
                    btnBarChart.setBackgroundResource(R.drawable.bg_white);
                    btnPieChart.setBackgroundResource(0);

                    txtPieChart.setTextColor(getResources().getColor(R.color.White));
                    btnPieChart.setBackgroundResource(R.drawable.bg_white);
                    isChartSelected = isPieSelected = false;

                    pieChartBDP.setData(null);
                    lilaPieChartBDP.setVisibility(View.GONE);
                    pieChartBAU.setData(null);
                    lilaPieChartBAU.setVisibility(View.GONE);
                }
                break;

            case R.id.btnTableView:
                if (!isTableSelected) {
                    btnTableView.setBackgroundResource(0);
                    txtTableView.setTextColor(getResources().getColor(R.color.ForestGreen));
                    btnTableView.setBackgroundResource(R.drawable.bg_white_selected);
                    isTableSelected = true;

                    if (isLineSelected || isBarSelected || isPieSelected || isTableSelected) {
                        getMEIPivotData();
                    } else {
                        Toast.makeText(getContext(), "Please select data view", Toast.LENGTH_LONG).show();
                    }
                } else {
                    btnTableView.setBackgroundResource(0);
                    txtTableView.setTextColor(getResources().getColor(R.color.White));
                    btnTableView.setBackgroundResource(R.drawable.bg_white);
                    isTableSelected = false;

                    lilaTableContent.setVisibility(View.GONE);
                    webViewTblContent.loadDataWithBaseURL(null, "", "text/HTML", "UTF-8", null);
                }
                break;

            default:
                break;
        }
    }

    private void getFiscalYearList() {
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

    private void getMEIPivotData() {
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
                            lstMEIPivotData.addAll(responseList);

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
        String[] xAxisLabel = fiscalYearList.toArray(new String[fiscalYearList.size()]);
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
        //endregion

        //region set data
        List<ILineDataSet> dataSets = new ArrayList<>();

        if (lstMEIPivotData.size() > 0) {
            for (int i = 0; i < lstMEIPivotData.size(); i++) {
                ArrayList<Entry> lineEntries = new ArrayList<>();

                for (int j = 0; j < xAxisLabel.length; j++) {
                    String val = getFieldNamesAndValues(xAxisLabel[j], lstMEIPivotData.get(i));
                    lineEntries.add(new Entry(j + 1, Float.parseFloat(val)));
                }

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

        LineData data = new LineData(dataSets);
        lineChart.setData(data);

        //lineChart.getDescription().setText(indicatorName);
        int width = lineChart.getWidth();
        int height = lineChart.getHeight();
        Description desc = new Description();
        desc.setText(indicatorName + " (" + lstMEIPivotData.get(0).getFyValueUnit() + ")");
        desc.setTextSize(13f);
        desc.setPosition(width - 200, height - (height - 30));
        lineChart.setDescription(desc);

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

        //YAxis yAxisLeft = barChart.getAxisLeft();
        //yAxisLeft.set
        //barChart.yAxis(0).title("Y-Axis with labels");

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

        int width = barChart.getWidth();
        int height = barChart.getHeight();
        Description desc = new Description();
        desc.setText(indicatorName + " (" + lstMEIPivotData.get(0).getFyValueUnit() + ")");
        desc.setTextSize(13f);
        desc.setPosition(width - 200, height - (height - 30));
        barChart.setDescription(desc);

        //Toast.makeText(getContext(), width + " :: wh :: " + height, Toast.LENGTH_SHORT).show();
        //barChart.getDescription().setText(indicatorName + " (" + lstMEIPivotData.get(0).getFyValueUnit() + ")");
        //barChart.getDescription().setPosition(width - 200, height - (height - 25));

        barChart.getAxisLeft().setEnabled(false); //show y-axis at left
        barChart.getAxisRight().setEnabled(true); //hide y-axis at right


        barChart.animateXY(1000, 1000);
        barChart.invalidate();

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                BarEntry pe = (BarEntry) e;

                Log.i(TAG, "Selected Value:" + pe.getX());
                Log.i(TAG, "Selected Label:" + pe.getYVals());
            }

            @Override
            public void onNothingSelected() {

            }
        });

        barChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                Float a = barChart.getHighlightByTouchPoint(me.getX(), me.getY()).getX();
                Float b = barChart.getHighlightByTouchPoint(me.getX(), me.getY()).getY();

                Toast.makeText(getContext(), a + " :: ab :: " + b, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });
    }

    private void loadPieChart(String indicatorName, List<MacroEconIndicatorPivotData> lstMEIPivotData) {
        MacroEconIndicatorPivotData mei;
        String[] xAxisLabel = fiscalYearList.toArray(new String[fiscalYearList.size()]);
        ArrayList<String> labels = (ArrayList<String>) fiscalYearList;

        if (lstMEIPivotData.size() > 0) {
            //region BDP
            mei = stream(lstMEIPivotData).where(x -> x.getIndicatorType().equals("BDP")).first();
            List<PieEntry> entriesBDP = new ArrayList<>();

            for (int j = 0; j < xAxisLabel.length; j++) {
                String val = getFieldNamesAndValues(xAxisLabel[j], mei);
                entriesBDP.add(new PieEntry(Float.parseFloat(val), xAxisLabel[j]));
            }

            PieDataSet set = new PieDataSet(entriesBDP, indicatorName);
            PieData data = new PieData(set);
            pieChartBDP.setData(data);
            set.setColors(ColorTemplate.MATERIAL_COLORS);
            pieChartBDP.getDescription().setText("BDP: " + indicatorName);
            pieChartBDP.setRotationEnabled(false);
            pieChartBDP.setEntryLabelColor(R.color.DeepGray);
            pieChartBDP.setEntryLabelTextSize(10f);
            pieChartBDP.animateXY(1500, 1500);

//            int width = pieChartBDP.getWidth();
//            int height = pieChartBDP.getHeight();
//            Description desc = new Description();
//            desc.setText(indicatorName + " (" + lstMEIPivotData.get(0).getFyValueUnit() + ")");
//            desc.setTextSize(13f);
//            desc.setPosition(width - 200, height - (height - 30));
//            pieChartBDP.setDescription(desc);

            pieChartBDP.invalidate();

            pieChartBDP.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    PieEntry pe = (PieEntry) e;

                    Log.i(TAG, "BDP Selected Value:" + pe.getValue());
                    Log.i(TAG, "BDP Selected Label:" + pe.getLabel());
                }

                @Override
                public void onNothingSelected() {

                }
            });
            //endregion

            //region BAU
            mei = stream(lstMEIPivotData).where(x -> x.getIndicatorType().equals("BAU")).first();
            List<PieEntry> entriesBAU = new ArrayList<>();

            for (int j = 0; j < xAxisLabel.length; j++) {
                String val = getFieldNamesAndValues(xAxisLabel[j], mei);
                entriesBAU.add(new PieEntry(Float.parseFloat(val), xAxisLabel[j]));
            }

            PieDataSet setBau = new PieDataSet(entriesBAU, indicatorName);
            PieData dataBau = new PieData(setBau);
            pieChartBAU.setData(dataBau);
            setBau.setColors(ColorTemplate.MATERIAL_COLORS);
            //setBau.setColors(getResources().getIntArray(R.array.chart_color));
            pieChartBAU.getDescription().setText("BAU: " + indicatorName);
            pieChartBAU.setRotationEnabled(false);
            pieChartBAU.setEntryLabelColor(R.color.DeepGray);
            pieChartBAU.setEntryLabelTextSize(10f);
            pieChartBAU.animateXY(1500, 1500);

//            width = pieChartBAU.getWidth();
//            height = pieChartBAU.getHeight();
//            desc = new Description();
//            desc.setText(indicatorName + " (" + lstMEIPivotData.get(0).getFyValueUnit() + ")");
//            desc.setTextSize(13f);
//            desc.setPosition(width - 200, height - (height - 30));
//            pieChartBAU.setDescription(desc);

            pieChartBAU.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    PieEntry pe = (PieEntry) e;

                    Log.i(TAG, "BAU Selected Value:" + pe.getValue());
                    Log.i(TAG, "BAU Selected Label:" + pe.getLabel());
                }

                @Override
                public void onNothingSelected() {

                }
            });

            pieChartBAU.invalidate();
            //endregion
        }

    }

    private void loadTableData(String indicatorName, List<MacroEconIndicatorPivotData> lstMEIPivotData) {
        String content = new String();
        String[] fyList = fiscalYearList.toArray(new String[fiscalYearList.size()]);

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
                    "\t<div style=\"max-height: 250px; overflow: overflow-y;\">\n" +
                    "\t\t<table>\n" +
                    "\t\t\t<tbody>\n" +
                    "\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t<td class=\"group-header\" colspan=\"3\">Indicator: " + indicatorName + " (" + lstMEIPivotData.get(0).getFyValueUnit() + ")" + "</td>\n" +
                    "\t\t\t\t</tr>\n" +
                    "\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t<td class=\"group-header\" style=\"text-align: center;\"></td>\n" +
                    "\t\t\t\t\t<td class=\"group-header\" style=\"text-align: center;\">BAU</td>\n" +
                    "\t\t\t\t\t<td class=\"group-header\" style=\"text-align: center;\">BDP</td>\n" +
                    "\t\t\t\t</tr>\n";

            for (int j = 0; j < fyList.length; j++) {
                String val1 = getFieldNamesAndValues(fyList[j], lstMEIPivotData.get(0));
                String val2 = getFieldNamesAndValues(fyList[j], lstMEIPivotData.get(1));

                content += "\t\t\t\t<tr>\n" +
                        "\t\t\t\t\t<td class=\"even\">FY-" + fyList[j] + "</td>\n" +
                        "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + val1 + "</td>\n" +
                        "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + val2 + "</td>\n" +
                        "\t\t\t\t</tr>\n";
            }

            content += "\t\t\t</tbody>  \n" +
                    "\t\t</table>\n" +
                    "\t</div>\n" +
                    "\t</body>\n" +
                    "</html>";
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

    private void loadClimateChangeListData(Spinner spinner) {
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
        Call call = api.getClimateScenarioSubItemList();

        call.enqueue(new Callback<List<ClimateScenarioSubItemList>>() {
            @Override
            public void onResponse(Call<List<ClimateScenarioSubItemList>> call, Response<List<ClimateScenarioSubItemList>> response) {
                if (response.isSuccessful()) {
                    try {
                        List<ClimateScenarioSubItemList> responseList = response.body();
                        list = new ArrayList<>();

                        if (responseList.size() > 0) {
                            listCSSL = new ArrayList<>();

                            for (int i = 0; i < responseList.size(); i++) {
                                ClimateScenarioSubItemList spinnerModel = new ClimateScenarioSubItemList();
                                spinnerModel.setSubitemId(responseList.get(i).getSubitemId());
                                spinnerModel.setSubitemName(responseList.get(i).getSubitemName());
                                spinnerModel.setSubitemUnit(responseList.get(i).getSubitemUnit());
                                spinnerModel.setSubitemDescription(responseList.get(i).getSubitemDescription());
                                listCSSL.add(spinnerModel);

                                list.add(responseList.get(i).getSubitemName());
                            }

                            spinner.setAdapter(null);
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), simple_spinner_item, list);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(spinnerArrayAdapter);

                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    int subitem_id = listCSSL.get(position).getSubitemId();
                                    getClimateChangeData(subitem_id);

                                    //String item = (String) parent.getItemAtPosition(position);
                                    //Toast.makeText(getContext(), item, Toast.LENGTH_LONG).show();
                                    //((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#2F4F4F"));
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
            public void onFailure(Call<List<ClimateScenarioSubItemList>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getClimateChangeData(int scenario_subitem_id) {
        String selected_item = spnrClimateChangeScenarios.getSelectedItem().toString();

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
        Call call = api.getClimateChangePivotList(scenario_subitem_id);

        call.enqueue(new Callback<List<ClimateChangePivotData>>() {
            @Override
            public void onResponse(Call<List<ClimateChangePivotData>> call, Response<List<ClimateChangePivotData>> response) {
                lstClimateChangePivotData = new ArrayList<>();

                if (response.isSuccessful()) {
                    try {
                        List<ClimateChangePivotData> responseList = response.body();

                        if (responseList.size() > 0) {
                            climateScenarioYearList = new ArrayList<>();

                            for (int i = 0; i < responseList.size(); i++) {
                                String year = responseList.get(i).getScenarioDataYear().toString();
                                climateScenarioYearList.add(year);
                            }

                            lstClimateChangePivotData.addAll(responseList);

                            lilaBarChart.setVisibility(View.VISIBLE);
                            lineChart.setData(null);
                            lilaLineChart.setVisibility(View.GONE);
                            pieChartBDP.setData(null);
                            lilaPieChartBDP.setVisibility(View.GONE);
                            pieChartBAU.setData(null);
                            lilaPieChartBAU.setVisibility(View.GONE);

                            loadCCSBarChart(selected_item, lstClimateChangePivotData);
                            loadCCTableData(selected_item, lstClimateChangePivotData);
                        } else {
                            lstClimateChangePivotData = new ArrayList<>();
                            Toast.makeText(getContext(), "Sorry, no data found!", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        lstClimateChangePivotData = new ArrayList<>();
                        e.printStackTrace();

                        if (!e.getMessage().equals("empty String")) {
                            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG)
                                    .setAction("Climate Change Data Loading Error", null)
                                    .show();
                        }
                    }
                } else {
                    lstClimateChangePivotData = new ArrayList<>();
                    Toast.makeText(getContext(), "Response not found.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<ClimateChangePivotData>> call, Throwable t) {
                lstClimateChangePivotData = new ArrayList<>();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadCCSBarChart(String selected_item, List<ClimateChangePivotData> lstCCSPivotData) {
        BarData data = new BarData();
        int groupCount = climateScenarioYearList.size();
        int selected_position = spnrClimateChangeScenarios.getSelectedItemPosition();

        if (lstCCSPivotData.size() > 0) {
            ArrayList<BarEntry> barActiveEntries = new ArrayList<>();
            ArrayList<BarEntry> barModerateEntries = new ArrayList<>();
            ArrayList<BarEntry> barProductiveEntries = new ArrayList<>();
            ArrayList<BarEntry> barResilientEntries = new ArrayList<>();

            for (int i = 0; i < lstCCSPivotData.size(); i++) {
                String valActive = lstCCSPivotData.get(i).getActive().toString();
                barActiveEntries.add(new BarEntry(i, Float.parseFloat(valActive)));

                String valModerate = lstCCSPivotData.get(i).getModerate().toString();
                barModerateEntries.add(new BarEntry(i, Float.parseFloat(valModerate)));

                String valProductive = lstCCSPivotData.get(i).getProductive().toString();
                barProductiveEntries.add(new BarEntry(i, Float.parseFloat(valProductive)));

                String valResilient = lstCCSPivotData.get(i).getResilient().toString();
                barResilientEntries.add(new BarEntry(i, Float.parseFloat(valResilient)));
            }

            BarDataSet barDataSet0 = new BarDataSet(barActiveEntries, "Active");
            barDataSet0.setColor(Color.parseColor("#000099"));
            barDataSet0.setAxisDependency(YAxis.AxisDependency.RIGHT);
            BarDataSet barDataSet1 = new BarDataSet(barModerateEntries, "Moderate");
            barDataSet1.setColors(Color.parseColor("#ED1C24"));
            BarDataSet barDataSet2 = new BarDataSet(barProductiveEntries, "Productive");
            barDataSet2.setColors(Color.parseColor("#59A550"));
            BarDataSet barDataSet3 = new BarDataSet(barResilientEntries, "Resilient");
            barDataSet3.setColors(Color.parseColor("#F68928"));

            String[] years = climateScenarioYearList.toArray(new String[climateScenarioYearList.size()]);
            data = new BarData(barDataSet0, barDataSet1, barDataSet2, barDataSet3);
            barChart.setData(data);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(years));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1);
            //xAxis.setDrawGridLines(true);
            xAxis.setCenterAxisLabels(true);
            xAxis.setGranularityEnabled(true);

            float barSpace = 0.02f;
            float groupSpace = 0.3f;
            //int groupCount = 6;

            //IMPORTANT *****
            data.setBarWidth(0.15f);
            barChart.getXAxis().setAxisMinimum(0);
            barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
            barChart.getAxisLeft().setAxisMinimum(0);
            barChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
            //***** IMPORTANT
        }

//        YAxis yAxisLeft = barChart.getAxisLeft();
//        yAxisLeft.setAxisMinimum(0);
//        barChart.yAxis(0).title("Y-Axis with labels");

        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(3);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);

        //measurement data
        //float groupSpace = 0.04f;
        //float barSpace = 0.02f;
        //float barWidth = 0.46f;
        // (0.46 + 0.02) * 2 + 0.04 = 1.00 -> interval per "group"

//        float groupSpace = 0.04f;
//        float barSpace = 0.02f;
//        float barWidth = 0.46f;
//        data.setBarWidth(barWidth);
//
//        barChart.getXAxis().setAxisMinimum(0);
//        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * 4);
//        barChart.getAxisLeft().setAxisMinimum(0);
//        barChart.groupBars(0, groupSpace, barSpace);

        int width = barChart.getWidth();
        int height = barChart.getHeight();
        Description desc = new Description();
        String title = "Unit: " + listCSSL.get(selected_position).getSubitemUnit();
        desc.setText(title);
        desc.setTextSize(13f);
        desc.setYOffset(20f);
        desc.setTextAlign(Paint.Align.CENTER);
        desc.setPosition(width - 500, height - (height - 60));
        barChart.setDescription(desc);
        barChart.getDescription().setXOffset(10f);

//        Description desc1 = new Description();
//        String title1 = listCSSL.get(selected_position).getSubitemDescription();
//        desc1.setText(title1);
//        desc1.setTextSize(13f);
//        desc1.setTextAlign(Paint.Align.CENTER);
//        desc1.setPosition(width - 500, height - (height - 60));
//        barChart.setDescription(desc1);

        YAxis rightYAxis = barChart.getAxisRight();
        rightYAxis.setEnabled(false);
        rightYAxis.setDrawGridLines(true);

        YAxis leftYAxis = barChart.getAxisLeft();
        leftYAxis.setEnabled(true);
        leftYAxis.setDrawAxisLine(true);
        leftYAxis.setDrawGridLines(true);
        //leftYAxis.setValueFormatter("Values");

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTextSize(13f);

        //Toast.makeText(getContext(), width + " :: wh :: " + height, Toast.LENGTH_SHORT).show();
        //barChart.getDescription().setText(indicatorName + " (" + lstMEIPivotData.get(0).getFyValueUnit() + ")");
        //barChart.getDescription().setPosition(width - 200, height - (height - 25));

        barChart.animateXY(1000, 1000);
        barChart.setExtraOffsets(5f, 50f, 2f, 10f);
        barChart.invalidate();

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            protected RectF mOnValueSelectedRectF = new RectF();

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e == null)
                    return;

                BarEntry pe = (BarEntry) e;

                RectF bounds = mOnValueSelectedRectF;
                barChart.getBarBounds((BarEntry) e, bounds);
                MPPointF position = barChart.getPosition(e, YAxis.AxisDependency.LEFT);

                final String xAxisLabel = barChart.getXAxis().getValueFormatter().getFormattedValue(e.getX(), barChart.getXAxis());

                Log.i("Year", xAxisLabel);
                Log.i("Value", e.getY() + "");
                Log.i("Scenario Name", getScenarioNameByIndex(h.getDataSetIndex()));

                String info = "Year: " + xAxisLabel + "" +
                        "\nScenario Name: " + getScenarioNameByIndex(h.getDataSetIndex()) +
                        "\nValue: " + e.getY();

                Snackbar snackbar = Snackbar.make(getView(), info, Snackbar.LENGTH_LONG);
                TextView tv = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                tv.setMaxLines(3);
                snackbar.show();

                //Log.i("bounds", bounds.toString());
                //Log.i("position", position.toString());
                //Log.i("VAL SELECTED", "Value: " + e.getY() + ", xIndex: " + e.getX()
                //        + ", DataSet index: " + h.getDataSetIndex());
                //Log.i(TAG, "Selected Value:" + pe.getX());
                //Log.i(TAG, "Selected Label:" + pe.getYVals());
                //Toast.makeText(getContext(), "Value: " + e.getY() + ", xIndex: " + e.getX() + ", DataSet index: " + h.getDataSetIndex() + ", Data Index: " + h.getDataIndex(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        barChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                Float a = barChart.getHighlightByTouchPoint(me.getX(), me.getY()).getX();
                Float b = barChart.getHighlightByTouchPoint(me.getX(), me.getY()).getY();

                //Toast.makeText(getContext(), a + " :: ab :: " + b, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });
    }

    private void loadCCTableData(String selected_item, List<ClimateChangePivotData> lstCCSPivotData) {
        String content = new String();
        String[] fyList = climateScenarioYearList.toArray(new String[climateScenarioYearList.size()]);

        if (lstCCSPivotData.size() > 0) {
            lilaTableContent.setVisibility(View.VISIBLE);

            content = "<html>\n" +
                    "<head>\n" +
                    "<style>\n" +
                    "\ttable {\n" +
                    "\t\tborder-collapse: collapse;\n" +
                    "\t\twidth: 100%;\n" +
                    "\t\tfont-size: 80%;\n" +
                    "\t}\n" +
                    "\tth, td {\n" +
                    "\t\tborder: 1px solid #c6c7cc; padding: 10px 15px;\n" +
                    "\t}\n" +
                    "\tth {\n" +
                    "\t\tfont-weight: bold;\n" +
                    "\t}\n" +
                    "\t.group-header {\n" +
                    "\t\tbackground: #D3DADF;\n" +
                    "\t\tcolor: #237A98;\n" +
                    "\t\tfont-weight: bold;\n" +
                    "\t}\n" +
                    "\t.odd {\n" +
                    "\t\tbackground: #F2F2F2;\n" +
                    "\t}\n" +
                    "\t.even {\n" +
                    "\t\tbackground: #FAFAF7;\n" +
                    "\t}\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div style=\"max-height: 250px; overflow: overflow-y;\">\n" +
                    "<table>\n" +
                    "<tbody>\n" +
                    "<tr>\n" +
                    "<td class=\"group-header\" colspan=\"5\" style=\"text-align: center;\">" + selected_item + "</td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "<td class=\"group-header\" style=\"text-align: center;\">Year</td>\n" +
                    "<td class=\"group-header\" style=\"text-align: center;\">Active</td>\n" +
                    "<td class=\"group-header\" style=\"text-align: center;\">Moderate</td>\n" +
                    "<td class=\"group-header\" style=\"text-align: center;\">Productive</td>\n" +
                    "<td class=\"group-header\" style=\"text-align: center;\">Resilient</td>\n" +
                    "</tr>";

            for (int i = 0; i < lstCCSPivotData.size(); i++) {
                String val1 = lstCCSPivotData.get(i).getScenarioDataYear().toString();
                String val2 = lstCCSPivotData.get(i).getActive().toString();
                String val3 = lstCCSPivotData.get(i).getModerate().toString();
                String val4 = lstCCSPivotData.get(i).getProductive().toString();
                String val5 = lstCCSPivotData.get(i).getResilient().toString();

                content += "\t\t\t\t<tr>\n" +
                        "\t\t\t\t\t<td class=\"even\">" + val1 + "</td>\n" +
                        "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + val2 + "</td>\n" +
                        "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + val3 + "</td>\n" +
                        "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + val4 + "</td>\n" +
                        "\t\t\t\t\t<td class=\"even\" style=\"text-align: right;\">" + val5 + "</td>\n" +
                        "\t\t\t\t</tr>\n";
            }

            content += "\t\t\t</tbody>  \n" +
                    "\t\t</table>\n" +
                    "\t</div>\n" +
                    "\t</body>\n" +
                    "</html>";
        }

        webViewTblContent.setVisibility(View.VISIBLE);
        webViewTblContent.loadDataWithBaseURL(null, content, "text/HTML", "UTF-8", null);
    }

    private String getScenarioNameByIndex(int index) {
        switch (index) {
            case 0:
                return "Active";

            case 1:
                return "Moderate";

            case 2:
                return "Productive";

            case 3:
                return "Resilient";

            default:
                return "";
        }
    }

    private void getTextTableHtmlContent() {
        Toast.makeText(getContext(), groupHeader, Toast.LENGTH_LONG).show();

        if (groupHeader.equals("Delta Challenges")) {
            webViewTblContent.loadDataWithBaseURL("file:///android_asset/", readAssetFileAsString("delta_challanges.html"), "text/html", "UTF-8", null);
        } else {
            ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "Please wait...", true);

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
            Call call = api.getTextTableHtmlContent(itemID, itemParentLevel, itemContentAs);

            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    dialog.dismiss();
                    String content = response.body().toString();
                    content = GenerateHtmlContent.getHtmlTable(content);
                    webViewTblContent.loadDataWithBaseURL(null, content, "text/HTML", "UTF-8", null);
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private String readAssetFileAsString(String sourceHtmlLocation) {
        InputStream is;
        try {
            is = getContext().getAssets().open(sourceHtmlLocation);
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            return new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private String getFieldNamesAndValues(String fieldName, MacroEconIndicatorPivotData data) {
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

            //Log.i(TAG, f.getGenericType() + " " + f.getName() + "= " + o);

            if (f.getName().contains(fieldName)) {
                result = o.toString();
                break;
            }
        }

        return result;
    }

    private String getCCSFieldNamesAndValues(String fieldName, ClimateChangePivotData data) {
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

            //Log.i(TAG, f.getGenericType() + " " + f.getName() + "= " + o);

            if (f.getName().contains(fieldName)) {
                result = o.toString();
                break;
            }
        }

        return result;
    }
}