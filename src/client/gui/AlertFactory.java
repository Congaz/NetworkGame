package client.gui;
import javafx.scene.control.Alert;

public class AlertFactory {

    public AlertFactory() {

    }

    public static Alert unrecoverableError(Exception e) {
        logAndPrint(e);
        // --- Create and return alert ---
        String txt = "";
        txt += "I acknowledge that the programmers should do a better job.\n";
        txt += "For your inconvenience the application will now close. Sorry!";
        Alert alert = new Alert(Alert.AlertType.ERROR, txt);
        String headerText = "";
        headerText += "Oops! Something went terribly wrong...\n";
        //headerText += "Something caused an " + e.getClass().getSimpleName() + ".";
        alert.setHeaderText(headerText);
        alert.setTitle("Unrecoverable Error... we f***** up!");
        return alert;
    }

    public static Alert serverConnectFailure(Exception e) {
        logAndPrint(e);
        // --- Create and return alert ---
        String txt = "";
        txt += "Server IP is incorrect or server is unresponsive.\n";
        txt += "Check IP and/or try again....\n";
        Alert alert = new Alert(Alert.AlertType.WARNING, txt);
        String headerText = "";
        headerText += "Unable to connect to server.\n";
        alert.setHeaderText(headerText);
        alert.setTitle("Server Connect Failure");
        return alert;
    }

     public static Alert serverError(Exception e) {
        logAndPrint(e);
        // --- Create and return alert ---
        String txt = "";
        txt += "As we don't know why things went tits up, application will shut down.";
        Alert alert = new Alert(Alert.AlertType.ERROR, txt);
        String headerText = "";
        headerText += "Server returned invalid or malformed data\n";
        alert.setHeaderText(headerText);
        alert.setTitle("Server f***** up!");
        return alert;
    }

    /**
     * Prints stack trace to console.
     * Logging not implemented.
     * @param e
     */
    private static void logAndPrint(Exception e) {
         e.printStackTrace();
    }

    private static String getExceptionName(Exception e) {
        return e.getClass().getSimpleName();
    }

}
