package Assignment2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.*;


/* PLEASE DO NOT MODIFY A SINGLE STATEMENT IN THE TEXT BELOW.
READ THE FOLLOWING CAREFULLY AND FILL IN THE GAPS

I hereby declare that all the work that was required to 
solve the following problem including designing the algorithms
and writing the code below, is solely my own and that I received
no help in creating this solution and I have not discussed my solution 
with anybody. I affirm that I have read and understood
the Senate Policy on Academic honesty at 
https://secretariat-policies.info.yorku.ca/policies/academic-honesty-senate-policy-on/
and I am well aware of the seriousness of the matter and the penalties that I will face as a 
result of committing plagiarism in this assignment.

BY FILLING THE GAPS,YOU ARE SIGNING THE ABOVE STATEMENTS.

Full Name: Dongwon Lee
Student Number: 215805260
Course Section: B
*/


/**
* This class generates a transcript for each student, whose information is in the text file.
* 
*
*/
public class Transcript {
	
	private ArrayList<Object> grade = new ArrayList<Object>();
	private File inputFile;
	private String outputFile;
	
	/**
	 * This the the constructor for Transcript class that 
	 * initializes its instance variables and call readFie private
	 * method to read the file and construct this.grade.
	 * @param inFile is the name of the input file.
	 * @param outFile is the name of the output file.
	 */
	public Transcript(String inFile, String outFile) {
		inputFile = new File(inFile);	
		outputFile = outFile;	
		grade = new ArrayList<Object>();
		this.readFile();
	}// end of Transcript constructor

	/** 
	 * This method reads a text file and add each line as 
	 * an entry of grade ArrayList.
	 * @exception It throws FileNotFoundException if the file is not found.
	 */
	private void readFile() {
		Scanner sc = null; 
		try {
			sc = new Scanner(inputFile);	
			while(sc.hasNextLine()){
				grade.add(sc.nextLine());
	        }      
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			sc.close();
		}		
	} // end of readFile
	
	/**
	 * Builds a student arrayList with an object arrayList read from the input file and return it.
	 * 
	 * @return a student arrayList built with an object arrayList read from the input file
	 */
	public ArrayList<Student> buildStudentArray() {
		
		ArrayList<Student> studentList = new ArrayList<Student>();

		for(Object g: this.grade) {
			String str = (String) g;
			String[] result = str.split(","); // a,b,c -> {a,b,c}
			
			// make an Assessment arrayList
			ArrayList<Assessment> assignment = new ArrayList<Assessment>();
			for(int i = 3; i < result.length - 1; i++) {
				Assessment a = new Assessment(result[i].charAt(0), Integer.valueOf(result[i].substring(1,3)));
				assignment.add(a);
			}
			
			// make a course with code, Assessment arrayList, credit
			Course c = new Course(result[0], assignment, Double.valueOf(result[1])); 
			
			// make and add students to studentList
			// check whether student exists
			if(studentList.size() == 0) {	// if there is no student in the list
				Student newStudent = new Student(result[2], result[result.length-1].trim(), new ArrayList<>());
				newStudent.addCourse(c);
				
				// Student - addGrade(ArrayList<Double> grades,ArrayList<Integer> weights)
				ArrayList<Double> grades = new ArrayList<Double>();
				ArrayList<Integer> weights = new ArrayList<Integer>();
				for(int i = 3; i < result.length - 1; i++) {
					Double grade = new Double(result[i].substring(result[i].indexOf("(") + 1, result[i].indexOf(")")));
					Integer weight = new Integer(Integer.valueOf(result[i].substring(1,3)));
					grades.add(grade);
					weights.add(weight);
				}
				try {
					newStudent.addGrade(grades, weights); 
				}
				catch(InvalidTotalException e) {
					System.out.println(e);
				}
				studentList.add(newStudent);
			}
			else {	// if there is a student in the list
				boolean flag = false;
				for(int i = 0; i < studentList.size(); i++) {
					if(studentList.get(i).getID().equals(result[2]) && studentList.get(i).getName().trim().equals(result[result.length-1].trim())) { // if the same student exits in the list 
						studentList.get(i).addCourse(c); // other block 1.add course 2.
						
						// Student - addGrade(ArrayList<Double> grades,ArrayList<Integer> weights)
						ArrayList<Double> grades = new ArrayList<Double>();
						ArrayList<Integer> weights = new ArrayList<Integer>();
						for(int j = 3; j < result.length - 1; j++) {
							Double grade = new Double(result[j].substring(result[j].indexOf("(") + 1, result[j].indexOf(")")));
							Integer weight = new Integer(Integer.valueOf(result[j].substring(1,3)));
							grades.add(grade);
							weights.add(weight);
						}
						try {
							studentList.get(i).addGrade(grades, weights); 
						}
						catch(InvalidTotalException e) {
							System.out.println(e);
						}
						flag = true;
					}
				}
					
				if(!flag) {	// if the same student does not exist in the list
					Student newStudent = new Student(result[2], result[result.length-1].trim(), new ArrayList<>());
					newStudent.addCourse(c);
					
					// Student - addGrade(ArrayList<Double> grades,ArrayList<Integer> weights)
					ArrayList<Double> grades = new ArrayList<Double>();
					ArrayList<Integer> weights = new ArrayList<Integer>();
					for(int i = 3; i < result.length - 1; i++) {
						Double grade = new Double(result[i].substring(result[i].indexOf("(") + 1, result[i].indexOf(")")));
						Integer weight = new Integer(Integer.valueOf(result[i].substring(1,3)));
						grades.add(grade);
						weights.add(weight);
					}
					
					try {
						newStudent.addGrade(grades, weights); 
					}
					catch(InvalidTotalException e) {
						System.out.println(e);
					}
					studentList.add(newStudent);
				}		
			}
		}	
		
		return studentList;
	}
	

