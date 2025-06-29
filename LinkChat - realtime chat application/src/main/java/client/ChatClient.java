package chatWhatsappApplication.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.google.gson.Gson;
import chatWhatsappApplication.service.MessageService;

import java.net.URI;

public class ChatClient extends WebSocketClient {
    private final Gson gson = new Gson();
    private final String myEmail;

    public ChatClient(URI serverUri, String myEmail) {
        super(serverUri);
        this.myEmail = myEmail;
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        // Connexion établie : s’identifier auprès du serveur
        Message login = new Message("login", myEmail, null, null);
        send(gson.toJson(login));
    }

    public interface MessageListener {
        void onMessageReceived(String from, String content);
    }

    private MessageListener messageListener;

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    @Override
    public void onMessage(String messageJson) {
        // Nouveau message reçu
        Message message = gson.fromJson(messageJson, Message.class);
        System.out.println("Message from " + message.from + ": " + message.content);
        if (messageListener != null) {
            messageListener.onMessageReceived(message.from, message.content);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public void sendMessage(String to, String content) {
        Message message = new Message("msg", myEmail, to, content);
        send(gson.toJson(message));
        MessageService.saveMessage(myEmail, message.to, message.content);

    }

    // Classe interne pour représenter les messages JSON
    static class Message {
        String type;
        String from;
        String to;
        String content;

        public Message(String type, String from, String to, String content) {
            this.type = type;
            this.from = from;
            this.to = to;
            this.content = content;
        }
    }
}
