# Firebase Auth Firestore Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Integrate Firebase Authentication and Cloud Firestore so each signed-in user has private Deck and FlashCard data.

**Architecture:** Firebase Auth owns identity and exposes the current UID. Firestore stores user-owned decks at `users/{uid}/decks/{deckId}` and flashcards at `users/{uid}/decks/{deckId}/cards/{cardId}`. Compose screens consume ViewModel state, ViewModels call repository interfaces, and Firebase implementations are registered through Koin.

**Tech Stack:** Android, Kotlin, Jetpack Compose, Koin 4.1, Navigation 3, Firebase Auth, Cloud Firestore, Kotlin coroutines Flow.

---

### Task 1: Firebase Gradle Setup

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `build.gradle.kts`
- Modify: `app/build.gradle.kts`
- Add manually from Firebase Console: `app/google-services.json`

- [x] **Step 1: Add Google Services plugin and Firestore aliases**

Add these entries to `gradle/libs.versions.toml`:

```toml
[versions]
googleServices = "4.4.4"

[libraries]
firebase-firestore = { module = "com.google.firebase:firebase-firestore" }

[plugins]
google-services = { id = "com.google.gms.google-services", version.ref = "googleServices" }
```

Keep existing `firebase-bom` and `firebase-auth` entries.

- [x] **Step 2: Register the plugin at the root**

Update root `build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.google.services) apply false
}
```

- [x] **Step 3: Apply Firebase plugin and Firestore dependency in app module**

Update `app/build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
}
```

- [ ] **Step 4: Add Firebase app config**

Create Firebase project, register Android app package `com.notsatria.flashcard`, then place the downloaded file at:

```text
app/google-services.json
```

Status: pending. This file must come from Firebase Console for package `com.notsatria.flashcard`. The app module applies the Google Services plugin only when this file exists, so local compilation can continue before the real Firebase project config is added.

- [x] **Step 5: Verify Gradle sync**

Run:

```bash
./gradlew :app:compileDebugKotlin
```

Expected: build reaches Kotlin compilation. If it fails because `google-services.json` is missing, add the real Firebase file before continuing.

- [ ] **Step 6: Commit**

```bash
git add gradle/libs.versions.toml build.gradle.kts app/build.gradle.kts app/google-services.json
git commit -m "chore: configure firebase dependencies"
```

---

### Task 2: Domain Models and UI State Types

**Files:**
- Modify: `app/src/main/java/com/notsatria/flashcard/model/Deck.kt`
- Modify: `app/src/main/java/com/notsatria/flashcard/model/FlashCard.kt`
- Create: `app/src/main/java/com/notsatria/flashcard/model/DeckWithCards.kt`
- Create: `app/src/main/java/com/notsatria/flashcard/ui/state/AsyncState.kt`

- [ ] **Step 1: Keep Deck as the public UI model**

Update `Deck.kt` so the model remains compatible with current UI while allowing Firestore metadata later:

```kotlin
package com.notsatria.flashcard.model

data class Deck(
    val id: String,
    val name: String,
    val cards: List<FlashCard> = emptyList(),
) {
    val cardCount: Int
        get() = cards.size
}
```

- [ ] **Step 2: Keep FlashCard simple and immutable**

Update `FlashCard.kt`:

```kotlin
package com.notsatria.flashcard.model

data class FlashCard(
    val id: String,
    val question: String,
    val answer: String,
)
```

- [ ] **Step 3: Add DeckWithCards for repository composition**

Create `DeckWithCards.kt`:

```kotlin
package com.notsatria.flashcard.model

data class DeckWithCards(
    val deck: Deck,
    val cards: List<FlashCard>,
) {
    fun asDeck(): Deck = deck.copy(cards = cards)
}
```

- [ ] **Step 4: Add shared async state**

Create `AsyncState.kt`:

```kotlin
package com.notsatria.flashcard.ui.state

sealed interface AsyncState<out T> {
    data object Loading : AsyncState<Nothing>
    data class Success<T>(val value: T) : AsyncState<T>
    data class Error(val message: String) : AsyncState<Nothing>
}
```

- [ ] **Step 5: Verify**

Run:

```bash
./gradlew :app:compileDebugKotlin
```

