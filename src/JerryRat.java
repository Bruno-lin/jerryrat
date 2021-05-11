import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class JerryRat implements Runnable {

    public static final String SERVER_PORT = "8080";
    public static final String WEB_ROOT = "res/webroot";
    ServerSocket serverSocket;

    public JerryRat() throws IOException {
        serverSocket = new ServerSocket(Integer.parseInt(SERVER_PORT));
    }

    @Override
    public void run() {
        while (true) {
            try (
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                String request = in.readLine();
                while (request != null) {
                    BufferedReader htmlReader;
                    String[] requestPart = request.split(" ");
                    String get = requestPart[0].toLowerCase();
                    if (!get.equals("get")) {
                        break;
                    }
                    String resource = requestPart[1];
                    File file = new File(WEB_ROOT + resource);
                    if (file.isFile()) {
                        FileReader fr = new FileReader(file);
                        char[] contents = new char[(int) file.length()];
                        fr.read(contents);
                        out.println(String.valueOf(contents));
                    } else {
                        FileReader fr = new FileReader(WEB_ROOT + "index.html");
                        char[] contents = new char[(int) file.length()];
                        fr.read(contents);
                        out.println(String.valueOf(contents));
                    }
                    request = in.readLine();
                }
            } catch (IOException e) {
                System.err.println("TCP连接错误！");
            }
        }

    }

    public static void main(String[] args) throws IOException {
        JerryRat jerryRat = new JerryRat();
        new Thread(jerryRat).start();
    }
}
