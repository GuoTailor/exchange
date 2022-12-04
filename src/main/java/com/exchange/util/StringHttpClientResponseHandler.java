package com.exchange.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.impl.classic.AbstractHttpClientResponseHandler;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

/**
 * create by GYH on 2022/11/17
 */
@Slf4j
@Contract(threading = ThreadingBehavior.STATELESS)
public class StringHttpClientResponseHandler extends AbstractHttpClientResponseHandler<String> {
    @Override
    public String handleEntity(HttpEntity entity) throws IOException {
        try {
            return EntityUtils.toString(entity);
        } catch (final ParseException ex) {
            throw new ClientProtocolException(ex);
        }
    }

    @Override
    public String handleResponse(final ClassicHttpResponse response) throws IOException {
        final HttpEntity entity = response.getEntity();
        if (response.getCode() >= HttpStatus.SC_REDIRECTION) {
            log.warn("{} {}", response.getCode(), response.getReasonPhrase());
            try {
                return EntityUtils.toString(entity);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return entity == null ? null : handleEntity(entity);
    }
}
