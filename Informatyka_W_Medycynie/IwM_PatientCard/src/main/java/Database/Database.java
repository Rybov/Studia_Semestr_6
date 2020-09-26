package Database;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;
import com.google.common.collect.Collections2;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hl7.fhir.r4.model.ResourceType.Bundle;
import static org.hl7.fhir.r4.model.ResourceType.Observation;

public class Database {
    private List<PatientClass> patients = new ArrayList<PatientClass>();
    public List<PatientClass> getPatients() {
        return this.patients;
    }

    @Search
    public void reloadPatients() {
        List<PatientClass> patients2 = new ArrayList<PatientClass>();
        FhirContext ctx = FhirContext.forR4();
        String serverBase = "http://localhost:8080/baseR4";
        IGenericClient client = ctx.newRestfulGenericClient(serverBase);
        Bundle results = client.search().forResource(Patient.class).returnBundle(org.hl7.fhir.r4.model.Bundle.class).execute();
        do {
            for(org.hl7.fhir.r4.model.Bundle.BundleEntryComponent bundleEntryComponent : results.getEntry())
            {
                Patient patient = (Patient) bundleEntryComponent.getResource();
                patients2.add(new PatientClass(patient));
            }
             results= client.loadPage().next(results).execute();
        } while (results.getLink(IBaseBundle.LINK_NEXT) != null);
        this.patients=patients2;
    }
    public ArrayList<Event> getAllEvents(PatientClass patient)
    {
        ArrayList<Event> events = new ArrayList<Event>();
        FhirContext ctx = FhirContext.forR4();
        String serverBase = "http://localhost:8080/baseR4";
        IGenericClient client = ctx.newRestfulGenericClient(serverBase);
        Bundle results = (Bundle) client
            .operation()
            .onInstance(new IdType("Patient", patient.getId()))
            .named("$everything")
            .withNoParameters(Parameters.class).useHttpGet() // No input parameters
            .execute().getParameterFirstRep().getResource();
        do {
            for(org.hl7.fhir.r4.model.Bundle.BundleEntryComponent bundleEntryComponent : results.getEntry())
            {
                Resource resource = bundleEntryComponent.getResource();
                if(resource instanceof Observation)
                {
                    Observation observation = (Observation) resource;
                    events.add(new Event(observation));
                }
                if(resource instanceof MedicationRequest)
                {
                    MedicationRequest medicationRequest = (MedicationRequest) resource;
                    events.add(new Event(medicationRequest));
                }
            }
            results= client.loadPage().next(results).execute();
        } while (results.getLink(IBaseBundle.LINK_NEXT) != null);

        return events;
    }
}
