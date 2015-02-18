import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestPA1 {

	private ByteArrayOutputStream outContent;
	private FileInputStream inContent;
	private static int highestTestNumber;
	private static boolean debugMode = true;
	private static String workingDirectory;


	@BeforeClass
	public static void writeFilesForTesting(){
		workingDirectory = System.getProperty("user.dir");
		highestTestNumber = 59;

		// Some sample texts

		PrintWriter writer = createWriter("text1.txt");
		writer.println("Hello World.\nThis is a text document.\nThe Idea of computing will change The World.\nFor this we celebrate.");
		writer.close();

		writer = createWriter("text2.txt");
		writer.println("Brandeis' CS department has many things going for it:\n -Its students\n -Its faculty\n -Pizza");
		writer.close();

		writer = createWriter("text3.txt");
		writer.close();

		writer = createWriter("text4.txt");
		for (int i = 0; i < 10000; i++){
			char name1 = (char) ('a' + i % 26);
			char name2 = (char) ('a' + (i*7) % 26);
			char name3 = (char) ('a' + (i*5) % 26);
			char name4 = (char) ('a' + (i*3) % 26);
			String name = "";
			if (i % 10 == 0) {
				name = "Anna";
			} else {
				name += name1 + name2 + name3 + name4;
			}
			writer.println("Happy Birthday to "+ name +"!");
		}
		writer.close();

		// Tests for piping and UNIX-like functionality

		writer = createWriter("test1.txt");
		writer.println("exit");
		writer.close();

		writer = createWriter("test2.txt");
		writer.println("cat text1.txt\nexit");
		writer.close();

		writer = createWriter("test3.txt");
		writer.println("cat text1.txt text2.txt\nexit");
		writer.close();

		writer = createWriter("test4.txt");
		writer.println("cat text2.txt | grep Its\nexit");
		writer.close();

		writer = createWriter("test5.txt");
		writer.println("cat text2.txt | grep -\nexit");
		writer.close();

		writer = createWriter("test6.txt");
		writer.println("cat text2.txt text1.txt| grep I\nexit");
		writer.close();

		writer = createWriter("test7.txt");
		writer.println("cat text2.txt text1.txt| grep I | grep -\nexit");
		writer.close();

		writer = createWriter("test8.txt");
		writer.println("cat text2.txt text1.txt| grep I | grep - | grep students\nexit");
		writer.close();

		writer = createWriter("test9.txt");
		writer.println("cat text4.txt | lc\nexit");
		writer.close();

		writer = createWriter("test10.txt");
		writer.println("cat text4.txt | grep Anna | lc\nexit");
		writer.close();

		writer = createWriter("test11.txt");
		writer.println("cat text4.txt | grep Anna | lc | lc \nexit");
		writer.close();

		writer = createWriter("test12.txt");
		writer.println("ls\nexit");
		writer.close();

		writer = createWriter("test13.txt");
		writer.println("pwd\nexit");
		writer.close();

		writer = createWriter("test14.txt");
		writer.println("cd ..\npwd\nexit");
		writer.close();

		writer = createWriter("test15.txt");
		writer.println("cd bin\npwd\nexit");
		writer.close();

		writer = createWriter("test16.txt");
		writer.println("cd bin\ncd ..\npwd\nexit");
		writer.close();

		writer = createWriter("test17.txt");
		writer.println("cd bin\ncd test\nhistory > newFile17.txt\ncat newFile17.txt\nexit");
		writer.close();

		writer = createWriter("test18.txt");
		writer.println("cd bin\npwd\ncd ..\npwd\ncd src\npwd\ncd ..\npwd\nhistory\nexit");
		writer.close();

		writer = createWriter("test19.txt");
		writer.println("history\nexit");
		writer.close();

		writer = createWriter("test20.txt");
		writer.println("ls | grep test | lc\nexit");
		writer.close();

		writer = createWriter("test21.txt");
		writer.println("ls | grep test | lc\nexit");
		writer.close();

		writer = createWriter("test22.txt");
		writer.println("ls | grep notastringinanything | lc\nexit");
		writer.close();

		writer = createWriter("test23.txt");
		writer.println("cat text3.txt | lc\nexit");
		writer.close();

		// Tests for Error Handling

		writer = createWriter("test24.txt");
		writer.println("cat notAFile.txt\nexit");
		writer.close();

		writer = createWriter("test25.txt");
		writer.println("cat notAFile.txt | lc\nexit");
		writer.close();

		writer = createWriter("test26.txt");
		writer.println("cd notADirectory\nexit");
		writer.close();

		writer = createWriter("test27.txt");
		writer.println("notACommand text1.txt\nexit");
		writer.close();

		writer = createWriter("test28.txt");
		writer.println("cattest1.txt\nexit");
		writer.close();

		writer = createWriter("test29.txt");
		writer.println("lc | grep hello \nexit");
		writer.close();

		writer = createWriter("test30.txt");
		writer.println("> newFile30.txt\nexit");
		writer.close();

		writer = createWriter("test31.txt");
		writer.println("cat text1.txt | history\nexit");
		writer.close();

		writer = createWriter("test32.txt");
		writer.println("grep hello | grep comoestas | cat text1.txt\nexit");
		writer.close();

		writer = createWriter("test33.txt");
		writer.println("cat text1.txt | grep\nexit");
		writer.close();

		writer = createWriter("test34.txt");
		writer.println("cat | grep ASDF\nexit");
		writer.close();

		writer = createWriter("test35.txt");
		writer.println("cat text1.txt | cd ..\nexit");
		writer.close();

		writer = createWriter("test36.txt");
		writer.println("cat text1.txt >\nexit");
		writer.close();

		// Test Spacing

		writer = createWriter("test37.txt");
		writer.println("cat text4.txt|grep Happy|lc\nexit");
		writer.close();

		writer = createWriter("test38.txt");
		writer.println("cat   text4.txt    |    grep   Happy  |  lc  \nexit");
		writer.close();

		// Test redirection

		writer = createWriter("test39.txt");
		writer.println("cat text4.txt > newFile39.txt\nls\nexit");
		writer.close();

		writer = createWriter("test40.txt");
		writer.println("cat text4.txt | grep Anna > newFile40.txt\nls\nexit");
		writer.close();

		writer = createWriter("test41.txt");
		writer.println("cat text4.txt | grep Anna > newFile41.txt\ncat newFile41.txt | lc\nexit");
		writer.close();

		writer = createWriter("test42.txt");
		writer.println("pwd > newFile42.txt\ncat newFile42.txt\nexit");
		writer.close();

		// More invalid piping tests

		writer = createWriter("test43.txt");
		writer.println("cd .. | lc\nexit");
		writer.close();

		writer = createWriter("test44.txt");
		writer.println("lc | cd ..\nexit");
		writer.close();

		writer = createWriter("test45.txt");
		writer.println("cat text4.txt | cat text2.txt\nexit");
		writer.close();

		writer = createWriter("test46.txt");
		writer.println("cat text4.txt | pwd\nexit");
		writer.close();

		writer = createWriter("test47.txt");
		writer.println("cat text4.txt | ls\nexit");
		writer.close();

		writer = createWriter("test48.txt");
		writer.println("cat text4.txt | history\nexit");
		writer.close();

		writer = createWriter("test49.txt");
		writer.println("grep HelloWorld\nexit");
		writer.close();

		writer = createWriter("test50.txt");
		writer.println("lc | grep 0\nexit");
		writer.close();

		writer = createWriter("test51.txt");
		writer.println("lc | grep 0\nexit");
		writer.close();

		writer = createWriter("test52.txt");
		writer.println("cat text4.txt | grep Happy Birthday\nexit");
		writer.close();

		writer = createWriter("test53.txt");
		writer.println("exit | grep hello\nexit");
		writer.close();

		// Sleep tests

		writer = createWriter("test54.txt");
		writer.println("sleep 5\nexit");
		writer.close();

		writer = createWriter("test55.txt");
		writer.println("sleep -1\nexit");
		writer.close();

		writer = createWriter("test56.txt");
		writer.println("sleep 0\nexit");
		writer.close();

		writer = createWriter("test57.txt");
		writer.println("sleep 5 | grep 2\nexit");
		writer.close();

		writer = createWriter("test58.txt");
		writer.println("sleep 4 > newFile58.txt\ncat newFile58.txt\nexit");
		writer.close();

		writer = createWriter("test59.txt");
		writer.println("cat text4.txt | sleep 5\nexit");
		writer.close();	
	}

	@Before
	public void initialize(){
		System.setProperty("user.dir", workingDirectory);
		outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
	}

	@After
	public void tearDown(){
		System.setOut(null);
		System.setIn(null);
	}

	@AfterClass 
	public static void cleanUp(){
		if (!debugMode){
			for (int i = 0; i <= highestTestNumber; i++){
				deleteFile("test" + i + ".txt");
				deleteFile("text" + i + ".txt");
				deleteFile("results" + i + ".txt");
				deleteFile("newFile" + i + ".txt");
			}
		}
	}

	@Test
	public void test0(){
		runCommandsAndStoreOutput("test1.txt", "results1.txt");
		String result = readFile("results1.txt");
		int count = (result.length() - result.replace("> ", "").length())/2;
		boolean successfulExit = result.contains("REPL exits. Bye.");
		Assert.assertEquals("The REPL loop must print out the correct number of prompts.", 1, count);
		Assert.assertTrue("The REPL loop says goodbye!", successfulExit);

	}

	@Test
	public void test1(){
		runCommandsAndStoreOutput("test1.txt", "results1.txt");
		String result = readFile("results1.txt");
		boolean success = result.contains("> ");
		Assert.assertTrue("The REPL loop must print out a prompt (> ) and exit successfully.", success);
	}

	@Test
	public void test2(){
		runCommandsAndStoreOutput("test2.txt", "results2.txt");
		String result = readFile("results2.txt");
		Boolean success = result.contains("Hello World.\nThis is a text document.\nThe Idea of computing will change The World.\nFor this we celebrate.");
		Assert.assertTrue("The Cat command successfully prints out Text1.", success);
		System.out.println(result);
	}

	@Test
	public void test3(){
		runCommandsAndStoreOutput("test3.txt", "results3.txt");
		String result = readFile("results3.txt");
		int location1 = result.indexOf("Hello World.\nThis is a text document.\nThe Idea of computing will change The World.\nFor this we celebrate.");
		int location2 = result.indexOf("Brandeis' CS department has many things going for it:\n -Its students\n -Its faculty\n -Pizza");
		Assert.assertTrue("Successfully prints out Text1.", location1 > 0);
		Assert.assertTrue("Successfully prints out Text2.", location2 > 0);
		Assert.assertTrue("Prints out Text1 and Text2 in the correct order.", location1 < location2);
	}

	@Test
	public void test4(){
		runCommandsAndStoreOutput("test4.txt", "results4.txt");
		String result = readFile("results4.txt");
		boolean failure = result.contains("Brandeis' CS department has many things going for it:\n -Its students\n -Its faculty\n -Pizza");
		Assert.assertFalse("Does not print out all of text3.", failure);
		boolean success = result.contains(" -Its students\n -Its faculty");
		Assert.assertTrue("Successfully uses Grep on 'Its'.", success);
	}

	@Test
	public void test5(){
		runCommandsAndStoreOutput("test5.txt", "results5.txt");
		String result = readFile("results5.txt");
		boolean failure = result.contains("Brandeis' CS department has many things going for it:\n -Its students\n -Its faculty\n -Pizza");
		Assert.assertFalse("Does not print out all of text3.", failure);
		boolean success = result.contains(" -Its students\n -Its faculty\n -Pizza");
		Assert.assertTrue("Successfully uses Grep on the character -.", success);
		int count = result.length() - result.replace(">", "").length();
		success = count == 2;
		Assert.assertTrue("Prints the correct number of prompts (2).", success);
	}

	@Test
	public void test6(){
		runCommandsAndStoreOutput("test6.txt", "results6.txt");
		String result = readFile("results6.txt");
		boolean success = result.contains(" -Its students\n -Its faculty\nThe Idea of computing will change The World.");
		Assert.assertTrue("Does not successfully grep for the string 'I'.", success);
	}

	@Test
	public void test7(){
		runCommandsAndStoreOutput("test7.txt", "results7.txt");
		String result = readFile("results7.txt");
		boolean success = result.contains(" -Its students\n -Its faculty");
		Assert.assertTrue("Does not successfully grep for the string 'I' and the character '-'.", success);
	}

	@Test
	public void test8(){
		runCommandsAndStoreOutput("test8.txt", "results8.txt");
		String result = readFile("results8.txt");
		boolean failure = result.contains(" -Its students\n -Its faculty");
		boolean success = result.contains(" -Its students");
		Assert.assertTrue("Needs to be able to pipe through grep commands", success);
		Assert.assertFalse("Does not complete all piping commands", failure);
	}

	@Test
	public void test9(){
		runCommandsAndStoreOutput("test9.txt", "results9.txt");
		String result = readFile("results9.txt");
		boolean failure = result.contains("Hello");
		boolean success = result.contains("10000");
		Assert.assertTrue("Must be able to count lines using the lc tool", success);
		Assert.assertFalse("Using LC should not print out any text it parses.", failure);
	}

	@Test
	public void test10(){
		runCommandsAndStoreOutput("test10.txt", "results10.txt");
		String result = readFile("results10.txt");
		boolean failure = result.contains("Hello");
		boolean success = result.contains("1000");
		Assert.assertTrue("Must be able to count lines using the lc tool (PSRQ) (expected 10)", success);
		Assert.assertFalse("Using LC should not print out any text it parses.", failure);
	}

	@Test
	public void test11(){
		runCommandsAndStoreOutput("test11.txt", "results11.txt");
		String result = readFile("results11.txt");
		boolean failure = result.contains("Hello");
		boolean success = result.contains("> 1\n");
		Assert.assertTrue("Must be able to count lines using the lc tool from any input (including lc) (expected 1)", success);
		Assert.assertFalse("Using LC should not print out any text it parses.", failure);
	}

	@Test
	public void test12(){
		runCommandsAndStoreOutput("test12.txt", "results12.txt");
		String result = readFile("results12.txt");
		boolean success = result.contains("text1.txt") && result.contains("text2.txt") && result.contains("text3.txt");
		Assert.assertTrue("The LS command must list all files in the current working directory", success);
		success = result.contains("test1.txt\ntest10.txt\ntest11.txt\ntest12.txt\ntest13.txt\ntest14.txt\n");
		Assert.assertTrue("The LS command must list files in the current directory in alphabetical order", success);

		int count = (result.length() - result.replace("> ", "").length())/2;
		success = count == 2;
		Assert.assertTrue("Prints the correct number of prompts (2).", success);		
	}

	@Test
	public void test13(){
		runCommandsAndStoreOutput("test13.txt", "results13.txt");
		String result = readFile("results13.txt");
		Scanner s = new Scanner(result);
		boolean found = false;
		String pwd = null;
		while (s.hasNextLine() && !found){
			String line = s.nextLine();
			if (line.contains("> ")){
				pwd = line.substring(2);
				found = true;
			}
		}
		String currentDirectory = System.getProperty("user.dir");
		Assert.assertEquals("The pwd command must return the current working directory.", currentDirectory, pwd);
	}

	@Test
	public void test14(){
		File currentFile = new File("");
		String parentDirectory = currentFile.getAbsoluteFile().getParent();
		runCommandsAndStoreOutput("test14.txt", "results14.txt");
		String result = readFile("results14.txt");
		Scanner s = new Scanner(result);
		boolean found = false;
		String pwd = null;
		String sep = System.getProperty("file.separator");
		String line = null;
		while (s.hasNextLine() && !found){
			line = s.nextLine();
			if (line.contains(sep)){
				pwd = line.replace(">", "").trim();
				found = true;
			}
		}
		Assert.assertEquals("The cd command with (..) must change the current working directory to the parent directory. Relies on pwd being implemented correctly.", parentDirectory, pwd);
	}

	@Test
	public void test15(){
		String currentDirectory = new File("").getAbsolutePath();
		runCommandsAndStoreOutput("test15.txt", "results15.txt");
		String result = readFile("results15.txt");
		Scanner s = new Scanner(result);
		boolean found = false;
		String pwd = null;
		String line = null;
		String sep = System.getProperty("file.separator");
		while (s.hasNextLine() && !found){
			line = s.nextLine();
			if (line.contains(sep)){
				pwd = line.replaceAll(">", "").trim();
				found = true;
			}
		}
		String childDirectory = currentDirectory + sep +"bin";
		Assert.assertEquals("The cd command with a directory name must change the current working directory to the specified child directory. Relies on pwd being implemented correctly.", childDirectory, pwd);
	}

	@Test
	public void test16(){
		runCommandsAndStoreOutput("test16.txt", "results16.txt");
		String result = readFile("results16.txt");
		Scanner s = new Scanner(result);
		boolean found = false;
		String pwd = null;
		String sep = System.getProperty("file.separator");
		while (s.hasNextLine() && !found){
			String line = s.nextLine();
			if (line.contains(sep)){
				pwd = line.replaceAll(">", "").trim();
				found = true;
			}
		}
		String currentDirectory = System.getProperty("user.dir");
		Assert.assertEquals("The cd command with a directory name must change the current working directory to the specified child directory. Relies on pwd being implemented correctly.", currentDirectory, pwd);
		s.close();
	}

	@Test
	public void test17(){
		runCommandsAndStoreOutput("test17.txt", "results17.txt");
		String result = readFile("results17.txt");
		boolean success = result.contains("cd bin\ncd test");
		Assert.assertTrue("all commands must run from the current working directory, which can change given user instructions.", success);
		if (! debugMode){
			File toDelete = new File("bin/test/results17.txt");
			toDelete.delete();
		}
	}

	@Test
	public void test18(){
		runCommandsAndStoreOutput("test18.txt", "results18.txt");
		String result = readFile("results18.txt");
		int numCDs = (result.length() - result.replace("cd ", "").length())/3;	
		int numPWDs = (result.length() - result.replace("pwd", "").length())/3;	
		Assert.assertEquals("The history command must print out all previous commands.", 4, numCDs);		
		Assert.assertEquals("The history command must print out all previous commands.", 4, numPWDs);
	}

	@Test
	public void test19(){
		runCommandsAndStoreOutput("test19.txt", "results19.txt");
		String result = readFile("results19.txt");
		int count = (result.length() - result.replace("> ", "").length())/2;
		boolean success = count == 2;
		boolean failure = result.contains("> history");
		Assert.assertFalse("The history command should not output itself.", failure);
		Assert.assertTrue("There should be two prompts (> )", success);
	}

	@Test
	public void test20(){
		runCommandsAndStoreOutput("test20.txt", "results20.txt");
		String result = readFile("results20.txt");
		Scanner s = new Scanner(result);
		int numberOfTests = -1;
		while (s.hasNextLine()){
			String line = s.nextLine();
			if (line.contains("> ") && line.length() > 2){
				String value = line.substring(2);
				try {
					numberOfTests = Integer.parseInt(value);
				} catch (NumberFormatException e){}
			}
		}		
		Assert.assertEquals("LC doesn't propperly count the number of lines of output for piped input.", 59, numberOfTests);
	}

	@Test
	public void test21(){
		runCommandsAndStoreOutput("test21.txt", "results21.txt");
		String result = readFile("results21.txt");
		boolean success = result.contains("59");
		Assert.assertTrue("There should be 59 test files. Run in debug mode if this breaks to see the files that appear.", success);
	}

	@Test
	public void test22(){
		runCommandsAndStoreOutput("test22.txt", "results22.txt");
		String result = readFile("results22.txt");
		boolean success = result.contains("> 0\n");
		Assert.assertTrue("The lc command should be able to process 0 lines of input", success);
	}

	@Test
	public void test23(){
		runCommandsAndStoreOutput("test23.txt", "results23.txt");
		String result = readFile("results23.txt");
		boolean success = result.contains("> 0\n");
		Assert.assertTrue("The lc command should be able to process 0 lines of input", success);
	}

	// TESTS OF ERROR HANDLING!!!

	@Test
	public void test24(){
		runCommandsAndStoreOutput("test24.txt", "results24.txt");
		String result = readFile("results24.txt");
		boolean success = result.contains("file not found");
		Assert.assertTrue("The loop should check that the file/directory does not exist.", success);
	}

	@Test
	public void test25(){
		runCommandsAndStoreOutput("test25.txt", "results25.txt");
		String result = readFile("results25.txt");
		boolean success = result.contains("file not found");
		Assert.assertTrue("The loop should check that the file/directory does not exist, and should not start piping commands", success);
	}

	@Test
	public void test26(){
		runCommandsAndStoreOutput("test26.txt", "results26.txt");
		String result = readFile("results26.txt");
		boolean success = result.contains("directory not found");
		Assert.assertTrue("The loop should check that the file/directory does not exist", success);
	}

	@Test
	public void test27(){
		runCommandsAndStoreOutput("test27.txt", "results27.txt");
		String result = readFile("results27.txt");
		boolean success = result.contains("invalid command");
		Assert.assertTrue("The loop should check for unrecognized commands", success);
	}

	@Test
	public void test28(){
		runCommandsAndStoreOutput("test28.txt", "results28.txt");
		String result = readFile("results28.txt");
		boolean success = result.contains("invalid command");
		Assert.assertTrue("The loop should check for unrecognized commands", success);
	}

	@Test
	public void test29(){
		runCommandsAndStoreOutput("test29.txt", "results29.txt");
		String result = readFile("results29.txt");
		boolean success = result.contains("invalid pipe order");
		Assert.assertTrue("The loop should ensure a valid piping order.", success);
	}

	@Test
	public void test30(){
		runCommandsAndStoreOutput("test30.txt", "results30.txt");
		String result = readFile("results30.txt");
		boolean success = result.contains("invalid pipe order") || result.contains("invalid command");
		Assert.assertTrue("The loop should ensure a valid piping order.", success);
	}

	@Test
	public void test31(){
		runCommandsAndStoreOutput("test31.txt", "results31.txt");
		String result = readFile("results31.txt");
		boolean success = result.contains("invalid pipe order");
		Assert.assertTrue("The loop should ensure a valid piping order.", success);
	}

	@Test
	public void test32(){
		runCommandsAndStoreOutput("test32.txt", "results32.txt");
		String result = readFile("results32.txt");
		boolean success = result.contains("invalid pipe order");
		Assert.assertTrue("The loop should ensure a valid piping order.", success);
	}

	@Test
	public void test33(){
		runCommandsAndStoreOutput("test33.txt", "results33.txt");
		String result = readFile("results33.txt");
		boolean success = result.contains("missing argument");
		Assert.assertTrue("The loop must make sure that all commands are given their appropriate inputs, and if not must report an missing argument.", success);
	}

	@Test
	public void test34(){
		runCommandsAndStoreOutput("test34.txt", "results34.txt");
		String result = readFile("results34.txt");
		boolean success = result.contains("missing argument");
		Assert.assertTrue("The loop must make sure that all commands are given their appropriate inputs, and if not must report an missing argument.", success);
	}

	@Test
	public void test35(){
		runCommandsAndStoreOutput("test35.txt", "results35.txt");
		String result = readFile("results35.txt");
		boolean success = result.contains("invalid pipe order");
		Assert.assertTrue("The loop must check for invalid piping orders.  cat file.txt | cd is should not be allowed.", success);
	}

	@Test
	public void test36(){
		runCommandsAndStoreOutput("test36.txt", "results36.txt");
		String result = readFile("results36.txt");
		boolean success = result.contains("missing argument");
		Assert.assertTrue("redirection must be given the name of a file to redirect to.", success);
	}

	@Test
	public void test37(){
		runCommandsAndStoreOutput("test37.txt", "results37.txt");
		String result = readFile("results37.txt");
		boolean success = result.contains("> 10000");
		Assert.assertTrue("The loop should parse well, even without 'proper' spacing.", success);
	}

	@Test
	public void test38(){
		runCommandsAndStoreOutput("test38.txt", "results38.txt");
		String result = readFile("results38.txt");
		boolean success = result.contains("> 10000");
		Assert.assertTrue("The loop should parse commands, even without what we would consider 'proper' spacing.", success);
	}

	@Test
	public void test39(){
		runCommandsAndStoreOutput("test39.txt", "results39.txt");
		String result = readFile("results39.txt");
		boolean success = result.contains("newFile39.txt");
		Assert.assertTrue("The loop should allow redirection of piped input into a file. Subsequent actions (including cat, ls and >), should be aware that this file is here.", success);
	}

	@Test
	public void test40(){
		runCommandsAndStoreOutput("test40.txt", "results40.txt");
		String result = readFile("results40.txt");
		boolean success = result.contains("newFile40.txt");
		Assert.assertTrue("The loop should allow redirection of any piped input into a file.", success);
	}

	@Test
	public void test41(){
		runCommandsAndStoreOutput("test41.txt", "results41.txt");
		String result = readFile("results41.txt");
		boolean success = result.contains("> 10");
		Assert.assertTrue("You should be able to manipulate the files you create. (Consider closing your PrintStreams!)", success);
	}

	@Test
	public void test42(){
		runCommandsAndStoreOutput("test42.txt", "results42.txt");
		String result = readFile("results42.txt");
		String pwd = System.getProperty("user.dir");
		boolean success = result.contains("> " + pwd);
		Assert.assertTrue("You should be able to manipulate the files you create. (Consider closing your PrintStreams!)", success);
	}

	@Test
	public void test43(){
		runCommandsAndStoreOutput("test43.txt", "results43.txt");
		String result = readFile("results43.txt");
		boolean success = result.contains("invalid pipe order");
		Assert.assertTrue("cd must be first (and only) in any command it is in (it doesn't pipe)", success);
	}

	@Test
	public void test44(){
		runCommandsAndStoreOutput("test44.txt", "results44.txt");
		String result = readFile("results44.txt");
		boolean success = result.contains("invalid pipe order");
		Assert.assertTrue("cd must be first (and only) in any command it is in (it doesn't pipe)", success);
	}

	@Test
	public void test45(){
		runCommandsAndStoreOutput("test45.txt", "results45.txt");
		String result = readFile("results45.txt");
		boolean success = result.contains("invalid pipe order");
		Assert.assertTrue("cat must be first in any command it is in (it doesn't accept piped input)", success);
	}

	@Test
	public void test46(){
		runCommandsAndStoreOutput("test46.txt", "results46.txt");
		String result = readFile("results46.txt");
		boolean success = result.contains("invalid pipe order");
		Assert.assertTrue("pwd must be first in any command it is in (it doesn't accept piped input)", success);
	}

	@Test
	public void test47(){
		runCommandsAndStoreOutput("test47.txt", "results47.txt");
		String result = readFile("results47.txt");
		boolean success = result.contains("invalid pipe order");
		Assert.assertTrue("ls must be the in any command it is in (it doesn't accept piped input)", success);
	}

	@Test
	public void test48(){
		runCommandsAndStoreOutput("test48.txt", "results48.txt");
		String result = readFile("results48.txt");
		boolean success = result.contains("invalid pipe order");
		Assert.assertTrue("history must be the first in any command it is in (it doesn't accept piped input).", success);
	}

	@Test
	public void test49(){
		runCommandsAndStoreOutput("test49.txt", "results49.txt");
		String result = readFile("results49.txt");
		boolean success = result.contains("invalid pipe order");
		Assert.assertTrue("grep necessitates piped input", success);
	}

	@Test
	public void test51(){
		runCommandsAndStoreOutput("test51.txt", "results51.txt");
		String result = readFile("results51.txt");
		boolean success = result.contains("invalid pipe order");
		Assert.assertTrue("lc necessitates piped input", success);
	}

	@Test
	public void test52(){
		runCommandsAndStoreOutput("test52.txt", "results52.txt");
		String result = readFile("results52.txt");
		boolean success = result.contains("invalid argument");
		Assert.assertTrue("Grep should only accept a contiguous string (see Invalid Arguments).", success);
	}

	@Test
	public void test53(){
		runCommandsAndStoreOutput("test53.txt", "results53.txt");
		String result = readFile("results53.txt");
		boolean success = result.contains("invalid pipe order");
		Assert.assertTrue("The exit command does not pipe output.", success);
	}

	@Test
	public void test54(){
		runCommandsAndStoreOutput("test54.txt", "results54.txt");
		String result = readFile("results54.txt");
		Assert.assertTrue("The sleep method does not appear to be implemented.", result.contains("Sleep: 4 seconds left."));
		Assert.assertFalse("The sleep method should count down like a timer (see example).", result.contains("Sleep: 5 seconds left."));
		Assert.assertTrue("The sleep method should count down to (and display) 0.", result.contains("Sleep: 0 seconds left."));
	}

	@Test
	public void test55(){
		runCommandsAndStoreOutput("test55.txt", "results55.txt");
		String result = readFile("results55.txt");
		Assert.assertTrue("The sleep method takes a non-negative integer as input.", result.contains("invalid argument"));
	}

	@Test
	public void test56(){
		runCommandsAndStoreOutput("test56.txt", "results56.txt");
		String result = readFile("results56.txt");
		Assert.assertFalse("The sleep method takes a non-negative integer as input, zero allowed.", result.contains("invalid argument"));
	}

	@Test
	public void test57(){
		runCommandsAndStoreOutput("test57.txt", "results57.txt");
		String result = readFile("results57.txt");
		Assert.assertTrue("The sleep command must give piped output.", result.contains("Sleep: 2 seconds left."));
		Assert.assertFalse("The sleep command must give piped output.", result.contains("Sleep: 1 seconds left."));
	}

	@Test
	public void test58(){
		runCommandsAndStoreOutput("test58.txt", "results58.txt");
		String result = readFile("results58.txt");
		Assert.assertTrue("The sleep command must give piped output. If this and Test 17 fail, look into closing your streams, or making sure your files are immediately availible for manipulation", result.contains("Sleep: 2 seconds left."));
	}

	@Test
	public void test59(){
		runCommandsAndStoreOutput("test59.txt", "results59.txt");
		String result = readFile("results59.txt");
		Assert.assertTrue("The sleep command must does not accept piped input.", result.contains("invalid pipe order"));
	}




	private static PrintWriter createWriter(String name){
		// A helper method which will create a PrintWriter from a new file name.
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(name, "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return writer;
	}

	public void commandsFromFile(String s){
		//Reads a set of commands from a given input file.
		try {
			inContent = new FileInputStream(s);
			System.setIn(inContent);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void runCommandsAndStoreOutput(String inputFile, String outputFile){
		commandsFromFile(inputFile);

		try {
			EricShell.main(null);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PrintWriter results = createWriter(outputFile);
		String result = outContent.toString();
		results.println(result);
		results.close();
		try {
			inContent.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String readFile(String path) {
		Scanner s = null;
		try {
			s = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String result = "";
		while (s.hasNextLine()){
			result += "\n" + s.nextLine();
		}
		return result;
	}

	private static boolean deleteFile(String relPath){
		File f = new File(relPath);
		return f.delete();
	}
}
