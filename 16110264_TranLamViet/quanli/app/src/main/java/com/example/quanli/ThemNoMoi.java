package com.example.quanli;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quanli.database.DBHelper;
import com.example.quanli.model.ActivityName;
import com.example.quanli.model.DBModel;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
    Class xử lý khi thêm mới nợ vào,
        + xử lý các vấn đề tạo, thêm, xử lý nhập liệu
 */



public class ThemNoMoi extends AppCompatActivity {



    private ActivityName sourceActivity;
    private int debtorId;

    private EditText edtName;
    private EditText edtPhone;
    private EditText edtAddress;
    private EditText edtDebt;
    private EditText edtInterest_rate;
    private EditText edtDate;
    private Button btnSelectDate;
    private EditText edtInterest_date;
    private EditText edtDescription;
    private DBHelper dbManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_no_moi);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_ThemNoMoi);
        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        sourceActivity = (ActivityName) intent.getSerializableExtra("sourceActivity");
        debtorId = intent.getIntExtra("idnguoino", -1);

        Initialize();
         mToolbar.setTitle("Thêm nợ mới");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.menu_them_no, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                finish();
                return true;
            case R.id.action_save:
                DBModel model = createDebtor();

                if (model != null)
                {
                    if (debtorId < 0) {
                        dbManager.addDebtor(model);
                        Toast.makeText(ThemNoMoi.this, "Thêm thành công!", Toast.LENGTH_LONG).show();

                        switch (sourceActivity) {
                            case MAIN:
                                startActivity(new Intent(ThemNoMoi.this, XuLyDSNo.class));
                                break;
                        }
                        finish();
                    }

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void Initialize() {
        dbManager = new DBHelper(this);
        edtName = findViewById(R.id.name);
        edtPhone = findViewById(R.id.phone);
        edtInterest_rate = findViewById(R.id.rate);
        edtDate = findViewById(R.id.date);
        btnSelectDate = findViewById(R.id.select_date);
        edtDescription = findViewById(R.id.description);
        edtAddress = findViewById(R.id.address);
        edtDebt = findViewById(R.id.amount);


        /// định dạng số tiền, copy
            edtDebt.addTextChangedListener(new TextWatcher() {
                private String current = " ";

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().equals("")) {
                        if (!s.toString().equals(current)) {
                            String cleanString = s.toString().replaceAll("[,.]", "");
                            double parsed = Double.parseDouble(cleanString);
                            String formatted = NumberFormat.getInstance().format((parsed));
                            current = formatted;
                            edtDebt.setText(formatted);
                            edtDebt.setSelection(formatted.length());
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
        });



            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            edtDate.setText(formatter.format(calendar.getTime()));

            btnSelectDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectDate();
                }
            });

    }

    private void selectDate() {
        final Calendar calendar = Calendar.getInstance();
        int mDay = calendar.get(Calendar.DATE);
        int mMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                edtDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, mYear, mMonth, mDay);

        // gioi han ngay chon ve sau
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }




    // Tạo chủ nợ mới
    private DBModel createDebtor() {
        if (checkValue()) {
            String name = edtName.getText().toString();
            String phone = edtPhone.getText().toString();
            String address = edtAddress.getText().toString();
            String strDebt = (edtDebt.getText().toString()).replaceAll("[,.]", "");
            Integer debt = Integer.valueOf(strDebt);
            Double interest_rate = Double.valueOf(edtInterest_rate.getText().toString());
            String date = edtDate.getText().toString();
            String description = edtDescription.getText().toString();

            DBModel debtor;
            debtor = new DBModel(name, phone, address, debt, interest_rate, date, date, description);

            return debtor;
        }
        return null;
    }



    // Xử lý lỗi nhập liệu
    private boolean checkValue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lỗi");
        if (edtName.getText().toString().equals("")) {
            Toast.makeText(ThemNoMoi.this, "Vui lòng nhập tên", Toast.LENGTH_LONG).show();
            return false;
        }
        if (edtDebt.getText().toString().equals("")) {
            Toast.makeText(ThemNoMoi.this, "Vui lòng nhập số tiền nợ", Toast.LENGTH_LONG).show();
            return false;
        }
        if (edtInterest_rate.getText().toString().equals("")) {
            edtInterest_rate.setText("0.0");
        }
        return true;
    }

}
