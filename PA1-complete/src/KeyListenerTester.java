import java.util.Scanner;


public class KeyListenerTester {
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		String line;
		KeyListener listener = new KeyListener();
		listener.start();
		System.out.println("Waiting for key inputs.");

		while (true) {
			line = scan.nextLine();
			if (line.equals("quit")) {
				break;
			}
			System.out.println("Repeat: " + line);
		}
		System.out.println("Quit");
		scan.close();
		listener.stop();
	}
}
