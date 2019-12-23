package com.cegis.deltaplan2100.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.cegis.deltaplan2100.MainActivity;
import com.cegis.deltaplan2100.R;
import com.cegis.deltaplan2100.ui.agriculture.AgricultureFragment;
import com.cegis.deltaplan2100.ui.climate.ClimateFragment;
import com.cegis.deltaplan2100.ui.delta.DeltaFragment;
import com.cegis.deltaplan2100.ui.envdis.EnvDisFragment;
import com.cegis.deltaplan2100.ui.socioeconomic.SocioEconomicFragment;
import com.cegis.deltaplan2100.ui.spatialplanninglanduse.SpatialPlanningLanduseFragment;
import com.cegis.deltaplan2100.ui.waterresource.WaterResourceFragment;

import retrofit2.Converter;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private HomeViewModel homeViewModel;
    private AlertDialog.Builder alertDialogBuilder;
    private View rootView;

    ImageView btnDelta, btnWater, btnAgriculture, btnEnvDis, btnSocial, btnLanduse, btnClimate;
    //private final String TAG = getResources().getString(R.string.mnuDP2100);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        //View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        btnDelta = rootView.findViewById(R.id.btnDelta);
        btnDelta.setOnClickListener(this);

        btnWater = rootView.findViewById(R.id.btnWater);
        btnWater.setOnClickListener(this);

        btnAgriculture = rootView.findViewById(R.id.btnAgriculture);
        btnAgriculture.setOnClickListener(this);

        btnEnvDis = rootView.findViewById(R.id.btnEnvDis);
        btnEnvDis.setOnClickListener(this);

        btnSocial = rootView.findViewById(R.id.btnSocial);
        btnSocial.setOnClickListener(this);

        btnLanduse = rootView.findViewById(R.id.btnLanduse);
        btnLanduse.setOnClickListener(this);

        btnClimate = rootView.findViewById(R.id.btnClimate);
        btnClimate.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDelta:
                swapFragment(R.string.db_mnuDP2100);
                break;

            case R.id.btnWater:
                swapFragment(R.string.db_mnuWaterResources);
                break;

            case R.id.btnAgriculture:
                swapFragment(R.string.db_mnuAgriculture);
                break;

            case R.id.btnEnvDis:
                swapFragment(R.string.db_mnuEnvironmentDisaster);
                break;

            case R.id.btnSocial:
                swapFragment(R.string.db_mnuSocioEconomic);
                break;

            case R.id.btnLanduse:
                swapFragment(R.string.db_mnuSpatialPlanningLanduse);
                break;

            case R.id.btnClimate:
                swapFragment(R.string.db_mnuClimate);
                break;

            default:
                swapFragment(0);
                break;
        }
    }

    public void swapFragment(Integer menuId) {
        try {
            Fragment fragment;
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

            switch (menuId) {
                case R.string.db_mnuDP2100:
                    fragment = new DeltaFragment();
                    break;

                case R.string.db_mnuWaterResources:
                    fragment = new WaterResourceFragment();
                    break;

                case R.string.db_mnuAgriculture:
                    fragment = new AgricultureFragment();
                    break;

                case R.string.db_mnuEnvironmentDisaster:
                    fragment = new EnvDisFragment();
                    break;

                case R.string.db_mnuSocioEconomic:
                    fragment = new SocioEconomicFragment();
                    break;

                case R.string.db_mnuSpatialPlanningLanduse:
                    fragment = new SpatialPlanningLanduseFragment();
                    break;

                case R.string.db_mnuClimate:
                    fragment = new ClimateFragment();
                    break;

                default:
                    fragment = new HomeFragment();
                    break;
            }

            fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (Exception e) {
            Toast.makeText(this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}