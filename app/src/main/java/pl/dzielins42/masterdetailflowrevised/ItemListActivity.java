package pl.dzielins42.masterdetailflowrevised;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to full screen {@link ItemDetailFragment} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AbsMasterDetailActivity implements View.OnClickListener {

    private static final String TAG = ItemListActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (mToolbar != null) {
            mToolbar.setTitle(getTitle());
        }

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        if (mFab != null) {
            mFab.setOnClickListener(this);
        }
    }

    @Override
    protected Fragment getListFragment() {
        return new ItemListFragment();
    }

    @Override
    protected Fragment getDetailFragment(String itemId) {
        Bundle arguments = new Bundle();
        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, itemId);
        ItemDetailFragment detailFragment = new ItemDetailFragment();
        detailFragment.setArguments(arguments);

        return detailFragment;
    }

    @Override
    protected Fragment getDetailFragment(long itemId) {
        //   getDetailFragment(String) is used
        throw new UnsupportedOperationException();
    }

    @Override
    protected int getMainPanelId() {
        return R.id.container_a;
    }

    @Override
    protected int getDetailPanelId() {
        return R.id.container_b;
    }

    @Override
    public void onBackStackChanged() {
        super.onBackStackChanged();
        if (mFab != null) {
            mFab.setVisibility((isItemSelected() && isSinglePaneMode()) ? View.GONE : View.VISIBLE);
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