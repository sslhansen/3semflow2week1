package facades;

import dto.PersonDTO;
import dto.PersonsDTO;
import entities.Address;
import entities.Person;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PersonFacade implements IPersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private PersonFacade() {
    }

    /**
     *
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getFacadeExample(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    //TODO Remove/Change this before use
    public long getPersonCount() {
        EntityManager em = emf.createEntityManager();
        try {
            long renameMeCount = (long) em.createQuery("SELECT COUNT(r) FROM Person r").getSingleResult();
            return renameMeCount;
        } finally {
            em.close();
        }

    }

    public void populate() {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Address a1 = new Address("Fiskevej 1", 2000, "Frederiksberg");
            Address a2 = new Address("Sverigevej 1", 3400, "Hillerød");
            Address a3 = new Address("Hejvej 1", 3050, "Humlebæk");
            Person p1 = new Person("John", "Svendsen", "12121212");
            Person p2 = new Person("John", "Klausen", "13131313");
            Person p3 = new Person("John", "Henriksen", "13541121");
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

    @Override
    public PersonDTO addPerson(String fName, String lName, String phone, String street, int zip, String city) throws MissingInputException {
        if (fName == null || lName == null) {
            throw new MissingInputException("First Name and/or Last Name is missing");
        } else {
            EntityManager em = getEntityManager();
            try {
                em.getTransaction().begin();
                Person p = new Person(fName, lName, phone);
                TypedQuery q1 = em.createQuery("SELECT a FROM Address a WHERE a.street = :street AND a.zip = :zip AND a.city = :city", Address.class);
                q1.setParameter("street", street);
                q1.setParameter("zip", zip);
                q1.setParameter("city", city);
                List<Address> addr = q1.getResultList();
                if (addr.size() > 0) {
                    p.setAddress(addr.get(0));
                } else {
                    p.setAddress(new Address(street, zip, city));
                }
                em.persist(p);
                em.getTransaction().commit();
                return new PersonDTO(p);
            } finally {
                em.close();
            }
        }
    }

    @Override
    public PersonDTO deletePerson(long id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        try {
            Person p1 = em.find(Person.class, id);
            if (p1 == null) {
                throw new PersonNotFoundException("Could not delete, provided id does not exist");
            } else {
                em.getTransaction().begin();
                TypedQuery q1 = em.createQuery("SELECT e FROM Person e WHERE e.address.id = :id", Person.class);
                q1.setParameter("id", p1.getAddress().getId());
                List<Person> persons = q1.getResultList();
                if (persons.size() > 1) {
                    em.remove(p1);
                } else {
                    em.remove(p1);
                    em.remove(p1.getAddress());
                }
                em.getTransaction().commit();
                return new PersonDTO(p1);
            }

        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO getPerson(long id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        PersonDTO pDTO;
        try {
            em.getTransaction().begin();
            Person p1 = em.find(Person.class, id);
            if (p1 == null) {
                throw new PersonNotFoundException("No person with provided id foundd");
            } else {
                pDTO = new PersonDTO(p1);
                em.getTransaction().commit();
                return pDTO;
            }
        } finally {
            em.close();
        }
    }

    @Override
    public PersonsDTO getAllPersons() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Person> q = em.createQuery("SELECT e FROM Person e", Person.class);
            List<Person> persons = q.getResultList();
            return new PersonsDTO(persons);
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException, MissingInputException {
        EntityManager em = getEntityManager();
        if (p.getfName() == null || p.getlName() == null || p.getfName().length() == 0 || p.getlName().length() == 0) {
            throw new MissingInputException("First Name and/or Last Name is missing");
        } else {
            try {
                Person pers = em.find(Person.class, p.getId());
                if (pers == null) {
                    throw new PersonNotFoundException("The person you are trying to edit is not found!");
                } else {
                    em.getTransaction().begin();
                    pers.setLastName(p.getlName());
                    pers.setFirstName(p.getfName());
                    pers.setPhone(p.getPhone());
                    em.getTransaction().commit();
                    return new PersonDTO(pers);
                }
            } finally {
                em.close();
            }
        }

    }

}
