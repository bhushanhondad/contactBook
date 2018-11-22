package com.plivo.contactbook;
 
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

/**
 * Created by shondad on 21/11/18.
 */

@Path("/contactbook")
public class ContactBookService {

	SessionFactory sessionFactory =null;
	static ContactList contactList = new ContactList();
	private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
	private Matcher matcher;
	private static Pattern pattern;


	/**
	 * '/plivo/contactbook/adduser' url used to add user to user table
	 * returns response 200 OK on success.
	 * @param contactBook
	 * @return
	 */
	@POST
	@Path("/adduser")
	public Response addUser(User contactBook) {

		if(sessionFactory==null) {
			sessionFactory = HibernateSessionFactory.getSessionFactory();
		}

		addEntry(contactBook);

		return Response.status(200).entity("Successful").build();

	}

	/**
	 * '/plivo/contactbook/addcontact' url used to add contacts to contacts table.
	 * performs Validation and returns proper response
	 * @param contacts
	 * @param uriInfo
	 * @param httpHeaders
	 * @return
	 * @throws ApiException
	 */
	@POST
	@Path("/addcontact")
	public Response addContact(Contacts contacts, @Context UriInfo uriInfo,@Context HttpHeaders httpHeaders) throws ApiException {
		if(sessionFactory==null) {
			sessionFactory = HibernateSessionFactory.getSessionFactory();
		}

		String userName = null;
		String password = null;

		if(httpHeaders!=null) {
			 userName = httpHeaders.getRequestHeader("username").get(0);
			 password = httpHeaders.getRequestHeader("password").get(0);
		}
		else
		{
			//For Testing Purposes.
			 userName = "demouser";
			 password = "password";
		}

		Response response = performValidation(contacts, userName, password);
		if (response != null) return response;

		addEntry(contacts);

		return Response.status(200).entity("Successful").build();

	}

	@PUT
	@Path("/editcontact")
	public Response editContact(Contacts contacts, @Context UriInfo uriInfo,@Context HttpHeaders httpHeaders) throws ApiException {
		if(sessionFactory==null) {
			sessionFactory = HibernateSessionFactory.getSessionFactory();
		}

		String userName = null;
		String password = null;

		if(httpHeaders!=null) {
			 userName = httpHeaders.getRequestHeader("username").get(0);
			 password = httpHeaders.getRequestHeader("password").get(0);
		}else {
			//For Testing Purposes.
			 userName = "demouser";
			 password = "password";
		}

		Response response = performValidationForEdit(contacts, userName, password);
		if (response != null) return response;

		updateEntry(contacts);

		return Response.status(204).entity("Successful").build();

	}

	/**
	 * '/plivo/contactbook/contacts' gets all contacts for a specific user
	 * '/plivo/contactbook/contacts?username=<Username>' gets specific contacts with specific username.
	 * '/plivo/contactbook/contacts?emailid=<emailId>' gets specific contacts with specific Email-id.
	 * '/plivo/contactbook/contacts?page=<'1'|'2'|'3'|'4'>' 10 pages at a time.
	 * @param pageNumber
	 * @param serachUserName
	 * @param searchEmailId
	 * @param uriInfo
	 * @param httpHeaders
	 * @return
	 * @throws ApiException
	 */
	@GET
	@Path("/contacts")
	public ContactList getContacts(@QueryParam("page") String pageNumber,@QueryParam("username") String serachUserName,@QueryParam("emailid") String searchEmailId,@Context UriInfo uriInfo,@Context HttpHeaders httpHeaders)throws ApiException {
		if(sessionFactory==null) {
			sessionFactory = HibernateSessionFactory.getSessionFactory();
		}

		String userName = null;
		String password = null;

		if(httpHeaders!=null) {
			userName = httpHeaders.getRequestHeader("username").get(0);
			password = httpHeaders.getRequestHeader("password").get(0);
		}else {
			//For Testing Purposes.
			userName = "demouser";
			password = "password";
		}

		//Checks if userName and password is Present in the user table.
		if(!isValidUser(userName,password))
		{
			throw new ApiException("Unauthorized User.");
		}

		//check's if userName is sent as query param in url.
		if(serachUserName!=null)
		{
			contactList.setContactsList(executeQueryForContacts("userName",serachUserName));
			return contactList;
		}

		//check's if emailId is sent as query param in url.
		if(searchEmailId!=null)
		{
			contactList.setContactsList(executeQueryForContacts("emailId",searchEmailId));
			return contactList;
		}

		//check's if pageNumber is sent as query param in url at a given time 10 contacts are populated.
		if(pageNumber!=null)
		{
			contactList.setContactsList(executeQueryForContacts("belongsToUser",userName));
			contactList = getPageListItems(contactList,pageNumber);
			return contactList;
		}

		contactList.setContactsList(executeQueryForContacts("belongsToUser",userName));
		return contactList;
	}


	/**
	 * Gets page number and returns list of contacts
	 * if page is 1 return 0 to 9 contacts
	 * @param contactList
	 * @param pageNumber
	 * @return
	 */
	private ContactList getPageListItems(ContactList contactList,String pageNumber) {

		if(contactList.getContactsList()==null)
		{
			return contactList;
		}

		Integer startingIndex = (Integer.parseInt(pageNumber)-1) == 0 ? 0 : ((Integer.parseInt(pageNumber) - 1) * 10) -1 ;
		Integer endingIndex = Integer.parseInt(pageNumber) * 10 > contactList.getContactsList().size() ? contactList.getContactsList().size() : Integer.parseInt(pageNumber) * 10;

		try {
			contactList.setContactsList(contactList.getContactsList().subList(startingIndex, endingIndex));
		}
		catch (IndexOutOfBoundsException exception)
		{
			contactList= new ContactList();
		}
		catch (IllegalArgumentException exception)
		{
			contactList= new ContactList();
		}


		return contactList;
	}

