package com.cegis.deltaplan2100.ui.map;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.cegis.deltaplan2100.MainActivity;
import com.cegis.deltaplan2100.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
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

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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
    private FloatingActionButton fab, fab1, fab2, fab3;

    private int itemID, itemParentLevel;
    private String groupHeader, itemContentAs;
    private boolean isFabOpen = false;

    private static final String GEOJSON_SRC_ID = "lgedprj";
    private static final String POI_LABELS_LAYER_ID = "poi_labels_layer_id";
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

        fab = root.findViewById(R.id.fab);
        TooltipCompat.setTooltipText(fab, "Menu");
        fab.setOnClickListener(view -> showFabMenu());

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
        fab3.setOnClickListener(view -> menuFullscreen());

        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return root;
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            mapboxMap.addOnMapClickListener(MapFragment.this::onMapClick);
            addGeoJsonSourceToMap(style);

            if (style != null) {
                mapboxMap.getStyle().addImage(
                        "lgedprj_icon",
                        BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_marker_20)
                );

                // if want to different types of feature icon
                //mapboxMap.getStyle().addImage(
                //        "sample_icon",
                //        BitmapFactory.decodeResource(this.getResources(), R.drawable.red_marker)
                //);

                style.addLayer(new LineLayer("lineLayerID", "bgd")
                        .withProperties(
                                PropertyFactory.lineWidth(1f),
                                PropertyFactory.lineColor(Color.parseColor("#165F8B"))
                        ));

                style.addLayer(new FillLayer("bgdJsonLayer", "bgd")
                        .withProperties(
                                fillColor(Color.parseColor("#F2F2F2")), fillOpacity(0.8f)
                        ));

                try {
                    GeoJsonSource lgedprj = new GeoJsonSource("lgedprj", new URI("asset://lgedprj.geojson"));
                    style.addSource(lgedprj);
                    FillLayer lgedprjArea = new FillLayer("lgedprjLayerID", "lgedprj");

                    lgedprjArea.setProperties(
                            fillColor(Color.parseColor("#65A0C3")),
                            fillOpacity(0.8f)
                    );

                    SymbolLayer symbolLayer = new SymbolLayer("lgedprjSymbolLayerID", "lgedprj")
                            .withProperties(
                                    PropertyFactory.iconImage("lgedprj_icon"),
                                    iconAllowOverlap(false),
                                    iconIgnorePlacement(false),
                                    iconOffset(new Float[]{0f, -2f})
                            );

                    SymbolLayer labelLayer = new SymbolLayer("lgedprjLabelLayerID", "lgedprj")
                            .withProperties(
                                    textField(get("SPNAME")),
                                    textSize(10f),
                                    textColor(Color.RED),
                                    textVariableAnchor(new String[]{TEXT_ANCHOR_TOP, TEXT_ANCHOR_BOTTOM, TEXT_ANCHOR_LEFT, TEXT_ANCHOR_RIGHT}),
                                    textJustify(TEXT_JUSTIFY_AUTO),
                                    textRadialOffset(0.5f));

                    style.addLayerAbove(lgedprjArea, "bgdJsonLayer");
                    style.addLayerAbove(labelLayer, "lgedprjLayerID");
                    style.addLayerAbove(symbolLayer, "lgedprjLabelLayerID");

                    Layer layer = style.getLayer("lgedprjLabelLayerID");
                    if (layer != null) {
                        layer.setProperties(visibility(NONE));
                    }
                } catch (URISyntaxException uriSyntaxException) {
                    uriSyntaxException.printStackTrace();
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
        List<Feature> featureList = mapboxMap.queryRenderedFeatures(rectF, "lgedprjLayerID");

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        ViewGroup viewGroup = root.findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(this.getContext()).inflate(R.layout.customview, viewGroup, false);
        TextView tv = dialogView.findViewById(R.id.popupHeader);
        WebView wv = dialogView.findViewById(R.id.webViewContent);
        Button btnOk = dialogView.findViewById(R.id.buttonOk);
        String regex = "[;\\/:*?\"<>|&']";
        String[] keyArray = {"AREA", "SPID", "DIVISION", "DISTRICT", "UPAZILA", "SPNAME", "SPTYPE", "GAREA", "BAREA", "WMCANAME", "WMCAREGION", "BENEFHHM", "MMALE", "MFEMALE", "CAPFDSHA", "CAPFDSAV", "CAPFDTOTAL", "MLOAN", "FLOAN", "LAMOUNT", "LREALIZE", "ZREALIZED", "OMFUND", "PHASE"};

        if (featureList.size() > 0) {
            for (Feature feature : featureList) {
                String prjName = feature.properties().get("SPTYPE").toString().replaceAll(regex, "");
                //Toast.makeText(this.getContext(), prjName, Toast.LENGTH_SHORT).show();

                tv.setText(prjName);
                tv.setTypeface(tf);

                String content = "\t<style>\n" +
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
                        "<table>";

                for (String key : keyArray) {
                    content += "<tr><td>" + key + "</td><td>" + feature.properties().get(key).toString().replaceAll(regex, "") + "</td></tr>";
                }
                content += "</table>";

                wv.loadDataWithBaseURL(null, content, "text/HTML", "UTF-8", null);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                btnOk.setOnClickListener(v -> alertDialog.dismiss());
            }

            return true;
        }

        return false;
    }

    @SuppressLint("TimberArgCount")
    private void addGeoJsonSourceToMap(@NonNull Style loadedMapStyle) {
        try {
            // Add GeoJsonSource to map
            loadedMapStyle.addSource(new GeoJsonSource("bgd", new URI("asset://bgd.geojson")));
            //loadedMapStyle.addSource(new GeoJsonSource("dhk_ctg", new URI("asset://dhk_ctg.geojson")));
        } catch (Throwable throwable) {
            Timber.e("Couldn't add GeoJsonSource to map - %s");
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

    private void menuLabel() {
        mapboxMap.getStyle(style -> {
            Layer layer = style.getLayer("lgedprjLabelLayerID");

            if (layer != null) {
                if (VISIBLE.equals(layer.getVisibility().getValue())) {
                    layer.setProperties(visibility(NONE));
                } else {
                    layer.setProperties(visibility(VISIBLE));

                    layer = style.getLayer("lgedprjSymbolLayerID");
                    if (layer != null) {
                        layer.setProperties(visibility(NONE));
                    }
                }
            }
        });
    }

    private void menuMarker() {
        mapboxMap.getStyle(style -> {
            Layer layer = style.getLayer("lgedprjSymbolLayerID");

            if (layer != null) {
                if (VISIBLE.equals(layer.getVisibility().getValue())) {
                    layer.setProperties(visibility(NONE));
                } else {
                    layer.setProperties(visibility(VISIBLE));

                    layer = style.getLayer("lgedprjLabelLayerID");
                    if (layer != null) {
                        layer.setProperties(visibility(NONE));
                    }
                }
            }
        });
    }

    private void menuFullscreen() {
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
        fab1.animate().translationY(-this.getResources().getDimension(R.dimen.standard_60));
        fab2.animate().translationY(-this.getResources().getDimension(R.dimen.standard_110));
        fab3.animate().translationX(-this.getResources().getDimension(R.dimen.standard_60));
    }

    private void closeFloatingMenu() {
        isFabOpen = false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationX(0);
    }

    private void toggleLayer() {
        mapboxMap.getStyle(style -> {
            Layer layer = style.getLayer("bgdJsonLayer");

            if (layer != null) {
                if (VISIBLE.equals(layer.getVisibility().getValue())) {
                    layer.setProperties(visibility(NONE));
                } else {
                    layer.setProperties(visibility(VISIBLE));
                }
            }
        });
    }
}