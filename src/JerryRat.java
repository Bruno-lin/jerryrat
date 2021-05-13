import util.ResponseHeaders;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;


public class JerryRat implements Runnable {

    public static final String SERVER_PORT = "8080";
    public static final String WEB_ROOT = "res/webroot";
    //组成
    private final Condition condition = new Condition();
    ServerSocket serverSocket;
    ResponseHeaders responseHeaders;


    public JerryRat() throws IOException {
        serverSocket = new ServerSocket(Integer.parseInt(SERVER_PORT));
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try (
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                responseHeaders = new ResponseHeaders();

                String request = in.readLine();
                String[] requestParts = request.trim().split("\\s+");

                if (condition.requestIllegal(requestParts)) {
                    responseHeaders.setStatusLine("400 Bad Request");
                    responseHeaders.setDate(new Date());
                    out.print(responseHeaders.toString());
                    out.flush();
                    continue;
                }

                if (requestParts[1].equals("/endpoints/user-agent")) {
                    String[] headerLine = in.readLine().split(":");
                    String value = headerLine[1].trim();

                    responseHeaders.setContentType(condition.getContentType(".txt"));
                    responseHeaders.setContentLength(value.getBytes(StandardCharsets.UTF_8).length);
                    responseHeaders.setLastModified(new Date());
                    responseHeaders.setStatusLine("200 OK");

                    out.print(responseHeaders.toString() + "\r\n\r\n" + value);
                    out.flush();
                    continue;
                }

                File file = condition.getFile(requestParts[1]);
                byte[] entityBody = condition.getEntityBody(file, this);

                if (condition.notOldVersion(requestParts[2])) {
                    out.print(responseHeaders.toString() + "\r\n\r\n" + new String(entityBody));
                } else {
                    out.print(new String(entityBody));
                }
                out.flush();
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
