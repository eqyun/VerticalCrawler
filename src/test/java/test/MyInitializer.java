package test;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;

import java.io.IOException;

/**
 * Created by eqyun on 2014/11/14.
 */
public class MyInitializer implements HttpRequestInitializer, HttpUnsuccessfulResponseHandler {

    @Override
    public boolean handleResponse(
            HttpRequest request, HttpResponse response, boolean retrySupported) throws IOException {
        System.out.println(response.getStatusCode() + " " + response.getStatusMessage());
        return false;
    }

    @Override
    public void initialize(HttpRequest request) throws IOException {
        request.setUnsuccessfulResponseHandler(this);
    }
}

