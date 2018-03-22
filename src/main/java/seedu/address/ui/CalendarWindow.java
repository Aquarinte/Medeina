package seedu.address.ui;

import java.time.LocalDate;
import java.time.LocalTime;

import com.calendarfx.model.Calendar;
//import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.CalendarSource;
//import com.calendarfx.model.Entry;
//import com.calendarfx.model.Interval;
import com.calendarfx.view.CalendarView;

import javafx.application.Platform;
import javafx.collections.ObservableList;
//import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.scene.Scene;
//import javafx.stage.Stage;

//import java.time.Duration;

//import java.time.LocalDateTime;

//import java.time.ZoneId;
//import java.util.List;

//import seedu.address.MainApp;
//import seedu.address.commons.core.LogsCenter;
//import seedu.address.commons.events.ui.PersonPanelSelectionChangedEvent;




/**
 *
 */
public class CalendarWindow extends UiPart<Region> {

    public static final String DEFAULT_PAGE = "CalendarPanel.fxml";

    private ObservableList<Person> ownerList;
    private Calendar calendar;

    @FXML
    private CalendarView calendarView;

    /**
     *
     * @param OwnerList
     */
    public CalendarWindow(ObservableList<Person> ownerList) {
        super(DEFAULT_PAGE);
        this.ownerList = ownerList;
        calendarView = new CalendarView();

        CalendarSource newCalendarSource = new CalendarSource("My Calendars");

        calendarView.getCalendarSources().add(newCalendarSource);

        calendarView.setRequestedTime(LocalTime.now());

        calendar = new Calendar("Appointments");


        CalendarSource mycalendarSource = new CalendarSource("My Appointments");
        mycalendarSource.getCalendars().addAll(calendar);

        calendarView.getCalendarSources().add(mycalendarSource);

        Thread updateTimeThread = new Thread("Calendar: Update Time Thread") {
            @Override
            public void run() {
                while (true) {
                    Platform.runLater(() -> {
                        calendarView.setToday(LocalDate.now());
                        calendarView.setTime(LocalTime.now());
                    });

                    try {
                        // update every 10 seconds
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            };
        };

        updateTimeThread.setPriority(Thread.MIN_PRIORITY);
        updateTimeThread.setDaemon(true);
        updateTimeThread.start();

    }

    public CalendarView getRoot() {
        return this.calendarView;
    }

}


