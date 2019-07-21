package com.example.quanli;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.quanli.database.DBHelper;
import com.example.quanli.model.ActivityName;
import com.example.quanli.model.DBModel;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private  int selectedID;
    private TextView tvTongTienNo;
    ListView lvDenHanTraLai;
    LinearLayout btnThemNo, btnDanhSachNo;
    List<DBModel> modelList ;

    private DBHelper dbHelper;

    CustomAdapter customAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.widget.Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(mToolbar);

        // button them nguoi no moi
        btnThemNo = (LinearLayout) findViewById(R.id.button_them_no);
        btnThemNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent themno = new Intent(MainActivity.this, ThemNoMoi.class);
                themno.putExtra("sourceActivity", ActivityName.MAIN);
                startActivity(themno);
            }
        });


        // button hien thi danh sach no
        btnDanhSachNo = (LinearLayout) findViewById(R.id.button_danhsach_no);
        btnDanhSachNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotolist = new Intent(MainActivity.this, XuLyDSNo.class);
                startActivity(gotolist);
            }
        });


        // hiển thị tổng số tiền đã cho nợ
        dbHelper = new DBHelper(this);
        tvTongTienNo = (TextView) findViewById(R.id.main_sum_debt);
        lvDenHanTraLai = (ListView) findViewById(R.id.main_thongbao_denhan);
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        tvTongTienNo.setText(String.valueOf(formatter.format(dbHelper.sumOfDebt())));

        // adapter Hiển thị những chủ nợ trên 30 ngày chưa trả lãi
        modelList = dbHelper.notifyDebtor();
        if (customAdapter == null) {
            customAdapter = new CustomAdapter(this, R.layout.list_nguoi_no, modelList);
            lvDenHanTraLai.setAdapter(customAdapter);
        }



        // xử lý khi click vào Danh sách các chủ nợ chưa trả lãi
        lvDenHanTraLai.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedID = modelList.get(position).getModelId();
                Intent intent = new Intent(MainActivity.this, XuLyNo.class);
                intent.putExtra("sourceActivity", ActivityName.MAIN);
                intent.putExtra("idnguoino", selectedID);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        recreate();
    }

    ///


}
