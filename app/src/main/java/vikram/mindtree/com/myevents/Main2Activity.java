package vikram.mindtree.com.myevents;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import vikram.mindtree.com.myevents.teaser.TeaserActivity;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DownloadResultReceiver.Receiver {

    RecyclerView mRecyclerView;
    RecyclerView mRecyclerViewEvents;
    RecyclerView.Adapter mAdapter;
    EventListAdapter mEventAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private CoordinatorLayout mCoordinatorLayout;

    private DownloadResultReceiver mReceiver;
    private String TAG = "Main2Activity";
    private BottomSheetBehavior mBottomSheetBehavior;
    private ImageButton mButtonCreateTeam;
    private ImageButton mButtonCreateEvent;
    private ImageButton mButtonSendPrimer;
    private ImageButton mButtonInform;

    public static final int REQUEST_CREATE_EVENT = 101;
    public static final int REQUEST_ADD_TEAM = 102;
    public static final int REQUEST_NOTIFIED = 103;

    private boolean isAdmin;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intentLogin = getIntent();
        isAdmin = intentLogin.getBooleanExtra("admin", false);

        if (savedInstanceState != null){
            isAdmin = savedInstanceState.getBoolean("admin");
        }

        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator_layout);

        mButtonCreateTeam = (ImageButton) findViewById(R.id.btn_create_team);
        mButtonCreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddTeamActivity = new Intent(Main2Activity.this, AddTeamActivity.class);
                startActivityForResult(intentAddTeamActivity, REQUEST_ADD_TEAM);
            }
        });

        mButtonCreateEvent = (ImageButton) findViewById(R.id.btn_create_event);
        mButtonCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCreateEventActivity = new Intent(Main2Activity.this, AddEventActivity.class);
                startActivityForResult(intentCreateEventActivity, REQUEST_CREATE_EVENT);
            }
        });

        mButtonSendPrimer = (ImageButton) findViewById(R.id.btn_send_primer);
        mButtonSendPrimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentComingSoon = new Intent(Main2Activity.this, TeaserActivity.class);
                startActivity(intentComingSoon);
            }
        });

        mButtonInform = (ImageButton) findViewById(R.id.btn_inform);
        mButtonInform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentInform = new Intent(Main2Activity.this, InformActivity.class);
                startActivity(intentInform);
            }
        });

        View bottomSheet = findViewById( R.id.bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        //As per login enable/disable view as applicable
        if (isAdmin) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        //Team list
        getTeamList();

        //EventList
        mRecyclerViewEvents = (RecyclerView) findViewById(R.id.recycler_view_events);
        mRecyclerView.setHasFixedSize(true);
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        getEventList();
    }

    private void getTeamList() {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, NetworkIntentService.class);
        intent.putExtra("url", Constants.TEAMS);
        intent.putExtra("method", Constants.GET_METHOD);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("from", "teams");
        startService(intent);
    }

    private void getEventList() {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, NetworkIntentService.class);
        intent.putExtra("url", Constants.Event);
        intent.putExtra("method", Constants.GET_METHOD);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("from", "get-events");
        intent.putExtra("isAdmin", isAdmin);
        startService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CREATE_EVENT) {
            getEventList();
        } else if (requestCode == REQUEST_ADD_TEAM) {
            getTeamList();
        } else if (requestCode == REQUEST_NOTIFIED) {
            getEventList();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean("admin", isAdmin);
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
                        ArrayList<TeamDetails> listTeam = new ArrayList<>();
                        JSONArray teams = json.getJSONArray("teams");
                        for(int i=0; i < teams.length(); i++){
                            JSONObject jsonObject = teams.getJSONObject(i);
                            String teamName = jsonObject.getString("teamName");
                            listTeam.add(new TeamDetails(teamName,null,null,null,null));
                        }
                        mAdapter = new TeamListAdapter(listTeam, new TeamListAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(TeamDetails item) {
                                Intent detailsIntent = new Intent(Main2Activity.this, TeamDetailsActivity.class);
                                detailsIntent.putExtra("teamName", item.getTeamName());
                                detailsIntent.putExtra("admin", isAdmin);
                                startActivity(detailsIntent);
                            }
                        });
                        mRecyclerView.setAdapter(mAdapter);
                        mLayoutManager = new LinearLayoutManager(this);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
                    } else if (json.has("events")) {
                        ArrayList<EventDetail> listEvent = new ArrayList<>();
                        JSONArray events = json.getJSONArray("events");
                        for(int i=0; i < events.length(); i++){
                            JSONObject jsonObject = events.getJSONObject(i);
                            String eventName = jsonObject.getString("eventName");
                            String eventDescription = jsonObject.getString("description");
                            String eventRules = jsonObject.getString("rules");
                            String eventVenue = jsonObject.getString("venue");
                            String eventPoints = jsonObject.getString("maxPoints");
                            String eventTime = jsonObject.getString("eventTime");
                            String eventDate = jsonObject.getString("eventDate");
                            boolean isNotified = jsonObject.getBoolean("isNotified");
                            listEvent.add(new EventDetail(eventName, eventDescription, eventRules, eventVenue, eventPoints,
                                    eventTime, eventDate, isNotified));
                        }
                        mEventAdapter = new EventListAdapter(isAdmin, listEvent,Main2Activity.this, new EventListAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(EventDetail item) {
                                Intent detailsIntent = new Intent(Main2Activity.this, EventDetailsActivity.class);
                                detailsIntent.putExtra("name", item.getEventName());
                                detailsIntent.putExtra("about", item.getAboutEvent());
                                detailsIntent.putExtra("rule", item.getEventRules());
                                detailsIntent.putExtra("venue", item.getEventVenue());
                                detailsIntent.putExtra("time", item.getEventTime());
                                detailsIntent.putExtra("date", item.getEventDate());
                                detailsIntent.putExtra("points", item.getEventPoints());
                                detailsIntent.putExtra("admin", isAdmin);

                                startActivityForResult(detailsIntent, REQUEST_NOTIFIED);
                            }
                        });
                        mRecyclerViewEvents.setAdapter(mEventAdapter);
                        mLayoutManager = new LinearLayoutManager(this);
                        mRecyclerViewEvents.setLayoutManager(mLayoutManager);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case NetworkIntentService.STATUS_ERROR:

                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Please click BACK again to exit", Snackbar.LENGTH_LONG);
            snackbar.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main2, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mEventAdapter != null) {
                    mEventAdapter.filter(newText);
                }

                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intentScoreBoard = new Intent(this, ScoreBoardActivity.class);
            startActivity(intentScoreBoard);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
