[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/aSaOP-dD)
[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=18202521)
# Webserver Project Spring 2025

## Team Members

1. Ulices Gonzalez
2. Kenny Wong

## Notes

- What was completed?
  
- What was not completed?

### Basic Request Handling
- #### Your server must handle invalid requests (requests that do not conform to the protocol). 
-   #### If an invalid request is received, your server must respond with a valid HTTP response (400).
- #### Your server must handle GET requests for a static document. The server will attempt to find the requested file and:
-   #### If the file exists, the server must respond to the client with a valid HTTP success response (200)
-   #### If the file does not exist, the server must respond to the client with a valid HTTP not found response (404)
- #### Your server must handle HEAD requests for a static document. The server will attempt to find the requested file and:
-   #### If the file exists, the server must respond to the client with a valid HTTP success response that does not include the file in the body of the response (200)
-   #### If the file does not exist, the server must respond to the client with a valid HTTP not found response (404)
- #### Your server must handle PUT requests. The server will create or overwrite a file at the requested path and:
-   #### If the file is successfully created, the server must respond with a valid HTTP created response (201)
-   #### If the file is not successfully created, the server must respond with a valid HTTP error response (500)
- #### Your server must handle DELETE requests. The server will attempt to find the requested file and:
-   #### If the file exists and is successfully deleted, the server must respond with a valid HTTP no content response (204)
-   #### If the file exists and is not successfully deleted, the server must respond with a valid HTTP error response (500)
-   #### If the file does not exist, the server must respond to the client with a valid HTTP not found response (404)
### Support Simple Authentication
- #### Your server must be able to handle the 401/403 authentication workflow. The presence of a .password file in the directory of the requested resource will determine permission to access a given resource. 
-   #### If a .password file exists in the directory where the server finds the requested file, and if no Authorization header is present, the server must respond to the client with a valid HTTP unauthorized response (401). This response must contain the header WWW-Authenticate, with the value Basic realm=“667 Server”.
-   #### If a .password file exists in the directory that the server finds the requested file, and if an Authorization header is present, the server must check that the .password file contains the username and password provided in the header and:
-   #### If the .passwords file does not contain the username and password, the server must respond with a valid HTTP forbidden response. (403)
-   #### If the .passwords file contains the username and password, the server must respond as it would normally.
