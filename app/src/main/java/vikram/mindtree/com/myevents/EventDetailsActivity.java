package vikram.mindtree.com.myevents;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventDetailsActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver {

    private static final String TAG = "EventDetailsActivity";
    private TextView txtName;
    private TextView txtAbout;
    private TextView txtRules;
    private TextView txtVenue;
    private TextView txtTime;
    private TextView txtDate;
    private TextView txtPoints;
    private Button mButtonNotify;
    private Spinner mSpinnerTeams;
    private Button mButtonScore;
    private EditText mEditTextPoints;
    private LinearLayout mLinearLayoutNotify;
    private LinearLayout mLinearLayoutLine;
    private LinearLayout mLinearLayoutAddScore;
    private LinearLayout mLinearLayoutSpinner;
    private LinearLayout mLinearLayoutEdit;
    private LinearLayout mLinearLayoutButton;

    private String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent detailsIntent =getIntent();
        final String name = detailsIntent.getStringExtra("name");
        eventName = name;
        final String about = detailsIntent.getStringExtra("about");
        String rule = detailsIntent.getStringExtra("rule");
        String venue = detailsIntent.getStringExtra("venue");
        final String time = detailsIntent.getStringExtra("time");
        final String date = detailsIntent.getStringExtra("date");
        String points = detailsIntent.getStringExtra("points");
        boolean isAdmin = detailsIntent.getBooleanExtra("admin", false);

        txtName = (TextView) findViewById(R.id.txt_name);
        txtAbout = (TextView) findViewById(R.id.txt_about);
        txtRules = (TextView) findViewById(R.id.txt_rule);
        txtVenue = (TextView) findViewById(R.id.txt_venue);
        txtTime = (TextView) findViewById(R.id.txt_time);
        txtDate = (TextView) findViewById(R.id.txt_date);
        txtPoints = (TextView) findViewById(R.id.txt_point);
        mButtonNotify = (Button) findViewById(R.id.btn_notify);
        mSpinnerTeams = (Spinner) findViewById(R.id.spinner_team);
        mButtonScore = (Button) findViewById(R.id.btn_add_score);
        mEditTextPoints = (EditText) findViewById(R.id.edit_score);

        mLinearLayoutNotify = (LinearLayout) findViewById(R.id.container_notify);
        mLinearLayoutLine = (LinearLayout) findViewById(R.id.container_line);
        mLinearLayoutAddScore = (LinearLayout) findViewById(R.id.container_add_score);
        mLinearLayoutSpinner = (LinearLayout) findViewById(R.id.container_spinner);
        mLinearLayoutEdit = (LinearLayout) findViewById(R.id.container_score);
        mLinearLayoutButton = (LinearLayout) findViewById(R.id.container_button);

        if (!isAdmin) {
            mLinearLayoutNotify.setVisibility(View.GONE);
            mLinearLayoutLine.setVisibility(View.GONE);
            mLinearLayoutAddScore.setVisibility(View.GONE);
            mLinearLayoutSpinner.setVisibility(View.GONE);
            mLinearLayoutEdit.setVisibility(View.GONE);
            mLinearLayoutButton.setVisibility(View.GONE);
        }

        txtName.setText(name);
        txtAbout.setText(about);
        txtRules.setText(rule);
        txtVenue.setText(venue);
        txtTime.setText(time);
        txtDate.setText(date);
        txtPoints.setText(points);

        mButtonNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyUser(name, about, time, date);
            }
        });

        //get teams
        getTeams();

        //Add score to team and event
        mButtonScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mEditTextPoints.getText().toString().equals("")) {
                    addScore(mSpinnerTeams.getSelectedItem().toString(), eventName, mEditTextPoints.getText().toString());
                } else {
                    Toast.makeText(EventDetailsActivity.this, R.string.points_field_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Add score to team and event
    private void addScore(String teamName, String eventName, String points) {
        DownloadResultReceiver mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(EventDetailsActivity.this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, EventDetailsActivity.this, NetworkIntentService.class);
        intent.putExtra("url", Constants.SCORE_BOARD);
        intent.putExtra("method", Constants.POST_METHOD);
        //Post body
        intent.putExtra("teamName", teamName);
        intent.putExtra("eventName", eventName);
        intent.putExtra("points", points);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("from", "add-score");

        startService(intent);
    }

    private void notifyUser(String eventName, String about, String time, String date) {
        DownloadResultReceiver mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(EventDetailsActivity.this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, EventDetailsActivity.this, NetworkIntentService.class);
        intent.putExtra("url", Constants.NOTIFY);
        intent.putExtra("method", Constants.POST_METHOD);
        //Post body
        //notify data - event name
        intent.putExtra("eventName", eventName);
        intent.putExtra("about", about);
        intent.putExtra("time", time);
        intent.putExtra("date", date);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("from", "notify-user");

        startService(intent);
    }

    private void getTeams() {
        DownloadResultReceiver mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(EventDetailsActivity.this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, NetworkIntentService.class);
        intent.putExtra("url", Constants.TEAMS);
        intent.putExtra("method", Constants.GET_METHOD);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("from", "teams");
        startService(intent);
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
                break;
            case NetworkIntentService.STATUS_FINISHED:
                Log.d(TAG,resultData.getString("result"));
                String response = resultData.getString("result");
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.has("teams")) {
                        ArrayList<String> listTeam = new ArrayList<>();
                        JSONArray teams = json.getJSONArray("teams");
                        for(int i=0; i < teams.length(); i++){
                            JSONObject jsonObject = teams.getJSONObject(i);
                            String teamName = jsonObject.getString("teamName");
                            listTeam.add(teamName);
                        }
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listTeam);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSpinnerTeams.setAdapter(dataAdapter);
                    } else if (json.has("score")) {
                        String message = json.getString("message");
                        Toast.makeText(this, "Score added", Toast.LENGTH_SHORT).show();
                    }
                    else if(json.has("notified") && json.getString("notified").equals("yes")){
                        setResult(Main2Activity.REQUEST_NOTIFIED);
                    }
                    if (json.has("statusMessage")) {
                        if (json.getString("statusMessage").equals("error")) {
                            Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case NetworkIntentService.STATUS_ERROR:
                Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
