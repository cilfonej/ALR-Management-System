package edu.wit.alr;

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

	@Transactional
	public void add() {
//		shipmentRepository.deleteAll();
//		drugRepository.deleteAll();
//		dogRepository.deleteAll();
//		personRepository.deleteAll();
//		fosterRepository.deleteAll();
//		distributorRepository.deleteAll();
//		
//		Person lynn = new Person("Lynn", "Stozenbach");
//		lynn.setPrimaryContact(new PhoneContact("+1 (860) 429-8794"));
//		personRepository.save(lynn);
//		
//		Distributor ann = new Distributor("Ann", "Cilfone", new Address("136 Dalleville Rd.", "Willington", "CT", "06279"));
//		ann.setPrimaryContact(new EmailContact("acilfone@gmail.com"));
//		distributorRepository.save(ann);
//		
//		Foster foster_lynn = new Foster(lynn, new Address("10 Lakeview Drive", "Andover", "CT", "06279-0123"));
//		foster_lynn.setMailingAddress(new Address("136 Dalleville Rd.", "Willington", "CT", "06279"));
//		fosterRepository.save(foster_lynn);
//		
//		Dog ella = new Dog("Ella");
//		ella.setWeight(32);
//		ella.setBirthday(LocalDate.of(2017, 3, 21));
//		ella.setCustodian(lynn);
//		dogRepository.save(ella);
//
//		Drug ft_tiny = new Drug("K9 Advantix II", DrugType.Flea_Tic, 4, 10);
//		Drug ft_small = new Drug("K9 Advantix II", DrugType.Flea_Tic, 11, 20);
//		Drug ft_medium = new Drug("K9 Advantix II", DrugType.Flea_Tic, 20, 55);
//		Drug ft_large = new Drug("K9 Advantix II", DrugType.Flea_Tic, 55, 200);
//
//		Drug heart_tiny = new Drug("Interceptor Plus", DrugType.Heartworm, 2, 8);
//		Drug heart_small = new Drug("Interceptor Plus", DrugType.Heartworm, 8, 25);
//		Drug heart_medium = new Drug("Interceptor Plus", DrugType.Heartworm, 25, 50);
//		Drug heart_large = new Drug("Interceptor Plus", DrugType.Heartworm, 50, 100);
//		
//		drugRepository.saveAll(List.of(ft_tiny, ft_small, ft_medium, ft_large, heart_tiny, heart_small, heart_medium, heart_large));
//		
//		Shipment shipment = new Shipment(ann.getBasePerson(), foster_lynn.getMailingAddress());
//		shipment.setDog(ella);
//		shipment.setSendDate(LocalDate.now());
//		shipment.setStatus(LocalDate.now().plusDays(5), ShipmentStatus.Delivered);
//		shipment.setTrackingNumber("999999999999", Courier.FedEx);
//		shipment.addItem(ft_medium, 1);
//		shipment.addItem(heart_medium, 1);
//		shipmentRepository.save(shipment);
	}
	
	private Random current;

	@Transactional
	public void generateTestData(int seed) {
		Random rand = current = new Random(seed);
		
		clearAll();
		generateStaticData();
		
		ArrayList<Person> people = new ArrayList<>();
		
		for(int i = 1000, limit = rand.nextInt(6) + 2; i < limit; i ++) {
			Faker faker = data();
			Person person = generatePerson(faker);
			people.add(person);
			
//			if(rand.nextDouble() > .9) 
//				distributors.add(generateDistributor(faker, person));
			
			if(rand.nextDouble() > .1)
				generateFoster(faker, person);
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
//		Distributor ann = new Distributor("Ann", "Cilfone", new Address("123 Street Rd.", "Boston", "NY", "90210"));
//		ann.setPrimaryContact(new PhoneContact("1 (800) 867-5309"));
//		distributorRepository.save(ann);
		
		Person person = new Person("Ann", "Cilfone");
		Foster foster = new Foster(person);
		Faker faker = new Faker();
//		foster.setPrimaryPhone(generatePhoneContact(faker));
		foster.setEmail(generateEmailContact(faker));
		foster.setHomeAddress(generateAddress(faker));
		person.addRole(foster);
		personRepository.save(person);
		
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
		
		person.addRole(foster);
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
