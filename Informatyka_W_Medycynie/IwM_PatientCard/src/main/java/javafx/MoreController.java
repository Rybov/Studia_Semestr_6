package javafx;

import Database.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class MoreController implements Initializable {


    Event event;
    @FXML
    Text type,description,value,requester,dateString,hour,requester2;
    public void initdata(Event event){this.event=event;
    type.setText(set(event.getType()));
    description.setText(set(event.getDescription()));
    value.setText(set(event.getValue().equals("") ? "No data" : event.getValue()));

    if(event.getType().equals("Medication Reference"))
    {
        requester2.setVisible(true);
        requester.setVisible(true);
        requester.setText(set(event.getRequester()));
    }
    dateString.setText(set(event.getDateString()));
    hour.setText(set(event.getHour()));
    }
    public String set(String s)
    {
        return s.equals("")?"No data":s;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        requester2.setVisible(false);
        requester.setVisible(false);
    }
}
