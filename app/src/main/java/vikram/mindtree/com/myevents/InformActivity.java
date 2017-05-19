package vikram.mindtree.com.myevents;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

public class InformActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver{
    private static final String TAG = "InformActivity";
    private EditText mEditTextComment;
    private Button mButtonNotify;
    private ProgressBar mProgress;
    private DownloadResultReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditTextComment = (EditText)findViewById(R.id.edit_comment);
        mButtonNotify = (Button)findViewById(R.id.btn_notify);
        mProgress = (ProgressBar) findViewById(R.id.progressBar3);

        mButtonNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditTextComment.getText().toString() != null && !mEditTextComment.getText().toString().equals("")) {
                    mReceiver = new DownloadResultReceiver(new Handler());
                    mReceiver.setReceiver(InformActivity.this);
                    Intent intent = new Intent(Intent.ACTION_SYNC, null, InformActivity.this, NetworkIntentService.class);
                    intent.putExtra("url", Constants.INFORM_EVENT);
                    intent.putExtra("method", Constants.POST_METHOD);
                    //Post body
                    intent.putExtra("comment", mEditTextComment.getText().toString());
                    intent.putExtra("receiver", mReceiver);
                    intent.putExtra("from", "inform-event");

                    startService(intent);
                } else {
                    Toast.makeText(InformActivity.this, R.string.comment_required, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case NetworkIntentService.STATUS_RUNNING:
                mProgress.setVisibility(View.VISIBLE);
                break;
            case NetworkIntentService.STATUS_FINISHED:
                Log.d(TAG,resultData.getString("result"));
                String response = resultData.getString("result");
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("statusMessage").equals("success")) {
                        mProgress.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        mProgress.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mProgress.setVisibility(View.INVISIBLE);
                }

                break;
            case NetworkIntentService.STATUS_ERROR:
                mProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(this, R.string.error_add_team, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
