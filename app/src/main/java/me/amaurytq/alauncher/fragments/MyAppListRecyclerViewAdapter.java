package me.amaurytq.alauncher.fragments;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import me.amaurytq.alauncher.R;
import me.amaurytq.alauncher.database.models.AppItem;

public class MyAppListRecyclerViewAdapter extends RecyclerView.Adapter<MyAppListRecyclerViewAdapter.ViewHolder> {

    private final List<AppItem> mValues;
    private final AppListFragment.OnListFragmentInteractionListener mListener;

    MyAppListRecyclerViewAdapter(List<AppItem> items, AppListFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.application_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder._applicationLabel.setText(mValues.get(position).packageLabel);

        holder._view.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onClickListener(holder.mItem);
            }
        });
        holder._view.setOnLongClickListener(v -> {
            if (null != mListener) {
                mListener.onLongClickListener(holder.mItem);
                return true;
            }
            else return false;
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View _view;
        final TextView _applicationLabel;
        AppItem mItem;

        ViewHolder(View view) {
            super(view);
            _view = view;
            _applicationLabel = view.findViewById(R.id.tvApplicationLabel);
        }
    }
}
