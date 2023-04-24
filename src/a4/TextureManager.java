package a4;

import java.util.HashMap;
import java.util.Map;

public class TextureManager {

    private static final TextureManager instance = new TextureManager();
    private final Map<String, Integer> textures = new HashMap<String, Integer>();

    private TextureManager() {

    }

    public static TextureManager getInstance() {
        return instance;
    }

    public int getTexture(String path) {
        if (textures.containsKey(path)) {
            return textures.get(path);
        } else {
            int textureID = Utils.loadTexture("a4/assets/" + path);
            textures.put(path, textureID);
            return textureID;
        }
    }

}
