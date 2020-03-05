package org.yeastrc.spectral_storage.scan_file_processor.program;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.yeastrc.spectral_storage.scan_file_processor.main.ProcessUploadedScanFileRequest;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.s3_aws_interface.S3_AWS_InterfaceObjectHolder;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

/**
 * 
 *
 */
public class Scan_File_Processor_MainProgram {
	
	private static final int PROGRAM_EXIT_CODE_INVALID_INPUT = 1;
	private static final int PROGRAM_EXIT_CODE_HELP = 1;
	private static final String FOR_HELP_STRING = "For help, run without any parameters, -h, or --help";

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
	
		System.out.println( "INFO: Command Line args START");
		for ( int index = 0; index < args.length; index++) {
			System.out.println( "arg: " + args[ index ] );
		}
		System.out.println( "INFO: Command Line args END");
		
		//  If writing to S3, the '--output_base_dir' is the temp dir to write to before copy to S3
		
		CmdLineParser cmdLineParser = new CmdLineParser();
//		CmdLineParser.Option configFileFromCommandLineFileNameCommandLineOpt = cmdLineParser.addStringOption( 'c', "config" );
		CmdLineParser.Option outputBaseDirStringCommandLineOpt = cmdLineParser.addStringOption( 'Z', "output_base_dir" );
		CmdLineParser.Option backupOldBaseDirStringCommandLineOpt = cmdLineParser.addStringOption( 'Z', "backup_old_base_dir" );
		CmdLineParser.Option s3OutputBucketCommandLineOpt = cmdLineParser.addStringOption( 'Z', "s3_output_bucket" );
		CmdLineParser.Option s3OutputRegionCommandLineOpt = cmdLineParser.addStringOption( 'Z', "s3_output_region" );
		CmdLineParser.Option s3InputRegionCommandLineOpt = cmdLineParser.addStringOption( 'Z', "s3_input_region" );
		CmdLineParser.Option deleteScanFileOnSuccessfulProcessingCommandLineOpt = cmdLineParser.addBooleanOption('Z', "delete-scan-file-on-successful-processing"); 
		CmdLineParser.Option helpOpt = cmdLineParser.addBooleanOption('h', "help"); 

		String outputBaseDirString = null; 
		String backupOldBaseDirString = null;
		String s3_OutputBucket = null;
		String s3_OutputRegion = null;
		String s3_InputRegion = null;
		
		try {
			// parse command line options
			try { cmdLineParser.parse(args); }
			catch (IllegalOptionValueException e) {
				System.err.println(e.getMessage());
				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}
			catch (UnknownOptionException e) {
				System.err.println(e.getMessage());
				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}
			Boolean help = (Boolean) cmdLineParser.getOptionValue(helpOpt, Boolean.FALSE);
			if(help) {
//				printHelp();
				System.out.println( "Help is not implemented");
				System.exit( PROGRAM_EXIT_CODE_HELP );
			}
			//  Show an error if there is anything on the command line not associated with a parameter
			String[] remainingArgs = cmdLineParser.getRemainingArgs();
			if( remainingArgs.length > 0 ) {
				System.out.println( "Unexpected command line parameters:");
				for ( String remainingArg : remainingArgs ) {
					System.out.println( remainingArg );
				}
				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );  //  EARLY EXIT
			}
			
			outputBaseDirString = (String)cmdLineParser.getOptionValue( outputBaseDirStringCommandLineOpt );
			
			backupOldBaseDirString = (String)cmdLineParser.getOptionValue( backupOldBaseDirStringCommandLineOpt );
			
			s3_OutputBucket = (String)cmdLineParser.getOptionValue( s3OutputBucketCommandLineOpt );
			
			s3_OutputRegion = (String)cmdLineParser.getOptionValue( s3OutputRegionCommandLineOpt );
			
			s3_InputRegion = (String)cmdLineParser.getOptionValue( s3InputRegionCommandLineOpt );
	
			Boolean deleteScanFileOnSuccess = (Boolean) cmdLineParser.getOptionValue(deleteScanFileOnSuccessfulProcessingCommandLineOpt, Boolean.FALSE);

