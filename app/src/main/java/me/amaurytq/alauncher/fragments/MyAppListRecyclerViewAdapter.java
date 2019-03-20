package me.amaurytq.alauncher.fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.amaurytq.alauncher.R;
import me.amaurytq.alauncher.fragments.models.ApplicationItem;

public class MyAppListRecyclerViewAdapter extends RecyclerView.Adapter<MyAppListRecyclerViewAdapter.ViewHolder> {

    private final List<ApplicationItem> mValues;
    private final AppListFragment.OnListFragmentInteractionListener mListener;

    MyAppListRecyclerViewAdapter(List<ApplicationItem> items, AppListFragment.OnListFragmentInteractionListener listener) {
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

        holder._view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onClickListener(holder.mItem);
                }
            }
        });
        holder._view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != mListener) {
                    mListener.onLongClickListener(holder.mItem);
                    return true;
                }
                else return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View _view;
        final TextView _applicationLabel;
        ApplicationItem mItem;

        ViewHolder(View view) {
            super(view);
            _view = view;
            _applicationLabel = view.findViewById(R.id.tvApplicationLabel);
        }
    }
}
