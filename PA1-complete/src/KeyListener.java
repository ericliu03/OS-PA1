
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.*;

/*
 * As to Part 4, generally, you don't need to change anything in this file. 
 * But you have to understand what killCommand() does.
 * Don't change codes without consulting the TA.
 * 
 * This KeyListener is running in an independent thread.
 */
public class KeyListener implements NativeKeyListener {
	private CommandManager cmd;

	/*
	 * register the command manager to listen the key events.
	 */
	public void registerCmd(CommandManager cmd) {
		this.cmd = cmd;
	}
	/*
	 * Unregister the current command manager.
	 */
	public void unregisterCmd() {
		this.cmd = null;
	}
	/*
	 * Keys have been pressed
	 */
	private final Set<Integer> pressed = new TreeSet<Integer>();
	/*
	 * Logging
	 */
	private Logger logger;
	
	/*
	 * Handling Control-C
	 */
	private void killCommand() {
		System.out.print("Control-C caught.\n> ");
		if (cmd != null) {
			//you need to modify the kill() method in the CommandManager class
			cmd.kill();
		}
		//unregister the cmd
		cmd = null;
	}
	/*
	 * Call to start the KeyListener.
	 */
	public void start() {
		//Log settings for keylistener.
		logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		LogManager.getLogManager().reset();
		logger.setLevel(Level.WARNING);
		
		try {
			GlobalScreen.registerNativeHook();
			GlobalScreen.getInstance().addNativeKeyListener(this);
			//System.out.println("Key listener is running");
		} catch (NativeHookException e) {
			System.err.println(e.getMessage());
			System.err.println("Failed to registerNativeHook. This error should not happen frequently.");
			System.err.println("If you keep seeing this error message, please contact TA.");
		}
	}

	/*
	 * Call to stop the KeyListener. 
	 * It should always be called when the program is going to exit.
	 */
	public void stop() {
		GlobalScreen.getInstance().removeNativeKeyListener(this);
		GlobalScreen.unregisterNativeHook();
		//System.out.println("Key listener stops");
	}

	/*
	 * Only captures Control-C
	 */
	@Override
	public void nativeKeyPressed(NativeKeyEvent arg0) {
		pressed.add(arg0.getKeyCode());
		if (pressed.size() == 2) {
			int pos = 0;
			for (int keycode : pressed) {
				if (pos == 0) {
					if (keycode == NativeKeyEvent.VC_CONTROL_L || keycode == NativeKeyEvent.VC_CONTROL_L) {
						pos++;
						continue;
					} else {
						return;
					}
				} else if (pos == 1) {
					if (keycode == NativeKeyEvent.VC_C) {
						killCommand();
					} else {
						return;
					}
				} else {
					return;
				}
			}
		}
	}


	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {
		pressed.remove(arg0.getKeyCode());
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
	}
}
