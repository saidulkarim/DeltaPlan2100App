package com.cegis.deltaplan2100;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class TabularViewFragment extends Fragment {
    private View root;
    private OnFragmentInteractionListener mListener;
    TextView textView;
    WebView webView;
    String groupHeader, parentID, content;
    BarChart barChart;
    PieChart pieChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = inflater.inflate(R.layout.fragment_tabular_view, container, false);
        webView = root.findViewById(R.id.web_view);
        // Enable Javascript
        webView.getSettings().setJavaScriptEnabled(true);
        barChart = root.findViewById(R.id.barchart);
        pieChart = root.findViewById(R.id.piechart);

        if (getArguments() != null) {
            groupHeader = getArguments().getString("GroupHeader");
            parentID = getArguments().getString("ParentID");
        }

        ((MainActivity) getActivity()).setToolBar(groupHeader);

        if (groupHeader.equals("Climate Change")) {
            content = "<html>\n" +
                    "\t<head>\n" +
                    "\t<style>\n" +
                    "\ttable {\n" +
                    "\t  border-collapse: collapse;\n" +
                    "\t  width: 100%;\n" +
                    "\t  font-size: 80%;\n" +
                    "\t}\n" +
                    "\tth,\n" +
                    "\ttd {\n" +
                    "\t  border: 1px solid #c6c7cc; padding: 10px 15px;\n" +
                    "\t}\n" +
                    "\tth {\n" +
                    "\t  font-weight: bold;\n" +
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
                    "\t</style>\n" +
                    "\t</head>\n" +
                    "\t<body>\n" +
                    "\t<table>\n" +
                    "\t  <tbody>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"group-header\" colspan=\"2\">OverView:</td>\t\t \n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"odd\">Title</td>\n" +
                    "\t\t  <td class=\"odd\">GBM Basin</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"even\">Abstract</td>\n" +
                    "\t\t  <td class=\"even\">A spatial data layer of GBM Basin for planning purpose of Bangladesh along with other attribute information.</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"group-header\" colspan=\"2\">General:</td>\t\t  \n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"odd\">Title</td>\n" +
                    "\t\t  <td class=\"odd\">GBM Basin</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"even\">Purpose of Production</td>\n" +
                    "\t\t  <td class=\"even\">The data layer is prepared to delineate the GBM Basin of the country. The resource assessment and its usages are needed for the planners for development of projects and alleviation of socio-economic concern.</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"odd\">Completeness</td>\n" +
                    "\t\t  <td class=\"odd\">The data layer covers all the embankment locations identified by BWDB.</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"even\">Quality</td>\n" +
                    "\t\t  <td class=\"even\">Quality of this data layer depends on the data collection process of the source organization.</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"odd\">History of the Dataset</td>\n" +
                    "\t\t  <td class=\"odd\"></td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"even\">Process Description</td>\n" +
                    "\t\t  <td class=\"even\"></td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"odd\">Type of Dataset</td>\n" +
                    "\t\t  <td class=\"odd\">Shapefile</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"even\">Dataset Language</td>\n" +
                    "\t\t  <td class=\"even\">English</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"odd\">Additional Information Source for the Dataset</td>\n" +
                    "\t\t  <td class=\"odd\"></td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"group-header\" colspan=\"2\">Access:</td>\t\t  \n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"odd\">Data Source Name</td>\n" +
                    "\t\t  <td class=\"odd\">GBMBasin.shp</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"even\">Data Source Location</td>\n" +
                    "\t\t  <td class=\"even\"></td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"odd\">Distribution File Format</td>\n" +
                    "\t\t  <td class=\"odd\">ArcInfo export, ArcView shapefile</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"even\">Media of Distribution</td>\n" +
                    "\t\t  <td class=\"even\">Optical disc (DVD,CD) Pen Drive, Hard Drive</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"odd\">Organization</td>\n" +
                    "\t\t  <td class=\"odd\">Bangladesh Delta Plan 2100</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"even\">Organization Address</td>\n" +
                    "\t\t  <td class=\"even\">House # 13/A , NE(K), Level 5, Road # 83,Gulshan-2, Dhaka-1212</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"odd\">Organization E-mail Address</td>\n" +
                    "\t\t  <td class=\"odd\">info@bangladeshdeltaplan2100.org</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"even\">Use Constraints</td>\n" +
                    "\t\t  <td class=\"even\">The data layer can be used for Bangladesh Delta Plan 2100</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"odd\">Access Constraints</td>\n" +
                    "\t\t  <td class=\"odd\">With permission of one can have access this data layer</td>\n" +
                    "\t\t</tr>\n" +
                    "\t  </tbody>\t  \n" +
                    "\t</table>\n" +
                    "\t</body>\n" +
                    "</html>";

            barChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.loadDataWithBaseURL(null, content, "text/HTML", "UTF-8", null);
        } else if (groupHeader.equals("Employment and Poverty")) {
            content = "<html>\n" +
                    "\t<head>\n" +
                    "\t<style>\n" +
                    "\ttable {\n" +
                    "\t  border-collapse: collapse;\n" +
                    "\t  width: 100%;\n" +
                    "\t  font-size: 80%;\n" +
                    "\t}\n" +
                    "\tth,\n" +
                    "\ttd {\n" +
                    "\t  border: 1px solid #c6c7cc; padding: 10px 15px;\n" +
                    "\t}\n" +
                    "\tth {\n" +
                    "\t  font-weight: bold;\n" +
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
                    "\t</style>\n" +
                    "\t</head>\n" +
                    "\t<body>\n" +
                    "\t<table>\n" +
                    "\t  <tbody>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"even\">Scenario</td>\n" +
                    "\t\t  <td class=\"even\">Socio-economic developments</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"odd\">Climate Scenario Scale</td>\n" +
                    "\t\t  <td class=\"odd\">Low</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"even\">Economic Scenario Scale</td>\n" +
                    "\t\t  <td class=\"even\">High</td>\n" +
                    "\t\t</tr>\t\t\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"odd\">Unit</td>\n" +
                    "\t\t  <td class=\"odd\">Million</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"even\">Scenario Name</td>\n" +
                    "\t\t  <td class=\"even\">Productive</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\t\t\n" +
                    "\t\t  <td class=\"odd\">Description</td>\n" +
                    "\t\t  <td class=\"odd\">Population is derived from World Pop with GUF Mask year 2015</td>\n" +
                    "\t\t</tr>\n" +
                    "\t  </tbody>\t  \n" +
                    "\t</table>\n" +
                    "\t</body>\n" +
                    "</html>";

            barChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.loadDataWithBaseURL(null, content, "text/HTML", "UTF-8", null);
        } else if (groupHeader.equals("GDP Per Capita")) {
            webView.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
            barChart.setVisibility(View.VISIBLE);

            ArrayList<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(8000f, 0));
            entries.add(new BarEntry(2000f, 1));
            entries.add(new BarEntry(5000f, 2));
            entries.add(new BarEntry(20000f, 3));
            entries.add(new BarEntry(15000f, 4));
            entries.add(new BarEntry(19000f, 5));

            BarDataSet bardataset = new BarDataSet(entries, "Cells");

            ArrayList<String> labels = new ArrayList<String>();
            labels.add("2016");
            labels.add("2015");
            labels.add("2014");
            labels.add("2013");
            labels.add("2012");
            labels.add("2011");

            BarData data = new BarData(labels, bardataset);
            barChart.setData(data); // set the data and list of labels into chart
            barChart.setDescription(groupHeader);  // set the description
            bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
            barChart.animateY(5000);
        } else if (groupHeader.equals("Labor Force Dynamics")) {
            webView.setVisibility(View.GONE);
            barChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);

            ArrayList NoOfEmp = new ArrayList();
            NoOfEmp.add(new Entry(945f, 0));
            NoOfEmp.add(new Entry(1040f, 1));
            NoOfEmp.add(new Entry(1133f, 2));
            NoOfEmp.add(new Entry(1240f, 3));
            NoOfEmp.add(new Entry(1369f, 4));
            NoOfEmp.add(new Entry(1487f, 5));
            NoOfEmp.add(new Entry(1501f, 6));
            NoOfEmp.add(new Entry(1645f, 7));
            NoOfEmp.add(new Entry(1578f, 8));
            NoOfEmp.add(new Entry(1695f, 9));
            PieDataSet dataSet = new PieDataSet(NoOfEmp, "Number Of Employees");

            //test comment
            ArrayList year = new ArrayList();

            year.add("2008");
            year.add("2009");
            year.add("2010");
            year.add("2011");
            year.add("2012");
            year.add("2013");
            year.add("2014");
            year.add("2015");
            year.add("2016");
            year.add("2017");
            PieData data = new PieData(year, dataSet);
            pieChart.setData(data);
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            pieChart.animateXY(5000, 5000);
        } else {
            content = "Not implemented!";
            webView.loadDataWithBaseURL(null, content, "text/HTML", "UTF-8", null);
        }

        return root;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
