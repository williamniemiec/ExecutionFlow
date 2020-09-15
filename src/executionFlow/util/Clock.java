package executionFlow.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Responsible for calling routines after a certain time or whenever the the 
 * timer expires.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class Clock 
{
	/**
	 * Stores routines sent to the setInterval method along with its ids.
	 */
	private static Map<Integer, Timer> intervalRoutines = new HashMap<>();
	
	
	/**
	 * Sets a timer which executes a routine once the timer expires.
	 * 
	 * @param		routine Routine to be performed
	 * @param		delay Waiting time before the routine is executed (in 
	 * milliseconds)
	 */
	public static void setTimeout(Runnable routine, int delay)
	{
		new Thread(() -> {
	        try {
	            Thread.sleep(delay);
	            routine.run();
	        }
	        catch (Exception e) {
	            System.err.println(e);
	        }
	    }).start();
	}
	
	/**
	 * Repeatedly calls a routine with a fixed time delay between each call.
	 * 
	 * @param		id Routine identifier (necessary to be able to stop it)
	 * @param		routine Routine to be performed
	 * @param		interval Interval that the routine will be invoked (in 
	 * milliseconds)
	 * 
	 * @return		False if an interval has has already been set with the 
	 * given id or true otherwise
	 */
	public static boolean setInterval(int id, Runnable routine, int interval)
	{
		if (intervalRoutines.containsKey(id))
			return false;
		
		Timer t = new Timer();
		
		
		intervalRoutines.put(id, t);
		t.scheduleAtFixedRate(new TimerTask(){
		    @Override
		    public void run()
		    {
		       routine.run();
		    }
		}, 0, interval);
		
		return true;
	}
	
	/**
	 * Cancels a timed, repeating action, which was previously established by a
	 * call to {@link #setInterval(int, Runnable, int)}.
	 * 
	 * @param		id Routine id
	 */
	public static void clearInterval(int id)
	{
		if (intervalRoutines.containsKey(id)) {
			intervalRoutines.get(id).cancel();
			intervalRoutines.remove(id);
		}
	}
}
