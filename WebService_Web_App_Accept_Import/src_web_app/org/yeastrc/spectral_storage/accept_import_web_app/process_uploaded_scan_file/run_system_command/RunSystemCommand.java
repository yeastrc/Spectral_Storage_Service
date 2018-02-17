package org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.run_system_command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileWebappConfigException;



/**
 * 
 *
 */
public class RunSystemCommand {


	private static Logger log = Logger.getLogger(RunSystemCommand.class);

	//  private constructor
	private RunSystemCommand() { }
	
	/**
	 * @return newly created instance
	 */
	public static RunSystemCommand getInstance() { 
		return new RunSystemCommand(); 
	}
	
	//  wait time before check for program done
//	private final int WAIT_TIME_TO_FOR_STREAM_COPY_THREADS_TO_COMPLETE = 10;  //  in seconds
	
	
	
	private volatile boolean shutdownRequested = false;
	
	
	private volatile Process process = null;
	
	private volatile OutputStream outputStreamToProcessStdIn = null;

	private volatile boolean inWaitFor = false;
	

	/**
	 * awaken thread to get next import or to complete
	 */
	public void awaken() {

		synchronized (this) {

//			exittedWaitDueToAwakenNotify = true;

			notify();
		}

	}
	

	/**
	 * @param commandAndItsArgumentsAsList
	 * @param dirToRunCommandIn
	 * @param fileToWriteSysoutTo
	 * @param fileToWriteSyserrTo
	 * @param throwExceptionOnCommandFailure
	 * @return
	 * @throws Throwable
	 */
	public RunSystemCommandResponse runCmd( 
			
			List<String> commandAndItsArgumentsAsList,
			
			File dirToRunCommandIn, 
			File fileToWriteSysoutTo,
			File fileToWriteSyserrTo,
			boolean throwExceptionOnCommandFailure )
	throws Throwable
	{
		
		if ( shutdownRequested ) {
			
			String msg = "Object not usable once shutdownRequested is true";
			log.error( msg );
			throw new IllegalStateException(msg);
		}
		

		String[] commandAndItsArguments = commandAndItsArgumentsAsList.toArray( new String[ commandAndItsArgumentsAsList.size() ] );
		
		
		if ( log.isInfoEnabled() ) {

			log.info( "runCmd: running shell command:  " + commandAndItsArgumentsAsList );
		}
		
		if ( fileToWriteSysoutTo.exists() ) {

			if ( ! fileToWriteSysoutTo.delete() ) {

				String msg = "Unable to delete previous file to fileToWriteSysoutTo: " + fileToWriteSysoutTo.getCanonicalPath();
				log.error( msg );
				throw new SpectralFileWebappConfigException(msg);
			}
		}

		if ( fileToWriteSyserrTo.exists() ) {

			if ( ! fileToWriteSyserrTo.delete() ) {

				String msg = "Unable to delete previous file to fileToWriteSyserrTo: " + fileToWriteSyserrTo.getCanonicalPath();
				log.error( msg );
				throw new SpectralFileWebappConfigException(msg);
			}
		}

		
		
		
		if ( ! fileToWriteSysoutTo.createNewFile() ) {
			
			String msg = "Unable to write to fileToWriteSysoutTo: " + fileToWriteSysoutTo.getCanonicalPath();
			log.error( msg );
			throw new SpectralFileWebappConfigException(msg);
		}

		if ( ! fileToWriteSyserrTo.createNewFile() ) {
			
			String msg = "Unable to write to fileToWriteSyserrTo: " + fileToWriteSyserrTo.getCanonicalPath();
			log.error( msg );
			throw new SpectralFileWebappConfigException(msg);
		}
		
		RunSystemCommandResponse runSystemCommandResponse = new RunSystemCommandResponse();

		runSystemCommandResponse.setCommandSuccessful( false );

		outputStreamToProcessStdIn = null;

		try {

			ProcessBuilder processBuilder = new ProcessBuilder( commandAndItsArguments );
			
			processBuilder.directory( dirToRunCommandIn );
			processBuilder.redirectOutput( Redirect.to( fileToWriteSysoutTo ) );
			processBuilder.redirectError( Redirect.to( fileToWriteSyserrTo ) );
			

			process = processBuilder.start();
			
			outputStreamToProcessStdIn = process.getOutputStream();
			
			
			try {

				inWaitFor = true;

				process.waitFor();
				
			} finally {
			
				inWaitFor = false;
			}
			

			if ( shutdownRequested ) {
				
				//  Shutdown Requested so early exit
				
				String msg = "Shutdown requested so early exit with runSystemCommandResponse.setShutdownRequested( true );";
				
				log.warn( msg );
				
				runSystemCommandResponse.setShutdownRequested( true );
				
				return runSystemCommandResponse;  //  EARLY EXIT
			}
			
			
			int exitValue = process.exitValue();

			runSystemCommandResponse.setCommandExitCode( exitValue );
			


			if ( exitValue != 0 ) {

				List<String> commandAndItsArgumentsList = Arrays.asList( commandAndItsArguments );
				
				String message = "runCmd: execute command returned non-zero.  exitValue = " + exitValue 
						+ ", commandAndItsArgumentsList = " + commandAndItsArgumentsList;

				log.error(message);

				if ( throwExceptionOnCommandFailure ) {

					throw new Exception( message );

				}

				runSystemCommandResponse.setCommandSuccessful( false );

			} else {

				runSystemCommandResponse.setCommandSuccessful( true );
			}
			

			if ( shutdownRequested ) {
				
				
				//  Shutdown Requested so early exit
				
				runSystemCommandResponse.setShutdownRequested( true );
			}
			
			

		} catch ( Throwable t ) {

			String msg = "Exception running command |" + commandAndItsArguments + "|.  Exception: " + t.toString();
			log.error( msg, t );
			
			//  Write msg to file in dirToRunCommandIn
			
			File errorMsgFile = new File( dirToRunCommandIn, "A_Exception_Running_Command.txt" );
			
			try ( BufferedWriter writer = new BufferedWriter( new FileWriterWithEncoding( errorMsgFile, StandardCharsets.UTF_8 ) ) ) {
				
				writer.write( msg );
				writer.newLine();
				
				PrintWriter printWriter = new PrintWriter(writer);
				
				t.printStackTrace( printWriter );
				

			} catch ( Throwable t_writer ) {
				log.error( "Failed to write exception to file", t_writer );
				
			}

			throw t;

		} finally {
			
			
//			process.getInputStream() and process.getErrorStream() are closed in the StreamCopyToFileThread objects

			if ( outputStreamToProcessStdIn != null ) {
				try {
					outputStreamToProcessStdIn.close();
				} catch ( Throwable t ) {
					log.info( "Exception closing stdin to process: " + t.toString(), t );
				}
				outputStreamToProcessStdIn = null;
			}

		}
		


		return runSystemCommandResponse;

	}
	
	
	/**
	 * Called on a separate thread when a shutdown request comes from the operating system.
	 * If this is not heeded, the process may be killed by the operating system after some time has passed ( controlled by the operating system )
	 */
	public void shutdown() {
		
		shutdownRequested = true;
		
		

//		try {
//
//			if ( outputStreamToProcessStdIn != null ) {
//
//				outputStreamToProcessStdIn.write( StringSendImporterToRequestShutdownConstants.SHUTDOWN );
//				
//				outputStreamToProcessStdIn.flush();
//				
//				outputStreamToProcessStdIn.close();
//				
//				outputStreamToProcessStdIn = null;
//
//				//  TODO  maybe do something here
//			}
//
//		} catch ( NullPointerException e ) {
//			
//			//  Eat the NullPointerException since that meant that nothing had to be done.
//			
//		} catch ( Throwable t ) {
//			
//			String msg = "Exception thrown sending shutdown text to child import process";
//			log.error( msg );
//			
//		}
		
		
		if ( inWaitFor ) {
			
			//  TODO  May want to issue an interrupt to exit the waitFor() immediately
		}
		
		try {

			if ( process != null ) {
				
				log.info( "killing child process scan file process" );

				process.destroy();
			}

		} catch ( NullPointerException e ) {
			
			//  Eat the NullPointerException since that meant that nothing had to be done.
			
		} catch ( Throwable t ) {
			
			String msg = "Exception thrown killing child process scan file process";
			log.error( msg );
			
		}
		
		this.awaken();
	}
	
}
