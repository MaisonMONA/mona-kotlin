# Latest MONA 1.0 ALPHA Version:

Initial internal app launch for testing within a specific range of people. Mona is a Kotlin 
native application used to locate, search and explore artworks and cultural places in Montreal.
The basic app features are:
 - Navigating through a MapView via OpenStreetMap
 - Sorting and filtering through the list of data in terms of attributes and calculations such as distance from user
 - Capturing pictures of items such as artworks and cultural places
 - Collect merit badges based upon captured items

This code uses:
1) [Android Jetpack Navigation](https://developer.android.com/guide/navigation)
2) [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
3) [Data Binding](https://developer.android.com/topic/libraries/data-binding)

What has to be worked on:
1) Data population -> Many skipped frames at SplashActivity launch when populating database at app launch. See Terminal
2) Navigation -> Implement such that each tab has its very own Navigation graph. [Example here](https://android.jlelse.eu/instagram-style-navigation-using-navigation-component-854037cf1389)
3) Badges -> Creating instances of the Badges before.. Possibility of a specific View Model for the Badges?
