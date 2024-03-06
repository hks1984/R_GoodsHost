package com.sarin.prod.goodshost;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.snackbar.Snackbar;
import com.sarin.prod.goodshost.databinding.ActivityMainBinding;
import com.sarin.prod.goodshost.util.PreferenceManager;

import android.Manifest;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainApplication.TAG;
    private ActivityMainBinding binding;
    public static final int NOTIFICATION_PERMISSION_CODE = 100;
    public static final int REQUEST_POST_NOTIFICATIONS = 200;

    static int before_itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        before_itemId = R.id.navigation_home;

        navView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            NavOptions.Builder navBuilder = new NavOptions.Builder();

            if (itemId == R.id.navigation_home) {
                if(before_itemId == R.id.navigation_home)   return true;    // 같은 메뉴 반복 클릭 방지
                navBuilder.setEnterAnim(R.anim.from_left_enter)
                        .setExitAnim(R.anim.to_right_exit);
            } else if (itemId == R.id.navigation_search) {
                if(before_itemId == R.id.navigation_search)   return true;  // 같은 메뉴 반복 클릭 방지
                if(before_itemId == R.id.navigation_home){
                    navBuilder.setEnterAnim(R.anim.from_right_enter)
                            .setExitAnim(R.anim.to_left_exit);
                } else {
                    navBuilder.setEnterAnim(R.anim.from_left_enter)
                            .setExitAnim(R.anim.to_right_exit);
                }
            } else if (itemId == R.id.navigation_favorite) {
                if(before_itemId == R.id.navigation_favorite)   return true;    // 같은 메뉴 반복 클릭 방지
                if(before_itemId == R.id.navigation_setting){
                    navBuilder.setEnterAnim(R.anim.from_left_enter)
                            .setExitAnim(R.anim.to_right_exit);
                } else {
                    navBuilder.setEnterAnim(R.anim.from_right_enter)
                            .setExitAnim(R.anim.to_left_exit);
                }
            } else if (itemId == R.id.navigation_setting) {
                if(before_itemId == R.id.navigation_setting)   return true; // 같은 메뉴 반복 클릭 방지
                navBuilder.setEnterAnim(R.anim.from_right_enter)
                        .setExitAnim(R.anim.to_left_exit);
            }

            before_itemId = itemId;

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            navController.navigate(itemId, null, navBuilder.build());
            return true; // true to display the item as the selected item
        });

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_favorite, R.id.navigation_setting)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission();
        } else {
            PreferenceManager.setString(getApplicationContext(), "isAlarmStatus", "1");
        }

    }



    private void checkNotificationPermission() {
        // 특정 권한이 PackageManager.PERMISSION_DENIED 인지, PackageManager.PERMISSION_GRANTED 인지 반환 한다.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            //사용자가 권한 요청을 명시적으로 거부한 경우 true를 반환한다.
            // 사용자가 권한 요청을 처음 보거나, 다시 묻지 않음 선택한 경우, 권한을 허용한 경우 false를 반환한다.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                // 첫 알림 권한 거부 후 실행 했을때 팝업창이 생기지 않고 여기로 빠짐.

            } else {
                // 첫 알림 권한 실행 시 실행됨.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        } else {
            //권한이 이미 부여되었습니다.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 알림 권한을 허용했을때 실행됨.
                PreferenceManager.setString(getApplicationContext(), "isAlarmStatus", "1");
            } else {
                // 알림 권한을 거부했을 때 실행됨
            }
        }
    }

}