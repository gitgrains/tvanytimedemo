package com.tvaapiexamples;

import bbc.rd.tvanytime.*;
import bbc.rd.tvanytime.programInformation.*;

/* Simple class to demonstrate creating a TV Anytime ProgramInformationTable using the BBC TVA API.
 * 
 * The program information table is populated by Title and Synopsis only.
 * Tim Sargeant, 12/12/02, (c) BBC Research & Development
 */
public class CreateProgInfoTable {

	/* declare the objects in our table */
	private ProgramInformationTable progInfoTable;
	private ProgramInformation progInfo;
	private ContentReference crid;
	private BasicDescription basicDesc;
	private Title title;
	private Synopsis synopsis;

	public CreateProgInfoTable() {
	}

	public void runCreateProgInfoTableExample() {
	
			/* initialise TVA objects to correct values */
		try {
			crid = new ContentReference("crid://bbc.co.uk/123456");
		} catch (TVAnytimeException tvae) {
			System.out.println(tvae);
		}
		title = new Title("The Life Of Mammals");
		synopsis = new Synopsis("Man Eaters: Join Sir David Attenborough as he sits beside wild lions in the darkness of the night, meets a Siberian tiger face to face and finds out what you need to be a true carnivore.");
		basicDesc = new BasicDescription();
		progInfoTable = new ProgramInformationTable();
		progInfo = new ProgramInformation();

		/* add objects to the table */
		basicDesc.addTitle(title);
		basicDesc.addSynopsis(synopsis);
		progInfo.setProgramID(crid);
		progInfo.setBasicDescription(basicDesc);
		progInfoTable.addProgramInformation(progInfo);

		/* output the table as TVA XML*/
		System.out.println(progInfoTable.toXML());
	}
}
