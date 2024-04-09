package com.sarin.prod.goodshost.fragment.setting;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sarin.prod.goodshost.activity.LoginActivity;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.MainActivity;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.activity.CategoryProductListActivity;
import com.sarin.prod.goodshost.activity.PermissionActivity;
import com.sarin.prod.goodshost.activity.SplashActivity;
import com.sarin.prod.goodshost.activity.WebViewActivity;
import com.sarin.prod.goodshost.databinding.FragmentFavoriteBinding;
import com.sarin.prod.goodshost.databinding.FragmentSettingBinding;
import com.sarin.prod.goodshost.fragment.favorite.FavoriteViewModel;
import com.sarin.prod.goodshost.fragment.home.HomeFragment;
import com.sarin.prod.goodshost.item.ReturnMsgItem;
import com.sarin.prod.goodshost.item.UserItem;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.CustomSnackbar;
import com.sarin.prod.goodshost.util.PreferenceManager;
import com.sarin.prod.goodshost.util.StringUtil;
import com.sarin.prod.goodshost.view.PopupDialogClickListener;
import com.sarin.prod.goodshost.view.PopupDialogUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingFragment extends Fragment {

    private static String TAG = MainApplication.TAG;
    static StringUtil sUtil = StringUtil.getInstance();
    private SettingViewModel mViewModel;
    private FragmentSettingBinding binding;

    private TextView app_version, setting_login, setting_info_nick, setting_info_social;

    private LinearLayout setting_privacy, setting_termsOfUse, setting_acc_delete, setting_easy_login;
    SwitchCompat alarm_switch;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        SettingViewModel settingViewModel =
                new ViewModelProvider(this).get(SettingViewModel.class);

        binding = FragmentSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        app_version = binding.appVersion;
        setting_privacy = binding.settingPrivacy;
        setting_termsOfUse = binding.settingTermsOfUse;
        setting_acc_delete = binding.settingAccDelete;
        setting_easy_login = binding.settingEasyLogin;
        setting_info_nick = binding.settingInfoNick;
        setting_info_social = binding.settingInfoSocial;

        if(!sUtil.nullCheck(PreferenceManager.getString(getContext(), "isSoicalLogin"))){
            setting_easy_login.setVisibility(View.GONE);
        }

        String userNick = PreferenceManager.getString(getContext(), "userNick");
        String nickFullText = String.format(getString(R.string.setting_user_nick), userNick);
        SpannableString spannableString = new SpannableString(nickFullText);

        // 대체된 문자열의 시작과 끝 위치를 찾습니다.
        int start = nickFullText.indexOf(userNick);
        int end = start + userNick.length();
        // 대체된 문자열 색상 변경
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.personal_2)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 대체된 문자열의 크기를 변경합니다. 1.5f는 150%를 의미합니다.
        spannableString.setSpan(new RelativeSizeSpan(0.6f), end, nickFullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 텍스트뷰에 스타일이 적용된 텍스트를 설정합니다.
        setting_info_nick.setText(spannableString);



        String userId = PreferenceManager.getString(getContext(), "userId");
        String idFullText = "";

        if(!sUtil.nullCheck(PreferenceManager.getString(getContext(), "isSoicalLogin"))){
            idFullText = String.format(getString(R.string.setting_user_soical_id), "", userId);
        }else {
            idFullText = String.format(getString(R.string.setting_user_soical_id), "비로그인", userId);
        }
        setting_info_social.setText(idFullText);


        try {
            // 현재 앱의 패키지 이름을 가져옵니다.
            String packageName = getContext().getPackageName();
            // 패키지 매니저를 통해 패키지 정보를 가져옵니다.
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(packageName, 0);
            // 버전 이름과 버전 코드를 가져옵니다.
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;

            app_version.setText(versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // 패키지 이름을 찾을 수 없는 경우의 예외 처리
        }

        alarm_switch = binding.alarmSwitch;
        checkAlarmStatus();

        alarm_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    
                    PreferenceManager.setString(getContext(), "isAlarmStatus", "1");
                    //checkAlarmStatus(1);
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        } else {
                            showPermissionDeniedExplanation();
                        }

                    }

                }else{
                    PreferenceManager.setString(getContext(), "isAlarmStatus", "0");
                    
                }
                checkAlarmStatus();

            }
        });


        setting_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), WebViewActivity.class);
                String url = MainApplication.BASE_URL;
                intent.putExtra("url", url + "privacy");
                v.getContext().startActivity(intent);	//intent 에 명시된 액티비티로 이동
            }
        });

        setting_termsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), WebViewActivity.class);
                String url = MainApplication.BASE_URL;
                intent.putExtra("url", url + "termsOfUse");
                v.getContext().startActivity(intent);	//intent 에 명시된 액티비티로 이동
            }
        });

