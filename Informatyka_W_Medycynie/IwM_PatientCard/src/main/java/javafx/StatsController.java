package javafx;

import Database.Event;
import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import static java.lang.Math.round;

public class StatsController implements Initializable {
    @FXML
    private Text nodata;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    DatePicker min,max;
    @FXML
    LineChart<Date, Double> chart;
    @FXML
    ChoiceBox<String> choice;
    private ArrayList<Event> eventArrayList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        choice.setItems(FXCollections.observableArrayList("Body Height","Body Weight","Body Mass Index"));
        nodata.setVisible(false);
    }
    public void init(ArrayList<Event> eventArrayList)
    {
        this.eventArrayList=eventArrayList;
        choice.setValue("Body Height");
        choiceOnClick();
    }
    public void choiceOnClick()
    {
        if(choice.getValue()!=null)
            generate(choice.getValue());
    }
    private void generate(String type)
    {
        ArrayList<Event> list =filter(eventArrayList,type);
        if(list.size()>0)
        {
            nodata.setVisible(false);
            XYChart.Series<Date, Double> series = new XYChart.Series();
            ArrayList<Double> axis= new ArrayList<Double>();
            Double small,big;
            small=list.get(0).getValueDouble();
            big=small;
            for(Event e: list)
            {
                if(e.getValueDouble()>big)
                    big=e.getValueDouble();
                if(e.getValueDouble()<small)
                    small=e.getValueDouble();
                if(e.getValueDouble()!=0)
                    series.getData().add(new XYChart.Data(e.getDate().toGMTString(),e.getValueDouble()));
            }
            chart.getData().clear();
            chart.getData().add(series);
            Double avg = round((big- small)/5.0)*1.0;
            series.setName(type);
            xAxis.setLabel("Time");
            xAxis.setAnimated(false);
            yAxis.setLabel(list.get(0).getUnit());
            yAxis.setAnimated(true);
            yAxis.setAutoRanging(false);
            yAxis.setUpperBound(round(big)+1);
            yAxis.setLowerBound(round(small)-1);
            yAxis.setTickUnit(avg);
        }
        else
        {
            nodata.setVisible(true);
        }

    }





    public ArrayList<Event> filter(ArrayList<Event> help,String type)
    {
        ArrayList<Event> helperList = new ArrayList<Event>();
        for(Event e: help)
        {
            if (e.getDescription().equals(type) && filterDown(e) && filterUp(e))
                helperList.add(e);
        }
        return helperList;
    }
    public Boolean filterUp(Event e)
    {
        if(e.getDate()!=null)
        {
            if(max.getValue()!=null)
            {
                LocalDate localDate = max.getValue();
                Instant instantlast = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
                return e.getDate().before(java.sql.Date.from(instantlast));
            }
            return true;
        }
        return false;
    }
    public Boolean filterDown(Event e)
    {
        if(e.getDate()!=null)
        {
            if(min.getValue()!=null)
            {
                LocalDate localDate = min.getValue();
                Instant instantlast = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
                return e.getDate().after(java.sql.Date.from(instantlast));
            }
            return true;
        }
        return false;
    }
}
