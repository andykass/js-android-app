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
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HttpEntityStub implements HttpEntity {
    @Override public boolean isRepeatable() {
        return true;
    }

    @Override public boolean isChunked() {
        throw new UnsupportedOperationException();
    }

    @Override public long getContentLength() {
        throw new UnsupportedOperationException();
    }

    @Override public Header getContentType() {
        throw new UnsupportedOperationException();
    }

    @Override public Header getContentEncoding() {
        throw new UnsupportedOperationException();
    }

    @Override public InputStream getContent() throws IOException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override public void writeTo(OutputStream outputStream) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override public boolean isStreaming() {
        throw new UnsupportedOperationException();
    }

    @Override public void consumeContent() throws IOException {
        throw new UnsupportedOperationException();
    }

    public static interface ResponseRule {
        boolean matches(HttpRequest request);

        HttpResponse getResponse() throws HttpException, IOException;
    }
}