Expected: compilation succeeds.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/notsatria/flashcard/model app/src/main/java/com/notsatria/flashcard/ui/state
git commit -m "refactor: prepare deck models for remote data"
```

---

### Task 3: Authentication Repository

**Files:**
- Create: `app/src/main/java/com/notsatria/flashcard/data/auth/AuthRepository.kt`
- Create: `app/src/main/java/com/notsatria/flashcard/data/auth/FirebaseAuthRepository.kt`

- [ ] **Step 1: Define auth contract**

Create `AuthRepository.kt`:

```kotlin
package com.notsatria.flashcard.data.auth

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<FirebaseUser?>
    val currentUser: FirebaseUser?

    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    fun signOut()
}
```

- [ ] **Step 2: Implement Firebase auth**

Create `FirebaseAuthRepository.kt`:

```kotlin
package com.notsatria.flashcard.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {
    override val authState: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    override suspend fun signUp(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
```

- [ ] **Step 3: Add coroutine Tasks dependency if needed**

If `kotlinx.coroutines.tasks.await` does not resolve, add to `gradle/libs.versions.toml`:

```toml
[versions]
kotlinxCoroutinesPlayServices = "1.10.2"

[libraries]
kotlinx-coroutines-play-services = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services", version.ref = "kotlinxCoroutinesPlayServices" }
```

Then add to `app/build.gradle.kts`:

```kotlin
implementation(libs.kotlinx.coroutines.play.services)
```

- [ ] **Step 4: Verify**

Run:

```bash
./gradlew :app:compileDebugKotlin
```

Expected: auth repository compiles.

- [ ] **Step 5: Commit**

```bash
git add gradle/libs.versions.toml app/build.gradle.kts app/src/main/java/com/notsatria/flashcard/data/auth
git commit -m "feat: add firebase auth repository"
```

---

### Task 4: Firestore Deck Repository

**Files:**
- Create: `app/src/main/java/com/notsatria/flashcard/data/deck/DeckRepository.kt`
- Create: `app/src/main/java/com/notsatria/flashcard/data/deck/FirebaseDeckRepository.kt`

- [ ] **Step 1: Define repository contract**

Create `DeckRepository.kt`:

```kotlin
package com.notsatria.flashcard.data.deck

import com.notsatria.flashcard.model.Deck
import com.notsatria.flashcard.model.FlashCard
import kotlinx.coroutines.flow.Flow

interface DeckRepository {
    fun observeDecks(): Flow<List<Deck>>
    fun observeDeck(deckId: String): Flow<Deck?>

    suspend fun createDeck(name: String)
    suspend fun deleteDeck(deckId: String)
    suspend fun addFlashCard(deckId: String, question: String, answer: String)
    suspend fun deleteFlashCard(deckId: String, cardId: String)
    suspend fun updateFlashCard(deckId: String, card: FlashCard)
}
```

- [ ] **Step 2: Implement Firestore paths and snapshots**

Create `FirebaseDeckRepository.kt`:

```kotlin
package com.notsatria.flashcard.data.deck

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.notsatria.flashcard.model.Deck
import com.notsatria.flashcard.model.FlashCard
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseDeckRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : DeckRepository {
    override fun observeDecks(): Flow<List<Deck>> = callbackFlow {
        val uid = requireCurrentUid()
        val registration = decksRef(uid)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val decks = snapshot?.documents.orEmpty().map { document ->
                    Deck(
                        id = document.id,
                        name = document.getString("name").orEmpty(),
                        cards = emptyList(),
                    )
                }
                trySend(decks)
            }

        awaitClose { registration.remove() }
    }

    override fun observeDeck(deckId: String): Flow<Deck?> = callbackFlow {
        val uid = requireCurrentUid()
        val deckDocument = decksRef(uid).document(deckId)
        val cardsCollection = deckDocument.collection(CARDS_COLLECTION)

        var currentDeckName: String? = null
        var currentCards: List<FlashCard> = emptyList()

        fun sendDeck() {
            val name = currentDeckName
            trySend(
                if (name == null) {
                    null
                } else {
                    Deck(id = deckId, name = name, cards = currentCards)
                }
            )
        }

        val deckRegistration = deckDocument.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            currentDeckName = snapshot?.takeIf { it.exists() }?.getString("name")
            sendDeck()
        }

        val cardsRegistration = cardsCollection
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                currentCards = snapshot?.documents.orEmpty().map { document ->
                    FlashCard(
                        id = document.id,
                        question = document.getString("question").orEmpty(),
                        answer = document.getString("answer").orEmpty(),
                    )
                }
                sendDeck()
            }

        awaitClose {
            deckRegistration.remove()
            cardsRegistration.remove()
        }
    }

    override suspend fun createDeck(name: String) {
        val uid = requireCurrentUid()
        val now = Timestamp.now()
        decksRef(uid).add(
            mapOf(
                "name" to name.trim(),
                "createdAt" to now,
                "updatedAt" to now,
            )
        ).await()
    }

    override suspend fun deleteDeck(deckId: String) {
        val uid = requireCurrentUid()
        decksRef(uid).document(deckId).delete().await()
    }

    override suspend fun addFlashCard(deckId: String, question: String, answer: String) {
        val uid = requireCurrentUid()
        val now = Timestamp.now()
        decksRef(uid).document(deckId).collection(CARDS_COLLECTION).add(
            mapOf(
                "question" to question.trim(),
                "answer" to answer.trim(),
                "createdAt" to now,
                "updatedAt" to now,
            )
        ).await()
    }

    override suspend fun deleteFlashCard(deckId: String, cardId: String) {
        val uid = requireCurrentUid()
        decksRef(uid).document(deckId).collection(CARDS_COLLECTION).document(cardId).delete().await()
    }

    override suspend fun updateFlashCard(deckId: String, card: FlashCard) {
        val uid = requireCurrentUid()
        decksRef(uid).document(deckId).collection(CARDS_COLLECTION).document(card.id).update(
            mapOf(
                "question" to card.question.trim(),
                "answer" to card.answer.trim(),
                "updatedAt" to Timestamp.now(),
            )
        ).await()
    }

    private fun decksRef(uid: String) =
        firestore.collection(USERS_COLLECTION).document(uid).collection(DECKS_COLLECTION)

    private fun requireCurrentUid(): String =
        auth.currentUser?.uid ?: error("User must be signed in before accessing decks.")

    private companion object {
        const val USERS_COLLECTION = "users"
        const val DECKS_COLLECTION = "decks"
        const val CARDS_COLLECTION = "cards"
    }
}
```

- [ ] **Step 3: Verify**

Run:

```bash
./gradlew :app:compileDebugKotlin
```

Expected: repository compiles.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/notsatria/flashcard/data/deck
git commit -m "feat: add firestore deck repository"
```

