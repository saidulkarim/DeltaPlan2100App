package com.cegis.deltaplan2100.ui.map;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.cegis.deltaplan2100.MainActivity;
import com.cegis.deltaplan2100.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.mapbox.mapboxsdk.style.expressions.Expression.exponential;
import static com.mapbox.mapboxsdk.style.expressions.Expression.has;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;

import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static java.nio.file.Paths.get;

import static com.mapbox.mapboxsdk.style.expressions.Expression.all;
import static com.mapbox.mapboxsdk.style.expressions.Expression.division;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.gte;
import static com.mapbox.mapboxsdk.style.expressions.Expression.has;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.lt;
import static com.mapbox.mapboxsdk.style.expressions.Expression.toNumber;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;

public class MapFragment extends Fragment implements OnMapReadyCallback, MapboxMap.OnMapClickListener {
    private MapViewModel mViewModel;
    private View root;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private FloatingActionButton fab;

    private int itemID, itemParentLevel;
    private String groupHeader, itemContentAs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this.getContext(), getString(R.string.map_access_token));
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

        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fab = root.findViewById(R.id.fab);

        return root;
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            mapboxMap.addOnMapClickListener(MapFragment.this::onMapClick);

            addGeoJsonSourceToMap(style);

            // Create FillLayer with GeoJSON source and add the FillLayer to the map
            if (style != null) {
                // The layer properties for our line. This is where we make the line dotted, set the
                // color, etc.
                style.addLayer(new LineLayer("lineLayerID", "bgd")
                        .withProperties(
                                //PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}),
                                //PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                //PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                PropertyFactory.lineWidth(1f),
                                PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
                        ));

                style.addLayer(new FillLayer("bgdJsonLayer", "bgd")
                        .withProperties(
                                fillColor(Color.parseColor("#F2F2F2")), fillOpacity(0.8f)
                        ));
            }

            try {
                GeoJsonSource lgedprj = new GeoJsonSource("lgedprj", new URI("asset://lgedprj.geojson"));
                style.addSource(lgedprj);
                FillLayer lgedprjArea = new FillLayer("lgedprjLayerID", "lgedprj");

                lgedprjArea.setProperties(
                        fillColor(Color.parseColor("#EC53A5")),
                        fillOpacity(0.4f)
                );

                style.addLayerAbove(lgedprjArea, "bgdJsonLayer");
            } catch (URISyntaxException uriSyntaxException) {
                uriSyntaxException.printStackTrace();
            }

            fab.setOnClickListener(view -> toggleLayer());
        });

        mapboxMap.getUiSettings().setAttributionEnabled(false);
        mapboxMap.getUiSettings().setLogoEnabled(false);
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        PointF pointf = mapboxMap.getProjection().toScreenLocation(point);
        RectF rectF = new RectF(pointf.x - 10, pointf.y - 10, pointf.x + 10, pointf.y + 10);
        //List<Feature> featureList = mapboxMap.queryRenderedFeatures(rectF, "bgdJsonLayer");
        List<Feature> featureList = mapboxMap.queryRenderedFeatures(rectF, "lgedprjLayerID");
        if (featureList.size() > 0) {
            for (Feature feature : featureList) {
                //String name = feature.properties().get("Name").toString();
                String name = feature.properties().get("SPID").toString();
                Toast.makeText(this.getContext(), name, Toast.LENGTH_SHORT).show();
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