# ShetiBill - Digital Farming Assistant 🌾

![ShetiBill Logo](logo.png)

**Your Complete Digital Farming Partner**

ShetiBill is a comprehensive Android application designed to empower farmers with modern digital tools for efficient agriculture management. From real-time market prices to crop management and financial tracking, ShetiBill bridges the gap between traditional farming and modern technology.

## 🚀 Features

### 📊 Market Intelligence
- **Real-time Market Prices**: Live updates for Wheat, Rice, Cotton, and other major crops
- **Price Trend Analysis**: Historical price data with percentage changes
- **Active Market Monitoring**: Track active sellers and trade volumes (₹4.2Cr+ trade volume)

### 🛒 Crop Marketplace
- **Quality Grading System**: A-Grade crop classification
- **Direct Farmer-to-Buyer Connection**: Eliminate middlemen
- **Location-based Trading**: Connect with local farmers and buyers
- **Crop Listings**: Easy upload and management of crop inventories

### 👨‍🌾 Labor Management
- **Daily Basis Tracking**: Monitor farm workers like Tejas and Vijya
- **Work Scheduling**: Organize daily farming activities
- **Labor Cost Management**: Track and calculate labor expenses

### 💰 Financial Management
- **Fertilizer Expense Tracking**: Monitor spending (₹1230.00 current season)
- **Payment Management**: Cash and digital payment tracking
- **Farm Loan Calculator**: Calculate EMI for agricultural loans
- **Expense Analytics**: Detailed financial insights

### 🏛️ Government Schemes Integration
- **Scheme Discovery**: Access to government agricultural schemes
- **Eligibility Checker**: Verify eligibility for various programs
- **Application Assistance**: Simplified application process
- **Subsidy Tracking**: Monitor subsidy amounts and benefits

### 🌤️ Weather Integration
- **Weather Forecasts**: Crop-specific weather alerts
- **Farming Recommendations**: AI-powered suggestions based on weather
- **Seasonal Planning**: Plan activities based on weather patterns

## 🛠️ Tech Stack

### Frontend
- **Language**: Java
- **UI Framework**: XML Layouts
- **Architecture**: MVVM Pattern

### Backend & Database
- **Local Database**: Room Database with SQLite
- **Cloud Services**: Firebase (Authentication, Analytics, App Check)
- **API Integration**: Retrofit 2.9.0 for REST API calls

### Key Libraries & Dependencies
```gradle
// Networking
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// Firebase
implementation platform('com.google.firebase:firebase-bom:33.6.0')
implementation 'com.google.firebase:firebase-analytics'
implementation 'com.google.firebase:firebase-auth:22.3.0'

// Database
implementation 'androidx.room:room-runtime:2.5.0'
implementation 'androidx.room:room-ktx:2.5.0'

// Image Loading
implementation 'com.github.bumptech.glide:glide:4.15.1'
implementation 'com.squareup.picasso:picasso:2.71828'

// AI Integration
implementation 'com.google.ai.client.generativeai:generativeai:0.9.0'

// Charts & Visualization
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'

// UI Components
implementation 'com.airbnb.android:lottie:6.6.0'
implementation 'com.facebook.shimmer:shimmer:0.5.0'
implementation 'de.hdodenhof:circleimageview:3.1.0'

// Localization
implementation 'com.github.YarikSOffice:lingver:1.0.0'
```

## 📱 Screenshots

| Home Screen | Market Prices | Crop Marketplace | Labor Management |
|-------------|---------------|------------------|------------------|
| ![Home](home.png) | ![Market](market.png) | ![Marketplace](marketplace.png) | ![Labor](labor.png) |

| Fertilizer Tracking | Government Schemes | Loan Calculator | Authentication |
|-------------------|-------------------|-----------------|----------------|
| ![Fertilizer](screenshots/fertilizer.png) | ![Schemes](screenshots/schemes.png) | ![Calculator](screenshots/calculator.png) | ![Auth](screenshots/auth.png) |

## 🚀 Installation

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 21 or higher
- Java 8 or higher

### Steps
1. **Clone the repository**
   ```bash
   git clone https://github.com/TG404-JV/ShetiBill.git
   cd ShetiBill
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Click "Open an existing Android Studio project"
   - Navigate to the cloned directory and select it

3. **Configure Firebase**
   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Add your Android app to the Firebase project
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Authentication and Analytics in Firebase Console

4. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or use Android Studio's "Run" button

## 🔧 Configuration

### API Keys
Create a `local.properties` file in the root directory:
```properties
# API Keys
GEMINI_API_KEY=your_gemini_api_key_here
WEATHER_API_KEY=your_weather_api_key_here
```

### Firebase Setup
1. Enable Authentication methods (Phone, Email)
2. Configure Firestore database rules
3. Set up App Check for security

## 📖 Usage

### For Farmers
1. **Register/Login** using phone number
2. **View Market Prices** for current crop rates
3. **List Your Crops** in the marketplace
4. **Manage Labor** and track daily activities
5. **Monitor Expenses** and calculate loan EMIs
6. **Explore Government Schemes** for subsidies

### For Buyers
1. **Browse Crop Listings** by location and quality
2. **Contact Farmers** directly through the app
3. **Check Market Trends** for informed purchasing
4. **Verify Crop Quality** through grading system

## 🤝 Contributing

We welcome contributions from the community! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add some amazing feature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open a Pull Request**

### Code Style
- Follow Android development best practices
- Use meaningful variable and method names
- Comment complex logic
- Maintain consistent indentation

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Farmers Community** for valuable feedback and requirements
- **Government Agricultural Departments** for scheme information
- **Open Source Libraries** that made this project possible
- **Firebase** for backend services
- **Google AI** for intelligent recommendations

## 📞 Contact

**Developer**: TG404-JV  
**Email**: your.email@example.com  
**LinkedIn**: [Your LinkedIn Profile](https://linkedin.com/in/yourprofile)  
**University**: Dr. Babasaheb Ambedkar Technological University

## 🔮 Future Enhancements

- [ ] **IoT Integration** for sensor data
- [ ] **Machine Learning** crop disease detection
- [ ] **Blockchain** for supply chain transparency
- [ ] **Multi-language Support** for regional languages
- [ ] **Offline Mode** for remote areas
- [ ] **Community Forum** for farmer discussions
- [ ] **Insurance Integration** for crop protection
- [ ] **Drone Integration** for field monitoring

## 📊 Project Stats

- **Development Time**: 6 months
- **Code Lines**: 15,000+ lines
- **Activities**: 25+ screens
- **Database Tables**: 12 entities
- **API Endpoints**: 20+ integrated
- **Supported Languages**: English, Hindi, Marathi

---

**"Growing Tomorrow's Success Today"** 🌱

*Made with ❤️ for the farming community*
