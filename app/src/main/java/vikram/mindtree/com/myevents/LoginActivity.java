package vikram.mindtree.com.myevents;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver{
    private EditText mEditTextPassword;
    private TextView mTextViewLoginParticipant;
    private DownloadResultReceiver mReceiver;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEditTextPassword = (EditText) findViewById(R.id.password_field);
        mTextViewLoginParticipant = (TextView) findViewById(R.id.txt_user_login);
        pd = new ProgressDialog(this);
        pd.setMessage("login...");

        //Login participant
        mTextViewLoginParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                intent.putExtra("admin", false);
                startActivity(intent);
                finish();
            }
        });

        //Login Admin
        mEditTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    login(s.toString());
                }
            }
        });
    }

    private void login(String password) {
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(LoginActivity.this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, LoginActivity.this, NetworkIntentService.class);
        intent.putExtra("url", Constants.LOGIN);
        intent.putExtra("method", Constants.POST_METHOD);
        //Post body
        intent.putExtra("password", password);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("from", "login");

        startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case NetworkIntentService.STATUS_RUNNING:
                pd.show();
                break;
            case NetworkIntentService.STATUS_FINISHED:
                pd.hide();
                if (resultData.getString("result").equals("success")) {
                    Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                    intent.putExtra("admin", true);
                    startActivity(intent);
                    finish();
                } else {
                    mEditTextPassword.setText("");
                    Toast.makeText(LoginActivity.this, R.string.password_error, Toast.LENGTH_SHORT).show();
                }
                break;
            case NetworkIntentService.STATUS_ERROR:
                pd.hide();
                mEditTextPassword.setText("");
                Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
