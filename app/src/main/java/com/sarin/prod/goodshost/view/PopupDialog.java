package com.sarin.prod.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sarin.prod.R;

public class PopupDialog extends Dialog {

    private Context context;
    private PopupDialogClickListener popupDialogClickListener;
    private TextView tvTitle, tvNegative, tvPositive;
    private String text;
    private String title;

    public PopupDialog(@NonNull Context context, PopupDialogClickListener popupDialogClickListener) {
        super(context);
        this.context = context;
        this.popupDialogClickListener = popupDialogClickListener;
        //this.text = text;
    }

    public void setText (String text) {
        this.text = text;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog);

        //TextView popupTilte = findViewById(R.id.option_codetype_dialog_title_tv);
        //popupTilte.setText(title);

        TextView popupText = findViewById(R.id.popupText);
        popupText.setText(text);


        // popup dialog 확인 버튼
        tvPositive = findViewById(R.id.btn_no);
        tvPositive.setOnClickListener(v -> {
            // 아니요 버튼 클릭
            this.popupDialogClickListener.onPositiveClick();
            dismiss();
        });

        tvNegative = findViewById(R.id.btn_yes);
        tvNegative.setOnClickListener(v -> {
            // 네 버튼 클릭
            this.popupDialogClickListener.onNegativeClick();
            dismiss();
        });
    }




}
