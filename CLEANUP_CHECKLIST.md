# Project Cleanup Checklist

## Files to Delete (Old Mars Photos API Example)

The following files are from the old example and should be removed:

- [ ] `app/src/main/java/com/example/marsphotos/network/MarsApiService.kt` - Old Mars API service (replaced by AuthApiService)
- [ ] `app/src/main/java/com/example/marsphotos/model/MarsPhoto.kt` - Old Mars photo model (deleted)
- [ ] `app/src/main/java/com/example/marsphotos/data/MarsPhotosRepository.kt` - Old Mars repository (replaced by AuthRepository)
- [ ] `app/src/main/java/com/example/marsphotos/ui/screens/HomeScreen.kt` - Old Mars display screen
- [ ] `app/src/main/java/com/example/marsphotos/ui/screens/MarsViewModel.kt` - Old Mars view model (replaced by AuthViewModel)
- [ ] `app/src/main/java/com/example/marsphotos/ui/MarsPhotosApp.kt` - Old root composable (replaced by AppRoot)
- [ ] `app/src/main/java/com/example/marsphotos/data/AppContainer.kt` - Old app container (moved to `di/` folder)

**Note:** The test files in `app/src/test/` reference the old Mars API:
- [ ] `app/src/test/java/com/example/marsphotos/MarsViewModelTest.kt`
- [ ] `app/src/test/java/com/example/marsphotos/NetworkMarsRepositoryTest.kt`
- [ ] `app/src/test/java/com/example/marsphotos/fake/` - Entire fake data folder

These can either be deleted or updated to test the new auth functionality.

## Files Kept & Preserved

The following files were kept as they provide useful base infrastructure:

- ✅ `app/build.gradle.kts` - Build configuration (keep)
- ✅ `app/src/main/AndroidManifest.xml` - Manifest (update if needed)
- ✅ `app/src/main/res/` - Resources directory
- ✅ `app/src/main/java/com/example/marsphotos/ui/theme/` - Theme files
- ✅ `app/src/main/java/com/example/marsphotos/MainActivity.kt` - Updated for new structure
- ✅ `app/src/main/java/com/example/marsphotos/MarsPhotosApplication.kt` - Updated with new DI

## New Files Created

The following new files have been created:

✅ Core Infrastructure:
- `core/network/RetrofitClient.kt` - Retrofit singleton
- `core/utils/ValidationUtils.kt` - Validation helpers

✅ Data Layer:
- `data/model/AuthModels.kt` - Login models
- `data/model/ApiModels.kt` - Generic API responses
- `data/remote/AuthApiService.kt` - Auth endpoints
- `data/repository/AuthRepository.kt` - Auth repository

✅ UI Layer:
- `ui/auth/AuthViewModel.kt` - Auth state management
- `ui/auth/LoginScreen.kt` - Login UI
- `ui/common/CommonComposables.kt` - Common UI components
- `ui/AppRoot.kt` - Root navigation

✅ DI:
- `di/AppContainer.kt` - Dependency injection container

✅ Documentation:
- `RESTRUCTURING_GUIDE.md` - Complete guide
- `CLEANUP_CHECKLIST.md` - This file

## Steps to Complete Restructuring

1. **Delete old files** (see list above)
2. **Delete old test files** or update them for new tests
3. **Update AndroidManifest.xml** if needed:
   - Ensure `MarsPhotosApplication` is referenced as application class
   - Remove any Mars-photos-specific permissions (if any)

4. **Update build.gradle.kts** dependencies:
   - Ensure you have all required dependencies for auth/networking
   - Check for any Mars-specific dependencies to remove

5. **Update backend URL** in `core/network/RetrofitClient.kt`

6. **Test the login flow**:
   - Build and run the app
   - Verify login screen appears
   - Test login functionality

7. **Implement main app content**:
   - Replace `MainScreen()` in `ui/AppRoot.kt` with your actual app
   - Add navigation between screens

## Build Issues to Watch For

If you encounter build errors after cleanup:

1. **"Cannot find symbol" errors**: Likely old imports still in files
   - Search for: `MarsPhotosRepository`, `MarsApiService`, `MarsPhoto`
   - Delete/update any files still referencing these

2. **Package not found errors**: Make sure `di/AppContainer.kt` is being used
   - Update imports: `com.example.marsphotos.di.AppContainer`

3. **ViewModel factory errors**: Ensure `AuthViewModel.Factory` is properly initialized
   - Check `ui/AppRoot.kt` has correct imports

## Reference Architecture

For future features, follow this pattern:

```
✅ DO THIS:
data/
├── remote/
│   └── FeatureApiService.kt (API endpoints)
├── model/
│   └── FeatureModels.kt (Data classes)
└── repository/
    └── FeatureRepository.kt (Data abstraction)

ui/
├── feature/
│   ├── FeatureViewModel.kt (State management)
│   ├── FeatureScreen.kt (UI)
│   └── FeatureEvent.kt (User actions)

di/
└── AppContainer.kt (Add feature repository)
```

## Support Files

For additional reference, see:
- [Android Guide - Repository Pattern](https://developer.android.com/guide/architecture)
- [Compose Documentation](https://developer.android.com/jetpack/compose/documentation)
- [Retrofit Documentation](https://square.github.io/retrofit/)
