package pmcep.logger;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class handles the logging methods.
 * 
 * @author Andrea Burattin
 */
public class Logger {

	/**
	 * This field can be used to turn on and off the logging mechanism.
	 */
	public static final boolean LOGGING_ENABLED = true;
	/**
	 * This field can be used to turn on and off the logging mechanism at the
	 * debug level.
	 */
	public static final boolean DEBUG_ENABLED = true;
	/**
	 * This field is used to indicate if exceptions should be logged.
	 */
	public static final boolean EXCEPTION_ENABLED = true;
	/**
	 * Signature for info message.
	 */
	public static final String INFO_SIGNATURE = " - INFO - ";
	/**
	 * Signature for debug message.
	 */
	public static final String DEBUG_SIGNATURE = " - DEBUG - ";
	/**
	 * Signature for error message.
	 */
	public static final String ERROR_SIGNATURE = " - ERROR - ";
	/**
	 * This field contains the date format for the logging text.
	 */
	public static final String DATE_FORMAT_NOW = "HH:mm:ss.SSS";
	/**
	 * The log will be written into this print stream
	 */
	public static PrintStream LOG_PRINT_STREAM = System.out;
	/**
	 * The error log will be written into this print stream
	 */
	public static PrintStream LOG_ERROR_STREAM = System.err;
	
	/**
	 * The actual logging object.
	 */
	private static Logger logger = new Logger();
	
	/**
	 * Protected constructor. To use the logger use {@link Logger#instance()}.
	 * This constructor is used just to not allow new instances of the class.
	 */
	protected Logger() { }
	
	/**
	 * This method returns a new instance of the logger
	 * 
	 * @return a logger instance
	 */
	public static Logger instance() {
		return logger;
	}
	
	/**
	 * This method is used to record the log of a new message
	 * 
	 * @param message the log message
	 */
	public void info(String message) {
		if (LOGGING_ENABLED) {
			print(now() + " " + getCaller() + DEBUG_SIGNATURE + message);
		}
	}
	
	/**
	 * This method is used to record debug messages
	 * 
	 * @param message the debug message
	 */
	public void debug(String message) {
		if (LOGGING_ENABLED && DEBUG_ENABLED) {
			print(now() + " " + getCaller() + DEBUG_SIGNATURE + message);
		}
	}
	
	/**
	 * This method is used to record exceptions
	 * 
	 * @param exception the exception to record
	 */
	public void error(Exception exception) {
		if (LOGGING_ENABLED && EXCEPTION_ENABLED) {
			printError(now() + ERROR_SIGNATURE + exception.getClass().getCanonicalName() + ": " + exception.getMessage());
			for (StackTraceElement ste : exception.getStackTrace()) {
				String fileName = "";
				if (ste.getFileName() != null) {
					fileName = " @ " + ste.getFileName() + ":" + ste.getLineNumber();
				}
				printError(now() + ERROR_SIGNATURE + "   at " + ste.getClassName() + "." + ste.getMethodName() + fileName);
			}
		}
	}
	
	/**
	 * This method is in charge of actually printing the message and archiving
	 * it in a serialized file (using {@link LogSerializer#serialize(String)})
	 * 
	 * @param message the message to print
	 */
	private void print(String message) {
		synchronized (logger) {
			LOG_PRINT_STREAM.println(message);
			LOG_PRINT_STREAM.flush();
			
			LogSerializer.serialize(message + "\n");
		}
	}
	
	/**
	 * This method is in charge of actually printing the message and archiving
	 * it in a serialized file (using {@link LogSerializer#serialize(String)})
	 * 
	 * @param message the message to print
	 */
	private void printError(String message) {
		synchronized (logger) {
			LOG_ERROR_STREAM.println(message);
			LOG_ERROR_STREAM.flush();
			
			LogSerializer.serialize(message + "\n");
		}
	}
	
	/**
	 * This method returns the caller signature (i.e., which class and where
	 * called the logger)
	 * 
	 * @return a string representation of the caller
	 */
	private String getCaller() {
		StackTraceElement callerElement = new Throwable().getStackTrace()[2];
		String caller = "(" + callerElement.getFileName() + ":" + callerElement.getLineNumber() + ")";
		return caller;
	}
	
	/**
	 * This method returns the current time.
	 * 
	 * @return a string representation of the current time.
	 * @see #DATE_FORMAT_NOW
	 */
	private String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}
}
