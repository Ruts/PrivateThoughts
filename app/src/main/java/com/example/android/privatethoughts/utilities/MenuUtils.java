package com.example.android.privatethoughts.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.android.privatethoughts.LoginActivity;
import com.example.android.privatethoughts.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class MenuUtils {
    public static void logoutAction(Context context) {
        GoogleSignInClient mGoogleSignInClient;

        FirebaseAuth.getInstance().signOut();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
        mGoogleSignInClient.signOut();

        LoginActivity.EMAIL_ACCOUNT = null;

        SharedPreferences sharedpreferences =
                context.getSharedPreferences(LoginActivity.MY_PREFRENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }
}
