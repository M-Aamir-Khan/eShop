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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
private Button joinNowButton,loginButton;

    private ProgressDialog loadingbar;
    Users usersdata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);
      loadingbar= new ProgressDialog(this);
        joinNowButton=findViewById(R.id.join_now_btn);
        loginButton=findViewById(R.id.main_login_btn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(login);
            }
        });
    joinNowButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent register=new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(register);
        }
    });
    String userphonekey= Paper.book().read(prevalent.userphonekey);
    String userpasswordkey=Paper.book().read(prevalent.userpassword);
    if (userphonekey!="" && userpasswordkey!=""){
        if (!TextUtils.isEmpty(userphonekey) && !TextUtils.isEmpty(userpasswordkey)){
            allowaccess(userphonekey,userpasswordkey);
        }
    }
    }

    private void allowaccess(final String phone, final String password) {
        final DatabaseReference rootref= FirebaseDatabase.getInstance().getReference();
        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(phone).exists() || snapshot.child("Admins").child(phone).exists()){
                    if(snapshot.child("Users").child(phone).exists()) {
                        Log.e("dddddf",phone);
                        usersdata = (snapshot.child("Users").child(phone).getValue(Users.class));
                    }
                    else
                       usersdata= snapshot.child("Admins").child(phone).getValue(Users.class);
                    Log.e("user", String.valueOf(snapshot.child("Users")));
                    Log.e("admin", String.valueOf(snapshot.child("Admins")));
                    Log.e("us", String.valueOf(usersdata));


                    if (usersdata.getPhone().equals(phone)){
                        if (usersdata.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this,"Login_Successful",Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                            prevalent.currentOnlineUser=usersdata;
                            if (snapshot.child("Users").child(phone).exists()){
                            Intent home=new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(home);
                            }
                            else {
                                startActivity(new Intent(MainActivity.this, AdminCatergoryActivity.class));
                            }
                            }
                        else {
                            loadingbar.dismiss();
                            Toast.makeText(MainActivity.this,"this phone number is not exist",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