---

### Task 5: Register Firebase Dependencies in Koin

**Files:**
- Modify: `app/src/main/java/com/notsatria/flashcard/di/AppModule.kt`

- [ ] **Step 1: Register Firebase SDK instances and repositories**

Update `AppModule.kt`:

```kotlin
package com.notsatria.flashcard.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.notsatria.flashcard.data.auth.AuthRepository
import com.notsatria.flashcard.data.auth.FirebaseAuthRepository
import com.notsatria.flashcard.data.deck.DeckRepository
import com.notsatria.flashcard.data.deck.FirebaseDeckRepository
import com.notsatria.flashcard.navigation.AppNavigator
import com.notsatria.flashcard.navigation.Navigator
import org.koin.dsl.module

val appModule = module {
    single<Navigator> { AppNavigator() }

    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    single<AuthRepository> { FirebaseAuthRepository(firebaseAuth = get()) }
    single<DeckRepository> {
        FirebaseDeckRepository(
            firestore = get(),
            auth = get(),
        )
    }
}
```

- [ ] **Step 2: Verify**

Run:

```bash
./gradlew :app:compileDebugKotlin
```

Expected: Koin module compiles.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/notsatria/flashcard/di/AppModule.kt
git commit -m "feat: register firebase dependencies"
```

---

### Task 6: Auth ViewModel and Auth Screen

**Files:**
- Create: `app/src/main/java/com/notsatria/flashcard/ui/screens/auth/AuthUiState.kt`
- Create: `app/src/main/java/com/notsatria/flashcard/ui/screens/auth/AuthViewModel.kt`
- Create: `app/src/main/java/com/notsatria/flashcard/ui/screens/auth/AuthScreen.kt`
- Modify: `app/src/main/java/com/notsatria/flashcard/di/AppModule.kt`
- Modify: `app/src/main/java/com/notsatria/flashcard/navigation/AppRoute.kt`

- [ ] **Step 1: Add auth route**

Add route to `AppRoute.kt`:

```kotlin
@Serializable
data object Auth : AppRoute
```

- [ ] **Step 2: Create AuthUiState**

Create `AuthUiState.kt`:

```kotlin
package com.notsatria.flashcard.ui.screens.auth

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isRegisterMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
```

- [ ] **Step 3: Create AuthViewModel**

Create `AuthViewModel.kt`:

```kotlin
package com.notsatria.flashcard.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.flashcard.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun toggleMode() {
        _uiState.update {
            it.copy(
                isRegisterMode = !it.isRegisterMode,
                errorMessage = null,
            )
        }
    }

    fun submit() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Email wajib diisi dan password minimal 6 karakter.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                if (state.isRegisterMode) {
                    authRepository.signUp(state.email, state.password)
                } else {
                    authRepository.signIn(state.email, state.password)
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Gagal masuk. Coba lagi.",
                    )
                }
            }
        }
    }
}
```

- [ ] **Step 4: Create AuthScreen**

Create `AuthScreen.kt`:

```kotlin
package com.notsatria.flashcard.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.flashcard.ui.components.FlashButton
import com.notsatria.flashcard.ui.components.FlashTextField
import com.notsatria.flashcard.ui.theme.FlashColors
import com.notsatria.flashcard.ui.theme.FlashSpacing
import com.notsatria.flashcard.ui.theme.FlashTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FlashColors.Background)
            .padding(FlashSpacing.lg),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = if (uiState.isRegisterMode) "Buat akun" else "Masuk",
            style = FlashTypography.displayLarge,
            color = FlashColors.Gray900,
        )
        Spacer(modifier = Modifier.height(FlashSpacing.md))
        FlashTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            placeholder = "Email",
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(FlashSpacing.sm))
        FlashTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            placeholder = "Password",
            modifier = Modifier.fillMaxWidth(),
        )
        uiState.errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(FlashSpacing.sm))
            Text(
                text = message,
                style = FlashTypography.bodyMedium,
                color = Color(0xFFDC2626),
            )
        }
        Spacer(modifier = Modifier.height(FlashSpacing.md))
        FlashButton(
            text = if (uiState.isLoading) "Memproses..." else if (uiState.isRegisterMode) "Daftar" else "Masuk",
            onClick = viewModel::submit,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(FlashSpacing.sm))
        FlashButton(
            text = if (uiState.isRegisterMode) "Sudah punya akun" else "Buat akun baru",
            onClick = viewModel::toggleMode,
            modifier = Modifier.fillMaxWidth(),
            color = FlashColors.Gray400,
        )
    }
}
```

If `FlashTextField` parameters differ, open `app/src/main/java/com/notsatria/flashcard/ui/components/FlashTextField.kt` and adapt only the argument names, not the Auth screen behavior.

- [ ] **Step 5: Register AuthViewModel**

Add to `AppModule.kt`:

```kotlin
import com.notsatria.flashcard.ui.screens.auth.AuthViewModel
import org.koin.core.module.dsl.viewModel

