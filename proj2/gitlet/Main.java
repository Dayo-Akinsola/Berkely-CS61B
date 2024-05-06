package gitlet;

import static gitlet.Utils.*;

import java.util.Objects;


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
                    if (args.length != 2) {
                        printMessageAndExit("Incorrect operands.");
                    }
                    Repository.init();
                    break;
                case "add":
                    Repository.isInGitletDirectory();
                    if (args.length != 2) {
                        printMessageAndExit("Incorrect operands.");
                    }
                    Repository.add(args[1]);
                    break;
                case "commit":
                    Repository.isInGitletDirectory();
                    if (args.length != 2) {
                        printMessageAndExit("Incorrect operands.");
                    }
                    final String commitMessage = args[1];
                    Repository.commit(commitMessage);
                    break;
                case "rm":
                    Repository.isInGitletDirectory();
                    if (args.length != 2) {
                        printMessageAndExit("Incorrect operands.");
                    }
                    Repository.remove(args[1]);
                    break;
                case "log":
                    Repository.isInGitletDirectory();
                    if (args.length != 2) {
                        printMessageAndExit("Incorrect operands.");
                    }
                    Repository.log();
                    break;
                case "global-log":
                    Repository.isInGitletDirectory();
                    if (args.length != 2) {
                        printMessageAndExit("Incorrect operands.");
                    }
                    Repository.globalLog();
                    break;
                case "find":
                    Repository.isInGitletDirectory();
                    if (args.length != 2) {
                        printMessageAndExit("Incorrect operands.");
                    }
                    final String message = args[1];
                    Repository.find(message);
                    break;
                case "status":
                    Repository.isInGitletDirectory();
                    if (args.length != 2) {
                        printMessageAndExit("Incorrect operands.");
                    }
                    Repository.status();
                case "checkout":
                    Repository.isInGitletDirectory();
                    if (args.length == 3 && Objects.equals(args[1], "--")) {
                        final String fileName = args[2];
                        Repository.checkoutFile(fileName);
                        return;
                    }

                    if (args.length == 4 && Objects.equals(args[2], "--")) {
                        final String commitHash = args[1];
                        final String fileName = args[3];
                        Repository.checkoutCommitFile(commitHash, fileName);
                        return;
                    }

                    if (args.length == 2) {
                        final String branchName = args[1];
                        Repository.checkoutBranch(branchName);
                        return;
                    }

                    printMessageAndExit("Incorrect operands.");
                case "branch":
                    Repository.isInGitletDirectory();
                    if (args.length == 2) {
                        final String branchName = args[1];
                        Repository.branch(branchName);
                    }
                default:
                    printMessageAndExit("No command with that name exists.");
            }

        }
    }
}
