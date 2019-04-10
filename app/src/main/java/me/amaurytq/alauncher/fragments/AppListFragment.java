package me.amaurytq.alauncher.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.reddit.indicatorfastscroll.FastScrollItemIndicator;
import com.reddit.indicatorfastscroll.FastScrollerThumbView;
import com.reddit.indicatorfastscroll.FastScrollerView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import me.amaurytq.alauncher.AppListFragmentManager;
import me.amaurytq.alauncher.R;
import me.amaurytq.alauncher.fragments.content.ApplicationContent;
import me.amaurytq.alauncher.fragments.models.ApplicationItem;

public class AppListFragment extends Fragment implements AppListFragmentManager {

    private OnListFragmentInteractionListener mListener;
    private MyAppListRecyclerViewAdapter _adapter;
    private int _color;

    private FastScrollerView fastScrollerView;
    private FastScrollerThumbView fastScrollerThumbView;

    private Switch switchDN;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applist_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.ApplicationList);

        fastScrollerView = view.findViewById(R.id.fastscroller);
        fastScrollerThumbView = view.findViewById(R.id.fastscroller_thumb);

        //switchDN = view.findViewById(R.id.switchDN);



        if (view instanceof ConstraintLayout){
            final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipeApplicationList);
            refreshLayout.setColorSchemeColors(Color.BLACK);

            refreshLayout.setOnRefreshListener(() -> {
                mListener.onSwipeRefreshAppList();
                refreshLayout.setRefreshing(false);
            });
        }

        Context context = view.getContext();
        _adapter = new MyAppListRecyclerViewAdapter(ApplicationContent.ITEMS, mListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(_adapter);

        fastScrollerView.setupWithRecyclerView(recyclerView, (position) -> {
            ApplicationItem item = ApplicationContent.ITEMS.get(position);
            return new FastScrollItemIndicator.Text(item.packageLabel.substring(0, 1).toUpperCase());
                }
        );
        fastScrollerThumbView.setupWithFastScroller(fastScrollerView);
        fastScrollerView.setTextColor(ColorStateList.valueOf(_color));
        fastScrollerThumbView.setThumbColor(ColorStateList.valueOf(_color));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener)
            mListener = (OnListFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void updateAdapter() {
        ApplicationContent.fillItemList();
        _adapter.notifyDataSetChanged();
    }

    @Override
    public void updateColor(int color) {
        _color = color;
        if (fastScrollerView != null) {
            fastScrollerView.setTextColor(ColorStateList.valueOf(_color));
            fastScrollerThumbView.setThumbColor(ColorStateList.valueOf(_color));
        }
    }

    public interface OnListFragmentInteractionListener {
        void onClickListener(ApplicationItem item);
        void onLongClickListener(ApplicationItem item);
        void onSwipeRefreshAppList();
    }

}
