# 🤝 Contributing to the Project

Thank you for your interest in contributing to the **Patient and Doctor Management** project!  
Contributions are always welcome, whether it’s fixing bugs, improving documentation, or adding new features.

---

## 📌 How to Contribute

1. **Fork the repository**  
   - Create your own copy of the project in your GitHub account.

2. **Create a new branch**  
   Use a descriptive branch name:
```bash
   git checkout -b feature/new-feature
```
Example: feature/search-by-lastname or fix/validation-error.

3. **Make your changes**

- Follow Java and Spring Boot best practices.

- If you modify the database, update the migration scripts if available.

- Ensure the code compiles without errors.

4. **Add tests**

- Include unit tests with JUnit, and Mockito when needed.

- Run tests with:
```bash
   mvn test
```
5. **Commit your changes**
- Use clear and descriptive messages:
```bash
  git commit -m "Add search by patient last name"
```

6. **Push your branch**
```bash
git push origin feature/new-feature
```

7. **Open a Pull Request (PR)**

- Explain what problem it solves or what feature it adds.

- Add screenshots if it’s a visual change or usage examples if it’s an endpoint.

## 🧪 Code and Test Standards

- Code style: **Java 17 + Spring Boot**  
- Naming conventions:  
  - Classes: `PatientService`, `DoctorController`.  
  - Methods: `findByLastName()`, `createPatient()`.  
- Tests:  
  - `src/test/java` should mirror the structure of `src/main/java`.  
  - Use **Mockito** to mock dependencies.  

---

## 📄 Documentation

- Update the `README.md` if you add new features.  
- Provide request/response examples for any new endpoints.  

---

## ⚠️ Reporting Issues

If you find a bug:

1. First, check the [Issues](https://github.com/veradelacruz/mini-netflix/issues) to see if it’s already reported.  
2. If not, open a new one and describe:  
- Steps to reproduce.  
- Expected vs. actual behavior.  
- Relevant logs or error messages.  

---

Thank you for helping improve this project! 💙

