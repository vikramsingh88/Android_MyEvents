package vikram.mindtree.com.myevents;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScoreBoardActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver {
    private static final String TAG = "ScoreBoardActivity";
    private TableLayout mTableLayoutScoreBoard;
    private Button btnTeamStanding;
    private DownloadResultReceiver mReceiver;
    private HashMap<String,Integer> teamScore = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);

        mTableLayoutScoreBoard = (TableLayout)findViewById(R.id.score_board);
        btnTeamStanding = (Button)findViewById(R.id.btn_team_standing);

        getScored();

        btnTeamStanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreBoardActivity.this, TeamStanding.class);
                Bundle extras = new Bundle();
                extras.putSerializable("details",teamScore);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    class Record {
        String eventName;
        String points;
        String date;

        public Record(String eventName, String points, String date) {
            this.eventName = eventName;
            this.points = points;
            this.date = date;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    private  HashMap<String, List<Record>> generateScoreBoard(ArrayList<ScoreBoard> records) {
        HashMap<String, List<Record>> score = new HashMap<>();
        for (ScoreBoard sb : records) {
            if (score.containsKey(sb.getTeamName())) {
                List<Record> temp = score.get(sb.getTeamName());
                temp.add(new Record(sb.getEventName(), sb.getPoints(), sb.getDate()));
                score.put(sb.getTeamName(), temp);
            } else {
                List<Record> temp = new ArrayList<>();
                temp.add(new Record(sb.getEventName(), sb.getPoints(), sb.getDate()));
                score.put(sb.getTeamName(), temp);
            }
        }
        return score;
    }

    private TableRow getTableHeader() {
        TableRow tr = (TableRow)getLayoutInflater().inflate(R.layout.table_header_template, null);
        return tr;
    }

    private TableRow getTableData(String team, String event, String points, int total) {
        TableRow tr = (TableRow)getLayoutInflater().inflate(R.layout.table_row_template, null);
        TextView txtTeam = (TextView) tr.findViewById(R.id.txt_td_teams);
        TextView txtEvent = (TextView) tr.findViewById(R.id.text_td_event);
        TextView txtPoints = (TextView) tr.findViewById(R.id.txt_td_points);
        TextView txtTotal = (TextView) tr.findViewById(R.id.txt_td_total);

        txtTeam.setText(team);
        txtEvent.setText(event);
        txtPoints.setText(points);
        if (total != 0){
            txtTotal.setText(String.valueOf(total));
            txtTotal.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTableHeader));
        } else {
            txtTotal.setText("");
        }

        return tr;
    }

    //get score board
    private void getScored() {
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(ScoreBoardActivity.this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, ScoreBoardActivity.this, NetworkIntentService.class);
        intent.putExtra("url", Constants.SCORE_BOARD);
        intent.putExtra("method", Constants.GET_METHOD);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("from", "get-score");

        startService(intent);
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
                    if (json.has("scoreboard")) {
                        ArrayList<ScoreBoard> scoreBoard = new ArrayList<>();
                        JSONArray scoreBoards = json.getJSONArray("scoreboard");
                        for(int i=0; i < scoreBoards.length(); i++){
                            JSONObject jsonObject = scoreBoards.getJSONObject(i);
                            String teamName = jsonObject.getString("teamName");
                            String eventName = jsonObject.getString("eventName");
                            String points = jsonObject.getString("points");
                            String date = null;
                            if (jsonObject.has("date")) {
                                date = jsonObject.getString("date");
                            }
                            scoreBoard.add(new ScoreBoard(teamName, eventName, points, date));
                        }
                        HashMap<String, List<Record>> hashRecords = generateScoreBoard(scoreBoard);
                        //Table header
                        TableRow tr= getTableHeader();
                        mTableLayoutScoreBoard.addView(tr);

                        //table body
                        for (String key : hashRecords.keySet()) {
                            List<Record> records = hashRecords.get(key);
                            int total = 0;
                            for (int i = 0; i<records.size(); i++) {
                                Record record = records.get(i);
                                total = total + Integer.parseInt(record.getPoints());
                                TableRow trData = getTableData(key, record.getEventName(), record.getPoints(), 0);
                                mTableLayoutScoreBoard.addView(trData);
                            }
                            TableRow trData = getTableData("", "", "", total);
                            teamScore.put(key, total);
                            mTableLayoutScoreBoard.addView(trData);
                        }
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
                Toast.makeText(this, R.string.error_score_board, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
