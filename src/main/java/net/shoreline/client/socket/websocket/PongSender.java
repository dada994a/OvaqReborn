package net.shoreline.client.socket.websocket;


class PongSender extends PeriodicalFrameSender
{
    private static final String TIMER_NAME = "PongSender";


    public PongSender(WebSocket webSocket, PayloadGenerator generator)
    {
        super(webSocket, TIMER_NAME, generator);
    }


    @Override
    protected WebSocketFrame createFrame(byte[] payload)
    {
        return WebSocketFrame.createPongFrame(payload);
    }
}
