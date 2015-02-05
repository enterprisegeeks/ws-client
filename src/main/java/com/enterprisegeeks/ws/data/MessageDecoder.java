package com.enterprisegeeks.ws.data;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * 
 */
public class MessageDecoder implements Decoder.Text<Message>{

    @Override
    public void init(EndpointConfig config) {}

    @Override
    public void destroy() {}
    
    @Override
    public Message decode(String s) throws DecodeException {
        JsonReader reader = Json.createReader(new StringReader(s));
        JsonObject obj = reader.readObject();
        String name = obj.getString("name");
        String message = obj.getString("message");
        return new Message(name, message);
    }

    @Override
    public boolean willDecode(String s) {
        return s != null;
    }
}
