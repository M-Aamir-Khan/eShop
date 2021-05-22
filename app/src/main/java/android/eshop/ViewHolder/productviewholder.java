package android.eshop.ViewHolder;

import android.eshop.Interface.ItemClickListner;
import android.eshop.R;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class productviewholder extends RecyclerView.ViewHolder{
    public TextView txtProductName,txtProductDescription,txtproductprice;
    public ImageView imageView;
    public productviewholder(View itemView){
        super(itemView);
        imageView=itemView.findViewById(R.id.product_image);
        txtProductName=itemView.findViewById(R.id.product_name);
        txtProductDescription=itemView.findViewById(R.id.product_descriptionitem);
        txtproductprice=itemView.findViewById(R.id.product_priceitem); 
    }

    }
