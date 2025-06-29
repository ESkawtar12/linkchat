package chatWhatsappApplication.server;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import com.google.gson.Gson;

import java.net.InetSocketAddress;
import java.util.*;

public class ChatServer extends WebSocketServer {
    private final Map<String, WebSocket> clients = new HashMap<>();
    private final Gson gson = new Gson();

    public ChatServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Client connected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        clients.values().removeIf(v -> v.equals(conn));
        System.out.println("Client disconnected");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Message m = gson.fromJson(message, Message.class);
        if (m.type.equals("login")) {
            clients.put(m.from, conn);
        } else if (m.type.equals("msg")) {
            WebSocket dest = clients.get(m.to);
            if (dest != null) dest.send(message);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("✅ WebSocket server started on port " + getPort());
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer(8887);
        server.start();
    }

    static class Message {
        String type, from, to, content;
    }
}
