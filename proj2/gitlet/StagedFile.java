package gitlet;

import java.io.Serializable;

public class StagedFile implements Serializable {
    private String fileName;
    private String fileContents;
    private StagingType stagingType;

    public StagedFile(final String fileName, final String fileContents, final StagingType stagingType) {
        this.fileName = fileName;
        this.fileContents = fileContents;
        this.stagingType = stagingType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContents() {
        return fileContents;
    }

    public enum StagingType {
        ADDITION,
        REMOVAL
    }

    public StagingType getStagingType() {
        return stagingType;
    }

    public void setFileContents(String fileContents) {
        this.fileContents = fileContents;
    }

    public void setStagingType(StagingType stagingType) {
        this.stagingType = stagingType;
    }
}
