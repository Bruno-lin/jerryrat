import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EchoServer implements Runnable {
    public static final String SERVER_PORT = "3456";
    ServerSocket serverSocket;

    public EchoServer() throws IOException {
        serverSocket = new ServerSocket(Integer.parseInt(SERVER_PORT));
    }

    @Override
    public void run() {
        try (
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String line = in.readLine();
            int i = 0;
            while (line != null) {
                i++;
                out.println(i+". "+line);
                line = in.readLine();
            }
        } catch (IOException e) {
            System.err.println("TCP连接错误！");
        }
    }

    public static void main(String[] args) throws IOException {
//        EchoServer echoServer = new EchoServer();
//        new Thread(echoServer).run();
        String decoded = new String(Base64.getDecoder().decode("aGVsbG86d29ybGQ="), StandardCharsets.UTF_8);
        System.out.println(decoded);
    }
}