package Json;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.log4j.Logger;

public class JsonPageReader extends AbstractJsonReader{
    private static final Logger log = Logger.getLogger(JsonPageReader.class);
    private JsonPage jsonPage = new JsonPage();

    public JsonPageReader(String url) {
        super(url);
    }

    public boolean readNextPage() {
        this.results = null;
        if (!jsonPage.finished()) {
            JsonNode rootNode = rootNode(url + jsonPage.urlPageParam());
            if (rootNodeIsOK(rootNode))
            {
                final JsonNode pager = rootNode.get("pager");
                jsonPage.incPage();
                jsonPage.setPer_page(pager.get("per_page").asInt());
                jsonPage.setTotal(pager.get("total").asInt());
                this.results = rootNode.get("results");
                return true;
            } else {
                return false;
            }
        } else {
            log.info("");
            return false;
        }
    }
}
