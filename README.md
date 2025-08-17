# 🚀 TechTrend - Plataforma de E-commerce

## 📋 Descripción

**TechTrend** es una plataforma de e-commerce moderna construida con arquitectura de microservicios usando Spring Boot 3.2.0. La aplicación incluye funcionalidades completas de autenticación, catálogo de productos, carrito de compras y procesamiento de pagos.

## 🏗️ Arquitectura de Microservicios

### 🔐 Microservicio de Autenticación
- **Funcionalidades**: Registro de usuarios, login, validación JWT, bloqueo de cuentas
- **Entidad**: `User` con roles CLIENT/ADMIN
- **Endpoints**: `/auth/register`, `/auth/login`, `/auth/validate`, `/auth/user-info`

### 📦 Microservicio de Catálogo
- **Funcionalidades**: Gestión de productos, control de stock, búsquedas, estadísticas
- **Entidad**: `Product` con gestión de inventario
- **Endpoints**: `/catalog/products`, `/catalog/products/{id}`, `/catalog/search`

### 🛒 Microservicio de Carrito
- **Funcionalidades**: Gestión de carrito, agregar/eliminar items, cálculos de totales
- **Entidad**: `CartItem` con gestión de cantidades
- **Endpoints**: `/cart/items`, `/cart/total`, `/cart/check-product`

### 💳 Microservicio de Pagos
- **Funcionalidades**: Procesamiento de pagos, validación de montos, simulación
- **Entidad**: `Payment` con estados PENDING/COMPLETED/FAILED
- **Endpoints**: `/payments/process`, `/payments/{id}/status`

## 🛠️ Tecnologías Utilizadas

### Backend
- **Spring Boot 3.2.0** - Framework principal
- **Spring Data JPA** - Persistencia de datos
- **Spring Security** - Seguridad y autenticación
- **JWT** - Tokens de autenticación
- **BCrypt** - Encriptación de contraseñas
- **H2 Database** - Base de datos en memoria (desarrollo)
- **PostgreSQL** - Base de datos de producción

### Testing
- **JUnit 5** - Framework de pruebas
- **Mockito** - Mocking de dependencias
- **JaCoCo** - Cobertura de código

### Herramientas
- **Maven** - Gestión de dependencias
- **Lombok** - Reducción de código boilerplate
- **Jackson** - Serialización JSON

## 📁 Estructura del Proyecto

```
techtrend-microservice/
├── src/
│   ├── main/
│   │   ├── java/com/techtrend/
│   │   │   ├── TechTrendApplication.java
│   │   │   ├── authentication/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   └── dto/
│   │   │   ├── catalog/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   └── dto/
│   │   │   ├── cart/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   └── dto/
│   │   │   ├── payment/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   └── dto/
│   │   │   └── common/
│   │   │       └── exception/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-test.yml
│   └── test/
│       └── java/com/techtrend/
│           ├── authentication/service/
│           ├── catalog/service/
│           ├── cart/service/
│           └── payment/service/
├── pom.xml
└── README.md
```

## 🚀 Instalación y Configuración

### Prerrequisitos
- **Java 17** o superior
- **Maven 3.6** o superior
- **Git**

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone <url-del-repositorio>
   cd techtrend-microservice
   ```

2. **Compilar el proyecto**
   ```bash
   mvn clean compile
   ```

3. **Ejecutar las pruebas**
   ```bash
   mvn test
   ```

4. **Ejecutar la aplicación**
   ```bash
   mvn spring-boot:run
   ```

## 🧪 Pruebas Unitarias

### Ejecutar Todas las Pruebas
```bash
mvn test
```

### Ver Cobertura de Código
```bash
mvn test jacoco:report
```

### Resultados de Pruebas
- **Total de pruebas**: 72
- **Autenticación**: 16 pruebas
- **Catálogo**: 20 pruebas  
- **Carrito**: 18 pruebas
- **Pagos**: 18 pruebas

### Estructura de Pruebas
Los tests están organizados de manera simple y legible:

```java
@Test
@DisplayName("✅ Registro exitoso con datos válidos")
void registroExitoso() {
    // PREPARAR
    // Configurar datos de prueba
    
    // EJECUTAR
    // Llamar al método que se está probando
    
    // VERIFICAR
    // Comprobar que el resultado es correcto
}
```

## 🌐 Endpoints de la API

### 🔐 Autenticación

#### Registrar Usuario
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "usuario@techtrend.com",
  "password": "password123",
  "role": "CLIENT"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "usuario@techtrend.com",
  "password": "password123"
}
```

#### Validar Token
```http
GET /api/auth/validate
Authorization: Bearer <token>
```

### 📦 Catálogo

#### Listar Productos
```http
GET /api/catalog/products?page=0&size=20
```

#### Obtener Producto por ID
```http
GET /api/catalog/products/1
```

#### Buscar Productos
```http
GET /api/catalog/products/search?name=laptop
```

#### Verificar Stock
```http
GET /api/catalog/products/1/stock?quantity=5
```

### 🛒 Carrito