	/**
	 * Perform basic validation for edit of contacts.
	 * @param contacts
	 * @param userName
	 * @param password
	 * @return
	 */
	private Response performValidationForEdit(Contacts contacts, String userName, String password) {
		//Check if the User is valid to add contacts.
		if(!isValidUser(userName,password) || !isUserAndBelongsToUserSame(userName,contacts.getBelongsToUser())) {
			return Response.status(401).entity("Unauthorized User.").build();
		}

		//Check if email address already exist.
		if(!isEmailExist(contacts.getEmailId())) {
			return Response.status(406).entity("EmailId Not present unable to update.").build();
		}

		return null;
	}

	/**
	 * Perform basic validation for create of contacts.
	 * @param contacts
	 * @param userName
	 * @param password
	 * @return
	 */
	private Response performValidation(Contacts contacts, String userName, String password) {
		//Check if the User is valid to add contacts.
		if(!isValidUser(userName,password) || !isUserAndBelongsToUserSame(userName,contacts.getBelongsToUser())) {
			return Response.status(401).entity("Unauthorized User.").build();
		}

		//Check if email address already exist.
		if(isEmailExist(contacts.getEmailId())) {
			return Response.status(406).entity("EmailId Already present.").build();
		}

		if(!validateEmail(contacts.getEmailId()))
		{
			return Response.status(406).entity("EmailId not in format").build();

		}

		return null;
	}

	/**
	 * checks if email is already present the DB
	 * @param emailId
	 * @return
	 */
	private boolean isEmailExist(String emailId) {

		if(sessionFactory==null) {
			sessionFactory = HibernateSessionFactory.getSessionFactory();
		}

		// Getting Session Object From SessionFactory
		Session sessionObj = sessionFactory.openSession();
		// Getting Transaction Object From Session Object
		sessionObj.beginTransaction();

		Criteria cr = sessionObj.createCriteria(Contacts.class);
		cr.add(Restrictions.eq("emailId", emailId));
		List<Contacts> results = cr.list();

		if(results.size() > 0)
			return true;
		else
			return false;
	}

	/**
	 * returns contacts for queried value.
	 * @param propertyName
	 * @param value
	 * @return
	 */
	private List<Contacts> executeQueryForContacts(String propertyName,String value)
	{

		// Getting Session Object From SessionFactory
		Session sessionObj = sessionFactory.openSession();
		// Getting Transaction Object From Session Object
		sessionObj.beginTransaction();

		Criteria cr = sessionObj.createCriteria(Contacts.class);
		cr.add(Restrictions.eq(propertyName, value));
		List<Contacts> results = cr.list();
		sessionObj.close();

		return  results;

	}

	/**
	 * check if userName passed in header is same as belongs to field in contact object.
	 * @param userName
	 * @param belongsToUser
	 * @return
	 */
	private boolean isUserAndBelongsToUserSame(String userName,String belongsToUser) {
		return userName.equals(belongsToUser);
	}

	/**
	 * check if userName and password exist in User table.
	 * @param userName
	 * @param password
	 * @return
	 */
	private boolean isValidUser(String userName,String password) {

		if(sessionFactory==null) {
			sessionFactory = HibernateSessionFactory.getSessionFactory();
		}

		// Getting Session Object From SessionFactory
		Session sessionObj = sessionFactory.openSession();
		// Getting Transaction Object From Session Object
		sessionObj.beginTransaction();

		Criteria cr = sessionObj.createCriteria(User.class);
		cr.add(Restrictions.eq("userName", userName));
		cr.add(Restrictions.eq("password", password));

		List<Contacts> results = cr.list();

		if(results.size()==1)
			return true;
		else
			return false;

	}

	/**
	 * This methos saves the object to DB
	 * @param addObject
	 */
	private void addEntry(Object addObject) {
		// Getting Session Object From SessionFactory
		Session sessionObj = sessionFactory.openSession();

		// Getting Transaction Object From Session Object
		sessionObj.beginTransaction();

		sessionObj.save(addObject);
		sessionObj.getTransaction().commit();
		sessionObj.close();
	}

	/**
	 * This method Updates object in DB
	 * @param addObject
	 */
	private void updateEntry(Object addObject) {
		// Getting Session Object From SessionFactory
		Session sessionObj = sessionFactory.openSession();

		// Getting Transaction Object From Session Object
		sessionObj.beginTransaction();

		sessionObj.saveOrUpdate(addObject);
		sessionObj.getTransaction().commit();
		sessionObj.close();
	}

	/**
	 * This method validates the input email address with EMAIL_REGEX pattern
	 *
	 * @param email
	 * @return boolean
	 */
	public boolean validateEmail(String email) {
		pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public static void main(String args[]) throws JAXBException
	{
		ContactBookService contactBookService = new ContactBookService();
		contactBookService.isValidUser("demouser","password");
		contactBookService.isEmailExist("test1@test1.com");
		Contacts contacts = new Contacts();
		contacts.setUserName("xyz");
		contacts.setEmailId("test1@test12.com");
		contacts.setBelongsToUser("demouser");

		contactBookService.validateEmail("testtest.com");

		try {
			contactBookService.editContact(contacts, null, null);
		}
		catch (Exception e)
		{

		}
		contactBookService.getPageListItems(contactList,"2");


		try {
			ContactList userList=contactBookService.getContacts(null,null, null, null, null);

			JAXBContext jaxbContext = JAXBContext.newInstance(ContactList.class);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(userList, System.out);
		}
		catch (ApiException e)
		{

		}
	}

}