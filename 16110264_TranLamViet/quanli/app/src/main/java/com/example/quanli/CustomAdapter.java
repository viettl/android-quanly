package com.example.quanli;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.quanli.model.DBModel;

import java.text.DecimalFormat;
import java.util.List;



// hien thi ten va so tien no trong "Danh sach no"
public class CustomAdapter extends ArrayAdapter<DBModel> {
    private Context context;
    private List<DBModel> modelList;
    private  int source;


    // tạo các row trên listview, set dữ liệu lên các row
    @Override
    public View getView(int pos, @Nullable View view, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        // hien thi nguoi no va so tien lai
        if (view == null) {
            // Gọi layoutInflater ra để bắt đầu ánh xạ, Đổ dữ liệu vào biến View
            view = LayoutInflater.from(context).inflate(R.layout.list_nguoi_no, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) view.findViewById(R.id.tv_Name);
            viewHolder.tvDebt = (TextView) view.findViewById(R.id.tv_Debt);
            view.setTag(viewHolder);
        } else
            {
            viewHolder = (ViewHolder) view.getTag();
        }
        DBModel model = modelList.get(pos);
        viewHolder.tvName.setText(model.getModelName());
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        viewHolder.tvDebt.setText(String.valueOf(formatter.format(model.getModelDebt())));
        return view;
    }

     // Gán cứng view để nó inflate file , dữ liệu sau đó chỉ việc đổ vào mà không tạo ra các layout con mới
    // tránh việc gọi findViewById
    public class ViewHolder {
        private TextView tvName;
        private TextView tvDebt;
    }
   public CustomAdapter(@NonNull Context context, int source, @NonNull List<DBModel> list)
   {
       super(context, source, list);
       this.context= context;
       this.source=source;
       this.modelList=list;


   }
}
