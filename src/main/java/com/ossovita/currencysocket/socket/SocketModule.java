package com.ossovita.currencysocket.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SocketModule {

    private SocketIOServer socketIOServer;

    public SocketModule(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
        socketIOServer.addConnectListener(onConnected());
        socketIOServer.addDisconnectListener(onDisconnected());

    }

    private ConnectListener onConnected() {
        return client -> {
            String symbol = client.getHandshakeData().getSingleUrlParam("symbol");

            client.joinRoom(symbol);//attend client to the room belongs to the symbol (e.g: USDTRY)
            if(symbol.equals("USDTRY")){
                client.getNamespace().getRoomOperations(symbol)
                        //TODO: send real value of the symbol (e.g: USDTRY: {value: 27.21})
                        .sendEvent("value", "This is dummy value..", client.getSessionId(), symbol);
            }else if(symbol.equals("/")){
                //TODO: send real values of the all symbols
                client.getNamespace().getRoomOperations(symbol)
                        .sendEvent("values", "This is dummy values..", client.getSessionId(), symbol);
            }

            log.info(String.format("SocketID: %s connected", client.getSessionId().toString()));

        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            log.info(String.format("SocketID: %s disconnected!", client.getSessionId().toString()));
        };
    }


}
