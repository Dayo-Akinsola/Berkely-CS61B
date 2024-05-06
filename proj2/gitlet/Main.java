package gitlet;

import java.util.Objects;

import static gitlet.Repository.*;
import static gitlet.Utils.printMessageAndExit;


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
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
//                if (args.length != 2) {
//                    printMessageAndExit("Incorrect operands.");
//                }
//                init();
                break;
//            case "add":
//                isInGitletDirectory();
//                if (args.length != 2) {
//                    printMessageAndExit("Incorrect operands.");
//                }
//                add(args[1]);
//                break;
//            case "commit":
//                isInGitletDirectory();
//                if (args.length != 2) {
//                    printMessageAndExit("Incorrect operands.");
//                }
//                final String commitMessage = args[1];
//                commit(commitMessage);
//                break;
//            case "rm":
//                isInGitletDirectory();
//                if (args.length != 2) {
//                    printMessageAndExit("Incorrect operands.");
//                }
//                remove(args[1]);
//                break;
//            case "log":
//                isInGitletDirectory();
//                if (args.length != 2) {
//                    printMessageAndExit("Incorrect operands.");
//                }
//                log();
//                break;
//            case "global-log":
//                isInGitletDirectory();
//                if (args.length != 2) {
//                    printMessageAndExit("Incorrect operands.");
//                }
//                globalLog();
//                break;
//            case "find":
//                isInGitletDirectory();
//                if (args.length != 2) {
//                    printMessageAndExit("Incorrect operands.");
//                }
//                final String message = args[1];
//                find(message);
//                break;
//            case "status":
//                isInGitletDirectory();
//                if (args.length != 2) {
//                    printMessageAndExit("Incorrect operands.");
//                }
//                status();
//                break;
//            case "checkout":
//                isInGitletDirectory();
//                if (args.length == 3 && Objects.equals(args[1], "--")) {
//                    final String fileName = args[2];
//                    checkoutFile(fileName);
//                    return;
//                }
//
//                if (args.length == 4 && Objects.equals(args[2], "--")) {
//                    final String commitHash = args[1];
//                    final String fileName = args[3];
//                    checkoutCommitFile(commitHash, fileName);
//                    return;
//                }
//
//                if (args.length == 2) {
//                    final String branchName = args[1];
//                    checkoutBranch(branchName);
//                    return;
//                }
//
//                printMessageAndExit("Incorrect operands.");
//                break;
//            case "branch":
//                isInGitletDirectory();
//                if (args.length == 2) {
//                    final String branchName = args[1];
//                    branch(branchName);
//                }
//                break;
            default:
//                printMessageAndExit("No command with that name exists.");
                break;
        }


    }
}
