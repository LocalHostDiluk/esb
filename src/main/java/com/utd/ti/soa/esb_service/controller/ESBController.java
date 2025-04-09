package com.utd.ti.soa.esb_service.controller;


import javax.websocket.server.PathParam;

import java.util.Map;
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
        

        // Validar token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        // Enviar petición al servicio de usuarios
        String response = webClient.get()
            .uri("https://users-production-b7a8.up.railway.app/app/users/all")
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
        

        //Validar token
        if (!auth.validateToken(token)){
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }
        
        //Enviar petición al servicio de usuarios
        String response = webClient.post()
            .uri("https://users-production-b7a8.up.railway.app/app/users/create")
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
        
        System.out.println("ID: " + id);

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.patch() // Usamos PATCH en lugar de POST
            .uri("https://users-production-b7a8.up.railway.app/app/users/update/" + id) // Coincide con la ruta del backend
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

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.delete() // Usamos DELETE en lugar de POST
            .uri("https://users-production-b7a8.up.railway.app/app/users/delete/" + id) // Coincide con la ruta del backend
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    //Login
    @PostMapping("/user/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
    
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
    
        String response = webClient.post()
            .uri("https://users-production-b7a8.up.railway.app/app/users/login")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(credentials))
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error: " + error.getMessage()))
            .block();
    
        return ResponseEntity.ok(response);
    }

    //Recuperar contraseña
    @PostMapping("/user/recuperar")
    public ResponseEntity<String> recuperar(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
    
        System.out.println("Email: " + email);
    
        String response = webClient.post()
            .uri("https://users-production-b7a8.up.railway.app/app/users/recover")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(credentials))
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
        

        // Validar token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        // Enviar petición al servicio de clientes
        String clientResponse = webClient.post()
            .uri("https://cliente-production-841a.up.railway.app/app/client/create")
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
            .uri("https://users-production-b7a8.up.railway.app/app/users/create")
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
        

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.get()
            .uri("https://cliente-production-841a.up.railway.app/app/client/all")
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
        
        System.out.println("ID: " + id);

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.patch()
            .uri("https://cliente-production-841a.up.railway.app/app/client/update/" + id)
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
        

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.delete()
            .uri("https://cliente-production-841a.up.railway.app/app/client/delete/" + id)
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error al eliminar cliente: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }



    //Obtener producto por ID
    @GetMapping("/producto/{id}")
    public ResponseEntity getProductById(@PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("ID recibido: " + id);
        

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.get()
            .uri("http://productos.railway.internal:5000/app/products/producto/" + id)
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error al obtener producto: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    //Obtener todos los productos
    @GetMapping("/producto")
    public ResponseEntity getProduct(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
    
        String response = webClient.get()
            .uri("http://productos.railway.internal:5000/app/products/all")
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error: " + error.getMessage()))
            .block();
    
        return ResponseEntity.ok(response);
    }

    //Crear producto
    @PostMapping("/producto")
    public ResponseEntity createProduct(@RequestBody Product product,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Request Body: " + product);
        

        // Validar token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        // Enviar petición al servicio de productos
        String response = webClient.post()
            .uri("http://productos.railway.internal:5000/app/products/create")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(product))
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error al crear producto: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    //Actualizar producto
    @PatchMapping("/producto/update/{id}")
    public ResponseEntity updateProduct(@PathVariable String id,
            @RequestBody Product product,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Request Body: " + product);
        
        System.out.println("ID: " + id);

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.patch()
            .uri("http://productos.railway.internal:5000/app/products/update/" + id)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(product))
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error al actualizar producto: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    //Eliminar producto
    @DeleteMapping("/producto/delete/{id}")
    public ResponseEntity deleteProduct(@PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("ID recibido para eliminar: " + id);
        

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.delete()
            .uri("http://productos.railway.internal:5000/app/products/delete/" + id)
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error al eliminar producto: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }



    //Crear pedido
    @PostMapping("/pedido")
    public ResponseEntity createPedido(@RequestBody Map<String, Object> pedido,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Request Body: " + pedido);
        

        // Validar token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        // Enviar petición al servicio de pedidos
        String response = webClient.post()
            .uri("https://pedidos-production-2523.up.railway.app/app/pedidos/crear")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(pedido))
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error al crear pedido: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    //Obtener Pedido por ID
    @GetMapping("/pedido/{id}")
    public ResponseEntity getPedido(@PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("ID recibido: " + id);
        

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.get()
            .uri("https://pedidos-production-2523.up.railway.app/app/pedidos/pedido/" + id)
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error al obtener pedido: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    //Obtener todos los pedidos
    @GetMapping("/pedido")
    public ResponseEntity getAllPedidos(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.get()
            .uri("https://pedidos-production-2523.up.railway.app/app/pedidos/all")
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    //Cancelar pedido
    @DeleteMapping("/pedido/cancelar/{id}")
    public ResponseEntity cancelPedido(@PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("ID recibido para cancelar: " + id);
        

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.delete()
            .uri("https://pedidos-production-2523.up.railway.app/app/pedidos/cancelar/" + id)
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error al cancelar pedido: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    //Actualizar estado pedido
    @PatchMapping("/pedido/actualizar/{id}")
    public ResponseEntity updatePedido(@PathVariable String id,
            @RequestBody Map<String, Object> pedido,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Request Body: " + pedido);
        
        System.out.println("ID: " + id);

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.patch()
            .uri("https://pedidos-production-2523.up.railway.app/app/pedidos/actualizar/" + id)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(pedido))
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error al actualizar pedido: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    //Obtener categorias
    @GettMapping("/categoria")
    public ResponseEntity createCategory(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        // Validar token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        // Enviar petición al servicio de categorías
        String response = webClient.post()
            .uri("https://categories-production-195b.up.railway.app/app/categories")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(category))
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error al crear categoría: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    //Eliiminar categoría
    @DeleteMapping("/categoria/delete/{id}")
    public ResponseEntity deleteCategory(@PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("ID recibido para eliminar: " + id);
        

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        String response = webClient.delete()
            .uri("https://categories-production-195b.up.railway.app/app/categories/" + id)
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error al eliminar categoría: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }

    //Crear categoría
    @PostMapping("/categoria")
    public ResponseEntity createCategory(@RequestBody Categoria category,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Request Body: " + category);
        

        // Validar token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401)
                .body("Token inválido o expirado");
        }

        // Enviar petición al servicio de categorías
        String response = webClient.post()
            .uri("https://categories-production-195b.up.railway.app/app/categories/create")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(category))
            .retrieve()
            .bodyToMono(String.class)
            .doOnError(error -> System.out.println("Error al crear categoría: " + error.getMessage()))
            .block();

        return ResponseEntity.ok(response);
    }
    
}
