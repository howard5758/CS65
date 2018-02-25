package cs65.edu.dartmouth.cs.gifto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Oliver on 2/24/2018.
 *
 * Standard boilerplate for signing up
 */

public class SignUpActivity extends AppCompatActivity implements Button.OnClickListener{
    private EditText passwordEditText;
    private EditText emailEditText;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();

        // get necessary views
        passwordEditText = findViewById(R.id.passwordField);
        emailEditText = findViewById(R.id.emailField);
        Button signUpButton = findViewById(R.id.signupButton);
        signUpButton.setOnClickListener(this);
    }

    // attempt to sign them up using AuthOnCompleteListener, Firebase handles the rest
    public void onClick(View view){
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty())
            Util.showDialog(this,
                    "Please make sure you enter an email address and password");
        else{
            Task<AuthResult> task = firebaseAuth.createUserWithEmailAndPassword(email, password);
            task.addOnCompleteListener(new AuthOnCompleteListener(this));
        }
    }

}