viewModel { AuthViewModel(authRepository = get()) }
```

- [ ] **Step 6: Verify**

Run:

```bash
./gradlew :app:compileDebugKotlin
```

Expected: auth screen and ViewModel compile.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/notsatria/flashcard/ui/screens/auth app/src/main/java/com/notsatria/flashcard/navigation/AppRoute.kt app/src/main/java/com/notsatria/flashcard/di/AppModule.kt
git commit -m "feat: add firebase auth screen"
```

---

### Task 7: Home ViewModel and Firestore Deck List

**Files:**
- Create: `app/src/main/java/com/notsatria/flashcard/ui/screens/home/HomeUiState.kt`
- Create: `app/src/main/java/com/notsatria/flashcard/ui/screens/home/HomeViewModel.kt`
- Modify: `app/src/main/java/com/notsatria/flashcard/ui/screens/home/HomeScreen.kt`
- Modify: `app/src/main/java/com/notsatria/flashcard/di/AppModule.kt`

- [ ] **Step 1: Add HomeUiState**

Create `HomeUiState.kt`:

```kotlin
package com.notsatria.flashcard.ui.screens.home

import com.notsatria.flashcard.model.Deck

data class HomeUiState(
    val isLoading: Boolean = true,
    val decks: List<Deck> = emptyList(),
    val errorMessage: String? = null,
)
```

- [ ] **Step 2: Add HomeViewModel**

Create `HomeViewModel.kt`:

```kotlin
package com.notsatria.flashcard.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.flashcard.data.auth.AuthRepository
import com.notsatria.flashcard.data.deck.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val deckRepository: DeckRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            deckRepository.observeDecks()
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Gagal memuat deck.",
                        )
                    }
                }
                .collect { decks ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            decks = decks,
                            errorMessage = null,
                        )
                    }
                }
        }
    }

    fun createDeck(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            runCatching {
                deckRepository.createDeck(name)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.message ?: "Gagal membuat deck.")
                }
            }
        }
    }

    fun deleteDeck(deckId: String) {
        viewModelScope.launch {
            runCatching {
                deckRepository.deleteDeck(deckId)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.message ?: "Gagal menghapus deck.")
                }
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}
```

