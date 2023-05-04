package a4;

import com.jogamp.opengl.GL4;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static com.jogamp.opengl.GL.*;

/**
 * A model imported from an external OBJ file.
 */
public class ImportedModel extends Model {

    private float[] vertices;
    private float[] texCoords;
    private float[] normals;
    private float[] tangents;
    private int[] texUnitIndices;
    private boolean hasMaterials = false;
    private final Map<String,Integer> matNameToTexUnitMap = new HashMap<String,Integer>();
    private final List<Integer> materialTextureIDs = new ArrayList<Integer>();

    public ImportedModel(String filename) {
        try {
            tryLoadMaterial(filename);
        }
        catch(Exception e) {
            System.err.println("Failed to load material for `" + filename + "`");
            e.printStackTrace();
        }

        try {
            parseOBJ(filename);
        } catch(Exception e) {
            System.err.println("Failed to load model from `" + filename + "`");
            e.printStackTrace();
        }
    }

    private void tryLoadMaterial(String filename) throws IOException {
        String materialFilename = filename.substring(0, filename.lastIndexOf('.')) + ".mtl";
        InputStream inStream = ImportedModel.class.getResourceAsStream(materialFilename);
        if(inStream == null) return;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        String currentMaterialName = "";

        String line;
        while((line = reader.readLine()) != null) {
            String[] directiveThenArgument = line.split(" ", 2);
            if(directiveThenArgument.length < 2) continue;
            String directive = directiveThenArgument[0];
            String argument = directiveThenArgument[1];

            switch(directive) {
                case "newmtl":
                    currentMaterialName = argument;
                    break;
                case "map_Kd":
                    int textureID = TextureManager.getInstance().getTexture(argument);
                    materialTextureIDs.add(textureID);
                    matNameToTexUnitMap.put(currentMaterialName, materialTextureIDs.size() - 1);
                    break;
                default:
                    System.out.println("Ignored line line in `" + filename + "`: " + line);
            }
        }

        hasMaterials = true;
	}

