package vikram.mindtree.com.myevents;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamStanding extends AppCompatActivity {

    private TableLayout mTableLayoutScoreBoard;
    private HashMap<String,Integer> teamScore = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_standing);

        mTableLayoutScoreBoard = (TableLayout)findViewById(R.id.score_board);

        Bundle bundle = this.getIntent().getExtras();

        if(bundle != null) {
            teamScore = (HashMap) bundle.getSerializable("details");
        }

        //Table header
        TableRow tr= getTableHeader();
        mTableLayoutScoreBoard.addView(tr);

        Map<String, Integer> sortedMapDesc = SortMapByValue.sortByComparator(teamScore, SortMapByValue.DESC);

        //table body
        for (String key : sortedMapDesc.keySet()) {
            int totalPoint = sortedMapDesc.get(key);
            TableRow trData = getTableData(key ,totalPoint);
            mTableLayoutScoreBoard.addView(trData);
        }
    }

    private TableRow getTableHeader() {
        TableRow tr = (TableRow)getLayoutInflater().inflate(R.layout.team_standing_table_hdr, null);
        return tr;
    }

    private TableRow getTableData(String team, int total) {
        TableRow tr = (TableRow)getLayoutInflater().inflate(R.layout.team_standing_table_row, null);
        TextView txtTeam = (TextView) tr.findViewById(R.id.txt_td_teams);
        TextView txtTotal = (TextView) tr.findViewById(R.id.txt_td_total);

        txtTeam.setText(team);
        if (total != 0){
            txtTotal.setText(String.valueOf(total));
        } else {
            txtTotal.setText("");
        }

        return tr;
    }
}