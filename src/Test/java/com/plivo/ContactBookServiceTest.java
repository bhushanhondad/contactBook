import com.plivo.contactbook.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * Created by shondad on 22/11/18.
 */
public class ContactBookServiceTest {

    static ContactBookService contactBookService = new ContactBookService();
    SessionFactory sessionFactory =null;
    static User user = null;
    static Contacts validContacts=null;

    @After
    public void clenUpTask()
    {

        if(user!=null)
        {
            deleteObject(user);
        }

        if(validContacts!=null){
            deleteObject(validContacts);
        }
    }


    public void deleteObject(Object object) {
        if(sessionFactory==null) {
            sessionFactory = HibernateSessionFactory.getSessionFactory();
        }

        // Getting Session Object From SessionFactory
        Session sessionObj = sessionFactory.openSession();

        // Getting Transaction Object From Session Object
        sessionObj.beginTransaction();

        sessionObj.delete(object);
        sessionObj.getTransaction().commit();
        sessionObj.close();
    }

    /**
     * Check if user is Added successfully.
     * @throws Exception
     */
    @Test
    public void testAddUser() throws Exception {
        user = new User();
        user.setUserName("demouserTest");
        user.setPassword("demoPassword");

        Response response = contactBookService.addUser(user);

        assertEquals("Invalid Response",response.getStatus(),200);
    }


    /**
     * Check if valid user can add contacts.
     * @throws Exception
     */
    @Test
    public void testAddContacts() throws Exception {
        validContacts = new Contacts();
        validContacts.setEmailId("test@test.com");
        validContacts.setUserName("testUser123");
        validContacts.setBelongsToUser("demouser");

        Response response = contactBookService.addContact(validContacts,null,null);
        assertEquals("Invalid Response, should have been 200OK",response.getStatus(),200);
    }

    /**
     * Check if valid user can add contacts with invalid EmailId.
     * @throws Exception
     */
    @Test
    public void testAddContactsInvalidEmailId() throws Exception {

        createContact("testtest.com","testUser123","demouser");

        Response response = contactBookService.addContact(validContacts,null,null);
        assertEquals("Invalid Response, should have been 401",response.getStatus(),406);
        validContacts=null;
    }

    /**
     * Check if valid invalid user can add contacts.
     * @throws Exception
     */
    @Test
    public void testAddContactsWithInvalidUser() throws Exception {
        createContact("testtest.com","testUser123","demouser123");

        Response response = contactBookService.addContact(validContacts,null,null);
        assertEquals("Invalid Response, should have been 401",response.getStatus(),401);
        validContacts=null;
    }

    /**
     * Check if valid user can edit contacts.
     * @throws Exception
     */
    @Test
    public void testEditContacts() throws Exception {
        createContact("test@test.com","testUser123","demouser");

        Response response = contactBookService.addContact(validContacts,null,null);

        if(response.getStatus()==200)
        {
            validContacts.setUserName("updateUserName");
            response = contactBookService.editContact(validContacts,null,null);
        }
        assertEquals("Invalid Response, should have been 204",response.getStatus(),204);
    }

    /**
     * Check if Invalid user can edit contacts.
     * @throws Exception
     */
    @Test
    public void testInvalidUserEditContacts() throws Exception {
        createContact("test@test.com","testUser123","demouser123");

        Response response = contactBookService.addContact(validContacts,null,null);

        if(response.getStatus()==200)
        {
            validContacts.setUserName("updateUserName");
            response = contactBookService.editContact(validContacts,null,null);
        }
        assertEquals("Invalid Response, should have been 401",response.getStatus(),401);
        validContacts=null;
    }

    /**
     * Check if valid user can edit contacts with invalid EmailId.
     * @throws Exception
     */
    @Test
    public void testValidUserEditContactsWithInvalidEmailId() throws Exception {
        createContact("test@test.com","testUser123","demouser123");

        Response response = contactBookService.addContact(validContacts,null,null);

        if(response.getStatus()==200)
        {
            validContacts.setUserName("testtest.com");
            validContacts.setUserName("updateUserName");
            response = contactBookService.editContact(validContacts,null,null);
        }
        assertEquals("Invalid Response, should have been 401",response.getStatus(),401);
        createContact("test@test.com","testUser123","demouser123");
    }

    /**
     * Test to check if valid contacts are retrieved for valid user.
     * @throws Exception
     */
    @Test
    public void testGetContacts() throws Exception {
        ContactList contactList = contactBookService.getContacts(null,null,null,null,null);
        assertEquals("Invalid Response, should have been 24 record",contactList.getContactsList().size(),24);
    }

    /**
     * Test to check if specific contacts with username are retrieved for valid user.
     * @throws Exception
     */
    @Test
    public void testGetContactsWithSpecificUserName() throws Exception {
        ContactList contactList = contactBookService.getContacts(null,"demouser",null,null,null);
        assertEquals("Invalid Response, should have been 10 record",contactList.getContactsList().size(),10);
    }

    /**
     * Test to check if specific contacts with email are retrieved for valid user.
     * @throws Exception
     */
    @Test
    public void testGetContactsWithSpecificEmail() throws Exception {
        ContactList contactList = contactBookService.getContacts(null,null,"demouser10@demouser10.com",null,null);
        assertEquals("Invalid Response, should have been 1 record",contactList.getContactsList().size(),1);
    }

    /**
     * Test to check if specific contacts 10 records are retrieved  for valid user.
     * @throws Exception
     */
    @Test
    public void testGetContactsWithSpecificPage() throws Exception {
        ContactList contactList = contactBookService.getContacts("1",null,null,null,null);
        assertEquals("Invalid Response, should have been 1 record",contactList.getContactsList().size(),10);
    }

    /**
     * Delete contact.
     * @throws Exception
     */
    @Test
    public void testDeleteContacts() throws Exception {
        createContact("test@test.com","testUser123","demouser");

        Response response = contactBookService.addContact(validContacts,null,null);

        if(response.getStatus()==200)
        {
            response = contactBookService.deleteContact(validContacts.getEmailId());
        }
        assertEquals("Invalid Response, should have been 200",response.getStatus(),200);
    }

    private void createContact(String emailId,String userName,String belongsToUser) {
        validContacts = new Contacts();
        validContacts.setEmailId(emailId); //Invalid Email ID
        validContacts.setUserName(userName);
        validContacts.setBelongsToUser(belongsToUser);
    }



}