package com.example.quanli;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.quanli.model.DBModelChiTiet;

import java.util.List;


/*
    Phục vụ cho listview hiển thị chi tiết của người nợ
 */


public class ChiTietAdapter extends ArrayAdapter<DBModelChiTiet> {

    private Context context;
    private int source;
    private List<DBModelChiTiet> dbModelChiTiets;


    public ChiTietAdapter(@NonNull Context context, int resource, @NonNull List<DBModelChiTiet > chiTietList)
    {
        super(context, resource, chiTietList);
        this.context=context;
        this.source=resource;
        this.dbModelChiTiets=chiTietList;
    }



    @NonNull
    @Override
    // chi tiet tien no
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.info_chi_tiet_tien_no, parent, false);
            // tự sinh để hiển thị
            viewHolder = new ViewHolder();
            viewHolder.heading = (TextView) view.findViewById(R.id.tv_detail_heading);
            viewHolder.content = (TextView) view.findViewById(R.id.tv_detail_content);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        DBModelChiTiet modelChiTiet = dbModelChiTiets.get(position);

        viewHolder.heading.setText(modelChiTiet.getHeading());
        viewHolder.content.setText(modelChiTiet.getContent());
        return view;
    }

    public class ViewHolder {
        private TextView heading;
        private TextView content;
    }

}
