package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
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
    private String message;
    /** The time the Commit was created */
    private Date timestamp;
    /** File name to blob reference mapping */
    private TreeMap<String, String> fileNameToBlobMap;
    /** The parent commit that this Commit references */
    private String parentCommitHash;

    /* TODO: fill in the rest of this class. */

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

    public void setParentCommitHash(final String parentCommitHash) {
        this.parentCommitHash = parentCommitHash;
    }

    public void putFileNameToBlobMap(final String fileName, final String fileBlob) {
        this.fileNameToBlobMap.put(fileName, fileBlob);
    }

    public void removeFileNameToBlobMapping(final String fileName) {
        this.fileNameToBlobMap.remove(fileName);
    }

}