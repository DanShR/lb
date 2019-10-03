package Json;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonReader extends AbstractJsonReader {

    public JsonReader(String url) {
        super(url);
    }

    public boolean read() {
        JsonNode rootNode = rootNode(url);
        if (rootNodeIsOK(rootNode)) {
            results = rootNode.get("results");
            return true;
        } else {
            return false;
        }

    }
}
