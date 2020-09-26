package javafx;

import Database.Event;
import Database.PatientClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static javafx.App.database;
import static javafx.App.stage;

public class PatientInfoController implements Initializable {
    int counter=0;
    Event lastevent;
    private Scene lastScene;
    private PatientClass patientClass;
    private ArrayList<Event> eventArrayList;
    private ObservableList<Event> eventObservableList;
    @FXML
    TextField id,firstname,lastname,gender,birthdate,phonenumber,city,address,country,postcode;
    @FXML
    TableView<Event> eventView;
    @FXML
    TableColumn<Event,String> dates,events;
    @FXML
    DatePicker startDate,lastDate;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public void initdata(PatientClass patientClass,Scene lastScene) {
        this.lastScene =lastScene;
        this.patientClass = patientClass;
        this.id.setText(patientClass.getId());
        this.firstname.setText(patientClass.getFirstname());
        this.lastname.setText(patientClass.getLastname());
        this.gender.setText(patientClass.getGender());
        this.birthdate.setText(patientClass.getBirthdate());
        this.phonenumber.setText(patientClass.getPhonenumber());
        this.city.setText(patientClass.getCity());
        this.address.setText(patientClass.getAddress());
        this.country.setText(patientClass.getCountry());
        this.postcode.setText(patientClass.getPostcode());
        eventArrayList=database.getAllEvents(patientClass);
        dates.setCellValueFactory(new PropertyValueFactory<>("dateString"));
        events.setCellValueFactory(new PropertyValueFactory<>("description"));
        fillEventView();
    }
    public void fillEventView()
    {
        ArrayList<Event> eventArrayList1 = filter();
        eventObservableList = FXCollections.observableArrayList(eventArrayList1);
        eventView.setItems(eventObservableList);
    }

    public ArrayList<Event> filter()
    {
        ArrayList<Event> helperList = new ArrayList<Event>();
        helperList=filterup(filterdown(eventArrayList));
        return helperList;
    }
    private ArrayList<Event> filterup(ArrayList<Event> help)
    {
        ArrayList<Event> helperList = new ArrayList<Event>();
        if(lastDate.getValue()!=null)
        {

            for(Event event:help)
            {
                if(event.getDate()!=null) {
                    LocalDate localDate = lastDate.getValue();
                    Instant instantlast = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
                    if (event.getDate().before(Date.from(instantlast))) {
                        helperList.add(event);
                    }
                }
            }
            return helperList;
        }
        return  help;
    }
    private ArrayList<Event> filterdown(ArrayList<Event> help)
    {
        ArrayList<Event> helperList = new ArrayList<Event>();
        if(startDate.getValue()!=null)
        {

            for(Event event:help)
            {
                if(event.getDate()!=null)
                {
                    LocalDate localDate = startDate.getValue();
                    Instant instantstart = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
                    if(event.getDate().after(Date.from(instantstart)))
                    {
                        helperList.add(event);
                    }

                }
            }
            return helperList;
        }
        return  help;
    }

    public void showmore()
    {

            if(eventView.getSelectionModel().getSelectedItem()!=null)
            {
                if(++counter>1)
                {
                    if(lastevent==eventView.getSelectionModel().getSelectedItem())
                    {
                        Event event = eventView.getSelectionModel().getSelectedItem();
                        try
                        {
                            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getClassLoader().getResource("more"+".fxml"));
                            Parent root = fxmlLoader.load();
                            MoreController moreController = fxmlLoader.getController();
                            moreController.initdata(event);
                            Stage stage2 = new Stage();
                            stage2.setScene(new Scene(root));
                            stage2.show();
                        }catch (IOException e){e.printStackTrace();}
                    }
                    counter=0;
                }
                lastevent=eventView.getSelectionModel().getSelectedItem();
            }
    }

    public void charts()
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getClassLoader().getResource("stats"+".fxml"));
            Parent root = fxmlLoader.load();
            StatsController statsController = fxmlLoader.getController();
            statsController.init(eventArrayList);
            Stage stage2 = new Stage();
            stage2.setScene(new Scene(root));
            stage2.show();
        }catch (IOException e){e.printStackTrace();}
    }


    public void back()
    {
        stage.setScene(lastScene);
    }


}
