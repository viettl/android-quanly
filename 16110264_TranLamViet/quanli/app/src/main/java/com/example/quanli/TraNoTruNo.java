package com.example.quanli;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TraNoTruNo  extends BottomSheetDialogFragment {

    private TextView tralai, tragocvalai;
    public  static TraNoTruNo newInstance(){
        return  new TraNoTruNo();
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        // chuyen xml -> view
        // attach to root -> chuyen ngay lap tuc
        View v = inflater.inflate(R.layout.info_chi_tra_no, container, false);

        tralai = (TextView) v.findViewById(R.id.tv_tra_laiy);

        tralai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                XuLyNo currentActivity = (XuLyNo) getActivity();
                if (currentActivity != null) {
                    currentActivity.payInterest();
                }
            }
        });

        tragocvalai = (TextView) v.findViewById(R.id.tv_tra_lai_goc);
        tragocvalai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                XuLyNo currentActivity = (XuLyNo) getActivity();
                if (currentActivity != null) {
                    currentActivity.payDebtAndInterest();
                }
            }
        });
        return v;
    }
}
