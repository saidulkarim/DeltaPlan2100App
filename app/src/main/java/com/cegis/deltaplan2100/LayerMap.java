package com.cegis.deltaplan2100;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;
import android.widget.Toast;

import com.mapbox.geojson.Feature;
import com.cegis.deltaplan2100.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.net.URI;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class LayerMap extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener {
    private MapView mapView;
    private MapboxMap mapboxMap;
    private static final String geoJsonSourceId = "geoJsonData";
    private static final String geoJsonLayerId = "polygonFillLayer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token));
        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_layer_map);
        Toast.makeText(LayerMap.this, R.string.action_symbol_layer_info, Toast.LENGTH_SHORT).show();

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        LayerMap.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapboxMap.addOnMapClickListener(LayerMap.this);
                addGeoJsonSourceToMap(style);

                // Create FillLayer with GeoJSON source and add the FillLayer to the map
                if (style != null) {
                    style.addLayer(new FillLayer(geoJsonLayerId, geoJsonSourceId)
                            .withProperties(fillColor(Color.parseColor("#ff0088")), fillOpacity(0.5f)));
                }

                findViewById(R.id.fab_layer_toggle).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toggleLayer();
                    }
                });
            }
        });
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        PointF pointf = mapboxMap.getProjection().toScreenLocation(point);
        RectF rectF = new RectF(pointf.x - 10, pointf.y - 10, pointf.x + 10, pointf.y + 10);
        List<Feature> featureList = mapboxMap.queryRenderedFeatures(rectF, geoJsonLayerId);
        if (featureList.size() > 0) {
            for (Feature feature : featureList) {
                Timber.d("Feature found with %1$s", feature.toJson());
                Toast.makeText(LayerMap.this, "click_on_polygon_toast", Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        return false;
    }

    private void addGeoJsonSourceToMap(@NonNull Style loadedMapStyle) {
        try {
            // Add GeoJsonSource to map
            loadedMapStyle.addSource(new GeoJsonSource(geoJsonSourceId, new URI("asset://bgd_adm.geojson")));
            //loadedMapStyle.addSource(new GeoJsonSource(geoJsonSourceId, new URI("asset://dhk_ctg.geojson")));
        } catch (Throwable throwable) {
            Timber.e("Couldn't add GeoJsonSource to map - %s", throwable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
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
    protected void onDestroy() {
        super.onDestroy();
        if (mapboxMap != null) {
            mapboxMap.removeOnMapClickListener(this);
        }
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void toggleLayer() {
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                Layer layer = style.getLayer(geoJsonLayerId);
                if (layer != null) {
                    if (VISIBLE.equals(layer.getVisibility().getValue())) {
                        layer.setProperties(visibility(NONE));
                    } else {
                        layer.setProperties(visibility(VISIBLE));
                    }
                }
            }
        });
    }
}
