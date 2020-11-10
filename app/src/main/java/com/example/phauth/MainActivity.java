package com.example.phauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    EditText phone,otp;
    Button getOtp,Submit;
    String VerificationId;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth=FirebaseAuth.getInstance();
        phone=(EditText)this.findViewById(R.id.phone_no);
        otp=(EditText)this.findViewById(R.id.phone_otp);
        getOtp=(Button) this.findViewById(R.id.get);
        Submit=(Button) this.findViewById(R.id.submit);
        getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText().toString().equals("")||phone.getText().toString().equals(null)||phone.getText().toString().contains("+95")){
                    Toast.makeText(getBaseContext(),R.string.error_msg,Toast.LENGTH_SHORT).show();
                }
                else {
                        sendOTP(phone.getText().toString().trim());
                  }
            }
        });
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp.getText().toString().equals("")||otp.getText().toString().equals(null)){
                    Toast.makeText(getBaseContext(),R.string.error_msg,Toast.LENGTH_SHORT).show();
                }else
                {
                    VerifyOTP(otp.getText().toString().trim());
                }
            }
        });


    }

    private void VerifyOTP(String otp_code) {
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(VerificationId,otp_code);
        if(credential!=null){
        signCredential(credential);
        }else {
            Toast.makeText(getBaseContext(),R.string.error_msg,Toast.LENGTH_SHORT).show();
        }
    }

    private void signCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                   // Toast.makeText(getBaseContext(),"Success!",Toast.LENGTH_SHORT).show();

                    Intent home=new Intent(MainActivity.this,Home.class);
                    startActivity(home);
                }else {
                    Toast.makeText(getBaseContext(),R.string.error_msg+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendOTP(String ph) {
        PhoneAuthOptions phoneAuthOptions= PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber("+95"+ph).setActivity(this).setTimeout(120l,TimeUnit.SECONDS).setCallbacks(mcallBack).build();
       PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
       // PhoneAuthProvider.verifyPhoneNumber(PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber("+95"+ph).setActivity(this).setTimeout(120l,TimeUnit.SECONDS).setCallbacks(mcallBack).build());
    //   Toast.makeText(getBaseContext(),phoneAuthOptions.toString(),Toast.LENGTH_SHORT).show();

        //PhoneAuthProvider.getInstance().verifyPhoneNumber("+95"+ph,60, TimeUnit.SECONDS,this,mcallBack);
    }
    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallBack=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            Log.i("HIHI",s+forceResendingToken);
            super.onCodeSent(s, forceResendingToken);
            VerificationId=s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            if (phoneAuthCredential!=null){
                VerifyOTP(phoneAuthCredential.getSmsCode());
            }else {
                Toast.makeText(getBaseContext(),"PhoneAuthCredential is null",Toast.LENGTH_SHORT).show();
            }
          //  VerifyOTP(phoneAuthCredential.getSmsCode());
           // Toast.makeText(getBaseContext(),R.string.error_msg,Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getBaseContext(),R.string.error_msg+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    };
}