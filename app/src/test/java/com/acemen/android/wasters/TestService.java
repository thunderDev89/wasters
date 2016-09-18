package com.acemen.android.wasters;

import android.content.Context;
import android.util.Log;

import com.acemen.android.wasters.event.IEvent;
import com.acemen.android.wasters.restful.SendWastesRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Created by Audrik ! on 23/08/2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class TestService {
    private static final String LOG_TAG = TestService.class.getSimpleName();


    private Context getContext() {
        return RuntimeEnvironment.application;
    }

    @Test
    public void testSendWasteService() {
        SendWastesRequest request = new SendWastesRequest.Builder()
                .addContext(getContext())
                .addFilePath("fakepath/fakeimage.jpg")
                .addWasteType(getContext().getResources().getStringArray(R.array.waste_types)[0])
                .addAdresse(buildFakeAdresse())
                .build();

        EventBus.getDefault().register(this);
        request.execute();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceived(IEvent event) {
        Assert.assertEquals(event.getMessage(), "Error", event.getHeader());
        Log.e(LOG_TAG, event.getHeader() + " --> " + event.getMessage());
//        Common.showAlertDialog(getContext(), event.getHeader()
//                , event.getMessage()
//                , false);
    }

    private Adresse buildFakeAdresse() {
        Adresse adresse = new Adresse(
                "48",
                "Avenue Aristide Briand",
                "92220",
                "Bagneux",
                3.3238422,
                48.8035527
        );
        return adresse;
    }
}
