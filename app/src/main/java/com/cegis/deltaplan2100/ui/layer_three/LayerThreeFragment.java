package com.cegis.deltaplan2100.ui.layer_three;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.cegis.deltaplan2100.Api;
import com.cegis.deltaplan2100.MainActivity;
import com.cegis.deltaplan2100.R;
import com.cegis.deltaplan2100.TabularViewFragment;
import com.cegis.deltaplan2100.models.ModelComponentLevelThree;
import com.cegis.deltaplan2100.models.ModelComponentLevelTwo;
import com.cegis.deltaplan2100.ui.delta.DeltaViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LayerThreeFragment extends Fragment {
    private LayerThreeViewModel mViewModel;
    private View root;
    private ListView listView;
    private SearchView searchView;
    private TextView text_view;
    String groupHeader, parentID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_layer_three, container, false);

        mViewModel = ViewModelProviders.of(this).get(LayerThreeViewModel.class);
        root = inflater.inflate(R.layout.fragment_layer_three, container, false);

        //        final TextView textView = root.findViewById(R.id.text_gallery);
        //        deltaViewModel.getText().observe(this, new Observer<String>() {
        //            @Override
        //            public void onChanged(@Nullable String s) {
        //                textView.setText(s);
        //            }
        //        });

        groupHeader = getArguments().getString("GroupHeader");
        parentID = getArguments().getString("ParentID");

        ((MainActivity) getActivity()).setToolBar(groupHeader);

        text_view = root.findViewById(R.id.text_view);
        searchView = root.findViewById(R.id.layer_three_list_search_view);
        listView = root.findViewById(R.id.layer_three_list_items);

        text_view.setText(groupHeader);
        getComponents();

        return root;
    }

    private void getComponents() {
        ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();

        Api api = retrofit.create(Api.class);

        Call<List<ModelComponentLevelThree>> call = api.getComLevelThree(Integer.parseInt(parentID));

        call.enqueue(new Callback<List<ModelComponentLevelThree>>() {
            @Override
            public void onResponse(Call<List<ModelComponentLevelThree>> call, Response<List<ModelComponentLevelThree>> response) {
                List<ModelComponentLevelThree> componentList = response.body();
                dialog.dismiss();

                if (componentList.size() > 0) {
                    //Creating an String array for the ListView
                    String[] components = new String[componentList.size()];

                    //looping through all the heroes and inserting the names inside the string array
                    for (int i = 0; i < componentList.size(); i++) {
                        components[i] = componentList.get(i).getComponentName();
                    }

                    //displaying the string array into listview
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.list_view, R.id.list_item_info, components);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        String name = components[position];
                        //Toast.makeText(getContext(), name + ": " + position, Toast.LENGTH_SHORT).show();

                        TabularViewFragment tvf = new TabularViewFragment();
                        Bundle args = new Bundle();
                        args.putString("GroupHeader", name);
                        args.putString("ParentID", name + ": " + position);
                        tvf.setArguments(args);

                        //Inflate the fragment
                        getFragmentManager().beginTransaction()
                                .replace(R.id.nav_host_fragment, tvf)
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
                    Toast.makeText(getContext(), "No data found!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<ModelComponentLevelThree>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}