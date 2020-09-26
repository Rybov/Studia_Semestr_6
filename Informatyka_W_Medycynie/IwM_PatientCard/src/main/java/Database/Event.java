package Database;

import javafx.scene.chart.XYChart;
import org.hl7.fhir.r4.model.*;

import java.util.Date;

public class Event {
    Date date;
    private String type ="";
    private String description="";
    double valueDouble =0;
    private String value="";
    private String requester="";
    private String dateString="";
    private String hour="";
    private String unit;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public double getValueDouble() {
        return valueDouble;
    }

    public String getUnit() {
        return unit;
    }

    Event(Observation observation)
    {
        type="Observation";
        description+=observation.getCode().getText().replace("by Automated count","");
        if(observation.hasValueCodeableConcept())
            value +=observation.getValueCodeableConcept().getText();
        else if(observation.hasValueQuantity())
        {
            valueDouble = observation.getValueQuantity().getValue().doubleValue();
            unit=observation.getValueQuantity().getUnit();
            value +=observation.getValueQuantity().getValue().toString() + " " + unit;
            date = observation.getEffectiveDateTimeType().getValue();
            dateString=observation.getEffectiveDateTimeType().getValue().toLocaleString();
            hour = dateString.substring(dateString.length()-8);
            dateString=dateString.substring(0,dateString.length()-8);
        }
    }
    Event( MedicationRequest medicationRequest)
    {
        type="Medication Reference";
        if(medicationRequest.hasMedicationCodeableConcept())
            description+=medicationRequest.getMedicationCodeableConcept().getText();
        if(medicationRequest.hasMedicationReference())
        {
            Medication medication= (Medication) medicationRequest.getMedicationReference().getResource();
            description+=medication.getCode().getText();
        }
        if(medicationRequest.hasDosageInstruction())
            value = "Dose -> " + medicationRequest.getDosageInstruction();
        if(medicationRequest.hasRequester())
        {
            requester=medicationRequest.getRequester().getDisplay().replaceAll("[0-9]","");
        }
        if(medicationRequest.hasAuthoredOn())
        {
            date=medicationRequest.getAuthoredOn();
            dateString=medicationRequest.getAuthoredOn().toLocaleString();
            hour = dateString.substring(dateString.length()-8);
            dateString=dateString.substring(0,dateString.length()-8);
        }
    }
}
