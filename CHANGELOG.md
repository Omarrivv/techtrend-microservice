# 📋 Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Configuración completa de GitFlow
- Plantillas para Pull Requests e Issues
- Pipeline de CI/CD con GitHub Actions
- Scripts de configuración automática
- Documentación completa de buenas prácticas
- Hooks de pre-commit
- Configuración de Swagger/OpenAPI

### Changed
- Mejorada la estructura de pruebas unitarias
- Refactorizado el manejo de excepciones
- Actualizada la documentación del README

### Fixed
- Corrección de errores en tests unitarios
- Solución de problemas de configuración de Spring Security
- Corrección de acceso a H2 Console y Swagger UI

## [1.0.0] - 2024-01-17

### Added
- 🚀 **TechTrend E-commerce Platform** - Plataforma completa de e-commerce
- 🔐 **Microservicio de Autenticación** - Registro, login, JWT, bloqueo de cuentas
- 📦 **Microservicio de Catálogo** - Gestión de productos, stock, búsquedas
- 🛒 **Microservicio de Carrito** - Gestión de carrito, cálculos de totales
- 💳 **Microservicio de Pagos** - Procesamiento de pagos, validaciones
- 🧪 **Pruebas Unitarias Completas** - 72 pruebas con cobertura > 90%
- 📚 **Documentación de API** - Swagger/OpenAPI integrado
- 🔒 **Seguridad Robusta** - Spring Security, BCrypt, JWT
- 🗄️ **Base de Datos** - H2 para desarrollo, PostgreSQL para producción
- 🎯 **Buenas Prácticas** - Clean Architecture, SOLID Principles

### Technical Details
- **Spring Boot 3.2.0** - Framework principal
- **Java 17** - Versión de Java
- **Maven** - Gestión de dependencias
- **JUnit 5 + Mockito** - Testing framework
- **JaCoCo** - Cobertura de código
- **Lombok** - Reducción de boilerplate
- **Jackson** - Serialización JSON

### API Endpoints
- **Autenticación**: `/api/auth/**`
- **Catálogo**: `/api/catalog/**`
- **Carrito**: `/api/cart/**`
- **Pagos**: `/api/payment/**`
- **Health Check**: `/health`
- **Swagger UI**: `/swagger-ui.html`
- **H2 Console**: `/h2-console`

---

## 📝 Tipos de Cambios

- **Added** para nuevas características
- **Changed** para cambios en funcionalidades existentes
- **Deprecated** para características que serán removidas
- **Removed** para características removidas
- **Fixed** para correcciones de bugs
- **Security** para vulnerabilidades de seguridad

## 🔗 Enlaces Útiles

- [README.md](./README.md) - Documentación principal
- [CONTRIBUTING.md](./CONTRIBUTING.md) - Guías de contribución
- [SECURITY.md](./SECURITY.md) - Política de seguridad
- [LICENSE](./LICENSE) - Licencia del proyecto

---

**Nota**: Este changelog se mantiene actualizado con cada release siguiendo las convenciones de GitFlow.
