package com.acemen.android.wasters;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.acemen.android.wasters.event.IEvent;
import com.acemen.android.wasters.fragment.NavigationPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @see <a href="https://developer.android.com/training/camera/photobasics.html">Capture photo</a>
 * @see <a href="https://guides.codepath.com/android/Fragment-Navigation-Drawer">Add Navigation Drawer</a>
 * @see <a href="https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library">Add OSM map</a>
 */
public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO use Butterknife for binding view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        FragmentPagerAdapter adapter = new NavigationPager(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        //important! set your user agent to prevent getting banned from the osm servers
//        OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);
//
//        MapView map = (MapView) findViewById(R.id.map);
//        assert map != null;
//        map.setTileSource(TileSourceFactory.MAPNIK);
//        map.setTilesScaledToDpi(true);
//
//        Location loc = Common.getCurrentLocation(this);
//        GeoPoint startPoint = new GeoPoint(loc.getLatitude(), loc.getLongitude());
//        IMapController mapController = map.getController();
//        mapController.setZoom(13);
//        mapController.setCenter(startPoint);
//
//        Marker startMarker = new Marker(map);
//        startMarker.setPosition(startPoint);
//        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//        map.getOverlays().add(startMarker);
//        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_menu_compass));
//        startMarker.setTitle("Start point");
//
//        //Refresh the map to see changes on map
//        map.invalidate();
//
//        // Set toolbar to replace the action bar
//        mToolbar = (Toolbar) findViewById(toolbar);
//        setSupportActionBar(mToolbar);

    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // The action bar home/up action should open or close the drawer.
//        switch (item.getItemId()) {
//            case home:
//                mDrawer.openDrawer(GravityCompat.START);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceived(IEvent event) {
        Common.showAlertDialog(this, event.getHeader()
                , event.getMessage()
                , false);
    }
}
