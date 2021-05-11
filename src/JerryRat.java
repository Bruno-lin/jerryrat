import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

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
                    String[] req = request.split(" ");
                    String get = req[0].toLowerCase();
                    if (!get.equals("get")) {
                        break;
                    }
                    String requestPath = req[1];
                    File requestFile = new File(WEB_ROOT + requestPath);
                    if (requestFile.isDirectory()) {
                        requestFile = new File(requestFile, "/index.html");
                        FileReader fos = new FileReader(requestFile);
                        char[] content = new char[(int) requestFile.length()];
                        fos.read(content);
                        out.println(String.valueOf(content));
                    } else {
                        FileReader fos = new FileReader(requestFile);
                        char[] content = new char[(int) requestFile.length()];
                        fos.read(content);
                        out.println(String.valueOf(content));
                    }
                    request = in.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("TCP连接错误！");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        JerryRat jerryRat = new JerryRat();
        new Thread(jerryRat).start();
    }
}
