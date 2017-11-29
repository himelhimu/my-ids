package com.sabbir.andorid.apps.myids.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sabbir.andorid.apps.myids.MainActivity;
import com.sabbir.andorid.apps.myids.R;
import com.sabbir.andorid.apps.myids.brain.DumbApp;
import com.sabbir.andorid.apps.myids.utilities.GPSTracker;
import com.sabbir.andorid.apps.myids.utilities.Helper;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RegisterActivity extends AppCompatActivity {
    private static final String TAG ="RegisterActivity" ;
    private static final int RC_GOOGLE_SING_IN_CODE =21 ;

    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.et_Email)
    EditText etEmail;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.imageView4)
    ImageView imageView;

    @BindView(R.id.sign_in_button)
    SignInButton signInButton;


    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog mProgressDialog;

    private GPSTracker gps;
    private String mLatitude;
    private String mLongitude;
    private LocationManager locationManager;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  Fabric.with(this,new Crashlytics());
        setContentView(R.layout.register_or_login);
        ButterKnife.bind(this);



        String[] PERMISSIONS={Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};

        if (Helper.hasPermissions(this,PERMISSIONS)){

        }else {
            ActivityCompat.requestPermissions(this,PERMISSIONS,19);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        gps=new GPSTracker(this);

        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
*/
        mFirebaseAuth=FirebaseAuth.getInstance();
        mProgressDialog=new ProgressDialog(this);

        locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!isLocationEnabled()){
          //  showSettingsAlert();
        }




        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.setMessage("Logging in...Please Wait");
                RegisterActivity.this.handleLoginClick();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.setMessage("Registering...Please Wait");
                RegisterActivity.this.handleRegisterClick();
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RegisterActivity.this.hiddenCallToMainActivity();
                return true;
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sigIn();
            }
        });
    }

    private void sigIn() {
        Log.d("TAG","Clicked");
        Intent intent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_GOOGLE_SING_IN_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==19){
            for (int result:grantResults){
                if (result!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,permissions, requestCode);
                }
            }
        }else {

        }
    }



    public void signInWithGoogle(View view){
        Log.d("TAG","Clicked");
        Intent intent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_GOOGLE_SING_IN_CODE);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        mProgressDialog.show();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            RegisterActivity.this.updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            RegisterActivity.this.updateUI(null);
                        }

                        // [START_EXCLUDE]
                        mProgressDialog.dismiss();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

            if (user!=null){
               DumbApp.getInstance().setFirebaseUser(user);
                String name=user.getDisplayName();
                callMainActivity();
                Log.d("TAG",name);
            }else {

            }

    }

    private void signOut() {
        // Firebase sign out
        mFirebaseAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        RegisterActivity.this.updateUI(null);
                    }
                });
    }



    private void hiddenCallToMainActivity() {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("loggedin",true);
        editor.apply();
        callMainActivity();
    }

    private void handleRegisterClick() {
        if(TextUtils.isEmpty(etEmail.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString())){
            Toast.makeText(getApplicationContext(),"Please provide email/password",Toast.LENGTH_SHORT).show();
        }else {
            mProgressDialog.show();

            justGetLocation();

            String email=etEmail.getText().toString().trim();
            String password=etPassword.getText().toString().trim();

            mFirebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this.getApplicationContext());
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("loggedin", true);
                                editor.apply();
                                mProgressDialog.dismiss();
                                RegisterActivity.this.callMainActivity();
                            } else {
                                mProgressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this.getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }) ;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void callMainActivity() {
        Intent intent=new Intent();
        setResult(RESULT_OK);
        finish();
    }

    private void handleLoginClick() {
        if(TextUtils.isEmpty(etEmail.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString())){
            Toast.makeText(getApplicationContext(),"Please provide email/password",Toast.LENGTH_SHORT).show();
        }else {
            mProgressDialog.show();
            String email=etEmail.getText().toString().trim();
            String password=etPassword.getText().toString().trim();

            mFirebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this.getApplicationContext());
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("loggedin", true);
                                editor.apply();
                                mProgressDialog.dismiss();
                                RegisterActivity.this.callMainActivity();
                            } else {
                                mProgressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this.getApplicationContext(), "Email/Password Mismatch,try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }

    private void getLocation() {
        //gps=new GPSTracker(this);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Allow location data")
                .setMessage("It'll be used to get Weather info");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (gps.canGetLocation()) {
                    mLatitude = Double.toString(gps.getLatitude());
                    mLongitude = Double.toString(gps.getLongitude());

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("lat", mLatitude);
                    editor.putString("lan", mLongitude);
                    editor.apply();

                    // \n is for new line
                    Log.i("LocationData ", mLatitude + "," + mLongitude);
                } else {
                    RegisterActivity.this.showSettingsAlert();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog dialog=builder.create();
        dialog.show();
       // pDialog.dismiss();
    }

    void justGetLocation(){
        if(gps.canGetLocation()){
            mLatitude = Double.toString(gps.getLatitude());
            mLongitude = Double.toString(gps.getLongitude());

            SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("lat",mLatitude);
            editor.putString("lan",mLongitude);
            editor.apply();

            // \n is for new line
            Log.i("LocationData ",mLatitude+","+mLongitude);
        }else{
            showSettingsAlert();
        }
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("GPS is Off");

        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                // startActivity(intent);
                RegisterActivity.this.startActivityForResult(intent, 21);
                //finish();
                // recreate();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog=alertDialog.create();

        dialog.show();
    }

    private class CityName{
        Double lat,lon;
        CityName(String lat,String lon){
            this.lat=Double.valueOf(lat);
            this.lon=Double.valueOf(lon);
        }

        public String getCityName(){
            String cityName="";
            Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses=geocoder.getFromLocation(lat,lon,1);
                cityName=addresses.get(0).getAddressLine(0);
                String statename=addresses.get(0).getAddressLine(1);
                String countryName=addresses.get(0).getAddressLine(2);
                //  Log.i("GeoCoder",cityName+statename+countryName);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return cityName;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        gps.stopUsingGPS();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SING_IN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }

    }
}
