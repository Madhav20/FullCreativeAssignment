# FullCreativeAssignment

📱 Event Booking App
A modern Android application that allows users to book event time slots, built using Clean MVVM architecture with strong offline support and full unit test coverage.

🚀 Features
1. View available time slots in a responsive grid layout
2. Select a slot and enter customer details
3. Form validation with real-time error handling
4. Book events via API
5. Offline-first booking using Room database fallback
6. View all booked events on the home screen
7. Proper loading, success, and error state management

🏗 Architecture
The app follows Clean MVVM architecture:
1. UI → Jetpack Compose
2. State Management → StateFlow & SharedFlow
3. ViewModels → Lifecycle-aware, testable
4. Repository Layer → Handles API + local database logic
5. Offline Support → Room database fallback when network fails

Dependency Injection → Hilt
🌐 Networking

Retrofit for API calls
OkHttp with custom connectivity interceptor

Robust error handling:
1. No Internet
2. Timeout
3. HTTP errors
4. Unknown errors

💾 Offline-First Approach
1. When network is unavailable:
2. Available slots are fetched from Room
3. Booking is stored locally
4. Slots are marked as booked in database
5. This ensures uninterrupted user experience.

🧪 Testing
1. Repository layer tests (API success, failure, offline fallback)
2. ViewModel tests (state mapping, validation, navigation events)

🛠 Tech Stack
1. Kotlin
2. Jetpack Compose
3. MVVM
4. Hilt
5. Retrofit
6. Room
7. Coroutines & Flow
8. Turbine (Flow testing)


<img width="280" height="700" alt="Screenshot_20260205_172548" src="https://github.com/user-attachments/assets/e4b81a5d-ea0a-4c50-ae1e-7e96c4873e86" />
<img width="280" height="700" alt="Screenshot_20260205_172617" src="https://github.com/user-attachments/assets/5153d00d-cb7f-4813-926b-e7141c24f59e" />
<img width="280" height="700" alt="Screenshot_20260205_172601" src="https://github.com/user-attachments/assets/a4ee1701-f67d-4fbf-8a95-39409d007379" />
<img width="280" height="700" alt="Screenshot_20260205_172732" src="https://github.com/user-attachments/assets/8b392f06-c1b5-4c45-bea6-7724b020f3c9" />
<img width="280" height="700" alt="Screenshot_20260205_172652" src="https://github.com/user-attachments/assets/7921457a-fc34-4eba-aa2f-6a359c8c3436" />
<img width="280" height="700" alt="Screenshot_20260205_172739" src="https://github.com/user-attachments/assets/a325e984-11b3-4245-8ecb-9116b18c6461" />
<img width="280" height="700" alt="Screenshot_20260205_172635" src="https://github.com/user-attachments/assets/de41b344-f46b-40e3-94fa-10bd23c8832e" />
