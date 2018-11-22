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

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

/**
 * Created by shondad on 21/11/18.
 */

@Path("/contactbook")
public class ContactBookService {

	SessionFactory sessionFactory =null;
	final static Logger logger = Logger.getLogger(ContactBookService.class);
	static ContactList contactList = new ContactList();


	@POST
	@Path("/adduser")
	public Response addUser(User contactBook) {

		if(sessionFactory==null) {
			sessionFactory = HibernateSessionFactory.getSessionFactory();
		}

		addEntry(contactBook);

		return Response.status(200).entity("Successful").build();

	}

	@POST
	@Path("/addcontact")
	public Response addContact(Contacts contacts, @Context UriInfo uriInfo,@Context HttpHeaders httpHeaders) throws ApiException {
		if(sessionFactory==null) {
			sessionFactory = HibernateSessionFactory.getSessionFactory();
		}

		String userName = httpHeaders.getRequestHeader("username").get(0);

		String password = httpHeaders.getRequestHeader("password").get(0);

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

		if(httpHeaders!=null) {
			String userName = httpHeaders.getRequestHeader("username").get(0);
			String password = httpHeaders.getRequestHeader("password").get(0);
		}
		String userName = "demouser";
		String password = "password";

		Response response = performValidationForEdit(contacts, userName, password);
		if (response != null) return response;

		updateEntry(contacts);

		return Response.status(204).entity("Successful").build();

	}

	@GET
	@Path("/contacts")
	public ContactList getContacts(@QueryParam("page") String pageNumber,@QueryParam("username") String serachUserName,@QueryParam("emailid") String searchEmailId,@Context UriInfo uriInfo,@Context HttpHeaders httpHeaders)throws ApiException {
		if(sessionFactory==null) {
			sessionFactory = HibernateSessionFactory.getSessionFactory();
		}

		String userName = httpHeaders.getRequestHeader("username").get(0);
		String password = httpHeaders.getRequestHeader("password").get(0);

		if(!isValidUser(userName,password))
		{
			throw new ApiException("Unauthorized User.");
		}

		if(serachUserName!=null)
		{
			contactList.setContactsList(executeQueryForContacts("userName",serachUserName));
			return contactList;
		}

		if(searchEmailId!=null)
		{
			contactList.setContactsList(executeQueryForContacts("emailId",searchEmailId));
			return contactList;
		}

		if(pageNumber!=null)
		{
			contactList.setContactsList(executeQueryForContacts("belongsToUser",userName));
			contactList = getPageListItems(contactList,pageNumber);
			return contactList;
		}

		contactList.setContactsList(executeQueryForContacts("belongsToUser",userName));
		return contactList;
	}



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

	private Response performValidation(Contacts contacts, String userName, String password) {
		//Check if the User is valid to add contacts.
		if(!isValidUser(userName,password) || !isUserAndBelongsToUserSame(userName,contacts.getBelongsToUser())) {
			return Response.status(401).entity("Unauthorized User.").build();
		}

		//Check if email address already exist.
		if(isEmailExist(contacts.getEmailId())) {
			return Response.status(406).entity("EmailId Already present.").build();
		}
		return null;
	}

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

	private boolean isUserAndBelongsToUserSame(String userName,String belongsToUser) {
		return userName.equals(belongsToUser);
	}

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

	private void addEntry(Object addObject) {
		// Getting Session Object From SessionFactory
		Session sessionObj = sessionFactory.openSession();

		// Getting Transaction Object From Session Object
		sessionObj.beginTransaction();

		sessionObj.save(addObject);
		sessionObj.getTransaction().commit();
		sessionObj.close();
	}

	private void updateEntry(Object addObject) {
		// Getting Session Object From SessionFactory
		Session sessionObj = sessionFactory.openSession();

		// Getting Transaction Object From Session Object
		sessionObj.beginTransaction();

		sessionObj.saveOrUpdate(addObject);
		sessionObj.getTransaction().commit();
		sessionObj.close();
	}


	public static void main(String args[]) throws JAXBException
	{
		ContactBookService contactBookService = new ContactBookService();
		//contactBookService.isValidUser("demouser","password");
		//contactBookService.isEmailExist("test1@test1.com");
		Contacts contacts = new Contacts();
		contacts.setUserName("xyz");
		contacts.setEmailId("test1@test12.com");
		contacts.setBelongsToUser("demouser");

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