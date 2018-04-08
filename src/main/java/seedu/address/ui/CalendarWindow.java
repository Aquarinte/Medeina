package seedu.address.ui;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.calendarfx.model.Calendar;
//import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.Interval;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DayView;

import com.calendarfx.view.DayViewBase;
import com.calendarfx.view.WeekView;
import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.collections.ObservableList;
//import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;

import seedu.address.commons.events.model.AddressBookChangedEvent;
import seedu.address.commons.events.ui.ChangeCalendarViewEvent;
import seedu.address.model.appointment.Appointment;


//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.scene.Scene;
//import javafx.stage.Stage;

//import java.time.Duration;

//import java.time.LocalDateTime;

//import java.time.ZoneId;


//import seedu.address.MainApp;
//import seedu.address.commons.core.LogsCenter;
//import seedu.address.commons.events.ui.PersonPanelSelectionChangedEvent;



//@@author Robert-Peng
/**
 * Implement CalendarView from CalendarFX to show appointments
 */
public class CalendarWindow extends UiPart<Region> {

    public static final String DEFAULT_PAGE = "CalendarPanel.fxml";

    private  ObservableList<Appointment> appointmentList;
    private Calendar calendar;

    @FXML
    private CalendarView calendarView;
    private DayView dayView;
    private WeekView weekView;

    /**
     *
     * @param OwnerList
     */
    public CalendarWindow(ObservableList<Appointment> appointmentList) {
        super(DEFAULT_PAGE);

        this.appointmentList = appointmentList;
        calendarView = new CalendarView();


        setView();
        setTime();
        setCalendar();
        disableViews();
        registerAsAnEventHandler(this);

    }

    private void setView() {
        this.dayView = calendarView.getDayPage().getDetailedDayView().getDayView();
        dayView.setHoursLayoutStrategy(DayViewBase.HoursLayoutStrategy.FIXED_HOUR_HEIGHT);
        dayView.setHourHeight(150);

        this.weekView = calendarView.getWeekPage().getDetailedWeekView().getWeekView();
        weekView.setHoursLayoutStrategy(DayViewBase.HoursLayoutStrategy.FIXED_HOUR_HEIGHT);
        weekView.setHourHeight(150);
    }

    private void setTime() {
        calendarView.setRequestedTime(LocalTime.now());
        calendarView.setToday(LocalDate.now());
        calendarView.setTime(LocalTime.now());
    }

    /**
     * Creates a new a calendar
     */
    private void setCalendar() {
        setTime();
        calendarView.getCalendarSources().clear();
        CalendarSource calendarSource = new CalendarSource("Appointments");
        int styleNumber = 0;
        int appointmentCounter = 0;

        for (Appointment appointment : appointmentList) {

            Calendar calendar = createCalendar(styleNumber, appointment);
            calendar.setReadOnly(true);
            calendarSource.getCalendars().add(calendar);

            LocalDateTime ldt = appointment.getDateTime();
            appointmentCounter++;

            Entry entry = new Entry (buildAppointment(appointment, appointmentCounter).toString());

            entry.setInterval(new Interval(ldt, ldt.plusMinutes(60)));

            styleNumber++;
            styleNumber = styleNumber % 7;

            calendar.addEntry(entry);

        }
        calendarView.getCalendarSources().add(calendarSource);
    }

    /**
     *
     * @param appointment
     * @param appointmentCounter
     * @return
     */
    private StringBuilder buildAppointment (Appointment appointment, int appointmentCounter) {
        final StringBuilder builder = new StringBuilder();
        builder.append(appointmentCounter)
            .append(". ")
            .append(appointment.getPetPatientName().toString() + "\n")
            .append("Owner Nric: " + appointment.getOwnerNric() + "\n")
            .append("Appointment type: " + appointment.getTagString());

        builder.append("\n");
        builder.append("Remarks: " + appointment.getRemark().toString());
        return builder;
    }

    /**
     *
     * @param styleNumber
     * @param appointment
     * @return a calendar with given info and corresponding style
     */
    private Calendar createCalendar(int styleNumber, Appointment appointment) {
        Calendar calendar = new Calendar(appointment.getPetPatientName().toString());
        calendar.setStyle(Calendar.Style.getStyle(styleNumber));
        calendar.setLookAheadDuration(Duration.ofDays(365));
        calendar.setLookBackDuration(Duration.ofDays(365));
        return calendar;
    }

    /**
     * close unwanted UI components
     */

    private void disableViews() {
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowSearchField(false);
        calendarView.setShowSearchResultsTray(false);
        calendarView.setShowPrintButton(false);
        calendarView.setShowSourceTrayButton(false);
        calendarView.showDayPage();
        calendarView.setShowSourceTray(false);
        calendarView.setShowPageToolBarControls(false);
    }

    /**
     *To switch between CalendarView displays
     * @param character
     */
    private void switchViews(Character character) {
        switch (character) {
        case('d'):
            calendarView.showDayPage();
            return;
        case('w'):
            calendarView.showWeekPage();
            return;
        case('m'):
            calendarView.showMonthPage();
            return;
        case('y'):
            calendarView.showYearPage();
            return;
        default:
            throw new AssertionError("Wrong showPage input");
        }

    }

    @Subscribe
    private void handleCalendarViewEvent(ChangeCalendarViewEvent event) {
        Character character = event.character;
        Platform.runLater(() -> switchViews(character));
    }

    public CalendarView getRoot() {
        return this.calendarView;
    }

    @Subscribe
    private void handleNewAppointmentEvent(AddressBookChangedEvent event) {
        appointmentList = event.data.getAppointmentList();
        Platform.runLater(
            this::setCalendar
        );

    }

}


