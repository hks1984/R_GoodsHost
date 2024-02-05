package com.sarin.prod.goodshost.fragment.setting;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sarin.prod.goodshost.activity.LoginActivity;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.activity.CategoryProductListActivity;
import com.sarin.prod.goodshost.databinding.FragmentFavoriteBinding;
import com.sarin.prod.goodshost.databinding.FragmentSettingBinding;
import com.sarin.prod.goodshost.fragment.favorite.FavoriteViewModel;
import com.sarin.prod.goodshost.fragment.home.HomeFragment;
import com.sarin.prod.goodshost.util.CustomSnackbar;

public class SettingFragment extends Fragment {

    private SettingViewModel mViewModel;
    private FragmentSettingBinding binding;

    private TextView app_version, setting_login;

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



        setting_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                v.getContext().startActivity(intent);	//intent 에 명시된 액티비티로 이동

            }
        });




        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        // TODO: Use the ViewModel
    }

}