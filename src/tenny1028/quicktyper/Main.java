/*
 * Copyright (c) 2014. Jasper Reddin.
 * All Rights Reserved.
 */

package tenny1028.quicktyper;

import tenny1028.quicktyper.exceptions.FileAlreadyExistsException;
import tenny1028.quicktyper.gui.CommandTyperFrame;
import tenny1028.quicktyper.gui.HelpSection;
import tenny1028.quicktyper.gui.ProfileChooser;

import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
	public static CommandTyperFrame typer;

	public static File libraryFolderDirectory; //~/Library/Application Support/wordsperminute/ on Mac, ~\Application Data\wordsperminute\ on Windows
	public static File profilesDirectory;      //<libraryFolderDirectory>/profiles/
	public static File currentProfile;
	public static File previouslyOpenedProfile;
	public static File lastLoginFile;          //<libraryFolderDirectory>/lastlogin.txt

	public static String libraryFolder;

	public static final float VERSION_ID = 0.1f;
	public static final String newline = System.getProperty("line.separator");

    public static void main(String[] args) {

	    // Check for update
	    if(checkForUpdate()){
		    int userWantsToUpdate = JOptionPane.showConfirmDialog(null,
				    "An update is available. Do you want to update?",
				    "Update available!",
				    JOptionPane.YES_NO_OPTION);
		    if(userWantsToUpdate == 0){
			    // Open browser to GitHub repo
			    String url = "https://github.com/tenny1028/WordsPerMinute/releases";
			    try {
				    java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
			    } catch(IOException e) {
				    e.printStackTrace();
			    }
			    System.exit(0);
		    }
	    }

	    boolean hasBeenOpened = createNeededFiles();

	    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

		    public void run() {
			    // Do what you want when the application is stopping
		    }
	    }));

	    if(!hasBeenOpened){
		    new HelpSection();
		    createProfileGUI();

	    }else{
		    new ProfileChooser();
	    }


    }
	public static void println(String str){
		//typer.println(str);
	}
	/**
	 * Checks to see if dependent files exist and if they don't, it means that this program is running for the first
	 * time.
	 */
	public static boolean createNeededFiles(){
		String userHome = System.getProperty("user.home");
		String os = OSValidator.findOS();
		String profilesDirectoryFolder;
			if(os.equals("mac")) {
				libraryFolder = userHome + "/Library/Application Support/wordsperminute/";
				profilesDirectoryFolder = libraryFolder + "profiles/";
			}
			else if(os.equals("windows")) {
				libraryFolder = userHome + "\\Application Data\\wordsperminute\\";
				profilesDirectoryFolder = libraryFolder + "profiles\\";
			}
			else {
				libraryFolder = userHome + "/.wordsperminute/";
				profilesDirectoryFolder = libraryFolder + "profiles/";
			}

		libraryFolderDirectory = new File(libraryFolder);
		profilesDirectory = new File(profilesDirectoryFolder);

		if(!profilesDirectory.exists()){
			try{
				profilesDirectory.mkdirs();
			}catch(SecurityException e){
			}
		}
		File[] list = profilesDirectory.listFiles();
		boolean hasBeenOpened=false;
		for(File file:list){
			if(!file.getName().endsWith(".wpmprofile")){
				file.delete(); // We do not need anything else in this directory except for profile files.
			}else if(file.getName().endsWith(".wpmprofile")&&!hasBeenOpened){
				hasBeenOpened = true;
			}
		}

		String lastLogin = libraryFolder + "lastlogin.txt";
		lastLoginFile = new File(lastLogin);
		if(!lastLoginFile.exists()){
			try{
				lastLoginFile.createNewFile();
			}catch(SecurityException | IOException e){
			}
		}
		return hasBeenOpened;
	}
	public static void createProfile(String profileName) throws FileAlreadyExistsException {
		String userProfileFilePath;
		if(!profileName.endsWith(".wpmprofile")) userProfileFilePath = profilesDirectory.getPath() + System.getProperty("file.separator")+profileName+".wpmprofile";
		else userProfileFilePath = profilesDirectory.getPath() + System.getProperty("file.separator")+profileName;
		File userProfile = new File(userProfileFilePath);
		if(!userProfile.exists()){
			try{
				userProfile.createNewFile();
			}catch(SecurityException | IOException e){
			}
			// Write default numbers for statistics:
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(userProfile));
				writer.write("0.0"+newline+"0.0"+newline+"0.0");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					writer.close();
				} catch (Exception e) {
				}
			}
		}else{
			throw new FileAlreadyExistsException(userProfileFilePath + " Already exists!", userProfile.getName());
		}

	}
	public static void openProfile(String filepath){
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(lastLoginFile.getAbsolutePath()));
			writer.write(filepath);
			writer.flush();
			previouslyOpenedProfile = new File(filepath);
			writer.close();
		} catch(IOException e) {
		}

	}
	public static String getLastOpened(){
		try {
			Scanner scanner = new Scanner(lastLoginFile);
			return scanner.nextLine();
		} catch(FileNotFoundException e) {
			return null;
		}

	}
	public static File getProfile(String profile){
		String filename;
		if(profile.endsWith(".wpmprofile")) filename = profilesDirectory.getAbsolutePath() + System.getProperty("file.separator") + profile;
		else filename = profilesDirectory.getAbsolutePath() + System.getProperty("file.separator") + profile + ".wpmprofile";
		return new File(filename);
	}

	/**
	 * Checks to see if there is a new version available to download.
	 * @return True if there is a newer version available on Sourceforge or Github
	 */
	public static boolean checkForUpdate(){
		try {
			URL website = new URL("http://tenny1028.github.io/WordsPerMinute/version");
			Scanner websiteReader = new Scanner(website.openStream());
			String versionID = websiteReader.nextLine();
			return !versionID.equals("VERSION_ID = " + VERSION_ID);
		} catch(MalformedURLException e) {
			e.printStackTrace();
		} catch(UnknownHostException e){
			System.out.println("[NOTICE] No internet connection or update server is not available.");
		} catch(IOException e) {
			e.printStackTrace();
		}
		return false;

	}

	public static String createProfileGUI(){
		String s = JOptionPane.showInputDialog(null, "Type name of new profile: ", "Create new profile", JOptionPane.PLAIN_MESSAGE);
		try{
			createProfile(s);
			return s;
		}catch(FileAlreadyExistsException e){
			JOptionPane.showMessageDialog(null, e.getNameOfFile()+" already exists.");
			return createProfileGUI();

		}
	}
}
