package vikram.mindtree.com.myevents;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeamDetailsActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver {
    private static final String TAG = "TeamDetailsActivity";
    private BottomSheetBehavior mBottomSheetBehavior;
    private RecyclerView mTeamMemberRecyclerView;
    private TextView mTextViewLabel;
    private EditText mEditTextName;
    private EditText mEditTextAbout;
    private Button mButtonAddMember;
    private ProgressBar mProgress;
    private DownloadResultReceiver mReceiver;
    private AlertDialog.Builder alertDialog;
    private AlertDialog.Builder deleteAlertDialog;
    private EditText mDialogEditTextName;
    private EditText mDialogEditTextAbout;
    private View view;
    private int edit_position;

    MemberListAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    private Paint p = new Paint();

    List<TeamMemberDetails.Member> mMembers = new ArrayList<>();
    private boolean isAdmin;
    private String mMemberId;
    private String mMemberName;
    private String teamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details);

        View bottomSheet = findViewById(R.id.bottom_sheet_add_member);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        mTextViewLabel = (TextView) findViewById(R.id.textView3);
        mEditTextName = (EditText) findViewById(R.id.edit_member_name);
        mEditTextAbout = (EditText) findViewById(R.id.edit_about);
        mButtonAddMember = (Button) findViewById(R.id.btn_add_team_member);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mTeamMemberRecyclerView = (RecyclerView) findViewById(R.id.list_team_members);

        Intent intent = getIntent();
        teamName = intent.getStringExtra("teamName");
        isAdmin = intent.getBooleanExtra("admin", false);
        getTeamMemberList(teamName);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(teamName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mButtonAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mEditTextName.getText().toString() != null && !mEditTextName.getText().toString().equals("")) &&
                        (mEditTextAbout.getText().toString() != null && !mEditTextAbout.getText().toString().equals(""))) {
                    mReceiver = new DownloadResultReceiver(new Handler());
                    mReceiver.setReceiver(TeamDetailsActivity.this);
                    Intent intent = new Intent(Intent.ACTION_SYNC, null, TeamDetailsActivity.this, NetworkIntentService.class);
                    intent.putExtra("url", Constants.MEMBER.replace("teamName", teamName));
                    intent.putExtra("method", Constants.POST_METHOD);
                    //Post body
                    //Team member data
                    intent.putExtra("teamName", teamName);
                    intent.putExtra("memberName", mEditTextName.getText().toString());
                    intent.putExtra("about", mEditTextAbout.getText().toString());
                    intent.putExtra("receiver", mReceiver);
                    intent.putExtra("from", "add-member");

                    startService(intent);
                } else {
                    Toast.makeText(TeamDetailsActivity.this, R.string.team_name_required, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAdapter = new MemberListAdapter(mMembers, this);
        mTeamMemberRecyclerView.setAdapter(mAdapter);
        mTeamMemberRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(TeamDetailsActivity.this);
        mTeamMemberRecyclerView.setLayoutManager(mLayoutManager);
        mTeamMemberRecyclerView.addItemDecoration(new MemberListDividerItemDecorator(this));

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

        if (isAdmin) {
            fab.setVisibility(View.VISIBLE);
            initDialog();
            deleteDialog();
            initSwipe();
        } else {
            fab.setVisibility(View.GONE);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    private void initDialog() {
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        view = getLayoutInflater().inflate(R.layout.dialog_edit_team_member, null);
        mDialogEditTextName = (EditText) view.findViewById(R.id.edit_member_name);
        mDialogEditTextAbout = (EditText) view.findViewById(R.id.edit_about);
        alertDialog.setView(view);
        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                updateMemberDetails(teamName, mDialogEditTextName.getText().toString(), mDialogEditTextAbout.getText().toString(), mMemberId);
            }
        });
    }

    private void deleteDialog() {
        deleteAlertDialog = new AlertDialog.Builder(this);
        deleteAlertDialog.setCancelable(false);
        deleteAlertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteMemberDetails(teamName, mMemberId);
            }
        });
        deleteAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void removeView() {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    edit_position = position;
                    mMemberId = mMembers.get(position).getId();
                    mMemberName = mMembers.get(position).getMemberName();
                    deleteAlertDialog.setMessage("Do you want to delete "+mMemberName);
                    deleteAlertDialog.show();
                } else {
                    removeView();
                    edit_position = position;
                    alertDialog.setTitle("Edit Member");
                    mDialogEditTextName.setText(mMembers.get(position).getMemberName());
                    mDialogEditTextAbout.setText(mMembers.get(position).getAboutMember());
                    mMemberId = mMembers.get(position).getId();
                    alertDialog.show();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_edit);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mTeamMemberRecyclerView);
    }

    private void getTeamMemberList(String teamName) {
        mMembers.clear();
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, NetworkIntentService.class);
        intent.putExtra("teamName", teamName);
        intent.putExtra("url", Constants.MEMBER.replace("teamName", teamName));
        intent.putExtra("method", Constants.GET_METHOD);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("from", "get-team-members");
        startService(intent);
    }

    private void updateMemberDetails(String teamName, String memberName, String about, String id) {
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, NetworkIntentService.class);
        intent.putExtra("teamName", teamName);
        intent.putExtra("memberName", memberName);
        intent.putExtra("about", about);
        intent.putExtra("id", id);
        intent.putExtra("url", Constants.MEMBER.replace("teamName", teamName));
        intent.putExtra("method", Constants.PUT_METHOD);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("from", "update-team-member");
        startService(intent);
    }

    private void deleteMemberDetails(String teamName, String id) {
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, NetworkIntentService.class);
        intent.putExtra("teamName", teamName);
        intent.putExtra("id", id);
        intent.putExtra("url", Constants.MEMBER.replace("teamName", teamName));
        intent.putExtra("method", Constants.DELETE_METHOD);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("from", "delete-team-member");
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
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case NetworkIntentService.STATUS_RUNNING:
                mProgress.setVisibility(View.VISIBLE);
                break;
            case NetworkIntentService.STATUS_FINISHED:
                mProgress.setVisibility(View.INVISIBLE);
                Log.d(TAG, resultData.getString("result"));
                String response = resultData.getString("result");
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.has("statusMessage") && json.getString("statusMessage").equals("success")) {
                        if (json.has("teamMembers")) {
                            JSONArray teamMembers = json.getJSONArray("teamMembers");
                            if (teamMembers.length() > 0) {
                                mTextViewLabel.setVisibility(View.GONE);
                                for (int i = 0; i < teamMembers.length(); i++) {
                                    JSONObject jsonObject = teamMembers.getJSONObject(i);
                                    String teamName = jsonObject.getString("teamName");
                                    String memberName = jsonObject.getString("memberName");
                                    String about = jsonObject.getString("aboutMember");
                                    String id = jsonObject.getString("_id");
                                    TeamMemberDetails.Member member = new TeamMemberDetails().new Member();
                                    member.setMemberName(memberName);
                                    member.setAboutMember(about);
                                    member.setId(id);
                                    mMembers.add(member);
                                }
                            } else {
                                mTextViewLabel.setVisibility(View.VISIBLE);
                            }
                        } else if (json.has("member")) {
                            mTextViewLabel.setVisibility(View.GONE);
                            Toast.makeText(this, R.string.success_team_member, Toast.LENGTH_SHORT).show();
                            JSONObject jsonObject = json.getJSONObject("member");
                            String teamName = jsonObject.getString("teamName");
                            String memberName = jsonObject.getString("memberName");
                            String about = jsonObject.getString("aboutMember");
                            TeamMemberDetails.Member member = new TeamMemberDetails().new Member();
                            member.setMemberName(memberName);
                            member.setAboutMember(about);
                            mMembers.add(member);
                        } else if (json.has("updated")) {
                            mAdapter.notifyDataSetChanged();
                            getTeamMemberList(teamName);
                        } else if (json.has("deleted")) {
                            mAdapter.removeItem(edit_position);
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
                break;
            case NetworkIntentService.STATUS_ERROR:
                mProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(this, R.string.error_team_member, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