			if ( StringUtils.isEmpty( outputBaseDirString ) ) {
				System.err.println( "Output Base Dir command line parameter not specified (--output_base_dir)" );
				System.exit( 1 );  //  EARLY EXIT
			}
			
			System.out.println( "Output Base Dir (--output_base_dir): " + outputBaseDirString );
			
			if ( StringUtils.isNotEmpty( s3_OutputBucket ) ) {
				System.out.println( "S3 output Bucket (--s3_output_bucket): " + s3_OutputBucket );
			}
			if ( StringUtils.isNotEmpty( s3_OutputRegion ) ) {
				System.out.println( "S3 output region (--s3_output_region): " + s3_OutputRegion );
			}
			if ( StringUtils.isNotEmpty( s3_InputRegion ) ) {
				System.out.println( "S3 input region (--s3_input_region): " + s3_InputRegion );
			}

			if ( deleteScanFileOnSuccess ) {
				System.out.println( "Will be deleting uploaded scan file on successful import" );
			}
			
			Scan_File_Processor_MainProgram_Params pgmParams = new Scan_File_Processor_MainProgram_Params();
			
			if ( StringUtils.isNotEmpty( outputBaseDirString ) ) {
				
				File outputBaseDir = new File( outputBaseDirString );

				System.out.println( "Output Base Dir canonical: " + outputBaseDir.getCanonicalPath() );

				if ( ! outputBaseDir.exists() ) {
					System.err.println( "Output Base Dir command line Not Exist: " + outputBaseDirString );
					System.exit( 1 );
				}
				if ( ! outputBaseDir.canWrite() ) {
					System.err.println( "Check of canWrite of Output Base Dir on command line Failed: " + outputBaseDirString );
					System.exit( 1 );
				}
				
				pgmParams.setOutputBaseDir( outputBaseDir );
			}

			if ( StringUtils.isNotEmpty( backupOldBaseDirString ) ) {
				
				File backupOldBaseDir = new File( backupOldBaseDirString );

				System.out.println( "Backup Old Base Dir canonical: " + backupOldBaseDir.getCanonicalPath() );

				if ( ! backupOldBaseDir.exists() ) {
					System.err.println( "Backup Old Base Dir command line Not Exist: " + backupOldBaseDirString );
					System.exit( 1 );
				}
				if ( ! backupOldBaseDir.canWrite() ) {
					System.err.println( "Check of canWrite of Backup Old Base Dir on command line Failed: " + backupOldBaseDirString );
					System.exit( 1 );
				}
				
				pgmParams.setBackupOldBaseDir( backupOldBaseDir );
			} else {

				System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!");
				System.out.println( "No Value for command line parameter 'backup_old_base_dir' so data files will not be backed up before they are replaced with a newer version.");
				System.out.println( "See Accept Webapp configuration.");
				System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!");
			}
			
			
			if ( StringUtils.isNotEmpty( s3_OutputBucket ) ) {
				pgmParams.setS3_OutputBucket( s3_OutputBucket );
			}
			
			pgmParams.setDeleteScanFileOnSuccess( deleteScanFileOnSuccess );
			
			if ( StringUtils.isNotEmpty( s3_InputRegion ) || StringUtils.isNotEmpty( s3_OutputRegion ) ) {

				if ( StringUtils.isNotEmpty( s3_InputRegion ) ) {
					S3_AWS_InterfaceObjectHolder.getSingletonInstance().setS3_InputRegion( s3_InputRegion );
				}
				if ( StringUtils.isNotEmpty( s3_OutputRegion ) ) {
					S3_AWS_InterfaceObjectHolder.getSingletonInstance().setS3_OutputRegion( s3_OutputRegion );
				}

				S3_AWS_InterfaceObjectHolder.getSingletonInstance().init();
			}
			
			ProcessUploadedScanFileRequest.getInstance().processUploadedScanFileRequest( pgmParams );
			
		} finally {
			
		}
	}

}
