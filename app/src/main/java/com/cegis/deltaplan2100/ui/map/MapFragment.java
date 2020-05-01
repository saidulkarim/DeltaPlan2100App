package com.cegis.deltaplan2100.ui.map;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.cegis.deltaplan2100.API;
import com.cegis.deltaplan2100.MainActivity;
import com.cegis.deltaplan2100.R;
import com.cegis.deltaplan2100.models.InvestmentProjectList;
import com.cegis.deltaplan2100.models.MacroEconIndicatorsList;
import com.cegis.deltaplan2100.models.MapLegendItem;
import com.cegis.deltaplan2100.utility.GenerateHtmlContent;
import com.cegis.deltaplan2100.utility.HttpGetRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.HTTP;

import static android.R.layout.simple_spinner_item;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_LEFT;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_RIGHT;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_TOP;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_JUSTIFY_AUTO;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textJustify;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textRadialOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textVariableAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;

public class MapFragment extends Fragment implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener {
    private PermissionsManager permissionsManager;
    private MapViewModel mViewModel;
    private View root;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private FloatingActionButton fab, fab0, fab1, fab2, fab3, fab4, fabDetails;
    private Spinner spnrHotspotList, spnrInvestProjList;

    private int itemID, itemParentLevel;
    private String groupHeader, itemContentAs, htmlRawContent = new String();
    private static final String EMPTY_STRING = "";
    private boolean isFabOpen = false, isFabSubOpen = false;

    private static final String ADMIN_BOUNDARY_LAYER = "ADMIN_BOUNDARY_LAYER";
    private static final String ADMIN_BOUNDARY_LINE_LAYER = "ADMIN_BOUNDARY_LINE_LAYER";
    private static final String ADMIN_BOUNDARY_FILL_LAYER = "ADMIN_BOUNDARY_FILL_LAYER";

    private static final String PROJECT_LAYER = "PROJECT_LAYER";
    private static final String PROJECT_FILL_LAYER = "PROJECT_FILL_LAYER";
    private static final String PROJECT_LABEL_LAYER = "PROJECT_LABEL_LAYER";
    private static final String PROJECT_SYMBOL_LAYER = "PROJECT_SYMBOL_LAYER";
    private static final String PROJECT_LINE_LAYER = "PROJECT_LINE_LAYER";

    private static final String MAP_MARKER_ICON = "map_marker_icon";

    private Typeface tf;
    public FeatureCollection featureCollection;

