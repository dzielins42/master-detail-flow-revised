package pl.dzielins42.masterdetailflowrevised;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public abstract class AbsMasterDetailActivity extends AppCompatActivity implements
        FragmentManager.OnBackStackChangedListener {

    private static final String KEY_MODE = "pl.dzielins42.masterdetailflowrevised.key.MODE";
    private static final String KEY_ACTIVE_ITEM_ID_STRING = "pl.dzielins42.masterdetailflowrevised.key.ACTIVE_ITEM_ID_STRING";
    private static final String KEY_ACTIVE_ITEM_ID_LONG = "pl.dzielins42.masterdetailflowrevised.key.ACTIVE_ITEM_ID_LONG";
    private static final String TAG_DETAIL_FRAGMENT = "pl.dzielins42.masterdetailflowrevised.tag.DETAL";
    private static final String TAG_LIST_FRAGMENT = "pl.dzielins42.masterdetailflowrevised.tag.LIST";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private String mItemIdString;
    private Long mItemIdLong;
    private Bundle mSavedInstanceStateForInitialization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSavedInstanceStateForInitialization = savedInstanceState;
    }

    @Override
    protected void onPause() {
        getSupportFragmentManager().removeOnBackStackChangedListener(this);

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        init();
    }

    protected void init() {
        //getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Fragment listFragment;
        // Try to use existing list Fragment if available
        listFragment = getSupportFragmentManager().findFragmentByTag(TAG_LIST_FRAGMENT);
        if (listFragment == null) {
            // Fragment not available, possibly first run
            listFragment = getListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(getMainPanelId(), listFragment, TAG_LIST_FRAGMENT)
                    .commit();
        }

        if (mSavedInstanceStateForInitialization != null) {
            restoreDetailFragment(mSavedInstanceStateForInitialization);
        }

        mSavedInstanceStateForInitialization = null;
    }

    protected boolean isTwoPaneMode() {
        return mTwoPane;
    }

    protected boolean isSinglePaneMode() {
        return !mTwoPane;
    }

    protected abstract Fragment getListFragment();

    protected abstract Fragment getDetailFragment(String itemId);

    protected abstract Fragment getDetailFragment(long itemId);

    protected abstract int getMainPanelId();

    protected abstract int getDetailPanelId();

    protected void restoreDetailFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null || !(savedInstanceState.containsKey(KEY_ACTIVE_ITEM_ID_LONG)
                || savedInstanceState.containsKey(KEY_ACTIVE_ITEM_ID_STRING))) {
            return;
        }

        // Restore ID
        if (savedInstanceState.containsKey(KEY_ACTIVE_ITEM_ID_LONG)) {
            mItemIdLong = savedInstanceState.getLong(KEY_ACTIVE_ITEM_ID_LONG);
        }
        mItemIdString = savedInstanceState.getString(KEY_ACTIVE_ITEM_ID_STRING);

        // If the mode (two-pane vs single-pane) did not change and detail Fragment exists,
        // it is assumed that everything is OK
        Fragment detailFragment = getSupportFragmentManager()
                .findFragmentByTag(TAG_DETAIL_FRAGMENT);
        // It is assumed that mode did not change
        boolean savedModeIsTwoPane = savedInstanceState.getBoolean(KEY_MODE, isTwoPaneMode());
        if (detailFragment != null && savedModeIsTwoPane == isTwoPaneMode()) {
            return;
        }

        FragmentManager fragmentManager=getSupportFragmentManager();
        // In this case, detail Fragment has to be moved between main panel and detail panel
        if(isTwoPaneMode()){
            // Was single-pane mode
            // Detail Fragment should be above list Fragment,
            // pop back stack to take it from the top, it will be added to detail panel
            fragmentManager.popBackStack();
        } else {
            // Was two-pane mode
            // Detail Fragment is attached to detail panel,
            // remove it and it will be added to main panel
            fragmentManager.beginTransaction().remove(detailFragment).commit();
        }
        fragmentManager.executePendingTransactions();
        showDetailFragment(detailFragment);
    }

    public void onItemSelected(String itemId) {
        mItemIdString = itemId;
        mItemIdLong = null;

        Fragment detailFragment = getDetailFragment(itemId);
        if (detailFragment != null) {
            showDetailFragment(detailFragment);
        }
    }

    public void onItemSelected(long itemId) {
        mItemIdLong = itemId;
        mItemIdString = null;

        Fragment detailFragment = getDetailFragment(itemId);
        if (detailFragment != null) {
            showDetailFragment(detailFragment);
        }
    }

    private void showDetailFragment(Fragment detailFragment) {
        if (isTwoPaneMode()) {
            getSupportFragmentManager().beginTransaction().replace(getDetailPanelId(),
                    detailFragment, TAG_DETAIL_FRAGMENT).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(getMainPanelId(),
                    detailFragment, TAG_DETAIL_FRAGMENT).addToBackStack(null).commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        boolean stackedFragments = getSupportFragmentManager().getBackStackEntryCount() > 0;
        if (stackedFragments) {
            mItemIdLong = null;
            mItemIdString = null;
            getSupportFragmentManager().popBackStack();
            return true;
        }

        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        boolean stackedFragments = getSupportFragmentManager().getBackStackEntryCount() > 0;
        if (stackedFragments) {
            mItemIdLong = null;
            mItemIdString = null;
        }

        super.onBackPressed();
    }

    protected boolean isItemSelected() {
        return mItemIdLong != null || mItemIdString != null;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        if (findViewById(getDetailPanelId()) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mItemIdLong != null) {
            outState.putLong(KEY_ACTIVE_ITEM_ID_LONG, mItemIdLong);
        } else if (mItemIdString != null) {
            outState.putString(KEY_ACTIVE_ITEM_ID_STRING, mItemIdString);
        }
        outState.putBoolean(KEY_MODE, isTwoPaneMode());
    }

    public void setActionBarTitle(CharSequence title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void onBackStackChanged() {
        boolean stackedFragments = getSupportFragmentManager().getBackStackEntryCount() > 0;
        boolean hasParentActivity = getSupportParentActivityIntent() != null;
        // Show the Up button in the action bar if needed
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(stackedFragments || hasParentActivity);
            if (!stackedFragments) {
                setActionBarTitle(getTitle());
            }
        }
    }

}