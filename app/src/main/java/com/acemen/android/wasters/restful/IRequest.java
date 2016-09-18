package com.acemen.android.wasters.restful;

import com.acemen.android.wasters.restful.helper.HttpRequest;

import org.json.JSONObject;

/**
 * Created by Audrik ! on 21/08/2016.
 */
public interface IRequest {

    String getRoute();

    HttpRequest buildRequest(String... params);



    /**
     * To execute/launch the request
     * @return
     */
    JSONObject execute();
}
