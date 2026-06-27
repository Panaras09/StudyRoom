# StudyRooms – Σύστημα Κράτησης Χώρων Μελέτης

## Περιγραφή
Το StudyRooms είναι Spring Boot web application για κρατήσεις θέσεων και δωματίων μελέτης σε βιβλιοθήκη. Υποστηρίζει UI με Thymeleaf, REST API με JSON, JWT authentication για API clients, H2 database, validation και κατανάλωση εξωτερικής υπηρεσίας αργιών.

## Ρόλοι
- Ανώνυμος: βλέπει βασικές πληροφορίες χώρων και κάνει εγγραφή.
- Φοιτητής: κάνει login, βλέπει χώρους, δημιουργεί/ακυρώνει κράτηση και βλέπει ιστορικό.
- Προσωπικό βιβλιοθήκης: δημιουργεί χώρους, βλέπει στατιστικά και μπορεί να ακυρώσει κρατήσεις μέσω API.

## Τεχνολογίες
- Java 17
- Spring Boot
- Spring MVC + Thymeleaf
- Spring Security
- JWT για REST API
- Spring Data JPA / Hibernate
- H2 Database
- springdoc-openapi / Swagger UI

## Εκτέλεση
```bash
cd studyrooms
mvn spring-boot:run
```

Άνοιγμα εφαρμογής:
- UI: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

H2 στοιχεία:
- JDBC URL: `jdbc:h2:mem:studyrooms`
- User: `sa`
- Password: κενό

## Demo χρήστες
- Φοιτητής: `student` / `student123`
- Προσωπικό: `staff` / `staff123`

## REST API παραδείγματα

### Λήψη JWT token
```bash
curl -X POST http://localhost:8080/api/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username":"student","password":"student123"}'
```

### Προβολή χώρων
```bash
curl http://localhost:8080/api/rooms
```

### Δημιουργία κράτησης με JWT
```bash
TOKEN="PASTE_TOKEN_HERE"
curl -X POST http://localhost:8080/api/reservations \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"roomId":1,"startsAt":"2026-01-15T10:00:00","endsAt":"2026-01-15T11:00:00"}'
```

## Business rules
- Δεν επιτρέπεται κράτηση εκτός ωραρίου χώρου.
- Δεν επιτρέπεται κράτηση όταν δεν υπάρχει διαθέσιμη χωρητικότητα.
- Κάθε φοιτητής έχει μέχρι 2 ενεργές κρατήσεις ανά ημέρα.
- Αν ο φοιτητής έχει penalty λόγω no-show, μπλοκάρεται η νέα κράτηση.
- Η εφαρμογή ελέγχει εξωτερική υπηρεσία εθνικών αργιών και αποτρέπει κράτηση σε αργία.

## Εξωτερική υπηρεσία
Η εφαρμογή χρησιμοποιεί adapter `PublicHolidayAdapter`, ο οποίος καλεί REST API αργιών. Το business logic δεν ξέρει λεπτομέρειες HTTP. Βλέπει μόνο το interface `HolidayPort`. Αυτό είναι παράδειγμα ports/adapters.

## Αρχιτεκτονική
- Controller: Web και REST endpoints.
- Service: business logic και validation κανόνων.
- Repository: JPA πρόσβαση στη βάση.
- Domain: entities και enums.
- External: port/adapters για εξωτερική υπηρεσία.
- Security: form login για UI και JWT για API.
