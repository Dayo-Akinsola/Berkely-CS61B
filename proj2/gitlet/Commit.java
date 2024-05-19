package gitlet;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *
 *  does at a high level.
 *
 *  @author Dayo Akinsola
 */
public class Commit implements Serializable {
    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private final String message;
    /** The time the Commit was created */
    private final Date timestamp;
    /** File name to blob reference mapping */
    private final TreeMap<String, String> fileNameToBlobMap;
    /** The parent commit that this Commit references */
    private final String parentCommitHash;

    public Commit(final String message, final Date timestamp, final TreeMap<String, String> fileNameToBlobMap, final String parentCommitHash) {
        this.message = message;
        this.timestamp = timestamp;
        this.fileNameToBlobMap = fileNameToBlobMap;
        this.parentCommitHash = parentCommitHash;
    }

    public String getMessage() {
        return this.message;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public TreeMap<String, String> getFileNameToBlobMap() {
        return this.fileNameToBlobMap;
    }

    public String getParentCommitHash() {
        return this.parentCommitHash;
    }

    public void putFileNameToBlobMap(final String fileName, final String fileBlob) {
        this.fileNameToBlobMap.put(fileName, fileBlob);
    }

    public void removeFileNameToBlobMapping(final String fileName) {
        this.fileNameToBlobMap.remove(fileName);
    }

}
