package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static gitlet.StagedFile.StagingType.ADDITION;
import static gitlet.StagedFile.StagingType.REMOVAL;
import static gitlet.Utils.*;


// TODO: any imports you need here

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * Directory to stored commits.
     */
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    /**
     * HEAD file pointing to the current commit
     */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    /**
     * Branches directory
     */
    public static final File BRANCHES = join(GITLET_DIR, "branches");
    /**
     * Staging area directory
     */
    public static final File STAGING_AREA = join(GITLET_DIR, "staging");
    /**
     * All file blobs
     */
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");

    /* TODO: fill in the rest of this class. */

    public static void init() throws IOException {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        } else {
            final var gitletDirectoryCreated = GITLET_DIR.mkdir();
            final var commitsDirectoryCreated = COMMITS_DIR.mkdir();
            final var headFileCreated = HEAD.createNewFile();
            final var branchesDirectoryCreated = BRANCHES.mkdir();
            final var stagingDirectoryCreated = STAGING_AREA.mkdir();
            final var blobsDirectoryCreated = BLOBS_DIR.mkdir();

            if (gitletDirectoryCreated && commitsDirectoryCreated && headFileCreated && branchesDirectoryCreated && stagingDirectoryCreated && blobsDirectoryCreated) {
                final var epochDate = new Date(0, Calendar.JANUARY, 1);
                final var initialCommit = new Commit("initial commit", epochDate, new TreeMap<>(), null);
                final var commitHash = saveCommit(initialCommit);
                createBranch("master", commitHash);
                writeContents(HEAD, "master");
            }
        }
    }


    private static void createBranch(final String name, final String commitHash) throws IOException {
        final var branchFile = join(BRANCHES, name);
        if (branchFile.createNewFile()) {
            writeContents(branchFile, commitHash);
        }
    }

    public static void add(final String fileName) throws IOException {
        final var fileToStage = join(CWD, fileName);
        if (!fileToStage.exists()) {
            System.out.println("File does not exists.");
            System.exit(0);
        }
        final var fileContents = readContentsAsString(fileToStage);
        final var fileBlob = sha1(fileContents);
        final var headCommit = getHeadCommit();

        final var stagedFile = join(STAGING_AREA, fileName);
        if (!headCommit.getFileNameToBlobMap().containsKey(fileName) || !headCommit.getFileNameToBlobMap().get(fileName).equals(fileBlob)) {

            if (!stagedFile.exists()) {
                stagedFile.createNewFile();
            }

            final var stagedFileObject = new StagedFile(fileName, fileContents, ADDITION);
            writeObject(stagedFile, stagedFileObject);
        } else if (stagedFile.exists()) {
            stagedFile.delete();
        }
    }

    public static void remove(final String fileName) throws IOException {
        final var stagedFile = join(STAGING_AREA, fileName);
        final var headCommit = getHeadCommit();

        if (!stagedFile.exists() && !headCommit.getFileNameToBlobMap().containsKey(fileName)) {
            System.out.println("No reason to remove the file.");
            return;
        }

        if (stagedFile.exists()) {
            final var file = readObject(stagedFile, StagedFile.class);
            if (file.getStagingType().equals(ADDITION)) {
                stagedFile.delete();
            }
        }

        if (headCommit.getFileNameToBlobMap().containsKey(fileName)) {
            final var fileToStage = join(CWD, fileName);

            stagedFile.createNewFile();

            if (fileToStage.exists()) {
                final var fileContents = readContentsAsString(fileToStage);
                final var fileToStageObject = new StagedFile(fileName, fileContents, REMOVAL);
                writeObject(stagedFile, fileToStageObject);
                restrictedDelete(fileToStage);
            } else {
                final var fileToStageObject = new StagedFile(fileName, null, REMOVAL);
                writeObject(stagedFile, fileToStageObject);
            }
        }
    }

    public static void commit(final String commitMessage) throws IOException {
        final var fileNamesInStagingArea = plainFilenamesIn(STAGING_AREA);
        if (fileNamesInStagingArea == null || fileNamesInStagingArea.size() == 0) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        final var currentHeadCommit = getHeadCommit();
        final var currentHeadCommitHash = sha1(serialize(currentHeadCommit));
        final var newCommit = new Commit(commitMessage, new Date(), currentHeadCommit.getFileNameToBlobMap(), currentHeadCommitHash);

        for (final var fileName : fileNamesInStagingArea) {
            final var stagedFile = join(STAGING_AREA, fileName);
            final var stagedFileObject = readObject(stagedFile, StagedFile.class);

            if (stagedFileObject.getStagingType().equals(ADDITION)) {
                final var fileBlob = sha1(stagedFileObject.getFileContents());
                final var blobFile = join(BLOBS_DIR, fileBlob);
                writeContents(blobFile, stagedFileObject.getFileContents());
                newCommit.putFileNameToBlobMap(fileName, fileBlob);
            } else if (stagedFileObject.getStagingType().equals(REMOVAL)) {
                newCommit.removeFileNameToBlobMapping(fileName);
            }
            stagedFile.delete();
        }
        saveCommit(newCommit);
        addCommitToBranch("master", newCommit);
    }

    public static void log() throws ParseException {
        final var headCommit = getHeadCommit();
        printCommitLog(headCommit);
    }

    public static void globalLog() throws ParseException {
        final var allCommitFileNames = plainFilenamesIn(COMMITS_DIR);

        assert allCommitFileNames != null;
        for (var commitFileName : allCommitFileNames) {
            final var commit = getCommitFromFile(commitFileName);
            printCommitLogContents(commit);
        }
    }

    public static void find(final String commitMessage) {
        final var allCommitFileNames = plainFilenamesIn(COMMITS_DIR);
        var commitMatchingMessageFound = false;

        assert allCommitFileNames != null;
        for (var commitFileName : allCommitFileNames) {
            final var commit = getCommitFromFile(commitFileName);
            if (commit.getMessage().equals(commitMessage)) {
                System.out.println(commitFileName);
                commitMatchingMessageFound = true;
            }
        }

        if (!commitMatchingMessageFound) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        printBranches();

        final var stagingAreaFiles = plainFilenamesIn(STAGING_AREA);
        assert stagingAreaFiles != null;
        stagingAreaFiles.sort(Comparator.naturalOrder());
        printStagingArea("Staged Files", stagingAreaFiles, ADDITION);
        printStagingArea("Removed Files", stagingAreaFiles, REMOVAL);
        printUnStagedModifications();
        printUntrackedFiles();
    }

    public static void checkoutFile(final String fileName) throws IOException {
        final var headCommit = getHeadCommit();

        writeFileToCwd(headCommit, fileName);
    }

    public static void checkoutCommitFile(final String commitHash, final String fileName) throws IOException {
        final var commit = join(COMMITS_DIR, commitHash);
        if (commit.exists()) {
            final var commitObject = readObject(commit, Commit.class);
            writeFileToCwd(commitObject, fileName);
        } else if (commitHash.length() < 40) {
            final var allCommitHashes = plainFilenamesIn(COMMITS_DIR);
            for (var fileCommitHash: allCommitHashes) {
                if (fileCommitHash.startsWith(commitHash)) {
                    final var commitWithFullHash = join(COMMITS_DIR, fileCommitHash);
                    final var commitObject = readObject(commitWithFullHash, Commit.class);
                    writeFileToCwd(commitObject, fileName);
                }
            }
        } else {
            printMessageAndExit("No commit with that id exists.");
        }
    }

    public static void checkoutBranch(final String branchName) {
        final var branchFile = join(BRANCHES, branchName);
        if (!branchFile.exists()) {
            printMessageAndExit("No such branch exists.");
        } else if (readContentsAsString(join(HEAD)).equals(branchName)) {
            printMessageAndExit("No need to checkout the current branch.");
        }

        final var cwdFiles = plainFilenamesIn(CWD);
        final var headCommit = getHeadCommit();
        for (var cwdFile : cwdFiles) {
            final var fileInStagingArea = join(STAGING_AREA, cwdFile);

            if (!fileInStagingArea.exists() && !headCommit.getFileNameToBlobMap().containsKey(cwdFile)) {
                printMessageAndExit("There is an untracked file in the way; delete it, or add and commit it first.");
            } else if (fileInStagingArea.exists()) {
                final var fileInStagingAreaObject = readObject(fileInStagingArea, StagedFile.class);
                if (fileInStagingAreaObject.getStagingType().equals(REMOVAL)) {
                    printMessageAndExit("There is an untracked file in the way; delete it, or add and commit it first.");
                }
            }
        }

        for (var cwdFile: cwdFiles) {
            restrictedDelete(cwdFile);
        }
        final var branchCommitHash = readContentsAsString(branchFile);
        final var branchCommit = readObject(join(COMMITS_DIR, branchCommitHash), Commit.class);

        for (var fileToBlob: branchCommit.getFileNameToBlobMap().entrySet()) {
            final var fileName = fileToBlob.getKey();
            final var fileBlob = fileToBlob.getValue();
            final var fileContents = readContentsAsString(join(BLOBS_DIR, fileBlob));
            writeContents(join(CWD, fileName), fileContents);
        }

        for (var stagingAreaFileName : Objects.requireNonNull(plainFilenamesIn(STAGING_AREA))) {
            join(STAGING_AREA, stagingAreaFileName).delete();
        }
        writeContents(HEAD, branchName);
    }

    public static void branch(final String branchName) {

    }

    private static void writeFileToCwd(final Commit commit, final String fileName) throws IOException {
        if (!commit.getFileNameToBlobMap().containsKey(fileName)) {
            printMessageAndExit("File does not exist in that commit.");
        } else {
            final var fileVersionInCwd = join(CWD, fileName);
            if (!fileVersionInCwd.exists()) {
                fileVersionInCwd.createNewFile();
            }
            final var blobFile = join(BLOBS_DIR, commit.getFileNameToBlobMap().get(fileName));
            final var fileContents = readContentsAsString(blobFile);
            writeContents(fileVersionInCwd, fileContents);
        }
    }

    private static void printStatusHeader(final String headerName) {
        System.out.printf("=== %s ===%n", headerName);
    }

    private static void printUntrackedFiles() {
        printStatusHeader("Untracked Files");

        final var cwdFileNames = plainFilenamesIn(CWD);
        final var headCommit = getHeadCommit();

        assert cwdFileNames != null;
        for (var fileName : cwdFileNames) {
            final var fileInStagingArea = join(STAGING_AREA, fileName);

            if (!fileInStagingArea.exists() && !headCommit.getFileNameToBlobMap().containsKey(fileName)) {
                System.out.println(fileName);
            } else if (fileInStagingArea.exists()) {
                final var fileInStagingAreaObject = readObject(fileInStagingArea, StagedFile.class);
                if (fileInStagingAreaObject.getStagingType().equals(REMOVAL)) {
                    System.out.println(fileName);
                }
            }
        }

        System.out.println();
    }

    private static void printUnStagedModifications() {
        printStatusHeader("Modifications Not Staged For Commit");
        final var cwdFileNames = plainFilenamesIn(CWD);
        final var headCommitFileNameToBlobMap = getHeadCommit().getFileNameToBlobMap();
        for (var fileName : cwdFileNames) {
            final var fileBlob = sha1(readContentsAsString(join(CWD, fileName)));
            final var stagedVersionOfFile = join(STAGING_AREA, fileName);
            if (headCommitFileNameToBlobMap.containsKey(fileName) && !headCommitFileNameToBlobMap.get(fileName).equals(fileBlob)) {
                if (stagedVersionOfFile.exists()) {
                    final var stagedVersionOfFileObject = readObject(stagedVersionOfFile, StagedFile.class);
                    if ((stagedVersionOfFileObject.getStagingType().equals(ADDITION) && !sha1(stagedVersionOfFileObject.getFileContents()).equals(fileBlob))) {
                        System.out.println(fileName + " (modified)");
                    }
                } else {
                    System.out.println(fileName + " (modified)");
                }


            }
        }

        for (var fileName : Objects.requireNonNull(plainFilenamesIn(STAGING_AREA))) {
            if (readObject(join(STAGING_AREA, fileName), StagedFile.class).getStagingType().equals(ADDITION) && !join(CWD, fileName).exists()) {
                System.out.println(fileName + " (deleted)");
            }
        }

        for (var fileName : headCommitFileNameToBlobMap.keySet()) {
            if (!join(STAGING_AREA, fileName).exists() && !join(CWD, fileName).exists()) {
                System.out.println(fileName + " (deleted)");
            }
        }
        System.out.println();

    }

    private static void printStagingArea(final String headerName, final List<String> sortedStagingAreaFileNames, final StagedFile.StagingType stagingType) {
        printStatusHeader(headerName);
        for (var fileName : sortedStagingAreaFileNames) {
            final var stagedFileObject = readObject(join(STAGING_AREA, fileName), StagedFile.class);
            if (stagedFileObject.getStagingType().equals(stagingType)) {
                System.out.println(stagedFileObject.getFileName());
            }
        }
        System.out.println();
    }

    private static void printBranches() {
        printStatusHeader("Branches");
        final var branchFileNames = plainFilenamesIn(BRANCHES);
        assert branchFileNames != null;
        branchFileNames.sort(Comparator.naturalOrder());
        final var headBranch = readContentsAsString(HEAD);

        for (var branchFileName : branchFileNames) {
            if (headBranch.equals(branchFileName)) {
                System.out.println("*" + branchFileName);
            } else {
                System.out.println(branchFileName);
            }
        }
        System.out.println();
    }

    private static void printCommitLog(final Commit commit) throws ParseException {
        printCommitLogContents(commit);

        final var parentCommitHash = commit.getParentCommitHash();

        if (parentCommitHash != null) {
            final var commitFile = join(COMMITS_DIR, parentCommitHash);
            final var parentCommit = readObject(commitFile, Commit.class);
            printCommitLog(parentCommit);
        }
    }

    private static void printCommitLogContents(final Commit commit) throws ParseException {
        System.out.println("===");
        System.out.println("commit " + sha1(serialize(commit)));
        System.out.println("Date: " + formatTime(commit.getTimestamp()));
        System.out.println(commit.getMessage());
        System.out.println();
    }

    private static String formatTime(final Date date) throws ParseException {
        final var formatter = new SimpleDateFormat("E MMM d HH:mm:ss yyyy ZZZZ");
        return formatter.format(date);
    }

    private static Commit getHeadCommit() {
        final var headBranch = readContentsAsString(HEAD);
        final var headCommitHash = readContentsAsString(join(BRANCHES, headBranch));
        return readObject(join(COMMITS_DIR, headCommitHash), Commit.class);
    }

    private static String saveCommit(final Commit commit) throws IOException {
        final var commitHash = sha1(serialize(commit));
        final var commitFile = join(COMMITS_DIR, commitHash);
        final var commitFileCreated = commitFile.createNewFile();

        if (commitFileCreated) {
            writeObject(commitFile, commit);
        }

        return commitHash;
    }

    private static void addCommitToBranch(final String branchName, final Commit commit) {
        writeContents(join(BRANCHES, branchName), sha1(serialize(commit)));
    }

    private static Commit getCommitFromFile(final String commitFileName) {
        final var commitFile = join(COMMITS_DIR, commitFileName);
        return readObject(commitFile, Commit.class);
    }
}
