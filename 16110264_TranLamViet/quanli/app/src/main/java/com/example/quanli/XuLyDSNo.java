package com.example.quanli;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.quanli.database.DBHelper;
import com.example.quanli.model.ActivityName;
import com.example.quanli.model.DBModel;

import java.util.List;


/*
     Xử lý ở trang hiện thị các danh sách người nơ
        +Hiển thị thông qua ListView, có sử dụng adapter
        +Các thao tác, onClick, onLongClick
 */

public class XuLyDSNo extends AppCompatActivity {
    private int selectedId;
    private BroadcastReceiver receiver;

    private List<DBModel> listDebtor;
    private DBHelper dbManager;
    private CustomAdapter customAdapter;
    private ListView lvDebtor;

    private FloatingActionButton mfbtn;
    private int[] listID;
    private boolean isSearching = false;
  //  ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xu_ly_dsno);

        android.support.v7.widget.Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_list);
        setSupportActionBar(mToolbar);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        // Lấy các intent để tiến hành reload lại trang
        // khi có thay đổi
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null) {
                    if (action.equals("reload_list_nguoi_no")) {
                        recreate();
                    } else if (action.equals("finish_list")) {
                        finish();
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("reload_list_nguoi_no");
        intentFilter.addAction("finish_list");
        registerReceiver(receiver, intentFilter);

        // lay arrayList<>
        Initialize();
        // gọi, set apdapter để show danh sách
        SetAdapter();

        // xét sự kiện cho floating button
        mfbtn = findViewById(R.id.list_add_btn);
        mfbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentThemNo = new Intent(XuLyDSNo.this, ThemNoMoi.class);
                intentThemNo.putExtra("sourceActivity", ActivityName.DS_NO_LIST);
                startActivity(intentThemNo);
            }
        });

        lvDebtor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              if (!isSearching) {
                DBModel debtor = listDebtor.get(position);
                  selectedId = debtor.getModelId();
            }
//            else {
//                    selectedId = listID[position];
//            }
                Intent goToPersonalInfo = new Intent(XuLyDSNo.this, XuLyNo.class);
                goToPersonalInfo.putExtra("sourceActivity", ActivityName.DS_NO_LIST);
                goToPersonalInfo.putExtra("idnguoino", selectedId);
                startActivity(goToPersonalInfo);
            }
        });
        lvDebtor.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DBModel debtor = listDebtor.get(position);
                selectedId = debtor.getModelId();
                return false;
            }
        });
        registerForContextMenu(lvDebtor);
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo
            menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_list_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_delete:
                deleteDebtor(selectedId);
                listDebtor.clear();
                listDebtor.addAll(dbManager.getAllDebtorNameASC());
                customAdapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void SetAdapter() {
        if (customAdapter == null) {
            customAdapter = new CustomAdapter(this, R.layout.list_nguoi_no, listDebtor);
            lvDebtor.setAdapter(customAdapter);
        } else {
            lvDebtor.setSelection(customAdapter.getCount() - 1);
        }
    }

    private void Initialize() {
        dbManager = new DBHelper(this);
        lvDebtor = findViewById(R.id.list_lv);
        listDebtor = dbManager.getAllDebtorNameASC();
    }



    private void deleteDebtor(final int ID) {
        dbManager = new DBHelper(this);
        DBModel debtor = dbManager.getDebtorById(ID);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa thông tin");
        builder.setMessage("Bạn có chắc muốn xóa " + debtor.getModelName() + " ra khỏi danh sách?");
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dbManager.deleteDebtor(ID)) {
                    Toast.makeText(XuLyDSNo.this, "Xóa thành công!", Toast.LENGTH_LONG).show();
                    dbManager.close();
                } else {
                    Toast.makeText(XuLyDSNo.this, "Xảy ra lỗi khi xóa", Toast.LENGTH_LONG).show();
                }
                recreate();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onDestroy() {
        if (receiver != null)
            unregisterReceiver(receiver);
        super.onDestroy();
    }
}
