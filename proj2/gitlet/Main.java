package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Objects;

import static gitlet.Utils.join;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author Dayo Akinsola
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        } else {
            String firstArg = args[0];
            switch (firstArg) {
                case "init":
                    Repository.init();
                    break;
                case "add":
                    if (args.length != 2) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    Repository.add(args[1]);
                    break;
                case "commit":
                    if (args.length != 2) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    final String commitMessage = args[1];
                    Repository.commit(commitMessage);
                    break;
                case "rm":
                    if (args.length != 2) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    Repository.remove(args[1]);
                    break;
                case "log":
                    Repository.log();
                    break;
                case "global-log":
                    Repository.globalLog();
                    break;
                case "find":
                    if (args.length != 2) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    final var message = args[1];
                    Repository.find(message);
                    break;
                case "status":
                    Repository.status();
                case "checkout":
                    if (args.length == 3 && Objects.equals(args[1], "--")) {
                        final var fileName = args[2];
                        Repository.checkoutFile(fileName);
                    }

                    if (args.length == 4 && Objects.equals(args[2], "--")) {
                        final var commitHash = args[1];
                        final var fileName = args[3];
                        Repository.checkoutCommitFile(commitHash, fileName);
                    }

                    if (args.length == 2) {
                        final var branchName = args[1];
                        Repository.checkoutBranch(branchName);
                    }
                case "branch":
                    if (args.length == 2) {
                        final var branchName = args[1];
                        Repository.branch(branchName);
                    }
            }

        }
    }

    private static void noInitialisedGitletError() {
        System.out.println("Not in an initialized Gitlet directory.");
        System.exit(0);
    }
}
