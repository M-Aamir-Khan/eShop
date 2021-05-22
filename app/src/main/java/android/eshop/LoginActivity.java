package android.eshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.eshop.Model.Users;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
private Button LoginButton;
private CheckBox checkBox;
private TextView adminlink,notadminlink,ForgetPassword;
private EditText InputPhoneNumber,InputPassword;
private ProgressDialog loadingbar;
    private String parentDbName="Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ForgetPassword=findViewById(R.id.forget_password_link);
        adminlink=findViewById(R.id.admin_panel_link);
        notadminlink=findViewById(R.id.not_admin_panel_link);
        checkBox=findViewById(R.id.remember_me_chkb);
        LoginButton=findViewById(R.id.login_btn);
        InputPassword=findViewById(R.id.login_password_input);
        InputPhoneNumber=findViewById(R.id.login_phone_number_input);
        loadingbar=new ProgressDialog(this);
        Paper.init(this);
        ForgetPassword.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(LoginActivity.this,ResetPasswordActivity.class);
        intent.putExtra("check","login");
        startActivity(intent);
    }
});
        adminlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login Admin");
                adminlink.setVisibility(View.INVISIBLE);
                notadminlink.setVisibility(View.VISIBLE);
                parentDbName="Admins";
            }
        });
        notadminlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login");
                adminlink.setVisibility(View.VISIBLE);
                notadminlink.setVisibility(View.INVISIBLE);
                parentDbName="Users";
            }
        });
LoginButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        LoginUser();
    }
});
    }

    private void LoginUser() {
        String phone=InputPhoneNumber.getText().toString();
        String password=InputPassword.getText().toString();
         if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please enter your phone number...",Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter your password...",Toast.LENGTH_SHORT);
        }
         else {
             loadingbar.setTitle("Login Account");
             loadingbar.setMessage("Please wait, while we are validating your credentials");
             loadingbar.setCanceledOnTouchOutside(false);
             loadingbar.show();
             AllowAccesstoAccount( phone,password);
         }

    }

    private void AllowAccesstoAccount(final String phone, final String password) {
        if(checkBox.isChecked()){
            Paper.book().write(prevalent.userphonekey,phone);
            Paper.book().write(prevalent.userpassword,password);
        }
        final DatabaseReference rootref= FirebaseDatabase.getInstance().getReference();
        rootref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(phone).exists()){
                    Users usersdata=snapshot.child(parentDbName).child(phone).getValue(Users.class);
                    if (usersdata.getPhone().equals(phone)){
                     if (usersdata.getPassword().equals(password)){
                        if (parentDbName.equals("Admins")){
                            loadingbar.dismiss();
                            Toast.makeText(LoginActivity.this,"Login_Successful",Toast.LENGTH_SHORT);
                            prevalent.currentOnlineUser=usersdata;
                            Intent home=new Intent(LoginActivity.this,AdminCatergoryActivity.class);
                            startActivity(home);
                        }
                        else {
                            loadingbar.dismiss();
                            Toast.makeText(LoginActivity.this,"Login_Successful",Toast.LENGTH_SHORT);
                            Intent home=new Intent(LoginActivity.this,HomeActivity.class);
                            prevalent.currentOnlineUser=usersdata;
                            startActivity(home);
                        }
                     }
                     else {
                      loadingbar.dismiss();
                      Toast.makeText(LoginActivity.this,"password is incorrect",Toast.LENGTH_SHORT);
                     }
                    }


                } else {

                    loadingbar.dismiss();
                    Toast.makeText(LoginActivity.this,"this phone number is not exist",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}