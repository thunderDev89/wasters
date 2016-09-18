package com.acemen.android.wasters.fragment;


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.acemen.android.wasters.BuildConfig;
import com.acemen.android.wasters.Common;
import com.acemen.android.wasters.R;
import com.acemen.android.wasters.event.IEvent;
import com.acemen.android.wasters.restful.SendWastesRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import static com.acemen.android.wasters.R.id.refresh_button;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewFragment extends Fragment {
    private static String LOG_TAG = OverviewFragment.class.getSimpleName();

    private Button mRefreshButton;
    private MapView mMapView;

    public OverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.overview_fragment, container, false);

        setupMapView(rootView);
        mRefreshButton = (Button) rootView.findViewById(refresh_button);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfos();
            }
        });

        return rootView;
    }

    private void setupMapView(View rootView) {
        //important! set your user agent to prevent getting banned from the osm servers
        OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);
        mMapView = (MapView) rootView.findViewById(R.id.map);
        assert mMapView != null;
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setTilesScaledToDpi(true);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);

        Location loc = Common.getCurrentLocation(getActivity());
        GeoPoint startPoint = new GeoPoint(loc.getLatitude(), loc.getLongitude());
        IMapController mapController = mMapView.getController();
        mapController.setZoom(9);

        Marker startMarker = new Marker(mMapView);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(startMarker);
        startMarker.setIcon(getResources().getDrawable(R.drawable.person));
        startMarker.setTitle("Start point");

        mapController.animateTo(startPoint);

        //Refresh the mMapView to
        mMapView.invalidate();
    }

    public void getInfos() {
        new SendWastesRequest(getActivity()).get();
    }

    public void setPointsToMap(String wastes) {
        mMapView.getOverlays().add(new ItemizedIconOverlay<OverlayItem>(SendWastesRequest.getWastes(wastes), null, getActivity()));
        mMapView.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceived(IEvent event) {
        if (SendWastesRequest.EVENT_GET_WASTES_ID == event.getId())
            setPointsToMap(event.getMessage());
    }
}
