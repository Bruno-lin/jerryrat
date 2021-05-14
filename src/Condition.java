import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class Condition {
    public Condition() {
    }

    //获取文件
    File getFile(String requestPart) {
        //中文解码
        String encoded = requestPart;
        requestPart = URLDecoder.decode(encoded, StandardCharsets.UTF_8);

        File file = new File(JerryRat.WEB_ROOT + requestPart);
        if (!file.isFile()) {
            file = new File(file + "/index.html");
        }

        return file;
    }

    //请求不合法
    boolean requestIllegal(String[] requestParts) {
        return requestParts.length != 3 ||
                !requestParts[2].equalsIgnoreCase("HTTP/1.0");
    }

    //获取文件的内容类型
    String getContentType(String content) {
        Map<String, String> map = new HashMap<>();
        File file = new File("res/webroot/mime.txt");
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            while (true) {
                String attr = br.readLine();
                if (attr == null) {
                    break;
                }
                String[] attrs = attr.split("\\s+");
                map.put(attrs[0], attrs[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matchType(content, map);
    }

    //匹配且返回对应的类型
    String matchType(String content, Map<String, String> map) {
        String[] name = content.split("\\.");
        String key = name[name.length - 1];
        if (map.get("." + key) == null) {
            return "application/octet-stream";
        }
        return map.get("." + key);
    }

    //获取文件内容
    byte[] getEntityBody(File file) {
        byte[] entityBody;
        try {
            entityBody = Files.readAllBytes(file.toPath());
        } catch (Exception e) {
            e.printStackTrace();
            entityBody = null;
        }
        return entityBody;
    }

    //是否为HTTP 0.9
    boolean isSimpleRequest(String[] requestParts) {
        return requestParts.length == 2 && requestParts[0].equalsIgnoreCase("GET");
    }
}