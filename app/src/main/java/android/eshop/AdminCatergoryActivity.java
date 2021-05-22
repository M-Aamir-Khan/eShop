package android.eshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import io.paperdb.Paper;

public class AdminCatergoryActivity extends AppCompatActivity {
private ImageView tshirt,sportshirt;
private Button maintainProductsBtn,LogOutBtn, checkOrdersBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_catergory);
        maintainProductsBtn=findViewById(R.id.maintain_btn);
        tshirt=findViewById(R.id.t_shirts);
    LogOutBtn=findViewById(R.id.admin_logout_btn);
    checkOrdersBtn=findViewById(R.id.check_order_btn);
    sportshirt=findViewById(R.id.sports_t_shirts);
tshirt.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(AdminCatergoryActivity.this,AdminActivity.class);
        intent.putExtra("category","tshirts");
        startActivity(intent);
    }
});
sportshirt.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(AdminCatergoryActivity.this,AdminActivity.class);
        intent.putExtra("category","sportshirts");
        startActivity(intent);
    }
});
LogOutBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Paper.book().destroy();
        Intent intent=new Intent(AdminCatergoryActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
});
checkOrdersBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(AdminCatergoryActivity.this,AdminNewOrdersActivity.class);
        startActivity(intent);


    }
});
maintainProductsBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(AdminCatergoryActivity.this,HomeActivity.class);
        intent.putExtra("Admin","Admin");
        startActivity(intent);
    }
});
    }
}