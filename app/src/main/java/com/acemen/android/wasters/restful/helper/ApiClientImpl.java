package com.acemen.android.wasters.restful.helper;

import android.content.Context;

import com.acemen.android.wasters.entity.ResponseEntity;
import com.acemen.android.wasters.entity.WasteEntity;
import com.acemen.android.wasters.restful.SendWastesRequest;

/**
 * Created by Audrik ! on 24/08/2016.
 */
public class ApiClientImpl implements ApiClient {
    private static final String LOG_TAG = ApiClientImpl.class.getSimpleName();

    private final Context context;

    public ApiClientImpl(Context context) {
        this.context = context;
    }

    @Override
    public ResponseEntity sendCleaningRequest(WasteEntity waste) {
        //TODO Check internet connection and GPS
        SendWastesRequest request = new SendWastesRequest.Builder()
                .addContext(context)
                .addFilePath(waste.getCaptureFilename())
                .addWasteType(waste.getWasteType())
//                .addAdresse(waste.getAdresse().)
                .build();
        return null;
    }

    @Override
    public WasteEntity getWaste(String wasteId) {
        return null;
    }
}
