package com.tvaapiexamples; /**
 * Copyright 2003 BBC Research & Development
 *
 * This file is part of the BBC R&D TV-Anytime Java API.
 *
 * The BBC R&D TV-Anytime Java API is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * The BBC R&D TV-Anytime Java API is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the BBC R&D TV-Anytime Java API; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307  USA
 */

import bbc.rd.tvanytime.*;
import bbc.rd.tvanytime.xml.*;
import bbc.rd.tvanytime.programInformation.*;
import bbc.rd.tvanytime.programLocation.*;
import bbc.rd.tvanytime.contentReferencing.*;
import bbc.rd.tvanytime.search.*;

import java.io.*;
import java.util.*;

/**
 * Code to demonstrate use of TVAnytime API.
 * 
 * @author Tristan Ferne, BBC Research &	Development, June 2003
 * @version	1.0
 */
public class APICodeExamples  {

  /**
   * Demonstrates parsing of XML program information table.
   * 
   * @param  filename  Filename of XML file containing program information table.
   */
  public void parseXML(String filename) {
    try {
      // Create parser
      SAXXMLParser parser = new SAXXMLParser();
      // Configure the parser to parse the standard profile (ie. everything).
      ((SAXXMLParser)parser).setParseProfile(SAXXMLParser.STANDARD);

      try {
        // Do the parsing...
        parser.parse(new File(filename));
      } catch (NonFatalXMLException nfxe) {
        // Handle non-fatal XML exceptions
        // Contain any invalid TVAnytime data values from XML source. 
        // These are all collated by the parser and thrown at the end to avoid
        // having to abort the parsing.
        nfxe.printStackTrace();
      }
      // Print out the contents of the parsed ProgramInformationTable...
      System.out.println(parser.getProgramInformationTable());

    }
    catch (TVAnytimeException tvae) {
      // Handle any other TVAnytime-specific exceptions that may be generated.
      // E.g. if the XML parser cannot be initialised.
      tvae.printStackTrace();      
    }
    catch (IOException ioe) {
      // Handle IOExceptions: things like missing file
      ioe.printStackTrace();
    }

  }

  /**
   * Creates a list of events sorted by time for a particular channel.
   * 
   * @param  programLocationTable  ProgramLocationTable to create EPG from.
   */
  public void createEPG(ProgramLocationTable programLocationTable, ProgramInformationTable programInformationTable) {
    Vector vector;
    ProgramInformation programInformation;

    // List of events for a particular channel
    List events = new ArrayList();
    
    // Search through all schedules in program location table
    for (int schedulect=0; schedulect<programLocationTable.getNumSchedules(); schedulect++) {
      Schedule schedule = programLocationTable.getSchedule(schedulect);
      if (schedule.getServiceID().equals("BBCOne")) {
        // Found schedule for a particular service, e.g. BBC 1
        for (int eventct=0; eventct<schedule.getNumScheduleEvents(); eventct++) {
          // Add each event to the list
          events.add(schedule.getScheduleEvent(eventct));
        }
      }
    }
    
    // Sort list of events
    Collections.sort(events, new Comparator() {
      // Returns a negative integer, zero, or a positive integer as the first 
      // argument is less than, equal to, or greater than the second.
      public int compare(Object o1, Object o2) {
      	return (int)( ((ScheduleEvent)o1).getPublishedStartTime().getTime() - ((ScheduleEvent)o2).getPublishedStartTime().getTime() );
      }
    });    

    // Print out the sorted list
    for (int ct=0; ct<events.size(); ct++) {
      System.out.println(events.get(ct));
      // Print out programme title
      try {
        vector = programInformationTable.getProgramInformation(((ScheduleEvent)events.get(ct)).getCRID());
        for (int ct2=0; ct2<vector.size(); ct2++) {
          // For each program found for this CRID
          programInformation = (ProgramInformation)vector.elementAt(ct2);
          System.out.println(programInformation.getBasicDescription().getTitle(0));
        }
      } catch (SearchInterfaceNotSupportedException sinse) {
        sinse.printStackTrace();
      }
    }
    
  }

  /**
   * Searches for the program information data and resolves a given CRID.
   * 
   * @param  programInformationTable  ProgramInformationTable to search.
   * @param  contentReferencingTable  ContentReferencingTable to search.
   */
   public void searchForCRID(ProgramInformationTable programInformationTable, ContentReferencingTable contentReferencingTable) {
    Vector vector;
    ProgramInformation programInformation;
    ProgramURL programURL;

    try {   
      // Create CRID to search for
      ContentReference crid = new ContentReference("crid://bbc.co.uk/276788823");

      // First get it from the current program information table
      // Note that this is the getProgramInformation() method from the 
      // MetadataSearch interface.
      System.out.println("This is the programme from the PI table...");
      vector = programInformationTable.getProgramInformation(crid);
      for (int ct=0; ct<vector.size(); ct++) {
        // For each program found for this CRID
        programInformation = (ProgramInformation)vector.elementAt(ct);
        System.out.println(programInformation);
      }
      
      // Then get it from the content referencing table
      System.out.println("Searching for CRID in CR table...");
      Result result = contentReferencingTable.resolveCRID(crid);

      // Print out the resulting locations...
      if (result != null) {
        if (result.getLocationsResult() != null) {
          for (int ct=0; ct<result.getLocationsResult().getNumLocators(); ct++) {
            System.out.println(result.getLocationsResult().getLocator(ct));
          }
        }
      }      
    }        
    catch (SearchInterfaceNotSupportedException snse) { 
      // Thrown if cannot do specified search on table
      snse.printStackTrace();
    }
    catch (TVAnytimeException tvae) {
      // Will be thrown if CRID with invalid syntax is created.
      tvae.printStackTrace();
    }
  }


  /**
   * Driver method.
   */
  public void runAPICodeExamples() {
    APICodeExamples example = new APICodeExamples();

    // Parse XML
    example.parseXML("C:/tvademo/src/main/xml/20021203BBCOne_pi.xml");

    // Parse XML and call example functions on tables
    try {
      // Create parser
      SAXXMLParser parser = new SAXXMLParser();
      ((SAXXMLParser)parser).setParseProfile(SAXXMLParser.STANDARD);
      // Parse PI, PL and CR
      try {
        parser.parse(new File("C:/tvademo/src/main/xml/20021203BBCOne_pi.xml"));
      } catch (NonFatalXMLException nfxe) {
        nfxe.printStackTrace();
      }
      try {
        parser.parse(new File("C:/tvademo/src/main/xml/20021203BBCOne_pl.xml"));
      } catch (NonFatalXMLException nfxe) {
        nfxe.printStackTrace();
      }
      try {
        parser.parse(new File("C:/tvademo/src/main/xml/20021203BBCOne_cr.xml"));
      } catch (NonFatalXMLException nfxe) {
        nfxe.printStackTrace();
      }

      // Create EPG
      example.createEPG(parser.getProgramLocationTable(), parser.getProgramInformationTable());
      // Search for stuff
      example.searchForCRID(parser.getProgramInformationTable(), parser.getContentReferencingTable());
    }
    catch (TVAnytimeException tvae) {
      tvae.printStackTrace();      
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }

 

  }
}