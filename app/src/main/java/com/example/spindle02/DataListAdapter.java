package com.example.spindle02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.spindle02.R;

import java.util.List;

public class DataListAdapter extends BaseAdapter {

    private Context context;
    private List<Data> dataList;

    public DataListAdapter(Context context, List<Data> dataList){
        this.context=context;
        this.dataList=dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View View, ViewGroup viewGroup) {
        View v=View.inflate(context, R.layout.activity_data,null);

        TextView value=(TextView)v.findViewById(R.id.value);

        value.setText(dataList.get(i).get_data());

        //v.setTag(dataList.get(i).getAve_temp());
        return v;
    }
}
