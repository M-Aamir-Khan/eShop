package android.eshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
private Button CreateAccountButton;
private EditText InputName,InputPhoneNumber, InputPassword;

 private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        CreateAccountButton=findViewById(R.id.register_btn);
    loadingbar=new ProgressDialog(this);
    InputPhoneNumber=findViewById(R.id.register_phone_number_input);
    InputName=findViewById(R.id.register_username_input);
    InputPassword=findViewById(R.id.register_password_input);
    CreateAccountButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CreateAccount();
        }
    });
    }

    private void CreateAccount() {
    String name=InputName.getText().toString();
    String phone=InputPhoneNumber.getText().toString();
    String password=InputPassword.getText().toString();
    if(TextUtils.isEmpty(name)){
        Toast.makeText(this,"Please enter your name...",Toast.LENGTH_SHORT);
    }
   else if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please enter your phone number...",Toast.LENGTH_SHORT);
        }
    else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter your password...",Toast.LENGTH_SHORT);
        }
    else {
        loadingbar.setTitle("Create Account");
        loadingbar.setMessage("Please wait, while we are validating your credentials");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();
        ValidatephoneNumber(name,phone,password);
    }
    }

    private void ValidatephoneNumber(final String name, final String phone, final String password) {
        final DatabaseReference rootref= FirebaseDatabase.getInstance().getReference();
        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("Users").child(phone).exists())){
                    HashMap<String,Object> userdataMap=new HashMap<>();
                    userdataMap.put("phone",phone);
                    userdataMap.put("password",password);
                    userdataMap.put("name",name);
                   rootref.child("Users").child(phone).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                      if (task.isSuccessful()){
                          Toast.makeText(RegisterActivity.this,"congrate",Toast.LENGTH_SHORT);
                          loadingbar.dismiss();
                          Intent login=new Intent(RegisterActivity.this,LoginActivity.class);
                          startActivity(login);
                      }else {
                          Toast.makeText(RegisterActivity.this,"congrate",Toast.LENGTH_SHORT);
                       loadingbar.dismiss();
                      }
                       }
                   });

                }
                else {
                    Toast.makeText(RegisterActivity.this,"this phone number already exist",Toast.LENGTH_SHORT);
                    loadingbar.dismiss();
                    Intent main=new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(main);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}