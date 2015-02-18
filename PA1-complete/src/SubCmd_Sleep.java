import java.util.concurrent.BlockingQueue;

/*
 * This is only partially implemented and the duration is set to 5 seconds by default. 
 * Your implementation should be able to set duration by passing argument.
 */
public class SubCmd_Sleep extends Filter {
	long duration = 5;

	public SubCmd_Sleep(BlockingQueue<Object> in, BlockingQueue<Object> out) throws InterruptedException {
		super(in, out);
	}

	@Override
	public void run() {
		while (!this.done) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println(Thread.currentThread().getName() + " 's sleep has been interrupted.");
			}
			System.out.println("Sleep: " + --duration + " seconds left.");
			if (duration == 0) {
				this.done = true;
			}
		}
	}

	@Override
	public Object transform(Object o) {
		return null;
	}
}
