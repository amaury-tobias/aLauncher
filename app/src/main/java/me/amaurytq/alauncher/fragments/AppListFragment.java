package me.amaurytq.alauncher.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reddit.indicatorfastscroll.FastScrollItemIndicator;
import com.reddit.indicatorfastscroll.FastScrollerThumbView;
import com.reddit.indicatorfastscroll.FastScrollerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.amaurytq.alauncher.R;
import me.amaurytq.alauncher.adapters.AppItemListContent;
import me.amaurytq.alauncher.adapters.AppListAdapter;
import me.amaurytq.alauncher.database.models.AppItem;

public class AppListFragment extends Fragment implements AppListAdapter.AdapterListener {

    private OnListFragmentInteractionListener mListener;
    private int _color;

    private AppItemListContent appItemsContent;

    public static AppListFragment newInstance(int color) {
        AppListFragment fragment = new AppListFragment();
        Bundle args = new Bundle();
        args.putInt("COLOR", color);
        fragment.setArguments(args);
        return fragment;
    }

    private List<AppItem> getAppList() {
        List<AppItem> items = new ArrayList<>();
        PackageManager packageManager = Objects.requireNonNull(getActivity()).getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = packageManager.queryIntentActivities( mainIntent, 0);
        Collections.sort(pkgAppsList, new ResolveInfo.DisplayNameComparator(packageManager));

        for (ResolveInfo resolveInfo: pkgAppsList) {
            AppItem item = new AppItem();
            item.isHidden = false;
            item.packageLabel = resolveInfo.loadLabel(packageManager).toString();
            item.packageName = resolveInfo.activityInfo.packageName;
            item.isSystemApp = ((resolveInfo.activityInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
            items.add(item);
        }
        return items;
    }

    private AppListAdapter mAppListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appItemsContent = new AppItemListContent(getContext());
        mAppListAdapter = new AppListAdapter(this, appItemsContent.appItems);

        if (getArguments() != null) {
            _color = getArguments().getInt("COLOR");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        appItemsContent.fillList();
        mAppListAdapter.notifyDataSetChanged();
    }

    @BindView(R.id.ApplicationList) RecyclerView mRecyclerView;
    @BindView(R.id.fastscroller) FastScrollerView mFastScrollerView;
    @BindView(R.id.fastscroller_thumb) FastScrollerThumbView mFastScrollerThumbView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applist_list, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.setAdapter(mAppListAdapter);

        mFastScrollerView.setupWithRecyclerView(mRecyclerView, position -> {
                    AppItem item = appItemsContent.appItems.get(position);
                    return new FastScrollItemIndicator.Text(item.packageLabel.substring(0, 1).toUpperCase());
                }
        );

        mFastScrollerView.setTextColor(ColorStateList.valueOf(_color));
        mFastScrollerThumbView.setupWithFastScroller(mFastScrollerView);
        mFastScrollerThumbView.setThumbColor(ColorStateList.valueOf(_color));

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
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
    public void onClick(AppItem item) {
        mListener.onClickListener(item);
    }

    @Override
    public boolean onLongClick(AppItem item) {
        return mListener.onLongClickListener(item);
    }

    public interface OnListFragmentInteractionListener {
        void onClickListener(AppItem item);
        boolean onLongClickListener(AppItem item);
    }

}
