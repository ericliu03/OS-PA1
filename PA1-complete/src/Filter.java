import java.util.concurrent.*;
import java.io.*;

/*
 * This is the Filter class all you command implementation needs to extend.
 */
public abstract class Filter extends Thread {
	protected BlockingQueue<Object> in;
	protected BlockingQueue<Object> out;
	protected volatile boolean done;
	/*
	 * The following flag is for Part 4.
	 */
	protected volatile boolean killed;

	public Filter (BlockingQueue<Object> in, BlockingQueue<Object> out) {
		if (in != null) this.in = in;
		if (out != null) this.out = out;
		this.done = false;
		this.killed = false;
	}


	/*
	 * This is for Part 4.
	 */
	public void cmdKill() {
		this.killed = true;
	}
	/*
	 * This method need to be overridden.
	 * @see java.lang.Thread#run()
	 */
	public void run() {
        Object o = null;
        while(!this.done) {
			// read from input queue, may block
            try {
				o = in.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}    
            
			// allow filter to change message
            o = transform(o); 

			// forward to output queue
            try {
				out.put(o);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}       
        }
	}

	/*
	 * This method might need to be overridden.
	 */
	public abstract Object transform(Object o);
}

class queueEnded {
	public queueEnded(){
	}
}
class FilterPwd extends Filter{

	protected String filePath;
	public FilterPwd(BlockingQueue<Object> in, BlockingQueue<Object> out) {
		super(null, out);
		// TODO Auto-generated constructor stub
	}

