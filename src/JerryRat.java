import util.ResponseHeaders;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
                clientSocket.setSoTimeout(10000);

                responseHeaders = new ResponseHeaders();
                String request = in.readLine();
                String[] requestParts = request.trim().split("\\s+");

                File file = condition.getFile(requestParts[1]);
                byte[] entityBody = condition.getEntityBody(file);

                // HTTP/0.9 GET 请求
                if (condition.isSimpleRequest(requestParts)) {
                    out.print(new String(entityBody));
                    out.flush();
                    continue;
                }

                //请求不合法
                if (condition.requestIllegal(requestParts)) {
                    responseHeaders.setStatusLine("400 Bad Request");
                    responseHeaders.setDate(new Date());
                    out.print(responseHeaders.toString());
                    out.flush();
                    continue;
                }

                if (requestParts[0].equalsIgnoreCase("POST")) {
                    if (requestParts[1].startsWith("/emails")) {
                        File directory = new File(WEB_ROOT, "/emails");
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        File file_under_email = new File(WEB_ROOT, requestParts[1]);
                        if (!file_under_email.exists()) {
                            file_under_email.createNewFile();
                        }

                        BufferedWriter writer = new BufferedWriter(new FileWriter(file_under_email));
                        String[] headerLine = in.readLine().trim().split(":");

                        String filed = headerLine[0].trim();
                        int contentLen = Integer.parseInt(headerLine[1].trim());
                        char[] readLimited = new char[contentLen];

                        if (filed.equalsIgnoreCase("Content-Length")) {
                            in.read(readLimited);
                            writer.write(readLimited);
                            writer.close();
                        }
                        responseHeaders.setStatusLine("201 Created");
                        responseHeaders.setDate(new Date());
                        out.print(responseHeaders.toString());
                        out.flush();
                        continue;
                    } else if (requestParts[1].startsWith("/endpoints/null")) {
                        responseHeaders.setStatusLine("204 No Content");
                        responseHeaders.setDate(new Date());
                        out.print(responseHeaders.toString());
                        out.flush();
                        continue;
                    }
                }

                //HTTP/1.0 HEAD 请求
                if (request_head(out, requestParts)) continue;

                //HTTP/1.0 GET请求
                if (request_contain_useragent(out, in, requestParts)) continue;
                request_get(out, requestParts, file, entityBody);

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("TCP连接错误！");
            }
        }
    }

    private boolean request_head(PrintWriter out, String[] requestParts) {
        if (requestParts[0].equalsIgnoreCase("HEAD")) {
            out.print(responseHeaders.toString());
            out.flush();
            return true;
        }
        return false;
    }

    private boolean request_contain_useragent(PrintWriter out, BufferedReader in, String[] requestParts) throws IOException {
        if (requestParts[1].equals("/endpoints/user-agent")) {
            String[] headerLine = in.readLine().split(":");
            String value = headerLine[1].trim();

            responseHeaders.setContentType(condition.getContentType(".txt"));
            responseHeaders.setContentLength(value.length());
            responseHeaders.setLastModified(new Date());
            responseHeaders.setStatusLine("200 OK");

            out.print(responseHeaders.toString() + "\r\n\r\n" + value);
            out.flush();
            return true;
        }
        return false;
    }

    private void request_get(PrintWriter out, String[] requestParts, File file, byte[] entityBody) {
        if (requestParts[0].equalsIgnoreCase("GET")) {
            responseHeaders.setLastModified(new Date(file.lastModified()));
            responseHeaders.setContentLength(entityBody.length);
            responseHeaders.setContentType(condition.getContentType(file.getName()));
            responseHeaders.setDate(new Date());
            responseHeaders.setStatusLine("200 OK");
            out.print(responseHeaders.toString() + "\r\n\r\n" + new String(entityBody));
            out.flush();
        }
    }

    public static void main(String[] args) throws IOException {
        JerryRat jerryRat = new JerryRat();
        new Thread(jerryRat).start();
    }

}
