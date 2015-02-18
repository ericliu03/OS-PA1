import java.io.IOException;
import java.util.Scanner;


public class EricShell {
	//Your REPL resides here
	public static void main(String[] args) throws IOException, InterruptedException {
		
		StringBuffer thisFilePath = new StringBuffer(System.getProperty("user.dir"));
		StringBuffer historyCommands = new StringBuffer();
		KeyListener listener = new KeyListener();
		
		CommandManager commandManager = new CommandManager();;
		commandManager.setParameters(thisFilePath, historyCommands, listener);
		
		listener.start();
		System.out.println("Welcome Come!");
		boolean isContinue = true;
		Scanner scanner = new Scanner(System.in);
		
		while(isContinue) {
			System.out.print("> ");
			String commandReceived = new String();
			commandReceived = scanner.nextLine();
			isContinue = commandManager.dealWithCommand(commandReceived);
			listener.unregisterCmd();
		}
		
		listener.stop();
		scanner.close();
		System.out.println("REPL exits. Bye.");
			
	}

}
