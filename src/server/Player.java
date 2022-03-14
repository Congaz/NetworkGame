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

    public void fromServer(String message) {
        System.out.println("Im player - from Server: ");
    }


}
