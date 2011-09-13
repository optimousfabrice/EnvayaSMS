package org.envaya.kalsms;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;

public class ForwarderTask extends HttpTask {

    private IncomingMessage originalSms;

    public ForwarderTask(IncomingMessage originalSms, BasicNameValuePair... paramsArr) {
        super(originalSms.app, paramsArr);
        this.originalSms = originalSms;
        
        params.add(new BasicNameValuePair("action", App.ACTION_INCOMING));
    }

    @Override
    protected String getDefaultToAddress() {
        return originalSms.getFrom();
    }

    @Override
    protected void handleResponse(HttpResponse response) throws Exception {

        for (OutgoingMessage reply : parseResponseXML(response)) {
            app.sendOutgoingMessage(reply);
        }

        app.setIncomingMessageStatus(originalSms, true);
    }

    @Override
    protected void handleFailure() {
        app.setIncomingMessageStatus(originalSms, false);
    }
}
