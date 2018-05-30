package it.uniroma2.dicii.bd.progetto.test;

import org.junit.AfterClass;
import org.junit.Test;

import it.uniroma2.dicii.bd.progetto.administration.AdministrationSession;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.repository.UsersRepository;
import it.uniroma2.dicii.bd.progetto.repository.UsersRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.user.User;
import it.uniroma2.dicii.bd.progetto.user.UserBean;
import org.junit.Assert;

public class UserRegistrationTest {

	/*
	 * Il metodo verifica che la registrazione di un utente semplice avvenga correttamente
	 * */
	@Test
	public void testStandardUserRegistration() throws ConfigurationError, DataAccessError {
		AdministrationSession administrationSession = AdministrationSession.getInstance();
		UserBean userToRegister = new UserBean();
		userToRegister.setUsername("username");
		userToRegister.setFirstName("name");
		userToRegister.setLastName("lastName");
		userToRegister.setMail("mail");
		userToRegister.setPassword("password");
		userToRegister.setType(1); // il tipo 1 corrisponde al livello più basso di privilegi
		administrationSession.registerUser(userToRegister);
		UsersRepositoryFactory usersRepositoryFactory = UsersRepositoryFactory.getInstance();
		UsersRepository usersRepository = usersRepositoryFactory.createUsersRepository();
		
		User userExpected = new User();
		userExpected.setUsername("username");
		userExpected.setFirstName("name");
		userExpected.setLastName("lastName");
		userExpected.setMail("mail");
		userExpected.setPassword("password");
		userExpected.setType(1);
		//nota: il metodo equals è stato opportunamente sovrascritto nella classe User
		User userFound = usersRepository.findByUsernameAndPassword("username", "password");
		Assert.assertEquals(userExpected, userFound);
	}
	
	/*
	 * Il metodo verifica che la registrazione di un amministratore avvenga correttamente
	 * */
	@Test
	public void testAdminUserRegistration() throws ConfigurationError, DataAccessError {
		AdministrationSession administrationSession = AdministrationSession.getInstance();
		UserBean userToRegister = new UserBean();
		userToRegister.setUsername("adminUsername");
		userToRegister.setFirstName("adminName");
		userToRegister.setLastName("adminLastName");
		userToRegister.setMail("adminMail");
		userToRegister.setPassword("adminPassword");
		userToRegister.setType(0); // il tipo 1 corrisponde al livello più alto di privilegi
		administrationSession.registerUser(userToRegister);
		UsersRepositoryFactory usersRepositoryFactory = UsersRepositoryFactory.getInstance();
		UsersRepository usersRepository = usersRepositoryFactory.createUsersRepository();
		
		User userExpected = new User();
		userExpected.setUsername("adminUsername");
		userExpected.setFirstName("adminName");
		userExpected.setLastName("adminLastName");
		userExpected.setMail("adminMail");
		userExpected.setPassword("adminPassword");
		userExpected.setType(0);
		//nota: il metodo equals è stato opportunamente sovrascritto nella classe User
		User userFound = usersRepository.findByUsernameAndPassword("adminUsername", "adminPassword");
		Assert.assertEquals(userExpected, userFound);
	}
	
	
	/*
	 * Il metodo rimuove dal database i dati inseriti durante lo svolgimento del test
	 */
	@AfterClass
	public static void cleanUp() throws ConfigurationError, DataAccessError {
		UsersRepositoryFactory usersRepositoryFactory = UsersRepositoryFactory.getInstance();
		UsersRepository usersRepository = usersRepositoryFactory.createUsersRepository();
		usersRepository.deleteUserWithUsername("username");
		usersRepository.deleteUserWithUsername("adminUsername");
	}
	
}