- [ ] **Step 3: Wire HomeScreen callbacks**

Modify `HomeScreen.kt` signature:

```kotlin
fun HomeScreen(
    decks: List<Deck>,
    onDeckClick: (Deck) -> Unit,
    onCreateDeck: () -> Unit,
    onDeleteDeck: (Deck) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
)
```

Update existing callbacks:

```kotlin
floatingActionButton = {
    FlashButton(text = "+ Deck Baru", onClick = onCreateDeck)
}
```

Update `DeckCard`:

```kotlin
DeckCard(
    deck = deck,
    deckIndex = index,
    onClick = { onDeckClick(deck) },
    onDelete = { onDeleteDeck(deck) },
)
```

Add a simple sign-out button near the header:

```kotlin
FlashButton(text = "Keluar", onClick = onSignOut)
```

Use a temporary create action in navigation first, such as `onCreateDeck = { viewModel.createDeck("Deck Baru") }`. Replace with a proper dialog in Task 10.

- [ ] **Step 4: Register HomeViewModel**

Add to `AppModule.kt`:

```kotlin
import com.notsatria.flashcard.ui.screens.home.HomeViewModel

viewModel {
    HomeViewModel(
        deckRepository = get(),
        authRepository = get(),
    )
}
```

- [ ] **Step 5: Verify**

Run:

```bash
./gradlew :app:compileDebugKotlin
```

Expected: Home screen compiles with new callbacks.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/notsatria/flashcard/ui/screens/home app/src/main/java/com/notsatria/flashcard/di/AppModule.kt
git commit -m "feat: load home decks from firestore"
```

---

### Task 8: Auth-Gated Navigation

**Files:**
- Create: `app/src/main/java/com/notsatria/flashcard/navigation/RootViewModel.kt`
- Modify: `app/src/main/java/com/notsatria/flashcard/navigation/AppNavigation.kt`
- Modify: `app/src/main/java/com/notsatria/flashcard/di/AppModule.kt`

- [ ] **Step 1: Add RootViewModel**

Create `RootViewModel.kt`:

```kotlin
package com.notsatria.flashcard.navigation

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.notsatria.flashcard.data.auth.AuthRepository
import kotlinx.coroutines.flow.Flow

class RootViewModel(
    authRepository: AuthRepository,
) : ViewModel() {
    val authState: Flow<FirebaseUser?> = authRepository.authState
}
```

- [ ] **Step 2: Register RootViewModel**

Add to `AppModule.kt`:

```kotlin
import com.notsatria.flashcard.navigation.RootViewModel

viewModel { RootViewModel(authRepository = get()) }
```

- [ ] **Step 3: Replace sampleDecks in AppNavigation**

In `AppNavigation.kt`, remove:

```kotlin
import com.notsatria.flashcard.model.sampleDecks
```

Collect auth and home ViewModel state:

```kotlin
val rootViewModel: RootViewModel = koinViewModel()
val authUser by rootViewModel.authState.collectAsStateWithLifecycle(initialValue = null)
```

Use `rememberNavBackStack(if (authUser == null) AppRoute.Auth else AppRoute.Home)`.

Add an Auth entry:

```kotlin
entry<AppRoute.Auth> {
    AuthScreen()
}
```

Update the Home entry:

```kotlin
entry<AppRoute.Home> {
    val homeViewModel: HomeViewModel = koinViewModel()
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        decks = uiState.decks,
        onDeckClick = { deck -> navigator.navigateTo(AppRoute.DeckDetail(deck.id)) },
        onCreateDeck = { homeViewModel.createDeck("Deck Baru") },
        onDeleteDeck = { deck -> homeViewModel.deleteDeck(deck.id) },
        onSignOut = { homeViewModel.signOut() },
    )
}
```

For detail/study/generate entries, temporarily pass `deck = null` until Task 9 wires their ViewModels. This keeps compilation moving while removing `sampleDecks`.

- [ ] **Step 4: Keep back stack aligned after auth changes**

Add this effect in `AppNavigation.kt` after `SideEffect`:

```kotlin
LaunchedEffect(authUser) {
    backStack.clear()
    backStack.add(if (authUser == null) AppRoute.Auth else AppRoute.Home)
}
```

- [ ] **Step 5: Verify**

Run:

```bash
./gradlew :app:compileDebugKotlin
```

Expected: app compiles without `sampleDecks` usage in navigation.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/notsatria/flashcard/navigation app/src/main/java/com/notsatria/flashcard/di/AppModule.kt
git commit -m "feat: gate navigation by firebase auth"
```

