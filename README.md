# 🌐 UserWeb Microservices Platform

Ce projet est une architecture microservices complète, sécurisée et réactive, conçue pour démontrer la maîtrise des technologies de pointe en Java et JavaScript.

## 🚀 Fonctionnalités Clés (Barème)

### 🧩 1. Architecture Microservices Avancée
- **Technologie Réactive** : Utilisation de **Spring WebFlux** pour une performance non-bloquante.
- **Base de Données Moderne** : Migration vers **MySQL** avec le driver réactif **R2DBC**.
- **Discovery Service** : Registry via **Netflix Eureka**.
- **API Gateway** : Point d'entrée unique avec **Spring Cloud Gateway**.

### 🔐 2. Sécurité & Authentification
- **Keycloak** : Authentification centralisée (IAM).
- **JWT (JSON Web Tokens)** : Sécurisation des échanges entre services.
- **RBAC (Role-Based Access Control)** : Gestion fine des accès (ADMIN, USER, VENDEUR).
- **Thème Personnalisé** : Interface Keycloak adaptée au design du projet.

### 📡 3. Communication Inter-services
- **Communication Synchrone** : Utilisation de **OpenFeign** pour les appels directs.
- **Communication Asynchrone (RabbitMQ)** : 4 scénarios d'événements (Création, Suppression...) pour un couplage faible.

### 📽️ 4. Frontend Angular
- Interface moderne et réactive.
- Intégration complète du flux d'authentification Keycloak.
- Gestion CRUD complète des utilisateurs.

### 🛠️ 5. Industrialisation (Valeurs Ajoutées)
- **Monitoring** : Prometheus et Actuator configurés.
- **Documentation** : Swagger centralisé au niveau de la Gateway.
- **Containerisation** : Fichiers **Dockerfile** et **Docker-Compose**.
- **Orchestration** : Manifestes **Kubernetes (K8s)** inclus.
- **CI/CD** : Pipeline **GitHub Actions** pour le build automatisé.

---

## 🏗️ Structure du Projet

```text
├── discovery-service      # Eureka Server (Port 8762)
├── gateway-service        # API Gateway + Swagger Centralisé (Port 8887)
├── user-service           # MS Réactif (WebFlux + MySQL R2DBC) (Port 8082)
├── webuser                # Frontend Angular (Port 4200)
├── k8s                    # Manifestes Kubernetes
└── keycloak-theme         # Personnalisation de l'auth
```

---

## 🚀 Installation et Lancement

### 1. Prérequis
- Java 17+
- MySQL (base : `userweb_db`)
- RabbitMQ
- Keycloak (Port 8080)

### 2. Execution du Backend
Lancer les services dans l'ordre suivant :
1. `discovery-service`
2. `gateway-service`
3. `user-service`

### 3. Execution du Frontend
```bash
cd webuser
npm install
npm start
```

---

## 📄 Documentation API
Accédez à la documentation Swagger centralisée de tous les services via la Gateway :
👉 [http://localhost:8887/swagger-ui.html](http://localhost:8887/swagger-ui.html)

---

## 👨‍💻 Démonstration (Scénarios de test)
1. **Sécurité** : Tester l'accès à `/users/api/users` sans token (401) puis avec token (200).
2. **Asynchrone** : Créer un utilisateur et vérifier que le message arrive dans la console RabbitMQ.
3. **Réactivité** : Montrer les types `Flux<User>` et `Mono<User>` dans le code du contrôleur.
