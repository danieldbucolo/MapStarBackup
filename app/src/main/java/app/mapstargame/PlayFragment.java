package app.mapstargame;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.mapstargame.util.IabHelper;
import app.mapstargame.util.IabResult;
import app.mapstargame.util.Purchase;

public class PlayFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "com.mapgame.playfragment";
    public final static String EXTRA_MAP_NAME = "com.app.mapgame.MAP";
    public final static String EXTRA_TIME_OF_LEVEL = "com.app.mapgame.TIME";
    public final static String EXTRA_SOUNDS_ON = "com.app.mapgame.SOUND";
    public final static String EXTRA_PREM = "com.app.mapgame.PREMIUM";

    private RecyclerView.LayoutManager mLayoutManager;
    private MapListAdapter mAdapter;

    private int allPixels;
    private float padding;
    private float firstItemWidth;
    private float itemWidth;

    private static int sliderAdjust = 40;

    private static int[] icons = {R.drawable.world, R.drawable.usa, R.drawable.europe,
            R.drawable.asia, R.drawable.africa, R.drawable.latin_america};

    private static String[] names = {"World", "USA", "Europe", "Asia", "Africa", "Latin America"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Context c = getActivity();

        View mapSelection = inflater.inflate(R.layout.play_fragment, container, false);

        if (((MapStarActivity) c).mIsPremium) {
            mapSelection.findViewById(R.id.purchasing_text).setVisibility(View.GONE);
        }

        RecyclerView recyclerView = (RecyclerView) mapSelection.findViewById(R.id.level_selector);
        recyclerView.hasFixedSize();

        mLayoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MapListAdapter(icons, names,
                new MapListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String map) {
                        if ((map == "World" || map == "USA") || ((MapStarActivity)c).mIsPremium) {
                            startMap(c, map);
                        }
                    }
                }, c, ((MapStarActivity)c).mIsPremium);
        recyclerView.setAdapter(mAdapter);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        firstItemWidth = getResources().getDimension(R.dimen.item_width) + sliderAdjust;
        itemWidth = getResources().getDimension(R.dimen.item_width) + sliderAdjust;
        padding = (size.x - itemWidth) / 2;

        allPixels = 0;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                synchronized (this) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        calculatePositionAndScroll(recyclerView);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                allPixels += dx;
            }
        });

        scrollListToPosition(recyclerView, 0);

        mapSelection.findViewById(R.id.back_button).setOnClickListener(this);
        mapSelection.findViewById(R.id.purchasing_text).setOnClickListener(this);
        return mapSelection;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.back_button) {
            getFragmentManager().popBackStack();
        }
        if (v.getId() == R.id.purchasing_text) {
            MapStarActivity parent = (MapStarActivity) getActivity();
            try {
                parent.mHelper.launchPurchaseFlow(parent, MapStarActivity.SKU_PREMIUM, 10001,
                        mPurchaseFinishedListener,
                        "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ/");
            } catch (IabHelper.IabAsyncInProgressException e) {
                parent.mHelper.flagEndAsync();
                try {
                    parent.mHelper.launchPurchaseFlow(parent, MapStarActivity.SKU_PREMIUM, 10001,
                            mPurchaseFinishedListener,
                            "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ/");
                } catch (IabHelper.IabAsyncInProgressException e1) {
                    Log.d(TAG, "Error purchasing premium");
                }
                Log.d(TAG, "Error purchasing premium");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((RecyclerView) getView().findViewById(R.id.level_selector)).setAdapter(null);
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
                return;
            }

            else if (purchase.getSku().equals(((MapStarActivity)getActivity()).SKU_PREMIUM)) {
                MapStarActivity parent = (MapStarActivity) getActivity();

                ((MapStarActivity)getActivity()).mIsPremium = true;
                mAdapter.prem = true;
                getView().findViewById(R.id.purchasing_text).setVisibility(View.GONE);
                parent.findViewById(R.id.ad_view).setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    private void calculatePositionAndScroll(RecyclerView view) {
        int expectedPosition = Math.round((allPixels + padding - firstItemWidth) / itemWidth);
        // Special cases for the padding items
        if (expectedPosition == -1) {
            expectedPosition = 0;
        }
        else if (expectedPosition >= view.getAdapter().getItemCount() - 2) {
            expectedPosition--;
        }
        scrollListToPosition(view, expectedPosition);
    }

    private void scrollListToPosition(RecyclerView recyclerView, int expectedPosition) {
        float targetScrollPos = expectedPosition * itemWidth + firstItemWidth - padding;
        float missingPx = targetScrollPos - allPixels;
        if (missingPx != 0) {
            recyclerView.smoothScrollBy((int) missingPx, 0);
        }
    }

    private void startMap(Context c, String map) {
        Intent i = new Intent(c, MainActivity.class);
        MapStarActivity parent = (MapStarActivity) getActivity();
        if (parent.timeOfLevel > 0) {
            i.putExtra(EXTRA_TIME_OF_LEVEL, parent.timeOfLevel);
        }
        i.putExtra(EXTRA_SOUNDS_ON, parent.soundsOn);
        i.putExtra(EXTRA_MAP_NAME, map);
        startActivity(i);
        getActivity().finish();
    }
}