    private void parseOBJ(String filename) throws IOException {
        // Data will be temporarily stored in lists since we don't know how many to expect
        List<Integer> vertIndices = new Vector<>();
        List<Vector3f> vertices = new Vector<>();
        List<Vector2f> texCoords = new Vector<>();
        List<Vector3f> normals = new Vector<>();
        List<Integer> texUnitIndices = new Vector<>();

        // Face definitions refer to shared data that will be stored in these lists
        List<Vector3f> vertVals = new Vector<>();
        List<Vector2f> tcVals = new Vector<>();
        List<Vector3f> normVals = new Vector<>();

        // Allocations for tangent calculation
        Map<Integer, Vector4f> intermediateTangents = new HashMap<>();
        Vector3f tangent = new Vector3f();
        Vector3f D = new Vector3f();
        Vector3f E = new Vector3f();
        Vector2f F = new Vector2f();
        Vector2f G = new Vector2f();
        int[] triIndices = new int[3];
        Vector3f[] triPoints = new Vector3f[3];
        Vector2f[] triTCs = new Vector2f[3];

        int textureUnit = -1;
        InputStream inStream = ImportedModel.class.getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        String line;

        while((line = reader.readLine()) != null) {
            String[] directiveThenArgument = line.split(" ", 2);
            if(directiveThenArgument.length < 2) continue;
            String directive = directiveThenArgument[0];
            String argument = directiveThenArgument[1];
            String[] values;

            switch(directive) {
                case "v": // vertex position
                    values = argument.split(" ");
                    Vector3f vert = new Vector3f(
                        Float.parseFloat(values[0]),
                        Float.parseFloat(values[1]),
                        Float.parseFloat(values[2]));
                    vertVals.add(vert);
                    break;
                case "vt": // tex coords
                    values = argument.split(" ");
                    Vector2f tc = new Vector2f(
                            Float.parseFloat(values[0]),
                            Float.parseFloat(values[1]));
                    tcVals.add(tc);
                    break;
                case "vn": // normal
                    values = argument.split(" ");
                    Vector3f norm = new Vector3f(
                            Float.parseFloat(values[0]),
                            Float.parseFloat(values[1]),
                            Float.parseFloat(values[2]));
                    normVals.add(norm);
                    break;
                case "usemtl": // select material
                    textureUnit = matNameToTexUnitMap.getOrDefault(argument, -1);
                    break;
                case "f": // face
                    values = argument.split(" ");

                    for(int i = 0; i < 3; i++) {
                        String[] indices = values[i].split("/");

                        if(indices.length > 0) {
                            int idx = Integer.parseInt(indices[0]) - 1;
                            vertIndices.add(idx);
                            triIndices[i] = idx;
                            triPoints[i] = new Vector3f(vertVals.get(idx));
                            vertices.add(triPoints[i]);
                        }
                        if(indices.length > 1) {
                            int idx = Integer.parseInt(indices[1]) - 1;
                            triTCs[i] = new Vector2f(tcVals.get(idx));
                            texCoords.add(triTCs[i]);
                        }
                        if(indices.length > 2) {
                            int idx = Integer.parseInt(indices[2]) - 1;
                            normals.add(new Vector3f(normVals.get(idx)));
                        }
                        if(textureUnit != -1) {
                            texUnitIndices.add(textureUnit);
                        }
                    }

                    // Calculate tangent vector
                    // Based on datenwolf's method described here: https://stackoverflow.com/a/5257471
                    D.set(triPoints[1]).sub(triPoints[0]);
                    E.set(triPoints[2]).sub(triPoints[0]);
                    F.set(triTCs[1]).sub(triTCs[0]);
                    G.set(triTCs[2]).sub(triTCs[0]);
                    tangent.set(
                        G.y * D.x - F.y * E.x,
                        G.y * D.y - F.y * E.y,
                        G.y * D.z - F.y * E.z);
                    tangent.mul(1f / (F.x * G.y - F.y * G.x));
                    tangent.normalize();

                    // Average new tangent vector with previous tangents calculated for this vertex
                    for(int i = 0; i < 3; i++) {
                        if(!intermediateTangents.containsKey(triIndices[i])) {
                            intermediateTangents.put(triIndices[i], new Vector4f(tangent.x, tangent.y, tangent.z, 1f));
                        }
                        else {
                            Vector4f tangentData = intermediateTangents.get(triIndices[i]);

                            float oldCount = tangentData.w;
                            float newCount = oldCount + 1;
                            tangentData.w = 0f;

                            tangentData.mul(oldCount / newCount);
                            tangentData.x += tangent.x / newCount;
                            tangentData.y += tangent.y / newCount;
                            tangentData.z += tangent.z / newCount;
                            tangentData.normalize();

                            tangentData.w = newCount;
                        }
                    }
                    break;
                default:
                    System.out.println("Ignored line in `" + filename + "`: " + line);
            }
        }

        inStream.close();

        // Convert lists to arrays
        this.vertices = ConvertUtil.toFloatArray(vertices.toArray(new Vector3f[0]));
        this.texCoords = ConvertUtil.toFloatArray(texCoords.toArray(new Vector2f[0]));
        this.normals = ConvertUtil.toFloatArray(normals.toArray(new Vector3f[0]));
        this.texUnitIndices = texUnitIndices.stream().mapToInt(Integer::intValue).toArray();

        // Collect the final tangents for each vertex
        this.tangents = new float[this.vertices.length];
        for (int i = 0; i < this.vertices.length / 3; i++) {
            Vector4f tangentData = intermediateTangents.get(vertIndices.get(i));
            this.tangents[i * 3 + 0] = tangentData.x;
            this.tangents[i * 3 + 1] = tangentData.y;
            this.tangents[i * 3 + 2] = tangentData.z;
        }
    }

    @Override
    public void bindTextures(GL4 gl) {
        super.bindTextures(gl);

        for(int i = 0; i < materialTextureIDs.size(); i++) {
            gl.glActiveTexture(GL_TEXTURE0 + i);
            gl.glBindTexture(GL_TEXTURE_2D, materialTextureIDs.get(i));
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        }
    }

    @Override
    public int getNumVertices() {
        return vertices.length / 3;
    }

    @Override
    public float[] getVertices() {
        return vertices;
    }

    @Override
    public float[] getTexCoords() {
        return texCoords;
    }

    @Override
    public float[] getNormals() {
        return normals;
    }

    @Override
    public float[] getTangents() {
        return tangents;
    }

    @Override
	public int[] getTexUnitIndices() {
		return texUnitIndices;
	}

	@Override
    public boolean hasTexCoords() {
        return true;
    }

    @Override
    public boolean hasNormals() {
        return true;
    }

    @Override
    public boolean hasTangents() {
        return true;
    }

    @Override
	public boolean hasTexUnitIndices() {
		return hasMaterials;
	}

}
