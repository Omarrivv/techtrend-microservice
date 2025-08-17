# 🔧 Configuración de SonarQube para TechTrend Microservice

## 📋 Prerrequisitos

Para que el pipeline de calidad funcione correctamente, necesitas configurar SonarQube:

### 1. **Configurar SonarQube Cloud (Recomendado)**

1. Ve a [SonarCloud](https://sonarcloud.io)
2. Crea una cuenta gratuita
3. Crea una nueva organización
4. Crea un nuevo proyecto para `techtrend-microservice`

### 2. **Configurar Secrets en GitHub**

En tu repositorio de GitHub, ve a **Settings > Secrets and variables > Actions** y agrega:

```
SONAR_TOKEN = [Tu token de SonarCloud]
SONAR_HOST_URL = https://sonarcloud.io
```

### 3. **Obtener el Token de SonarCloud**

1. En SonarCloud, ve a **Account > Security**
2. Genera un nuevo token
3. Copia el token y agrégalo como `SONAR_TOKEN` en GitHub

## 🚀 Workflows Disponibles

### **Workflow Principal (`ci.yml`)**
- ✅ Tests completos
- ✅ Análisis de seguridad con OWASP
- ✅ Análisis de calidad con SonarQube
- ⚠️ Requiere configuración de SonarQube

### **Workflow Simplificado (`ci-simple.yml`)**
- ✅ Tests completos
- ✅ Análisis de seguridad con OWASP
- ✅ Verificación básica de calidad
- ✅ No requiere SonarQube

## 🔄 Cambiar de Workflow

Si quieres usar el workflow simplificado mientras configuras SonarQube:

1. Renombra `ci.yml` a `ci-full.yml`
2. Renombra `ci-simple.yml` a `ci.yml`
3. Haz commit y push

## 📊 Monitoreo de Calidad

### **Métricas que se analizan:**
- ✅ Cobertura de código
- ✅ Duplicación de código
- ✅ Complejidad ciclomática
- ✅ Vulnerabilidades de seguridad
- ✅ Code smells
- ✅ Bugs potenciales

### **Umbrales de calidad:**
- Cobertura mínima: 80%
- Duplicación máxima: 3%
- Vulnerabilidades críticas: 0
- Bugs críticos: 0

## 🛠️ Solución de Problemas

### **Error: "SonarQube analysis failed"**
1. Verifica que `SONAR_TOKEN` esté configurado
2. Verifica que `SONAR_HOST_URL` sea correcto
3. Asegúrate de que el proyecto existe en SonarCloud

### **Error: "Security scan failed"**
1. Revisa las dependencias vulnerables en `pom.xml`
2. Actualiza las dependencias a versiones seguras
3. Ejecuta `mvn dependency:check` localmente

### **Error: "Tests failed"**
1. Ejecuta `mvn test` localmente
2. Revisa los logs de error
3. Corrige los tests fallidos

## 📈 Mejores Prácticas

1. **Ejecuta tests localmente antes de hacer push**
2. **Revisa el reporte de cobertura regularmente**
3. **Mantén las dependencias actualizadas**
4. **Revisa los reportes de SonarQube semanalmente**

## 🔗 Enlaces Útiles

- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [OWASP Dependency Check](https://owasp.org/www-project-dependency-check/)
- [JaCoCo Coverage](https://www.jacoco.org/jacoco/trunk/doc/)
- [GitHub Actions](https://docs.github.com/en/actions)
