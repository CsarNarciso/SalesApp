# Microservices Sales Application.

![Old Final Chat Web System Design](https://github.com/CsarNarciso/Assets/blob/main/ChatWeb/OLD%20Final%20System%20Design.png)

## Running The Application

#### Prerequsistes

1. **Clone the repository using Git (or download directly)**
   ```bash 
    git clone https://github.com/CsarNarciso/SalesApp.git 
    ```
2. **Navigate to the project root directory**
   ```bash
   cd SalesApp/
   ```
3. **Use Docker Compose to setup the whole project**
   ```bash 
   docker compose up -d 
   ```

## Using The Application

### Use Postman to test the services APIs

Login to Postman Web and download the Postman collection in JSON format, then just import it in Postman Desktop.

https://marco4-4703.postman.co/workspace/My-Workspace~081a7c47-d6b5-4834-808e-9ee9a9a06a0b/collection/39547010-10844b0f-8e9d-43cb-bc6e-a5accc797557?action=share&source=copy-link&creator=39547010

Or just use directly the collection .postman.json file already integrated in the root path of this repository.

### Or use the client!

The repository already includes a simple react client to consume some services, and it setups automatically along the rest of the project application (the backend services), just with the same docker compose command seen in previous section.

Access the client in your browser through:

http://localhost:3000
