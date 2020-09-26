package Database;

import org.hl7.fhir.r4.model.Patient;

public class PatientClass {

    private final String id;
    private final String firstname;
    private final String lastname;
    private final String gender;
    private final String birthdate;
    private final String phonenumber;
    private final String city;
    private final String address;
    private final String country;
    private final String postcode;

    public PatientClass(Patient patient) {
        this.id=patient.getIdElement().getIdPart();
        this.firstname=patient.getName().get(0).getGivenAsSingleString(). replaceAll("[0-9]","");
        this.lastname=patient.getName().get(0).getFamily(). replaceAll("[0-9]","");
        String date=patient.getBirthDate().toLocaleString();
        date=date.substring(0,date.length()-8);
        this.birthdate=date;
        this.gender=patient.getGender().toString();
        this.phonenumber=patient.getTelecom().get(0).getValue();
        this.city=patient.getAddress().get(0).getCity();
        this.country=patient.getAddress().get(0).getCountry();
        this.postcode=patient.getAddress().get(0).getPostalCode();
        this.address=patient.getAddress().get(0).getLine().get(0).toString();
    }

    public String getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String getCountry() {
        return country;
    }

    public String getPostcode() {
        return postcode;
    }
}