//        setting_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), LoginActivity.class);
//                v.getContext().startActivity(intent);	//intent 에 명시된 액티비티로 이동
//
//            }
//        });



        setting_acc_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
Log.d(TAG, "ddd");
                PopupDialogUtil.showCustomDialog(getActivity(), new PopupDialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        String USER_ID = PreferenceManager.getString(getContext(), "userId");
                        UserItem userItem = new UserItem();
                        userItem.setUser_id(USER_ID);
                        setAccountDelete(userItem);
                    }
                    @Override
                    public void onNegativeClick() {

                    }
                }, "TWO", getResources().getString(R.string.setting_acc_delete_msg));
            }
        });

        return root;
    }

    public void setAccountDelete(UserItem userItem){

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<ReturnMsgItem> call = service.setAccountDelete("setAccountDelete", userItem);
        call.enqueue(new Callback<ReturnMsgItem>() {
            @Override
            public void onResponse(Call<ReturnMsgItem> call, Response<ReturnMsgItem> response) {
                if(response.isSuccessful()){
                    ReturnMsgItem returnMsgItem = response.body();
                    if(returnMsgItem.getCode() > 0) {

                        PreferenceManager.setString(getContext(), "userId", "");
                        PreferenceManager.setString(getContext(), "androidId", "");
                        PreferenceManager.setString(getContext(), "isPermission", "");
                        PreferenceManager.setString(getContext(), "isLogin", "");
                        PreferenceManager.setString(getContext(), "fcmToken", "");
                        PreferenceManager.setInt(getContext(), "fcmFlag", 0);
                        PreferenceManager.setString(getContext(), "isSoicalLogin", "");

                        PopupDialogUtil.showCustomDialog(getActivity(), new PopupDialogClickListener() {
                            @Override
                            public void onPositiveClick() {
//                                getActivity().finish();
                                getActivity().moveTaskToBack(true); // 태스크를 백그라운드로 이동
                                getActivity().finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                                System.exit(0);
                            }
                            @Override
                            public void onNegativeClick() {
                            }
                        }, "ONE", getResources().getString(R.string.setting_acc_delete_success));
                    }

                }


            }
            @Override
            public void onFailure(Call<ReturnMsgItem> call, Throwable t) {
                PopupDialogUtil.showCustomDialog(getActivity(), new PopupDialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                    }
                    @Override
                    public void onNegativeClick() {
                    }
                }, "ONE", getResources().getString(R.string.server_not_connecting));
            }

        });

    }


    private void checkAlarmStatus() {
        if("1".equals(PreferenceManager.getString(getContext(), "isAlarmStatus"))){
            // 알람 ON일 경우
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // 사용자가 앱알림을 ON했더라도 알림 권한이 OFF 인 경우 OFF 로 재 설정.
                alarm_switch.setChecked(false);
                PreferenceManager.setString(getContext(), "isAlarmStatus", "0");
            } else {
                alarm_switch.setChecked(true);
            }

        } else {
            // OFF일 경우
            alarm_switch.setChecked(false);
        }

    }

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            // 권한이 부여되었으면 원하는 작업 수행
            PreferenceManager.setString(getContext(), "isAlarmStatus", "1");
        } else {
            // 사용자가 알림 권한 2번 이상 거부로 설정에서 직접 알림 설정을 하도록 유도해야함.

            if("1".equals(PreferenceManager.getString(getContext(), "deniedAlarm"))){
                showPermissionDeniedExplanation();
            }
            PreferenceManager.setString(getContext(), "deniedAlarm", "1");
            PreferenceManager.setString(getContext(), "isAlarmStatus", "0");
        }
    });


    private void showPermissionDeniedExplanation() {
        new AlertDialog.Builder(getContext())
                .setTitle(getContext().getResources().getString(R.string.setting_alarm_title))
                .setMessage(getContext().getResources().getString(R.string.setting_alarm_info))
                .setPositiveButton(getContext().getResources().getString(R.string.setting_alarm_set_go), (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton(getContext().getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAlarmStatus();
    }
}