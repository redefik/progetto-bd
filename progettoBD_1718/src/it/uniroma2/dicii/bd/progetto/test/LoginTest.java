package it.uniroma2.dicii.bd.progetto.test;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import it.uniroma2.dicii.bd.progetto.entry.LoginSession;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.repository.UsersRepository;
import it.uniroma2.dicii.bd.progetto.repository.UsersRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.user.User;
import it.uniroma2.dicii.bd.progetto.user.UserBean;

public class LoginTest {

	/*
	 * Il metodo inserisce nel database gli input necessari all'esecuzione del caso di test.
	 * Precisamente, inserisce un utente. 
	 * Il test verificherà che la ricerca dell'utente abbia esito positivo mentre quella di un utente inesistente fallisca.
	 * */
	@BeforeClass
	public static void setUp() throws ConfigurationError, DataAccessError {
		User userTest = new User();
		userTest.setUsername("usernameTest");
		userTest.setFirstName("nameTest");
		userTest.setLastName("lastNameTest");
		userTest.setPassword("passwordTest");
		userTest.setMail("mailTest");
		userTest.setType(0);
		
		UsersRepositoryFactory usersRepositoryFactory = UsersRepositoryFactory.getInstance();
		UsersRepository usersRepository = usersRepositoryFactory.createUsersRepository();
		usersRepository.persist(userTest);
	}
	
	@Test
	public void testFindExistentUser() throws ConfigurationError, DataAccessError {
		LoginSession loginSession = LoginSession.getInstance();
		UserBean userFound = loginSession.findUser("usernameTest", "passwordTest");
		UserBean userExpected = new UserBean();
		userExpected.setUsername("usernameTest");
		userExpected.setFirstName("nameTest");
		userExpected.setLastName("lastNameTest");
		userExpected.setPassword("passwordTest");
		userExpected.setMail("mailTest");
		userExpected.setType(0);
		Assert.assertEquals(userExpected, userFound); //nota: il metodo equals è stato opportunamente sovrascritto in UserBean
	}
	
	@Test
	public void testFindNotExistentUser() throws ConfigurationError, DataAccessError {
		LoginSession loginSession = LoginSession.getInstance();
		UserBean userFound = loginSession.findUser("xxxx", "yyyy");
		Assert.assertTrue(userFound == null);
	}
	
	/*
	 * Il metodo rimuove dal database i dati utilizzati nel test
	 * */
	@AfterClass
	public static void cleanUp() throws ConfigurationError, DataAccessError {
		UsersRepositoryFactory usersRepositoryFactory = UsersRepositoryFactory.getInstance();
		UsersRepository usersRepository = usersRepositoryFactory.createUsersRepository();
		usersRepository.deleteUserWithUsername("usernameTest");
	}
	
}
