package com.cegis.deltaplan2100.ui.layer_three;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.cegis.deltaplan2100.API;
import com.cegis.deltaplan2100.ListAdapter;
import com.cegis.deltaplan2100.MainActivity;
import com.cegis.deltaplan2100.R;
import com.cegis.deltaplan2100.models.ListViewItems;
import com.cegis.deltaplan2100.models.ModelComponentLevelThree;
import com.cegis.deltaplan2100.ui.layer_four.LayerFourFragment;
import com.cegis.deltaplan2100.ui.map.MapFragment;
import com.cegis.deltaplan2100.utility.GenerateHtmlContent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LayerThreeFragment extends Fragment {
    private LayerThreeViewModel mViewModel;
    private View root;
    private WebView webView;
    private ListView listView;
    private SearchView searchView;
    private TextView text_view;
    String groupHeader, itemContentAs, content;
    int itemID, itemParentLevel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(LayerThreeViewModel.class);
        root = inflater.inflate(R.layout.fragment_layer_three, container, false);

        //        final TextView textView = root.findViewById(R.id.text_gallery);
        //        deltaViewModel.getText().observe(this, new Observer<String>() {
        //            @Override
        //            public void onChanged(@Nullable String s) {
        //                textView.setText(s);
        //            }
        //        });

        itemID = getArguments().getInt("ItemID");
        groupHeader = getArguments().getString("GroupHeader");
        itemContentAs = getArguments().getString("ItemContentAs");
        itemParentLevel = getArguments().getInt("ItemParentLevel");

        ((MainActivity) getActivity()).setToolBar(groupHeader);

        webView = root.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);

        text_view = root.findViewById(R.id.text_view);
        text_view.setText(groupHeader);
        searchView = root.findViewById(R.id.layer_three_list_search_view);
        listView = root.findViewById(R.id.layer_three_list_items);

        if (itemParentLevel == 2) {
            if (itemContentAs.toLowerCase().contains("text") ||
                    itemContentAs.toLowerCase().contains("table") ||
                    itemContentAs.toLowerCase().contains("html")) {
                webView.setVisibility(View.VISIBLE);
                listView.setAdapter(null);
                listView.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);


                getTextTableHtmlContent();
            } else if (itemContentAs.toLowerCase().contains("map")) {
                //map content
            } else if (itemContentAs.toLowerCase().contains("graph") ||
                    itemContentAs.toLowerCase().contains("chart") ||
                    itemContentAs.toLowerCase().contains("graphchart")) {
                //graph chart content
            } else {
                webView.setVisibility(View.GONE);
                listView.setAdapter(null);
                listView.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);

                getComponents();
            }
        } else if (itemParentLevel == 3) {
            Fragment fragment;
            Bundle args;

            fragment = new LayerFourFragment();
            args = new Bundle();
            args.putInt("ItemID", itemID);
            args.putString("GroupHeader", groupHeader);
            args.putString("ItemContentAs", itemContentAs);
            args.putInt("ItemParentLevel", 3);
            fragment.setArguments(args);

            //Inflate the fragment
            getFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(null)
                    .commit();
        }

        return root;
    }

    private void getTextTableHtmlContent() {
        ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "Please wait...", true);

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
        Call call = api.getTextTableHtmlContent(itemID, itemParentLevel, itemContentAs);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                dialog.dismiss();
                String content = response.body().toString();
                content = GenerateHtmlContent.getHtmlTable(content);

                webView.loadDataWithBaseURL(null, content, "text/HTML", "UTF-8", null);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
        Call<List<ModelComponentLevelThree>> call = api.getComLevelThree(itemID);

        call.enqueue(new Callback<List<ModelComponentLevelThree>>() {
            @Override
            public void onResponse(Call<List<ModelComponentLevelThree>> call, Response<List<ModelComponentLevelThree>> response) {
                List<ModelComponentLevelThree> componentList = response.body();
                dialog.dismiss();

                if (componentList.size() > 0) {
                    String[] components = new String[componentList.size()];
                    List<ListViewItems> lstViewItems = new ArrayList<>();

                    for (int i = 0; i < componentList.size(); i++) {
                        components[i] = componentList.get(i).getComponentName().trim();
                        ListViewItems _lvi = new ListViewItems();

                        _lvi.setItemID(componentList.get(i).getComponentLevel3Id());
                        _lvi.setItemName(componentList.get(i).getComponentName().trim());

                        if (!TextUtils.isEmpty(componentList.get(i).getDataVisualization())) {
                            _lvi.setItemIcon(componentList.get(i).getDataVisualization().trim());
                        } else {
                            _lvi.setItemIcon("");
                        }

                        _lvi.setItemParentID(componentList.get(i).getComponentLevel3Id().toString());
                        lstViewItems.add(_lvi);
                    }

                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.list_view, R.id.list_item_info, components);
                    listView.setAdapter(new ListAdapter(getContext(), lstViewItems));

                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        Fragment fragment = new Fragment();
                        Bundle args;

                        int itemID = lstViewItems.get(position).getItemID();
                        String itemName = lstViewItems.get(position).getItemName();
                        String itemContentAs = lstViewItems.get(position).getItemIcon();

                        if (itemContentAs.toLowerCase().contains("map")) {
                            fragment = new MapFragment();
                        } else if (itemContentAs.toLowerCase().contains("graph") ||
                                itemContentAs.toLowerCase().contains("chart") ||
                                itemContentAs.toLowerCase().contains("graphchart")) {
                            fragment = new LayerFourFragment();
                        }

                        args = new Bundle();
                        args.putInt("ItemID", itemID);
                        args.putString("GroupHeader", itemName);
                        args.putString("ItemContentAs", itemContentAs);
                        args.putInt("ItemParentLevel", 3);
                        fragment.setArguments(args);

                        //Inflate the fragment
                        getFragmentManager().beginTransaction()
                                .replace(R.id.nav_host_fragment, fragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .addToBackStack(null)
                                .commit();
                    });

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            adapter.getFilter().filter(newText);
                            return false;
                        }
                    });
                } else {
                    listView.setAdapter(null);
                    Toast.makeText(getContext(), "Sorry, no data found!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<ModelComponentLevelThree>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}