package com.sarin.prod.goodshost.fragment.setting;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.sarin.prod.goodshost.activity.LoginActivity;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.MainActivity;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.activity.CategoryProductListActivity;
import com.sarin.prod.goodshost.databinding.FragmentFavoriteBinding;
import com.sarin.prod.goodshost.databinding.FragmentSettingBinding;
import com.sarin.prod.goodshost.fragment.favorite.FavoriteViewModel;
import com.sarin.prod.goodshost.fragment.home.HomeFragment;
import com.sarin.prod.goodshost.util.CustomSnackbar;
import com.sarin.prod.goodshost.util.PreferenceManager;

public class SettingFragment extends Fragment {

    private static String TAG = MainApplication.TAG;
    private SettingViewModel mViewModel;
    private FragmentSettingBinding binding;

    private TextView app_version, setting_login;
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
        setting_login = binding.settingLogin;


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
                    Log.d(TAG, "사용");
                    PreferenceManager.setString(getContext(), "isAlarmStatus", "1");
                    //checkAlarmStatus(1);
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                    }

                }else{
                    PreferenceManager.setString(getContext(), "isAlarmStatus", "0");
                    Log.d(TAG, "해제");
                }

            }
        });





        setting_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                v.getContext().startActivity(intent);	//intent 에 명시된 액티비티로 이동

            }
        });




        return root;
    }

    private void checkAlarmStatus() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            alarm_switch.setChecked(false);
        } else {
            alarm_switch.setChecked(true);
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
                .setTitle("알림 권한 거부됨")
                .setMessage("알림 기능을 사용하려면 앱 설정에서 알림 권한을 활성화해야 합니다.")
                .setPositiveButton("설정으로 이동", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAlarmStatus();
    }
}