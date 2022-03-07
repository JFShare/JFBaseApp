package com.cxkj.common.utils.update;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ProgressInterceptor implements Interceptor {

    private ProgressResponseBody.ProgressListener progressListener;

    public ProgressInterceptor(ProgressResponseBody.ProgressListener progressListener) {
        this.progressListener = progressListener;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(chain.request().url().url().toString(), originalResponse.body(), progressListener))
                .build();
    }
}
