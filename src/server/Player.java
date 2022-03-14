package server;

public class Player
{
    private ServerThreadRead read;
    private ServerThreadWrite write;
    private String name;

    public Player(ServerThreadRead read, ServerThreadWrite write) {
        this.read = read;
        this.write = write;
    }

    public void writeToServer(String message) {
        this.write.write(message);
    }




}
