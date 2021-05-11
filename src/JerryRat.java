import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class JerryRat implements Runnable {

    public static final String SERVER_PORT = "8080";
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
                BufferedReader htmlReader = null;
                while (request != null) {
                    String[] requestPart = request.split(" ");
                    String get = requestPart[0].toLowerCase();
                    String resource = requestPart[1];
                    if (get.equals("get")) {
                        File file = new File("res/webroot" + resource);
                        if (file.isFile()) {
                            htmlReader = new BufferedReader(new FileReader(file));
                            out.println(htmlReader.readLine());
                        }else {
                            File file1 = new File("res/webroot" + "/foo");
                            htmlReader = new BufferedReader(new FileReader(file1));
                            out.println(htmlReader.readLine());
                        }
                    }
                    htmlReader.close();
                    request = null;
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
