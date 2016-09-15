package microsofia.boot;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import microsofia.boot.config.Settings;
import microsofia.boot.launch.GraphDumper;
import microsofia.boot.launch.MainLauncher;

/**
 * Main that uses Aether to update the local repository given an Artifact dependency and launchs a main.
 * It provides the following help
 * 
 * <pre>
 *  usage: boot </br>
-c,--class <classname>   Class that contains the main to run</br>
-d,--dump                Dumps the dependency graph to the console
-h,--help                Prints this help</br>
-s,--settings <arg>      Settings file path. Default is ./settings.xml</br>
-t,--template            Dumps a dummy example of settings.xml</br>
 * </pre>
 * */
public class Main {
	public static void main(String[] args) throws Throwable{
		Options options=new Options();
		
		Option o1=Option.builder("c").argName("classname")
								     .longOpt("class")
							         .desc("Class that contains the main to run")
							         .hasArg()
							         .build();
		
		options.addOption(o1);
		options.addOption("s","settings", true, "Settings file path. Default is ./settings.xml");
		options.addOption("d","dump", false, "Dumps the dependency graph to the console");
		options.addOption("t","template", false, "Dumps a dummy example of settings.xml");
		options.addOption("h","help", false, "Prints this help");
		
		CommandLineParser parser=new DefaultParser();
		CommandLine cmd=parser.parse(options, args);
		
		if (cmd.hasOption("help")){
			HelpFormatter helpFormatter=new HelpFormatter();
			helpFormatter.printHelp("boot",options);
			return;
		}
		
		if (cmd.hasOption("template")){
			Settings settings=SettingsTemplateBuilder.createSettings();
			File file=new File("./dummy_settings.xml");
			settings.writeTo(new FileOutputStream(file));
			System.out.println("File "+file.getAbsolutePath()+" created.");
			return;
		}

		String settingsFile=cmd.getOptionValue("settings","./settings.xml");
		
		if (cmd.hasOption("dump")){
			GraphDumper dumper=new GraphDumper();
			dumper.setSettingsFile(new File(settingsFile));
			dumper.dump();

		}else{
			String mainClass=cmd.getOptionValue("class");
			if (mainClass==null){
				throw new IllegalArgumentException("Missing required option: class");
			}
	
						
			MainLauncher launcher=new MainLauncher();
			launcher.setMainClass(mainClass);
			if (settingsFile!=null){
				launcher.setSettingsFile(new File(settingsFile));
			}
			launcher.launch();
		}
	}
}