	public void SetFilePath(StringBuffer thisFilePath){
		filePath = thisFilePath.toString();
	}
	@Override
	public void run() {
		Object o = null;
		o = transform(o);
		try {
			out.put(o);
			out.put(new queueEnded());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Override
	public Object transform(Object o) {
		return filePath;
	}
	
}

class FilterLs extends Filter{
	protected StringBuffer filePath;
	public FilterLs(BlockingQueue<Object> in, BlockingQueue<Object> out) {
		super(null, out);
		// TODO Auto-generated constructor stub
	}


	public void SetFilePath(StringBuffer thisFilePath){
		filePath = thisFilePath;
	}
	@Override
	public void run() {
		Object o = null;
	
		o = transform(o);
		try {

			out.put(new queueEnded());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Object transform(Object o) {
		
		File myDir = new File(filePath.toString());
		String[] contents = myDir.list(); 
		
		for(String fileNames : contents) { 
			if(this.killed) return 0;
			try {
				
				out.put(fileNames);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		return 0;
	}
}
		

class FilterCat extends Filter {

	protected String parameter[] = new String[10];
	protected StringBuffer filePath;

	public FilterCat(BlockingQueue<Object> in, BlockingQueue<Object> out) {
		super(null, out);
	}

	public void setParameter(String[] parameters){
		parameter = parameters;
	}
	public void SetFilePath(StringBuffer path){
		filePath = path;
	}
	@Override
	public void run() {
		Object o = null;
		for(int n = 1; n < parameter.length; n++) {
			if(this.killed) break;
			o = parameter[n];
			o = transform(o);
			if(o.equals(false)) {
				break;
			}
		}
		try {
			out.put(new queueEnded());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public Object transform(Object o){
		String content = new String();
		String temp = new String();
		FileInputStream file;
		
		try {
			file = new FileInputStream(o.toString());
		} catch (FileNotFoundException e) {
			return false;
		}
		
		BufferedReader reader=new BufferedReader(new InputStreamReader(file));
		
		
		try {
			while( (temp = reader.readLine()) !=null && !this.killed){
				out.put(temp);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//System.out.println(content);
		return content;
	}
}

class FilterExit extends Filter{

	public FilterExit(BlockingQueue<Object> in, BlockingQueue<Object> out) {
		super(null, out);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		Object o = null;
		o = transform(o);
		try {
			out.put(o);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public Object transform(Object o) {
		return false;
	}
}

class FilterGrep extends Filter{

	private String pattern;
	
	public FilterGrep(BlockingQueue<Object> in, BlockingQueue<Object> out) {
		super(in, out);
		// TODO Auto-generated constructor stub
	}

	public void setPattern(String p){
		this.pattern = p;
	}
	@Override
	public void run() {
		Object o = null;
		try {
			while(!this.killed) {
				o = in.take();
				if (o instanceof queueEnded) break;
				o = transform(o);
			}
			out.put(new queueEnded());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public Object transform(Object o) {
		try {
			if(o.toString().contains(pattern)){
				out.put(o.toString());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
}

class FilterLc extends Filter{

	int numberOfLines = 0;
	
	public FilterLc(BlockingQueue<Object> in, BlockingQueue<Object> out) {
		super(in, out);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		Object o = null;
		
		try {
			//since in other commands I use out.put for each line
			//so in this command I count for the number in blockingqueue
			while(!this.killed) {
				o = in.take();
				if (o instanceof queueEnded) break;
				transform(o);
			}
			out.put(numberOfLines);
			out.put(new queueEnded());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public Object transform(Object o) {
		numberOfLines += 1;
		return 0;
	}
	
}
class FilterCd extends Filter{

	protected StringBuffer filePath;
	protected String parameter = new String();
	boolean isValidPath = true;
	public FilterCd(BlockingQueue<Object> in, BlockingQueue<Object> out) {
		super(null, out);
		// TODO Auto-generated constructor stub
	}
	public void SetParameter(String p){
		parameter = p;
	}
	public void SetFilePath(StringBuffer path){
		this.filePath = path;
	}
	@Override
	public void run() {
		String directoryNames[] = parameter.split(System.getProperty("file.separator"));
		//in order to get the original path
		File myDir = new File(filePath.toString());
		
		for(int i = 0; i < directoryNames.length; i++) {
			if(this.killed) break;
			
			if(directoryNames[i].equals("..")) {
				// replace filePath to its parent path
				filePath.replace(0, filePath.length(), myDir.getParent());
				
			} else if (directoryNames[i].equals(".")){
				//pass
			} else {
				filePath.append("/").append(directoryNames[i]);
			}
			
			//set myDir to newest filePath each loop and check whether it is a valid directory
			myDir = new File(filePath.toString());
			if (!myDir.isDirectory()) {
				isValidPath = false;
				System.out.println(i);
				break;
			}
			//System.out.println(filePath);
		}
	}
	@Override
	public Object transform(Object o) {
		return 0;
	}
}

class FilterHistory extends Filter{

	String historyCommands = new String();
	
	public FilterHistory(BlockingQueue<Object> in, BlockingQueue<Object> out) {
		super(in, out);
		// TODO Auto-generated constructor stub
	}

	public void setHistoryCommands(StringBuffer history){
		historyCommands = history.toString();
	}
	@Override
	public void run() {
		try {
			out.put(historyCommands);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public Object transform(Object o) {
		return 0;
	}
	
}	

class FilterSleep extends Filter{

	int seconds = 0;
	boolean willOutput = true;
	
	public FilterSleep(BlockingQueue<Object> in, BlockingQueue<Object> out) {
		super(in, out);
		// TODO Auto-generated constructor stub
	}

	public void setSeconds(String second){
		seconds = Integer.valueOf(second).intValue();
	}
	public void setOutput(boolean willOutput){
		this.willOutput = willOutput;
		
	}
	@Override
	public void run() {
		try {
			while(seconds-- > 0 && !this.killed){
				if(willOutput){
					System.out.printf("Sleep: %d seconds left.\n", seconds );
				}
				else {
						out.put("Sleep: " + seconds  + " seconds left.\n");	
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.err.println(Thread.currentThread().getName() + " 's sleep has been interrupted.");
				}
			}
			out.put(new queueEnded());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Override
	public Object transform(Object o) {
		return 0;
	}
	
}	
	