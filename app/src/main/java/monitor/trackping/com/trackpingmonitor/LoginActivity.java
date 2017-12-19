package monitor.trackping.com.trackpingmonitor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;

    @BindView(R.id.etEmail)
    EditText editTextEmail;
    @BindView(R.id.etPassword)
    EditText editTextPassword;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //getting FireBase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the objects getCurrentUser method is not null
        //means user is already logged in
        if(firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, SelectDeviceActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.btn_login)
    public void onSignIn(View view) {

        final String email = editTextEmail.getText().toString().trim();
        final String password  = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //authenticate user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                editTextPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Successful!, Start tracking!", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(LoginActivity.this, SelectDeviceActivity.class);
                            startActivity(intent);
                        }
                    }
                }).addOnFailureListener(LoginActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
            }
        });

    }



}