	/**
	 * Writes the information of students using the argument students and toString of Student to the output file.
	 * 
	 * @param students arrayList of students to be written to the output file.
	 */
	public void printTranscript(ArrayList<Student> students) {
		try {
			FileOutputStream outputStream = new FileOutputStream(this.outputFile);
			for(Student student: students) {
				String str = student.toString();
				byte b[] = str.getBytes();
				outputStream.write(b);
			}
			outputStream.close();
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}

	
} // end of Transcript

/**
* This class stores and handles information of a student. Information of student id, name, courses that the student has taken,
*  and the final grades of the courses.
*
*/
public class Student {
	
	private String studentID;
	private String name;
	private ArrayList<Course> courseTaken;
	private ArrayList<Double> finalGrade;
	
	/**
	 * Create a student with empty studentID, name, courseTaken, and finalGrade.
	 */
	Student(){
		this.setID("");
		this.setName("");
		this.setCourse(new ArrayList<Course>());
		this.setGrade(new ArrayList<Double>());
	}
	
	/**
	 * Create a student with the argument studentID, name, courseTaken, and empty finalGrade.
	 * 
	 * @param studentID the studentID of the student
	 * @param name the name of the student
	 * @param courseTaken the courseTaken of the student
	 */
	Student(String studentID, String name, ArrayList<Course> courseTaken){
		this.setID(studentID);
		this.setName(name);
		this.setCourse(courseTaken);
		this.setGrade(new ArrayList<Double>());
	}
	
	/**
	 * Calculate the grade of a course by calulcating assessments' grades and weights and add the grade to finalGrade
	 * 
	 * @param grades the grades of the assessments of a course
	 * @param weights the weights of the assessments of a course
	 * @throws InvalidTotalException custom exception that is thrown when sum of weights is not 100 or 
	 * 		   sum of grades is greater than 100.0 
	 */
	public void addGrade(ArrayList<Double> grades, ArrayList<Integer> weights) throws InvalidTotalException {
		Integer weightSum = new Integer(0);
		Double gradeSum = new Double(0.0);

		// check for exception
		for(Integer weight: weights) {
			weightSum += weight;
		}
		for(int i = 0; i < grades.size();i++) {
			gradeSum += grades.get(i) * weights.get(i) / 100;
		}
		
		if(weightSum != 100)
			throw new InvalidTotalException("The sum of the weight is not 100");
		else if(gradeSum > 100.0)
			throw new InvalidTotalException("The sum of grade is greater than 100");
		
		// add the grade of a course to finalGrade
		this.finalGrade.add(Math.round(gradeSum*10.0)/10.0);
	}
	
	/**
	 * Computes GPA from finalGrade and return it
	 * 
	 * @return GPA computed from finalGrade
	 */
	public double weightedGPA() {
		double gpa = 0.0;
		double totalCredit = 0;
		for(int i = 0; i < this.getGrade().size(); i++) {
			Double grade = new Double(this.getGrade().get(i));
			int gp;
			if(grade >= 90) gp = 9;
			else if(grade >= 80) gp = 8;
			else if(grade >= 75) gp = 7;
			else if(grade >= 70) gp = 6;
			else if(grade >= 65) gp = 5;
			else if(grade >= 60) gp = 4;
			else if(grade >= 55) gp = 3;
			else if(grade >= 50) gp = 2;
			else if(grade >= 47) gp = 1;
			else gp = 0;
			gpa = gpa + gp * this.getCourse().get(i).getCredit();
			totalCredit = totalCredit + this.getCourse().get(i).getCredit();
		}
		gpa = gpa / totalCredit;
		return Math.round(gpa * 10.0) / 10.0;
	}
	
