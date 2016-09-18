package com.acemen.android.wasters.restful.helper;

import com.acemen.android.wasters.entity.ResponseEntity;
import com.acemen.android.wasters.entity.WasteEntity;

/**
 * Created by Audrik ! on 20/04/2016.
 */
public interface ApiClient {
    String BASE_URL = "http://codiad.wasters.bonaxium.com/"; //http://od3m.com:8282/

    String ROUTE_SEND_WASTE_REQUEST = "api/v1/waste";

    String CLIENT_ID = "15147524726452519394";

    String CLIENT_SECRET = "7AwTLP1A2J67DX95vlj5";

    ResponseEntity sendCleaningRequest(final WasteEntity waste);

    WasteEntity getWaste(final String wasteId);
}
