package com.gpridesolutions.buber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gpridesolutions.buber.models.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    Button login, register;
    RelativeLayout rootLayout;
    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_main);

        //init firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("users");

        //init views
        login = findViewById(R.id.sign_in);
        login.setOnClickListener(this);

        register = findViewById(R.id.sign_up);
        register.setOnClickListener(this);

        rootLayout = findViewById(R.id.root_layout);
    }

    @Override
    public void onClick(View v) {
        if(v == login){
            showLoginDialog();
        }else if(v == register){
            showRegisterDialog();
        }
    }

    private void showRegisterDialog(){
        final AlertDialog.Builder reg = new AlertDialog.Builder(this);
        reg.setTitle("Register");
        reg.setMessage("Register with your email address");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register, null);

        final MaterialEditText email = register_layout.findViewById(R.id.reg_email);
        final MaterialEditText password = register_layout.findViewById(R.id.reg_password);
        final MaterialEditText re_pass = register_layout.findViewById(R.id.reg_retype_password);
        final MaterialEditText username = register_layout.findViewById(R.id.reg_username);
        final MaterialEditText phone = register_layout.findViewById(R.id.reg_phone);

        reg.setView(register_layout);
        reg.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //validation
                if(validate(email, "email") && validate(password, "password") &&
                        validate(re_pass, "password") && validate(username, "username") &&
                        validate(phone, "phone") &&
                        password.getText().toString().trim().equals(re_pass.getText().toString())){
                    auth.createUserWithEmailAndPassword(email.getText().toString().trim(),
                            password.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            //save user in db
                            User user = new User(email.getText().toString().trim(),
                                    password.getText().toString().trim(),
                                    username.getText().toString().trim(),
                                    phone.getText().toString().trim());

                            //Use email as key in db
                            users.child(user.getEmail())
                                    .setValue(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Snackbar.make(rootLayout, "Registration successful", Snackbar.LENGTH_LONG)
                                                    .show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Snackbar.make(rootLayout, "Registration failed "+e.getMessage(), Snackbar.LENGTH_LONG)
                                                    .show();
                                        }
                                    });

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(rootLayout, "Registration failed "+e.getMessage(), Snackbar.LENGTH_LONG)
                                            .show();
                                }
                            });
                }else{
                    Snackbar.make(rootLayout, "Registration failed", Snackbar.LENGTH_LONG)
                            .show();
                }

            }
        });

        reg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        reg.show();
    }

    private void showLoginDialog(){
        final AlertDialog.Builder reg = new AlertDialog.Builder(this);
        reg.setTitle("Register");
        reg.setMessage("Login with your email address");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login, null);

        final MaterialEditText email = login_layout.findViewById(R.id.reg_email);
        final MaterialEditText password = login_layout.findViewById(R.id.reg_password);

        reg.setView(login_layout);
        reg.setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();



                final SpotsDialog waitingDialog = new SpotsDialog(MainActivity.this);
                waitingDialog.show();

                //validationH
                if(validate(email, "email") && validate(password, "password")){
                    auth.signInWithEmailAndPassword(email.getText().toString().trim(),
                            password.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            waitingDialog.dismiss();
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            finish();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    waitingDialog.dismiss();
                                    Snackbar.make(rootLayout, "Sign in failed "+e.getMessage(), Snackbar.LENGTH_LONG)
                                            .show();
                                }
                            });
                }else{
                    Snackbar.make(rootLayout, "Sign in failed", Snackbar.LENGTH_LONG)
                            .show();
                }

            }
        });

        reg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        reg.show();
    }

    private boolean validate(MaterialEditText editText, String type){
        if(type.equals("password")){
            if(editText.getText().toString().trim().length() < 6){
                Snackbar.make(rootLayout, "Password should be more than 6 characters",Snackbar.LENGTH_SHORT)
                        .show();
                return false;
            }
        }else if(type.equals("phone")){
            if(!TextUtils.isDigitsOnly(editText.getText().toString().trim())){
                Snackbar.make(rootLayout, "Phone Number contains only digits",Snackbar.LENGTH_SHORT)
                        .show();
                return false;
            }
        }
        else{
            if(TextUtils.isEmpty(editText.getText().toString().trim())){
                Snackbar.make(rootLayout, "Field "+type+" cannot be empty",Snackbar.LENGTH_SHORT)
                        .show();
                return false;
            }
        }
        return true;
    }
}