	/**
	 * Add the argument course to courseTaken
	 * 
	 * @param course the course that is to be added to courseTaken
	 */
	public void addCourse(Course course) {
		this.courseTaken.add(course);
	}
	
	/**
	 * Returns the studentID of this student
	 * 
	 * @return the studentID of this student
	 */
	public String getID() {
		return new String(this.studentID);
	}
	
	/**
	 * Returns the name of this student
	 * 
	 * @return the name of this student
	 */
	public String getName() {
		return new String(this.name);
	}
	
	/**
	 * Returns the courseTaken of this student 
	 * 
	 * @return the courseTaken of this student 
	 */
	public ArrayList<Course> getCourse() {
		return new ArrayList<Course>(this.courseTaken);
	}
	
	/**
	 * Returns the grade finalGrade of this student
	 * 
	 * @return the grade finalGrade of this student
	 */
	public ArrayList<Double> getGrade() {
		return new ArrayList<Double>(this.finalGrade);
	}
	
	/**
	 * Sets the studentID of this student to studentID
	 * 
	 * @param studentID the new studentID of this student
	 */
	public void setID(String studentID) {
		this.studentID = new String(studentID);
	}
	
	/**
	 * Sets the name of this student to name
	 * 
	 * @param name the new name of this student
	 */
	public void setName(String name) {
		this.name = new String(name);
	}
	
	/**
	 * Sets the courseTaken of this student to courseTaken
	 * 
	 * @param courseTaken the new courseTaken of this student
	 */
	public void setCourse(ArrayList<Course> courseTaken) {
		this.courseTaken = new ArrayList<Course>();
		for(Course c: courseTaken) {
			this.courseTaken.add(c);
		}
	}
	
	/**
	 * Sets the finalGrade of this student to finalGrade
	 * 
	 * @param finalGrade the new finalGrade of this student
	 */
	public void setGrade(ArrayList<Double> finalGrade) {
		this.finalGrade = new ArrayList<Double>();
		for(Double g: finalGrade) {
			this.finalGrade.add(g);
		}
	}
	
	/**
	 * Returns a string representation of this student. The string representation of this student is
	 * the name and studentID of this student separated by a tab, and 20 dashes on the next line, and 
	 * code of each element of courseTaken and its corresponding finalGrade element separated by a tab in a line
	 * for all elements of courseTaken on the next line, and 20 dashed on the next line, and weightedGPA
	 * on the next line.
	 * 
	 * @return a string representation of this student.
	 */
	@Override
	public String toString() {
		String str = this.getName() + "\t" + this.getID() + "\n";
		str += "--------------------\n";
		for(int i = 0; i < this.getCourse().size();i++) {
			str += this.getCourse().get(i).getCode() + "\t" + this.getGrade().get(i) + "\n";
		}
		str += "--------------------\n";
		str += "GPA: " + this.weightedGPA();
		str += "\n\n";
		return str;
	}
}

/**
* This class stores and handles the information of a course. The information of code, assignments, and credit of the course.
* 
*
*/
public class Course {
	
	String code;
	ArrayList<Assessment> assignment;
	double credit;
	
	/**
	 * Create a course with an empty code, empty assignment , and 0 credit.
	 */
	Course(){
		this.setCode("");
		this.setAssignment(new ArrayList<Assessment>());
		this.setCredit(0.0);
	}
	
	/**
	 * Create a course with the argument code, assignment, and credit.
	 * 
	 * @param code the code of the course
	 * @param assignment the assignment of the course
	 * @param credit the credit of the course
	 */
	Course(String code, ArrayList<Assessment> assignment, double credit){
		this.setCode(code);
		this.setAssignment(assignment);
		this.setCredit(credit);
	}
	
	/**
	 * Create a course with the same fields as the argument course
	 * 
	 * @param course another course
	 */
	Course(Course course){
		this(course.getCode(), course.getAssignment(), course.getCredit());
	}
	
	/**
	 * Compares this course with the given object. The result is true
	 * if and only if the argument is no null and is a Course object having
	 * the same code, assignment, and credit as this object.
	 * 
	 * @param obj the object to compare this assessment against
	 * @return	true if the given object represents a Course equivalent to this course,
	 * 			false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(this.getClass() != obj.getClass()) return false;
		Course course = (Course) obj;
		if(!this.getCode().equals(course.getCode())) return false;
		if(this.getCode() == null) return false;
		if(course.getCode() == null) return false;
		ArrayList<Assessment> tmp1 = new ArrayList<Assessment>(this.getAssignment()); 
		ArrayList<Assessment> tmp2 = new ArrayList<Assessment>(course.getAssignment());
		Collections.sort(tmp1);
		Collections.sort(tmp2);
		if(!tmp1.equals(tmp2)) return false; 
		if(this.getCredit() != course.getCredit()) return false;
		return true;
	}
	
	/**
	 * Returns the code of this course
	 * 
	 * @return the code of this course
	 */
	public String getCode() {
		return new String(this.code);
	}
	
