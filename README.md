# Android Project - Integration of Google Places and Firebase

This project uses **Google Places API** to access Google Places services, as well as **Firebase** for features such as real-time database.

## Prerequisites

Before you can run this project locally, you'll need to configure a few things:

### 1. Google Places API Key

The Google Places API requires an API key for access to the services. Here's how to obtain and integrate your key into the project:

#### Obtain your Google Places API Key

1. Go to the [Google Cloud Console](https://console.developers.google.com/).
2. Create a new project or use an existing project.
3. Enable the Google Places API:
    - In the console menu, go to **APIs & Services > Library**.
    - Search for "Places API" and enable it for your project.
4. Once the API is enabled, go to **APIs & Services > Credentials**.
5. Create an API key by clicking on **Create Credentials > API Key**.
6. Copy the generated key, as you'll need it in the next steps.

#### Add the API Key to your project

1. At the root of your project, create a file called `secrets.properties`.
2. Open the `secrets.properties` file and add the following line:

   ```properties
   PLACES_API_KEY=YOUR_API_KEY
Replace **YOUR_API_KEY** with the key you copied from the Google Cloud Console

### 2. Firebase Integration
Firebase is integrated for features Firestore and Storage. Follow the steps below to configure Firebase for this project.

1. Go to Firebase Console. 
2. Create a new Firebase project:
3. Click on Add project and follow the steps to create a project. 
4. You can choose whether or not to enable Google Analytics for the project, based on your preferences. 
5. Once the project is created, you'll be redirected to the project’s home page. 
6. Add your Android app to Firebase.In the Firebase console, click on Add App and select Android 
7. Enter your Android app’s package name (available in the AndroidManifest.xml file of your project). 
8. Download the google-services.json file and place it in the app/ directory of the project. 
9. Go to Firestore Database in the Firebase Console. 
10. Click Create Database and choose Start in test mode (this allows full access for testing purposes). 
11. Select your preferred Firestore location. 
12. Click Enable. 
13. Firebase Storage: Firebase Storage is used for storing files such as images, videos, or other media files. 
14. Go to Storage in the Firebase Console. 
15. Click Get Started to enable Firebase Storage. 
16. Choose the storage location and click Done.

Note: Firebase Storage offers a free trial for up to 1GB of storage. After the free trial ends, you may be charged for storage usage. You can monitor usage and switch to a paid plan if needed. For the storage requirements of the app (e.g., storing images or files), make sure you have sufficient free storage or switch to a paid plan after the free trial ends.
Note: Firebase Services is already enabled for the project you don't have to do any configuration in gradle for this.