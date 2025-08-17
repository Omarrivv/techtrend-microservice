#!/bin/bash

# Script de configuración de GitFlow para TechTrend
# Este script configura GitFlow y todas las herramientas necesarias

echo "🚀 Configurando GitFlow para TechTrend..."

# Verificar que Git esté instalado
if ! command -v git &> /dev/null; then
    echo "❌ Git no está instalado. Por favor, instala Git primero."
    exit 1
fi

# Verificar que GitFlow esté instalado
if ! command -v git-flow &> /dev/null; then
    echo "⚠️  GitFlow no está instalado. Instalando..."
    
    # Para Ubuntu/Debian
    if command -v apt-get &> /dev/null; then
        sudo apt-get update
        sudo apt-get install -y git-flow
    # Para macOS
    elif command -v brew &> /dev/null; then
        brew install git-flow
    # Para CentOS/RHEL
    elif command -v yum &> /dev/null; then
        sudo yum install -y git-flow
    else
        echo "❌ No se pudo instalar GitFlow automáticamente."
        echo "Por favor, instala GitFlow manualmente:"
        echo "  Ubuntu/Debian: sudo apt-get install git-flow"
        echo "  macOS: brew install git-flow"
        echo "  CentOS/RHEL: sudo yum install git-flow"
        exit 1
    fi
fi

echo "✅ GitFlow instalado correctamente"

# Inicializar GitFlow
echo "🔧 Inicializando GitFlow..."
git flow init -d

# Configurar commit template
echo "📝 Configurando commit template..."
git config commit.template .gitmessage

# Hacer el hook ejecutable
echo "🔗 Configurando pre-commit hook..."
chmod +x .git/hooks/pre-commit

# Configurar alias útiles
echo "⚡ Configurando alias de Git..."
git config alias.feature "flow feature"
git config alias.release "flow release"
git config alias.hotfix "flow hotfix"
git config alias.support "flow support"

# Configurar configuración de usuario (si no está configurada)
if [ -z "$(git config user.name)" ]; then
    echo "👤 Configurando usuario de Git..."
    read -p "Ingresa tu nombre: " username
    read -p "Ingresa tu email: " email
    git config user.name "$username"
    git config user.email "$email"
fi

# Configurar configuración de línea de comandos
echo "🔧 Configurando configuración de Git..."
git config core.autocrlf input
git config core.eol lf
git config pull.rebase false

echo "✅ Configuración de GitFlow completada exitosamente!"
echo ""
echo "📋 Comandos útiles:"
echo "  git flow feature start <nombre>     - Crear nueva feature"
echo "  git flow feature finish <nombre>    - Finalizar feature"
echo "  git flow release start <version>    - Crear release"
echo "  git flow release finish <version>   - Finalizar release"
echo "  git flow hotfix start <nombre>      - Crear hotfix"
echo "  git flow hotfix finish <nombre>     - Finalizar hotfix"
echo ""
echo "🎯 Próximos pasos:"
echo "  1. Crear tu primera feature: git flow feature start mi-primera-feature"
echo "  2. Hacer cambios y commits siguiendo las convenciones"
echo "  3. Finalizar la feature: git flow feature finish mi-primera-feature"
echo ""
echo "📚 Para más información, consulta el README.md"
