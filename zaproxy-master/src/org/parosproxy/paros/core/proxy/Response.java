package org.parosproxy.paros.core.proxy;

import java.io.IOException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.parosproxy.paros.network.HttpHeader;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpOutputStream;
import org.parosproxy.paros.network.HttpRequestHeader;

public class Response {
	
	public static void setError(HttpMessage msg, String responseStatus, Exception cause)
            throws HttpMalformedHeaderException {
		setError(msg, responseStatus, cause, "ZAP Error");
    }

    public static void setError(HttpMessage msg, String responseStatus, Exception cause, String errorType)
            throws HttpMalformedHeaderException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(errorType)
                .append(" [")
                .append(cause.getClass().getName())
                .append("]: ")
                .append(cause.getLocalizedMessage())
                .append("\n\nStack Trace:\n");
        for (String stackTraceFrame : ExceptionUtils.getRootCauseStackTrace(cause)) {
            strBuilder.append(stackTraceFrame).append('\n');
        }

        setError(msg, responseStatus, strBuilder.toString());
    }

    public static void setError(HttpMessage msg, String responseStatus, String message)
            throws HttpMalformedHeaderException {
        msg.setResponseHeader("HTTP/1.1 " + responseStatus);

        if (!HttpRequestHeader.HEAD.equals(msg.getRequestHeader().getMethod())) {
            msg.setResponseBody(message);
        }

        msg.getResponseHeader().addHeader(HttpHeader.CONTENT_LENGTH, Integer.toString(message.length()));
        msg.getResponseHeader().addHeader(HttpHeader.CONTENT_TYPE, "text/plain; charset=UTF-8");
    }
    
    public static void writeHttp(HttpMessage msg, HttpOutputStream outputStream) throws IOException {
        outputStream.write(msg.getResponseHeader());
        outputStream.flush();

        if (msg.getResponseBody().length() > 0) {
            outputStream.write(msg.getResponseBody().getBytes());
            outputStream.flush();
        }
    }
}
