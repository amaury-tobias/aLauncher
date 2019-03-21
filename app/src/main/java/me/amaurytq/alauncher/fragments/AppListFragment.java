package me.amaurytq.alauncher.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.amaurytq.alauncher.R;
import me.amaurytq.alauncher.fragments.content.ApplicationContent;
import me.amaurytq.alauncher.fragments.models.ApplicationItem;

public class AppListFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    public static MyAppListRecyclerViewAdapter _adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applist_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.ApplicationList);

        if (view instanceof SwipeRefreshLayout){
            final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view;
            refreshLayout.setColorSchemeColors(Color.BLACK);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mListener.onSwipeRefreshAppList();
                    refreshLayout.setRefreshing(false);
                }
            });
        }
        Context context = view.getContext();

        _adapter = new MyAppListRecyclerViewAdapter(ApplicationContent.ITEMS, mListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(_adapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (null != _adapter)
            _adapter.notifyDataSetChanged();
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement 1OnListFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onClickListener(ApplicationItem item);
        void onLongClickListener(ApplicationItem item);
        void onSwipeRefreshAppList();
    }

}
