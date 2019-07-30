package co.grandcircus;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Task {

    private static final int MAX_DESCRIPTION_SIZE = 50;
    private static final int MAX_NAME_SIZE = 30;
    private boolean completed;
    private String teamMember;
    private LocalDate dueDate;
    private String description;

    public Task() {
	completed = false;
    }

    public Task(String teamMember, String dueDate, String description) throws NameLengthException {
	completed = false;
	this.setTeamMember(teamMember);
	this.setDueDate(dueDate);
	this.setDescription(description);
    }

    public Task(String teamMember, LocalDate dueDate, String description) throws NameLengthException {
	completed = false;
	this.setTeamMember(teamMember);
	this.setDueDate(dueDate);
	this.setDescription(description);
    }

    public Task(String saveData) {
	restore(saveData);
    }

    public String getTeamMember() {
	return teamMember;
    }

    public void setTeamMember(String teamMember) throws NameLengthException {
	if (teamMember.length() > MAX_NAME_SIZE || teamMember.contains("/")) {
	    throw new NameLengthException();
	}
	this.teamMember = teamMember;
    }

    public LocalDate getDueDate() {
	return dueDate;
    }

    public void setDueDate(String date) throws DateTimeParseException {
	try {
	    dueDate = LocalDate.parse(date);
	} catch (DateTimeParseException e) {
	    dueDate = LocalDate.parse(Task.convertDateFormat(date));
	}
    }

    public void setDueDate(LocalDate date) {
	dueDate = date;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) throws NameLengthException {
	if (description.length() > MAX_DESCRIPTION_SIZE || description.contains("/")) {
	    throw new NameLengthException();
	}
	this.description = description;
    }

    public void markCompleted() {
	completed = true;
    }

    public boolean isCompleted() {
	return completed;
    }

    @Override
    public String toString() {
	StringBuffer buff = new StringBuffer();
	buff.append(formatSpaces());
	return buff.toString();
    }

    public static String convertDateFormat(String date) {
	if (date.contains("-")) {
	    String[] parts = date.split("-");
	    String year = parts[0];
	    String month = parts[1];
	    String day = parts[2];
	    date = month + "/" + day + "/" + year;
	} else if (date.contains("/")) {
	    String[] parts = date.split("/");
	    String year = parts[2];
	    String month = parts[0];
	    String day = parts[1];
	    date = year + "-" + month + "-" + day;
	}
	return date;
    }

    public String getSaveDataString() {
	StringBuffer buff = new StringBuffer();
	String regex = "/";
	buff.append(teamMember);
	buff.append(regex);
	buff.append(dueDate.toString());
	buff.append(regex);
	buff.append(description);
	buff.append(regex);
	buff.append(completed);
	return buff.toString();
    }

    private String formatSpaces() {
	String date = convertDateFormat(dueDate.toString());
	return formatSpaces(Boolean.toString(completed), date, teamMember, description);
    }

    public static String formatSpaces(String bool, String date, String name, String desc) {
	String regex = "%-8s%-13s%-" + MAX_NAME_SIZE + "s%s";
	return String.format(regex, bool, date, name, desc);
    }

    private void restore(String saveData) {
	String[] parts = saveData.split("/");
	if (parts.length == 4) {
	    this.teamMember = parts[0];
	    this.dueDate = LocalDate.parse(parts[1]);
	    this.description = parts[2];
	    this.completed = Boolean.parseBoolean(parts[3]);
	} else {
	    System.out.println("Could not load save data.");
	}
    }

    public static boolean isValidTeamMemberName(String name) {
	if (name.length() > MAX_NAME_SIZE) {
	    return false;
	}
	return true;
    }

    public static boolean isValidDescription(String description) {
	if (description.length() > MAX_DESCRIPTION_SIZE) {
	    return false;
	}
	return true;
    }

    public static boolean isValidDate(String date) {
	if (date.matches("\\d{2}\\/\\d{2}\\/\\d{4}") || date.matches("\\d{4}\\-\\d{2}\\-\\d{2}")) {
	    return true;
	}
	return false;
    }

    public static String getTaskListHeader() {
	return Task.formatSpaces("Done?", "Due Date", "Team Member", "Description");
    }

    public void resetCompletion() {
	completed = false;
    }

}
