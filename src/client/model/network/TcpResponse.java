package client.model.network;

@FunctionalInterface
public interface TcpResponse {
    void response(String message);
}
