package vikram.mindtree.com.myevents;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.MyViewHolder> {

    private List<TeamMemberDetails.Member> mMembers;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView about;
        ImageView pic;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.txt_name);
            about = (TextView) view.findViewById(R.id.txt_about);
            pic = (ImageView) view.findViewById(R.id.img_member_pic);
        }

    }


    public MemberListAdapter(List<TeamMemberDetails.Member> members, Context context) {
        this.mMembers = members;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TeamMemberDetails.Member member = mMembers.get(position);
        holder.name.setText("Name - "+member.getMemberName());
        holder.about.setText("About - "+member.getAboutMember());

        //get first letter of each String item
        String firstLetter = String.valueOf(member.getMemberName().charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getColor(member);
        TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter, color); // radius in px
        holder.pic.setImageDrawable(drawable);
        /*if (member.getMemberPic() != null) {
            holder.pic.setImageBitmap(member.getMemberPic());
        } else {
            holder.pic.setImageDrawable(ContextCompat.getDrawable(mContext, android.R.drawable.sym_def_app_icon));
        }*/

    }

    @Override
    public int getItemCount() {
        return mMembers.size();
    }

    public void removeItem(int position) {
        mMembers.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mMembers.size());
    }
}