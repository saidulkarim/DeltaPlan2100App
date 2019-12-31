package com.cegis.deltaplan2100.ui.layer_four;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.cegis.deltaplan2100.MainActivity;
import com.cegis.deltaplan2100.R;
import com.cegis.deltaplan2100.ui.layer_three.LayerThreeViewModel;

public class LayerFourFragment extends Fragment {
    private LayerFourViewModel mViewModel;
    private View root;
    private WebView webView;
    private ListView listView;
    private SearchView searchView;
    private TextView text_view, text_view_note;
    String groupHeader, itemContentAs, content;
    int itemID, itemParentLevel;

    public static LayerFourFragment newInstance() {
        return new LayerFourFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_layer_three, container, false);

        mViewModel = ViewModelProviders.of(this).get(LayerFourViewModel.class);
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

        text_view = root.findViewById(R.id.text_view);
        //text_view_note = root.findViewById(R.id.text_view_note);
        text_view.setText(groupHeader);

        webView = root.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVisibility(View.VISIBLE);
        String content = "Filtering content goes here";
        //text_view_note.setText(content);
        webView.loadDataWithBaseURL(null, content, "text/HTML", "UTF-8", null);

        return root;
    }

    //    @Override
    //    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    //        super.onActivityCreated(savedInstanceState);
    //        mViewModel = ViewModelProviders.of(this).get(LayerFourViewModel.class);
    //        // TODO: Use the ViewModel
    //    }
}
