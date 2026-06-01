# Snaphan: Token-Driven AI Image Generator

Snaphan is a production-ready Android application powered by modern generative AI workflows and a scalable backend infrastructure. The platform features a dynamic, token-based credit economy allowing users to generate high-quality images via serverless execution patterns, backed by secure mobile monetization channels.

---

## Technical Architecture & Backend Infrastructure

The core engine of Snaphan is designed around decoupled, state-controlled cloud triggers rather than processing heavy AI operations on the client side.

* **Firebase Genkit & Serverless Execution:** Image generation orchestration is managed off-device via custom Node.js environments utilizing Firebase Genkit. This guarantees unified prompt manipulation, safety filtering, and model abstraction.
* **Firebase Cloud Functions (v2):** Secure, isolated backend endpoints handle the prompt distribution and communication with upstream generative models. Client applications never communicate with AI APIs directly.
* **Token-Based Economy:** A robust ledger architecture built on top of cloud database triggers. Every image generation action validates user token balances server-side, preventing client-side spoofing or balance manipulation.
* **Firebase Authentication:** Secure user identity mapping linked to token balances, enabling seamless cross-device profile synchronization.

---

## Monetization & Google Play Integration

* **In-App Purchases (IAP):** Integrated with the native Google Play Billing Library. Users purchase tiered token packs directly inside the application.
* **Real-Time Developer Notifications (RTDN):** Connected via Google Cloud Pub/Sub to listen to transaction updates instantly. 
* **Server-Side Purchase Verification:** To eliminate fraud, purchase tokens received from the Android app are transmitted to a secure Firebase Cloud Function. The function cryptographically verifies the transaction against the Google Play Developer API before updating the user’s token ledger in Firestore.

---

## Tech Stack & Architecture (Android Client)

| Layer | Technologies Used |
| :--- | :--- |
| **Architectural Pattern** | Clean Architecture & MVVM (Model-View-ViewModel) |
| **UI Framework** | **Jetpack Compose** (Declarative UI) with Material Design 3 |
| **Language & Core** | **Kotlin**, Coroutines & Asynchronous Flow structures |
| **Dependency Injection** | **Hilt** (Dagger-based enterprise injection) |
| **Network & Backend** | Firebase Common SDK (Auth, Firestore) & Cloud Functions Client |
| **Billing Implementation** | Native Google Play Billing API |

---

## Application Workflow & Security Layer

```text
[Jetpack Compose UI] 
         │
         ▼ (Triggers IAP Token Pack Purchase)
[Google Play Billing API] ────► (Cryptographic Purchase Token)
         │
         ▼ (Passes Token for Fraud Verification)
[Firebase Cloud Function] ────► [Google Play Developer API] (Validates)
         │
         ▼ (If Valid: Increments User Balance)
[Firestore Token Ledger]
         │
         ▼ (User requests AI Image Generation)
[Firebase Genkit Engine] ◄──── [Cloud Function] (Validates token cost & calls AI model)
         │
         ▼
[Secure Image URL Delivered to Client]

```

### 📋 Detailed Application Workflow

* **Authentication & Handshake:** The user authenticates via Firebase Auth, establishing an encrypted session token.
* **Monetization Cycle:** The user executes an in-app purchase. The Android client sends the receipt token to the secure backend function, which cross-checks with Google Play, confirms validity, and grants the tokens locally in Firestore.
* **Generation Cycle:** The client requests an image generation through Cloud Functions. The backend subtracts the exact token fee, executes the Firebase Genkit abstraction layer, and streams the asset link securely back to the app.

---

## Project Status

* **Development State:** Core backend functionality, payment processing pipelines, and AI gen layers are fully implemented.
* **Deployment:** Internal testing environment config complete.
