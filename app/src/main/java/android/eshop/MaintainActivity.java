package android.eshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MaintainActivity extends AppCompatActivity {
private Button applyChangeBtn,deleteBtn;
private EditText name, price,description;
private ImageView imageView;


    private String productID;
    private DatabaseReference productsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain);
    applyChangeBtn=findViewById(R.id.apply_changes_btn);
    name=findViewById(R.id.product_name_maintain);
    deleteBtn=findViewById(R.id.delete_btn);
        price=findViewById(R.id.product_priceitem_maintain);
        description=findViewById(R.id.product_descriptionitem_maintain);
    imageView=findViewById(R.id.product_image_maintain);

        productID=getIntent().getStringExtra("pid");
        productsRef= FirebaseDatabase.getInstance().getReference().child("Products").child(productID);
    displaySpecificProductInfo();
    applyChangeBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            applyChanges();
        }
    });
    deleteBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            deleteThisProduct();
        }
    });
    }

    private void deleteThisProduct() {
    productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            startActivity(new Intent(MaintainActivity.this,AdminCatergoryActivity.class));
            finish();
            Toast.makeText(MaintainActivity.this,"The Product deleted Successfully",Toast.LENGTH_SHORT).show();

        }
    });
    }

    private void applyChanges() {
    String pName=name.getText().toString();
        String pPrice=price.getText().toString();
        String pDescription=description.getText().toString();
        if (pName.equals("")){
            Toast.makeText(this,"Write down name",Toast.LENGTH_SHORT);
        }
        else if (pPrice.equals("")){
            Toast.makeText(this,"Write down price",Toast.LENGTH_SHORT);
        }
       else if (pDescription.equals("")){
            Toast.makeText(this,"Write down description",Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String,Object> productmap=new HashMap<>();
            productmap.put("pid",productID);
            productmap.put("description",pDescription);
            productmap.put("price",pPrice);
            productmap.put("pname",pName);
            productsRef.updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(MaintainActivity.this,"Changes applied successfully",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MaintainActivity.this,AdminCatergoryActivity.class));
                        finish();
                    }
                }
            });
        }

    }

    private void displaySpecificProductInfo() {
    productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                String pName = snapshot.child("pname").getValue().toString();
                String pPrice = snapshot.child("price").getValue().toString();
                String pDescription = snapshot.child("description").getValue().toString();
                String pImage = snapshot.child("image").getValue().toString();
                name.setText(pName);
                price.setText(pPrice);
                description.setText(pDescription);
                Picasso.get().load(pImage).into(imageView);
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
    }

}