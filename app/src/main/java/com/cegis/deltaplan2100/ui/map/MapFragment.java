package com.cegis.deltaplan2100.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.cegis.deltaplan2100.MainActivity;
import com.cegis.deltaplan2100.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;

import org.json.JSONException;

import java.io.IOException;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private MapViewModel mViewModel;
    private View root;
    private GoogleMap mMap;
    private MapView mMapView;

    private int itemID, itemParentLevel;
    private String groupHeader, itemContentAs;

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

        mMapView = root.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        mMapView.onResume(); // needed to get the map to display immediately

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap = googleMap;

        //23.7814932, 90.4132565
        LatLng point1 = new LatLng(23.7814932, 90.4132565);
        mMap.addMarker(new MarkerOptions().position(point1).title("This is CEGIS"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point1));

//        //23.7832977, 90.4158691
//        LatLng point2 = new LatLng(23.7832977, 90.4158691);
//        mMap.addMarker(new MarkerOptions().position(point2).title("This is SCB"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(point2));

        GeoJsonLayer layer = null;

        try {
            layer = new GeoJsonLayer(mMap, R.raw.bgd, this.getContext());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GeoJsonPolygonStyle polygonStyle = layer.getDefaultPolygonStyle();
        polygonStyle.setStrokeColor(ContextCompat.getColor(this.getContext(), R.color.green));
        polygonStyle.setStrokeWidth(1);
        polygonStyle.setFillColor(ContextCompat.getColor(this.getContext(), R.color.PeachPuff));
        polygonStyle.setLineStringWidth(1);
        layer.addLayerToMap();

        try {
            layer = new GeoJsonLayer(mMap, R.raw.lgedprj, this.getContext());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //layer.addLayerToMap();

        polygonStyle = layer.getDefaultPolygonStyle();
        polygonStyle.setStrokeColor(ContextCompat.getColor(this.getContext(), R.color.LightGreen));
        polygonStyle.setStrokeWidth(1);
        polygonStyle.setFillColor(ContextCompat.getColor(this.getContext(), R.color.Yellow));
        layer.addLayerToMap();

        mMap.setMinZoomPreference(7);
    }
}