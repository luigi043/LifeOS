package com.luigi.lifeos.core.common.dispatcher

import javax.inject.Qualifier

/**
 * Qualifier so consumers inject a *specific* dispatcher rather than depending
 * on `Dispatchers.IO` directly. This makes tests deterministic — they can
 * swap in `StandardTestDispatcher` via the same binding.
 *
 * Usage:
 * ```
 * class FooRepo @Inject constructor(
 *     @Dispatcher(LifeOsDispatcher.IO) private val io: CoroutineDispatcher
 * )
 * ```
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: LifeOsDispatcher)

enum class LifeOsDispatcher { Default, IO }
