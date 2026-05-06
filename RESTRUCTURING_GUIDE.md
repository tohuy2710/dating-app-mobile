# Project Restructuring Guide

## New Project Structure

This project has been restructured from a Mars Photos example to a production-ready authentication-based application.

### Directory Organization

```
com.example.marsphotos/
├── core/                           # Core infrastructure & utilities
│   ├── network/
│   │   └── RetrofitClient.kt       # Singleton Retrofit configuration
│   └── utils/
│       └── ValidationUtils.kt      # Email, password validation helpers
│
├── data/                           # Data layer (Repository Pattern)
│   ├── model/
│   │   ├── AuthModels.kt           # LoginRequest, LoginResponse, User
│   │   └── ApiModels.kt            # Generic API response wrappers
│   ├── remote/
│   │   └── AuthApiService.kt       # Retrofit API endpoints (login only)
│   └── repository/
│       └── AuthRepository.kt       # Auth repository implementations
│
├── ui/                             # UI layer (Compose)
│   ├── auth/
│   │   ├── AuthViewModel.kt        # Authentication state management
│   │   └── LoginScreen.kt          # Login UI composable
│   ├── common/
│   │   └── CommonComposables.kt    # Reusable UI components
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Shape.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── AppRoot.kt                  # Root navigation composable
│   └── screens/                    # (Optional: for other screens)
│
├── di/                             # Dependency Injection
│   └── AppContainer.kt             # DI container & factory
│
├── MainActivity.kt                 # Entry point
├── MarsPhotosApplication.kt        # Application class
└── R.java                          # Resources
```

## Architecture Layers

### 1. **Core Layer** (`core/`)
- Contains shared infrastructure and utilities
- `RetrofitClient`: Centralized Retrofit configuration
- `ValidationUtils`: Common validation functions

### 2. **Data Layer** (`data/`)
- **Remote**: API service definitions
  - `AuthApiService.kt`: Backend endpoints
- **Repository**: Abstraction layer for data sources
  - `AuthRepository`: Interface and implementation
- **Model**: Data classes
  - `AuthModels.kt`: Login/User models
  - `ApiModels.kt`: Generic API response types

### 3. **UI Layer** (`ui/`)
- **Auth**: Authentication feature
  - `AuthViewModel.kt`: Handles login logic and state
  - `LoginScreen.kt`: UI implementation
- **Common**: Reusable UI components
- **Theme**: App styling

### 4. **DI Container** (`di/`)
- `AppContainer.kt`: Manages dependency instances
- Follows lazy initialization pattern

## How to Use

### Adding a New Feature

1. **Create API endpoint** in `data/remote/YourApiService.kt`
```kotlin
interface YourApiService {
    @GET("endpoint")
    suspend fun getYourData(): YourResponse
}
```

2. **Create models** in `data/model/YourModels.kt`
```kotlin
@Serializable
data class YourResponse(val data: String)
```

3. **Create repository** in `data/repository/YourRepository.kt`
```kotlin
interface YourRepository {
    suspend fun getData(): YourResponse
}

class NetworkYourRepository(private val api: YourApiService) : YourRepository {
    override suspend fun getData() = api.getYourData()
}
```

4. **Update AppContainer** in `di/AppContainer.kt`
```kotlin
override val yourRepository: YourRepository by lazy {
    NetworkYourRepository(retrofitService)
}
```

5. **Create ViewModel** in `ui/your_feature/YourViewModel.kt`
```kotlin
class YourViewModel(private val repo: YourRepository) : ViewModel() {
    // UI state and logic
}
```

6. **Create UI** in `ui/your_feature/YourScreen.kt`

## Configuration

### Base URL
Update the base URL in `core/network/RetrofitClient.kt`:
```kotlin
private const val BASE_URL = "https://your-backend-api.com/api/"
```

### Authentication Token
Store and use authentication tokens in:
- `AuthRepository`: Manages login
- Add token interceptor in `RetrofitClient` for authenticated requests

### Add OkHttp Interceptor
```kotlin
private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val original = chain.request()
        val authorized = original.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        chain.proceed(authorized)
    }
    .build()
```

## Migration from Example

### ✅ Kept
- MVVM + Repository pattern
- Retrofit + kotlinx.serialization
- Compose UI framework
- Dependency injection structure

### ❌ Removed
- Mars Photos API endpoints
- Mars Photo data models
- Photos grid UI
- Example functionality

### ✨ New
- Authentication system
- Login screen
- Auth repository
- Auth view model
- AppRoot navigation
- Validation utilities
- API response wrappers

## Best Practices

1. **Separation of Concerns**: Each layer has specific responsibilities
2. **Dependency Injection**: Use AppContainer for all dependencies
3. **State Management**: Use ViewModels for UI state
4. **Error Handling**: Use sealed interfaces for UI states
5. **Lazy Initialization**: Use `by lazy` for repositories and services
6. **Immutability**: Use `data class` for models with `@Serializable`

## Next Steps

1. Update `RetrofitClient.kt` with your backend URL
2. Implement token storage (SharedPreferences, DataStore)
3. Add networking interceptors for authentication headers
4. Replace `MainScreen()` placeholder with your actual app content
5. Add additional APIs and features following the established pattern
6. Implement navigation between screens using Compose Navigation
