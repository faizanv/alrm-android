package mas.alrm.alrm;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

/**
 * Created by faizanv on 11/16/16.
 */

public class AlarmRecylerViewAdapter extends RecyclerView.Adapter<AlarmRecylerViewAdapter.ViewHolder> {

    List<Date> dataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.alarm_title);
        }
    }

    public AlarmRecylerViewAdapter(List<Date> dataset) {
        this.dataset = dataset;
    }

    @Override
    public AlarmRecylerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_card, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Date alarmDate = dataset.get(position);
        holder.mTextView.setText(alarmDate.toString());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


}
