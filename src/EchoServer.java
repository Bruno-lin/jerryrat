import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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
            String line;
            int i = 0;
            while ((line = in.readLine()) != null) {
                i++;
                out.println(i+". "+line);
//                switch (line) {
//                    case "hello" -> out.println("1. hello");
//                    case "world" -> out.println("2. world");
//                    case "你好" -> out.println("3. 你好");
//                    case "吗？" -> out.println("4. 吗？");
//                }
            }
        } catch (IOException e) {
            System.err.println("TCP连接错误！");
        }
    }

    public static void main(String[] args) throws IOException {
        EchoServer echoServer = new EchoServer();
        new Thread(echoServer).run();
    }
}