package com.ossovita.currencysocket.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.ossovita.currencysocket.data.EventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SocketModule {

    private final SocketIOServer socketIOServer;
    private final EventProducer eventProducer;
    private final ScheduledExecutorService scheduler;

    public SocketModule(SocketIOServer socketIOServer, EventProducer eventProducer) {
        this.socketIOServer = socketIOServer;
        this.eventProducer = eventProducer;
        scheduler = Executors.newScheduledThreadPool(1);
        socketIOServer.addConnectListener(onConnected());
        socketIOServer.addDisconnectListener(onDisconnected());

    }

    private ConnectListener onConnected() {
        return client -> {

            Runnable runnable = () -> eventProducer.sendEventToTheClient(client);

            scheduler.scheduleAtFixedRate(runnable, 0, 3, TimeUnit.SECONDS);

            log.info(String.format("SocketID: %s connected", client.getSessionId().toString()));

        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            log.info(String.format("SocketID: %s disconnected!", client.getSessionId().toString()));
        };
    }


}
