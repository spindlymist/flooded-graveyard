package a4;

import java.util.HashMap;
import java.util.Map;

public class ModelManager {

    private static final ModelManager instance = new ModelManager();
    private final Map<String, Model> models = new HashMap<String, Model>();

    private ModelManager() {
    }

    public static ModelManager getInstance() {
        return instance;
    }

    public Model getModel(String name) {
        return models.getOrDefault(name, null);
    }

    public void addModel(String name, Model model) {
        models.put(name, model);
    }

    public ImportedModel importModel(String name, String path) {
        ImportedModel model = new ImportedModel(path);
        addModel(name, model);

        return model;
    }

}
