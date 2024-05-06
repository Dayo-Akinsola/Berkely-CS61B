package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.StagedFile.StagingType.ADDITION;
import static gitlet.StagedFile.StagingType.REMOVAL;
import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 *  does at a high level.
 *
 * @author Dayo Akinsola
 */
public class Repository {
    /**
     *
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

    public static void init() throws IOException {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        } else {
            final Boolean gitletDirectoryCreated = GITLET_DIR.mkdir();
            final Boolean commitsDirectoryCreated = COMMITS_DIR.mkdir();
            final Boolean headFileCreated = HEAD.createNewFile();
            final Boolean branchesDirectoryCreated = BRANCHES.mkdir();
            final Boolean stagingDirectoryCreated = STAGING_AREA.mkdir();
            final Boolean blobsDirectoryCreated = BLOBS_DIR.mkdir();

            if (gitletDirectoryCreated && commitsDirectoryCreated && headFileCreated && branchesDirectoryCreated && stagingDirectoryCreated && blobsDirectoryCreated) {
                final Date epochDate = new Date(0, Calendar.JANUARY, 1);
                final Commit initialCommit = new Commit("initial commit", epochDate, new TreeMap<>(), null);
                final String commitHash = saveCommit(initialCommit);
                createBranch("master", commitHash);
                writeContents(HEAD, "master");
            }
        }
    }


    private static void createBranch(final String name, final String commitHash) throws IOException {
        final File branchFile = join(BRANCHES, name);
        if (branchFile.createNewFile()) {
            writeContents(branchFile, commitHash);
        }
    }

    public static void add(final String fileName) throws IOException {
        final File fileToStage = join(CWD, fileName);
        if (!fileToStage.exists()) {
            System.out.println("File does not exists.");
            System.exit(0);
        }
        final String fileContents = readContentsAsString(fileToStage);
        final String fileBlob = sha1(fileContents);
        final Commit headCommit = getHeadCommit();

        final File stagedFile = join(STAGING_AREA, fileName);
        if (!headCommit.getFileNameToBlobMap().containsKey(fileName) || !headCommit.getFileNameToBlobMap().get(fileName).equals(fileBlob)) {

            if (!stagedFile.exists()) {
                stagedFile.createNewFile();
            }

            final StagedFile stagedFileObject = new StagedFile(fileName, fileContents, ADDITION);
            writeObject(stagedFile, stagedFileObject);
        } else if (stagedFile.exists()) {
            stagedFile.delete();
        }
    }

    public static void remove(final String fileName) throws IOException {
        final File stagedFile = join(STAGING_AREA, fileName);
        final Commit headCommit = getHeadCommit();

        if (!stagedFile.exists() && !headCommit.getFileNameToBlobMap().containsKey(fileName)) {
            System.out.println("No reason to remove the file.");
            return;
        }

        if (stagedFile.exists()) {
            final StagedFile file = readObject(stagedFile, StagedFile.class);
            if (file.getStagingType().equals(ADDITION)) {
                stagedFile.delete();
            }
        }

        if (headCommit.getFileNameToBlobMap().containsKey(fileName)) {
            final File fileToStage = join(CWD, fileName);

            stagedFile.createNewFile();

            if (fileToStage.exists()) {
                final String fileContents = readContentsAsString(fileToStage);
                final StagedFile fileToStageObject = new StagedFile(fileName, fileContents, REMOVAL);
                writeObject(stagedFile, fileToStageObject);
                restrictedDelete(fileToStage);
            } else {
                final StagedFile fileToStageObject = new StagedFile(fileName, null, REMOVAL);
                writeObject(stagedFile, fileToStageObject);
            }
        }
    }

    public static void commit(final String commitMessage) throws IOException {
        final List<String> fileNamesInStagingArea = plainFilenamesIn(STAGING_AREA);
        if (fileNamesInStagingArea == null || fileNamesInStagingArea.size() == 0) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        final Commit currentHeadCommit = getHeadCommit();
        final String currentHeadCommitHash = sha1(serialize(currentHeadCommit));
        final Commit newCommit = new Commit(commitMessage, new Date(), currentHeadCommit.getFileNameToBlobMap(), currentHeadCommitHash);

        for (final String fileName : fileNamesInStagingArea) {
            final File stagedFile = join(STAGING_AREA, fileName);
            final StagedFile stagedFileObject = readObject(stagedFile, StagedFile.class);

            if (stagedFileObject.getStagingType().equals(ADDITION)) {
                final String fileBlob = sha1(stagedFileObject.getFileContents());
                final File blobFile = join(BLOBS_DIR, fileBlob);
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
        final Commit headCommit = getHeadCommit();
        printCommitLog(headCommit);
    }

    public static void globalLog() throws ParseException {
        final List<String> allCommitFileNames = plainFilenamesIn(COMMITS_DIR);

        assert allCommitFileNames != null;
        for (String commitFileName : allCommitFileNames) {
            final Commit commit = getCommitFromFile(commitFileName);
            printCommitLogContents(commit);
        }
    }

    public static void find(final String commitMessage) {
        final List<String> allCommitFileNames = plainFilenamesIn(COMMITS_DIR);
        boolean commitMatchingMessageFound = false;

        assert allCommitFileNames != null;
        for (String commitFileName : allCommitFileNames) {
            final Commit commit = getCommitFromFile(commitFileName);
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

        final List<String> stagingAreaFiles = plainFilenamesIn(STAGING_AREA);
        assert stagingAreaFiles != null;
        stagingAreaFiles.sort(Comparator.naturalOrder());
        printStagingArea("Staged Files", stagingAreaFiles, ADDITION);
        printStagingArea("Removed Files", stagingAreaFiles, REMOVAL);
        printUnStagedModifications();
        printUntrackedFiles();
    }

    public static void checkoutFile(final String fileName) throws IOException {
        final Commit headCommit = getHeadCommit();

        writeFileToCwd(headCommit, fileName);
    }

    public static void checkoutCommitFile(final String commitHash, final String fileName) throws IOException {
        final File commit = join(COMMITS_DIR, commitHash);
        if (commit.exists()) {
            final Commit commitObject = readObject(commit, Commit.class);
            writeFileToCwd(commitObject, fileName);
        } else if (commitHash.length() < 40) {
            final List<String> allCommitHashes = plainFilenamesIn(COMMITS_DIR);
            for (String fileCommitHash: allCommitHashes) {
                if (fileCommitHash.startsWith(commitHash)) {
                    final File commitWithFullHash = join(COMMITS_DIR, fileCommitHash);
                    final Commit commitObject = readObject(commitWithFullHash, Commit.class);
                    writeFileToCwd(commitObject, fileName);
                }
            }
        } else {
            printMessageAndExit("No commit with that id exists.");
        }
    }

    public static void checkoutBranch(final String branchName) {
        final File branchFile = join(BRANCHES, branchName);
        if (!branchFile.exists()) {
            printMessageAndExit("No such branch exists.");
        } else if (readContentsAsString(join(HEAD)).equals(branchName)) {
            printMessageAndExit("No need to checkout the current branch.");
        }

        final List<String> cwdFiles = plainFilenamesIn(CWD);
        final Commit headCommit = getHeadCommit();
        for (String cwdFile : cwdFiles) {
            final File fileInStagingArea = join(STAGING_AREA, cwdFile);

            if (!fileInStagingArea.exists() && !headCommit.getFileNameToBlobMap().containsKey(cwdFile)) {
                printMessageAndExit("There is an untracked file in the way; delete it, or add and commit it first.");
            } else if (fileInStagingArea.exists()) {
                final StagedFile fileInStagingAreaObject = readObject(fileInStagingArea, StagedFile.class);
                if (fileInStagingAreaObject.getStagingType().equals(REMOVAL)) {
                    printMessageAndExit("There is an untracked file in the way; delete it, or add and commit it first.");
                }
            }
        }

        for (String cwdFile: cwdFiles) {
            restrictedDelete(cwdFile);
        }
        final String branchCommitHash = readContentsAsString(branchFile);
        final Commit branchCommit = readObject(join(COMMITS_DIR, branchCommitHash), Commit.class);

        for (Map.Entry<String, String> fileToBlob: branchCommit.getFileNameToBlobMap().entrySet()) {
            final String fileName = fileToBlob.getKey();
            final String fileBlob = fileToBlob.getValue();
            final String fileContents = readContentsAsString(join(BLOBS_DIR, fileBlob));
            writeContents(join(CWD, fileName), fileContents);
        }

        for (String stagingAreaFileName : Objects.requireNonNull(plainFilenamesIn(STAGING_AREA))) {
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
            final File fileVersionInCwd = join(CWD, fileName);
            if (!fileVersionInCwd.exists()) {
                fileVersionInCwd.createNewFile();
            }
            final File blobFile = join(BLOBS_DIR, commit.getFileNameToBlobMap().get(fileName));
            final String fileContents = readContentsAsString(blobFile);
            writeContents(fileVersionInCwd, fileContents);
        }
    }

    private static void printStatusHeader(final String headerName) {
        System.out.printf("=== %s ===%n", headerName);
    }

    private static void printUntrackedFiles() {
        printStatusHeader("Untracked Files");

        final List<String> cwdFileNames = plainFilenamesIn(CWD);
        final Commit headCommit = getHeadCommit();

        assert cwdFileNames != null;
        for (String fileName : cwdFileNames) {
            final File fileInStagingArea = join(STAGING_AREA, fileName);

            if (!fileInStagingArea.exists() && !headCommit.getFileNameToBlobMap().containsKey(fileName)) {
                System.out.println(fileName);
            } else if (fileInStagingArea.exists()) {
                final StagedFile fileInStagingAreaObject = readObject(fileInStagingArea, StagedFile.class);
                if (fileInStagingAreaObject.getStagingType().equals(REMOVAL)) {
                    System.out.println(fileName);
                }
            }
        }

        System.out.println();
    }

    private static void printUnStagedModifications() {
        printStatusHeader("Modifications Not Staged For Commit");
        final List<String> cwdFileNames = plainFilenamesIn(CWD);
        final TreeMap<String, String> headCommitFileNameToBlobMap = getHeadCommit().getFileNameToBlobMap();
        for (String fileName : cwdFileNames) {
            final String fileBlob = sha1(readContentsAsString(join(CWD, fileName)));
            final File stagedVersionOfFile = join(STAGING_AREA, fileName);
            if (headCommitFileNameToBlobMap.containsKey(fileName) && !headCommitFileNameToBlobMap.get(fileName).equals(fileBlob)) {
                if (stagedVersionOfFile.exists()) {
                    final StagedFile stagedVersionOfFileObject = readObject(stagedVersionOfFile, StagedFile.class);
                    if ((stagedVersionOfFileObject.getStagingType().equals(ADDITION) && !sha1(stagedVersionOfFileObject.getFileContents()).equals(fileBlob))) {
                        System.out.println(fileName + " (modified)");
                    }
                } else {
                    System.out.println(fileName + " (modified)");
                }


            }
        }

        for (String fileName : Objects.requireNonNull(plainFilenamesIn(STAGING_AREA))) {
            if (readObject(join(STAGING_AREA, fileName), StagedFile.class).getStagingType().equals(ADDITION) && !join(CWD, fileName).exists()) {
                System.out.println(fileName + " (deleted)");
            }
        }

        for (String fileName : headCommitFileNameToBlobMap.keySet()) {
            if (!join(STAGING_AREA, fileName).exists() && !join(CWD, fileName).exists()) {
                System.out.println(fileName + " (deleted)");
            }
        }
        System.out.println();

    }

    private static void printStagingArea(final String headerName, final List<String> sortedStagingAreaFileNames, final StagedFile.StagingType stagingType) {
        printStatusHeader(headerName);
        for (String fileName : sortedStagingAreaFileNames) {
            final StagedFile stagedFileObject = readObject(join(STAGING_AREA, fileName), StagedFile.class);
            if (stagedFileObject.getStagingType().equals(stagingType)) {
                System.out.println(stagedFileObject.getFileName());
            }
        }
        System.out.println();
    }

    private static void printBranches() {
        printStatusHeader("Branches");
        final List<String> branchFileNames = plainFilenamesIn(BRANCHES);
        assert branchFileNames != null;
        branchFileNames.sort(Comparator.naturalOrder());
        final String headBranch = readContentsAsString(HEAD);

        for (String branchFileName : branchFileNames) {
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

        final String parentCommitHash = commit.getParentCommitHash();

        if (parentCommitHash != null) {
            final File commitFile = join(COMMITS_DIR, parentCommitHash);
            final Commit parentCommit = readObject(commitFile, Commit.class);
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
        final SimpleDateFormat formatter = new SimpleDateFormat("E MMM d HH:mm:ss yyyy ZZZZ");
        return formatter.format(date);
    }

    private static Commit getHeadCommit() {
        final String headBranch = readContentsAsString(HEAD);
        final String headCommitHash = readContentsAsString(join(BRANCHES, headBranch));
        return readObject(join(COMMITS_DIR, headCommitHash), Commit.class);
    }

    private static String saveCommit(final Commit commit) throws IOException {
        final String commitHash = sha1(serialize(commit));
        final File commitFile = join(COMMITS_DIR, commitHash);
        final boolean commitFileCreated = commitFile.createNewFile();

        if (commitFileCreated) {
            writeObject(commitFile, commit);
        }

        return commitHash;
    }

    private static void addCommitToBranch(final String branchName, final Commit commit) {
        writeContents(join(BRANCHES, branchName), sha1(serialize(commit)));
    }

    private static Commit getCommitFromFile(final String commitFileName) {
        final File commitFile = join(COMMITS_DIR, commitFileName);
        return readObject(commitFile, Commit.class);
    }

    public static void isInGitletDirectory() {
        if (GITLET_DIR.exists()) {
            return;
        }

        printMessageAndExit("Not in an initialized Gitlet directory.");
    }
}