    private ArrayList<InvestmentProjectList> lstInvestmentProjectHotspotList, lstInvestmentProjectList;
    private List<MapLegendItem> lstMapLegendItem = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<String>(), listHotSpot = new ArrayList<String>();
    private static String textView = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this.getContext(), getString(R.string.map_access_token));
        tf = Typeface.createFromAsset(this.getContext().getAssets(), "fonts/titillium_semi_bold.ttf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        root = inflater.inflate(R.layout.fragment_map, container, false);

        //region receiving params from called fragment
        itemID = getArguments().getInt("ItemID");
        groupHeader = getArguments().getString("GroupHeader");
        itemContentAs = getArguments().getString("ItemContentAs");
        itemParentLevel = getArguments().getInt("ItemParentLevel");
        //endregion receiving

        //region control
        fab = root.findViewById(R.id.fab);
        TooltipCompat.setTooltipText(fab, "Menu");
        fab.setOnClickListener(view -> showFabMenu());

        fab0 = root.findViewById(R.id.fab0);
        TooltipCompat.setTooltipText(fab0, "Map Layers");
        fab0.setAlpha(0.75f);
        fab0.setOnClickListener(view -> menuBaseLayer());

        fab1 = root.findViewById(R.id.fab1);
        TooltipCompat.setTooltipText(fab1, "Show Map Label");
        fab1.setAlpha(0.75f);
        fab1.setOnClickListener(view -> menuLabel());

        fab2 = root.findViewById(R.id.fab2);
        TooltipCompat.setTooltipText(fab2, "Show Map Marker");
        fab2.setAlpha(0.75f);
        fab2.setOnClickListener(view -> menuMarker());

        fab3 = root.findViewById(R.id.fab3);
        TooltipCompat.setTooltipText(fab3, "Show My Location");
        fab3.setAlpha(0.75f);
        fab3.setOnClickListener(view -> menuGetMyCurrentLocation());

        fab4 = root.findViewById(R.id.fab4);
        TooltipCompat.setTooltipText(fab4, "100% View");
        fab4.setAlpha(0.75f);
        fab4.setOnClickListener(view -> menu100PView());

        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        spnrHotspotList = root.findViewById(R.id.spnrHotspotList);
        spnrInvestProjList = root.findViewById(R.id.spnrInvestProjList);
        fabDetails = root.findViewById(R.id.fabDetails);

        //endregion

        //region loading data
        ((MainActivity) getActivity()).setToolBar(groupHeader);

        if (groupHeader.toLowerCase().contains("investment projects")) {
            fabDetails.show();
            spnrHotspotList.setVisibility(View.VISIBLE);
            loadInvProjHotspotData(spnrHotspotList);

            TooltipCompat.setTooltipText(fabDetails, "View Map Legend");
            fabDetails.setAlpha(0.90f);
            fabDetails.setOnClickListener(view -> fabDetailsClick());
        }
        //endregion

        return root;
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            mapboxMap.addOnMapClickListener(MapFragment.this::onMapClick);
            addAdminBoundaryLayerToMap(style);

            if (style != null) {
                mapboxMap.getStyle().addImage(
                        MAP_MARKER_ICON,
                        BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_map_marker_15)
                );

                // if want to different types of feature icon
                //mapboxMap.getStyle().addImage(
                //        "sample_icon",
                //        BitmapFactory.decodeResource(this.getResources(), R.drawable.red_marker)
                //);

                if (groupHeader.toLowerCase().contains("bwdb")) {
                    addBwdbProjLayerToMap(style);
                } else if (groupHeader.toLowerCase().contains("lged")) {
                    addLgedProjLayerToMap(style);
                } else if (groupHeader.toLowerCase().contains("flood prone")) {
                    addFloodProneLayerToMap(style);
                } else if (groupHeader.toLowerCase().contains("ground water zone")) {
                    addGroundWaterZoneLayerToMap(style);
                } else if (groupHeader.toLowerCase().contains("salinity area")) {
                    addSoilSalinityAreaLayerToMap(style);
                } else if (groupHeader.toLowerCase().contains("investment projects")) {
                    //addInvestmentProjectsLayerToMap(style);
                }
            }
        });

        mapboxMap.getUiSettings().setAttributionEnabled(false);
        mapboxMap.getUiSettings().setLogoEnabled(false);
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        PointF pointf = mapboxMap.getProjection().toScreenLocation(point);
        RectF rectF = new RectF(pointf.x - 10, pointf.y - 10, pointf.x + 10, pointf.y + 10);

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        ViewGroup viewGroup = root.findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(this.getContext()).inflate(R.layout.popup_viewer, viewGroup, false);
        TextView tv = dialogView.findViewById(R.id.popupHeader);
        WebView wv = dialogView.findViewById(R.id.webViewContent);
        Button btnOk = dialogView.findViewById(R.id.buttonOk);
        String regex = "[\\*?\"|']"; //String regex = "[\\/*?\"<>|']";
        String[] keyArray;
        String prjName = new String();
        List<Feature> featureList = mapboxMap.queryRenderedFeatures(rectF, PROJECT_FILL_LAYER);

        if (featureList.size() > 0) {
            //region getting table content properties from a single feature
            List<String> keys = new ArrayList<String>();

            for (Map.Entry<String, JsonElement> entry : featureList.get(0).properties().entrySet()) {
                //Log.e(TAG, String.format("%s = %s", entry.getKey(), entry.getValue()));
                keys.add(entry.getKey());
            }

            Collections.sort(keys);
            keyArray = new String[keys.size()];
            keys.toArray(keyArray);
            //endregion

            for (Feature feature : featureList) {
                if (feature.properties() != null) {
                    try {
                        if (groupHeader.toLowerCase().contains("bwdb")) {
                            prjName = feature.properties().get("2. Title").toString().replaceAll(regex, "");
                        } else if (groupHeader.toLowerCase().contains("lged")) {
                            prjName = feature.properties().get("SPNAME").toString().replaceAll(regex, "");
                        } else if (groupHeader.toLowerCase().contains("flood prone")) {
                            prjName = feature.properties().get("TYPE").toString().replaceAll(regex, "");
                        } else if (groupHeader.toLowerCase().contains("ground water zone")) {
                            prjName = feature.properties().get("GW_TABLE_M").toString().replaceAll(regex, "");
                        } else if (groupHeader.toLowerCase().contains("salinity area")) {
                            prjName = feature.properties().get("TYPE").toString().replaceAll(regex, "");
                        } else if (groupHeader.toLowerCase().contains("investment projects")) {
                            prjName = feature.properties().get("2. Title").toString().replaceAll(regex, "");
                        }

                        tv.setText(prjName);
                        tv.setTypeface(tf);
                    } catch (Exception ex) {

                    }

                    htmlRawContent = new String();
                    for (String key : keyArray) {
                        htmlRawContent += "<tr><td nowrap=\"nowrap\">" + key + "</td><td>" + feature.properties().get(key).toString().replaceAll(regex, "") + "</td></tr>";
                    }
                    htmlRawContent = GenerateHtmlContent.getHtmlTable(htmlRawContent);

                    wv.loadDataWithBaseURL(null, htmlRawContent, "text/HTML", "UTF-8", null);
                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    try {
                        btnOk.setOnClickListener(v -> alertDialog.dismiss());
                    } catch (Exception ex) {

                    }
                }
            }

            return true;
        }

        return false;
    }

    //layer position: 1
    //base layer :: administrative boundary
    private void addAdminBoundaryLayerToMap(@NonNull Style style) {
        try {
            GeoJsonSource source = new GeoJsonSource(ADMIN_BOUNDARY_LAYER, new URI("asset://admin/bgd.json"));
            style.addSource(source);

            style.addLayer(new FillLayer(ADMIN_BOUNDARY_FILL_LAYER, ADMIN_BOUNDARY_LAYER)
                    .withProperties(
                            fillColor(Color.parseColor("#F2F2F2")), fillOpacity(0.8f)
                    ));

            style.addLayer(new LineLayer(ADMIN_BOUNDARY_LINE_LAYER, ADMIN_BOUNDARY_LAYER)
                    .withProperties(
                            PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}),
                            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                            PropertyFactory.lineWidth(2f),
                            PropertyFactory.lineColor(Color.parseColor("#C95F09"))
                    ));

            SymbolLayer labelLayer = new SymbolLayer(PROJECT_LABEL_LAYER, PROJECT_LAYER)
                    .withProperties(
                            textField(get("GW_TABLE_M")),
                            textSize(10f),
                            textColor(Color.RED),
                            textVariableAnchor(new String[]{TEXT_ANCHOR_TOP, TEXT_ANCHOR_BOTTOM, TEXT_ANCHOR_LEFT, TEXT_ANCHOR_RIGHT}),
                            textJustify(TEXT_JUSTIFY_AUTO),
                            textRadialOffset(0.5f));

            style.addLayerAbove(labelLayer, ADMIN_BOUNDARY_FILL_LAYER);

            MapLegendItem mapLegendItem = new MapLegendItem();
            mapLegendItem.setTitle("Administrative Boundary");
            mapLegendItem.setSubTitle(EMPTY_STRING);
            mapLegendItem.setSourceLayerName(ADMIN_BOUNDARY_LAYER);
            mapLegendItem.setFillLayerName(ADMIN_BOUNDARY_FILL_LAYER);
            mapLegendItem.setFillLayerColor("#F2F2F2");
            mapLegendItem.setLineLayerName(ADMIN_BOUNDARY_LINE_LAYER);
            mapLegendItem.setLineStyle("Dashed 2f");
            mapLegendItem.setLineLayerColor("#C95F09");
            lstMapLegendItem.add(mapLegendItem);
        } catch (Throwable throwable) {
            Snackbar.make(this.getView(), "Could not add admin boundary source to map", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        }
    }

    //layer position: 2
    //region BWDB Project Layer
    private void addBwdbProjLayerToMap(@NonNull Style style) {
        getBwdbProjLayer();
    }

    private void getBwdbProjLayer() {
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
        retrofit2.Call call = api.getBwdbProjectLayer();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(retrofit2.Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        String data = response.body();

                        if (!data.isEmpty()) {
                            addBwdbProjLayerToMap(data);
                        } else {
                            Toast.makeText(getContext(), "Sorry, no data found!", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addBwdbProjLayerToMap(String data) {
        mapboxMap.getStyle(style -> {
            try {
                //GeoJsonSource source = new GeoJsonSource(PROJECT_LAYER, new URI("asset://water_resources_wgs84/bwdbprj.json"));
                GeoJsonSource source = new GeoJsonSource(PROJECT_LAYER, data);
                style.addSource(source);

                FillLayer fillLayer = new FillLayer(PROJECT_FILL_LAYER, PROJECT_LAYER);
                fillLayer.setProperties(
                        fillColor(Color.parseColor("#65A0C3")),
                        fillOpacity(0.8f)
                );

                style.addLayer(new LineLayer(PROJECT_LINE_LAYER, PROJECT_LAYER)
                        .withProperties(
                                PropertyFactory.lineWidth(1f),
                                PropertyFactory.lineColor(Color.parseColor("#3B7699"))
                        ));

                SymbolLayer labelLayer = new SymbolLayer(PROJECT_LABEL_LAYER, PROJECT_LAYER)
                        .withProperties(
                                textField(get("2. Title")),
                                textSize(10f),
                                textColor(Color.RED),
                                textVariableAnchor(new String[]{TEXT_ANCHOR_TOP, TEXT_ANCHOR_BOTTOM, TEXT_ANCHOR_LEFT, TEXT_ANCHOR_RIGHT}),
                                textJustify(TEXT_JUSTIFY_AUTO),
                                textRadialOffset(0.5f));

                SymbolLayer symbolLayer = new SymbolLayer(PROJECT_SYMBOL_LAYER, PROJECT_LAYER)
                        .withProperties(
                                PropertyFactory.iconImage(MAP_MARKER_ICON),
                                iconAllowOverlap(false),
                                iconIgnorePlacement(false),
                                iconOffset(new Float[]{0f, -2f})
                        );

                style.addLayerAbove(fillLayer, ADMIN_BOUNDARY_FILL_LAYER);
                style.addLayerAbove(labelLayer, PROJECT_FILL_LAYER);
                style.addLayerAbove(symbolLayer, PROJECT_LABEL_LAYER);

                Layer layer = style.getLayer(PROJECT_LABEL_LAYER);
                if (layer != null) {
                    layer.setProperties(visibility(NONE));
                }

                MapLegendItem mapLegendItem = new MapLegendItem();
                mapLegendItem.setTitle("BWDB Project");
                mapLegendItem.setSubTitle(EMPTY_STRING);
                mapLegendItem.setSourceLayerName(PROJECT_LAYER);
                mapLegendItem.setFillLayerName(PROJECT_FILL_LAYER);
                mapLegendItem.setFillLayerColor("#65A0C3");
                mapLegendItem.setLineLayerName(PROJECT_LINE_LAYER);
                mapLegendItem.setLineStyle("Solid 1f");
                mapLegendItem.setLineLayerColor("#3B7699");
                lstMapLegendItem.add(mapLegendItem);
            } catch (Throwable throwable) {
                Snackbar.make(this.getView(), "Couldn't add BWDB source to map", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }
        });
    }
    //endregion

    //layer position: 2
    //reigon LGED Project Layer
    private void addLgedProjLayerToMap(@NonNull Style style) {
//        try {
//            //URI uri = new URI(API.MAP_BASE_URL + "water_resources_wgs84/lgedprj.json");
//            //GeoJsonSource source = new GeoJsonSource(PROJECT_LAYER, uri);
//
//            GeoJsonSource source = new GeoJsonSource(PROJECT_LAYER, new URI("asset://water_resources_wgs84/lgedprj.json"));
//            style.addSource(source);
//
//            FillLayer fillLayer = new FillLayer(PROJECT_FILL_LAYER, PROJECT_LAYER);
//            fillLayer.setProperties(
//                    fillColor(Color.parseColor("#65A0C3")),
//                    fillOpacity(0.8f)
//            );
//
//            SymbolLayer labelLayer = new SymbolLayer(PROJECT_LABEL_LAYER, PROJECT_LAYER)
//                    .withProperties(
//                            textField(get("SPNAME")),
//                            textSize(10f),
//                            textColor(Color.RED),
//                            textVariableAnchor(new String[]{TEXT_ANCHOR_TOP, TEXT_ANCHOR_BOTTOM, TEXT_ANCHOR_LEFT, TEXT_ANCHOR_RIGHT}),
//                            textJustify(TEXT_JUSTIFY_AUTO),
//                            textRadialOffset(0.5f));
//
//            SymbolLayer symbolLayer = new SymbolLayer(PROJECT_SYMBOL_LAYER, PROJECT_LAYER)
//                    .withProperties(
//                            PropertyFactory.iconImage(MAP_MARKER_ICON),
//                            iconAllowOverlap(false),
//                            iconIgnorePlacement(false),
//                            iconOffset(new Float[]{0f, -2f})
//                    );
//
//            style.addLayerAbove(fillLayer, ADMIN_BOUNDARY_FILL_LAYER);
//            style.addLayerAbove(labelLayer, PROJECT_FILL_LAYER);
//            style.addLayerAbove(symbolLayer, PROJECT_LABEL_LAYER);
//
//            Layer layer = style.getLayer(PROJECT_LABEL_LAYER);
//            if (layer != null) {
//                layer.setProperties(visibility(NONE));
//            }
//        } catch (Throwable throwable) {
//            Snackbar.make(this.getView(), "Couldn't add LGED to map", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null)
//                    .show();
//        }
    }

    private void getLgedProjLayer() {
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
        retrofit2.Call call = api.getBwdbProjectLayer();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(retrofit2.Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        String data = response.body();

                        if (!data.isEmpty()) {
                            addBwdbProjLayerToMap(data);
                        } else {
                            Toast.makeText(getContext(), "Sorry, no data found!", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addLgedProjLayerToMap(String data) {
        mapboxMap.getStyle(style -> {
            try {
                //GeoJsonSource source = new GeoJsonSource(PROJECT_LAYER, new URI("asset://water_resources_wgs84/bwdbprj.json"));
                GeoJsonSource source = new GeoJsonSource(PROJECT_LAYER, data);
                style.addSource(source);

                FillLayer fillLayer = new FillLayer(PROJECT_FILL_LAYER, PROJECT_LAYER);
                fillLayer.setProperties(
                        fillColor(Color.parseColor("#65A0C3")),
                        fillOpacity(0.8f)
                );

                style.addLayer(new LineLayer(PROJECT_LINE_LAYER, PROJECT_LAYER)
                        .withProperties(
                                PropertyFactory.lineWidth(1f),
                                PropertyFactory.lineColor(Color.parseColor("#3B7699"))
                        ));

                SymbolLayer labelLayer = new SymbolLayer(PROJECT_LABEL_LAYER, PROJECT_LAYER)
                        .withProperties(
                                textField(get("2. Title")),
                                textSize(10f),
                                textColor(Color.RED),
                                textVariableAnchor(new String[]{TEXT_ANCHOR_TOP, TEXT_ANCHOR_BOTTOM, TEXT_ANCHOR_LEFT, TEXT_ANCHOR_RIGHT}),
                                textJustify(TEXT_JUSTIFY_AUTO),
                                textRadialOffset(0.5f));

                SymbolLayer symbolLayer = new SymbolLayer(PROJECT_SYMBOL_LAYER, PROJECT_LAYER)
                        .withProperties(
                                PropertyFactory.iconImage(MAP_MARKER_ICON),
                                iconAllowOverlap(false),
                                iconIgnorePlacement(false),
                                iconOffset(new Float[]{0f, -2f})
                        );

                style.addLayerAbove(fillLayer, ADMIN_BOUNDARY_FILL_LAYER);
                style.addLayerAbove(labelLayer, PROJECT_FILL_LAYER);
                style.addLayerAbove(symbolLayer, PROJECT_LABEL_LAYER);

                Layer layer = style.getLayer(PROJECT_LABEL_LAYER);
                if (layer != null) {
                    layer.setProperties(visibility(NONE));
                }

                MapLegendItem mapLegendItem = new MapLegendItem();
                mapLegendItem.setTitle("BWDB Project");
                mapLegendItem.setSubTitle(EMPTY_STRING);
                mapLegendItem.setSourceLayerName(PROJECT_LAYER);
                mapLegendItem.setFillLayerName(PROJECT_FILL_LAYER);
                mapLegendItem.setFillLayerColor("#65A0C3");
                mapLegendItem.setLineLayerName(PROJECT_LINE_LAYER);
                mapLegendItem.setLineStyle("Solid 1f");
                mapLegendItem.setLineLayerColor("#3B7699");
                lstMapLegendItem.add(mapLegendItem);
            } catch (Throwable throwable) {
                Snackbar.make(this.getView(), "Couldn't add BWDB source to map", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }
        });
    }
    //endregion

    //layer position: 2
    //ground water zone layer
    private void addGroundWaterZoneLayerToMap(@NonNull Style style) {
        try {
            //URI uri = new URI(API.MAP_BASE_URL + "water_resources_wgs84/gwzone.json");
            //GeoJsonSource source = new GeoJsonSource(PROJECT_LAYER, uri);

            GeoJsonSource source = new GeoJsonSource(PROJECT_LAYER, new URI("asset://water_resources_wgs84/lgedprj.json"));
            style.addSource(source);

            FillLayer fillLayer = new FillLayer(PROJECT_FILL_LAYER, PROJECT_LAYER);
            fillLayer.setProperties(
                    fillColor(Color.parseColor("#9ED9FC")),
                    fillOpacity(0.8f)
            );

            SymbolLayer labelLayer = new SymbolLayer(PROJECT_LABEL_LAYER, PROJECT_LAYER)
                    .withProperties(
                            textField(get("GW_TABLE_M")),
                            textSize(10f),
                            textColor(Color.RED),
                            textVariableAnchor(new String[]{TEXT_ANCHOR_TOP, TEXT_ANCHOR_BOTTOM, TEXT_ANCHOR_LEFT, TEXT_ANCHOR_RIGHT}),
                            textJustify(TEXT_JUSTIFY_AUTO),
                            textRadialOffset(0.5f));

            SymbolLayer symbolLayer = new SymbolLayer(PROJECT_SYMBOL_LAYER, PROJECT_LAYER)
                    .withProperties(
                            PropertyFactory.iconImage(MAP_MARKER_ICON),
                            iconAllowOverlap(false),
                            iconIgnorePlacement(false),
                            iconOffset(new Float[]{0f, -2f})
                    );

            style.addLayerAbove(fillLayer, ADMIN_BOUNDARY_FILL_LAYER);
            style.addLayerAbove(labelLayer, PROJECT_FILL_LAYER);
            style.addLayerAbove(symbolLayer, PROJECT_LABEL_LAYER);

            Layer layer = style.getLayer(PROJECT_LABEL_LAYER);
            if (layer != null) {
                layer.setProperties(visibility(NONE));
            }
        } catch (Throwable throwable) {
            Snackbar.make(this.getView(), "Couldn't add ground water zone to map", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        }
    }

    //layer position: 2
    //soil salinity area layer
    private void addSoilSalinityAreaLayerToMap(@NonNull Style style) {
        try {
            //URI uri = new URI(API.MAP_BASE_URL + "water_resources_wgs84/soil_salanity_area.json");
            //GeoJsonSource source = new GeoJsonSource(PROJECT_LAYER, uri);

            GeoJsonSource source = new GeoJsonSource(PROJECT_LAYER, new URI("asset://water_resources_wgs84/soil_salanity_area.json"));
            style.addSource(source);

            FillLayer fillLayer = new FillLayer(PROJECT_FILL_LAYER, PROJECT_LAYER);
            fillLayer.setProperties(
                    fillColor(Color.parseColor("#9A9657")),
                    fillOpacity(0.8f)
            );

            SymbolLayer labelLayer = new SymbolLayer(PROJECT_LABEL_LAYER, PROJECT_LAYER)
                    .withProperties(
                            textField(get("TYPE")),
                            textSize(10f),
                            textColor(Color.RED),
                            textVariableAnchor(new String[]{TEXT_ANCHOR_TOP, TEXT_ANCHOR_BOTTOM, TEXT_ANCHOR_LEFT, TEXT_ANCHOR_RIGHT}),
                            textJustify(TEXT_JUSTIFY_AUTO),
                            textRadialOffset(0.5f));

            SymbolLayer symbolLayer = new SymbolLayer(PROJECT_SYMBOL_LAYER, PROJECT_LAYER)
                    .withProperties(
                            PropertyFactory.iconImage(MAP_MARKER_ICON),
                            iconAllowOverlap(false),
                            iconIgnorePlacement(false),
                            iconOffset(new Float[]{0f, -2f})
                    );

            style.addLayerAbove(fillLayer, ADMIN_BOUNDARY_FILL_LAYER);
            style.addLayerAbove(labelLayer, PROJECT_FILL_LAYER);
            style.addLayerAbove(symbolLayer, PROJECT_LABEL_LAYER);

            Layer layer = style.getLayer(PROJECT_LABEL_LAYER);
            if (layer != null) {
                layer.setProperties(visibility(NONE));
            }
        } catch (Throwable throwable) {
            Snackbar.make(this.getView(), "Couldn't add salinity area to map", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        }
    }

    //layer position: 2
    //flood prone area layer
    private void addFloodProneLayerToMap(@NonNull Style style) {
        try {
            //URI uri = new URI(API.MAP_BASE_URL + "water_resources_wgs84/flood_prone.json");
            //GeoJsonSource source = new GeoJsonSource(PROJECT_LAYER, uri);

            GeoJsonSource source = new GeoJsonSource(PROJECT_LAYER, new URI("asset://water_resources_wgs84/flood_prone.json"));
            style.addSource(source);

            FillLayer fillLayer = new FillLayer(PROJECT_FILL_LAYER, PROJECT_LAYER);
            fillLayer.setProperties(
                    fillColor(Color.parseColor("#9A9657")),
                    fillOpacity(0.8f)
            );

            SymbolLayer labelLayer = new SymbolLayer(PROJECT_LABEL_LAYER, PROJECT_LAYER)
                    .withProperties(
                            textField(get("TYPE")),
                            textSize(10f),
                            textColor(Color.RED),
                            textVariableAnchor(new String[]{TEXT_ANCHOR_TOP, TEXT_ANCHOR_BOTTOM, TEXT_ANCHOR_LEFT, TEXT_ANCHOR_RIGHT}),
                            textJustify(TEXT_JUSTIFY_AUTO),
                            textRadialOffset(0.5f));

            SymbolLayer symbolLayer = new SymbolLayer(PROJECT_SYMBOL_LAYER, PROJECT_LAYER)
                    .withProperties(
                            PropertyFactory.iconImage(MAP_MARKER_ICON),
                            iconAllowOverlap(false),
                            iconIgnorePlacement(false),
                            iconOffset(new Float[]{0f, -2f})
                    );

            style.addLayerAbove(fillLayer, ADMIN_BOUNDARY_FILL_LAYER);
            style.addLayerAbove(labelLayer, PROJECT_FILL_LAYER);
            style.addLayerAbove(symbolLayer, PROJECT_LABEL_LAYER);

            Layer layer = style.getLayer(PROJECT_LABEL_LAYER);
            if (layer != null) {
                layer.setProperties(visibility(NONE));
            }
        } catch (Throwable throwable) {
            Snackbar.make(this.getView(), "Couldn't add salinity area to map", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        }
    }

    //layer position: 2
    //flood prone area layer
    private void addInvestmentProjectsLayerToMap(@NonNull Style style) {
        try {
            GeoJsonSource source = new GeoJsonSource(PROJECT_LAYER, new URI("asset://investment_project/1.json"));
            style.addSource(source);

            FillLayer fillLayer = new FillLayer(PROJECT_FILL_LAYER, PROJECT_LAYER);
            fillLayer.setProperties(
                    fillColor(Color.parseColor("#C812C8")),
                    fillOpacity(0.8f)
            );

            SymbolLayer labelLayer = new SymbolLayer(PROJECT_LABEL_LAYER, PROJECT_LAYER)
                    .withProperties(
                            textField(get("name")),
                            textSize(10f),
                            textColor(Color.RED),
                            textVariableAnchor(new String[]{TEXT_ANCHOR_TOP, TEXT_ANCHOR_BOTTOM, TEXT_ANCHOR_LEFT, TEXT_ANCHOR_RIGHT}),
                            textJustify(TEXT_JUSTIFY_AUTO),
                            textRadialOffset(0.5f));

            SymbolLayer symbolLayer = new SymbolLayer(PROJECT_SYMBOL_LAYER, PROJECT_LAYER)
                    .withProperties(
                            PropertyFactory.iconImage(MAP_MARKER_ICON),
                            iconAllowOverlap(false),
                            iconIgnorePlacement(false),
                            iconOffset(new Float[]{0f, -2f})
                    );

            style.addLayerAbove(fillLayer, ADMIN_BOUNDARY_FILL_LAYER);
            style.addLayerAbove(labelLayer, PROJECT_FILL_LAYER);
            style.addLayerAbove(symbolLayer, PROJECT_LABEL_LAYER);

            //            Layer layer = style.getLayer(PROJECT_LABEL_LAYER);
            //            if (layer != null) {
            //                layer.setProperties(visibility(NONE));
            //            }
        } catch (Throwable throwable) {
            Snackbar.make(this.getView(), "Couldn't add base investment project to map", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        }
    }

    //call level: 3
    //layer position: 3
    //Selected Investment Project Layer
    private void addSelectedInvProjLayerToMap(String data, String item) {
        mapboxMap.getStyle(style -> {
            Source oldSource = style.getSource(PROJECT_LAYER);

            if (oldSource != null) {
                String id = oldSource.getId();

                style.removeLayer(PROJECT_LABEL_LAYER);
                //style.removeLayer(PROJECT_SYMBOL_LAYER);
                style.removeLayer(PROJECT_FILL_LAYER);
                style.removeLayer(PROJECT_LINE_LAYER);
                style.removeLayer(PROJECT_LAYER);
                style.removeSource(id);
            }
        });

        mapboxMap.getStyle(style -> {
            try {
                GeoJsonSource source = new GeoJsonSource(PROJECT_LAYER, data);
                style.addSource(source);

                FillLayer fillLayer = new FillLayer(PROJECT_FILL_LAYER, PROJECT_LAYER);
                fillLayer.setProperties(
                        fillColor(Color.parseColor("#27A6E1")),
                        fillOpacity(0.6f)
                );

                LineLayer lineLayer = new LineLayer(PROJECT_LINE_LAYER, PROJECT_LAYER);
                lineLayer.withProperties(
                        PropertyFactory.lineWidth(1f),
                        PropertyFactory.lineColor(Color.parseColor("#2A5F78"))
                );

                SymbolLayer labelLayer = new SymbolLayer(PROJECT_LABEL_LAYER, PROJECT_LAYER)
                        .withProperties(
                                textField(get("2. Title")),
                                textSize(10f),
                                textColor(Color.parseColor("#0A445F")),
                                textVariableAnchor(new String[]{TEXT_ANCHOR_TOP, TEXT_ANCHOR_BOTTOM, TEXT_ANCHOR_LEFT, TEXT_ANCHOR_RIGHT}),
                                textJustify(TEXT_JUSTIFY_AUTO),
                                textRadialOffset(0.5f));

                //SymbolLayer symbolLayer = new SymbolLayer(PROJECT_SYMBOL_LAYER, PROJECT_LAYER)
                //        .withProperties(
                //                PropertyFactory.iconImage(MAP_MARKER_ICON),
                //                iconAllowOverlap(false),
                //                iconIgnorePlacement(false),
                //                iconOffset(new Float[]{0f, -2f})
                //        );

                style.addLayerAbove(fillLayer, ADMIN_BOUNDARY_FILL_LAYER);
                style.addLayerAbove(lineLayer, PROJECT_FILL_LAYER);
                style.addLayerAbove(labelLayer, PROJECT_FILL_LAYER);
                //style.addLayerAbove(symbolLayer, PROJECT_FILL_LAYER);

                Layer lblLayer = style.getLayer(PROJECT_LABEL_LAYER);
                if (lblLayer != null) {
                    lblLayer.setProperties(visibility(NONE));
                }

                MapLegendItem mapLegendItem = new MapLegendItem();
                mapLegendItem.setTitle("Investment Project");
                mapLegendItem.setSubTitle(item);
                mapLegendItem.setSourceLayerName(PROJECT_LAYER);
                mapLegendItem.setFillLayerName(PROJECT_FILL_LAYER);
                mapLegendItem.setFillLayerColor("#65A0C3");
                mapLegendItem.setLineLayerName(PROJECT_LINE_LAYER);
                mapLegendItem.setLineStyle("Solid 1f");
                mapLegendItem.setLineLayerColor("#2A5F78");
                lstMapLegendItem.add(mapLegendItem);
            } catch (Throwable throwable) {
                Snackbar.make(this.getView(), "Couldn't add this investment project layer to map. " + throwable.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }
        });
    }

    //call level: 1
    //spinner loading
    //investment project
    private void loadInvProjHotspotData(Spinner spinner) {
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
        retrofit2.Call call = api.getInvestmentProjectHotspotList();

        call.enqueue(new Callback<List<InvestmentProjectList>>() {
            @Override
            public void onResponse(retrofit2.Call<List<InvestmentProjectList>> call, Response<List<InvestmentProjectList>> response) {
                if (response.isSuccessful()) {
                    try {
                        List<InvestmentProjectList> responseList = response.body();
                        listHotSpot = new ArrayList<>();

                        if (responseList.size() > 0) {
                            lstInvestmentProjectHotspotList = new ArrayList<>();

                            for (int i = 0; i < responseList.size(); i++) {
                                InvestmentProjectList spinnerModel = new InvestmentProjectList();
                                spinnerModel.setCode(responseList.get(i).getCode());
                                spinnerModel.setName(responseList.get(i).getName());
                                lstInvestmentProjectHotspotList.add(spinnerModel);

                                listHotSpot.add(responseList.get(i).getName());
                            }

                            spinner.setAdapter(null);
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), simple_spinner_item, listHotSpot);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(spinnerArrayAdapter);

                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    String item = (String) parent.getItemAtPosition(position);
                                    String code = lstInvestmentProjectHotspotList.get(position).getCode();

                                    loadInvestmentProjectData(spnrInvestProjList, code);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        } else {
                            list = new ArrayList<>();
                            lstInvestmentProjectHotspotList = new ArrayList<>();

                            Toast.makeText(getContext(), "Sorry, no data found!", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        list = new ArrayList<>();
                        lstInvestmentProjectHotspotList = new ArrayList<>();

                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<InvestmentProjectList>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadInvestmentProjectData(Spinner spinner, String hotspot) {
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
        retrofit2.Call call = api.getInvestmentProjectList(hotspot);

        call.enqueue(new Callback<List<InvestmentProjectList>>() {
            @Override
            public void onResponse(retrofit2.Call<List<InvestmentProjectList>> call, Response<List<InvestmentProjectList>> response) {
                if (response.isSuccessful()) {
                    try {
                        List<InvestmentProjectList> responseList = response.body();
                        list = new ArrayList<>();

                        if (responseList.size() > 0) {
                            spnrInvestProjList.setVisibility(View.VISIBLE);
                            lstInvestmentProjectList = new ArrayList<>();

                            for (int i = 0; i < responseList.size(); i++) {
                                InvestmentProjectList spinnerModel = new InvestmentProjectList();
                                spinnerModel.setCode(responseList.get(i).getCode());
                                spinnerModel.setName(responseList.get(i).getName());
                                lstInvestmentProjectList.add(spinnerModel);

                                list.add(responseList.get(i).getName());
                            }

                            spinner.setAdapter(null);
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), simple_spinner_item, list);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(spinnerArrayAdapter);

                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    String item = (String) parent.getItemAtPosition(position);
                                    String code = lstInvestmentProjectList.get(position).getCode();

                                    getSelectedInvProjLayer(item, code);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        } else {
                            spinner.setAdapter(null);
                            spnrInvestProjList.setVisibility(View.GONE);
                            list = new ArrayList<>();
                            lstInvestmentProjectList = new ArrayList<>();

                            Toast.makeText(getContext(), "Sorry, no data found!", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        spinner.setAdapter(null);
                        spnrInvestProjList.setVisibility(View.GONE);
                        list = new ArrayList<>();
                        lstInvestmentProjectList = new ArrayList<>();

                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<InvestmentProjectList>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //call level: 2
    private void getSelectedInvProjLayer(String item, String code) {
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
        retrofit2.Call call = api.getInvestmentProjectLayer(code);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(retrofit2.Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        String data = response.body();

                        if (!data.isEmpty()) {
                            addSelectedInvProjLayerToMap(data, item);
                        } else {
                            Toast.makeText(getContext(), "Sorry, no data found!", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

//            // Create and customize the LocationComponent's options
//            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(getContext())
//                    .elevation(5)
//                    .accuracyAlpha(.8f)
//                    .accuracyColor(Color.BLUE)
//                    .foregroundDrawable(R.drawable.my_location)
//                    .build();
//
//            LocationComponentActivationOptions locationComponentActivationOptions =
//                    LocationComponentActivationOptions.builder(getContext(), loadedMapStyle)
//                            .locationComponentOptions(customLocationComponentOptions)
//                            .build();
//
//            // Activate with options
//            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Activate with options
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(getContext(), loadedMapStyle).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this.getActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getContext(), R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(getContext(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            //finish();
        }
    }

    private void fabDetailsClick() {
        getMapLegendInfo();
    }

    private void getMapLegendInfo() {
        //mapboxMap.getStyle(style -> {
        //    List<Source> sources = style.getSources();
        //    if (sources.size() > 0) {
        //        textView = "<span style='color: #97D44C; font-weight: bold;'>Layers</span><br />";
        //        for (Source src : sources) {
        //            if (!src.getId().equals("composite") && !src.getId().equals("com.mapbox.annotations")) {
        //                GeoJsonSource gjs = style.getSourceAs(src.getId());
        //                List<Feature> testList = gjs.querySourceFeatures(Expression.literal(tr
        //                //textView += src.getId() + "___";
        //                String sourceName = src.getId();
        //                String contentLength = "" + testList.si
        //                textView += "Source: " + sourceName +
        //                        "<br />Contents: " + contentLength + " Polygon Features<br />";
        //            }
        //        }
        //    }
        //});
        //        String text = "<span style='color: #97D44C; font-weight: bold;'>Layers</span><br />" +
        //                "<div style='border-bottom: 1px solid #FFFFFF !important;'>" +
        //                "Name: Administrative Boundary<br />" +
        //                "Source: ADMIN_BOUNDARY_LAYER<br />" +
        //                "Contents: 500 Polygon Features<br />" +
        //                "Line: <span style='color: #C95F09;'>— --</span><br />" +
        //                "</div>" +
        //                "<div style='border-top: 1px solid #FFFFFF;'>" +
        //                "Name: Investment Project Layer<br />" +
        //                "Source: ADMIN_BOUNDARY_LAYER<br />" +
        //                "Contents: 250 Polygon Features<br />" +
        //                "Line: <span style='color: #C95F09;'>—</span><br />" +
        //                "</div>";

        Style style = mapboxMap.getStyle();
        if (lstMapLegendItem.size() > 0) {
            textView = "<span style='color: #97D44C; font-weight: bold;'>Layers</span><br />";
            for (MapLegendItem li : lstMapLegendItem) {
                textView += "Title: " + li.getTitle();
                textView += li.getSubTitle().isEmpty() ? "" : "<br />Sub Title: " + li.getSubTitle();
                textView += li.getSourceLayerName().isEmpty() ? "" : "<br />Source Layer: " + li.getSourceLayerName();
                textView += li.getFillLayerName().isEmpty() ? "" : "<br />Fill Layer: " + li.getFillLayerName();
                textView += li.getFillLayerColor().isEmpty() ? "" : "<br />Fill Color: <i style='width: 50px; height: 50px; background-color: " + li.getFillLayerColor() + ";'></i>" + li.getFillLayerColor();
                textView += li.getLineLayerName().isEmpty() ? "" : "<br />Line Layer: " + li.getLineLayerName();
                textView += li.getLineStyle().isEmpty() ? "" : "<br />Line Style: " + li.getLineStyle();
                textView += li.getLineLayerColor().isEmpty() ? "" : "<br />Line Color: " + li.getLineLayerColor();

                GeoJsonSource gjs = style.getSourceAs(li.getSourceLayerName());
                List<Feature> features = gjs.querySourceFeatures(Expression.literal(true));
                textView += "<br />Contents: " + features.size() + " Polygon Features<br />";
            }
        }

        Snackbar snackbar = Snackbar.make(this.getView(), textView, Snackbar.LENGTH_INDEFINITE);
        TextView tv = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setSingleLine(false);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tv.setText(Html.fromHtml(textView, Html.FROM_HTML_MODE_LEGACY));
        } else {
            tv.setText(Html.fromHtml(textView));
        }

        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
                //your other action after user hit the "OK" button
            }
        });

        snackbar.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mapboxMap != null) {
            mapboxMap.removeOnMapClickListener(MapFragment.this::onMapClick);
        }

        mapView.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void menuBaseLayer() {
        //mapboxMap.removeLayer(mapboxMap.getLayers().get(i));
        mapboxMap.getStyle(style -> {
            Layer layer = style.getLayer(ADMIN_BOUNDARY_FILL_LAYER);

            if (layer != null) {
                if (VISIBLE.equals(layer.getVisibility().getValue())) {
                    layer.setProperties(visibility(NONE));
                } else {
                    layer.setProperties(visibility(VISIBLE));
                }
            }
        });
    }

    private void menuLabel() {
        mapboxMap.getStyle(style -> {
            Layer layer = style.getLayer(PROJECT_LABEL_LAYER);

            if (layer != null) {
                if (VISIBLE.equals(layer.getVisibility().getValue())) {
                    layer.setProperties(visibility(NONE));
                } else {
                    layer.setProperties(visibility(VISIBLE));

                    layer = style.getLayer(PROJECT_SYMBOL_LAYER);
                    if (layer != null) {
                        layer.setProperties(visibility(NONE));
                    }
                }
            }
        });
    }

    private void menuMarker() {
        mapboxMap.getStyle(style -> {
            Layer layer = style.getLayer(PROJECT_SYMBOL_LAYER);

            if (layer != null) {
                if (VISIBLE.equals(layer.getVisibility().getValue())) {
                    layer.setProperties(visibility(NONE));
                } else {
                    layer.setProperties(visibility(VISIBLE));

                    layer = style.getLayer(PROJECT_LABEL_LAYER);
                    if (layer != null) {
                        layer.setProperties(visibility(NONE));
                    }
                }
            }
        });
    }

    private void menuGetMyCurrentLocation() {
        mapboxMap.getStyle(style -> {
            enableLocationComponent(style);
        });

        //View decorView = this.getActivity().getWindow().getDecorView();
        //int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        //decorView.setSystemUiVisibility(uiOptions);
    }

    private void menu100PView() {
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(23.781568, 90.4112203))
                .zoom(6)
                .tilt(20)
                .build();

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 30);
    }

    private void showFabMenu() {
        if (!isFabOpen) {
            showFloatingMenu();
        } else {
            closeFloatingMenu();
        }
    }

    private void showFloatingMenu() {
        isFabOpen = true;

        fab0.animate().translationY(-this.getResources().getDimension(R.dimen.standard_180));
        fab1.animate().translationY(-this.getResources().getDimension(R.dimen.standard_60));
        fab2.animate().translationY(-this.getResources().getDimension(R.dimen.standard_120));

        fab3.animate().translationX(-this.getResources().getDimension(R.dimen.standard_120));
        fab4.animate().translationX(-this.getResources().getDimension(R.dimen.standard_60));
    }

    private void closeFloatingMenu() {
        isFabOpen = false;

        fab0.animate().translationY(0);
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);

        fab3.animate().translationX(0);
        fab4.animate().translationX(0);
    }

    // function to generate a random string of length n
    private String GenerateColorCode(int n) {
        String AlphaNumericString = "ABCDEF0123456789";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();
    }
}