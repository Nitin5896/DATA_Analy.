# Firestore model classes are (de)serialized via reflection - keep their fields and no-arg constructors.
-keepclassmembers class com.beperfectsalon.app.data.model.** {
    <init>();
    <fields>;
}
