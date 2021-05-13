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
                    BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream());
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                responseHeaders = new ResponseHeaders();

                String request = in.readLine();
                String[] requestParts = request.split(" ");

                if (requestWrong(requestParts)) {
                    responseHeaders.setStatusLine("400 Bad Request");
                    responseHeaders.setDate(new Date());
                    out.write(responseHeaders.toString().getBytes(StandardCharsets.UTF_8));
                }

                File file = getFile(requestParts[1]);
                byte[] entityBody = getEntity(file);
                responseHeaders.setDate(new Date());

                out.write(responseHeaders.toString().getBytes(StandardCharsets.UTF_8));

                if (entityBody != null) {
                    out.write("\r\n\r\n".getBytes(StandardCharsets.UTF_8));
                    out.write(entityBody);
                }
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

    private byte[] getEntity(File file) throws Exception {
        byte[] entityBody = Files.readAllBytes(file.toPath());
        responseHeaders.setContentLength(String.valueOf(entityBody.length));
        responseHeaders.setLastModified(new Date(file.lastModified()));
        responseHeaders.setContentType(getContentType(file.getName()));
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
            for (int i = 0; i < attrs.length; i += 2) {
                map.put(attrs[i], attrs[i + 1]);
            }
        }
        return getType(content, map);
    }

    private String getType(String content, Map<String, String> map) {
        String[] name = content.split("\\.");
        String key = name[name.length - 1];
        if (!map.containsKey(key)) {
            return map.get(".*");
        }
        return map.get(key);
    }

    public static void main(String[] args) throws IOException {
        JerryRat jerryRat = new JerryRat();
        new Thread(jerryRat).start();
    }
}
