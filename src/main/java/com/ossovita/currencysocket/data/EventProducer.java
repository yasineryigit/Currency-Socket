package com.ossovita.currencysocket.data;

import com.corundumstudio.socketio.SocketIOClient;
import com.ossovita.currencysocket.model.Currency;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventProducer {


    public void sendEventToTheClient(SocketIOClient client, List<Currency> currencyList) {
        String symbol = client.getHandshakeData().getSingleUrlParam("symbol");

        client.joinRoom(symbol);//attend client to the room belongs to the symbol (e.g: USD)

        if (symbol.equals("ALL")) {
            client.getNamespace().getRoomOperations(symbol)
                    .sendEvent("response", currencyList, client.getSessionId(), symbol);

        } else {
            Optional<Currency> currencyOptional = currencyList.stream()
                    .filter(currency -> symbol.equals(currency.getSymbol()))
                    .findFirst();

            if(currencyOptional.isPresent()){
                client.getNamespace().getRoomOperations(symbol)
                        .sendEvent("response", currencyOptional.get(), client.getSessionId(), symbol);
            }else{
                client.getNamespace().getRoomOperations(symbol)
                        .sendEvent("response", "Invalid symbol", client.getSessionId(), symbol);
                client.disconnect();
            }

        }

    }


}