#### Agregar Producto al Carrito
```http
POST /api/cart/items
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

#### Obtener Items del Carrito
```http
GET /api/cart/items
```

#### Actualizar Cantidad
```http
PUT /api/cart/items/1
Content-Type: application/json

{
  "quantity": 3
}
```

#### Calcular Total
```http
GET /api/cart/total
```

### 💳 Pagos

#### Procesar Pago
```http
POST /api/payments/process
Content-Type: application/json

{
  "orderId": "ORD-001",
  "amount": 1500.00,
  "paymentMethod": "CREDIT_CARD"
}
```

#### Obtener Estado del Pago
```http
GET /api/payments/1/status
```

## 🔧 Configuración

### Archivo de Configuración Principal (`application.yml`)

```yaml
spring:
  application:
    name: techtrend-microservice
  
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  
  h2:
    console:
      enabled: true
      path: /h2-console

server:
  port: 8080
  servlet:
    context-path: /api

# Configuraciones personalizadas
techtrend:
  auth:
    max-login-attempts: 3
    lockout-duration: 300000
  catalog:
    default-page-size: 20
  payment:
    max-amount: 100000.00
```

### Configuración de Pruebas (`application-test.yml`)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
  h2:
    console:
      enabled: false
  jpa:
    show-sql: false

logging:
  level:
    com.techtrend: WARN
```

## 🗄️ Base de Datos

### H2 Console (Desarrollo)
- **URL**: http://localhost:8080/api/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Usuario**: `sa`
- **Contraseña**: (vacío)

### Entidades Principales

#### User
```java
@Entity
public class User {
    private Long id;
    private String email;
    private String password;
    private UserRole role;
    private boolean isActive;
    private int loginAttempts;
    private LocalDateTime lockedUntil;
}
```

#### Product
```java
@Entity
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private int quantity;
    private String category;
    private String brand;
    private String sku;
    private boolean isActive;
}
```

#### CartItem
```java
@Entity
public class CartItem {
    private Long id;
    private Long userId;
    private Long productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private boolean isActive;
}
```

#### Payment
```java
@Entity
public class Payment {
    private Long id;
    private String orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String paymentMethod;
    private String transactionId;
    private Long userId;
}
```

## 🔒 Seguridad

### Autenticación JWT
- **Algoritmo**: HS256
- **Expiración**: 24 horas
- **Claims**: email, role, exp, iat

### Encriptación de Contraseñas
- **Algoritmo**: BCrypt
- **Strength**: 12 (configurable)

### Bloqueo de Cuentas
- **Máximo intentos**: 3
- **Duración del bloqueo**: 5 minutos

## 📊 Características Destacadas

### ✅ Funcionalidades Implementadas
- [x] Registro y autenticación de usuarios
- [x] Gestión de catálogo de productos
- [x] Carrito de compras completo
- [x] Procesamiento de pagos
- [x] Validaciones robustas
- [x] Manejo global de excepciones
- [x] Logging detallado
- [x] Pruebas unitarias completas
- [x] Cobertura de código > 90%

### 🎯 Buenas Prácticas Aplicadas
- **Clean Architecture** con separación de capas
- **SOLID Principles** implementados
- **DTOs** para transferencia de datos
- **Validaciones** con Bean Validation
- **Logging estructurado** para debugging
- **Tests descriptivos** y legibles
- **Documentación completa** de APIs

## 🐛 Solución de Problemas

### Error: Cannot load driver class: org.h2.Driver
**Solución**: El driver H2 ya está incluido en las dependencias. Si persiste el error:
1. Limpiar y recompilar: `mvn clean compile`
2. Verificar que H2 no tenga scope `test` en `pom.xml`

### Error: Puerto 8080 en uso
**Solución**: Cambiar el puerto en `application.yml`:
```yaml
server:
  port: 8081
```

### Error: Base de datos no encontrada
**Solución**: Verificar la configuración de la base de datos en `application.yml`

## 📈 Métricas y Monitoreo

### Health Checks
- **URL**: http://localhost:8080/api/actuator/health
- **Estado**: UP/DOWN con detalles de componentes

### Logs
- **Nivel**: INFO para operaciones normales
- **Nivel**: WARN para situaciones de atención
- **Nivel**: ERROR para errores críticos

## 🤝 Contribución

### Guías de Contribución
1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

### Estándares de Código
- **Java**: Java 17
- **Formato**: Usar el formateador de IDE
- **Tests**: Mantener cobertura > 90%
- **Documentación**: Comentar métodos públicos

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 👥 Equipo

- **Desarrollador Principal**: TechTrend Team
- **Arquitecto**: TechTrend Team
- **QA**: TechTrend Team

## 📞 Soporte

Para soporte técnico o preguntas:
- **Email**: soporte@techtrend.com
- **Documentación**: [Wiki del proyecto]
- **Issues**: [GitHub Issues]

---

## 🎉 ¡TechTrend está listo para usar!

La aplicación se ejecuta en: **http://localhost:8080/api**

¡Disfruta explorando la plataforma de e-commerce más moderna! 🚀
# techtrend-microservice
