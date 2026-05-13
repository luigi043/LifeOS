package com.luigi.lifeos.core.common.dispatcher

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

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
@Retention(RUNTIME)
annotation class Dispatcher(val dispatcher: LifeOsDispatcher)

enum class LifeOsDispatcher { Default, IO }
