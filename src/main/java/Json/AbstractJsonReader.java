package Json;

import Util.HttpClientUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.IOException;

public abstract class AbstractJsonReader {
    private static final Logger log = Logger.getLogger(JsonPageReader.class);

    public String url;
    public JsonNode results = null;

    public AbstractJsonReader(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public JsonNode rootNode(String urlStr) {
        JsonNode rootNode = null;
        String content = HttpClientUtil.getFromUrl(urlStr);
        if (!content.isEmpty() && !content.equals("")) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                rootNode = mapper.readValue(content, JsonNode.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rootNode;
    }

    public boolean rootNodeIsOK(JsonNode rootNode) {
        if (rootNode != null) {
            final String status = rootNode.get("success").asText();
            if (status == "1") {
               return true;
            } else {
                log.error(rootNode.get("error").asText());
                return false;
            }
        } else {
            log.error("NULL responce " + url);
            return false;
        }
    }
}
