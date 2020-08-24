package com.componente_practico.main;



import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.validate.ValidationException;

public class GenerarIcsMain {
	
	public static void main(String[] args) throws ValidationException, IOException, ParseException {
		/*
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
		calendar.set(java.util.Calendar.DAY_OF_MONTH, 25);

		// initialise as an all-day event..
		VEvent christmas = new VEvent(new Date(calendar.getTime()), "Christmas Day");

		// Generate a UID for the event..
		UidGenerator ug = new UidGenerator("1");
		christmas.getProperties().add(ug.generateUid());

		net.fortuna.ical4j.model.Calendar cal = new net.fortuna.ical4j.model.Calendar();
		cal.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
		cal.getProperties().add(Version.VERSION_2_0);
		cal.getProperties().add(CalScale.GREGORIAN);
		cal.getComponents().add(christmas);
		
		// Create a calendar
		//net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
		//icsCalendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
		//icsCalendar.getProperties().add(CalScale.GREGORIAN);

		
		CalendarOutputter outputter = new CalendarOutputter();
		OutputStream out = System.out;
		outputter.output(cal, out);
		*/
		
		// Create a TimeZone
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone timezone = registry.getTimeZone("America/Guayaquil");
		VTimeZone tz = timezone.getVTimeZone();

		 Date fechaInicio = new Date();
		 Calendar fechaFin = Calendar.getInstance(timezone);
		 fechaFin.setTime(fechaInicio);
		 fechaFin.add(Calendar.MINUTE, 2);

		 
		 System.out.println(fechaInicio);
		// Create the event
		String eventName = "Prueba Hola Lola";
		DateTime start = new DateTime(fechaInicio);
		DateTime end = new DateTime(fechaFin.getTime());
		VEvent meeting = new VEvent(start, end, eventName);

		// add timezone info..
		meeting.getProperties().add(tz.getTimeZoneId());

		// generate unique identifier..
		//UidGenerator ug = new UidGenerator("uidGen");
		//Uid uid = ug.generateUid();
		
		meeting.getProperties().add(new Uid(System.currentTimeMillis()+"@hello-lola.com"));

		/*
		// add attendees..
		Attendee dev1 = new Attendee(URI.create("mailto:dev1@mycompany.com"));
		dev1.getParameters().add(Role.REQ_PARTICIPANT);
		dev1.getParameters().add(new Cn("Developer 1"));
		meeting.getProperties().add(dev1);

		Attendee dev2 = new Attendee(URI.create("mailto:dev2@mycompany.com"));
		dev2.getParameters().add(Role.OPT_PARTICIPANT);
		dev2.getParameters().add(new Cn("Developer 2"));
		meeting.getProperties().add(dev2);
		*/

		// Create a calendar
		net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
		icsCalendar.getProperties().add(new ProdId("-//Hola Lola//EN"));
		icsCalendar.getProperties().add(Version.VERSION_2_0);
		icsCalendar.getProperties().add(CalScale.GREGORIAN);


		// Add the event and print
		icsCalendar.getComponents().add(meeting);
		//System.out.println(icsCalendar);
		
		FileOutputStream fout = new FileOutputStream("D://mycalendar.ics");

		CalendarOutputter outputter = new CalendarOutputter();
		outputter.output(icsCalendar, fout);
	}

}
