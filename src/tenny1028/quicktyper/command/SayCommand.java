/*
 * Copyright (c) 2014. Jasper Reddin.
 * All Rights Reserved.
 */

package tenny1028.quicktyper.command;

import tenny1028.quicktyper.Main;
import tenny1028.quicktyper.exceptions.CommandNotFoundException;

/**
 * Created by jasper on 1/27/14.
 */
public class SayCommand extends Command {

	public void execute(String[] args)throws CommandNotFoundException {
		String printToLine = "";

		for(String str:args) {
			if (str != null) printToLine += str + " ";
		}
	}

	public String getName(){
		return "say";
	}
}
