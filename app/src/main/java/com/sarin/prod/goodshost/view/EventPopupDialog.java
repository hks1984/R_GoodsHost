package com.sarin.prod.goodshost.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.util.StringUtil;
import com.sarin.prod.goodshost.util.PreferenceManager;

public class EventPopupDialog {

    private StringUtil stringUtil = StringUtil.getInstance();
    public void showImageDialog(Context context, String imageUrl) {
        // AlertDialog 빌더 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // 커스텀 레이아웃 인플레이트
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_event_image, null);

        // 이미지 뷰에 Glide로 이미지 로드
        ImageView imageView = dialogView.findViewById(R.id.dialog_image_view);
        Glide.with(context)
                .load(imageUrl)  // 네트워크 URL이나 로컬 파일 경로를 사용할 수 있음
//                .placeholder(R.drawable.placeholder)  // 로딩 중일 때 보여줄 이미지
//                .error(R.drawable.error_image)  // 에러가 발생했을 때 보여줄 이미지
                .into(imageView);

        // 다이얼로그에 커스텀 레이아웃 설정
        builder.setView(dialogView);

        // 다이얼로그 생성
        AlertDialog dialog = builder.create();

        // 팝업 외부를 클릭했을 때 닫히지 않도록 설정
        dialog.setCancelable(false);

        // 확인 버튼 설정
        LinearLayout okButton = dialogView.findViewById(R.id.dialog_button_ok);
        okButton.setOnClickListener(v -> {
            // 팝업 창 닫기
            dialog.dismiss();  // 'dialog'를 이 범위 내에서 참조할 수 있음
        });

        LinearLayout dayButton = dialogView.findViewById(R.id.dialog_button_day);
        dayButton.setOnClickListener(v -> {
            // 팝업 창 닫기
            String currentDate = stringUtil.getCurrentDate();
            PreferenceManager.setString(context, "shouldShowPopup", currentDate);
            dialog.dismiss();  // 'dialog'를 이 범위 내에서 참조할 수 있음
        });

        // 다이얼로그 표시
        dialog.show();
    }
}

