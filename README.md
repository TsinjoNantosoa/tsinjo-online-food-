# Backend Online Food

Ce projet est une application backend développée avec Spring Boot pour un système de commande de nourriture en ligne.

## 🚀 Technologies Utilisées

- Java 21
- Spring Boot 3.3.5
- Spring Security
- Spring Data JPA
- MySQL
- JWT (JSON Web Token)
- Lombok
- Hibernate Core 6.5.3

## 📋 Prérequis

- JDK 21 ou supérieur
- Maven 3.6 ou supérieur
- MySQL 8.0 ou supérieur

## 🔧 Installation

1. Clonez le repository :
```bash
git clone [URL_DU_REPOSITORY]
```

2. Naviguez vers le dossier du projet :
```bash
cd backend
```

3. Configurez votre base de données MySQL dans le fichier `application.properties`

4. Compilez le projet :
```bash
mvn clean install
```

5. Lancez l'application :
```bash
mvn spring-boot:run
```

## 🔐 Configuration de la Base de Données

Assurez-vous de configurer les paramètres de connexion à la base de données dans le fichier `src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/[nom_de_votre_base]
spring.datasource.username=[votre_username]
spring.datasource.password=[votre_password]
```

## ��️ Fonctionnalités

### 👤 Gestion des Utilisateurs
- Inscription et connexion des utilisateurs
- Authentification JWT
- Gestion des profils utilisateurs
- Rôles utilisateur (Client, Admin)

### 🏪 Gestion des Restaurants
- Création et gestion des restaurants
- Profils détaillés des restaurants
- Gestion des informations de contact
- Interface d'administration des restaurants

### 🍽️ Gestion des Menus
- Création et gestion des plats
- Catégorisation des plats
- Gestion des ingrédients
- Prix et descriptions détaillées

### 🛒 Système de Panier
- Ajout/Suppression d'articles
- Gestion des quantités
- Calcul automatique des totaux
- Sauvegarde du panier

### 📦 Gestion des Commandes
- Création de commandes
- Suivi des commandes en temps réel
- Historique des commandes
- Interface d'administration des commandes

### 📱 API Endpoints

#### Authentification
- POST /api/auth/register - Inscription
- POST /api/auth/login - Connexion
- POST /api/auth/logout - Déconnexion

#### Restaurants
- GET /api/restaurants - Liste des restaurants
- GET /api/restaurants/{id} - Détails d'un restaurant
- POST /api/admin/restaurants - Création d'un restaurant (Admin)
- PUT /api/admin/restaurants/{id} - Modification d'un restaurant (Admin)

#### Menus
- GET /api/foods - Liste des plats
- GET /api/foods/{id} - Détails d'un plat
- POST /api/admin/foods - Création d'un plat (Admin)
- PUT /api/admin/foods/{id} - Modification d'un plat (Admin)

#### Panier
- GET /api/cart - Voir le panier
- POST /api/cart/add - Ajouter au panier
- PUT /api/cart/update - Mettre à jour le panier
- DELETE /api/cart/remove - Supprimer du panier

#### Commandes
- POST /api/orders - Créer une commande
- GET /api/orders - Liste des commandes
- GET /api/orders/{id} - Détails d'une commande
- PUT /api/admin/orders/{id} - Mettre à jour le statut (Admin)

## 📝 Documentation API

La documentation de l'API sera disponible à l'adresse suivante une fois l'application lancée :
```
http://localhost:8080/swagger-ui.html
```

## 🧪 Tests

Pour exécuter les tests :
```bash
mvn test
```

## 📦 Structure du Projet

```
src/
├── main/
│   ├── java/
│   │   └── com/tsinjo/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── service/
│   │       └── security/
│   └── resources/
│       └── application.properties
└── test/
```

## 🤝 Contribution

Les contributions sont les bienvenues ! N'hésitez pas à :

1. Fork le projet
2. Créer une branche pour votre fonctionnalité (`git checkout -b feature/AmazingFeature`)
3. Commit vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 👥 Auteurs

- TsinjoNantosoa - *Développeur principal*

## 🙏 Remerciements

- Tous les contributeurs 