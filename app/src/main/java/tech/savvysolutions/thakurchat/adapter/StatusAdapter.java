package tech.savvysolutions.thakurchat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tech.savvysolutions.thakurchat.R;
import tech.savvysolutions.thakurchat.model.User;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {

    private Context context;
    private List<User> users;

    public StatusAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        StatusViewHolder holder;
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.item_status,parent,false);
        holder = new StatusViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
        holder.tvName.setText(users.get(position).getName());

        if(users.get(position).isStatus())
        {
            holder.tvStatus.setText("Online");
            holder.tvStatus.setTextColor(Color.parseColor("#00FF00"));
        }
        else
        {
            holder.tvStatus.setText("Offline");
            holder.tvStatus.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public static class StatusViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;
        private TextView tvStatus;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}
