package pl.dzielins42.masterdetailflowrevised;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import pl.dzielins42.masterdetailflowrevised.dummy.DummyContent;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to full screen {@link ItemDetailFragment} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity implements FragmentManager
        .OnBackStackChangedListener, View.OnClickListener {

    private static final String ACTIVE_ITEM_ID = "active_item_id";
    private static final String TAG = ItemListActivity.class.getSimpleName();

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private String mActiveItemId = null;

    private Toolbar mToolbar;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setToolbarText(getTitle());

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        if (mFab != null) {
            mFab.setOnClickListener(this);
        }

        if (findViewById(R.id.container_b) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        ItemListFragment listFragment = new ItemListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container_a, listFragment)
                .commit();

        DummyContent.DummyItem savedActiveItem = getSavedActiveItem(savedInstanceState);
        if (savedActiveItem != null) {
            mActiveItemId = savedActiveItem.id;
            showItemDetail(savedActiveItem);
            if (mFab != null) {
                mFab.setVisibility(mTwoPane ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void showItemDetail(DummyContent.DummyItem item) {
        Bundle arguments = new Bundle();
        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.id);
        ItemDetailFragment detailFragment = new ItemDetailFragment();
        detailFragment.setArguments(arguments);
        if (mTwoPane) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_b,
                    detailFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_a,
                    detailFragment).addToBackStack(null).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActiveItemId != null) {
            outState.putString(ACTIVE_ITEM_ID, mActiveItemId);
        }
    }

    private DummyContent.DummyItem getSavedActiveItem(Bundle savedInstanceState) {
        if (savedInstanceState == null || !savedInstanceState.containsKey(ACTIVE_ITEM_ID)) {
            return null;
        } else {
            String id = savedInstanceState.getString(ACTIVE_ITEM_ID);
            return DummyContent.ITEM_MAP.get(id);
        }
    }

    public void onItemClick(DummyContent.DummyItem item) {
        mActiveItemId = item.id;
        showItemDetail(item);
    }

    public void setToolbarText(CharSequence text) {
        if (mToolbar != null) {
            mToolbar.setTitle(text);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        boolean stackedFragments = getSupportFragmentManager().getBackStackEntryCount() > 0;
        if (stackedFragments) {
            getSupportFragmentManager().popBackStack();
            return true;
        }

        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackStackChanged() {
        boolean stackedFragments = getSupportFragmentManager().getBackStackEntryCount() > 0;
        boolean hasParentActivity = getSupportParentActivityIntent() != null;
        // Show the Up button in the action bar if needed
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(stackedFragments || hasParentActivity);
        }
        if (!stackedFragments) {
            setToolbarText(getTitle());
        }
        if (mFab != null) {
            mFab.setVisibility(stackedFragments && !mTwoPane ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }

        switch (v.getId()) {
            case R.id.fab:
                onFabClick(v);
                break;

            default:
                break;
        }
    }

    private void onFabClick(View v) {
        Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG).setAction
                ("Action", null).show();
    }

}