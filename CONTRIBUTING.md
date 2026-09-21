# Contributing to SahanaMart

Welcome to **SahanaMart**! This guide outlines setup and coding standards for developers and evaluators.

## Prerequisites
- **JDK 17** (LTS) or higher
- **Apache Maven 3.8+**
- **Apache Tomcat 9.0.x** (Servlet API 4.0 / `javax.servlet.*`)
- **Git**

## Quickstart: Clone to Running Instance

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/sahana0909/sahanamart.git
   cd sahanamart
   ```

2. **Run Tests & Verify Build**:
   ```bash
   mvn clean verify
   ```

3. **Build the WAR**:
   ```bash
   mvn clean package
   ```
   This generates `target/sahanamart.war`.

4. **Deploy to Apache Tomcat 9**:
   - Copy `target/sahanamart.war` into your Tomcat `webapps/` directory:
     ```bash
     cp target/sahanamart.war /path/to/tomcat/webapps/
     ```
   - Start Tomcat:
     - On Linux/macOS: `./bin/startup.sh`
     - On Windows: `.\bin\startup.bat`

5. **Access the Application**:
   - Storefront: `http://localhost:8080/sahanamart/`
   - Health Check: `http://localhost:8080/sahanamart/api/v1/health`

## Demo Accounts
- **Admin**: `admin@sahanamart.com` / `Admin@123`
- **Seller**: `seller@sahanamart.com` / `Seller@123`
- **Buyer**: `buyer@sahanamart.com` / `Buyer@123`

## Development Standards
- **Conventional Commits**: `feat:`, `fix:`, `test:`, `docs:`
- **PreparedStatement**: All database access must use `PreparedStatement` within try-with-resources.
- **DTO Isolation**: Entity models must never leak password hashes or internal keys to the client.