---

### Task 9: Deck Detail ViewModel and Cards

**Files:**
- Create: `app/src/main/java/com/notsatria/flashcard/ui/screens/detail/DeckDetailUiState.kt`
- Create: `app/src/main/java/com/notsatria/flashcard/ui/screens/detail/DeckDetailViewModel.kt`
- Modify: `app/src/main/java/com/notsatria/flashcard/ui/screens/detail/DeckDetailScreen.kt`
- Modify: `app/src/main/java/com/notsatria/flashcard/navigation/AppNavigation.kt`
- Modify: `app/src/main/java/com/notsatria/flashcard/di/AppModule.kt`

- [ ] **Step 1: Add detail UI state**

Create `DeckDetailUiState.kt`:

```kotlin
package com.notsatria.flashcard.ui.screens.detail

import com.notsatria.flashcard.model.Deck

data class DeckDetailUiState(
    val isLoading: Boolean = true,
    val deck: Deck? = null,
    val errorMessage: String? = null,
)
```

- [ ] **Step 2: Add DeckDetailViewModel**

Create `DeckDetailViewModel.kt`:

```kotlin
package com.notsatria.flashcard.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.flashcard.data.deck.DeckRepository
import com.notsatria.flashcard.model.FlashCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeckDetailViewModel(
    private val deckId: String,
    private val deckRepository: DeckRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DeckDetailUiState())
    val uiState: StateFlow<DeckDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            deckRepository.observeDeck(deckId)
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Gagal memuat kartu.",
                        )
                    }
                }
                .collect { deck ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            deck = deck,
                            errorMessage = null,
                        )
                    }
                }
        }
    }

    fun addFlashCard(question: String, answer: String) {
        if (question.isBlank() || answer.isBlank()) return
        viewModelScope.launch {
            runCatching {
                deckRepository.addFlashCard(deckId, question, answer)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.message ?: "Gagal menambah kartu.")
                }
            }
        }
    }

    fun deleteFlashCard(cardId: String) {
        viewModelScope.launch {
            runCatching {
                deckRepository.deleteFlashCard(deckId, cardId)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.message ?: "Gagal menghapus kartu.")
                }
            }
        }
    }

    fun updateFlashCard(card: FlashCard) {
        viewModelScope.launch {
            runCatching {
                deckRepository.updateFlashCard(deckId, card)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.message ?: "Gagal menyimpan kartu.")
                }
            }
        }
    }
}
```

- [ ] **Step 3: Let DeckDetailScreen delete cards**

Modify `DeckDetailScreen.kt` signature:

```kotlin
fun DeckDetailScreen(
    deck: Deck?,
    deckIndex: Int,
    onBack: () -> Unit,
    onStudyClick: () -> Unit,
    onGenerateClick: () -> Unit,
    onAddCard: () -> Unit,
    onDeleteCard: (FlashCard) -> Unit,
    modifier: Modifier = Modifier,
)
```

Update the bottom button:

```kotlin
FlashButton(
    text = "+ Tambah",
    onClick = onAddCard,
    modifier = Modifier.weight(1f),
    color = deckColor,
)
```

Update `CardItem`:

```kotlin
CardItem(
    card = card,
    deckColor = deckColor,
    onDelete = { onDeleteCard(card) },
)
```

- [ ] **Step 4: Register parameterized ViewModel**

Add to `AppModule.kt`:

```kotlin
import com.notsatria.flashcard.ui.screens.detail.DeckDetailViewModel

viewModel { parameters ->
    DeckDetailViewModel(
        deckId = parameters.get(),
        deckRepository = get(),
    )
}
```

- [ ] **Step 5: Wire detail route**

In `AppNavigation.kt` detail entry:

```kotlin
entry<AppRoute.DeckDetail> { route ->
    val detailViewModel: DeckDetailViewModel = koinViewModel(
        parameters = { parametersOf(route.deckId) }
    )
    val uiState by detailViewModel.uiState.collectAsStateWithLifecycle()

    DeckDetailScreen(
        deck = uiState.deck,
        deckIndex = 0,
        onBack = { navigator.navigateBack() },
        onStudyClick = { navigator.navigateTo(AppRoute.StudyMode(route.deckId)) },
        onGenerateClick = { navigator.navigateTo(AppRoute.GenerateAI(route.deckId)) },
        onAddCard = { detailViewModel.addFlashCard("Pertanyaan baru", "Jawaban baru") },
        onDeleteCard = { card -> detailViewModel.deleteFlashCard(card.id) },
    )
}
```

