package vikram.mindtree.com.myevents;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver{

    private static Button mButtonTime;
    private static Button mButtonDate;
    private Button mButtonSubmit;
    private EditText mEditTextName;
    private EditText mEditTextAbout;
    private EditText mEditTextRules;
    private EditText mEditTextVenue;
    private EditText mEditTextPoints;

    private static String mTime = null;
    private static String mDate = null;

    private ProgressBar mProgress;
    private DownloadResultReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mButtonTime = (Button) findViewById(R.id.btn_time);
        mButtonDate = (Button) findViewById(R.id.btn_date);
        mButtonSubmit = (Button) findViewById(R.id.button);

        mProgress = (ProgressBar) findViewById(R.id.progressBar);

        mEditTextName = (EditText) findViewById(R.id.edit_event_name);
        mEditTextAbout = (EditText) findViewById(R.id.edit_event_desc);
        mEditTextRules = (EditText) findViewById(R.id.edit_event_rule);
        mEditTextVenue = (EditText) findViewById(R.id.edit_event_venue);
        mEditTextPoints = (EditText) findViewById(R.id.edit_event_points);

        mButtonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        mButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEditTextName.getText().toString();
                String about = mEditTextAbout.getText().toString();
                String rules = mEditTextRules.getText().toString();
                String venue = mEditTextVenue.getText().toString();
                String points = mEditTextPoints.getText().toString();

                if (mTime != null &&
                        mDate != null &&
                        (name != null && !name.equals("")) &&
                        (about != null && !about.equals("")) &&
                        (rules != null && !rules.equals("")) &&
                        (venue != null && !venue.equals("")) &&
                        (points != null && !points.equals(""))) {

                    mReceiver = new DownloadResultReceiver(new Handler());
                    mReceiver.setReceiver(AddEventActivity.this);
                    Intent intent = new Intent(Intent.ACTION_SYNC, null, AddEventActivity.this, NetworkIntentService.class);
                    intent.putExtra("url", Constants.Event);
                    intent.putExtra("method", Constants.POST_METHOD);
                    //Post body
                    //Event data
                    intent.putExtra("eventName", name);
                    intent.putExtra("eventDescription", about);
                    intent.putExtra("eventRule", rules);
                    intent.putExtra("eventVenue", venue);
                    intent.putExtra("eventPoints",points);
                    intent.putExtra("eventTime", mTime);
                    intent.putExtra("eventDate", mDate);
                    intent.putExtra("receiver", mReceiver);
                    intent.putExtra("from", "add-event");

                    startService(intent);
                } else {
                    Toast.makeText(AddEventActivity.this, R.string.add_data_required, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case NetworkIntentService.STATUS_RUNNING:
                mProgress.setVisibility(View.VISIBLE);
                break;
            case NetworkIntentService.STATUS_FINISHED:
                mProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(this, R.string.event_success, Toast.LENGTH_SHORT).show();
                setResult(Main2Activity.REQUEST_CREATE_EVENT);
                finish();
                break;
            case NetworkIntentService.STATUS_ERROR:
                mProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(this, R.string.error_event, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mButtonTime.setText(hourOfDay+"."+minute);
            mTime = hourOfDay+"."+minute;
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            mButtonDate.setText(day+"/"+month+"/"+year);
            mDate = day+"/"+month+"/"+year;
        }
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
        setResult(RESULT_CANCELED);
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }
}
