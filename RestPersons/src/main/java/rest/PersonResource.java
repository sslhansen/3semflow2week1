package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.PersonDTO;
import dto.PersonsDTO;
import entities.Person;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import utils.EMF_Creator;
import facades.PersonFacade;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//Todo Remove or change relevant parts before ACTUAL use
@Path("person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();

    //An alternative way to get the EntityManagerFactory, whithout having to type the details all over the code
    //EMF = EMF_Creator.createEntityManagerFactory(DbSelector.DEV, Strategy.CREATE);
    private static final PersonFacade FACADE = PersonFacade.getFacadeExample(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }

    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPersonCount() {
        long count = FACADE.getPersonCount();
        //System.out.println("--------------->"+count);
        return "{\"count\":" + count + "}";  //Done manually so no need for a DTO
    }

    @Path("{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPerson(@PathParam("id") Long id) throws PersonNotFoundException {
        PersonDTO p = FACADE.getPerson(id);
        return new Gson().toJson(p);
    }

    @Path("populate")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String populate() {
        FACADE.populate();
        //System.out.println("--------------->"+count);
        return "added entries to db";  //Done manually so no need for a DTO
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String addPerson(String pers) throws MissingInputException {
        PersonDTO p1 = new Gson().fromJson(pers, PersonDTO.class);
        PersonDTO p = FACADE.addPerson(p1.getfName(), p1.getlName(), p1.getPhone());
        return GSON.toJson(p);
    }

    @Path("{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String deletePerson(@PathParam("id") Long id) throws PersonNotFoundException {
        FACADE.deletePerson(id);
        return "person removed";
    }

    @Path("all")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String all() {
        PersonsDTO persons = FACADE.getAllPersons();
        return GSON.toJson(persons);
    }

    @Path("{id}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String editPerson(@PathParam("id") Long id, String person) throws PersonNotFoundException, MissingInputException {
        PersonDTO pDTO = GSON.fromJson(person, PersonDTO.class);
        pDTO.setId(id);
        PersonDTO pny = FACADE.editPerson(pDTO);
        return GSON.toJson(pny);
    }

}
