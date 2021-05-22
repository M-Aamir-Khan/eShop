package android.eshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {
private String check="";
private TextView pageTitle,titleQuestions;
private EditText phoneNumber,question1,question2;
private Button verifyButton;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
    check=getIntent().getStringExtra("check");
titleQuestions=findViewById(R.id.title_question);
    pageTitle=findViewById(R.id.page_title);
    phoneNumber=findViewById(R.id.find_phone_number);
    question1=findViewById(R.id.question_1);
    question2=findViewById(R.id.question_2);
    verifyButton=findViewById(R.id.verify_btn);
    phoneNumber.setVisibility(View.GONE);

    }

    @Override
    protected void onStart() {
        super.onStart();
   if (check.equals("settings")){
       displayPreviousAnswers();
pageTitle.setText("Set Question");
titleQuestions.setText("Set the Answer");
verifyButton.setText("Set");
verifyButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
setAnswers();

    }
});
   }
   else if (check.equals("login")){
       phoneNumber.setVisibility(View.VISIBLE);
   }
    }

    void setAnswers(){

    String answer1=question1.getText().toString().toLowerCase();
    String answer2=question2.getText().toString().toLowerCase();
    if (question1.equals("")&& question2.equals("")){
        Toast.makeText(ResetPasswordActivity.this,"Please Answer both question",Toast.LENGTH_SHORT).show();
    }
    else {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(prevalent.currentOnlineUser.getPhone());
        HashMap<String,Object> userdataMap=new HashMap<>();
        userdataMap.put("answer1",answer1);
        userdataMap.put("answer2",answer2);
        ref.child("Security Questions").updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ResetPasswordActivity.this,"Answer update Successfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ResetPasswordActivity.this,HomeActivity.class));
                    finish();
                }
            }
        });
    }
}
private void displayPreviousAnswers(){
    DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users")
            .child(prevalent.currentOnlineUser.getPhone());
    ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
        String ans1=snapshot.child("answer1").getValue().toString();
            String ans2=snapshot.child("answer2").getValue().toString();
            question1.setText(ans1);
            question2.setText(ans2);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
}
}