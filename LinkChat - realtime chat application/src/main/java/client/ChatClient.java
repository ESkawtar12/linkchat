package chatWhatsappApplication.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.google.gson.Gson;
import chatWhatsappApplication.service.MessageService;
import chatWhatsappApplication.DiscussionDetail;
import javax.swing.SwingUtilities;

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
        // Now safe to request online list
        requestOnlineList();
    }

    public interface MessageListener {
        void onMessageReceived(String from, String content);
    }

    private MessageListener messageListener;

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    public interface TypingListener {
        void onTypingReceived(String from);
    }

    private TypingListener typingListener;

    public void setTypingListener(TypingListener listener) {
        this.typingListener = listener;
    }

    @Override
    public void onMessage(String messageJson) {
        Message message = gson.fromJson(messageJson, Message.class);
        if ("edit".equals(message.type)) {
            String[] parts = message.content.split(":", 2);
            int msgId = Integer.parseInt(parts[0]);
            String newContent = parts[1];
            MessageService.editMessage(msgId, newContent);
            DiscussionDetail panel = chatWhatsappApplication.Main.discussionPanels.get(message.from);
            if (panel != null) {
                SwingUtilities.invokeLater(panel::loadPreviousMessages);
            }
            return;
        }
        if ("onlineList".equals(message.type)) {
            // Notify listeners with the list of online emails
            java.util.List<String> onlineEmails = gson.fromJson(message.content, java.util.List.class);
            if (onlineStatusListener != null) {
                onlineStatusListener.onOnlineStatusReceived(onlineEmails);
            }
            return;
        }
        if ("delete".equals(message.type)) {
            int msgId = Integer.parseInt(message.content);
            MessageService.deleteMessage(msgId);
            // Notify UI to refresh if this chat is open
            DiscussionDetail panel = chatWhatsappApplication.Main.discussionPanels.get(message.from);
            if (panel != null) {
                SwingUtilities.invokeLater(panel::loadPreviousMessages);
            }
            return;
        }
        if ("typing".equals(message.type)) {
            if (typingListener != null) {
                typingListener.onTypingReceived(message.from);
            }
            return;
        }
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

    public void requestOnlineList() {
        Message msg = new Message("whoisonline", myEmail, null, null);
        send(gson.toJson(msg));
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

    // Add a listener interface:
    public interface OnlineStatusListener {
        void onOnlineStatusReceived(java.util.List<String> onlineEmails);
    }

    private OnlineStatusListener onlineStatusListener;

    public void setOnlineStatusListener(OnlineStatusListener listener) {
        this.onlineStatusListener = listener;
    }

    public void sendDeleteMessage(String to, int messageId) {
        Message message = new Message("delete", myEmail, to, String.valueOf(messageId));
        send(gson.toJson(message));
    }

    public void sendEditMessage(String to, int messageId, String newContent) {
        Message message = new Message("edit", myEmail, to, messageId + ":" + newContent);
        send(gson.toJson(message));
    }

    public void sendTypingMessage(String to) {
        Message message = new Message("typing", myEmail, to, "");
        send(gson.toJson(message));
    }
}
