package competition.sessionmanagerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter {

    List list = new ArrayList();
    Context my;

    public CustomAdapter(Context context, int resource){
        super(context, R.layout.item, resource);
        my = context;
    }

    static class DataHandler {
        TextView title;
    }

    @Override
    public void add (Object object){
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount (){
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        DataHandler handler;
            row = convertView;
            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.item, parent, false);
                handler = new DataHandler();
                handler.title = (TextView) row.findViewById(R.id.itemTitle);
                row.setTag(handler);
            }
             else {
                handler = (DataHandler) row.getTag();
             }

        ListData data;
        data = (ListData) this.getItem(position);
        handler.title.setText(data.getTitle());

        return row;
    }
}
