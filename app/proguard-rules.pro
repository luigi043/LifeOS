# Add project-specific ProGuard rules here.
# Hilt + Room + kotlinx.serialization keep rules are auto-included by their plugins.
# Add module-specific rules in each module's consumer-rules.pro.

# Coroutines — required because coroutine internals use reflection in places.
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
