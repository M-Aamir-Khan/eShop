package android.eshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropOverlayView;

import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
private CircleImageView profileImageView;
private EditText fullNameEditText, userPhoneEditText,addressEditText;
private TextView profileChangeTextBtn,closeTextBtn,saveTextButton;
private Uri imageUri;
private String myUrl="";
private StorageReference storageProfilePrictureRef;
private String checker="";
String parentdb;
private Button securityQuestionBtn;
    private StorageTask uploadTast;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
securityQuestionBtn=findViewById(R.id.security_questions_btn);
        String type=getIntent().getStringExtra("type");
        if (type.equals("Admin"))
            parentdb="Admins";
        else {
            parentdb="Users";
        }
        storageProfilePrictureRef= FirebaseStorage.getInstance().getReference().child("Profile pictures");
        profileImageView=findViewById(R.id.settings_profile_image);
        fullNameEditText=findViewById(R.id.settings_full_name);
        userPhoneEditText=findViewById(R.id.settings_phone_number);
        addressEditText=findViewById(R.id.settings_address);
        profileChangeTextBtn=findViewById(R.id.profile_image_change_btn);
        closeTextBtn=findViewById(R.id.close_settings_btn);
        saveTextButton=findViewById(R.id.update_account_settings_btn);
        userInfoDisplay(profileImageView,fullNameEditText,userPhoneEditText,addressEditText);
securityQuestionBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(SettingsActivity.this,ResetPasswordActivity.class);
        intent.putExtra("check","settings");
        startActivity(intent);
    }
});
    closeTextBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    });
    
    saveTextButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (checker.equals("clicked")){
                userInfoSaved();
            }
            else {
                updateOnlyUserInfo();
            }
        }
    });
    profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            checker="clicked";
              CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(SettingsActivity.this);
        }
    });
    }

    private void updateOnlyUserInfo() {

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child(parentdb);
        HashMap<String,Object> userMap=new HashMap<>();
        userMap.put("name",fullNameEditText.getText().toString());
        userMap.put("address",addressEditText.getText().toString());
        userMap.put("phoneOrder",userPhoneEditText.getText().toString());
        ref.child(prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
        Intent intent =new Intent(SettingsActivity.this,HomeActivity.class);
        intent.putExtra("Admin","Admin");
        startActivity(intent);
        Toast.makeText(SettingsActivity.this,"Profile info updated",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =new Intent(SettingsActivity.this,HomeActivity.class);
        intent.putExtra("Admin","Admin");
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
        CropImage.ActivityResult result=CropImage.getActivityResult(data);
        imageUri=result.getUri();
        profileImageView.setImageURI(imageUri);
    }
    else {
        Toast.makeText(this,"Error try again",Toast.LENGTH_SHORT);
        startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
        finish();
    }
    }

    private void userInfoSaved() {
    if (TextUtils.isEmpty(fullNameEditText.getText().toString())){
        Toast.makeText(this,"Name is mandortory",Toast.LENGTH_SHORT);
    }
        else if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this,"Name is address",Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString())){
            Toast.makeText(this,"Name is address",Toast.LENGTH_SHORT);
        }
       else{
            uploadImage();
        }
    }

    private void uploadImage() {
    final ProgressDialog progressDialog=new ProgressDialog(this);
    progressDialog.setTitle("Update Profile");
    progressDialog.setMessage("please wait, while we are updating your account information");
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.show();
    if (imageUri!=null){
        final StorageReference fileRef=storageProfilePrictureRef
                .child(prevalent.currentOnlineUser.getPhone()+".jpg");
    uploadTast=fileRef.putFile(imageUri);
    uploadTast.continueWithTask(new Continuation() {
        @Override
        public Object then(@NonNull Task task) throws Exception {
            if (!task.isSuccessful()){
                throw  task.getException();
            }
            return fileRef.getDownloadUrl();
        }
    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
        @Override
        public void onComplete(@NonNull Task<Uri> task) {
            if (task.isSuccessful()){
                Uri downloadUrl=task.getResult();
                myUrl=downloadUrl.toString();
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
                HashMap<String,Object> userMap=new HashMap<>();
                userMap.put("name",fullNameEditText.getText().toString());
                userMap.put("address",addressEditText.getText().toString());
                userMap.put("phoneOrder",userPhoneEditText.getText().toString());
                userMap.put("image",myUrl);
                ref.child(prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
                progressDialog.dismiss();
                Intent intent=new Intent(SettingsActivity.this,HomeActivity.class);
                intent.putExtra("Admin","Admin");
                startActivity(intent);
                Toast.makeText(SettingsActivity.this,"Profile info updated",Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                progressDialog.dismiss();
                Toast.makeText(SettingsActivity.this,"Error",Toast.LENGTH_SHORT).show();

            }
        }
    });
    }
    else {
        Toast.makeText(SettingsActivity.this,"image is not Selected",Toast.LENGTH_SHORT).show();;
    }
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText) {
        DatabaseReference userref= FirebaseDatabase.getInstance().getReference().child(parentdb).child(prevalent.currentOnlineUser.getPhone());
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()){
                    if (snapshot.child("image").getValue()!=null){
                        String image=snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(profileImageView);
                    }

                    String name=snapshot.child("name").getValue().toString();
                    String phone=snapshot.child("phone").getValue().toString();
                    String address=snapshot.child("address").getValue().toString();

                    fullNameEditText.setText(name);
                    userPhoneEditText.setText(phone);
                    addressEditText.setText(address);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}