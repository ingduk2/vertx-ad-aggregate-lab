package com.ad.aggregate.lab.verticle.model;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class FlushPayloadCodec implements MessageCodec<FlushPayload, FlushPayload> {

    @Override
    public void encodeToWire(Buffer buffer, FlushPayload flushPayload) {

    }

    @Override
    public FlushPayload decodeFromWire(int pos, Buffer buffer) {
        return null;
    }

    @Override
    public FlushPayload transform(FlushPayload flushPayload) {
        return flushPayload;
    }

    @Override
    public String name() {
        return "FlushPayloadCodec";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
