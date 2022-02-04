package com.pcloud.sdk.internal.networking.serialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import okio.ByteString;

import java.io.IOException;

public class ByteStringTypeAdapter extends TypeAdapter<ByteString> {
    @Override
    public void write(JsonWriter out, ByteString value) throws IOException {
        out.value(value.hex());
    }

    @Override
    public ByteString read(JsonReader in) throws IOException {
        return ByteString.decodeHex(in.nextString());
    }
}
