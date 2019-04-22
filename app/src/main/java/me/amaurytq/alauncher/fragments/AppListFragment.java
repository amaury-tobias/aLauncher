package me.amaurytq.alauncher.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reddit.indicatorfastscroll.FastScrollItemIndicator;
import com.reddit.indicatorfastscroll.FastScrollerThumbView;
import com.reddit.indicatorfastscroll.FastScrollerView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import me.amaurytq.alauncher.R;
import me.amaurytq.alauncher.database.models.AppItem;
import me.amaurytq.alauncher.fragments.content.ApplicationContent;

public class AppListFragment extends Fragment implements ApplicationContent.OnApplicationContentInteractionListener {

    private OnListFragmentInteractionListener mListener;
    private MyAppListRecyclerViewAdapter _adapter;
    private int _color;

    public static AppListFragment newInstance(int color) {
        AppListFragment fragment = new AppListFragment();
        Bundle args = new Bundle();
        args.putInt("COLOR", color);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _color = getArguments().getInt("COLOR");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applist_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.ApplicationList);

        FastScrollerView fastScrollerView = view.findViewById(R.id.fastscroller);
        FastScrollerThumbView fastScrollerThumbView = view.findViewById(R.id.fastscroller_thumb);

        final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipeApplicationList);
        refreshLayout.setColorSchemeColors(Color.BLACK);
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            ApplicationContent.fillItemList();
            _adapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);
        });

        _adapter = new MyAppListRecyclerViewAdapter(ApplicationContent.ITEMS, mListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(_adapter);

        fastScrollerView.setupWithRecyclerView(recyclerView, position -> {
                    AppItem item = ApplicationContent.ITEMS.get(position);
                    return new FastScrollItemIndicator.Text(item.packageLabel.substring(0, 1).toUpperCase());
                }
        );
        fastScrollerThumbView.setupWithFastScroller(fastScrollerView);
        fastScrollerView.setTextColor(ColorStateList.valueOf(_color));
        fastScrollerThumbView.setThumbColor(ColorStateList.valueOf(_color));

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener)
            mListener = (OnListFragmentInteractionListener) context;
        ApplicationContent.setListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        ApplicationContent.setListener(null);
    }

    @Override
    public void notifyUpdated() {
        _adapter.notifyDataSetChanged();
    }

    public interface OnListFragmentInteractionListener {
        void onClickListener(AppItem item);
        void onLongClickListener(AppItem item);
    }

}
