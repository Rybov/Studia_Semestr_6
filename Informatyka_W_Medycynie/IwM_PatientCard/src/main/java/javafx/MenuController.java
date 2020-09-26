package javafx;

import Database.Database;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import Database.PatientClass;
import static javafx.App.database;
import static javafx.App.stage;

public class MenuController implements Initializable {

    private List<PatientClass> patients;
    private ObservableList<PatientClass> patientTable;
    @FXML
    TableView<PatientClass> patiendview;
    @FXML
    TableColumn<PatientClass, String> column1;
    @FXML
    TableColumn<PatientClass, String> column2;
    @FXML
    TableColumn<PatientClass, String> column3;
    @FXML
    TextField filter;
    @FXML
    Button patientallinfo;
    @FXML
    Label label;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTableView();
        label.setVisible(false);
    }
    public void initTableView()
    {
        database.reloadPatients();
        patients=database.getPatients();
        for(PatientClass patientClass:patients)
            if(patientClass.getLastname().equals("Bogisich202"))
                database.getAllEvents(patientClass);

        column1.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        column2.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        column3.setCellValueFactory(new PropertyValueFactory<>("birthdate"));
        fillPatientView();
    }
    public void fillPatientView()
    {
        List<PatientClass> patientClasses =  filterPatients();
        patientTable = FXCollections.observableArrayList(patientClasses);
        patiendview.setItems(patientTable);
    }
    public List<PatientClass> filterPatients()
    {
        List<PatientClass> helperList = new ArrayList<PatientClass>();
        for(PatientClass patientClass:patients)
        {
            if(patientClass.getLastname().toLowerCase().contains(filter.getText().toLowerCase()))
            {
                helperList.add(patientClass);
            }
        }
        return helperList;
    }
    @FXML
    public void goToALLInfo() {
        if(patiendview.getSelectionModel().getSelectedItem()!=null)
        {
            label.setVisible(false);
            PatientClass patientClass = patiendview.getSelectionModel().getSelectedItem();
            try
            {
                FXMLLoader fxmlLoader = new FXMLLoader(App.class.getClassLoader().getResource("patientInfo"+".fxml"));
                Parent root = fxmlLoader.load();
                PatientInfoController patientInfoController = fxmlLoader.getController();
                Scene lastscene = stage.getScene();
                patientInfoController.initdata(patientClass,lastscene);
                stage.setScene(new Scene(root));
            }catch (IOException e){e.printStackTrace();}
        }
        else
        {
            label.setVisible(true);
        }

    }

}
