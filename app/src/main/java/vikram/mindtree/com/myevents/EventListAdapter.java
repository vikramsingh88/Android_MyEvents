package vikram.mindtree.com.myevents;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.MyViewHolder> {

    private List<EventDetail> mEvents, mFilterList;
    boolean isAdmin;
    Context mContext;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(EventDetail item);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtVenue;
        TextView txtTime;
        TextView txtDate;
        TextView txtPoints;
        TextView txtNotified;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.txt_name);
            txtVenue = (TextView) view.findViewById(R.id.txt_venue);
            txtTime = (TextView) view.findViewById(R.id.txt_time);
            txtDate = (TextView) view.findViewById(R.id.txt_date);
            txtPoints = (TextView) view.findViewById(R.id.txt_point);
            txtNotified = (TextView) view.findViewById(R.id.txt_notified);
            cardView = (CardView) view.findViewById(R.id.card_view);
        }

        public void bind(final EventDetail item, final EventListAdapter.OnItemClickListener listener) {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }


    public EventListAdapter(boolean isAdmin, List<EventDetail> events, Context context,OnItemClickListener listener) {
        this.isAdmin = isAdmin;
        this.mEvents = events;
        this.mFilterList = new ArrayList<>();
        this.mFilterList.addAll(this.mEvents);
        mContext = context;
        this.listener = listener;
    }

    @Override
    public EventListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item_row, parent, false);
        return new EventListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EventListAdapter.MyViewHolder holder, int position) {
        EventDetail event = mFilterList.get(position);
        holder.txtName.setText(event.getEventName());
        holder.txtVenue.setText(event.getEventVenue());
        holder.txtTime.setText(event.getEventTime());
        holder.txtDate.setText(event.getEventDate());
        holder.txtPoints.setText(event.getEventPoints());
        if (isAdmin && event.getIsNotified()) {
            holder.txtNotified.setVisibility(View.VISIBLE);
        } else {
            holder.txtNotified.setVisibility(View.INVISIBLE);
        }
        holder.bind(mFilterList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return (null != mFilterList ? mFilterList.size() : 0);
    }
    //Searching item
    public void filter(final String text) {
        // Searching could be complex..so we will dispatch it to a different thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Clear the filter list
                mFilterList.clear();
                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {

                    mFilterList.addAll(mEvents);

                } else {
                    // Iterate in the original List and add it to filter list
                    for (EventDetail item : mEvents) {
                        if (item.getEventName().toLowerCase().contains(text.toLowerCase()) ) {
                            // Adding Matched items
                            mFilterList.add(item);
                        }
                    }
                }
                // Set on UI Thread
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed
                        notifyDataSetChanged();
                    }
                });

            }
        }).start();
    }
}