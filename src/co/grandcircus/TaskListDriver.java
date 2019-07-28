package co.grandcircus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TaskListDriver {

    private static final String SAVE_FILE_NAME = "src/co/grandcircus/SaveData.txt";

    public static void main(String[] args) {
	Scanner userInput = new Scanner(System.in);
	File saveFile = new File(SAVE_FILE_NAME);
	ArrayList<Task> taskList = new ArrayList<>();
	try {
	    retrieveSaveData(saveFile, taskList);
	} catch (FileNotFoundException e) {
	    System.out.println("No save data found. Moving on.");
	} catch (DateTimeParseException e) {
	    System.out.println("Could not load save data.");
	}
	runTaskManager(userInput, taskList);
	saveData(saveFile, taskList);
	System.out.println("Goodbye.");
	userInput.close();
    }

    private static void runTaskManager(Scanner userInput, ArrayList<Task> taskList) {
	System.out.println("Welcome to the Task Manager!");
	boolean run = true;
	while (run) {
	    printOptions();
	    System.out.println("What would you like to do?");
	    try {
		int menuSelection = userInput.nextInt();

		userInput.nextLine();
		switch (menuSelection) {
		case 1:
		    printTaskList(taskList);
		    break;
		case 2:
		    addTask(userInput, taskList);
		    break;
		case 3:
		    deleteTask(userInput, taskList);
		    break;
		case 4:
		    markTask(userInput, taskList);
		    break;
		case 5:
		    editTask(userInput, taskList);
		    break;
		case 6:
		    run = quit(userInput);
//		    run = false;
		    break;
		default:
		    System.out.println("Invalid Input.");
		    break;
		}


	    } catch (InputMismatchException e) {
		System.out.println("Please enter one of the values on the options menu.");
		userInput.nextLine();
		continue;
	    }
	}
    }

    private static void editTask(Scanner userInput, ArrayList<Task> taskList) {
	if (taskList.isEmpty()) {
	    System.out.println("Task list empty. No entries to edit.");
	    return;
	}

	boolean run = true;
	do {
	    System.out.println("Which task number would you like to edit?");
	    try {
		int choice = userInput.nextInt();
		Task task = taskList.get(choice - 1);

		String edit = "";
		while (!edit.equalsIgnoreCase("yes") && !edit.equalsIgnoreCase("no")) {
		    System.out.println(Task.getTaskListHeader());
		    System.out.println(task);
		    System.out.println("Edit this task? (yes/no)");
		    edit = userInput.next();
		    System.out.println();
		    userInput.nextLine();
		}

		if (edit.equalsIgnoreCase("yes")) {
		    run = false;
		    String[] options = { "team member", "due date", "description", "completion status", "delete" };
		    System.out.println("What would you like to change?");
		    boolean valid = false;
		    while (!valid) {
			System.out.print("(Please enter ");
			for (int index = 0; index < options.length; index++) {
			    System.out.print("\"" + options[index] + "\"");
			    if (index < options.length - 1) {
				System.out.print(" or ");
			    }
			}
			System.out.println(")");

			String selection = userInput.nextLine().toLowerCase();
			switch (selection) {
			case "team member":
			    System.out.println("Please enter the new name: ");
			    selection = userInput.nextLine();
			    try {
				task.setTeamMember(selection);
				System.out.println("Team member name set!");
				valid = true;
			    } catch (NameLengthException e) {
				System.out.println("Name is too long.");
			    }
			    break;
			case "due date":
			    System.out.println("Please enter the new due date: ");
			    selection = userInput.nextLine();
			    task.setDueDate(parseDueDate(selection));
			    System.out.println("New due date set!");
			    valid = true;
			    break;
			case "description":
			    System.out.println("Please enter the new description: ");
			    selection = userInput.nextLine();
			    try {
				task.setDescription(selection);
				System.out.println("New description set!");
				valid = true;
			    } catch (NameLengthException e) {
				System.out.println("Name is too long.");
			    }
			    break;
			case "completion status":
			    if (!task.isCompleted()) {
				task.markCompleted();
				valid = true;
				System.out.println("Marked task complete!");
			    } else {
				while (!selection.equalsIgnoreCase("yes") && !selection.equalsIgnoreCase("no")) {
				    System.out
					    .println("Do you want to change the completion status to false? (yes/no)");
				    selection = userInput.next();
				}

				if (selection.equalsIgnoreCase("yes")) {
				    valid = true;
				    task.resetCompletion();
				    System.out.println("Completion reset.");
				}
			    }
			    break;
			case "delete":
			    String delete = "";
			    while (!delete.equalsIgnoreCase("yes") && !delete.equalsIgnoreCase("no")) {
				System.out.println(Task.getTaskListHeader());
				System.out.println(task);
				System.out.println("Delete this task? (yes/no)");
				delete = userInput.next();
				System.out.println();
			    }
			    if (delete.equalsIgnoreCase("yes")) {
				valid = true;
				taskList.remove(task);
				System.out.println("Task deleted!");
			    } else {
				valid = true;
				System.out.println("Task was not deleted.");
			    }
			    break;
			}
		    }
		    System.out.println("Task edited.");
		} else {
		    run = false;
		    System.out.println("Task was not edited.");
		}
	    } catch (InputMismatchException e) {
		System.out.println("Please enter a whole number.");
	    } catch (IndexOutOfBoundsException e) {
		System.out.println("Please enter a number between 1 and " + taskList.size());
	    }
	} while (run);

    }

    private static boolean quit(Scanner userInput) {
	String response = "";
	while (!response.equalsIgnoreCase("yes") && !response.equalsIgnoreCase("no")) {
	    System.out.println("Are you sure you would like to quit? (yes/no)");
	    response = userInput.next();
	}
	return !response.equalsIgnoreCase("yes");
    }

    private static void printTaskList(ArrayList<Task> list) {
	if (list.isEmpty()) {
	    System.out.println("The task list is empty.");
	    return;
	}

	String taskListHeader = Task.getTaskListHeader();
	System.out.println(String.format("%-9s", "Task #") + taskListHeader);
	for (int i = 0; i < list.size(); i++) {
	    String taskNumber = String.format("%-9s", Integer.toString(i + 1));
	    System.out.println(taskNumber + list.get(i));
	}
	System.out.println("\n");
    }

    private static void addTask(Scanner userInput, ArrayList<Task> list) {
	System.out.println("ADD TASK");
	System.out.println();

	System.out.print("Team Member Name: ");
	String teamMember = userInput.nextLine();
	System.out.print("Due Date: ");
	String dueDate = userInput.nextLine();
	System.out.print("Task Description: ");
	String description = userInput.nextLine();
	try {
	    dueDate = parseDueDate(dueDate);
	    Task t = new Task(teamMember, dueDate, description);
	    list.add(t);
	    System.out.println("Task entered!");
	} catch (NameLengthException | DateTimeParseException e) {
	    System.out.println("Something went wrong. Please try again." + "\n");
	    addTask(userInput, list);
	}
    }

    private static String parseDueDate(String date) {
	String dueDate = "";
	if (date.equalsIgnoreCase("today")) {
	    dueDate = LocalDate.now().toString();
	} else if (date.equalsIgnoreCase("tomorrow")) {
	    dueDate = LocalDate.now().plusDays(1).toString();
	} else if (Task.isValidDate(date)) {
	    dueDate = date;
	} else if (date.toLowerCase().matches("in \\d days")) {
	    String[] pieces = date.split(" ");
	    int days = Integer.parseInt(pieces[1]);
	    dueDate = LocalDate.now().plusDays(days).toString();
	} else if (date.toLowerCase().matches("in \\d weeks")) {
	    String[] pieces = date.split(" ");
	    int weeks = Integer.parseInt(pieces[1]);
	    dueDate = LocalDate.now().plusWeeks(weeks).toString();
	}
	return dueDate;
    }

    private static void deleteTask(Scanner userInput, ArrayList<Task> taskList) {
	if (taskList.isEmpty()) {
	    System.out.println("Task list empty. No entries to delete.");
	    return;
	}

	boolean run = true;
	do {
	    System.out.println("Which task number would you like to delete?");
	    try {
		int choice = userInput.nextInt();
		Task task = taskList.get(choice - 1);

		String delete = "";
		while (!delete.equalsIgnoreCase("yes") && !delete.equalsIgnoreCase("no")) {
		    System.out.println(Task.getTaskListHeader());
		    System.out.println(task);
		    System.out.println("Delete this task? (yes/no)");
		    delete = userInput.next();
		    System.out.println();
		}
		if (delete.equalsIgnoreCase("yes")) {
		    run = false;
		    taskList.remove(task);
		    System.out.println("Task deleted!");
		} else {
		    run = false;
		    System.out.println("Task was not deleted.");
		}

	    } catch (InputMismatchException e) {
		System.out.println("Please enter a whole number.");
	    } catch (IndexOutOfBoundsException e) {
		System.out.println("Please enter a number between 1 and " + taskList.size());
	    }
	} while (run);

    }

    private static void markTask(Scanner userInput, ArrayList<Task> taskList) {
	if (taskList.isEmpty()) {
	    System.out.println("Task list empty. No entries to mark.");
	    return;
	}

	boolean run = true;
	do {
	    System.out.println("Which task number would you like to mark completed?");
	    try {
		int choice = userInput.nextInt();
		Task task = taskList.get(choice - 1);

		String complete = "";
		while (!complete.equalsIgnoreCase("yes") && !complete.equalsIgnoreCase("no")) {
		    System.out.println(Task.getTaskListHeader());
		    System.out.println(task);
		    System.out.println("Mark this task as complete? (yes/no)");
		    complete = userInput.next();
		    System.out.println();
		}

		if (complete.equalsIgnoreCase("yes")) {
		    run = false;
		    task.markCompleted();
		    System.out.println("Task completed!");
		} else {
		    run = false;
		    System.out.println("Task was not marked.");
		}
	    } catch (InputMismatchException e) {
		System.out.println("Please enter a whole number.");
	    } catch (IndexOutOfBoundsException e) {
		System.out.println("Please enter a number between 1 and " + taskList.size());
	    }
	} while (run);

    }

    private static void printOptions() {
	System.out.println("Options:");
	System.out.println("\t1. List Tasks");
	System.out.println("\t2. Add task");
	System.out.println("\t3. Delete task");
	System.out.println("\t4. Mark task as complete");
	System.out.println("\t5. Edit task");
	System.out.println("\t6. Quit");
    }

    private static void saveData(File saveFile, ArrayList<Task> taskList) {
	System.out.println("Saving data...");
	try {
	    if (saveFile.exists()) {
		FileWriter writer = new FileWriter(saveFile);
		for (Task task : taskList) {
		    writer.write(task.getSaveDataString() + "\n");
		}
		writer.close();
	    } else {
		saveFile.createNewFile();
	    }
	} catch (IOException e) {
	    System.out.println("Error saving to file \"" + SAVE_FILE_NAME + "\".");
	}
	System.out.println("Finished saving data.");
    }

    private static void retrieveSaveData(File saveFile, ArrayList<Task> taskList) throws FileNotFoundException {
	if (saveFile.exists()) {
	    Scanner fileInput = new Scanner(new FileReader(saveFile));
	    while (fileInput.hasNextLine()) {
		String input = fileInput.nextLine();
		taskList.add(new Task(input));
	    }
	    fileInput.close();
	} else {
	    try {
		saveFile.createNewFile();
	    } catch (IOException e) {
		System.out.println("Whoops. Error creating new save file.");
	    }
	}
    }

}
