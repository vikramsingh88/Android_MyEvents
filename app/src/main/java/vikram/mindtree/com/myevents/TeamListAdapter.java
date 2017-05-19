package vikram.mindtree.com.myevents;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;

public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private ArrayList<TeamDetails> listTeams;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TeamDetails item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int holderId;
        TextView textView;
        ImageView imageView;
        ImageView profile;
        TextView Name;
        TextView email;
        RelativeLayout relTeamContainer;

        public ViewHolder(View itemView,int ViewType) {
            super(itemView);
            if(ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.row_text);
                imageView = (ImageView) itemView.findViewById(R.id.row_icon);
                relTeamContainer = (RelativeLayout)itemView.findViewById(R.id.contain_team);
                holderId = 1;
            }
            else{//header view
                //Name = (TextView) itemView.findViewById(R.id.name);
                //email = (TextView) itemView.findViewById(R.id.email);
                //profile = (ImageView) itemView.findViewById(R.id.circleView);
                //holderId = 0;
            }
        }

        public void bind(final TeamDetails item, final OnItemClickListener listener) {
            relTeamContainer.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    TeamListAdapter(ArrayList<TeamDetails> listTeams, OnItemClickListener listener){
        this.listTeams = listTeams;
        this.listener = listener;
    }

    @Override
    public TeamListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row,parent,false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v,viewType); //Creating ViewHolder and passing the object of type view
            return vhItem; // Returning the created object
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_header_main2,parent,false); //Inflating the layout
            ViewHolder vhHeader = new ViewHolder(v,viewType); //Creating ViewHolder and passing the object of type view
            return vhHeader; //returning the object created
        }
        return null;
    }

    @Override
    public void onBindViewHolder(TeamListAdapter.ViewHolder holder, int position) {
        if(holder.holderId ==1) {
            holder.textView.setText(listTeams.get(position - 1).getTeamName());
            //holder.imageView.setImageResource(mIcons[position -1]);

            //get first letter of each String item
            String firstLetter = String.valueOf(listTeams.get(position - 1).getTeamName().charAt(0));
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            // generate random color
            int color = generator.getColor(listTeams.get(position - 1));
            TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter, color); // radius in px
            holder.imageView.setImageDrawable(drawable);

            holder.bind(listTeams.get(position - 1), listener);
        }
        else{//header
            //holder.profile.setImageResource(profile);
           // holder.Name.setText(name);
            //holder.email.setText(email);
        }
    }

    @Override
    public int getItemCount() {
        return listTeams.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}