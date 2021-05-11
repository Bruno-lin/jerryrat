import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class JerryRat implements Runnable {

    public static final String SERVER_PORT = "8080";
    ServerSocket serverSocket;

    public JerryRat() throws IOException {
        serverSocket = new ServerSocket(Integer.parseInt(SERVER_PORT));
    }

    @Override
    public void run() {
        try (
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String request = in.readLine();
            while (request != null) {
                String[] requestPart = request.split(" ");
                String get = requestPart[0].toLowerCase();
                String resource = requestPart[1];
                if (get.equals("get") && (resource.equals("/res/webroot") || resource.equals("/foo"))) {
                    File file = new File("res/webroot/foo");
                    BufferedReader htmlReader = new BufferedReader(new FileReader(file));
                    out.println(htmlReader.readLine());
                    request = in.readLine();
                } else
                    break;
            }
        } catch (IOException e) {
            System.err.println("TCP连接错误！");
        }
    }

    public static void main(String[] args) throws IOException {
        JerryRat jerryRat = new JerryRat();
        new Thread(jerryRat).start();
    }
}
