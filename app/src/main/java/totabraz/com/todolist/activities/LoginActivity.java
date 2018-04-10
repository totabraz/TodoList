package totabraz.com.todolist.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.security.PrivateKey;

import totabraz.com.todolist.R;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "Firebase";
    private FirebaseAuth mAuth;
    private TextInputEditText edEmail = findViewById(R.id.edtEmail);
    private TextInputEditText edPasswd = findViewById(R.id.edtPasswd);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.mAuth = FirebaseAuth.getInstance();

        this.edPasswd.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            login();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        Button btnLogin = findViewById(R.id.btnEnter);
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            login();
            }
        });
    }
    private void login(){
        String mail = edEmail.getText().toString();
        String pswd = edPasswd.getText().toString();
        setupFirebaseUser(mail, pswd);
    }
    private void goToTodos(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void setupFirebaseUser(String mail, String pswd) {
        final boolean[] userNotExist = {true};
        this.mAuth.signInWithEmailAndPassword(mail, pswd)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    userNotExist[0] = false;
                    goToTodos();
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                }
                }
            });

        if (userNotExist[0]) {
            createUSer(mail, pswd);
        }
    }

    private void createUSer(String mail, String pswd) {
        this.mAuth.createUserWithEmailAndPassword(mail, pswd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    goToTodos();

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                }
            }

        });
    }
}