	/**
	 * Returns the assignment of this course
	 * 
	 * @return the assignment of this course
	 */
	public ArrayList<Assessment> getAssignment(){
		return new ArrayList<Assessment>(this.assignment);
	}
	
	/**
	 * Returns the credit of this course
	 * 
	 * @return the credit of this course
	 */
	public double getCredit() {
		return this.credit;
	}
	
	/**
	 * Sets the code of this course to code
	 * 
	 * @param code the new code of this course
	 */
	public void setCode(String code) {
		this.code = new String(code);
	}
	
	/**
	 * Sets the assignment of this course to assignment
	 * 
	 * @param assignment the new assignment of this course
	 */
	public void setAssignment(ArrayList<Assessment> assignment) {
		this.assignment = new ArrayList<Assessment>();
		for(Assessment a: assignment) {
			this.assignment.add(a);
		}
	}
	
	/**
	 * Sets the credit of this course to credit
	 * 
	 * @param credit the new credit of this course
	 */
	public void setCredit(double credit) {
		this.credit = credit;
	}
}

/**
* This class stores and handles the information of an assessment. Information of the type and weight of the assessment.
* 
*
*/
public class Assessment implements Comparable<Assessment>{
	
	char type;
	int weight;
	
	/**
	 * Create an assessment with an empty type and 0 weight.
	 */
	Assessment(){
		this.setType('\0');
		this.setWeigth(0);
	}
	
	/**
	 * Create an assessment with the argument type and weight.
	 * 
	 * @param type the type of the assignment
	 * @param weight the weight of the assignment
	 */
	Assessment(char type, int weight){
		this.setType(type);
		this.setWeigth(weight);
	}
	
	/**
	 * A static factory method that creates an assessment instance with the argument type and weight and return it.
	 * 
	 * @param type the type for the instance
	 * @param weight the weight for the instance
	 * @return an assessment instance with the argument type and weight
	 */
	public static Assessment getInstance(char type, int weight) {
		return new Assessment(type, weight);
	}
	
	/**
	 * Compares this assessment with the given object. The result is true
	 * if and only if the argument is no null and is an Assessment object having
	 * the same type and weight as this object.
	 * 
	 * @param obj the object to compare this assessment against
	 * @return	true if the given object represents an Assessment equivalent to this assessment,
	 * 			false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(this.getClass() != obj.getClass()) return false;
		Assessment assessment = (Assessment) obj;
		if(this.getType() != assessment.getType()) return false;
		if(this.getWeight() != assessment.getWeight()) return false;
		return true;
	}
	
	/**
	 * Returns the type of this assessment
	 * 
	 * @return the type of this assessment
	 */
	public char getType() {
		return this.type;
	}
	
	/**
	 * Returns the weight of this assessment
	 * 
	 * @return the weight of this assessment
	 */
	public int getWeight() {
		return this.weight;
	}
	
	/**
	 * Sets the type of this assessment to type
	 * 
	 * @param type the new type of this assessment
	 */
	public void setType(char type) {
		this.type = type;
	}
	
	/**
	 * Sets the weight of this assessment to weight
	 * 
	 * @param weight the new weight of this assessment
	 */
	public void setWeigth(int weight) {
		this.weight = weight;
	}
	
	/**
	 * Compare this assessment and argument assessment's type and return 1 if this assessment's type unicode value is greater than 
	 * the argument assessment, -1 if less, 0 if equal
	 * 
	 * @return 1 if this assessment type's unicode value is greater than the argument assessment, -1 if less, 0 if equal
	 */
	@Override
	public int compareTo(Assessment other) {
		if(this.getType() > other.getType())
			return 1;
		else if(this.getType() < other.getType())
			return -1;
		else
			return 0;
	}
}


/**
* This class is a custom exception class to be used for Student class' addGrade method when 
* the sum of weight of assessements of a course is not 100 and the sum of grade of assessments of a course is more than 100. 
*
*/
public class InvalidTotalException extends Exception {

	private static final long serialVersionUID = -3949147535695170701L;

	/**
	 * Constructor of a custom exception class InvalidTotalException. Calls the super class constructor with the argument errorMessage.
	 * 
	 * @param errorMessage an argument to be send to super class Exception constructor
	 */
	public InvalidTotalException(String errorMessage) {
		super(errorMessage);
	}
}

