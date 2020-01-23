package com.cegis.deltaplan2100;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FloatingActionButton fab = findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                .setAction("Action", null).show();
        //    }
        //});

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_dp2100,
                R.id.nav_water_resources,
                R.id.nav_agriculture,
                R.id.nav_environment_disaster,
                R.id.nav_socio_economic,
                R.id.nav_spatial_planning_landuse,
                R.id.nav_climate,
                R.id.nav_share,
                R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    public void setToolBar(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle(R.string.close_header_title);
            alertDialogBuilder.setMessage("Are you want to close this apps?");
            alertDialogBuilder.setCancelable(false);

            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            alertDialogBuilder.setNeutralButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            super.onBackPressed();
        }

    }

    //    @Override
    //    public void onBackPressed() {
    //        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
    //        alertDialogBuilder.setTitle(R.string.close_header_title);
    //        alertDialogBuilder.setMessage("Are you want to close this apps?");
    //        alertDialogBuilder.setCancelable(false);
    //
    //        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    //            @Override
    //            public void onClick(DialogInterface dialog, int which) {
    //                finish();
    //            }
    //        });
    //
    //        alertDialogBuilder.setNeutralButton("No", new DialogInterface.OnClickListener() {
    //            @Override
    //            public void onClick(DialogInterface dialog, int which) {
    //                dialog.cancel();
    //            }
    //        });
    //
    //      /*  alertDialogBuilder.setNegativeButton("Cencel", new DialogInterface.OnClickListener() {
    //            @Override
    //            public void onClick(DialogInterface dialog, int which) {
    //                Toast toast = Toast.makeText(MainActivity.this,"You Cencel This",Toast.LENGTH_SHORT);
    //                toast.show();
    //            }
    //        });*/
    //
    //        AlertDialog alertDialog = alertDialogBuilder.create();
    //        alertDialog.show();
    //    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            Toast.makeText(MainActivity.this, "About Us info", Toast.LENGTH_SHORT).show();

            //Intent cIntent = new Intent(MainActivity.this, ClimateActivity.class);
            Intent cIntent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(cIntent);
        } else if (item.getItemId() == R.id.action_about) {
            //Intent aIntent = new Intent(MainActivity.this, MapActivity.class);
            Intent aIntent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(aIntent);
        }
        //else if (item.getItemId() == R.id.action_symbol_layer_info) {
        //            //Intent aIntent = new Intent(MainActivity.this, LayerMap.class);
        //            Intent aIntent = new Intent(MainActivity.this, MainActivity.class);
        //            startActivity(aIntent);
        //        } else if (item.getItemId() == R.id.activity_draw_geojson_line) {
        //            //Intent aIntent = new Intent(MainActivity.this, DrawGeojsonLineActivity.class);
        //            Intent aIntent = new Intent(MainActivity.this, MainActivity.class);
        //            startActivity(aIntent);
        //        }

        return super.onOptionsItemSelected(item);
    }
}
