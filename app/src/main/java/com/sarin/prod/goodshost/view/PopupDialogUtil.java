package com.sarin.prod.goodshost.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
public class PopupDialogUtil {

    private static String TAG = MainApplication.TAG;
    private static PopupDialog currentDialog = null; // 현재 활성화된 팝업을 추적하는 변수

    public static void showCustomDialog(Context context, PopupDialogClickListener listener, String type, String text) {


        if (currentDialog != null && currentDialog.isShowing()) {
            // 이미 활성화된 팝업이 있으면 추가적인 팝업을 생성하지 않음
            Log.d(TAG, "showCustomDialog");
            return;
        }

        currentDialog = new PopupDialog(context, listener);
        currentDialog.setType(type);
        currentDialog.setText(text);
        currentDialog.setCanceledOnTouchOutside(false);
        currentDialog.setCancelable(false);
        currentDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        currentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        currentDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // 팝업이 닫힐 때 currentDialog 참조를 제거하는 리스너 설정
        currentDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d(TAG, "setOnDismissListener");
                currentDialog.cancel();
                currentDialog.dismiss();
                currentDialog = null; // 팝업 참조 제거

            }
        });

        Log.d(TAG, "currentDialog.show()");
        currentDialog.show();
    }

}
