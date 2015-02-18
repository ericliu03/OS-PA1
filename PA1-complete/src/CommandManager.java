import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.*;

/*
 * This class manages the lifetime of commands. You need to modify this class such that it can:
 *     1. validate the command
 *     2. create subcommands
 *     3. execute subcommands
 *     4. suspend/stop/resume the command if you are doing Part 4.
 * 
 */


// pwd ls cd cat grep lc > history sleep exit
public class CommandManager {
	
	StringBuffer thisFilePath;
	StringBuffer historyCommands;
	Filter filters[];
	KeyListener listener;
	boolean killed;
	ArrayList<String> hasInCommands = new ArrayList<String>();
	ArrayList<String> noInCommands = new ArrayList<String>();
	ArrayList<String> noInOutCommands = new ArrayList<String>();
	ArrayList<String> allCommands = new ArrayList<String>();
	ArrayList<LinkedBlockingQueue<Object>> blockingQueue = new ArrayList<LinkedBlockingQueue<Object>>();
	
	public CommandManager() {
		
		hasInCommands.add("grep");
		hasInCommands.add("lc");
		
		noInCommands.add("pwd");
		noInCommands.add("ls");
		noInCommands.add("cat");
		noInCommands.add("history");
		noInCommands.add("sleep");

		noInOutCommands.add("cd");
		noInOutCommands.add("exit");
		
		allCommands.add("grep");
		allCommands.add("lc");
		allCommands.add("pwd");
		allCommands.add("ls");
		allCommands.add("cat");
		allCommands.add("history");
		allCommands.add("sleep");
		allCommands.add("cd");
		allCommands.add("exit");
		allCommands.add(">");
		



	}
	public void setParameters(StringBuffer path, StringBuffer history, KeyListener listen){
		thisFilePath = path;
		historyCommands = history;
		listener = listen;
		
		
	}
	
	
	protected boolean dealWithCommand(String commands) throws InterruptedException, IOException {
		//clear the blockingqueue that is used for the last command
		blockingQueue.clear();
		blockingQueue.add(new LinkedBlockingQueue<Object>());
		killed = false;
		//add to history if the cammand not contains history
		if(!commands.contains("history")) {
			historyCommands.append(commands+'\n');
		}
		
		boolean willRedirect = false;
		String fileNameForRedirection = new String();
		listener.registerCmd(this);

		
		String commandSplited[] = commands.trim().split("\\|");
		String subcommandWithParameter[][] = new String[commandSplited.length][20];
		filters = new Filter[commandSplited.length];
		
		
		if (commandSplited[0].equals("")) return true;

		
		for(int n = 0; n < commandSplited.length; n++) {
			
			//check if there is a redirection command(valid or not)
			if(commandSplited[n].contains(">") ){
				if((n == commandSplited.length - 1)){
					String[] temp = commandSplited[n].split(">");
					if(temp.length > 2 || temp.length < 2) {
						System.out.println("redirection: missing argument");
						return true;
					}
					commandSplited[n] = temp[0].trim();
					fileNameForRedirection = temp[1].trim();
					willRedirect = true;
				}
				else {
					System.out.println("redirection: invalid pipe order");
				}
			}
			
//			System.out.print("2");
			subcommandWithParameter[n] = commandSplited[n].replaceAll( "\\s+ ", " ").trim().split(" ");
//			System.out.print('f');
			int isValid = isValidCommand(n, commandSplited.length, subcommandWithParameter);
			
			if(isValid != 0) {
				if(isValid == 1)
					System.out.println(subcommandWithParameter[n][0]+": invalid pipe order");
				else if (isValid ==2)
					System.out.println(subcommandWithParameter[n][0]+": invalid command");
				return true;
			}
			//if it is valid, do something
			
			blockingQueue.add(new LinkedBlockingQueue<Object>());
			
			if (subcommandWithParameter[n][0].equals("pwd") ) {
				//System.out.println(n);
				filters[n] = new FilterPwd(null, blockingQueue.get(n+1));
				((FilterPwd) filters[n]).SetFilePath(thisFilePath);
				filters[n].start();
				
			} else if (subcommandWithParameter[n][0].equals("ls")) {
				filters[n] = new FilterLs(null, blockingQueue.get(n+1));
				((FilterLs) filters[n]).SetFilePath(thisFilePath);
				filters[n].start();
				
			} else if (subcommandWithParameter[n][0].equals("cat")) {
				
				if (subcommandWithParameter[n].length == 1) {
					System.out.println("cat: missing argument");
					return true;
				}
				for(int i = 1; i < subcommandWithParameter[n].length; i++){
					try {
						new FileInputStream(subcommandWithParameter[n][i]);
					} catch (FileNotFoundException e) {
						System.out.println("cat: file not found");
						return true;
					}
				}

				filters[n] = new FilterCat(blockingQueue.get(n), blockingQueue.get(n+1));
				((FilterCat) filters[n]).setParameter(subcommandWithParameter[n]);
				((FilterCat) filters[n]).SetFilePath(thisFilePath);
				filters[n].start();
				
			} else if (subcommandWithParameter[n][0].equals("exit")){
				if (subcommandWithParameter[n].length > 1) {
					System.out.println("exit: invalid argument");
					return true;
				}
				filters[n] = new FilterExit(blockingQueue.get(n), blockingQueue.get(n+1));
				filters[n].start();
				
			} else if (subcommandWithParameter[n][0].equals("grep")){
				if (subcommandWithParameter[n].length == 1) {
					System.out.println("grep: missing argument");
					return true;
				}
				else if (subcommandWithParameter[n].length > 2) {
					System.out.println("grep: invalid argument");
					return true;
				}
				filters[n] = new FilterGrep(blockingQueue.get(n), blockingQueue.get(n+1));
				((FilterGrep) filters[n]).setPattern(subcommandWithParameter[n][1]);
				filters[n].start();
			} else if(subcommandWithParameter[n][0].equals("lc")) {
				if (subcommandWithParameter[n].length > 1) {
					System.out.println("lc: invalid argument");
					return true;
				}
				filters[n] = new FilterLc(blockingQueue.get(n), blockingQueue.get(n+1));
				filters[n].start();
			} else if(subcommandWithParameter[n][0].equals("cd")) {
				if (subcommandWithParameter[n].length == 1) {
					System.out.println("cd: missing argument");
					return true;
				}
				else if (subcommandWithParameter[n].length > 2) {
					System.out.println("cd: invalid argument");
					return true;
				}
				filters[n] = new FilterCd(blockingQueue.get(n), blockingQueue.get(n+1));
				((FilterCd) filters[n]).SetParameter(subcommandWithParameter[n][1]);
				((FilterCd) filters[n]).SetFilePath(thisFilePath);
				filters[n].start();
			} else if (subcommandWithParameter[n][0].equals("history")){
				if (subcommandWithParameter[n].length > 1) {
					System.out.println("cd: invalid argument");
					return true;
				}
				filters[n] = new FilterHistory(blockingQueue.get(n), blockingQueue.get(n+1));
				((FilterHistory) filters[n]).setHistoryCommands(historyCommands);
				filters[n].start();
			} else if (subcommandWithParameter[n][0].equals("sleep")){
				if (subcommandWithParameter[n].length > 2 || 
						Integer.valueOf(subcommandWithParameter[n][1]).intValue() < 0 ) {
					System.out.println("sleep: invalid argument");
					return true;
				} else if (subcommandWithParameter[n].length == 1 ){
					System.out.println("sleep: missing argument");
					return true;
				}
				filters[n] = new FilterSleep(blockingQueue.get(n), blockingQueue.get(n+1));
				((FilterSleep) filters[n]).setSeconds(subcommandWithParameter[n][1]);
				((FilterSleep) filters[n]).setOutput(subcommandWithParameter.length == 1);
				filters[n].start();
			}else {
				System.out.println("Command not inplemented yet");
				return true;
			}

		}

		for (Filter filter: filters) {
			//System.out.print("1");
			try {
				//System.out.println(filters.length);
				filter.join();
				//if the user cd to a file, or to a path that not exists
				if(filter instanceof FilterCd && ((FilterCd) filter).isValidPath == false) {
					System.out.println("cd: directory not found");
					return true;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(this.killed) {

				System.out.println("command has been killed");
				return true;
			}
		}
		//System.out.println(out.size());
		if(willRedirect){
				Object temp;
				FileWriter fileWriter = new FileWriter(fileNameForRedirection);

				while(!blockingQueue.get(commandSplited.length).isEmpty()) {
					temp = blockingQueue.get(commandSplited.length).take();
					//if the temp is false means the exit has been used, so exit
					if(temp.toString() == "false") {
						fileWriter.close();
						return false;
					}
					//if the end of the ouput comes up, return to loop
					else if(temp instanceof queueEnded) {
						fileWriter.close();
						return true;
					}
					
					fileWriter.write(temp+"\n");
					fileWriter.flush();
				} 
				
				fileWriter.close();
		} else {
			try {
				Object temp;
				while(!blockingQueue.get(commandSplited.length).isEmpty()) {
					temp = blockingQueue.get(commandSplited.length).take();
					//if the temp is false means the exit has been used, so exit
					if(temp.toString() == "false") return false;
					//if the end of the ouput comes up, return to loop
					else if(temp instanceof queueEnded) return true;
					System.out.println(temp.toString());
				} 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//System.out.println(thisFilePath);
		return true;
	}
	
	protected void writeFile(String content, String fileName) throws IOException{


	}
	protected int isValidCommand(int n, int length, String[][] subcommandWithParameters){
		//0 valid, 1 invalid pipe order, 2invalid command
		if(! allCommands.contains(subcommandWithParameters[n][0]))
			return 2;
		if(length != 1 && noInOutCommands.contains(subcommandWithParameters[n][0]))
			return 1;
		else if(n != 0 && noInCommands.contains(subcommandWithParameters[n][0]))
			return 1;
		else if( n == 0 && hasInCommands.contains(subcommandWithParameters[n][0]))
			return 1;
		else
			return 0;
		
	}
	/*
	 * This is for Part 4
	 */
	public void kill() {
		this.killed = true;

		for (Filter filter: this.filters) {
			filter.cmdKill();
			}
	}
}
