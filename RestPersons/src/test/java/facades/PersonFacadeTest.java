package facades;

import dto.PersonDTO;
import dto.PersonsDTO;
import entities.Address;
import utils.EMF_Creator;
import entities.Person;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    private Address a1;
    private Address a2;
    private Address a3;
    private Person p1;
    private Person p2;
    private Person p3;

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = PersonFacade.getFacadeExample(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the script below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            a1 = new Address("Fiskevej 1", 2000, "Frederiksberg");
            a2 = new Address("Sverigevej 1", 3400, "Hillerød");
            a3 = new Address("Hejvej 1", 3050, "Humlebæk");
            p1 = new Person("John", "Svendsen", "12121212");
            p2 = new Person("John", "Klausen", "13131313");
            p3 = new Person("John", "Henriksen", "13541121");
            p1.setAddress(a1);
            p2.setAddress(a2);
            p3.setAddress(a3);
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    // TODO: Delete or change this method 
    @Test
    public void testAFacadeMethod() {
        assertEquals(3, facade.getPersonCount(), "Expects two rows in the database");
    }

    @Test
    public void testAddPerson() throws MissingInputException {
        int before = facade.getAllPersons().getAll().size();
        facade.addPerson("john", "john", "1234", "johnvej", 123, "johnby");
        int now = facade.getAllPersons().getAll().size();
        assertEquals(before + 1, now);
    }

    @Test
    public void testDeletePerson() throws PersonNotFoundException {
        int before = facade.getAllPersons().getAll().size();
        facade.deletePerson(facade.getAllPersons().getAll().get(0).getId());
        int now = facade.getAllPersons().getAll().size();
        assertEquals(before - 1, now);
    }

    @Test
    public void testGetPerson() throws MissingInputException, PersonNotFoundException {
        PersonDTO pers = facade.addPerson("john", "john", "1234", "johnvej", 123, "johnby");
        PersonDTO persGet = facade.getPerson(pers.getId());
        assertEquals(pers.getfName(), persGet.getfName());
    }

    @Test
    public void testGetAllPersons() {
        PersonsDTO personsdto = facade.getAllPersons();
        assertEquals(3, personsdto.getAll().size());
    }

    @Test
    public void testEditPerson() throws PersonNotFoundException, MissingInputException {
        PersonDTO pDTO = new PersonDTO(p1);
        pDTO.setfName("Juice");
        facade.editPerson(pDTO);
        assertEquals("Juice", facade.getPerson(pDTO.getId()).getfName());
    }

    @Test
    public void testEditPersonException() {
        Exception exception = assertThrows(MissingInputException.class, () -> {
            PersonDTO pDTO = new PersonDTO(p1);
            pDTO.setfName("");
            facade.editPerson(pDTO);
        });
        assertEquals("First Name and/or Last Name is missing", exception.getMessage());
    }

    @Test
    public void testEditPersonException2() {
        Exception exception = assertThrows(PersonNotFoundException.class, () -> {
            PersonDTO pDTO = new PersonDTO(p1);
            pDTO.setId(9999999);
            facade.editPerson(pDTO);
        });
        assertEquals("The person you are trying to edit is not found!", exception.getMessage());
    }

}
