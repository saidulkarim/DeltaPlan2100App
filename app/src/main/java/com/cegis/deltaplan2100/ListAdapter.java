package com.cegis.deltaplan2100;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cegis.deltaplan2100.models.ListViewItems;
import com.cegis.deltaplan2100.utility.FontawesomeLight;

import java.util.List;

public class ListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Context context;
    List<ListViewItems> items;

    public ListAdapter(Context context, List<ListViewItems> items) {
        this.context = context;
        this.items = items;

        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.toArray().length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = inflater.inflate(R.layout.element_list, null);

        TextView txtItemID = view.findViewById(R.id.txtItemID);
        TextView txtListItem = view.findViewById(R.id.txtListItem);
        FontawesomeLight list_icon = view.findViewById(R.id.list_icon);
        txtItemID.setText(items.get(position).getItemParentID());
        txtListItem.setText(items.get(position).getItemName());
        list_icon.setText(getListIcon(items.get(position).getItemIcon()));
        list_icon.setTextSize(25);
        list_icon.setTag(position);

        list_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //code goes here
                //icoList[(Integer) v.getTag()]
            }
        });

        return view;
    }

    //0 chart: \uf080, 1 map: \uf5a0, 2 table: \uf0ce, 3 text: \uf036, 4 right arrow: \uf101
    private String getListIcon(String type) {
        switch (type) {
            case "Graph":
            case "Chart":
            case "GraphChart":
                return "\uf080";

            case "Map":
                return "\uf5a0";

            case "Table":
                return "\uf0ce";

            case "Text":
            case "TextTable":
                return "\uf036";

            default:
                return "\uf101";
        }
    }
}
