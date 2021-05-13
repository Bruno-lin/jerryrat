import util.ResponseHeaders;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JerryRat implements Runnable {

    public static final String SERVER_PORT = "8080";
    public static final String WEB_ROOT = "res/webroot";
    ServerSocket serverSocket;
    ResponseHeaders responseHeaders;


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
                responseHeaders = new ResponseHeaders();

                String request = in.readLine();
                String[] requestParts = request.split(" ");

                if (requestWrong(requestParts)) {
                    responseHeaders.setStatusLine("400 Bad Request");
                    responseHeaders.setDate(new Date());
                    out.println(responseHeaders.toString());
                }

                File file = getFile(requestParts[1]);
                byte[] entityBody = getEntity(file);

                out.println(responseHeaders.toString());
                out.println(entityBody.toString());

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("TCP连接错误！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private File getFile(String requestPart) {
        File file = new File(WEB_ROOT + requestPart);
        if (!file.isFile()) {
            file = new File(WEB_ROOT + "/index.html");
        }
        return file;
    }

    private byte[] getEntity(File file) {
        byte[] entityBody = null;
        try {
            entityBody = Files.readAllBytes(file.toPath());
            responseHeaders.setLastModified(new Date(file.lastModified()));
            responseHeaders.setContentLength(entityBody.length);
            responseHeaders.setContentType(getContentType(file.getName()));
            responseHeaders.setDate(new Date());
            responseHeaders.setStatusLine("200 OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entityBody;
    }

    private boolean requestWrong(String[] requestParts) {
        return requestParts.length < 3 ||
                !requestParts[0].equalsIgnoreCase("get") ||
                !requestParts[2].equalsIgnoreCase("HTTP/1.0");
    }

    private String getContentType(String content) throws Exception {
        Map<String, String> map = new HashMap<>();
        File file = new File("res/webroot/mime.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));

        while (true) {
            String attr = br.readLine();
            if (attr == null) {
                break;
            }
            String[] attrs = attr.split("\\s+");
            map.put(attrs[0],attrs[1]);
        }
        return getType(content, map);
    }

    private String getType(String content, Map<String, String> map) {
        String[] name = content.split("\\.");
        String key = name[name.length - 1];
        if (map.get("." + key) == null) {
            return "application/octet-stream";
        }
        return map.get("." + key);
    }

    public static void main(String[] args) throws IOException {
        JerryRat jerryRat = new JerryRat();
        new Thread(jerryRat).start();
    }

}
