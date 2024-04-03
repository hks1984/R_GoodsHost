package com.sarin.prod.goodshost.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.sarin.prod.goodshost.MainActivity;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.databinding.ActivityLoginBinding;

import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.util.PreferenceManager;
import com.sarin.prod.goodshost.util.StringUtil;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {

    private static String TAG = MainApplication.TAG;
    private AppBarConfiguration appBarConfiguration;
    private ActivityLoginBinding binding;

    static StringUtil sUtil = StringUtil.getInstance();
    /**
     *  Google 로그인
     */
    private static final int RC_SIGN_IN = 9001;
    // 구글api클라이언트
    private GoogleSignInClient mGoogleSignInClient;
    // 구글 계정
    private GoogleSignInAccount gsa;
    // 파이어베이스 인증 객체 생성
    private FirebaseAuth mAuth;
    // 구글  로그인 버튼
    private SignInButton btnGoogleLogin;
    private Button btnLogoutGoogle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String userId = PreferenceManager.getString(getApplicationContext(), "userId");
        // userId가 null이면
        if(sUtil.nullCheck(userId)){

            try{
                userId = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
                PreferenceManager.setString(getApplicationContext(), "userId", userId);
            }catch(Exception e){

            }
            if(sUtil.nullCheck(userId)){
                long currentTimeMillis = System.currentTimeMillis();
                String deviceModel = android.os.Build.MODEL;
                userId = deviceModel + Long.toString(currentTimeMillis);
                userId = userId.trim();
                PreferenceManager.setString(getApplicationContext(), "userId", userId);
            }

        }
        // userId가 기존 있으면.
        else{
            userId = userId;
        }

        TextView noAccLogin = binding.noAccLogin;
        noAccLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.setString(getApplicationContext(), "isPermission", "1");

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 파이어베이스 인증 객체 선언
        mAuth = FirebaseAuth.getInstance();

        // Google 로그인을 앱에 통합
        // GoogleSignInOptions 개체를 구성할 때 requestIdToken을 호출
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        btnGoogleLogin = findViewById(R.id.googleLogin);
        TextView googleText = (TextView) btnGoogleLogin.getChildAt(0);
        googleText.setText(getString(R.string.google_login_text));
        btnGoogleLogin.setOnClickListener(view -> {
            // 기존에 로그인 했던 계정을 확인한다.
            gsa = GoogleSignIn.getLastSignedInAccount(LoginActivity.this);

            if (gsa != null) // 로그인 되있는 경우
                Toast.makeText(LoginActivity.this, R.string.google_status_login, Toast.LENGTH_SHORT).show();
            else
                signIn();
        });

//        btnLogoutGoogle = findViewById(R.id.btn_logout_google);
//        btnLogoutGoogle.setOnClickListener(view -> {
//            signOut(); //로그아웃
//        });


        /**
         * 카카오
         */
        // 카카오톡이 설치되어 있는지 확인하는 메서드 , 카카오에서 제공함. 콜백 객체를 이용합.
        Function2<OAuthToken,Throwable,Unit> callback =new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            // 콜백 메서드 ,
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                Log.e(TAG,"CallBack Method");
                //oAuthToken != null 이라면 로그인 성공
                if(oAuthToken!=null){
                    // 토큰이 전달된다면 로그인이 성공한 것이고 토큰이 전달되지 않으면 로그인 실패한다.
                    updateKakaoLoginUi();

                }else {
                    //로그인 실패
                    Log.e(TAG, "invoke: login fail" );
                }

                return null;
            }
        };

        // 로그인 버튼 클릭 리스너
        binding.kakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 해당 기기에 카카오톡이 설치되어 있는 확인
                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)){
                    UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, callback);
                }else{
                    // 카카오톡이 설치되어 있지 않다면
                    UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);
                }
            }
        });


    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    /* 사용자 정보 가져오기 */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            if (acct != null) {
                firebaseAuthWithGoogle(acct.getIdToken());

                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                Log.d(TAG, "handleSignInResult:personName "+personName);
                Log.d(TAG, "handleSignInResult:personGivenName "+personGivenName);
                Log.d(TAG, "handleSignInResult:personEmail "+personEmail);
                Log.d(TAG, "handleSignInResult:personId "+personId);
                Log.d(TAG, "handleSignInResult:personFamilyName "+personFamilyName);
                Log.d(TAG, "handleSignInResult:personPhoto "+personPhoto);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "" + e);
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }


    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        Toast.makeText(LoginActivity.this, R.string.google_success_login, Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, R.string.google_failed_login, Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

    }

    /* 로그아웃 */
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    mAuth.signOut();
                    Toast.makeText(LoginActivity.this, R.string.google_success_logout, Toast.LENGTH_SHORT).show();
                    // ...
                });
        gsa = null;
    }

    /* 회원 삭제요청 */
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, task -> {
                    // ...
                });
    }



    private void updateKakaoLoginUi() {

        // 로그인 여부에 따른 UI 설정

        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {

                if (user != null) {

//                    // 유저의 아이디
//                    Log.d(TAG, "invoke: id =" + user.getId());
//                    // 유저의 이메일
//                    Log.d(TAG, "invoke: email =" + user.getKakaoAccount().getEmail());
//                    // 유저의 닉네임
//                    Log.d(TAG, "invoke: nickname =" + user.getKakaoAccount().getProfile().getNickname());
//                    // 유저의 성별
//                    Log.d(TAG, "invoke: gender =" + user.getKakaoAccount().getGender());
//                    // 유저의 연령대
//                    Log.d(TAG, "invoke: age=" + user.getKakaoAccount().getAgeRange());
//                    // 유저 닉네임 세팅해주기
//                    Log.d(TAG, "invoke: profile = "+user.getKakaoAccount().getProfile().getThumbnailImageUrl());

                    String USER_ID =user.getKakaoAccount().getEmail();
                    if(sUtil.nullCheck(USER_ID)){
                       return null;
                    }
                    else {
                        PreferenceManager.setString(getApplicationContext(), "userId", USER_ID);
                    }

                } else {
                    // 로그인 되어있지 않으면

                }
                return null;
            }
        });
    }


}