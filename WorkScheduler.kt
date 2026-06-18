<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Theme.FirstPhone" parent="android:Theme.Material.Light.NoActionBar">
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:windowLightStatusBar" tools:targetApi="m">true</item>
        <item name="android:windowBackground">@android:color/white</item>
    </style>

    <style name="Theme.FirstPhone.Splash" parent="Theme.FirstPhone">
        <item name="android:windowSplashScreenBackground" tools:targetApi="s">@color/white</item>
    </style>

    <style name="Theme.FirstPhone.Block" parent="Theme.FirstPhone">
        <item name="android:windowIsTranslucent">false</item>
        <item name="android:windowAnimationStyle">@null</item>
    </style>
</resources>
