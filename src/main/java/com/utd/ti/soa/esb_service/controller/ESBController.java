package com.utd.ti.soa.esb_service.controller;


import javax.websocket.server.PathParam;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.HttpHeadResponseDecorator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.utd.ti.soa.esb_service.model.Client;
import com.utd.ti.soa.esb_service.model.User;
import com.utd.ti.soa.esb_service.utils.Auth;
import com.utd.ti.soa.esb_service.model.Product;

@RestController
@RequestMapping("/app/esb")
public class ESBController {
    private final WebClient webClient = WebClient.create();
    private final Auth auth = new Auth();

    //Obtener todos los usuarios con autenticación
    @GetMapping("/user")
    public ResponseEntity getUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Token recibido: " + token);

        // Validar token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        // Enviar petición al servicio de usuarios
        String response = webClient.get()
            .uri("http://users.railway.internal:5000/app/users/all")
            .header(HttpHeaders.AUTHORIZATION, token) // Agregar el token en la petición
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    //Crear usuario
    @PostMapping("/user")
    public ResponseEntity createUser (@RequestBody User user,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        System.out.println("Request Body: " + user);
        System.out.println("Token recibido: " + token);

        //Validar token
        if (!auth.validateToken(token)){
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }
        
        //Enviar petición al servicio de usuarios
        String response = webClient.post()
            .uri("http://users.railway.internal:5000/app/users/create")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(user))
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error: " + error.getMessage()))
            .block();
        
        return ResponseEntity.ok(response);
    }

    // Actualizar usuario
    @PatchMapping("/user/update/{id}")
    public ResponseEntity updateUser(@PathVariable String id,
            @RequestBody User user,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Request Body: " + user);
        System.out.println("Token recibido: " + token);
        System.out.println("ID: " + id);

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.patch() // Usamos PATCH en lugar de POST
            .uri("http://users.railway.internal:5000/app/users/update/" + id) // Coincide con la ruta del backend
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(user))
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    // Eliminar usuario
    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity deleteUser(@PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("ID recibido para eliminar: " + id);
        System.out.println("Token recibido: " + token);

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.delete() // Usamos DELETE en lugar de POST
            .uri("http://users.railway.internal:5000/app/users/delete/" + id) // Coincide con la ruta del backend
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    //Login
    @PostMapping("/user/login")
    public ResponseEntity login(@PathVariable String username, @PathVariable String password,
            @RequestBody User user) {
        System.out.println("Request Body: " + username);
        System.out.println("Request Body: " + password);

        String response = webClient.post()
            .uri("http://users.railway.internal:5000/app/users/login")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(user))
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }


    //Crear cliente
    @PostMapping("/client")
    public ResponseEntity createClient(@RequestBody Client client,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Request Body: " + client);
        System.out.println("Token recibido: " + token);

        // Validar token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        // Enviar petición al servicio de clientes
        String clientResponse = webClient.post()
            .uri("http://cliente.railway.internal:7000/app/client/create")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(client))
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error al crear cliente: " + error.getMessage()))
            .block();

        System.out.println("Cliente creado: " + clientResponse);

        // Crear usuario
        User newUser = new User();
        newUser.setUsername(client.getMail());
        newUser.setPhone(client.getPhone());
        newUser.setPassword("123456789a");

        String userResponse = webClient.post()
            .uri("http://users.railway.internal:5000/app/users/create")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(newUser))
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error al crear usuario: " + error.getMessage()))
            .block();

        System.out.println("Usuario creado: " + userResponse);

        // Respuesta combinada
        return ResponseEntity.ok("Cliente y usuario creados exitosamente.");
    }

    //Obtener clientes
    @GetMapping("/client")
    public ResponseEntity getClient(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Token recibido: " + token);

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.get()
            .uri("http://cliente.railway.internal:7000/app/client/all")
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error al obtener clientes: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    // Actualizar cliente
    @PatchMapping("/client/update/{id}")
    public ResponseEntity updateClient(@PathVariable String id,
            @RequestBody Client client,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Request Body: " + client);
        System.out.println("Token recibido: " + token);
        System.out.println("ID: " + id);

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.patch()
            .uri("http://cliente.railway.internal:7000/app/client/update/" + id)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(client))
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error al actualizar cliente: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    // Eliminar cliente
    @DeleteMapping("/client/delete/{id}")
    public ResponseEntity deleteClient(@PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("ID recibido para eliminar: " + id);
        System.out.println("Token recibido: " + token);

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.delete()
            .uri("http://cliente.railway.internal:7000/app/client/delete/" + id)
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error al eliminar cliente: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    //Obtener todos los productos
    @GetMapping("/producto")
    public ResponseEntity getProduct(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Token recibido: " + token);
    
        String response = webClient.get()
            .uri("http://productos.railway.internal:5000/app/products/all")
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error: " + error.getMessage()))
            .block();
    
        return ResponseEntity.ok(response);
    }

}
