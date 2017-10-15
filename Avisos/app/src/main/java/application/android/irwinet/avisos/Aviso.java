package application.android.irwinet.avisos;

/**
 * Created by Irwinet on 15/10/2017.
 */

public class Aviso {
    private int intAvisoId;
    private String varContent;
    private int intImportant;

    public Aviso(int intAvisoId, String varContent, int intImportant) {
        this.intAvisoId = intAvisoId;
        this.varContent = varContent;
        this.intImportant = intImportant;
    }

    public int getIntAvisoId() {
        return intAvisoId;
    }

    public void setIntAvisoId(int intAvisoId) {
        this.intAvisoId = intAvisoId;
    }

    public String getVarContent() {
        return varContent;
    }

    public void setVarContent(String varContent) {
        this.varContent = varContent;
    }

    public int getIntImportant() {
        return intImportant;
    }

    public void setIntImportant(int intImportant) {
        this.intImportant = intImportant;
    }
}
