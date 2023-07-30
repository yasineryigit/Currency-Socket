package com.ossovita.currencysocket.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.ossovita.currencysocket.data.DataScraper;
import com.ossovita.currencysocket.data.EventProducer;
import com.ossovita.currencysocket.model.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SocketModule {

    private final SocketIOServer socketIOServer;
    private final EventProducer eventProducer;
    private final DataScraper dataScraper;

    public SocketModule(SocketIOServer socketIOServer, EventProducer eventProducer, DataScraper dataScraper) {
        this.socketIOServer = socketIOServer;
        this.eventProducer = eventProducer;
        this.dataScraper = dataScraper;

        socketIOServer.addConnectListener(onConnected());
        socketIOServer.addDisconnectListener(onDisconnected());
    }

    @Scheduled(fixedRate = 3000)
    private void getDataAndSendEventToTheClientPeriodically(){
        //fetch data
        List<Currency> currencyList = dataScraper.getDataFromWebsite();
        //send event to each client
        for (SocketIOClient client : socketIOServer.getAllClients()) {
            eventProducer.sendEventToTheClient(client, currencyList);//send related events to each client that connected to our server
        }
    }

    private ConnectListener onConnected() {
        return client -> {
            log.info(String.format("SocketID: %s connected", client.getSessionId().toString()));
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            log.info(String.format("SocketID: %s disconnected!", client.getSessionId().toString()));
        };
    }


}