Add imports:

```kotlin
import com.notsatria.flashcard.ui.screens.detail.DeckDetailViewModel
import org.koin.core.parameter.parametersOf
```

- [ ] **Step 6: Verify**

Run:

```bash
./gradlew :app:compileDebugKotlin
```

Expected: detail screen gets data from Firestore.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/notsatria/flashcard/ui/screens/detail app/src/main/java/com/notsatria/flashcard/navigation/AppNavigation.kt app/src/main/java/com/notsatria/flashcard/di/AppModule.kt
git commit -m "feat: load deck detail from firestore"
```

---

### Task 10: Replace Placeholder Create Actions With Dialogs

**Files:**
- Modify: `app/src/main/java/com/notsatria/flashcard/ui/screens/home/HomeScreen.kt`
- Modify: `app/src/main/java/com/notsatria/flashcard/ui/screens/detail/DeckDetailScreen.kt`
- Modify: `app/src/main/java/com/notsatria/flashcard/navigation/AppNavigation.kt`

- [ ] **Step 1: Add create deck dialog state in HomeScreen**

Inside `HomeScreen`, add Compose state:

```kotlin
var showCreateDialog by rememberSaveable { mutableStateOf(false) }
var deckName by rememberSaveable { mutableStateOf("") }
```

Change FAB to:

```kotlin
FlashButton(text = "+ Deck Baru", onClick = { showCreateDialog = true })
```

Add an `AlertDialog`:

```kotlin
if (showCreateDialog) {
    AlertDialog(
        onDismissRequest = { showCreateDialog = false },
        title = { Text("Deck baru") },
        text = {
            FlashTextField(
                value = deckName,
                onValueChange = { deckName = it },
                placeholder = "Nama deck",
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            FlashButton(
                text = "Simpan",
                onClick = {
                    onCreateDeck(deckName)
                    deckName = ""
                    showCreateDialog = false
                },
            )
        },
        dismissButton = {
            FlashButton(
                text = "Batal",
                onClick = { showCreateDialog = false },
                color = FlashColors.Gray400,
            )
        },
    )
}
```

Update signature from `onCreateDeck: () -> Unit` to:

```kotlin
onCreateDeck: (String) -> Unit
```

- [ ] **Step 2: Add create card dialog state in DeckDetailScreen**

Inside `DeckDetailScreen`, add:

```kotlin
var showAddCardDialog by rememberSaveable { mutableStateOf(false) }
var question by rememberSaveable { mutableStateOf("") }
var answer by rememberSaveable { mutableStateOf("") }
```

Change bottom action to set `showAddCardDialog = true`.

Add an `AlertDialog` that calls:

```kotlin
onAddCard(question, answer)
```

Update signature from `onAddCard: () -> Unit` to:

```kotlin
onAddCard: (question: String, answer: String) -> Unit
```

- [ ] **Step 3: Update navigation callbacks**

In Home entry:

```kotlin
onCreateDeck = homeViewModel::createDeck
```

In detail entry:

```kotlin
onAddCard = detailViewModel::addFlashCard
```

- [ ] **Step 4: Verify**

Run:

```bash
./gradlew :app:compileDebugKotlin
```

Expected: create deck/card dialogs compile and call ViewModels with real input.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/notsatria/flashcard/ui/screens/home/HomeScreen.kt app/src/main/java/com/notsatria/flashcard/ui/screens/detail/DeckDetailScreen.kt app/src/main/java/com/notsatria/flashcard/navigation/AppNavigation.kt
git commit -m "feat: add deck and card creation dialogs"
```

---

### Task 11: Study and Generate Screens Use Firestore Decks

**Files:**
- Modify: `app/src/main/java/com/notsatria/flashcard/navigation/AppNavigation.kt`

- [ ] **Step 1: Reuse DeckDetailViewModel for StudyMode**

In `entry<AppRoute.StudyMode>`, replace sample lookup with:

```kotlin
val detailViewModel: DeckDetailViewModel = koinViewModel(
    parameters = { parametersOf(route.deckId) }
)
val uiState by detailViewModel.uiState.collectAsStateWithLifecycle()

StudyModeScreen(
    deck = uiState.deck,
    deckIndex = 0,
    onBack = { navigator.navigateBack() },
)
```

- [ ] **Step 2: Reuse DeckDetailViewModel for GenerateAI**

In `entry<AppRoute.GenerateAI>`, replace sample lookup with:

```kotlin
val detailViewModel: DeckDetailViewModel = koinViewModel(
    parameters = { parametersOf(route.deckId) }
)
val uiState by detailViewModel.uiState.collectAsStateWithLifecycle()

GenerateAIScreen(
    deck = uiState.deck,
    deckIndex = 0,
    onBack = { navigator.navigateBack() },
)
```

- [ ] **Step 3: Verify no `sampleDecks` source remains in navigation**

Run:

```bash
rg "sampleDecks" app/src/main/java/com/notsatria/flashcard
```

Expected: no usage in navigation or screen data flow. It is acceptable if `SampleData.kt` still exists unused for previews.

- [ ] **Step 4: Verify compile**

Run:

```bash
./gradlew :app:compileDebugKotlin
```

Expected: study and generate screens compile with Firestore-backed deck data.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/notsatria/flashcard/navigation/AppNavigation.kt
git commit -m "feat: use firestore data across deck routes"
```

---

### Task 12: Firestore Security Rules

**Files:**
- Create: `firestore.rules`
- Create: `firebase.json`

- [ ] **Step 1: Add Firestore rules**

Create `firestore.rules`:

```js
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null
        && request.auth.uid == userId;

      match /decks/{deckId} {
        allow read, write: if request.auth != null
          && request.auth.uid == userId;

        match /cards/{cardId} {
          allow read, write: if request.auth != null
            && request.auth.uid == userId;
        }
      }
    }
  }
}
```

- [ ] **Step 2: Add Firebase rules config**

Create `firebase.json`:

```json
{
  "firestore": {
    "rules": "firestore.rules"
  }
}
```

- [ ] **Step 3: Validate rules manually in Firebase Console**

In the Firestore Rules simulator, verify:

```text
Authenticated uid abc can read /users/abc/decks/deck1: allow
Authenticated uid abc can read /users/xyz/decks/deck1: deny
Unauthenticated request can read /users/abc/decks/deck1: deny
Authenticated uid abc can write /users/abc/decks/deck1/cards/card1: allow
```

- [ ] **Step 4: Commit**

```bash
git add firestore.rules firebase.json
git commit -m "chore: add firestore user data rules"
```

---

### Task 13: Manual QA and Final Verification

**Files:**
- Verify app behavior across Auth, Home, Deck Detail, Study, and Generate routes.

- [ ] **Step 1: Run compile**

```bash
./gradlew :app:compileDebugKotlin
```

Expected: build succeeds.

- [ ] **Step 2: Install or run the app**

Use Android Studio or:

```bash
./gradlew :app:installDebug
```

Expected: app installs on connected device/emulator.

- [ ] **Step 3: Test user isolation**

Manual checklist:

```text
Create User A.
Create deck "Deck A".
Add flashcard "Q A" / "A A".
Sign out.
Create User B.
Confirm User B does not see "Deck A".
Create deck "Deck B".
Sign out.
Sign in as User A.
Confirm User A sees "Deck A" and does not see "Deck B".
Open Deck A detail.
Confirm card "Q A" / "A A" appears.
Delete card and confirm it disappears.
Delete Deck A and confirm it disappears from Home.
```

- [ ] **Step 4: Check Firestore data paths**

In Firebase Console, confirm documents are created under:

```text
users/{uid}/decks/{deckId}
users/{uid}/decks/{deckId}/cards/{cardId}
```

- [ ] **Step 5: Commit any QA fixes**

```bash
git add app firestore.rules firebase.json gradle build.gradle.kts
git commit -m "fix: polish firebase deck flow"
```

---

## Notes for Implementation

- Enable Email/Password provider in Firebase Console before testing auth.
- `google-services.json` is environment-specific. Do not fabricate it; use the file generated by Firebase Console for package `com.notsatria.flashcard`.
- Firestore subcollection rules are explicit because parent document rules do not automatically protect nested subcollections.
- The first implementation intentionally keeps deck color as `deckIndex = 0` on non-list screens. Restore stable color assignment later by storing `colorIndex` or `createdAt` order if visual consistency matters.
- Deleting a deck with client SDK does not automatically delete all subcollection card documents. For small student/dev usage this can be accepted temporarily, but production should add recursive delete through a trusted backend or delete visible cards before deleting the deck.
