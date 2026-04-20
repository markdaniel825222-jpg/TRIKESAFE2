# TRIKESAFE2

## Project Definition
A mobile security application designed to enhance the safety of commuters. The system allows users to log ride details and automatically notify guardians via SMS. TRIKESAFE2 provides comprehensive safety features for trike riders, enabling real-time tracking and emergency notifications to keep commuters safe during their journeys.

## Features
- **QR Code Integration:** Quick scanning for driver and vehicle identification using ZXing library
- **Guardian Notifications:** Automated SMS alerts using the Android SMS API to notify guardians of trip details
- **Data Management:** Secure storage of driver information and ride timestamps in Firebase Realtime Database
- **Franchise Tracking:** Uses franchise numbers for accurate vehicle verification and identification
- **User Authentication:** Secure login system using Firebase Authentication
- **Trip History:** Comprehensive trip logging and history tracking for students and commuters
- **Multi-User Support:** Support for different user roles including Students, Drivers, and Admins
- **Real-time Database Sync:** Live synchronization of ride data across devices

## Technology Stack
### Languages & Frameworks
- **Primary Language:** Java
- **IDE:** Android Studio
- **Minimum SDK:** Android 6.0 (API Level 23)
- **Target SDK:** Android 14 (API Level 34)

### Backend & Database
- **Authentication:** Firebase Authentication
- **Database:** Firebase Realtime Database & Firestore
- **Analytics:** Firebase Analytics

### Libraries & Dependencies
- **QR Code Scanning:** ZXing Android Embedded (v4.3.0), ZXing Core (v3.5.1)
- **UI Framework:** AndroidX, Material Design Components
- **Testing:** JUnit, Espresso
- **Build System:** Gradle 8.4.2

## Setup Instructions

### Prerequisites
- Android Studio (latest version)
- Java Development Kit (JDK 8 or higher)
- Firebase Project with credentials configured
- Android device or emulator (API 23+)

### Installation Steps
1. **Clone the Repository:**
   ```bash
   git clone https://github.com/markdaniel825222-jpg/TRIKESAFE2.git
   cd TRIKESAFE2
   ```

2. **Open in Android Studio:**
   - Launch Android Studio
   - Click "Open an existing Android Studio project"
   - Navigate to the TRIKESAFE2 directory and select it

3. **Configure Firebase:**
   - Download your `google-services.json` file from Firebase Console
   - Place it in the `app/` directory

4. **Build the Project:**
   ```bash
   ./gradlew build
   ```

5. **Run the Application:**
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

## Main Components

### Activities & UI Modules
- **MainActivity:** Entry point of the application
- **UserTypeSelectionActivity:** Allows users to select their role (Student, Driver, or Admin)
- **StudentRegistration:** Registration module for student commuters
- **ViewLoginActivity:** User authentication and login interface
- **HomePage:** Main dashboard for logged-in users
- **SafeTripActivity:** Trip tracking and management interface
- **StudentHistoryActivity:** View past trips and ride history
- **AdminLoginPage & AdminHomePage:** Administrative dashboard and controls

### Core Functionality Modules
- **QRGeneratorPage:** Generates QR codes for drivers and vehicles
- **QRdisplay:** Displays generated QR codes
- **CaptureAct:** QR code scanning functionality
- **DriverInfoActivity:** Display and manage driver information
- **ViewDriversActivity:** Admin view to manage drivers

### Data Models
- **User.java:** Core user model storing user information
- **UsersAdapter & DriversAdapter:** RecyclerView adapters for displaying lists

### Admin Features
- **ViewUsersActivity:** Manage and view all registered users
- **EditUserActivity:** Modify user information
- **TermsActivity:** Display app terms and conditions

## Project Structure
```
TRIKESAFE2/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/trikesafe/
│   │   │   │   ├── Activities (20+ activities)
│   │   │   │   ├── Adapters
│   │   │   │   └── Models
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   ├── build.gradle
│   └── google-services.json (not included, add your own)
├── gradle/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── README.md
```

## Android Configuration
- **Compile SDK:** 34
- **Minimum SDK:** 23 (Android 6.0)
- **Target SDK:** 34 (Android 14)
- **Java Compatibility:** Java 8
- **Namespace:** com.example.trikesafe

## Key Dependencies
- Firebase Bill of Materials (BoM): 33.1.2
- AndroidX AppCompat: 1.6.1
- Material Design: 1.9.0
- ZXing for QR Code functionality
- JUnit 4.13.2 for testing

## Getting Started for Developers

### First Time Setup
1. Install Android Studio and necessary SDKs
2. Clone this repository
3. Sync Gradle files: `File > Sync Now`
4. Configure Firebase credentials
5. Build and run on an emulator or physical device

### Build & Run
```bash
# Clean build
./gradlew clean

# Build APK
./gradlew build

# Run on connected device
./gradlew installDebug
```

## Security Notes
- All sensitive data should be stored securely using Firebase Security Rules
- Never commit `google-services.json` with production credentials
- Ensure Firebase rules restrict data access appropriately
- SMS notifications require proper permissions and user consent

## Contributing
Feel free to fork this project and submit pull requests for improvements.

## License
This project is currently unlicensed. Please contact the repository owner for licensing information.

## Contact
**Developer:** Mark Daniel  
**Repository:** https://github.com/markdaniel825222-jpg/TRIKESAFE2