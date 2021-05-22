package android.eshop;

import android.content.Intent;
import android.eshop.Model.Products;
import android.eshop.ViewHolder.productviewholder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

private DatabaseReference productref;
private RecyclerView recyclerView;
private String type="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if (bundle!=null)
            type=intent.getStringExtra("Admin");
Log.e("home","create"+type);
        productref= FirebaseDatabase.getInstance().getReference().child("Products");
        Paper.init(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toogle=new ActionBarDrawerToggle(
                this,drawer,toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toogle);
        toogle.syncState();
        NavigationView navigationView = findViewById(R.id. nav_view);
       navigationView.setNavigationItemSelectedListener(this);
    View headerview=navigationView.getHeaderView(0);
        TextView userNameTextview=headerview.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView=headerview.findViewById(R.id.user_profile_image);
        userNameTextview.setText(prevalent.currentOnlineUser.getName());
        Picasso.get().load(prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);
        recyclerView=findViewById(R.id.recycler_menu);
recyclerView.setHasFixedSize(true);
recyclerView.setLayoutManager(new LinearLayoutManager(this));
     }


    @Override
    protected void onStart() {
     Log.e("home","in on start");
        super.onStart();
        FirebaseRecyclerOptions<Products> options=
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(productref,Products.class)
                .build();
        FirebaseRecyclerAdapter<Products, productviewholder> adapter=
                new FirebaseRecyclerAdapter<Products, productviewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull productviewholder holder, int position, @NonNull final Products model) {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtproductprice.setText("Price= "+model.getPrice() + "$");
                        Picasso.get().load(model.getImage()).into(holder.imageView);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (type.equals("Admin")){
                                    Intent intent=new Intent(HomeActivity.this,MaintainActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);
                                }
                                else {
                                Intent intent=new Intent(HomeActivity.this,ProductDetailsActivity.class);
                                intent.putExtra("pid",model.getPid());
                                 startActivity(intent);}
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public productviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                        productviewholder holder=new productviewholder(view);
                        return holder;
                    }
                };
 recyclerView.setAdapter(adapter);
 adapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.e("d","ddddddddddd");
        int id=item.getItemId();
        if (id==R.id.nav_cart){
            Intent intent=new Intent(HomeActivity.this,CartActivity.class);
            startActivity(intent);
        }
        else if (id==R.id.nav_search){
        startActivity(new Intent(HomeActivity.this,SearchProductsActivity.class));
        }
        else if (id==R.id.nav_settings){
            Intent intent=new Intent(this,SettingsActivity.class);
            intent.putExtra("type",type);
            startActivity(intent);
            finish();
        }
        else if (id==R.id.nav_logout){
            Paper.book().destroy();
            Intent intent=new Intent(HomeActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }



        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
Log.e("home","stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    Log.e("home","destroy");
    }

}