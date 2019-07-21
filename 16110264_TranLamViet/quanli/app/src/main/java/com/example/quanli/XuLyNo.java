package com.example.quanli;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quanli.database.DBHelper;
import com.example.quanli.model.ActivityName;
import com.example.quanli.model.DBModel;
import com.example.quanli.model.DBModelChiTiet;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/*
    Xử lý các vấn đề liên quan
        + trừ lãi
        + từ tiền gốc và cả lãi
        + cộng thêm tiền nợ gốc
        + xóa chủ nợ
 */
public class XuLyNo extends AppCompatActivity {
    private ActivityName sourceActivity;
    private int idnguoino;
    public int getIdnguoino() {
        return idnguoino;
    }
    public void setIdnguoino(int idnguoino) {
        this.idnguoino = idnguoino;
    }
    private BroadcastReceiver receiver;
    private DBHelper dbHelper;
    private List<DBModelChiTiet> details;
    private ChiTietAdapter chiTietAdapter;
    private ListView lvDebtorDetails;
    //model
    private TextView tvName;
    private TextView tvDebt;
    private Button btnThemNo;
    private Button btnTraNo;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.menu_xu_ly_no, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_delete:
                deleteDebtor();
        }
        return super.onContextItemSelected(item);
    }
    // Hiển thị chi tiết thông tin của nợ đã lấy từ intent  .

    private void Initialize() {
        dbHelper = new DBHelper(this);

        // hiển thị thông tin người nợ đã lấy id được truyền vào từ activity trước
        DBModel model = dbHelper.getDebtorById(idnguoino);

        details = new ArrayList<>();
        lvDebtorDetails = (ListView) findViewById(R.id.lv_debtor_detail);
        DecimalFormat formatter = new DecimalFormat("###,###,###");

        // sử dựng ChiTietAdapter hiển thị thao dạng gồnm heading và content ;
        details.add(new DBModelChiTiet("Điện thoại", (model.getModelPhone())));
        details.add(new DBModelChiTiet("Địa chỉ ", model.getModelAddress()));
        details.add(new DBModelChiTiet("Lãi suất/nam %", String.valueOf(model.getModelInterest_rate())));
        details.add(new DBModelChiTiet("Tiền lãi hiện tại", String.valueOf(formatter.format((int) dbHelper.calculateInterest(model)))));
        details.add(new DBModelChiTiet("Ngày vay", model.getModelDate()));
        details.add(new DBModelChiTiet("Ngày trả gần nhất", model.getModelInterest_date()));
        details.add(new DBModelChiTiet("Mô tả", model.getModelDescription()));

        chiTietAdapter = new ChiTietAdapter(this, R.layout.info_chi_tiet_tien_no, details);

        tvName = (TextView) findViewById(R.id.name);
        tvDebt = (TextView) findViewById(R.id.debt);
        tvName.setText(model.getModelName());
        tvDebt.setText(String.valueOf(formatter.format(model.getModelDebt())));
        // đưa vào lisview để hiển thị
        lvDebtorDetails.setAdapter(chiTietAdapter);
    }
    /*
        Xử lý trả tiền lãi:
            - nếu tiền lãi vay ngay hôm nay mà chọn ""Thanh toán" , thông báo: chưa có lãi hiển lên
            - nếu có lãi, sẽ set lại Tiền Lãi =0;
                + cập nhật lại ""Ngày trả lãi gần nhất"  là ngày hôm nay
     */
    public void payInterest() {
        dbHelper = new DBHelper(this);
        final DBModel model = dbHelper.getDebtorById(idnguoino);

        //format tiền lãi
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        String interest = String.valueOf(formatter.format((int) dbHelper.calculateInterest(model)));

        if (dbHelper.calculateInterest(model) == 0) {
            Toast.makeText(XuLyNo.this, "Chưa có tiền lãi! ", Toast.LENGTH_LONG).show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Trừ tiền lãi");
            builder.setMessage("Tiền lãi hiện tại: " + interest +
                    "\n, xóa tiền lãi cho khoản nợ này?");

            builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  //  DBModel model1 = dbHelper.getDebtorById(idnguoino);
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String date = simpleDateFormat.format(calendar.getTime());

                    // set lại ngày trả lãi gần nhất
                    model.setModelInterest_date(date);

                    //
                    if (dbHelper.UpdateDebtor(model))
                        Toast.makeText(XuLyNo.this, "Xóa lãi thành công!", Toast.LENGTH_LONG).show();
                    Initialize();
                    dbHelper.close();
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
    }


    /*
        Xủ lý trả tiền lãi và xóa tiền vay
     */
    public void payDebtAndInterest() {
        dbHelper = new DBHelper(this);
        DBModel model = dbHelper.getDebtorById(idnguoino);

        DecimalFormat formatter = new DecimalFormat("###,###,###");
        String loanAmount, interest;
        loanAmount = String.valueOf(formatter.format(model.getModelDebt()));
        interest = String.valueOf(formatter.format((int) dbHelper.calculateInterest(model)));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (dbHelper.calculateInterest(model) == 0) {
            builder.setTitle("Hoàn tất khoản nợ");
            builder.setMessage("Tiền nợ " + loanAmount +
                    "\nBạn có chắc chắn muốn hoàn tất khoản nợ này?");
        } else {
            builder.setTitle("Hoàn tất khoản nợ");
            builder.setMessage("Tiền nợ : " + loanAmount +
                    "\nTiền lãi hiện tại: " + interest +
                    "\nBạn có chắc chắn muốn hoàn tất khoản nợ này?");
        }
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBModel debtor = dbHelper.getDebtorById(idnguoino);
                debtor.setModelInterest_date("");
                debtor.setModelDate("");
                debtor.setModelInterest_rate(0);
                debtor.setModelDebt(0);
                if (dbHelper.UpdateDebtor(debtor))
                    Toast.makeText(XuLyNo.this, "Đã hoàn tất khoản nợ!", Toast.LENGTH_LONG).show();
                Initialize();
                dbHelper.close();

                sendBroadcast(new Intent("reload_list_nguoi_no"));
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

    private void deleteDebtor() {
        dbHelper = new DBHelper(this);
        DBModel debtor = dbHelper.getDebtorById(idnguoino);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa thông tin");
        builder.setMessage("Bạn có chắc chắn muốn xóa " + debtor.getModelName() + " ra khỏi danh sách?");
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dbHelper.deleteDebtor(idnguoino))
                    Toast.makeText(XuLyNo.this, "Xóa thành công!", Toast.LENGTH_LONG).show();
                else {
                    Toast.makeText(XuLyNo.this, "Xảy ra lỗi khi xóa", Toast.LENGTH_LONG).show();
                }
                dbHelper.close();

                if (sourceActivity == ActivityName.DS_NO_LIST) {
                    sendBroadcast(new Intent("reload_list_nguoi_no"));
                }
                finish();
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xu_ly_no);


        Toolbar toolbar =  findViewById(R.id.toolbar_PersonalInfo);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        // getID tu activity truoc
        sourceActivity = (ActivityName) intent.getSerializableExtra("sourceActivity");
        idnguoino = intent.getIntExtra("idnguoino", -1);

        Initialize();
        btnTraNo = (Button) findViewById(R.id.pay_debt_btn);
        btnTraNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbHelper.getDebtorById(idnguoino).getModelInterest_rate() == 0) {
                    payDebtAndInterest();
                }
                else {
//                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                    Fragment prev = getSupportFragmentManager().findFragmentByTag("pay_loan_action");
//                    if (prev != null) {
//                            ft.remove(prev);
//                    }
//                    ft.addToBackStack(null);
                    TraNoTruNo payLoanBottomSheet = TraNoTruNo.newInstance();
                    payLoanBottomSheet.show(getSupportFragmentManager(), "pay_loan_action");
                }
            }
        });


    }
}