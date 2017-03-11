package pl.dzielins42.masterdetailflowrevised;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public abstract class AbsMasterDetailActivity extends AppCompatActivity implements
        FragmentManager.OnBackStackChangedListener {

    private static final String ACTIVE_ITEM_ID_STRING = "pl.dzielins42.masterdetailflowrevised" +
            ".key.ACTIVE_ITEM_ID_STRING";
    private static final String ACTIVE_ITEM_ID_LONG = "pl.dzielins42.masterdetailflowrevised.key"
            + ".ACTIVE_ITEM_ID_LONG";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private String mItemIdString;
    private Long mItemIdLong;
    private Bundle mSavedInstanceStateForInitialization;
    private boolean mDoInit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDoInit = true;
        mSavedInstanceStateForInitialization = savedInstanceState;
    }

    @Override
    protected void onStart() {
        super.onStart();

        init();
    }

    protected void init() {
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Fragment listFragment = getListFragment();
        getSupportFragmentManager().beginTransaction().replace(getMainPanelId(), listFragment)
                .commit();

        if (mSavedInstanceStateForInitialization != null) {
            restoreDetailFragment(mSavedInstanceStateForInitialization);
        }

        mDoInit = false;
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
        if (savedInstanceState == null || !(savedInstanceState.containsKey(ACTIVE_ITEM_ID_LONG)
                || savedInstanceState.containsKey(ACTIVE_ITEM_ID_STRING))) {
            return;
        }

        if (savedInstanceState.containsKey(ACTIVE_ITEM_ID_LONG)) {
            onItemSelected(savedInstanceState.getLong(ACTIVE_ITEM_ID_LONG));
        } else if (savedInstanceState.containsKey(ACTIVE_ITEM_ID_STRING)) {
            onItemSelected(savedInstanceState.getString(ACTIVE_ITEM_ID_STRING));
        }
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
                    detailFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(getMainPanelId(),
                    detailFragment).addToBackStack(null).commit();
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
            outState.putLong(ACTIVE_ITEM_ID_LONG, mItemIdLong);
        } else if (mItemIdString != null) {
            outState.putString(ACTIVE_ITEM_ID_STRING, mItemIdString);
        }
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