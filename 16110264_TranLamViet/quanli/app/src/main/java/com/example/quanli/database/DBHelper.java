package com.example.quanli.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.quanli.model.DBModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private final String TAG = "DBManager";

    private static final String DATABASE_NAME = "quanli.db";
    private static final String TABLE_NAME = "thongtin"; //debtors
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PHONE = "phone";
    private static final String ADDRESS = "address";
    private static final String DEBT = "debt";
    private static final String INTEREST_RATE = "interest_rate";
    private static final String DATE = "date";
    private static final String INTEREST_DATE = "interest_date";
    private static final String DESCRIPTION = "description";
    private static final int VERSION = 1;
    private Context context;

    private String SQLquery = "CREATE TABLE " + TABLE_NAME + " (" + ID + " integer primary key, " +
            NAME + " TEXT, " +
            PHONE + " TEXT, " +
            ADDRESS + " TEXT, " +
            DEBT + " integer, " +
            INTEREST_RATE + " DOUBLE, " +
            DATE + " TEXT, " +
            INTEREST_DATE + " TEXT, " +
            DESCRIPTION + " TEXT)";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
        Log.d(TAG, "DBManager:");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLquery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    // them nguoi no moi
    public void addDebtor(DBModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, model.getModelName());
        values.put(PHONE, model.getModelPhone());
        values.put(ADDRESS, model.getModelAddress());
        values.put(DEBT, model.getModelDebt());
        values.put(INTEREST_RATE, model.getModelInterest_rate());
        values.put(DATE, model.getModelDate());
        values.put(INTEREST_DATE, model.getModelInterest_date());
        values.put(DESCRIPTION, model.getModelDescription());
        db.insert(TABLE_NAME, null, values);
        db.close();


    }

    // lay danh sach nguoi vay, xep theo ten tang dan
    public List<DBModel> getAllDebtorNameASC() {
        List<DBModel> ListDebtor = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + NAME + " ASC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DBModel model = new DBModel();
                model.setModelId(cursor.getInt(0));
                model.setModelName(cursor.getString(1));
                model.setModelPhone(cursor.getString(2));
                model.setModelAddress(cursor.getString(3));
                model.setModelDebt(cursor.getInt(4));
                model.setModelInterest_rate(cursor.getDouble(5));
                model.setModelDate(cursor.getString(6));
                model.setModelInterest_date(cursor.getString(7));
                model.setModelDescription(cursor.getString(8));
                ListDebtor.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ListDebtor;
    }


/*
        cập nhật tiền nợ
            + mỗi lần thao tác trừ tiền lãi, tiền gốc, sử dụng hàm này để cập nhật
            + các cập nhật được nhận qua model
 */
    public boolean UpdateDebtor(DBModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, model.getModelName());
        values.put(PHONE, model.getModelPhone());
        values.put(ADDRESS, model.getModelAddress());
        values.put(DEBT, model.getModelDebt());
        values.put(INTEREST_RATE, model.getModelInterest_rate());
        values.put(DATE, model.getModelDate());
        values.put(INTEREST_DATE, model.getModelInterest_date());
        values.put(DESCRIPTION, model.getModelDescription());


        if (db.update(TABLE_NAME, values, ID + "=?", new String[]{String.valueOf(model.getModelId())}) != 0) {

            db.close();
            return true;
        }
        db.close();
        return false;
    }

    // xóa tất cả thông tin người nợ
    public boolean deleteDebtor(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db.delete(TABLE_NAME, ID + "=?", new String[]{String.valueOf(id)}) > 0) {
            db.close();
            return true;
        }
        db.close();
        return false;
    }
    // tong tien no goc cua tat ca nguoi no
    public int sumOfDebt() {
        int sumDetb = 0;
        String selectQuery = "SELECT  SUM(debt) FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst())
            sumDetb = cursor.getInt(0);
        cursor.close();
        db.close();
        return sumDetb;
    }

    // lấy người nợ theo id
    /*
        sử dụng khi click vào một item người nợ nào đó
            + lấy tất cả thông tin thông qua id
     */
    public DBModel getDebtorById(int id) {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + ID + " = " + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        DBModel model = new DBModel();
        if (cursor.moveToFirst()) {
            model.setModelId(cursor.getInt(0));
            model.setModelName(cursor.getString(1));
            model.setModelPhone(cursor.getString(2));
            model.setModelAddress(cursor.getString(3));
            model.setModelDebt(cursor.getInt(4));
            model.setModelInterest_rate(cursor.getDouble(5));
            model.setModelDate(cursor.getString(6));
            model.setModelInterest_date(cursor.getString(7));
            model.setModelDescription(cursor.getString(8));
        }
        cursor.close();
        db.close();
        return model;
    }



    // tính tiền lãi ( hiện tại đang tính tiền lãi theo năm)
    /*

        cách thức tính đang sử dụng : (tiền lãi / 365 )* số ngày đã mượn, tính từ ngày trã lãi cuối cùng
     */
    public double calculateInterest(DBModel model)
    {
        Calendar calendar = Calendar.getInstance();
        Date toDay = calendar.getTime();
        String date = model.getModelInterest_date();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date interest = new Date();

        try {
            interest = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // convert sang milliesecond
        long totalDay = (toDay.getTime() - interest.getTime()) / (24 * 3600 * 1000);
        // tinh tien
        double interestRate = model.getModelInterest_rate() / 100 / 365 * model.getModelDebt() * totalDay;
        return Math.round(interestRate * 10) / 10;
    }


    /// trả về danh sách chủ nợ trên 1 tháng chưa trả lãi
    public List<DBModel> notifyDebtor()
    {
        List<DBModel> ListDebtor = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + DEBT + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();

        final Calendar calendar = Calendar.getInstance();

        Date toDay = calendar.getTime();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst())
        {
            do
                {
                    DBModel model = new DBModel();
                    model.setModelId(cursor.getInt(0));
                    model.setModelName(cursor.getString(1));
                    model.setModelPhone(cursor.getString(2));
                    model.setModelAddress(cursor.getString(3));
                    model.setModelDebt(cursor.getInt(4));
                    model.setModelInterest_rate(cursor.getDouble(5));
                    model.setModelDate(cursor.getString(6));
                    model.setModelInterest_date(cursor.getString(7));
                    model.setModelDescription(cursor.getString(8));

                    // cap nhat tien lai
                    model.setModelDebt((int) Math.round(calculateInterest(model)));

                    // Xử lý loc những chủ nợ nợ nhiều ngày

                    // lấy ngày trã lãi mới nhất
                    String date = model.getModelInterest_date();
                    // format lai
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                    Date interest = new Date();
                    try {
                        // convert lai date tra lai gan nhat
                        interest = simpleDateFormat.parse(date);

                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }

                    long totalDay = (toDay.getTime() - interest.getTime()) / (24 * 3600 * 1000); // 1000 : convert to milicsection time


                    int today = calendar.get(Calendar.DATE);

                    if (model.getModelDate().length() > 5 && calculateInterest(model) != 0) {
                        if (today == Integer.valueOf(model.getModelDate().substring(0, 2)) || totalDay >= 29) // lay ngay
                            ListDebtor.add(model);
                    }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ListDebtor;
    }
}
