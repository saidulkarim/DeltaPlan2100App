package com.cegis.deltaplan2100.ui.delta;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
//import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.cegis.deltaplan2100.API;
import com.cegis.deltaplan2100.ListAdapter;
import com.cegis.deltaplan2100.MainActivity;
import com.cegis.deltaplan2100.R;
import com.cegis.deltaplan2100.models.ListViewItems;
import com.cegis.deltaplan2100.models.ModelComponentLevelTwo;
import com.cegis.deltaplan2100.ui.layer_three.LayerThreeFragment;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeltaFragment extends Fragment {
    private DeltaViewModel deltaViewModel;
    private View root;
    private ListView listView;
    //private SearchView searchView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        deltaViewModel = ViewModelProviders.of(this).get(DeltaViewModel.class);
        root = inflater.inflate(R.layout.fragment_delta, container, false);

        //        final TextView textView = root.findViewById(R.id.text_gallery);
        //        deltaViewModel.getText().observe(this, new Observer<String>() {
        //            @Override
        //            public void onChanged(@Nullable String s) {
        //                textView.setText(s);
        //            }
        //        });

        ((MainActivity) getActivity()).setToolBar(getString(R.string.mnuDP2100));

        listView = root.findViewById(R.id.delta_plan_list_items);
        //searchView = root.findViewById(R.id.delta_plan_list_search_view);

        //calling the method to display component level 2 list
        getComponents();

        return root;
    }

    private void getComponents() {
        ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "Please wait...", true);

        OkHttpClient okHttpClient = API.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API api = retrofit.create(API.class);
        Call<List<ModelComponentLevelTwo>> call = api.getComLevelTwo(Integer.parseInt(getResources().getString(R.string.db_mnuDP2100)));

        call.enqueue(new Callback<List<ModelComponentLevelTwo>>() {
            @Override
            public void onResponse(Call<List<ModelComponentLevelTwo>> call, Response<List<ModelComponentLevelTwo>> response) {
                List<ModelComponentLevelTwo> componentList = response.body();
                dialog.dismiss();

                if (componentList.size() > 0) {
                    String[] components = new String[componentList.size()];
                    List<ListViewItems> lstViewItems = new ArrayList<>();

                    for (int i = 0; i < componentList.size(); i++) {
                        components[i] = componentList.get(i).getComponentName().trim();
                        ListViewItems _lvi = new ListViewItems();

                        _lvi.setItemID(componentList.get(i).getComponentLevel2Id());
                        _lvi.setItemName(componentList.get(i).getComponentName().trim());

                        if (!TextUtils.isEmpty(componentList.get(i).getDataVisualization())) {
                            _lvi.setItemIcon(componentList.get(i).getDataVisualization().trim());
                        } else {
                            _lvi.setItemIcon("");
                        }

                        _lvi.setItemParentID(componentList.get(i).getComponentLevel2Id().toString());
                        lstViewItems.add(_lvi);
                    }

                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.list_view, R.id.list_item_info, components);
                    listView.setAdapter(new ListAdapter(getContext(), lstViewItems));

                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        Fragment fragment;
                        Bundle args;

                        int itemID = lstViewItems.get(position).getItemID();
                        String itemName = lstViewItems.get(position).getItemName();
                        String itemContentAs = lstViewItems.get(position).getItemIcon();

                        fragment = new LayerThreeFragment();
                        args = new Bundle();
                        args.putInt("ItemID", itemID);
                        args.putString("GroupHeader", itemName);
                        args.putString("ItemContentAs", itemContentAs);
                        args.putInt("ItemParentLevel", 2);
                        fragment.setArguments(args);

                        //Inflate the fragment
                        getFragmentManager().beginTransaction()
                                .replace(R.id.nav_host_fragment, fragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .addToBackStack(null)
                                .commit();
                    });

//                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                        @Override
//                        public boolean onQueryTextSubmit(String query) {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onQueryTextChange(String newText) {
//                            adapter.getFilter().filter(newText);
//                            return false;
//                        }
//                    });
                } else {
                    listView.setAdapter(null);
                    Toast.makeText(getContext(), "Sorry, no data found!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<ModelComponentLevelTwo>> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}