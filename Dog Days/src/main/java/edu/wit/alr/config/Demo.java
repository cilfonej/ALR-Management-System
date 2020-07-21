package edu.wit.alr.config;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.github.javafaker.Faker;

import edu.wit.alr.database.model.Address;
import edu.wit.alr.database.model.Contact.EmailContact;
import edu.wit.alr.database.model.Contact.PhoneContact;
import edu.wit.alr.database.model.Dog;
import edu.wit.alr.database.model.Drug;
import edu.wit.alr.database.model.Drug.DrugType;
import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.model.roles.Adopter;
import edu.wit.alr.database.model.roles.ApplicationCoordinator;
import edu.wit.alr.database.model.roles.Foster;
import edu.wit.alr.database.repository.AddressRepository;
import edu.wit.alr.database.repository.DogRepository;
import edu.wit.alr.database.repository.DrugRepository;
import edu.wit.alr.database.repository.PersonRepository;

@Component
public class Demo {
	
	@Autowired
	private DogRepository dogRepository;
	@Autowired
	private PersonRepository personRepository;
	@Autowired
	private DrugRepository drugRepository;
	@Autowired
	private AddressRepository addressRepository;
	
	private Random current;

	@Transactional
	public void generateTestData(int seed) {
		Random rand = current = new Random(seed);
		
		clearAll();
		generateStaticData();
		
		ArrayList<Person> people = new ArrayList<>();
		
		for(int i = 0, limit = rand.nextInt(6) + 4; i < limit; i ++) {
			Faker faker = data();
			Person person = generatePerson(faker);
			people.add(person);
			
			if(rand.nextDouble() > .75) {
				generateAdopter(faker, person);
				
			} else {
				if(rand.nextDouble() > .1)
					generateFoster(faker, person);
				
				if(rand.nextDouble() > .7)
					generateCoordinator(faker, person);
			}
		}
		
		ArrayList<Dog> dogs = new ArrayList<>();
		for(int i = 0, limit = rand.nextInt(6) + 3; i < limit; i ++) {
			Faker faker = data();
			Dog dog = generateDog(faker);
			dogs.add(dog);
			
//			if(faker.bool().bool())
//				dog.setCustodian(fosters.get(rand.nextInt(fosters.size())).getBasePerson());
		}
		
		personRepository.saveAll(people);
		dogRepository.saveAll(dogs);
	}
	
	private Faker data() {
		return new Faker(Locale.getDefault(), current);
	}
	
	private void clearAll() {
		drugRepository.deleteAll();
		dogRepository.deleteAll();
		personRepository.deleteAll();
		addressRepository.deleteAll();
	}
	
	private void generateStaticData() {
		Drug ft_tiny = new Drug("K9 Advantix II", DrugType.Flea_Tic, 4, 10);
		Drug ft_small = new Drug("K9 Advantix II", DrugType.Flea_Tic, 11, 20);
		Drug ft_medium = new Drug("K9 Advantix II", DrugType.Flea_Tic, 20, 55);
		Drug ft_large = new Drug("K9 Advantix II", DrugType.Flea_Tic, 55, 200);

		Drug heart_tiny = new Drug("Interceptor Plus", DrugType.Heartworm, 2, 8);
		Drug heart_small = new Drug("Interceptor Plus", DrugType.Heartworm, 8, 25);
		Drug heart_medium = new Drug("Interceptor Plus", DrugType.Heartworm, 25, 50);
		Drug heart_large = new Drug("Interceptor Plus", DrugType.Heartworm, 50, 100);
		
		drugRepository.saveAll(List.of(ft_tiny, ft_small, ft_medium, ft_large, heart_tiny, heart_small, heart_medium, heart_large));
	}
	
	private void generateFoster(Faker faker, Person person) {
		Foster foster = new Foster(person);
		
		foster.setPrimaryPhone(generatePhoneContact(faker));
		foster.setEmail(generateEmailContact(faker));
		foster.setHomeAddress(generateAddress(faker));
		
		Faker f2 = data();
		if(f2.bool().bool()) foster.setMailingAddress(generateAddress(f2));
	}
	
	private void generateCoordinator(Faker faker, Person person) {
		ApplicationCoordinator coordinator = new ApplicationCoordinator(person);
		
		coordinator.setEmail(generateEmailContact(faker));
		
		if(faker.bool().bool()) coordinator.setPrimaryPhone(generatePhoneContact(faker));
		
		Faker f2 = data();
		if(f2.bool().bool()) coordinator.setMailingAddress(generateAddress(f2));
	}
	
	private void generateAdopter(Faker faker, Person person) {
		Adopter adopter = new Adopter(person);
		
		adopter.setPrimaryPhone(generatePhoneContact(faker));
		adopter.setEmail(generateEmailContact(faker));
		adopter.setHomeAddress(generateAddress(faker));
		
		Faker f2 = data();
		if(f2.bool().bool()) adopter.setMailingAddress(generateAddress(f2));
	}
	
	private Dog generateDog(Faker faker) {
		Dog dog = new Dog(faker.cat().name());
		dog.setWeight(faker.number().randomDouble(2, 2, 120));
		
		LocalDate date = LocalDate.from(faker.date().birthday(0, 15).toInstant().atZone(ZoneId.systemDefault()));
		dog.setBirthday(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
		return dog;
	}
	
	private Person generatePerson(Faker faker) {
		return new Person(faker.name().firstName(), faker.name().lastName());
	}
	
//	private Contact generateContact(Faker faker) {
//		return faker.bool().bool() ? generatePhoneContact(faker) : generateEmailContact(faker);
//	}
	
	private PhoneContact generatePhoneContact(Faker faker) {
		return new PhoneContact(faker.phoneNumber().phoneNumber());
	}
	
	private EmailContact generateEmailContact(Faker faker) {
		return new EmailContact(faker.internet().emailAddress());
	}
	
	private Address generateAddress(Faker faker) {
		return new Address(
				faker.address().streetAddress(), 
				faker.address().city(), 
				faker.address().stateAbbr(), 
				faker.address().zipCode(),
				"United States");
	}
}
