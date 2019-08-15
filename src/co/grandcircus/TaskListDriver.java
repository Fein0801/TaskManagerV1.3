package co.grandcircus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Task Manager
 * 
 * @author Ben Feinstein
 * @version 1.3.0
 */
public class TaskListDriver {

//    private static final String TEXT_FILE_NAME = "SaveData.txt";
    private static final String JSON_FILE_NAME = "data.json";

    public static void main(String[] args) {
	Scanner userInput = new Scanner(System.in);
	ArrayList<Task> taskList = parseJSON();
	runTaskManager(userInput, taskList);
	writeToJSONFile(taskList);
	System.out.println("Goodbye.");
	userInput.close();
    }

    private static ArrayList<Task> parseJSON() {
	ArrayList<Task> list = new ArrayList<>();
	Path path = Paths.get(JSON_FILE_NAME);
	File inputFile = path.toFile();

	BufferedReader br = null;

	try {
	    br = new BufferedReader(new FileReader(inputFile));
	    String input = "";
	    StringBuffer data = new StringBuffer();

	    while (input != null) {
		input = br.readLine();
		data.append(input);
	    }

	    JSONObject obj = new JSONObject(data.toString());
	    JSONArray objArr = obj.getJSONArray("tasks");
	    JSONObject[] tasksJSON = new JSONObject[objArr.length()];

	    for (int i = 0; i < objArr.length(); i++) {
		tasksJSON[i] = objArr.getJSONObject(i);
		Task t = Task.parseJSONObject(tasksJSON[i]);

		if (t != null) {
		    list.add(t);
		}
	    }
	} catch (FileNotFoundException e) {
	    System.out.println("Could not find input file " + JSON_FILE_NAME);
	} catch (IOException e) {
	    System.out.println("Error reading from " + JSON_FILE_NAME);
	}

	return list;
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
		    printTasksByTeamMember(userInput, taskList);
		    break;
		case 3:
		    addTask(userInput, taskList);
		    break;
		case 4:
		    deleteTask(userInput, taskList);
		    break;
		case 5:
		    markTask(userInput, taskList);
		    break;
		case 6:
		    editTask(userInput, taskList);
		    break;
		case 7:
		    printTasksByDate(userInput, taskList);
		    break;
		case 8:
		    run = quit(userInput);
		    break;
		default:
		    System.out.println("Please enter one of the values on the options menu.");
		    break;
		}

	    } catch (InputMismatchException e) {
		System.out.println("Invalid input.");
		userInput.nextLine();
		continue;
	    }
	}
    }

    private static void printTasksByDate(Scanner userInput, ArrayList<Task> taskList) {
	if (taskList.isEmpty()) {
	    System.out.println("The task list is empty.");
	    return;
	}

	boolean valid = false;
	while (!valid) {
	    System.out.println("What date should I show all tasks before?");
	    String input = userInput.nextLine();
	    String date = parseDueDate(input);

	    if (!date.equals("")) {

		if (date.contains("/")) {
		    System.out.println("Tasks due before " + date + ":");
		    date = Task.convertDateFormat(date);
		} else {
		    System.out.println("Tasks due before " + Task.convertDateFormat(date) + ":");
		}
		LocalDate dueDate;
		try {
		    dueDate = LocalDate.parse(date);
		} catch (DateTimeParseException e) {
		    date = Task.convertDateFormat(date);
		    dueDate = LocalDate.parse(date);
		}
		valid = true;
		for (Task t : taskList) {
		    if (t.getDueDate().isBefore(dueDate) || t.getDueDate().isEqual(dueDate)) {
			System.out.println(t);
		    }
		}
	    } else {
		System.out.println("Invalid date format.");
	    }
	}
    }

    private static void printTaskList(ArrayList<Task> list) {
	while (list.contains(null)) {
	    list.remove(null);
	}

	if (list.isEmpty()) {
	    System.out.println("The task list is empty.");
	    return;
	}

	String taskListHeader = Task.getTaskListHeader();
	System.out.println(String.format("%-9s", "Task #") + taskListHeader);
	for (int i = 0; i < list.size(); i++) {
	    try {
		String taskNumber = String.format("%-9s", Integer.toString(i + 1));
		System.out.println(taskNumber + list.get(i));
	    } catch (NullPointerException e) {
		list.remove(i);
	    }
	}
	System.out.println("\n");
    }

    private static void printTasksByTeamMember(Scanner userInput, ArrayList<Task> taskList) {
	if (taskList.isEmpty()) {
	    System.out.println("The task list is empty.");
	    return;
	}

	ArrayList<String> names = new ArrayList<String>();

	for (Task task : taskList) {
	    String name = task.getTeamMember();
	    if (!names.contains(name)) {
		names.add(name);
	    }
	}

	String[] namesArr = new String[names.size()];
	System.out.println("Team members: ");

	for (int i = 0; i < names.size(); i++) {
	    namesArr[i] = names.get(i);
	}

	for (String tempName : namesArr) {
	    System.out.println(tempName);
	    names.remove(tempName);
	    tempName = tempName.toLowerCase();
	    names.add(tempName);
	}

	String name = "";
	while (!names.contains(name.toLowerCase())) {
	    System.out.println("Whose tasks would you like to display? (Enter one of the names above)");
	    name = userInput.nextLine();
	}

	for (Task task : taskList) {
	    if (task.getTeamMember().equalsIgnoreCase(name)) {
		System.out.println(task);
	    }
	}

	System.out.println();
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
	    printTaskList(taskList);
	    System.out.println("Which task number would you like to delete? (0 to cancel)");
	    try {
		int choice = userInput.nextInt();
		if (choice == 0) {
		    System.out.println("Cancelled.");
		    return;
		}

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
		continue; // Infinite loop otherwise
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
	    printTaskList(taskList);
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

    private static boolean quit(Scanner userInput) {
	String response = "";
	while (!response.equalsIgnoreCase("yes") && !response.equalsIgnoreCase("no")) {
	    System.out.println("Are you sure you would like to quit? (yes/no)");
	    response = userInput.next();
	}
	return !response.equalsIgnoreCase("yes");
    }

    private static void printOptions() {
	System.out.println("Options:");
	System.out.println("\t1. List ALL tasks");
	System.out.println("\t2. List tasks by team member");
	System.out.println("\t3. Add task");
	System.out.println("\t4. Delete task");
	System.out.println("\t5. Mark task as complete");
	System.out.println("\t6. Edit task");
	System.out.println("\t7. List tasks before date");
	System.out.println("\t8. Quit");
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

//    private static ArrayList<Task> readFromFile() {
//	ArrayList<Task> list = new ArrayList<Task>();
//	String fileName = TEXT_FILE_NAME;
//	Path path = Paths.get(fileName);
//
//	File file = path.toFile();
//
//	BufferedReader br = null;
//	try {
//	    br = new BufferedReader(new FileReader(file));
//	    String line = br.readLine();
//
//	    while (line != null) {
//		Task t = new Task(line);
//		if (t != null) {
//		    list.add(t);
//		}
//		line = br.readLine();
//	    }
//	    br.close();
//
//	} catch (FileNotFoundException e) {
//	    System.out.println("error reading from save file");
//	} catch (IOException e) {
//	    System.out.println();
//	}
//
//	return list;
//
//    }

//    private static void writeToFile(ArrayList<Task> list) {
//	String fileName = TEXT_FILE_NAME;
//	Path path = Paths.get(fileName);
//
//	File file = path.toFile();
//	PrintWriter output = null;
//
//	try {
//	    output = new PrintWriter(new FileOutputStream(file, false));
//	    if (list.isEmpty()) {
//		return;
//	    }
//	    for (Task s : list) {
//		output.println(s.getSaveDataString());
//	    }
//	} catch (FileNotFoundException e) {
//	    System.err.println("I AM ERROR!");
//	} finally {
//	    output.close();
//	}
//
//    }

    private static void writeToJSONFile(ArrayList<Task> list) {
	Path p = Paths.get(JSON_FILE_NAME);
	File file = p.toFile();

	if (!list.isEmpty()) {
	    JSONObject allTasks = new JSONObject();
	    JSONArray jsonList = new JSONArray();

	    try {
		if (!file.exists()) {
		    file.createNewFile();
		}

		for (Task t : list) {
		    if (t == null) {
			list.remove(list.indexOf(null));
		    } else {
			JSONObject obj = t.toJSONObject();
			jsonList.put(obj);
		    }
		}
		allTasks.put("tasks", jsonList);
		Files.write(p, allTasks.toString().getBytes());
	    } catch (FileNotFoundException e) {
		System.out.println("Could not find JSON file " + JSON_FILE_NAME);
	    } catch (NullPointerException e) {
		System.out.println("Skipped one object when saving to JSON file");
	    } catch (IOException e) {
		System.out.println("Error writing to " + JSON_FILE_NAME);
	    }
	}
    }
}
