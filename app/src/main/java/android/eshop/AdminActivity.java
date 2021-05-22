package android.eshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.SimpleTimeZone;

public class AdminActivity extends AppCompatActivity {
private String categoryname;
Button addproduct;ImageView productimage;
EditText productname,productdescription,productprice;
DatabaseReference productref;
private Uri imageuri;
    private String description,price,pname;
    private String savecurrentdate;
    private String savecurrenttime;
private StorageReference productimgref;
    private String productkey;
     String downloadimguri;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        loadingbar=new ProgressDialog(this);
        productref=FirebaseDatabase.getInstance().getReference().child("Products");
        productimgref= FirebaseStorage.getInstance().getReference().child("prodimg");
        categoryname=getIntent().getExtras().getString("category");
        Toast.makeText(this,categoryname,Toast.LENGTH_SHORT);
        addproduct=findViewById(R.id.add_new_product);
        productimage=findViewById(R.id.select_product_image);
        productname=findViewById(R.id.product_name);
        productdescription=findViewById(R.id.product_description);
        productprice=findViewById(R.id.product_price);
  productimage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          opengallery();
      }
  });
  addproduct.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          validateproductdata();
      }
  });
    }

    private void validateproductdata() {
    description=productdescription.getText().toString();
    price=productprice.getText().toString();
    pname=productname.getText().toString();
     if (imageuri==null){
Toast.makeText(this,"porduct image is mandatory",Toast.LENGTH_SHORT);
     }
     else if (TextUtils.isEmpty(description)){
         Toast.makeText(this,"please write description",Toast.LENGTH_SHORT);
     }
     else if (TextUtils.isEmpty(price)){
         Toast.makeText(this,"please include price",Toast.LENGTH_SHORT);
     }
     else {
         storeproductinfo();
     }

    }

    private void storeproductinfo() {
        loadingbar.setTitle("Storing Product");
        loadingbar.setMessage("Please wait, while we are storing your product");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat("MMM dd,yyyy");
        savecurrentdate=currentdate.format(calendar.getTime());
        SimpleDateFormat currenttime=new SimpleDateFormat("HH:mm:ss");
        savecurrenttime=currenttime.format(calendar.getTime());
        productkey=savecurrentdate+savecurrenttime;
        final StorageReference filepath=productimgref.child(imageuri.getLastPathSegment()+productkey);
    final UploadTask uploadTask=filepath.putFile(imageuri);
    uploadTask.addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            loadingbar.dismiss();
            Toast.makeText(AdminActivity.this,e.toString(),Toast.LENGTH_SHORT);
        }
    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            loadingbar.dismiss();
            Toast.makeText(AdminActivity.this,"product store successfully",Toast.LENGTH_SHORT);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful())
                        throw task.getException();

                    return filepath.getDownloadUrl() ;
                   }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){downloadimguri=task.getResult().toString();
                    savedatatodb();}
                }
            });
        }
    });
    }

    private void savedatatodb() {
        HashMap<String,Object> productmap=new HashMap<>();
        productmap.put("pid",productkey);
        productmap.put("date",savecurrentdate);
        productmap.put("time",savecurrenttime);
        productmap.put("description",description);
        productmap.put("image",downloadimguri);
        productmap.put("category",categoryname);
        productmap.put("price",price);
        productmap.put("pname",pname);
productref.child(productkey).updateChildren(productmap)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    loadingbar.dismiss();
                    Toast.makeText(AdminActivity.this,"product upload successfully",Toast.LENGTH_SHORT);
                startActivity(new Intent(AdminActivity.this,AdminCatergoryActivity.class));
                }

                else {   loadingbar.dismiss();
                    Toast.makeText(AdminActivity.this, task.getException().toString(), Toast.LENGTH_SHORT);
                }
                }
        });

    }

    private void opengallery() {
        Intent galleryintent=new Intent();
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
         startActivityForResult(galleryintent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null){
            imageuri=data.getData();
            Log.e("uri", String.valueOf(imageuri));
            productimage.setImageURI(imageuri);

        }
    }
}