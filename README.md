# 🛒 Projet Microservices - Système de Gestion de Commandes

> Architecture microservices Spring Boot avec Keycloak, Eureka, API Gateway et Docker.

---

## 📐 Architecture Globale

```
                        ┌─────────────────┐
  Angular Frontend  ──▶ │   API Gateway   │ :8085
                        └────────┬────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                  │
   ┌──────────▼──────────┐  ┌────▼────┐  ┌─────────▼────────┐
   │  commande-service   │  │ Eureka  │  │  config-service  │
   │       :8087         │  │  :8761  │  │      :8888       │
   └──────────┬──────────┘  └─────────┘  └──────────────────┘
              │
   ┌──────────▼──────────┐  ┌──────────────────┐
   │    MySQL DB         │  │    Keycloak       │
   │      :3306          │  │      :8080        │
   └─────────────────────┘  └──────────────────┘
```

---

## 🧩 Microservices

| Service | Port | Rôle |
|---|---|---|
| `eureka-server` | 8761 | Service Discovery (annuaire) |
| `config-service` | 8888 | Centralisation des configurations |
| `api-gateway` | 8085 | Point d'entrée unique (reverse proxy) |
| `commande-service` | 8087 | Gestion métier des commandes |

---

## 🚀 Technologies Utilisées

- **Java 17** + **Spring Boot 3.2.3**
- **Spring Cloud** (Eureka, Config Server, Gateway)
- **Spring Security + OAuth2** (JWT via Keycloak)
- **Spring Data JPA** + **MySQL 8**
- **Lombok** (réduction du boilerplate)
- **Docker** + **Docker Compose**
- **Keycloak** (Gestion des identités et des rôles)

---

## 📦 Modèle de données : `Commande`

```java
public class Commande {
    Long id;
    String prenom, nom;
    String adresse, telephone, adresseEmail;
    LocalDateTime dateCommande;
    StatutCommande statutCommande;   // EN_ATTENTE, CONFIRMEE, EN_PREPARATION,
                                     // EXPEDIEE, LIVREE, ANNULEE, REMBOURSEE
    ModePaiement modePaiement;       // CARTE, PAYPAL, ESPECES, VIREMENT
}
```

---

## 📡 API REST - Endpoints disponibles

### Commandes (Base URL : `/api/commandes`)

| Méthode | URL | Description | Rôle requis |
|---|---|---|---|
| `GET` | `/api/commandes` | Lister toutes les commandes | Authentifié |
| `GET` | `/api/commandes/{id}` | Obtenir une commande par ID | Authentifié |
| `GET` | `/api/commandes/search?query=` | Rechercher par nom/prénom | Authentifié |
| `GET` | `/api/commandes/search/nom?nom=` | Rechercher par nom | Authentifié |
| `GET` | `/api/commandes/search/prenom?prenom=` | Rechercher par prénom | Authentifié |
| `POST` | `/api/commandes` | Créer une commande | `user` |
| `PUT` | `/api/commandes/{id}` | Modifier une commande | `admin` |
| `PUT` | `/api/commandes/{id}/statut?nouveauStatut=` | Changer le statut | `admin` |
| `PUT` | `/api/commandes/{id}/cancel` | Annuler une commande | `admin` |
| `DELETE` | `/api/commandes/{id}` | Supprimer une commande | `admin` |

### 📊 Dashboard Statistiques Admin

| Méthode | URL | Description | Rôle requis |
|---|---|---|---|
| `GET` | `/api/commandes/stats` | Tableau de bord statistiques complet | Authentifié |

#### Exemple de réponse `/api/commandes/stats`
```json
{
  "totalCommandes": 42,
  "commandesParStatut": {
    "EN_ATTENTE": 10,
    "CONFIRMEE": 8,
    "EN_PREPARATION": 5,
    "EXPEDIEE": 12,
    "LIVREE": 5,
    "ANNULEE": 2,
    "REMBOURSEE": 0
  },
  "commandesAujourdhui": 3,
  "commandesCetteSemaine": 15,
  "commandesCeMois": 42,
  "commandesParModePaiement": {
    "CARTE": 20,
    "PAYPAL": 12,
    "ESPECES": 7,
    "VIREMENT": 3
  },
  "modePaiementPlusUtilise": "CARTE"
}
```

---

## 🔐 Sécurité avec Keycloak

Le projet utilise **Keycloak** comme serveur d'authentification OAuth2/OIDC.

### Configuration
- **Realm** : `eya-Realm`
- **Client** : `Eya-client` (Public, Direct Access Grants activé)
- **Rôles** : `user`, `admin`

### Obtenir un token (Postman)
```http
POST http://localhost:8080/realms/eya-Realm/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password
&client_id=Eya-client
&username=votre_user
&password=votre_mot_de_passe
```

---

## 🐳 Lancer avec Docker Compose

### Prérequis
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installé et démarré

### Démarrage
```bash
docker-compose up -d
```

### Ordre de démarrage automatique
1. `mysqldb` + `keycloak`
2. `eureka-server`
3. `config-service`
4. `commande-service` + `api-gateway`

### Arrêt
```bash
docker-compose down
```

---

## 🖥️ Lancer en local (sans Docker)

### Prérequis
- Java 17+
- Maven 3.8+
- MySQL démarré sur le port 3306
- Keycloak démarré sur le port 8080

### Ordre de démarrage
```bash
# 1. Eureka Server
cd eureka-server && mvn spring-boot:run

# 2. Config Service
cd config-service && mvn spring-boot:run

# 3. Commande Service
cd commande-service && mvn spring-boot:run

# 4. API Gateway
cd api-gateway && mvn spring-boot:run
```

---

## 🖼️ Images Docker Hub

| Service | Image |
|---|---|
| Eureka Server | `eyaeyahermi/eureka-server:latest` |
| Config Service | `eyaeyahermi/config-service:latest` |
| Commande Service | `eyaeyahermi/commande-service:latest` |
| API Gateway | `eyaeyahermi/api-gateway:latest` |

---

## 👩‍💻 Auteurs

- **Eya Hermi** — Architecture microservices, Commande Service, Sécurité Keycloak, Docker

---

## 📄 Licence

Ce projet est développé dans le cadre d'un projet universitaire de systèmes distribués.
