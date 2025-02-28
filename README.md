[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/aSaOP-dD)
[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=18202521)
# Webserver Project Spring 2025

## Team Members

1. Ulices Gonzalez
2. Kenny Wong

## Notes

- What was completed?
  Get Requests are partially working, page is displayed but need to fix image
  200 Responses work
  404 Responses work
  Head Requests work completely
  200 Responses work
  404 Responses work
  Put Requests work completely
  201 Responses work
  500 Responses worked when debugging (files were not being created in supplied directory)
  Delete Requests work completely
  204 Responses work
  500 Responsed worked when debugging (same issue as put, file could not be found but gave response)
  404 Responses work
- What was not completed? <---
  Get Requests are partially working, page is displayed but need to fix image
  Support Simple Authentication
  Your server must be able to handle the 401/403 authentication workflow.  The presence of a .password file in the directory of the requested resource will determine permission to access a given resource. 
  If a .password file exists in the directory where the server finds the requested file, and if no Authorization header is present, the server must respond to the client with a valid HTTP unauthorized response (401). This response must contain the header WWW-Authenticate, with the value Basic realm=“667 Server”.
  If a .password file exists in the directory that the server finds the requested file, and if an Authorization header is present, the server must check that the .password file contains the username and password provided in the header and:
  If the .passwords file does not contain the username and password, the server must respond with a valid HTTP forbidden response. (403)
  If the .passwords file contains the username and password, the server must respond as it would normally.
