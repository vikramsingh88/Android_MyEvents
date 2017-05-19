package vikram.mindtree.com.myevents;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AddTeamActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver {

    private EditText mEditTextTeamName;
    private Button mButtonAddTeam;
    private ProgressBar mProgress;
    private DownloadResultReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditTextTeamName = (EditText)findViewById(R.id.edit_team_name);
        mButtonAddTeam = (Button)findViewById(R.id.btn_add_team);
        mProgress = (ProgressBar) findViewById(R.id.progressBar3);

        mButtonAddTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditTextTeamName.getText().toString() != null && !mEditTextTeamName.getText().toString().equals("")) {
                    mReceiver = new DownloadResultReceiver(new Handler());
                    mReceiver.setReceiver(AddTeamActivity.this);
                    Intent intent = new Intent(Intent.ACTION_SYNC, null, AddTeamActivity.this, NetworkIntentService.class);
                    intent.putExtra("url", Constants.TEAMS);
                    intent.putExtra("method", Constants.POST_METHOD);
                    //Post body
                    //Team name
                    intent.putExtra("teamName", mEditTextTeamName.getText().toString());
                    intent.putExtra("receiver", mReceiver);
                    intent.putExtra("from", "add-team");

                    startService(intent);
                } else {
                    Toast.makeText(AddTeamActivity.this, R.string.team_name_required, Toast.LENGTH_SHORT).show();
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
                mProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(this, R.string.success_add_team, Toast.LENGTH_SHORT).show();
                setResult(Main2Activity.REQUEST_ADD_TEAM);
                finish();
                break;
            case NetworkIntentService.STATUS_ERROR:
                mProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(this, R.string.error_add_team, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
