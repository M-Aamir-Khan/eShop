package android.eshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.eshop.Model.Cart;
import android.eshop.ViewHolder.CartViewHolder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class CartActivity extends AppCompatActivity {
private RecyclerView recyclerView;
private RecyclerView.LayoutManager layoutManager;
private Button NextProcessbtn;
private TextView txtTotalAmount,txtMsg1;
private int overTotalPrice=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView=findViewById(R.id.cart_List);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        NextProcessbtn=findViewById(R.id.next_process_btn);
        txtTotalAmount=findViewById(R.id.total_price);
        txtMsg1=findViewById(R.id.msg1);
    NextProcessbtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            txtTotalAmount.setText("Total Price = $"+String.valueOf(overTotalPrice));
            Intent intent=new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
            intent.putExtra("Total Price",String.valueOf(overTotalPrice));
            startActivity(intent);
        }
    });
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckorderState();
    final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference()
            .child("Cart List")
            .child("User View")
            .child(prevalent.currentOnlineUser.getPhone())
            .child("Products");
        FirebaseRecyclerOptions<Cart> options=new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef,Cart.class).build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter=
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                        holder.txtProductQuantity.setText(model.getQuantity());
                        holder.txtProductPrice.setText(model.getPrice());
                        holder.txtProductName.setText(model.getPname());
                    int oneTypeProductTPrice=((Integer.valueOf(model.getPrice())))* Integer.valueOf(model.getQuantity());
                        overTotalPrice=overTotalPrice+oneTypeProductTPrice;

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CharSequence options[]=new CharSequence[]{
                              "Edit",
                              "Remove"
                            };
                            AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                            builder.setTitle("Cart Options");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i==0){
                                        Intent intent=new Intent(CartActivity.this,ProductDetailsActivity.class);
                                        intent.putExtra("pid",model.getPid());
                                        startActivity(intent);
                                    }
                                    else {
                                        cartListRef.child("User View")
                                                .child(prevalent.currentOnlineUser.getPhone())
                                                .child("Products")
                                                .child(model.getPid())
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(CartActivity.this, "Remove Successfully", Toast.LENGTH_SHORT);
                                                        Intent intent=new Intent(CartActivity.this,HomeActivity.class);
                                                        startActivity(intent);
                                                        }
                                                        }
                                                });
                                    }
                                }
                            });

                            builder.show();
                        }
                    });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View   view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                        CartViewHolder holder=new CartViewHolder(view);
                        return holder;
                    }
                };
    recyclerView.setAdapter(adapter);
    adapter.startListening();

    }
    private void CheckorderState(){
        DatabaseReference orderRef;
        orderRef=FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String shippingState=snapshot.child("state").getValue().toString();
                    String userName=snapshot.child("name").getValue().toString();
                    if (shippingState.equals("shipped")){
                        txtTotalAmount.setText("Dear"+ userName + "\n order is shipped successfullly");
                    recyclerView.setVisibility(View.GONE);
                    txtMsg1.setVisibility(View.VISIBLE);
                    NextProcessbtn.setVisibility(View.INVISIBLE);
                    Toast.makeText(CartActivity.this,"you can purchase more product once you recieve your first order",Toast.LENGTH_SHORT).show();

                    }
                    else if (shippingState.equals("not shipped")){
                        txtTotalAmount.setText("Shipping state=not shipped");
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        NextProcessbtn.setVisibility(View.INVISIBLE);
                        Toast.makeText(CartActivity.this,"you can purchase more product once you recieve your first order",Toast.LENGTH_LONG).show();

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}