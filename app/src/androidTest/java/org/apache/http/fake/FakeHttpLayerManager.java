/*
 * The MIT License
 *
 * Copyright (c) 2010 Xtreme Labs and Pivotal Labs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.apache.http.fake;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import java.util.List;

public class FakeHttpLayerManager {
    private static class FakeHttpLayerManagerHolder {
        private static final FakeHttpLayer INSTANCE = new FakeHttpLayer();
    }

    public static FakeHttpLayer getFakeHttpLayer() {
        return FakeHttpLayerManagerHolder.INSTANCE;
    }

    /**
     * Sets up an HTTP response to be returned by calls to Apache's {@code HttpClient} implementers.
     *
     * @param statusCode   the status code of the response
     * @param responseBody the body of the response
     * @param headers      optional headers for the request
     */
    public static void addPendingHttpResponse(int statusCode, String responseBody, Header... headers) {
        getFakeHttpLayer().addPendingHttpResponse(statusCode, responseBody, headers);
    }

    /**
     * Sets up an HTTP response to be returned by calls to Apache's {@code HttpClient} implementers.
     *
     * @param statusCode   the status code of the response
     * @param responseBody the body of the response
     * @param contentType  the contentType of the response
     * @deprecated use {@link #addPendingHttpResponse(int, String, Header...)} instead
     */
    public static void addPendingHttpResponseWithContentType(int statusCode, String responseBody, Header contentType) {
        getFakeHttpLayer().addPendingHttpResponse(statusCode, responseBody, contentType);
    }

    /**
     * Sets up an HTTP response to be returned by calls to Apache's {@code HttpClient} implementers.
     *
     * @param httpResponse the response
     */
    public static void addPendingHttpResponse(HttpResponse httpResponse) {
        getFakeHttpLayer().addPendingHttpResponse(httpResponse);
    }

    /**
     * Sets up an HTTP response to be returned by calls to Apache's {@code HttpClient} implementers.
     *
     * @param httpResponseGenerator an HttpResponseGenerator that will provide responses
     */
    public static void addPendingHttpResponse(HttpResponseGenerator httpResponseGenerator) {
        getFakeHttpLayer().addPendingHttpResponse(httpResponseGenerator);
    }

    /**
     * Accessor to obtain HTTP requests made during the current test in the order in which they were made.
     *
     * @param index index of the request to retrieve.
     * @return the requested request.
     */
    public static HttpRequest getSentHttpRequest(int index) {
        return getFakeHttpLayer().getSentHttpRequestInfo(index).getHttpRequest();
    }

    public static HttpRequest getLatestSentHttpRequest() {
        int requestCount = FakeHttpLayerManager.getFakeHttpLayer().getSentHttpRequestInfos().size();
        return FakeHttpLayerManager.getFakeHttpLayer().getSentHttpRequestInfo(requestCount - 1).getHttpRequest();
    }

    /**
     * Accessor to find out if HTTP requests were made during the current test.
     *
     * @return whether a request was made.
     */
    public static boolean httpRequestWasMade() {
        return getFakeHttpLayer().hasRequestInfos();
    }

    public static boolean httpRequestWasMade(String uri) {
        return getFakeHttpLayer().hasRequestMatchingRule(
                new FakeHttpLayer.UriRequestMatcher(uri));
    }

    /**
     * Accessor to obtain metadata for an HTTP request made during the current test in the order in which they were made.
     *
     * @param index index of the request to retrieve.
     * @return the requested request metadata.
     */
    public static HttpRequestInfo getSentHttpRequestInfo(int index) {
        return getFakeHttpLayer().getSentHttpRequestInfo(index);
    }

    /**
     * Accessor to obtain HTTP requests made during the current test in the order in which they were made.
     *
     * @return the requested request or null if there are none.
     */
    public static HttpRequest getNextSentHttpRequest() {
        HttpRequestInfo httpRequestInfo = getFakeHttpLayer().getNextSentHttpRequestInfo();
        return httpRequestInfo == null ? null : httpRequestInfo.getHttpRequest();
    }

    /**
     * Accessor to obtain metadata for an HTTP request made during the current test in the order in which they were made.
     *
     * @return the requested request metadata or null if there are none.
     */
    public static HttpRequestInfo getNextSentHttpRequestInfo() {
        return getFakeHttpLayer().getNextSentHttpRequestInfo();
    }

    /**
     * Adds an HTTP response rule. The response will be returned when the rule is matched.
     *
     * @param method   method to match.
     * @param uri      uri to match.
     * @param response response to return when a match is found.
     */
    public static void addHttpResponseRule(String method, String uri, HttpResponse response) {
        getFakeHttpLayer().addHttpResponseRule(method, uri, response);
    }

    /**
     * Adds an HTTP response rule with a default method of GET. The response will be returned when the rule is matched.
     *
     * @param uri      uri to match.
     * @param response response to return when a match is found.
     */
    public static void addHttpResponseRule(String uri, HttpResponse response) {
        getFakeHttpLayer().addHttpResponseRule(uri, response);
    }

    /**
     * Adds an HTTP response rule. The response will be returned when the rule is matched.
     *
     * @param uri      uri to match.
     * @param response response to return when a match is found.
     */
    public static void addHttpResponseRule(String uri, String response) {
        getFakeHttpLayer().addHttpResponseRule(uri, response);
    }

    /**
     * Adds an HTTP response rule. The response will be returned when the rule is matched.
     *
     * @param requestMatcher custom {@code RequestMatcher}.
     * @param response       response to return when a match is found.
     */
    public static void addHttpResponseRule(RequestMatcher requestMatcher, HttpResponse response) {
        getFakeHttpLayer().addHttpResponseRule(requestMatcher, response);
    }

    /**
     * Adds an HTTP response rule. For each time the rule is matched, responses will be shifted
     * off the list and returned. When all responses have been given and the rule is matched again,
     * an exception will be thrown.
     *
     * @param requestMatcher custom {@code RequestMatcher}.
     * @param responses      responses to return in order when a match is found.
     */
    public static void addHttpResponseRule(RequestMatcher requestMatcher, List<? extends HttpResponse> responses) {
        getFakeHttpLayer().addHttpResponseRule(requestMatcher, responses);
    }

    public static void setDefaultHttpResponse(int statusCode, String responseBody) {
        getFakeHttpLayer().setDefaultHttpResponse(statusCode, responseBody);
    }

    public static void setDefaultHttpResponse(HttpResponse defaultHttpResponse) {
        getFakeHttpLayer().setDefaultHttpResponse(defaultHttpResponse);
    }

    public static void clearHttpResponseRules() {
        getFakeHttpLayer().clearHttpResponseRules();
    }

    public static void clearPendingHttpResponses() {
        getFakeHttpLayer().clearPendingHttpResponses();
    }
}
