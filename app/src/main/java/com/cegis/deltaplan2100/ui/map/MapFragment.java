package com.cegis.deltaplan2100.ui.map;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.cegis.deltaplan2100.MainActivity;
import com.cegis.deltaplan2100.R;
import com.cegis.deltaplan2100.utility.GenerateTable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonElement;
import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.mapbox.mapboxsdk.style.expressions.Expression.exponential;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_LEFT;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_RIGHT;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_TOP;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_JUSTIFY_AUTO;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;

import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textJustify;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textRadialOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textVariableAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static java.nio.file.Paths.get;

import static com.mapbox.mapboxsdk.style.expressions.Expression.division;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.gte;
import static com.mapbox.mapboxsdk.style.expressions.Expression.has;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.lt;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class MapFragment extends Fragment implements OnMapReadyCallback, MapboxMap.OnMapClickListener {
    private MapViewModel mViewModel;
    private View root;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private FloatingActionButton fab, fab0, fab1, fab2, fab3, fab4;

    private int itemID, itemParentLevel;
    private String groupHeader, itemContentAs;
    private boolean isFabOpen = false, isFabSubOpen = false;

    private String htmlRawContent = new String();

    private static final String ADMIN_BOUNDARY_LAYER = "admBoundaryLayer";
    private static final String ADMIN_BOUNDARY_LINE_LAYER = "admBoundaryLineLayer";
    private static final String ADMIN_BOUNDARY_FILL_LAYER = "admBoundaryFillLayer";

    private static final String PROJECT_LAYER = "projectLayer";
    private static final String PROJECT_FILL_LAYER = "projectFillLayer";
    private static final String PROJECT_LABEL_LAYER = "projectLabelLayer";
    private static final String PROJECT_SYMBOL_LAYER = "projectSymbolLayer";

    private static final String MAP_MARKER_ICON = "map_marker_icon";

    Typeface tf;

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

        //region loading data
        ((MainActivity) getActivity()).setToolBar(groupHeader);
        //endregion

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
        TooltipCompat.setTooltipText(fab3, "Fullscreen");
        fab3.setAlpha(0.75f);
        fab3.setOnClickListener(view -> menuFullScreen());

        fab4 = root.findViewById(R.id.fab4);
        TooltipCompat.setTooltipText(fab4, "100% View");
        fab4.setAlpha(0.75f);
        fab4.setOnClickListener(view -> menu100PView());

        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
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
        String regex = "[\\/*?\"<>|']";
        String[] keyArray;
        String prjName = new String();

        //region LGED_PROJECT
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
                    if (groupHeader.toLowerCase().contains("bwdb")) {
                        prjName = feature.properties().get("SNAME").toString().replaceAll(regex, "");
                    } else if (groupHeader.toLowerCase().contains("lged")) {
                        prjName = feature.properties().get("SPNAME").toString().replaceAll(regex, "");
                    }

                    tv.setText(prjName);
                    tv.setTypeface(tf);

                    htmlRawContent = new String();
                    for (String key : keyArray) {
                        htmlRawContent += "<tr><td>" + key + "</td><td>" + feature.properties().get(key).toString().replaceAll(regex, "") + "</td></tr>";
                    }
                    htmlRawContent = GenerateTable.getHtmlTable(htmlRawContent);

                    wv.loadDataWithBaseURL(null, htmlRawContent, "text/HTML", "UTF-8", null);
                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    btnOk.setOnClickListener(v -> alertDialog.dismiss());
                }
            }

            return true;
        }
        //endregion

        return false;
    }

    //layer position: 1
    //base layer :: administrative boundary
    private void addAdminBoundaryLayerToMap(@NonNull Style style) {
        try {
            GeoJsonSource source = new GeoJsonSource(ADMIN_BOUNDARY_LAYER, new URI("asset://bgd.json"));
            style.addSource(source);

            style.addLayer(new LineLayer(ADMIN_BOUNDARY_LINE_LAYER, ADMIN_BOUNDARY_LAYER)
                    .withProperties(
                            PropertyFactory.lineWidth(1f),
                            PropertyFactory.lineColor(Color.parseColor("#165F8B"))
                    ));

            style.addLayer(new FillLayer(ADMIN_BOUNDARY_FILL_LAYER, ADMIN_BOUNDARY_LAYER)
                    .withProperties(
                            fillColor(Color.parseColor("#F2F2F2")), fillOpacity(0.8f)
                    ));
        } catch (Throwable throwable) {
            Snackbar.make(this.getView(), "Couldn't add GeoJsonSource to map - %s", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        }
    }

    //layer position: 2
    //lged project layer
    private void addLgedProjLayerToMap(@NonNull Style style) {
        try {
            GeoJsonSource source = new GeoJsonSource(PROJECT_LAYER, new URI("asset://lgedprj.json"));
            style.addSource(source);

            FillLayer lgedprjArea = new FillLayer(PROJECT_FILL_LAYER, PROJECT_LAYER);
            lgedprjArea.setProperties(
                    fillColor(Color.parseColor("#65A0C3")),
                    fillOpacity(0.8f)
            );

            SymbolLayer labelLayer = new SymbolLayer(PROJECT_LABEL_LAYER, PROJECT_LAYER)
                    .withProperties(
                            textField(get("SPNAME")),
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

            style.addLayerAbove(lgedprjArea, ADMIN_BOUNDARY_FILL_LAYER);
            style.addLayerAbove(labelLayer, PROJECT_FILL_LAYER);
            style.addLayerAbove(symbolLayer, PROJECT_LABEL_LAYER);

            Layer layer = style.getLayer(PROJECT_LABEL_LAYER);
            if (layer != null) {
                layer.setProperties(visibility(NONE));
            }
        } catch (Throwable throwable) {
            Snackbar.make(this.getView(), "Couldn't add LGED to map - %s", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        }
    }

    //layer position: 2
    //bwdb project layer
    private void addBwdbProjLayerToMap(@NonNull Style style) {
        try {
            GeoJsonSource source = new GeoJsonSource(PROJECT_LAYER, new URI("asset://bwdbprj.json"));
            style.addSource(source);

            FillLayer lgedprjArea = new FillLayer(PROJECT_FILL_LAYER, PROJECT_LAYER);
            lgedprjArea.setProperties(
                    fillColor(Color.parseColor("#65A0C3")),
                    fillOpacity(0.8f)
            );

            SymbolLayer labelLayer = new SymbolLayer(PROJECT_LABEL_LAYER, PROJECT_LAYER)
                    .withProperties(
                            textField(get("SNAME")),
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

            style.addLayerAbove(lgedprjArea, ADMIN_BOUNDARY_FILL_LAYER);
            style.addLayerAbove(labelLayer, PROJECT_FILL_LAYER);
            style.addLayerAbove(symbolLayer, PROJECT_LABEL_LAYER);

            Layer layer = style.getLayer(PROJECT_LABEL_LAYER);
            if (layer != null) {
                layer.setProperties(visibility(NONE));
            }
        } catch (Throwable throwable) {
            Snackbar.make(this.getView(), "Couldn't add BWDB to map - %s", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void menuBaseLayer() {
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

    private void menuFullScreen() {
        View decorView = this.getActivity().getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
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

        fab3.animate().translationX(-this.getResources().getDimension(R.dimen.standard_60));
        fab4.animate().translationX(-this.getResources().getDimension(R.dimen.standard_120));
    }

    private void closeFloatingMenu() {
        isFabOpen = false;

        fab0.animate().translationY(0);
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);

        fab3.animate().translationX(0);
        fab4.animate().translationX(0);
    }
}