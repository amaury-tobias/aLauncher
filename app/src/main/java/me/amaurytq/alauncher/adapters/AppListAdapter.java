package me.amaurytq.alauncher.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.amaurytq.alauncher.R;
import me.amaurytq.alauncher.database.models.AppItem;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private final List<AppItem> appItems;
    private AdapterListener mListener;

    public AppListAdapter(AdapterListener listener, List<AppItem> appItems) {
        this.mListener = listener;
        this.appItems = appItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.application_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mAppItem = appItems.get(position);
        holder.mApplicationLabel.setText(holder.mAppItem.packageLabel);
        if (mListener == null) return;
        holder.mView.setOnClickListener(v -> mListener.onClick(holder.mAppItem));
        holder.mView.setOnLongClickListener(v -> mListener.onLongClick(holder.mAppItem));
    }

    @Override
    public int getItemCount() {
        return appItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvApplicationLabel)
        TextView mApplicationLabel;

        View mView;
        AppItem mAppItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, mView);
        }
    }

    public interface AdapterListener {
        void onClick(AppItem item);
        boolean onLongClick(AppItem item);
    }
}
