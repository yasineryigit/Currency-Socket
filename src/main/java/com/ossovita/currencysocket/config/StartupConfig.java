package com.ossovita.currencysocket.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.ossovita.currencysocket.data.DataScraper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupConfig implements CommandLineRunner {

    private final SocketIOServer socketIOServer;
    private final DataScraper dataScraper;

    //Start the socket.io server on first run
    @Override
    public void run(String... args) throws Exception {
        socketIOServer.start();

    }


}
